package com.monead.semantic.workbench;

import org.apache.log4j.Logger;

import com.monead.semantic.workbench.utilities.GuiUtilities;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Configure the proxy to be used when requesting services over the network.
 * 
 * @author David Read
 */
public class ProxyConfigurationDialog extends JDialog implements ActionListener {
  /**
   * Serial UID
   */
  private static final long serialVersionUID = 934313995824565308L;

  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger
      .getLogger(ProxyConfigurationDialog.class);

  /**
   * The display size of the proxy server input field
   */
  private static final int FIELD_DISPLAY_LENGTH_SERVER = 30;
  
  /**
   * The display size of the proxy port
   */
  private static final int FIELD_DISPLAY_LENGTH_PORT = 5;
  
  /**
   * The proxy server IP or DNS name
   */
  private JTextField proxyServer;

  /**
   * The proxy port
   */
  private JTextField proxyPort;

  /**
   * Whether to send HTTP requests through the proxy
   */
  private JCheckBox protocolHttp;

  /**
   * Whether to send SOCKS requests through the proxy
   */
  private JCheckBox protocolSocks;

  /**
   * The okay button, indicating the user wants the settings saved
   */
  private JButton okay;

  /**
   * The cancel button, indicating the user wants to cancel any changes to the
   * settings
   */
  private JButton cancel;

  /**
   * Indicates whether the user accepted or canceled the configuration changes
   */
  private boolean accepted;

  /**
   * Create a dialog with the proxy configuration fields displayed. This dialog
   * will be modal on top of the parent window.
   * 
   * @param parent
   *          The parent window.
   * @param currentProxyServer
   *          The current proxy server IP or DNS name
   * @param currentProxyPort
   *          The current proxy port
   * @param currentProtocolHttp
   *          The current selection for whether to proxy HTTP requests
   * @param currentProtocolSocks
   *          The current selection for whether to proxy SOCKS requests
   */
  public ProxyConfigurationDialog(JFrame parent, String currentProxyServer,
      Integer currentProxyPort, boolean currentProtocolHttp,
      boolean currentProtocolSocks) {
    super(parent, "Proxy Configuration", true);

    setupGui(currentProxyServer, currentProxyPort, currentProtocolHttp,
        currentProtocolSocks);
    GuiUtilities.centerWindow(this, parent);

    setVisible(true);
  }

  /**
   * Arrange the graphical components on the dialog
   * 
   * @param currentProxyServer
   *          The current proxy server IP or DNS name
   * @param currentProxyPort
   *          The current proxy port
   * @param currentProtocolHttp
   *          The current selection for whether to proxy HTTP requests
   * @param currentProtocolSocks
   *          The current selection for whether to proxy SOCKS requests
   */
  private void setupGui(String currentProxyServer, Integer currentProxyPort,
      boolean currentProtocolHttp, boolean currentProtocolSocks) {
    JPanel tempPanel;
    JPanel rightPanel;

    getContentPane().setLayout(new GridLayout(0, 1));

    tempPanel = new JPanel();
    tempPanel.setLayout(new FlowLayout());

    tempPanel = new JPanel();
    tempPanel.setLayout(new GridLayout(0, 2));
    tempPanel.add(new JLabel("Proxy URL: "));
    proxyServer = new JTextField(FIELD_DISPLAY_LENGTH_SERVER);
    tempPanel.add(proxyServer);

    if (currentProxyServer != null) {
      proxyServer.setText(currentProxyServer.trim());
    }

    tempPanel.add(new JLabel("Proxy Port: "));
    proxyPort = new JTextField(FIELD_DISPLAY_LENGTH_PORT);
    tempPanel.add(proxyPort);

    if (currentProxyPort != null) {
      proxyPort.setText(currentProxyPort + "");
    }

    tempPanel.add(new JLabel("Protocols: "));
    rightPanel = new JPanel();
    rightPanel.setLayout(new GridLayout(0, 2));
    protocolHttp = new JCheckBox("HTTP");
    rightPanel.add(protocolHttp);
    protocolSocks = new JCheckBox("SOCKS");
    rightPanel.add(protocolSocks);
    tempPanel.add(rightPanel);
    getContentPane().add(tempPanel);

    protocolHttp.setSelected(currentProtocolHttp);
    protocolSocks.setSelected(currentProtocolSocks);

    tempPanel = new JPanel();
    tempPanel.setLayout(new FlowLayout());
    okay = new JButton("OK");
    tempPanel.add(okay);
    okay.addActionListener(this);
    getRootPane().setDefaultButton(okay);
    cancel = new JButton("Cancel");
    tempPanel.add(cancel);
    cancel.addActionListener(this);
    getContentPane().add(tempPanel);

    pack();
  }

  /**
   * Check whether the user accepted the changes or canceled them.
   * This should only be called after the dialog is dismissed.
   * 
   * @return True if the user accepted the updates made to the values
   */
  public boolean isAccepted() {
    return accepted;
  }

  /**
   * Get the user-entered proxy server IP or DNS name.
   * 
   * @return The IP/DNS name or null if the user canceled the request
   */
  public String getProxyServer() {
    if (accepted) {
      return proxyServer.getText().trim();
    } else {
      return null;
    }
  }

  /**
   * Get the user-entered proxy port. If the user entered a non-numeric port
   * value, calling this method will result in an exception being thrown.
   * 
   * @return The proxy port or null if the user canceled the request
   */
  public Integer getProxyPort() {
    Integer value = null;

    if (accepted) {
      try {
        value = Integer.parseInt(proxyPort.getText());
      } catch (Throwable throwable) {
        LOGGER.error(
            "Illegal proxy port number entered: " + proxyPort.getText(),
            throwable);
        value = null;
      }
    }

    return value;
  }

  /**
   * Returns whether HTTP requests are to be proxied.
   * 
   * @return True if HTTP should be proxied
   */
  public boolean isProtocolHttp() {
    return protocolHttp.isSelected();
  }

  /**
   * Returns whether SOCKS requests are to be proxied.
   * 
   * @return True of SOCKS should be proxied
   */
  public boolean isProtocolSocks() {
    return protocolSocks.isSelected();
  }

  @Override
  public void actionPerformed(ActionEvent what) {
    if (what.getSource() == okay) {
      accepted = true;
      dispose();
    } else if (what.getSource() == cancel) {
      dispose();
    }
  }
}
