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
            JAVA_COLORIDO = new ImageIcon(resizeImage("/icons/java-colorido.png", 16, 16));
            RUST_COLORIDO = new ImageIcon(resizeImage("/icons/rust-colorido.png", 16, 16));
            ECLIPSE = new ImageIcon(resizeImage("/icons/eclipse.png", 16, 16));
            FILE = new ImageIcon(resizeImage("/icons/file.png", 16, 16));
            FILE = new ImageIcon(resizeImage("/icons/file.png", 16, 16));
            FOLDER = new ImageIcon(resizeImage("/icons/folder.png", 16, 16));
            OPEN_FOLDER = new ImageIcon(resizeImage("/icons/open-folder.png", 16, 16));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage resizeImage(String classpath, int targetWidth, int targetHeight) throws IOException {
        Image resultingImage = ImageIO.read(Objects.requireNonNull(Icons.class.getResourceAsStream(classpath)))
                .getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.setBackground(new Color(0,0,0,0));
        g2.setColor(new Color(0,0,0,0));
        g2.drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public static BufferedImage resizeImage(Image image, int targetWidth, int targetHeight) throws IOException {
        Image resultingImage = image
                .getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.setBackground(new Color(0,0,0,0));
        g2.setColor(new Color(0,0,0,0));
        g2.drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }
}
