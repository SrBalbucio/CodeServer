package org.codeserver.model;

import lombok.Getter;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Document {

    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Getter
    private String path;
    @Getter
    private Project project;
    private File file;
    private StringBuilder builder;
    private int lines = 0;

    public Document(String path, File file, Project project) throws Exception {
        this.path = path;
        this.project = project;
        this.file = file;
        builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = reader.readLine()) != null && lines < 5000) {
            builder.append(line).append("\n");
            lines++;
        }
        executor.scheduleAtFixedRate(() -> {
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }, 20, 5, TimeUnit.SECONDS);
    }

    
    public void remove(int offs, int len) {
        builder.delete(offs, offs+len);
    }

    
    public void insertString(int offset, String str) {
        builder.insert(offset, str);
    }

    
    public String getText(int offset, int length) throws BadLocationException {
        return null;
    }

    
    public void getText(int offset, int length, Segment txt) throws BadLocationException {

    }

    public void replace(int offset, int length, String str){
        builder.replace(offset, offset+length, str);
    }

    public String getText(){
        return builder.toString();
    }

    
    public Position getStartPosition() {
        return null;
    }

    
    public Position getEndPosition() {
        return null;
    }

    
    public Position createPosition(int offs) throws BadLocationException {
        return null;
    }

}
