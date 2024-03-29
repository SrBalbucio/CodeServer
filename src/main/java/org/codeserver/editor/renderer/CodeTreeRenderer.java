package org.codeserver.editor.renderer;

import lombok.AllArgsConstructor;
import org.codeserver.main.CodeClient;
import org.codeserver.model.EditableProject;
import org.codeserver.utils.Icons;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;

@AllArgsConstructor
public class CodeTreeRenderer extends DefaultTreeCellRenderer {

    private EditableProject project;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getRoot();

        String str = (String) node.getUserObject();

        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < node.getUserObjectPath().length; i++) {
            pathBuilder.append("/").append(node.getUserObjectPath()[i]);
        }

        String path = pathBuilder.toString();

        if(path.contains(".")){
            setIcon(Icons.FILE);
        } else if(expanded){
            setIcon(Icons.OPEN_FOLDER);
        } else {
            setIcon(Icons.FOLDER);
        }

        if (path.contains(project.getLanguage().getSrcPath())) {
            String[] parsedPath = str.split("\\.");
            if (parsedPath.length > 0) {
                setIcon(Icons.SOURCE_FOLDER_WHITE);
            }
        }

        CodeClient.languages.forEach(l -> {
            if (str.endsWith(l.getFileExtesion())) {
                try {
                    setIcon(new ImageIcon(Icons.resizeImage(l.base64ToImage(), 16, 16)));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        if(str.endsWith(".iml")){
            setIcon(Icons.JETBRAINS_WHITE);
        } else if(str.endsWith(".json")){
            setIcon(Icons.JSON);
        } else if(str.endsWith(".xml")){
            setIcon(Icons.XML);
        } else if(str.endsWith(".txt")){
            setIcon(Icons.TXT);
        } else if(str.endsWith(".java")){
            setIcon(Icons.JAVA_COLORIDO);
        } else if(str.endsWith(".rs")){
            setIcon(Icons.RUST_COLORIDO);
        } else if(str.endsWith(".png") || str.endsWith(".jpg") || str.endsWith(".gif")){
            setIcon(Icons.IMAGE);
        } else if(str.endsWith(".sh") || str.endsWith(".bat")){
            setIcon(Icons.CMD);
        } else if(str.endsWith(".md")){
            setIcon(Icons.MARKDOWN_ICON);
        } else if(str.endsWith(".asm") || str.endsWith(".ASM")){
            setIcon(Icons.ASSEMBLY);
        }

        switch (str) {
            case ".git": {
                setIcon(Icons.GIT);
                break;
            }
            case ".idea": {
                setIcon(Icons.JETBRAINS_WHITE);
                break;
            }
            case "pom.xml": {
                setIcon(Icons.MAVEN);
                break;
            }
            case ".classpath":
            case ".settings":
            case ".project": {
                setIcon(Icons.ECLIPSE);
                break;
            }
            case "Cargo.lock":
            case "Cargo.toml":{
                setIcon(Icons.CARGO_RUST);
            }
        }

        if(str.equalsIgnoreCase(project.getName()+" ("+project.getId()+")")){
            setIcon(Icons.FOLDER_EXTENSION);
        }

        return c;
    }
}
