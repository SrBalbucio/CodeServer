package org.codeserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.codeserver.main.CodeServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Project {

    @NonNull
    @Getter
    private String id;
    @NonNull
    @Getter
    private String name;
    @NonNull
    @Getter
    private String path;
    @NonNull
    @Getter
    private String language;
    @Getter
    private List<Submodule> submodules;
    @Getter
    private File rootPath;

    public Project(@NonNull String id, @NonNull String name, @NonNull String path, @NonNull String language) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.language = language;
        this.rootPath = new File(path);
    }

    public List<String> listarArquivos() {
        return listarArquivos(rootPath, "");
    }

    public List<String> listarArquivos(File path, String prefixo) {
        List<String> listFiles = new ArrayList<>();
        File[] arquivos = path.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isDirectory()) {
                    if (arquivo.listFiles() != null && arquivo.listFiles().length > 0) {
                        listFiles.addAll(listarArquivos(arquivo, prefixo + arquivo.getName() + "/"));
                    } else {
                        listFiles.add(prefixo + arquivo.getName() + "/");
                    }
                } else {
                    if (!arquivo.getName().equalsIgnoreCase("zippedProject.zip") &&
                            !arquivo.getName().equalsIgnoreCase("codeserver-config.yml")) {
                        listFiles.add(prefixo + arquivo.getName());
                    }
                }
            }
        }
        return listFiles;
    }

    public String loadDocument(String filePath) {
        try {
            File file = new File(rootPath, filePath);
            if (!file.exists()) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int lines = 0;
            String line = null;
            while ((line = reader.readLine()) != null && lines < 5000) {
                builder.append(line).append("\n");
                lines++;
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public File getFile(String filePath) {
        File file = new File(rootPath, filePath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    public File createFile(String path, String fileName) throws IOException {
        File file = new File(rootPath + "/" + path, fileName);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public boolean deleteFile(String path) {
        File file = new File(rootPath, path);
        return file.delete();
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("id", id);
        json.put("language", language);
        return json;
    }

    public static Project fromSection(String id, ConfigurationSection section) {
        Project project = new Project(id, section.getString("name"), section.getString("path"), section.getString("language"));
        CodeServer.logger.info("The project " + project.getName() + "(" + id + ") was loaded successfully!");
        return project;
    }
}
