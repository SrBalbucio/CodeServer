package org.codeserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
public class EditableProject {

    private String id;
    private String name;
    private Language language;
    private List<String> paths;

    public EditableProject(String id, String name, Language language, List<String> paths) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.paths = paths;
    }
}
