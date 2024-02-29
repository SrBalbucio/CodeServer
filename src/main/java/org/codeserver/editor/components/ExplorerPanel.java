package org.codeserver.editor.components;

import org.codeserver.editor.EditorView;
import org.codeserver.editor.renderer.CodeTreeRenderer;
import org.codeserver.model.EditableProject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ExplorerPanel extends JScrollPane  {

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
        this.getViewport().setView(fileTree);
    }

    public JTree createJTree(List<String> paths) {
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
                    srcPathing.append(srcPaths[i]);
                } else {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(srcPaths[i]);
                    srcNode.add(node);
                    srcNode = node;
                    srcPathing.append(srcPaths[i]).append("/");
                }
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
            System.out.println(className);
            System.out.println(javaPackage);

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


        return new JTree(new DefaultTreeModel(root));
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
        System.out.println(nodeName);
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName);
        parent.add(newNode);
        return newNode;
    }

    private boolean isFile(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1].contains(".");
    }

    private boolean containsPath(String path, List<String> paths){
        return paths.stream().anyMatch(p -> p.contains(path));
    }
}
