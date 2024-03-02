package org.codeserver.editor.tabs;

import lombok.Getter;
import lombok.SneakyThrows;
import org.codeserver.editor.components.HtmlPane;
import org.codeserver.editor.components.TabbedPanel;
import org.codeserver.model.Language;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
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

    @Getter
    private String path;
    @Getter
    private String document;
    private TabbedPanel panel;
    @Getter
    public Language language;

    public FileTab(String path, String document, TabbedPanel panel, Language language) {
        this.path = path;
        this.document = document;
        this.panel = panel;
        this.language = language;
        this.setLayout(new BorderLayout());
        this.add(getCenterPanel(), BorderLayout.CENTER);
    }

    private RTextScrollPane scrollPane;
    private RSyntaxTextArea syntaxArea;
    private AutoCompletion autoCompletion;
    private boolean hasMdPanel = false;
    private HtmlPane mdPanel;

    public JComponent getCenterPanel() {
        syntaxArea = new RSyntaxTextArea(document);
        syntaxArea.getDocument().addDocumentListener(this);
        syntaxArea.setCodeFoldingEnabled(true);
        String syntax = language != null ? language.getSyntaxName() : trySearchSyntax();
        syntaxArea.setSyntaxEditingStyle(syntax);
        DARK_THEME.apply(syntaxArea);
        autoCompletion = new AutoCompletion(createCompletionProvider());
        autoCompletion.install(syntaxArea);
        scrollPane = new RTextScrollPane(syntaxArea);
        addLanguageSupport(syntax);
        if(getFileExtension().equalsIgnoreCase("md")){
            hasMdPanel = true;
            this.mdPanel = new HtmlPane();
            mdPanel.load(document);
            return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, new JScrollPane(mdPanel));
        }
        return scrollPane;
    }

    private CompletionProvider createCompletionProvider() {

        // A DefaultCompletionProvider is the simplest concrete implementation
        // of CompletionProvider. This provider has no understanding of
        // language semantics. It simply checks the text entered up to the
        // caret position for a match against known completions. This is all
        // that is needed in the majority of cases.
        DefaultCompletionProvider provider = new DefaultCompletionProvider();

        // Add completions for all Java keywords. A BasicCompletion is just
        // a straightforward word completion.
        provider.addCompletion(new BasicCompletion(provider, "abstract"));
        provider.addCompletion(new BasicCompletion(provider, "assert"));
        provider.addCompletion(new BasicCompletion(provider, "break"));
        provider.addCompletion(new BasicCompletion(provider, "case"));
        // ... etc ...
        provider.addCompletion(new BasicCompletion(provider, "transient"));
        provider.addCompletion(new BasicCompletion(provider, "try"));
        provider.addCompletion(new BasicCompletion(provider, "void"));
        provider.addCompletion(new BasicCompletion(provider, "volatile"));
        provider.addCompletion(new BasicCompletion(provider, "while"));

        // Add a couple of "shorthand" completions. These completions don't
        // require the input text to be the same thing as the replacement text.
        provider.addCompletion(new ShorthandCompletion(provider, "sysout",
                "System.out.println(", "System.out.println("));
        provider.addCompletion(new ShorthandCompletion(provider, "syserr",
                "System.err.println(", "System.err.println("));

        return provider;

    }

    public String trySearchSyntax() {
        String fileName = getFileName();
        if (fileName.endsWith(".xml")) {
            return SyntaxConstants.SYNTAX_STYLE_XML;
        } else if (fileName.endsWith(".md")) {
            return SyntaxConstants.SYNTAX_STYLE_MARKDOWN;
        } else if (fileName.endsWith(".bat")) {
            return SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH;
        } else {
            return "";
        }
    }

    public String getFileName() {
        String[] paths = path.split("/");
        return paths[paths.length - 1];
    }

    public String getFileExtension() {
        String[] paths = getFileName().split("\\.");
        return paths[paths.length - 1];
    }

    public void addLanguageSupport(String syntax) {
        LanguageSupportFactory lsf = LanguageSupportFactory.get();
        lsf.register(syntaxArea);
    }

    @Override
    @SneakyThrows
    public void insertUpdate(DocumentEvent e) {
        boolean edited = (boolean) panel.getView().getClient().request("insert_document", new JSONObject()
                .put("path", path)
                .put("projectName", panel.getView().getProject().getName())
                .put("offset", e.getOffset())
                .put("text", e.getDocument().getText(e.getOffset(), e.getLength())));
        updateMDPanel();
    }

    @Override
    @SneakyThrows
    public void removeUpdate(DocumentEvent e) {
        boolean edited = (boolean) panel.getView().getClient().request("remove_document", new JSONObject()
                .put("path", path)
                .put("projectName", panel.getView().getProject().getName())
                .put("offset", e.getOffset())
                .put("length", e.getLength()));
        updateMDPanel();
    }

    @Override
    @SneakyThrows
    public void changedUpdate(DocumentEvent e) {
        boolean edited = (boolean) panel.getView().getClient().request("change_document", new JSONObject()
                .put("path", path)
                .put("projectName", panel.getView().getProject().getName())
                .put("offset", e.getOffset())
                .put("length", e.getLength())
                .put("text", e.getDocument().getText(e.getOffset(), e.getLength())));
        updateMDPanel();
    }

    public void updateMDPanel(){
        if(hasMdPanel){
            mdPanel.load(document);
        }
    }
}
