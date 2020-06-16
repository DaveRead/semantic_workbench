package com.monead.semantic.workbench.utilities;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This memory warning system will call the listener when we exceed the
 * percentage of available memory specified. There should only be one instance
 * of this object created, since the usage threshold can only be set to one
 * number.
 * 
 * ( adapted from http://www.javaspecialists.eu/archive/Issue092.html )
 */
public class MemoryWarningSystem {
  /**
   * Logger Instance
   */
  private static final Logger LOGGER = LogManager
      .getLogger(MemoryWarningSystem.class);

  /**
   * Interface for registering for low memory warnings
   */
  public interface Listener {
    /**
     * Called when memory falls below the configured threshold
     * 
     * @param usedMemory
     *          The memory currently used
     * @param maxMemory
     *          The maximumu heap size
     */
    void memoryUsageLow(long usedMemory, long maxMemory);
  }

  /**
   * The collection of listeners registered for memory events
   */
  private final Collection<Listener> listeners = new ArrayList<Listener>();

  /**
   * The tenured generation memory pool (heap)
   */
  private static final MemoryPoolMXBean TENURED_GENERATION_POOL = findTenuredGenPool();

  /**
   * The collection of latest memory free values reported to callers
   */
  private static final Map<Object, Long> LATEST_AVAILABLE_TENURED_CHANGE_TRACKING = new HashMap<Object, Long>();

  /**
   * Setup the mbeans and listener
   */
  public MemoryWarningSystem() {
    final MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
    final NotificationEmitter emitter = (NotificationEmitter) mbean;
    emitter.addNotificationListener(new NotificationListener() {
      @Override
      public void handleNotification(Notification n, Object hb) {
        if (n.getType().equals(
            MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
          final long maxMemory = TENURED_GENERATION_POOL.getUsage().getMax();
          final long usedMemory = TENURED_GENERATION_POOL.getUsage().getUsed();
          for (Listener listener : listeners) {
            listener.memoryUsageLow(usedMemory, maxMemory);
          }
        }
      }
    }, null, null);
  }

  /**
   * Register as a listener
   * 
   * @param listener
   *          The instance registering for memory events
   * 
   * @return True if the instance was added as a listener. False implies the
   *         instance was already registered.
   */
  public boolean addListener(Listener listener) {
    return listeners.add(listener);
  }

  /**
   * Remove an instance from the listener collection
   * 
   * @param listener
   *          The instance to remove
   * 
   * @return True if the instance is removed. False implies the instance was not
   *         registered.
   */
  public boolean removeListener(Listener listener) {
    return listeners.remove(listener);
  }

  /**
   * Set the alarm percentage level (range 0.0 to 1.0). 0.0 disables any
   * alarming.
   * 
   * @param percentage
   *          The percentage of memory below which to alert registered
   *          listeners. This is a value from 0.0 to 1.0
   */
  public void setPercentageUsageThreshold(double percentage) {
    if (percentage <= 0.0 || percentage > 1.0) {
      throw new IllegalArgumentException("Percentage not in range");
    }

    final long maxMemory = TENURED_GENERATION_POOL.getUsage().getMax();
    final long warningThreshold = (long) (maxMemory * percentage);

    TENURED_GENERATION_POOL.setUsageThreshold(warningThreshold);
  }

  /**
   * Tenured Space Pool can be determined by it being of type HEAP and by it
   * being possible to set the usage threshold.
   * 
   * @return The tenured generation pool MX bean
   */
  public static MemoryPoolMXBean findTenuredGenPool() {
    for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
      // I don't know whether this approach is better, or whether
      // we should rather check for the pool name "Tenured Gen"?
      if (pool.getType() == MemoryType.HEAP
          && pool.isUsageThresholdSupported()) {
        return pool;
      }
    }
    throw new IllegalStateException("Could not find tenured space");
  }

  /**
   * Get the free memory as of the last garbace collection execution
   * 
   * @see #hasLatestAvailableTenuredGenAfterCollectionChanged(Object)
   * 
   * @return Free memory (used minus maximum)
   */
  public static long getLatestAvailableTenuredGenAfterCollection() {
    final MemoryUsage usage = TENURED_GENERATION_POOL.getCollectionUsage();

    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Latest available heap after collection: "
          + (usage.getMax() - usage.getUsed()));
    }

    return usage.getMax() - usage.getUsed();
  }

  /**
   * Determine whether the free memory as of the last GC has changed since the
   * last time this object checked this status.
   * 
   * @see #getLatestAvailableTenuredGenAfterCollection()
   * 
   * @param requestor
   *          The instance that is monitoring free memory
   * 
   * @return True if the free memory as of last GC has changed since the last
   *         time this instance called this method
   */
  public static boolean hasLatestAvailableTenuredGenAfterCollectionChanged(
      Object requestor) {
    final Long lastVal = LATEST_AVAILABLE_TENURED_CHANGE_TRACKING
        .get(requestor);

    if (lastVal == null
        || lastVal != getLatestAvailableTenuredGenAfterCollection()) {
      LATEST_AVAILABLE_TENURED_CHANGE_TRACKING.put(requestor,
          getLatestAvailableTenuredGenAfterCollection());
      return true;
    }

    return false;
  }
}
