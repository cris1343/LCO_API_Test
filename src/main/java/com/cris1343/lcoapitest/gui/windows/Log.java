package com.cris1343.lcoapitest.gui.windows;

import com.cris1343.lcoapitest.Config;
import com.cris1343.lcoapitest.Main;
import com.cris1343.lcoapitest.Utility;
import com.cris1343.lcoapitest.gui.Dialog;
import com.cris1343.lcoapitest.gui.Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Log extends Interface {
    public JList<String> list;
    public DefaultListModel<String> log;
    public JCheckBox autoScroll;

    public Log() {
        super("Log");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.mainMenu.log.setSelected(false);
            }
        });

        components();
    }

    private void components() {
        //OBSERVATIONS LIST
        log = new DefaultListModel<>();
        list = new JList<>();
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        JScrollPane listPane = new JScrollPane(list);
        listPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            int prevMax;

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (e.getAdjustable().getMaximum() != prevMax && autoScroll.isSelected())
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                prevMax = e.getAdjustable().getMaximum();
            }
        });
        add(listPane,
                Utility.gBCBuilder().pos(0, 0).fill(GridBagConstraints.BOTH).weight(1.0D, 1.0D).build());

        //AUTOSCROLL CHECKBOX
        autoScroll = new JCheckBox("Auto-scroll");
        autoScroll.addActionListener(e -> Config.edit("autoScrollLog", autoScroll.isSelected()));
        add(autoScroll, Utility.gBCBuilder().pos(0, 1).anchor(GridBagConstraints.LINE_START).build());

        //ADJUST WINDOW SIZE AND POSITION
        pack();
        setSize(Math.round(getWidth() * 1.15F), Math.round(getHeight() * 5.5F));
        setLocation(Main.mainMenu.getX() - getWidth(), Main.mainMenu.getY());
//        dialog.setLocationRelativeTo(Main.frame);
    }
}
