package org.codeserver.model;

import lombok.Getter;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Document {

    @Getter
    private String path;
    private File file;
    private StringBuilder builder;
    private int lines = 0;

    public Document(String path, File file) throws Exception {
        this.path = path;
        this.file = file;
        builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = reader.readLine()) != null && lines < 5000) {
            builder.append(line).append("\n");
            lines++;
        }
    }

    
    public void remove(int offs, int len) throws BadLocationException {

    }

    
    public void insertString(int offset, String str) throws BadLocationException {

    }

    
    public String getText(int offset, int length) throws BadLocationException {
        return null;
    }

    
    public void getText(int offset, int length, Segment txt) throws BadLocationException {

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
