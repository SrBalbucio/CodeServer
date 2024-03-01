package org.codeserver.editor.components;

import org.codeserver.editor.EditorView;
import org.codeserver.utils.Icons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatusBar extends JPanel {

    private EditorView view;

    public StatusBar(EditorView view){
        this.view = view;
        setLayout(new BorderLayout());
        this.add(getWestPanel(), BorderLayout.WEST);
        this.add(getEastPanel(), BorderLayout.EAST);
    }

    private JLabel languageLabel;
    private JLabel pingLabel;

    public JPanel getWestPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(new EmptyBorder(3,5,3,5));
        panel.add((languageLabel = new JLabel("")));
        return panel;
    }
    public JPanel getEastPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(3,5,3,5));
        panel.add(new JLabel(Icons.PING));
        panel.add((pingLabel = new JLabel("0ms")));
        return panel;
    }

}
