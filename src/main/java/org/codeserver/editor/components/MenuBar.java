package org.codeserver.editor.components;

import org.codeserver.editor.EditorView;

import javax.swing.*;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar {

    private EditorView view;

    public MenuBar(EditorView view) {
        super();
        this.view = view;
        this.add(getProjectMenu());
        this.add(getFileMenu());
        this.add(getWatchdogMenu());
    }

    public JMenu getProjectMenu() {
        JMenu menu = new JMenu("Project");
        {
            JMenuItem switchProject = new JMenuItem("Change Project");
            switchProject.addActionListener((e) -> {
                view.setVisible(false);
                view.dispose();
                view.getClient().getOpenedProjects().remove(view.getProject());
                view.getClient().loadProjects();
            });
            switchProject.setAccelerator(KeyStroke.getKeyStroke("F1"));
            menu.add(switchProject);
        }
        {
            JMenuItem switchProject = new JMenuItem("Close Project");
            switchProject.addActionListener((e) -> {
                view.setVisible(false);
                view.dispose();
                view.getClient().getOpenedProjects().remove(view.getProject());
                view.getClient().loadProjects();
            });
            switchProject.setAccelerator(KeyStroke.getKeyStroke("F2"));
            menu.add(switchProject);
        }
        {
            JMenuItem otherProject = new JMenuItem("Open New Window");
            otherProject.addActionListener((e) -> {
                view.getClient().loadProjects();
            });
            otherProject.setAccelerator(KeyStroke.getKeyStroke("F2"));
            menu.add(otherProject);
        }

        return menu;
    }

    public JMenu getFileMenu() {
        JMenu menu = new JMenu("File");
        {
            JMenuItem newFile = new JMenuItem("New File...");
            newFile.addActionListener((e) -> {
                view.getClient().loadProjects();
            });
            newFile.setAccelerator(KeyStroke.getKeyStroke("ctrl f1"));
            menu.add(newFile);
        }
        return menu;
    }

    public JMenu getWatchdogMenu() {
        JMenu menu = new JMenu("WatchDog");
        {
            JMenuItem start = new JMenuItem("Start/Stop...");
            start.addActionListener((e) -> {
                view.toggleWatchDog();
            });
            start.setAccelerator(KeyStroke.getKeyStroke("alt f1"));
            menu.add(start);
        }
        return menu;
    }
}
