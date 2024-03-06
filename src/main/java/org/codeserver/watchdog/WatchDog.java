package org.codeserver.watchdog;

import org.codeserver.model.EditableProject;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class WatchDog {

    private EditableProject project;
    private File projectFolder;
    private WatchService watchService;

    public WatchDog(EditableProject project, File projectFolder) throws Exception {
        this.project = project;
        this.projectFolder = projectFolder;
        this.watchService = FileSystems.getDefault().newWatchService();
    }
}
