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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;
import java.text.ParseException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ObservationSearch extends Interface {
    private JLabel status;
    private JLabel files;
    private JButton viewOnLCO;
    private JButton downloadSelected;
    private JSONArray resultsArray;
    private DefaultMutableTreeNode treeRoot;
    private JTree tree;
    private HashMap<Integer, DefaultMutableTreeNode> requests;

    private final int limit = 1000;

    public ObservationSearch() {
        super("Observation search");

        try {
            components();
        } catch (ParseException e) {
            Main.mainMenu.dispose();
            e.printStackTrace();
        }

        //ADJUST WINDOW SIZE AND POSITION
        pack();
        setSize(Math.round(getWidth() /* * 1.25F*/), getHeight()/*Math.round(getHeight() * 2.5F)*/);
        setLocationRelativeTo(Main.mainMenu);
        setVisible(true);
    }

    private void components() throws ParseException {
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
        proposalID.setText("LCOEPO2021B-007");
        add(proposalID, Utility.gBCBuilder().pos(0, 3).size(2, 1).fill(GridBagConstraints.HORIZONTAL).build());

        //OFFSET LABEL
        JLabel searchOffset = new JLabel("Search offset: (leave empty for full search)");
        add(searchOffset, Utility.gBCBuilder().pos(0, 4).size(2, 1).anchor(GridBagConstraints.LINE_START).build());

        //OFFSET FIELD
        JTextField offset = new JTextField();
        offset.addKeyListener(new A());
        add(offset, Utility.gBCBuilder().pos(0, 5).size(2, 1).fill(GridBagConstraints.HORIZONTAL).build());

        //RESULTS TREE
        //ROOT TREE NODE
        treeRoot = new DefaultMutableTreeNode("Results");

        //RESULTS TREE
        tree = new JTree(treeRoot);
//        tree.setToggleClickCount(1);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(e -> {
            viewOnLCO.setEnabled(true);
            downloadSelected.setEnabled(true);
        });
        add(new JScrollPane(tree),
                Utility.gBCBuilder().pos(2, 1).size(3, 7).fill(GridBagConstraints.BOTH).insets(0, 5, 5, 0)
                        .weight(0.5D, 0.0D).build());

        //STATUS LABEL
        status = new JLabel();
        status.setVisible(false);
        add(status,
                Utility.gBCBuilder().pos(0, 6).size(2, 1).anchor(GridBagConstraints.FIRST_LINE_START).weight(0.1D, 0.0D)
                        .build());

        //FILES LABEL
        files = new JLabel();
        files.setVisible(false);
        add(files,
                Utility.gBCBuilder().pos(0, 7).size(2, 1).anchor(GridBagConstraints.FIRST_LINE_START).weight(0.1D, 0.5D)
                        .build());

        //SEARCH BUTTON
        JButton search = new JButton("Search (1000 files max)");
        search.addActionListener(e -> {
            if (offset.getText().isEmpty())
                offset.setText("0");

            search(objectName.getText(), proposalID.getText(), Integer.parseInt(offset.getText()), false);
        });
        add(search,
                Utility.gBCBuilder().pos(0, 8).fill(GridBagConstraints.HORIZONTAL).weight(0.025D, 0.0D).build());

        //FULL SEARCH BUTTON
        JButton fullSearch = new JButton("Full search");
        fullSearch.addActionListener(e -> {
            if (offset.getText().isEmpty())
                offset.setText("0");

            search(objectName.getText(), proposalID.getText(), Integer.parseInt(offset.getText()), true);
        });
        add(fullSearch,
                Utility.gBCBuilder().pos(1, 8).fill(GridBagConstraints.HORIZONTAL).weight(0.03D, 0.0D)
                        .build());

        //VIEW ON LCO BUTTON
        viewOnLCO = new JButton("View on LCO");
        viewOnLCO.addActionListener(e -> {
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
        viewOnLCO.setEnabled(false);
        add(viewOnLCO,
                Utility.gBCBuilder().pos(2, 8).fill(GridBagConstraints.HORIZONTAL).insets(0, 5, 0, 0)
                        .weight(0.05D, 0.0D)
                        .build());

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
                Utility.gBCBuilder().pos(3, 8).fill(GridBagConstraints.HORIZONTAL).weight(0.05D, 0.0D).build());

        //CLOSE BUTTON
        JButton back = new JButton("Close");
        back.addActionListener(e -> dispose());
        add(back,
                Utility.gBCBuilder().pos(4, 8).fill(GridBagConstraints.HORIZONTAL).insets(0, 5, 0, 0)
                        .weight(0.025D, 0.0D)
                        .build());
    }

    private void search(String objectName, String proposalID, int offset, boolean full) {
        treeRoot.removeAllChildren();
        tree.updateUI();

        viewOnLCO.setEnabled(false);
        downloadSelected.setEnabled(false);

        status.setText("Requesting data (may take a while)");
        status.setVisible(true);

        files.setText("");
        files.setVisible(true);

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        //LOAD DATA FROM LCO
        Executors.newSingleThreadExecutor().submit(() -> {
            int newOffset = offset;
            requests = new HashMap<>();

            while (true)
                try {
                    JSONObject json = Http.get(
                            "https://archive-api.lco.global/frames/?limit=" + limit + "&offset=" + newOffset + "&" +
                                    (objectName.isEmpty() ? "" : "target_name=" + objectName.replace(" ", "+")) +
                                    (proposalID.isEmpty() ? "" : "&proposal_id=" + proposalID));

                    if (json == null || !json.has("results")) {
                        status.setText("Error!");

                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        return;
                    }

                    resultsArray = json.getJSONArray("results");

                    if (resultsArray.isEmpty()) {
                        status.setText("No observations found");

                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        return;
                    }

//                    requests = new HashMap<>();
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

                    int fileCount = json.getInt("count");

                    newOffset += limit;
                    if (!full || fileCount - newOffset - limit < 0) {
                        status.setText("Found " + fileCount + " files");
                        files.setText("Showing " + requests.size() + " requests" +
                                (fileCount != resultsArray.length() ?
                                        ", " + (full ? fileCount : resultsArray.length()) + " files" : ""));

                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        break;
                    } else {
                        status.setText("Requesting data... (may take a while)");
                        files.setText(newOffset + " of " + fileCount + " files (" + newOffset * 100 / fileCount + "%)");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }

    private static class A implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            if (!Pattern.compile("\\d+").matcher(Character.valueOf(e.getKeyChar()).toString()).find())
                e.consume();
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
}
