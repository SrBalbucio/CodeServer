package org.codeserver.main;

import co.gongzh.procbridge.server.IDelegate;
import co.gongzh.procbridge.server.Server;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.codeserver.logger.LoggerFormat;
import org.codeserver.model.Document;
import org.codeserver.model.Language;
import org.codeserver.model.Project;
import org.codeserver.model.User;
import org.codeserver.utils.Payload;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class CodeServer implements IDelegate {

    private static File CONFIG_FILE = new File("config.yml");
    public static Gson GSON = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
    private static Algorithm ALGORITHM;

    private boolean running = false;
    public static Logger logger;
    private Server server;
    private YamlConfiguration config;
    private CopyOnWriteArrayList<Project> projects = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Language> languages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Document> openedDocuments = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String, Long> tokens = new ConcurrentHashMap<>();
    private ConcurrentHashMap<User, List<Document>> documentByUser = new ConcurrentHashMap<>();
    private ConcurrentHashMap<User, List<Project>> projectByUser = new ConcurrentHashMap<>();


    public CodeServer(int port) throws Exception {
        configureLogger();
        this.server = new Server(port, this);
        loadConfig();
        server.start();
        logger.info("\u001B[32mThe server was successfully started on port "+port+"!");
        running = true;
        while (running) {}
    }

    public void configureLogger() {
        this.logger = Logger.getLogger("SERVER-THREAD");
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LoggerFormat());
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }

    public void loadConfig() {
        try {
            if (!CONFIG_FILE.exists()) {
                Files.copy(this.getClass().getResourceAsStream("/config.yml"), CONFIG_FILE.toPath());
            }
            this.config = YamlConfiguration.loadConfiguration(CONFIG_FILE);

            ALGORITHM = Algorithm.HMAC512(config.getString("security.secret"));

            ConfigurationSection langs = config.getConfigurationSection("languages");
            for (String langName : langs.getKeys(false)) {
                this.languages.add(Language.fromSection(langName, langs.getConfigurationSection(langName)));
            }

            ConfigurationSection projects = config.getConfigurationSection("projects");
            for (String projectId : projects.getKeys(false)) {
                this.projects.add(Project.fromSection(projectId, projects.getConfigurationSection(projectId)));
            }

            ConfigurationSection users = config.getConfigurationSection("users");
            for (String key : users.getKeys(false)) {
                this.users.add(new User(users.getString(key + ".user"), users.getString(key + ".password")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private JSONObject auth(String user, String password) {
        JSONObject json = new JSONObject();
        json.put("error", true);
        users.stream()
                .filter(u -> u.getUser().equals(user) && u.getPassword().equals(password))
                .findFirst().ifPresent(u -> {
                    json.put("error", false);
                    String token = JWT.create()
                            .withIssuer("auth-token")
                            .withSubject(u.getUser())
                            .sign(ALGORITHM);
                    json.put("token", token);
                    tokens.put(token, System.currentTimeMillis() + 1000 * 60 * config.getInt("disconnectionTime"));
                    logger.info("The user "+user+" logged in successfully!");
                });
        return json;
    }

    private Payload validate(JSONObject payload) {
        String token = payload.getString("token");
        if (tokens.containsKey(token)) {
            // AQUI É MAIOR >>
            if (tokens.get(token) > System.currentTimeMillis()) {
                String user = JWT.decode(token).getSubject();
                User us = users.stream().filter(u -> u.getUser().equalsIgnoreCase(user)).findFirst().orElse(null);
                return new Payload(us, payload.getJSONObject("data"));
            }
        }
        return null;
    }

    public JSONArray listProjects() {
        JSONArray array = new JSONArray();
        projects.forEach(p -> array.put(p.toJSON()));
        return array;
    }

    public JSONArray listLanguages() {
        JSONArray array = new JSONArray();
        languages.forEach(l -> array.put(GSON.toJson(l)));
        return array;
    }

    public Project getProjectByName(String project) {
        return projects.stream().filter(p -> p.getName().equalsIgnoreCase(project)).findFirst().orElse(null);
    }

    public JSONObject selectProject(User user, String project) {
        JSONObject json = new JSONObject();
        Project p = getProjectByName(project);
        List<Project> pjs = projectByUser.getOrDefault(user, new CopyOnWriteArrayList<>());
        pjs.add(p);
        projectByUser.put(user, pjs);
        json.put("paths", p.listarArquivos());
        json.put("id", p.getId());
        json.put("name", p.getName());
        json.put("language", p.getLanguage());
        return json;
    }

    @Override
    public @Nullable Object handleRequest(@Nullable String key, @Nullable Object o) {

        JSONObject payload = (JSONObject) o;

        if (key.equalsIgnoreCase("login")) {
            return auth(payload.getString("user"), payload.getString("password"));
        }

        Payload data = validate(payload);

        if (data == null) {
            return new JSONObject().put("error", true).put("message", "Your token is not valid or has expired!");
        }

        if (key.equalsIgnoreCase("list_projects")) {
            return listProjects();
        } else if (key.equalsIgnoreCase("select_project")) {
            return selectProject(data.getKey(), data.getValue().getString("project"));
        } else if (key.equalsIgnoreCase("list_languages")) {
            return listLanguages();
        } else if (key.equalsIgnoreCase("download_file")) {

        } else if (key.equalsIgnoreCase("get_document_file")) {
            Project project = getProjectByName(data.getValue().getString("projectName"));
            String path = data.getValue().getString("path");
            File file = project.getFile(path);
            if (file == null) {
                return new JSONObject().put("error", true).put("message", "This file does not exist or the server cannot open it now!");
            }
            if (file.isDirectory()) {
                return new JSONObject().put("error", true).put("silent", true).put("message", "Is this shit a directory?????");
            }
            Document document = openedDocuments.stream().filter(d -> d.getPath().equalsIgnoreCase(path) && d.getProject().equals(project)).findFirst().orElse(null);
            if (document == null) {
                try {
                    document = new Document(path, file, project);
                    openedDocuments.add(document);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new JSONObject().put("error", true).put("message", "This file does not exist or the server cannot open it now!");
                }
            }

            List<Document> documents = documentByUser.getOrDefault(data.getKey(), new CopyOnWriteArrayList<>());
            documents.add(document);
            documentByUser.put(data.getKey(), documents);

            return new JSONObject().put("document", document.getText());
        } else if (key.equalsIgnoreCase("insert_document")) {
            String path = data.getValue().getString("path");
            String projectName = data.getValue().getString("projectName");
            int offset = data.getValue().getInt("offset");
            String text = data.getValue().getString("text");
            Document document = documentByUser.get(data.getKey()).stream().filter(d -> d.getPath().equalsIgnoreCase(path) && d.getProject().getName().equalsIgnoreCase(projectName)).findFirst().orElse(null);
            if (document != null) {
                document.insertString(offset, text);
                return true;
            }
            return false;
        } else if (key.equalsIgnoreCase("remove_document")) {
            String path = data.getValue().getString("path");
            String projectName = data.getValue().getString("projectName");
            int offset = data.getValue().getInt("offset");
            int length = data.getValue().getInt("length");
            Document document = documentByUser.get(data.getKey()).stream().filter(d -> d.getPath().equalsIgnoreCase(path) && d.getProject().getName().equalsIgnoreCase(projectName)).findFirst().orElse(null);
            if (document != null) {
                document.remove(offset, length);
                return true;
            }
            return false;
        } else if (key.equalsIgnoreCase("change_document")) {
            String path = data.getValue().getString("path");
            String projectName = data.getValue().getString("projectName");
            int offset = data.getValue().getInt("offset");
            int length = data.getValue().getInt("length");
            String text = data.getValue().getString("text");
            Document document = documentByUser.get(data.getKey()).stream().filter(d -> d.getPath().equalsIgnoreCase(path) && d.getProject().getName().equalsIgnoreCase(projectName)).findFirst().orElse(null);
            if (document != null) {
                document.replace(offset, length, text);
                return true;
            }
            return false;
        } else if (key.equalsIgnoreCase("close_document_file")) {
            Project project = getProjectByName(data.getValue().getString("projectName"));
            String path = data.getValue().getString("path");
            if (project != null) {
                documentByUser.get(data.getKey()).stream().filter(d -> d.getPath().equals(path) && d.getProject().equals(project)).findFirst().ifPresent(d -> {
                    documentByUser.get(data.getKey()).remove(d);
                    if(documentByUser.values().stream().noneMatch(l -> l.contains(d))){
                        d.save();
                        openedDocuments.remove(d);
                    }
                });
            }
        } else if(key.equalsIgnoreCase("close_project")){
            Project project = getProjectByName(data.getValue().getString("projectName"));
            projectByUser.get(data.getKey()).stream().filter(p -> p.equals(project)).findFirst().ifPresent(p -> {
                projectByUser.get(data.getKey()).remove(p);
            });
        } else if(key.equalsIgnoreCase("create_file")){
            Project project = getProjectByName(data.getValue().getString("projectName"));
            if(project != null){
                try {
                    project.createFile(data.getValue().getString("path"), data.getValue().getString("fileName"));
                    return new JSONObject().put("error", false);
                } catch (Exception ex){
                    ex.printStackTrace();
                    return new JSONObject().put("error", true).put("message", "Could not create this file! Check the server to find out more.");
                }
            }
        } else if(key.equalsIgnoreCase("update_paths")){
            Project project = getProjectByName(data.getValue().getString("projectName"));
            if(project != null){
                return new JSONArray(project.listarArquivos());
            }
        } else if(key.equalsIgnoreCase("delete_file")){
            Project project = getProjectByName(data.getValue().getString("projectName"));
            if(project != null){
                return project.deleteFile(data.getValue().getString("path"));
            }
            return false;
        } else if (key.equalsIgnoreCase("update_window_size")) {
            data.getKey().setAppSize(new Dimension(data.getValue().getInt("w"), data.getValue().getInt("h")));
        } else if (key.equalsIgnoreCase("update_screen_size")) {
            data.getKey().setScreenSize(new Dimension(data.getValue().getInt("w"), data.getValue().getInt("h")));
        } else if (key.equalsIgnoreCase("init_watchdod")){

        }

        return null;
    }
}
