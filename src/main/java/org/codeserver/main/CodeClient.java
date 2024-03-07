package org.codeserver.main;

import balbucio.fts.FTSClient;
import co.gongzh.procbridge.client.Client;
import de.milchreis.uibooster.UiBooster;
import de.milchreis.uibooster.components.ProgressDialog;
import de.milchreis.uibooster.model.ListElement;
import de.milchreis.uibooster.model.LoginCredentials;
import lombok.Getter;
import org.codeserver.editor.EditorView;
import org.codeserver.model.EditableProject;
import org.codeserver.model.Language;
import org.codeserver.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CodeClient {

    @Getter
    private UiBooster ui;
    @Getter
    private long lastPing;
    @Getter
    private boolean logged;
    @Getter
    private String token;
    @Getter
    private User user;
    @Getter
    private Client client;
    @Getter
    private FTSClient fileClient;
    @Getter
    private List<EditableProject> openedProjects = new CopyOnWriteArrayList<>();
    @Getter
    private EditorView editorView;
    public static CopyOnWriteArrayList<Language> languages = new CopyOnWriteArrayList<>();
    @Getter
    private ProgressDialog progress;
    public static boolean preventStop;
    public static File ROOT_PATH = new File("CodeServer");

    public CodeClient() {
        this.ui = new UiBooster();
        ROOT_PATH.mkdirs();
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
            if(token.optBoolean("hasFileServer", false)) {
                this.fileClient = new FTSClient(server[0], token.getInt("fileServerPort"));
            }
            logged = true;
            this.token = token.getString("token");
        } catch (Exception e){
            e.printStackTrace();
            ui.showErrorDialog("The app was forced to terminate!", "Close!");
            System.exit(-1);
        }
    }

    public boolean loadProjects() {
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
        if(e == null){
            return false;
        }
        progress = ui.showProgressDialog("We are loading your project, this may take a few moments...", "Loading project", 0, 100);
        progress.setProgress(10);
        JSONObject project = (JSONObject) client.request("select_project", new JSONObject().put("token", token).put("data", new JSONObject().put("project", e.getTitle())));
        progress.setProgress(60);
        if(openedProjects.stream().anyMatch(p -> p.getName().equalsIgnoreCase(project.getString("name")) && p.getId().equalsIgnoreCase(project.getString("id")))){
            ui.showErrorDialog("This project is now open!", "Unable to open project!");
            return false;
        }
        EditableProject editableProject = new EditableProject(
                project.getString("id"),
                project.getString("name"),
                getLanguage(project.getString("language")),
                project.getJSONArray("paths").toList().stream().map(o -> (String) o).collect(Collectors.toList()));
        openedProjects.add(editableProject);
        progress.setProgress(70);
        SwingUtilities.invokeLater(() -> {
            editorView = new EditorView(editableProject, this);
        });
        progress.setProgress(100);
        progress.close();
        return true;
    }

    public static Language getLanguage(String id) {
        return languages.stream().filter(l -> l.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public static Language getLanguageByPath(String path) {
        return languages.stream().filter(l -> l.isSupportedFile(path)).findFirst().orElse(null);
    }

    public Object request(String method, Object object){
        long i = System.currentTimeMillis();
        Object obj = client.request(method, new JSONObject().put("token", token).put("data", object == null ? new JSONObject() : object));
        lastPing = System.currentTimeMillis() - i;
        return obj;
    }

    public void preventClose(){
        preventStop = true;
    }

    public void checkIfCanClose(){
        if(preventStop){
            preventStop = false;
            return;
        }
        if(openedProjects.isEmpty()){
            request("closed", new JSONObject().put("time", System.currentTimeMillis()));
            System.exit(0);
        }
    }
}
