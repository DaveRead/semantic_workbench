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
 * Present a dialog of configuration options for the SPARQL server
 * 
 * <p>
 * Copyright: Copyright (c) 2015, David Read
 * </p>
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * </p>
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * </p>
 * <p>
 * </p>
 * 
 * @author David Read
 *
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
   * The set of allowed maximum processing time selections
   */
  private static final Integer[] MAX_TIME_OPTIONS = {
      15, 30, 60, 120, 600
  };

  /**
   * The port number to listen on
   */
  private JTextField portNumber;

  /**
   * The maximum runtime for a query
   */
  private JComboBox<Integer> maxRuntime;

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
    tempPanel.add(maxRuntime = new JComboBox<Integer>(MAX_TIME_OPTIONS));
    maxRuntime.setSelectedItem(currentMaxRuntime);

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
   * Check whether the user pressed the OK button. Only meaningful if checked
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
    return maxRuntime.getItemAt(maxRuntime.getSelectedIndex());
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
