package com.monead.semantic.workbench;

import org.apache.log4j.Logger;

import com.monead.semantic.workbench.utilities.GuiUtilities;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
  private static final Logger LOGGER = Logger
      .getLogger(SparqlServerConfigurationDialog.class);

  /**
   * The set of allowed maximum processing time selectiolns
   */
  private static final String[] MAX_TIME_OPTIONS = {
      "15", "30", "60", "120", "600"
  };

  /**
   * The port number to listen on
   */
  private JTextField portNumber;

  /**
   * The maximum runtime for a query
   */
  private JComboBox maxRuntime;

  /**
   * Allow remote updates to the model
   */
  private JCheckBox supportRemoteUpdate;

  /**
   * The OK button
   */
  private JButton okay;

  /**
   * The cancel button
   */
  private JButton cancel;

  /**
   * Whether the user pressed the OK button (accepted the changes)
   */
  private boolean accepted;

  /**
   * Create a dialog for setting SPARQL server configuration options
   * 
   * @param parent
   *          The parent frame
   * @param currentPort
   *          The current port number
   * @param currentMaxRuntime
   *          The current maximum query runtime
   * @param remoteUpdateAllowed
   *          Are remote updates supported
   */
  public SparqlServerConfigurationDialog(JFrame parent, int currentPort,
      int currentMaxRuntime, boolean remoteUpdateAllowed) {
    super(parent, "SPARQL Server Configuration", true);

    setupGui(currentPort, currentMaxRuntime, remoteUpdateAllowed);
    GuiUtilities.centerWindow(this, parent);

    setVisible(true);
  }

  /**
   * Setup the graphical user interface, laying out the controls
   * 
   * @param currentPort
   *          The current port number
   * @param currentMaxRuntime
   *          The current maximum query runtime
   * @param remoteUpdateAllowed
   *          Are remote updates supported
   */
  private void setupGui(int currentPort,
      int currentMaxRuntime, boolean remoteUpdateAllowed) {
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
    maxRuntime.setSelectedItem(currentMaxRuntime + "");

    tempPanel.add(new JLabel("Remote Updates Allowed: "));
    tempPanel.add(supportRemoteUpdate = new JCheckBox());
    supportRemoteUpdate.setSelected(remoteUpdateAllowed);

    getContentPane().add(tempPanel);

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

  /**
   * Check whetyher the user pressed the OK button. Only meaningful if checked
   * after the dialog is closed.
   * 
   * @return True if the user pressed the OK button
   */
  public boolean isAccepted() {
    return accepted;
  }

  /**
   * Retrieve the port number entered into the dialog. This will be null if a
   * non-numeric value is entered.
   * 
   * @return The entered port number.
   */
  public Integer getPortNumber() {
    Integer value = null;

    if (accepted) {
      try {
        value = Integer.parseInt(portNumber.getText());
      } catch (Throwable throwable) {
        LOGGER.error("Illegal port number entered: " + portNumber.getText(),
            throwable);
        value = null;
      }
    }

    return value;
  }

  /**
   * Get the maximum runtime entered by the user. If a non-numeric value is
   * entered this will return null.
   * 
   * @return The maximum runtime entered
   */
  public Integer getMaxRuntime() {
    Integer value;

    try {
      value = Integer.parseInt((String) maxRuntime.getSelectedItem());
    } catch (Throwable throwable) {
      LOGGER.error(
          "Illegal maximum runtime entered: " + maxRuntime.getSelectedItem(),
          throwable);
      value = null;
    }

    return value;
  }

  /**
   * Are remote updates allowed
   * 
   * @return True if remote updates are allowed
   */
  public boolean areRemoteUpdatesAllowed() {
    return supportRemoteUpdate.isSelected();
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
