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
public class ProxyConfigurationDialog extends JDialog implements ActionListener {
  /**
   * Serial UID
   */
  private static final long serialVersionUID = 934313995824565308L;

  /**
   * Logger Instance
   */
  private static Logger LOGGER = Logger
      .getLogger(ProxyConfigurationDialog.class);

  private JTextField proxyServer;
  private JTextField proxyPort;
  private JCheckBox protocolHttp;
  private JCheckBox protocolSocks;
  private JButton okay, cancel;
  private boolean accepted;

  public ProxyConfigurationDialog(JFrame parent, String currentProxyServer,
      Integer currentProxyPort, boolean currentProtocolHttp,
      boolean currentProtocolSocks) throws HeadlessException {
    super(parent, "Proxy Configuration", true);

    setupGui(currentProxyServer, currentProxyPort, currentProtocolHttp,
        currentProtocolSocks);
    GuiUtilities.centerWindow(this, parent);

    setVisible(true);
  }

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
    tempPanel.add(proxyServer = new JTextField(30));

    if (currentProxyServer != null) {
      proxyServer.setText(currentProxyServer.trim());
    }

    tempPanel.add(new JLabel("Proxy Port: "));
    tempPanel.add(proxyPort = new JTextField(5));

    if (currentProxyPort != null) {
      proxyPort.setText(currentProxyPort + "");
    }

    tempPanel.add(new JLabel("Protocols: "));
    rightPanel = new JPanel();
    rightPanel.setLayout(new GridLayout(0, 2));
    rightPanel.add(protocolHttp = new JCheckBox("HTTP"));
    rightPanel.add(protocolSocks = new JCheckBox("SOCKS"));
    tempPanel.add(rightPanel);
    getContentPane().add(tempPanel);

    protocolHttp.setSelected(currentProtocolHttp);
    protocolSocks.setSelected(currentProtocolSocks);

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

  public String getProxyServer() {
    if (accepted) {
      return proxyServer.getText().trim();
    } else {
      return null;
    }
  }

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

  public boolean isProtocolHttp() {
    return protocolHttp.isSelected();
  }

  public boolean isProtocolSocks() {
    return protocolSocks.isSelected();
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
