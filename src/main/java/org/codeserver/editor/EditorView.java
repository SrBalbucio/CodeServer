package org.codeserver.editor;

import lombok.Getter;
import org.codeserver.editor.components.ExplorerPanel;
import org.codeserver.editor.components.MenuBar;
import org.codeserver.editor.components.StatusBar;
import org.codeserver.editor.components.TabbedPanel;
import org.codeserver.main.CodeClient;
import org.codeserver.model.EditableProject;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

public class EditorView extends JFrame implements WindowListener {

    @Getter
    private EditableProject project;
    @Getter
    private CodeClient client;
    @Getter
    private StatusBar statusBar;

    public EditorView(EditableProject project, CodeClient client){
        super("CodeServer - "+project.getName());
        this.project = project;
        this.client = client;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.add(getCenter(), BorderLayout.CENTER);
        this.add((statusBar = new StatusBar(this)), BorderLayout.SOUTH);
        this.addWindowListener(this);
        this.setJMenuBar(new MenuBar(this));
        this.setSize(1280, 720);
        this.setVisible(true);
    }

    @Getter
    private ExplorerPanel explorer;
    @Getter
    private TabbedPanel tabbedPanel;

    public JSplitPane getCenter(){
        JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, (explorer = new ExplorerPanel(this, project)), (tabbedPanel = new TabbedPanel(this)));
        return panel;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        client.request("close_project", new JSONObject().put("projectName", project.getName()));
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
