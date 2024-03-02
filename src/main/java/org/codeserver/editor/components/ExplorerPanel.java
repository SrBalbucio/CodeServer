package org.codeserver.editor.components;

import org.codeserver.editor.EditorView;
import org.codeserver.editor.renderer.CodeTreeRenderer;
import org.codeserver.model.EditableProject;
import org.codeserver.utils.PathUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ExplorerPanel extends JScrollPane implements TreeSelectionListener, MouseListener {

    private DefaultMutableTreeNode nodeRoot;
    private DefaultTreeModel treeModel;
    private JTree fileTree;
    private EditorView view;
    private EditableProject project;

    public ExplorerPanel(EditorView view, EditableProject project) {
        super();
        this.view = view;
        this.project = project;
        this.nodeRoot = new DefaultMutableTreeNode(project.getName() + " (" + project.getId() + ")");
        this.treeModel = new DefaultTreeModel(nodeRoot, true);
        Collections.sort(project.getPaths());
        this.fileTree = createJTree(project.getPaths());
        this.fileTree.setCellRenderer(new CodeTreeRenderer(project));
        this.fileTree.addTreeSelectionListener(this);
        this.fileTree.addMouseListener(this);
        this.getViewport().setView(fileTree);
    }

    public JTree createJTree(List<String> paths) {
        return new JTree(createTreeModel(paths));
    }

    public DefaultTreeModel createTreeModel(List<String> paths){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(project.getName() + " (" + project.getId() + ")");

        // BOTA AS PASTAS
        paths.stream().filter(path -> !path.startsWith(project.getLanguage().getSrcPath()) && !isFile(path)).forEach(path -> {
            DefaultMutableTreeNode parentNode;
            String[] parts = path.split("/");
            parentNode = findOrCreateNode(root, parts[0]);
            for (int i = 1; i < parts.length; i++) {
                parentNode = findOrCreateNode(parentNode, parts[i]);
            }
        });

        DefaultMutableTreeNode srcNode = null;
        String[] srcPaths = project.getLanguage().getSrcPath().split("/");
        StringBuilder srcPathing = new StringBuilder();
        for (int i = 0; i < srcPaths.length; i++) {
            if(containsPath(srcPathing.toString()+srcPaths[i], paths)) {
                if (i == 0) {
                    srcNode = new DefaultMutableTreeNode(srcPaths[i]);
                    root.add(srcNode);
                    srcPathing.append(srcPaths[i]).append("/");
                } else {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(srcPaths[i]);
                    srcNode.add(node);
                    srcNode = node;
                    srcPathing.append(srcPaths[i]).append("/");
                }
                System.out.println(srcPathing.toString()+srcPaths[i]);
            }
        }


        DefaultMutableTreeNode finalSrcNode = srcNode;
        paths.stream().filter(path -> path.startsWith(project.getLanguage().getSrcPath())).forEach(path -> {
            DefaultMutableTreeNode parentNode;
            String[] parts = path.replace(project.getLanguage().getSrcPath() + "/", "").split("/");

            StringBuilder javaPackage = new StringBuilder();

            for (int i = 0; i < (parts.length - 1); i++) {
                if (i > 0) {
                    javaPackage.append(".");
                }
                javaPackage.append(parts[i]);
            }

            String className = parts[parts.length - 1];

            parentNode = findOrCreateNode(finalSrcNode, javaPackage.toString());
            DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(className);
            parentNode.add(classNode);
        });

        paths.stream().filter(path -> !path.startsWith(project.getLanguage().getSrcPath()) && isFile(path)).forEach(path -> {
            DefaultMutableTreeNode parentNode;
            String[] parts = path.split("/");
            parentNode = findOrCreateNode(root, parts[0]);
            for (int i = 1; i < parts.length; i++) {
                parentNode = findOrCreateNode(parentNode, parts[i]);
            }
        });

        return new DefaultTreeModel(root);
    }

    private DefaultMutableTreeNode findOrCreateNode(DefaultMutableTreeNode parent, String nodeName) {
        if(nodeName.isEmpty()){ return parent; }
        for (int i = 0; i < parent.getChildCount(); i++) {
            TreeNode childNode = parent.getChildAt(i);
            if (childNode instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) childNode;
                if (child.getUserObject().toString().equals(nodeName)) {
                    return child;
                }
            }
        }
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName);
        parent.add(newNode);
        return newNode;
    }

    public void updatePaths(List<String> paths){
        fileTree.setModel(createTreeModel(paths));
    }

    private boolean isFile(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1].contains(".") && !parts[parts.length - 1].startsWith(".");
    }

    private boolean containsPath(String path, List<String> paths){
        return paths.stream().anyMatch(p -> p.contains(path));
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        System.out.println(e.getPath().toString());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int selRow = fileTree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = fileTree.getPathForLocation(e.getX(), e.getY());
        if(selRow != -1) {
            if(e.getClickCount() == 1) {
            }
            else if(e.getClickCount() == 2) {
                view.getTabbedPanel().createNewFileTab(selPath);
            }
        }

        if(SwingUtilities.isRightMouseButton(e)){
            JPopupMenu menu = new JPopupMenu();
            {
                JMenuItem newFile = new JMenuItem("New File...");
                newFile.addActionListener((event) -> view.createNewFile(PathUtils.generatePath(selPath)));
                menu.add(newFile);
            }
            menu.show(this, e.getX(),  e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
