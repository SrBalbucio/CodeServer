package org.codeserver.editor.components;

import lombok.Getter;
import org.codeserver.editor.EditorView;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TabbedPanel extends JTabbedPane {

    @Getter
    private EditorView view;

    public TabbedPanel(EditorView view){
        super();
        this.view = view;
    }

    public FileTab createNewFileTab(TreePath node){
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < node.getPathCount(); i++) {
            pathBuilder.append("/").append(node.getPath()[i]);
        }

        String path = pathBuilder.toString();
        String[] paths = path.split("/");
        System.out.println(path);
        JSONObject json = (JSONObject) view.getClient().request("get_document_file", new JSONObject().put("path", path).put("projectName", view.getProject().getName()));
        if(json.optBoolean("error", false)){
            view.getClient().getUi().showErrorDialog(json.getString("message"), "Error!");
            return null;
        }
        FileTab tab = new FileTab(path, json.getString("document"), this);
        this.addTab(paths[paths.length - 1], tab);
        return tab;
    }
}
