package org.codeserver.editor;

import org.codeserver.editor.components.ExplorerPanel;
import org.codeserver.model.EditableProject;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Vector;

public class EditorView extends JFrame {

    private EditableProject project;

    public EditorView(EditableProject project){
        super("CodeServer - "+project.getName());
        this.project = project;
        this.setLayout(new BorderLayout());
        this.add(getCenter(), BorderLayout.CENTER);
        this.setSize(1280, 720);
        this.setVisible(true);
    }

    private ExplorerPanel explorer;

    public JSplitPane getCenter(){
        JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, (explorer = new ExplorerPanel(this, project)), new JTabbedPane());
        return panel;
    }

}
