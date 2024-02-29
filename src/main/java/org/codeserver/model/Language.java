package org.codeserver.model;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class Language {

    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private String srcPath;
    @Getter
    private String resourcePath;
    @Getter
    private String fileExtesion;
    @Getter
    private String imagePath;
    private String imageBase64;

    public Language(String id, String name, String srcPath, String resourcePath, String fileExtesion, String imagePath) {
        this.id = id;
        this.name = name;
        this.srcPath = srcPath;
        this.resourcePath = resourcePath;
        this.imagePath = imagePath;
        this.fileExtesion = fileExtesion;
        try {
            this.imageBase64 = imageToBase64(ImageIO.read(new File(imagePath)), imagePath.split("\\.")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String imageToBase64(Image image, String formatName) {
        try {
            BufferedImage bufferedImage = (BufferedImage) image;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, formatName, bos);
            byte[] imageBytes = bos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            System.out.println("Erro ao converter imagem para Base64: " + e.getMessage());
            return null;
        }
    }

    public Image base64ToImage() {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(bis);
            return bufferedImage;
        } catch (IOException e) {
            System.out.println("Erro ao converter Base64 para imagem: " + e.getMessage());
            return null;
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        return json;
    }

    public static Language fromSection(String langId, ConfigurationSection section) {
        return new Language(langId, section.getString("name"), section.getString("src"), section.getString("resource"), section.getString("fileExtension"), section.getString("icon"));
    }

}
