package com.cris1343.lcoapitest.gui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class Dialog extends JDialog {
    private final JPanel pane;

    public Dialog(JFrame owner, String title) {
        super(owner, title);

        pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(pane);
    }

    @Override
    public void add(@NotNull Component comp, Object constraints) {
        if (constraints instanceof GridBagConstraints)
            pane.add(comp, constraints);
        else
            super.add(comp, constraints);
    }
}
