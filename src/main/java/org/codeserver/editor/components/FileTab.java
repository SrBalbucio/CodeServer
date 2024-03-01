package org.codeserver.editor.components;

import lombok.SneakyThrows;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;

public class FileTab extends JPanel implements DocumentListener {

    private static Theme DARK_THEME;

    static {
        try {
            DARK_THEME = Theme.load(FileTab.class.getResourceAsStream(
                    "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String path;
    private String document;
    private TabbedPanel panel;

    public FileTab(String path, String document, TabbedPanel panel) {
        this.path = path;
        this.document = document;
        this.panel = panel;
        this.setLayout(new BorderLayout());
        this.add(getCenterPanel(), BorderLayout.CENTER);
    }

    private RTextScrollPane scrollPane;
    private RSyntaxTextArea syntaxArea;

    public JComponent getCenterPanel() {
        syntaxArea = new RSyntaxTextArea(document);
        syntaxArea.getDocument().addDocumentListener(this);
        DARK_THEME.apply(syntaxArea);
        scrollPane = new RTextScrollPane(syntaxArea);
        return scrollPane;
    }

    @Override
    @SneakyThrows
    public void insertUpdate(DocumentEvent e) {
        boolean edited = (boolean) panel.getView().getClient().request("insert_document", new JSONObject()
                .put("path", path)
                .put("offset", e.getOffset())
                .put("text", e.getDocument().getText(e.getOffset(), e.getLength())));
    }

    @Override
    @SneakyThrows
    public void removeUpdate(DocumentEvent e) {
        boolean edited = (boolean) panel.getView().getClient().request("remove_document", new JSONObject()
                .put("path", path)
                .put("offset", e.getOffset())
                .put("length", e.getLength()));
    }

    @Override
    @SneakyThrows
    public void changedUpdate(DocumentEvent e) {
        boolean edited = (boolean) panel.getView().getClient().request("change_document", new JSONObject()
                .put("path", path)
                .put("offset", e.getOffset())
                .put("length", e.getLength())
                .put("text", e.getDocument().getText(e.getOffset(), e.getLength())));
    }
}
