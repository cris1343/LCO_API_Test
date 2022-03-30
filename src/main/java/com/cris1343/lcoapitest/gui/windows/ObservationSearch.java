package com.cris1343.lcoapitest.gui.windows;

import com.cris1343.lcoapitest.Http;
import com.cris1343.lcoapitest.Main;
import com.cris1343.lcoapitest.Utility;
import com.cris1343.lcoapitest.gui.Interface;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;

public class ObservationSearch extends Interface {
    private JLabel status;
    private JButton openSelected;
    private JButton downloadSelected;
    private JSONArray resultsArray;
    private DefaultMutableTreeNode treeRoot;
    private JTree tree;
    private HashMap<Integer, DefaultMutableTreeNode> requests;

    public ObservationSearch() {
        super("Observation search");

        components();

        //ADJUST WINDOW SIZE AND POSITION
        pack();
        setSize(Math.round(getWidth() * 1.5F), getHeight()/*Math.round(getHeight() * 2.5F)*/);
        setLocationRelativeTo(Main.mainMenu);
        setVisible(true);
    }

    private void components() {
        //OBJECT LABEL
        JLabel object = new JLabel("Object name:");
        add(object, Utility.gBCBuilder().pos(0, 0).anchor(GridBagConstraints.LINE_START).build());

        //SEARCH RESULTS LABEL
        JLabel results = new JLabel("Search results:");
        add(results, Utility.gBCBuilder().pos(2, 0).insets(0, 5, 0, 0).anchor(GridBagConstraints.LINE_START).build());

        //OBJECT NAME FIELD
        JTextField objectName = new JTextField();
        add(objectName, Utility.gBCBuilder().pos(0, 1).size(2, 1).fill(GridBagConstraints.HORIZONTAL).build());

        //PROPOSAL LABEL
        JLabel proposal = new JLabel("Proposal:");
        add(proposal, Utility.gBCBuilder().pos(0, 2).anchor(GridBagConstraints.LINE_START).build());

        //PROPOSAL ID FIELD
        JTextField proposalID = new JTextField();
        add(proposalID, Utility.gBCBuilder().pos(0, 3).size(2, 1).fill(GridBagConstraints.HORIZONTAL).build());

        //RESULTS TREE
        //ROOT TREE NODE
        treeRoot = new DefaultMutableTreeNode("Results");

        //RESULTS TREE
        tree = new JTree(treeRoot);
//        tree.setToggleClickCount(1);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(e -> {
            openSelected.setEnabled(true);
            downloadSelected.setEnabled(true);
        });
        add(new JScrollPane(tree),
                Utility.gBCBuilder().pos(2, 1).size(2, 4).fill(GridBagConstraints.BOTH).insets(0, 5, 5, 0)
                        .weight(0.5D, 0.0D).build());

        //STATUS LABEL
        status = new JLabel();
        status.setVisible(false);
        add(status,
                Utility.gBCBuilder().pos(0, 4).size(2, 1).anchor(GridBagConstraints.FIRST_LINE_START).weight(0.1D, 0.5D)
                        .build());

        //SEARCH BUTTON
        JButton search = new JButton("Search");
        search.addActionListener(e -> search(objectName.getText(), proposalID.getText()));
        add(search,
                Utility.gBCBuilder().pos(0, 5).fill(GridBagConstraints.HORIZONTAL).weight(0.1D, 0.0D).build());

        //CLOSE BUTTON
        JButton back = new JButton("Close");
        back.addActionListener(e -> dispose());
        add(back,
                Utility.gBCBuilder().pos(1, 5).fill(GridBagConstraints.HORIZONTAL).weight(0.1D, 0.0D).build());

        //OPEN SELECTED BUTTON
        openSelected = new JButton("Open selected");
        openSelected.addActionListener(e -> {
            if (tree.getSelectionPaths() != null)
                for (DefaultMutableTreeNode request : requests.values()) {
                    for (TreePath path : tree.getSelectionPaths())
                        if (((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject()
                                .equals(request.getUserObject())) {
                            try {
                                Desktop.getDesktop().browse(new URI(
                                        "https://observe.lco.global/requests/" +
                                                ((String) request.getUserObject()).split(" - ")[1]));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            break;
                        }
                }
        });
        openSelected.setEnabled(false);
        add(openSelected,
                Utility.gBCBuilder().pos(2, 5).fill(GridBagConstraints.HORIZONTAL).insets(0, 5, 0, 0).build());

        //DOWNLOAD SELECTED BUTTON
        downloadSelected = new JButton("Download selected");
        downloadSelected.addActionListener(e -> {
            if (tree.getSelectionPaths() != null)
                for (TreePath path : tree.getSelectionPaths()) {
                    for (Object o : resultsArray) {
                        JSONObject jsonObj = ((JSONObject) o);
                        DefaultMutableTreeNode lastComp = ((DefaultMutableTreeNode) path.getLastPathComponent());
                        if (lastComp.getUserObject()
                                .equals(jsonObj.getString("basename")) || (!lastComp.isLeaf() &&
                                ((String) lastComp.getUserObject()).split(" - ")[1].equals(
                                        String.valueOf(jsonObj.getInt("request_id")))))
                            try {
                                Desktop.getDesktop().browse(new URI(jsonObj.getString("url")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                    }
                }
        });
        downloadSelected.setEnabled(false);
        add(downloadSelected,
                Utility.gBCBuilder().pos(3, 5).fill(GridBagConstraints.HORIZONTAL).build());
    }

    private void search(String objectName, String proposalID) {
        treeRoot.removeAllChildren();

        openSelected.setEnabled(false);
        downloadSelected.setEnabled(false);

        status.setText("Requesting the data...");
        status.setVisible(true);

        //LOAD DATA FROM LCO
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                JSONObject json = Http.get(
                        "https://archive-api.lco.global/frames/?" +
                                (objectName.isEmpty() ? "" : "target_name=" + objectName) +
                                (proposalID.isEmpty() ? "" : "&proposal_id=" + proposalID));

                if (json == null || !json.has("results")) {
                    status.setText("Error!");
                    return;
                }

                resultsArray = json.getJSONArray("results");

                if (resultsArray.isEmpty()) {
                    status.setText("No observations found");
                    return;
                }

                requests = new HashMap<>();
                for (Object r : resultsArray) {
                    JSONObject result = ((JSONObject) r);
                    DefaultMutableTreeNode request;
                    int reqID;

                    if (!result.isNull("request_id"))
                        reqID = result.getInt("request_id");
                    else
                        reqID = 0;
                    if (requests.containsKey(reqID))
                        request = requests.get(reqID);
                    else {
                        request = new DefaultMutableTreeNode(result.getString("target_name") + " - " +
                                (result.isNull("request_id") ? "null" : reqID));
                        treeRoot.add(request);
                        requests.put(reqID, request);
                    }
                    request.add(new DefaultMutableTreeNode(result.getString("basename")));
                }

                tree.expandPath(new TreePath(treeRoot.getPath()));
                tree.updateUI();

                status.setText(
                        "Found " + json.getInt("count") + " files (showing " + requests.size() + " requests" +
                                (json.getInt("count") != resultsArray.length() ?
                                        ", " + resultsArray.length() + " files)" : ")"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
