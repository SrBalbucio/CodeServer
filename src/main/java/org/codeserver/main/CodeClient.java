package org.codeserver.main;

import co.gongzh.procbridge.client.Client;
import de.milchreis.uibooster.UiBooster;
import de.milchreis.uibooster.model.ListElement;
import de.milchreis.uibooster.model.LoginCredentials;
import org.codeserver.editor.EditorView;
import org.codeserver.model.EditableProject;
import org.codeserver.model.Language;
import org.codeserver.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CodeClient {

    private UiBooster ui;
    private boolean logged;
    private String token;
    private User user;
    private Client client;
    private EditableProject editableProject;
    private EditorView editorView;
    public static CopyOnWriteArrayList<Language> languages = new CopyOnWriteArrayList<>();

    public CodeClient() {
        this.ui = new UiBooster();
        performLogin();
        loadProjects();
    }

    public void performLogin() {
        try {
            LoginCredentials credentials = ui.showLogin("Enter your login credentials: ", "Login", "User", "Password", "Login", "Cancel");
            this.user = new User(credentials.getUsername(), credentials.getPassword());
            String[] server = ui.showTextInputDialog("Enter the server you want to connect to (IP:port):").split(":");
            this.client = new Client(server[0], Integer.parseInt(server[1]));
            JSONObject token = (JSONObject) client.request("login", new JSONObject().put("user", user.getUser()).put("password", user.getPassword()));
            if (token.getBoolean("error")) {
                ui.showErrorDialog("The credentials entered are incorrect or you do not have permission on this server!", ":C");
                System.exit(-1);
            }
            logged = true;
            this.token = token.getString("token");
        } catch (Exception e){
            e.printStackTrace();
            ui.showErrorDialog("The app was forced to terminate!", "Close!");
            System.exit(-1);
        }
    }

    public void loadProjects() {
        JSONArray langs = (JSONArray) client.request("list_languages", new JSONObject().put("token", token).put("data", new JSONObject()));
        langs.forEach(o -> languages.add(CodeServer.GSON.fromJson(((String) o), Language.class)));

        JSONArray array = (JSONArray) client.request("list_projects", new JSONObject().put("token", token).put("data", new JSONObject()));
        ListElement[] options = new ListElement[array.length()];

        for (int i = 0; i < array.length(); i++) {
            JSONObject p = (JSONObject) array.get(i);
            options[i] = new ListElement(p.getString("name"),
                    "Language of Project: " + p.getString("language") + "\nID of Project: " + p.getString("id"),
                    getLanguage(p.getString("language")).base64ToImage());
        }
        ListElement e = ui.showList("These are the projects that are available to you on the server you connected to:", "Select Project", (ex) -> {
        }, options);
        JSONObject project = (JSONObject) client.request("select_project", new JSONObject().put("token", token).put("data", new JSONObject().put("project", e.getTitle())));
        editableProject = new EditableProject(
                project.getString("id"),
                project.getString("name"),
                getLanguage(project.getString("language")),
                project.getJSONArray("paths").toList().stream().map(o -> (String) o).collect(Collectors.toList()));
        editorView = new EditorView(editableProject);
    }

    private Language getLanguage(String id) {
        return languages.stream().filter(l -> l.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
