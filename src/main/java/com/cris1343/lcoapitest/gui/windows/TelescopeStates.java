package com.cris1343.lcoapitest.gui.windows;

import com.cris1343.lcoapitest.Http;
import com.cris1343.lcoapitest.Main;
import com.cris1343.lcoapitest.Utility;
import com.cris1343.lcoapitest.gui.Dialog;
import com.cris1343.lcoapitest.gui.Interface;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.concurrent.Executors;

public class TelescopeStates extends Interface {
    private DefaultMutableTreeNode telescopes;
    private JTree tree;

    public TelescopeStates() {
        super("Telescope States - Checking...");

        components();

        //ADJUST WINDOW SIZE AND POSITION
        pack();
        setSize(Math.round(getWidth() * 2.5F), getHeight());
        setLocationRelativeTo(Main.mainMenu);

        loadStates();

        setVisible(true);
    }

    private void components() {
        //TELESCOPE TREE NODE
        telescopes = new DefaultMutableTreeNode("Telescopes");

        //TELESCOPE TREE
        tree = new JTree(telescopes);
        tree.setToggleClickCount(1);
        add(new JScrollPane(tree),
                Utility.gBCBuilder().pos(0, 0).fill(GridBagConstraints.BOTH).weight(1.0D, 0.1D).build());

        //BACK BUTTON
        JButton back = new JButton("Back");
        back.addActionListener(e -> dispose());
        add(back, Utility.gBCBuilder().pos(0, 1).insets(5, 0, 0, 0).build());
    }

    private void loadStates() {
        DefaultMutableTreeNode coj = new DefaultMutableTreeNode("Siding Spring Observatory - NSW, Australia");
        telescopes.add(coj);
        DefaultMutableTreeNode tlv = new DefaultMutableTreeNode("Wise Observatory - Israel");
        telescopes.add(tlv);
        DefaultMutableTreeNode cpt = new DefaultMutableTreeNode("SAAO - Sutherland, South Africa");
        telescopes.add(cpt);
        DefaultMutableTreeNode tfn = new DefaultMutableTreeNode("Teide Observatory - Tenerife, Spain");
        telescopes.add(tfn);
        DefaultMutableTreeNode lsc = new DefaultMutableTreeNode("CTIO - Region IV, Chile");
        telescopes.add(lsc);
        DefaultMutableTreeNode elp = new DefaultMutableTreeNode("McDonald Observatory - Texas, USA");
        telescopes.add(elp);
        DefaultMutableTreeNode ogg = new DefaultMutableTreeNode("Haleakala Observatory - Maui, USA");
        telescopes.add(ogg);

        //LOAD TELESCOPE STATES FROM LCO
        Executors.newSingleThreadExecutor().submit(() -> {
            JSONObject json = Http.get("https://observe.lco.global/api/telescope_states/");
            json.keys().forEachRemaining(s -> {
                DefaultMutableTreeNode parent;
                switch (s.split("\\.")[0]) {
                    case "coj":
                        parent = coj;
                        break;
                    case "tlv":
                        parent = tlv;
                        break;
                    case "cpt":
                        parent = cpt;
                        break;
                    case "tfn":
                        parent = tfn;
                        break;
                    case "lsc":
                        parent = lsc;
                        break;
                    case "elp":
                        parent = elp;
                        break;
                    case "ogg":
                        parent = ogg;
                        break;
                    default:
                        parent = telescopes;
                }
                parent.add(new DefaultMutableTreeNode(
                        s + " - " + json.getJSONArray(s).getJSONObject(0).getString("event_type")));
            });

            setTitle("Telescope States");
            tree.expandPath(new TreePath(telescopes.getPath()));
            tree.updateUI();
        });
    }
}
