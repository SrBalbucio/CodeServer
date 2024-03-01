package org.codeserver.editor.components;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;

public class FileTab extends JPanel {

    private String path;
    private String document;
    private TabbedPanel panel;

    public FileTab(String path, String document, TabbedPanel panel){
        this.path = path;
        this.document = document;
        this.panel = panel;
        this.setLayout(new BorderLayout());
        this.add(getCenterPanel(), BorderLayout.CENTER);
    }

    private RSyntaxTextArea syntaxArea;

    public JComponent getCenterPanel(){
        syntaxArea = new RSyntaxTextArea(document);
        return syntaxArea;
    }
}
