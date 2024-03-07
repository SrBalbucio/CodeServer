package org.codeserver.editor.components;

import org.codeserver.editor.EditorView;
import org.codeserver.utils.Icons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatusBar extends JPanel {

    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private EditorView view;

    public StatusBar(EditorView view){
        this.view = view;
        setLayout(new BorderLayout());
        this.add(getWestPanel(), BorderLayout.WEST);
        this.add(getEastPanel(), BorderLayout.EAST);
        executor.scheduleAtFixedRate(() -> {
            watchdogIcon.setVisible(view.isStartedWatchDog());
            if(view.getClient().isLogged()) {
                pingLabel.setText(view.getClient().getLastPing()+" ms");
            } else{
                pingLabel.setText("Desconectado!");
            }
        }, 5, 1, TimeUnit.SECONDS);
    }
    public JLabel languageLabel;
    public JLabel pingLabel;
    private JLabel watchdogIcon;

    public JPanel getWestPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(new EmptyBorder(3,5,3,5));
        panel.add((languageLabel = new JLabel("")));
        return panel;
    }
    public JPanel getEastPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(3,5,3,5));
        panel.add((watchdogIcon = new JLabel(Icons.WATCHDOG)));
        watchdogIcon.setToolTipText("CodeServer is analyzing project files in real time. You can now use another IDE, such as IDEA.\nDouble click to open the project in file explorer. ");
        watchdogIcon.setVisible(false);
        panel.add(new JLabel(Icons.PING));
        panel.add((pingLabel = new JLabel("0ms")));
        return panel;
    }

}
