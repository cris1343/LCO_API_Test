package com.cris1343.lcoapitest.gui.windows;

import com.cris1343.lcoapitest.Config;
import com.cris1343.lcoapitest.Http;
import com.cris1343.lcoapitest.Main;
import com.cris1343.lcoapitest.Utility;
import com.cris1343.lcoapitest.gui.Dialog;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class ChangeApiKey extends Dialog {
    private JTextField apiKey;

    public ChangeApiKey() {
        super(Main.mainMenu, "Change API Key");

        components();

        //ADJUST WINDOW SIZE AND POSITION
        pack();
        setSize(Math.round(getWidth() * 1.25F), Math.round(getHeight() * 1.5F));
        setLocationRelativeTo(Main.mainMenu);
        setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        setVisible(true);
    }

    private void components() {
        //WRITE API KEY LABEL
        JLabel writeApiKey = new JLabel("Paste your API Key here:");
        add(writeApiKey,
                Utility.gBCBuilder().pos(0, 0).size(2, 1).insets(0, 0, 0, 5).anchor(GridBagConstraints.PAGE_END)
                        .weight(0.0D, 0.1D)
                        .build());

        //WRITE API KEY LABEL
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
//        dialog.add(Box.createHorizontalStrut(10), Utility.gBCBuilder().pos(2, 0).size(1, 3).weight(0.0D, 1.0D).build());
        add(separator,
                Utility.gBCBuilder().pos(2, 0).size(1, 3).fill(GridBagConstraints.BOTH).weight(0.0D, 0.0D).build());

        //API KEY ADVICE LABEL
        JTextArea apiKeyAdvice = new JTextArea(
                "Your LCO API Key is required for this application. " +
                        "Copy your API Key located on LCO profile page and paste it here. " +
                        "Please treat your API Key like a password.");
        apiKeyAdvice.setEditable(false);
        apiKeyAdvice.setOpaque(false);
        apiKeyAdvice.setWrapStyleWord(true);
        apiKeyAdvice.setLineWrap(true);
        add(apiKeyAdvice,
                Utility.gBCBuilder().pos(3, 0).size(1, 2).fill(GridBagConstraints.BOTH)
                        .insets(0, 5, 0, 0).anchor(GridBagConstraints.CENTER).weight(0.1D, 1.0D).build());

        //API KEY FIELD
        apiKey = new JTextField();
        apiKey.addActionListener(e -> checkApiKey(apiKey.getText()));
        add(apiKey,
                Utility.gBCBuilder().pos(0, 1).size(2, 1).fill(GridBagConstraints.HORIZONTAL).insets(0, 0, 10, 5)
                        .anchor(GridBagConstraints.PAGE_START).weight(0.0D, 0.1D).build());

        //APPLY BUTTON
        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> checkApiKey(apiKey.getText()));
        add(apply,
                Utility.gBCBuilder().pos(0, 2).fill(GridBagConstraints.HORIZONTAL).weight(0.1D, 0.0D).build());

        //CANCEL BUTTON
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        add(cancel,
                Utility.gBCBuilder().pos(1, 2).fill(GridBagConstraints.HORIZONTAL).insets(0, 0, 0, 5).weight(0.1D, 0.0D)
                        .build());

        //OPEN LCO PROFILE BUTTON
        JButton openLCOProfile = new JButton("LCO Account Profile");
        openLCOProfile.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://observe.lco.global/accounts/profile"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        add(openLCOProfile,
                Utility.gBCBuilder().pos(3, 2).fill(GridBagConstraints.HORIZONTAL).insets(0, 5, 0, 0).build());
    }

    private void checkApiKey(String key) {
        String oldKey = Http.apiKey;
        Http.apiKey = key;
        if (Http.get("https://observe.lco.global/api/") == null) {
            apiKey.setText("Invalid API key!");
            Http.apiKey = oldKey;
        } else {
            Config.edit("apiKey", key);
            for (Component comp : Main.mainMenu.actions.getComponents())
                comp.setEnabled(true);
            Utility.log("API Key has changed");
            dispose();
        }
    }
}
