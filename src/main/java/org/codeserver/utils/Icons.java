package org.codeserver.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Icons {

    public static ImageIcon SOURCE_FOLDER;
    public static ImageIcon SOURCE_FOLDER_WHITE;
    public static ImageIcon GIT;
    public static ImageIcon JETBRAINS;
    public static ImageIcon JETBRAINS_WHITE;
    public static ImageIcon MAVEN_LOGO;
    public static ImageIcon MAVEN;
    public static ImageIcon JSON;
    public static ImageIcon TXT;
    public static ImageIcon XML;
    public static ImageIcon JAVA_COLORIDO;
    public static ImageIcon RUST_COLORIDO;
    public static ImageIcon ECLIPSE;
    public static ImageIcon FILE;
    public static ImageIcon FOLDER;
    public static ImageIcon OPEN_FOLDER;
    public static ImageIcon CARGO_RUST;
    public static ImageIcon IMAGE;
    public static ImageIcon CMD;
    public static ImageIcon PING;
    public static ImageIcon MARKDOWN_ICON;
    public static ImageIcon FOLDER_EXTENSION;

    static {
        try {
            SOURCE_FOLDER = new ImageIcon(resizeImage("/icons/source-folder.png", 16, 16));
            SOURCE_FOLDER_WHITE = new ImageIcon(resizeImage("/icons/source-folder-white.png", 16, 16));
            GIT = new ImageIcon(resizeImage("/icons/git.png", 16, 16));
            JETBRAINS = new ImageIcon(resizeImage("/icons/jetbrains.png", 16, 16));
            JETBRAINS_WHITE = new ImageIcon(resizeImage("/icons/jetbrains-white.png", 16, 16));
            MAVEN_LOGO = new ImageIcon(ImageIO.read(Objects.requireNonNull(Icons.class.getResourceAsStream("/icons/maven-logo.png"))));
            MAVEN = new ImageIcon(resizeImage("/icons/maven-representation.png", 16, 16));
            JSON = new ImageIcon(resizeImage("/icons/json.png", 16, 16));
            TXT = new ImageIcon(resizeImage("/icons/txt.png", 16, 16));
            XML = new ImageIcon(resizeImage("/icons/xml.png", 16, 16));
            JAVA_COLORIDO = new ImageIcon(resizeImage("/icons/java-colorido.png", 18, 18));
            RUST_COLORIDO = new ImageIcon(resizeImage("/icons/rust-colorido.png", 18, 18));
            ECLIPSE = new ImageIcon(resizeImage("/icons/eclipse.png", 16, 16));
            FILE = new ImageIcon(resizeImage("/icons/file.png", 16, 16));
            CMD = new ImageIcon(resizeImage("/icons/cmd.png", 16, 16));
            FOLDER = new ImageIcon(resizeImage("/icons/folder.png", 16, 16));
            OPEN_FOLDER = new ImageIcon(resizeImage("/icons/open-folder.png", 16, 16));
            CARGO_RUST = new ImageIcon(resizeImage("/icons/cargo-rust.png", 16, 16));
            IMAGE = new ImageIcon(resizeImage("/icons/image-2.png", 16, 16));
            PING = new ImageIcon(resizeImage("/icons/ping.png", 16, 16));
            MARKDOWN_ICON = new ImageIcon(resizeImage("/icons/markdown.png", 16, 16));
            FOLDER_EXTENSION = new ImageIcon(resizeImage("/icons/folder-extension.png", 16, 16));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage resizeImage(String classpath, int targetWidth, int targetHeight) throws IOException {
        return resizeImage(ImageIO.read(Objects.requireNonNull(Icons.class.getResourceAsStream(classpath))), targetWidth, targetHeight);
    }

    public static BufferedImage resizeImage(Image image, int targetWidth, int targetHeight) throws IOException {
        Image resultingImage = image
                .getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setBackground(new Color(0,0,0,0));
        g2.setColor(new Color(0,0,0,0));
        g2.drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }
}
