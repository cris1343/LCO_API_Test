package com.cris1343.lcoapitest.gui.windows;

import com.cris1343.lcoapitest.Config;
import com.cris1343.lcoapitest.Main;
import com.cris1343.lcoapitest.Utility;
import com.cris1343.lcoapitest.gui.Interface;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends Interface {
    public JCheckBox log;
    public JCheckBox logAtStartup;
    public JPanel actions;

    public MainMenu() {
        super("LCO API Test");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        //SET WINDOW ON THE SCREEN CENTER AND WITH HALF SCREEN SIZE
        setBounds(width / 4, height / 4, width / 2, height / 2);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        components();

        setVisible(true);
    }

    private void components() {
        //CONFIG SECTION
        {
            JPanel config = new JPanel();
            config.setLayout(new GridBagLayout());
            config.setBorder(BorderFactory.createTitledBorder("Configuration"));

            //CHANGE API KEY BUTTON
            JButton changeApiKey = new JButton("Change API Key");
            changeApiKey.addActionListener(e -> new ChangeApiKey());
            config.add(changeApiKey, Utility.gBCBuilder().pos(0, 0).build());

            //SHOW LOG CHECKBOX
            log = new JCheckBox("Show log");
            log.addActionListener(e -> Main.log.setVisible(log.isSelected()));
            config.add(log, Utility.gBCBuilder().pos(0, 1).build());

            //SHOW LOG AT STARTUP CHECKBOX
            logAtStartup = new JCheckBox("Show log at startup");
            logAtStartup.addActionListener(e -> Config.edit("showLogAtStartup", logAtStartup.isSelected()));
            config.add(logAtStartup, Utility.gBCBuilder().pos(0, 2).build());

            //SEPARATOR
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            config.add(separator, Utility.gBCBuilder().pos(0, 3).fill(GridBagConstraints.HORIZONTAL).build());

            //VERSION
            JLabel version = new JLabel("v1.0-beta1");
            config.add(version, Utility.gBCBuilder().pos(0, 4).build());

            add(config, Utility.gBCBuilder().pos(0, 0).build());
        }

        //ACTIONS SECTION
        {
            actions = new JPanel();
            actions.setLayout(new GridBagLayout());
            actions.setBorder(BorderFactory.createTitledBorder("Actions"));

            //TELESCOPE STATES BUTTON
            JButton teleStates = new JButton("Check Telescope States");
            teleStates.addActionListener(e -> new TelescopeStates());
            actions.add(teleStates, Utility.gBCBuilder().pos(0, 0).build());

            //TELESCOPE STATES BUTTON
            JButton searchByObject = new JButton("Search Observations");
            searchByObject.addActionListener(e -> new ObservationSearch());
            actions.add(searchByObject, Utility.gBCBuilder().pos(0, 1).build());

            add(actions, Utility.gBCBuilder().pos(2, 0).build());
        }
    }
}
