package com.monead.semantic.workbench;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.apache.log4j.Logger;

import com.monead.semantic.workbench.utilities.GuiUtilities;

import java.awt.HeadlessException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: Blue Slate Solutions
 * </p>
 * 
 * @author David Read
 * @version $Id: PasswordDialog.java,v 1.2 2005/01/24 23:10:37 uid513 Exp $
 */
public class SparqlServerConfigurationDialog extends JDialog implements
    ActionListener {
  

  /**
   * Serial UID
   */
  private static final long serialVersionUID = -284835059117735660L;

  /**
   * Logger Instance
   */
  private static Logger LOGGER = Logger.getLogger(SparqlServerConfigurationDialog.class);
  
  private final static String[] MAX_TIME_OPTIONS = { "15", "30", "60", "120",
      "600" };

  private JTextField portNumber;
  private JComboBox maxRuntime;
  private JButton okay, cancel;
  private boolean accepted;

  public SparqlServerConfigurationDialog(JFrame parent, int currentPort,
      int currentMaxRuntime)
      throws HeadlessException {
    super(parent, "SPARQL Server Configuration", true);

    setupGui(currentPort, currentMaxRuntime);
    GuiUtilities.centerWindow(this, parent);

    setVisible(true);
  }

  private void setupGui(int currentPort,
      int currentMaxRuntime) {
    JPanel tempPanel;
    getContentPane().setLayout(new GridLayout(0, 1));

    tempPanel = new JPanel();
    tempPanel.setLayout(new FlowLayout());
    // tempPanel.add(new JLabel("Please enter your user id and password"));
    // getContentPane().add(tempPanel);

    tempPanel = new JPanel();
    tempPanel.setLayout(new GridLayout(0, 2));
    tempPanel.add(new JLabel("Server Port Number: "));
    tempPanel.add(portNumber = new JTextField(5));
    
    portNumber.setText(currentPort + "");

    tempPanel.add(new JLabel("Max Query Runtime (seconds): "));
    tempPanel.add(maxRuntime = new JComboBox(MAX_TIME_OPTIONS));
    getContentPane().add(tempPanel);

    maxRuntime.setSelectedItem(currentMaxRuntime + "");
    
    tempPanel = new JPanel();
    tempPanel.setLayout(new FlowLayout());
    tempPanel.add(okay = new JButton("OK"));
    okay.addActionListener(this);
    getRootPane().setDefaultButton(okay);
    tempPanel.add(cancel = new JButton("Cancel"));
    cancel.addActionListener(this);
    getContentPane().add(tempPanel);

    pack();
  }

  public boolean isAccepted() {
    return accepted;
  }

  public Integer getPortNumber() {
    Integer value = null;
    
    if (accepted) {
      try {
        value = Integer.parseInt(portNumber.getText());
      }
      catch (Throwable throwable) {
        LOGGER.error("Illegal port number entered: " + portNumber.getText(), throwable);
        value = null;
      }
    }
    
    return value;
  }

  public Integer getMaxRuntime() {
    Integer value;
    
    try {
      value = Integer.parseInt((String) maxRuntime.getSelectedItem());
    }
    catch (Throwable throwable) {
      LOGGER.error("Illegal maximum runtime entered: " + maxRuntime.getSelectedItem(), throwable);
      value = null;
    }
    
    return value;
  }

  // Begin ActionListener Interface

  public void actionPerformed(ActionEvent what) {
    if (what.getSource() == okay) {
      accepted = true;
      dispose();
    } else if (what.getSource() == cancel) {
      dispose();
    }
  }

  // End ActionListener Interface
}
