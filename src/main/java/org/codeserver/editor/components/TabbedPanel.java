package org.codeserver.editor.components;

import lombok.Getter;
import org.codeserver.editor.EditorView;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;

public class TabbedPanel extends JTabbedPane {

    @Getter
    private EditorView view;

    public TabbedPanel(EditorView view) {
        super();
        this.view = view;
    }

    public FileTab createNewFileTab(TreePath node) {
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < node.getPathCount(); i++) {
            DefaultMutableTreeNode mtn = ((DefaultMutableTreeNode) node.getPath()[i]);
            String str = mtn.getUserObject().toString();
            if ((node.getPathCount() - 1) == i && mtn.getChildCount() <= 0) {
                pathBuilder.append("/").append(str);
            } else{
                pathBuilder.append("/").append(str.replace(".", "/"));
            }
        }

        String path = pathBuilder.toString();
        String[] paths = path.split("/");
        System.out.println(path);

        if (this.indexOfTab(paths[paths.length - 1]) >= 0) {
            return null;
        }

        JSONObject json = (JSONObject) view.getClient().request("get_document_file", new JSONObject().put("path", path).put("projectName", view.getProject().getName()));
        if (json.optBoolean("error", false) && !json.optBoolean("silent", false)) {
            view.getClient().getUi().showErrorDialog(json.getString("message"), "Error!");
            return null;
        }
        if (!json.has("document")) {
            return null;
        }
        FileTab tab = new FileTab(path, json.getString("document"), this);
        this.addTab(paths[paths.length - 1], tab);
        int index = this.indexOfTab(paths[paths.length - 1]);
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(paths[paths.length - 1]);
        JButton btnClose = new JButton("x");
        btnClose.putClientProperty("JButton.buttonProperty", "roundRect");
        btnClose.setFocusPainted(false);
        //btnClose.setOpaque(false);
        btnClose.setContentAreaFilled(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);

        this.setTabComponentAt(index, pnlTab);

        btnClose.addActionListener((e) -> {
            if (index >= 0) {
                this.removeTabAt(index);
            }
        });
        return tab;
    }
}
