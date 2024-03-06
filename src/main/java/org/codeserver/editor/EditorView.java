package org.codeserver.editor;

import de.milchreis.uibooster.model.Form;
import lombok.Getter;
import org.codeserver.editor.components.ExplorerPanel;
import org.codeserver.editor.components.MenuBar;
import org.codeserver.editor.components.StatusBar;
import org.codeserver.editor.components.TabbedPanel;
import org.codeserver.main.CodeClient;
import org.codeserver.model.EditableProject;
import org.codeserver.watchdog.WatchDog;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;
import java.util.stream.Collectors;

public class EditorView extends JFrame implements WindowListener, ComponentListener {

    @Getter
    private EditableProject project;
    @Getter
    private CodeClient client;
    @Getter
    private StatusBar statusBar;

    public EditorView(EditableProject project, CodeClient client) {
        super("CodeServer - " + project.getName());
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

    public JSplitPane getCenter() {
        JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, (explorer = new ExplorerPanel(this, project)), (tabbedPanel = new TabbedPanel(this)));
        return panel;
    }

    public void updatePathsOfProject() {
        JSONArray array = (JSONArray) client.request("update_paths", new JSONObject().put("projectName", project.getName()).put("id", project.getId()));
        explorer.updatePaths(array.toList().stream().map(o -> (String) o).collect(Collectors.toList()));
    }

    public void createNewFile(String path) {
        Form cf = client.getUi().createForm("Create New File")
                .addText("Directory:", path).addText("File Name").show();
        String p = cf.getByIndex(0).asString();
        String name = cf.getByIndex(1).asString();
        project.getPaths().add(p + "/" + name);
        System.out.println(p + "/" + name);
        JSONObject json = (JSONObject) client.request("create_file", new JSONObject().put("path", p).put("fileName", name).put("projectName", project.getName()));
        if (json.optBoolean("error", false)) {
            client.getUi().showErrorDialog(json.getString("message"), "File not created :C");
        } else {
            updatePathsOfProject();
        }
    }

    public void deleteFile(String path) {
        client.getUi().showConfirmDialog("Do you really want to delete the file/path? \nFile/path: " + path, "Delete File", () -> {
            boolean approved = (boolean) client.request("delete_file", new JSONObject().put("path", path).put("projectName", project.getName()));
            if (approved) {
                updatePathsOfProject();
            } else {
                client.getUi().showErrorDialog("Unable to delete the file/path!", "File not deleted :C");
            }
        }, () -> {
        });
    }

    public void moveFiles(TreePath... paths) {
    }

    @Getter
    private boolean startedWatchDog = false;
    private WatchDog watchDog;

    public void toggleWatchDog() {
        if (startedWatchDog) {
            startedWatchDog = false;
        } else {
            client.getUi().showConfirmDialog("WatchDog mode downloads project files to your PC and starts analyzing them in real time, allowing you to edit them in your preferred IDE.\n" +
                    "\n" +
                    "It is important that you terminate the App and WatchDog correctly so that no files are lost or leaked.\n" +
                    "\n" +
                    "Do you really want to continue?", "WatchDog Mode", () -> {
                startedWatchDog = true;

            }, () -> {
            });
        }
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
        client.checkIfCanClose();
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

    private GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();

    @Override
    public void componentResized(ComponentEvent e) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        client.request("update_window_size", new JSONObject().put("h", this.getHeight()).put("w", this.getWidth()));
        client.request("update_screen_size", new JSONObject().put("h", dim.getHeight()).put("w", dim.getWidth()));
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
