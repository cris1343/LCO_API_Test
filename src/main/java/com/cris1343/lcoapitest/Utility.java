package com.cris1343.lcoapitest;

import javax.swing.*;

public class Utility {
    public static GridBagConstraintBuilder gBCBuilder() {
        return new GridBagConstraintBuilder();
    }

    public static void log(String text) {
        SwingUtilities.invokeLater(() -> {
            Main.log.log.addElement(text);
            Main.log.list.setModel(Main.log.log);
            System.out.println(text);
        });
    }
}
