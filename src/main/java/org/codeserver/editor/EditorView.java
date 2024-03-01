package org.codeserver.editor;

import lombok.Getter;
import org.codeserver.editor.components.ExplorerPanel;
import org.codeserver.editor.components.MenuBar;
import org.codeserver.main.CodeClient;
import org.codeserver.model.EditableProject;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Vector;

public class EditorView extends JFrame {

    @Getter
    private EditableProject project;
    @Getter
    private CodeClient client;

    public EditorView(EditableProject project, CodeClient client){
        super("CodeServer - "+project.getName());
        this.project = project;
        this.client = client;
        this.setLayout(new BorderLayout());
        this.add(getCenter(), BorderLayout.CENTER);
        this.setJMenuBar(new MenuBar(this));
        this.setSize(1280, 720);
        this.setVisible(true);
    }

    private ExplorerPanel explorer;

    public JSplitPane getCenter(){
        JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, (explorer = new ExplorerPanel(this, project)), new JTabbedPane());
        return panel;
    }

}
