package org.codeserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.json.JSONObject;

import java.io.File;
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
                    listFiles.add(prefixo + arquivo.getName());
                }
            }
        }
        return listFiles;
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
        return project;
    }
}
