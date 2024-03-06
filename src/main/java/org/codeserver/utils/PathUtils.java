package org.codeserver.utils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PathUtils {

    public static String generatePath(TreePath node) {
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < node.getPathCount(); i++) {
            DefaultMutableTreeNode mtn = ((DefaultMutableTreeNode) node.getPath()[i]);
            String str = mtn.getUserObject().toString();
            if ((node.getPathCount() - 1) == i && mtn.getChildCount() <= 0) {
                pathBuilder.append("/").append(str);
            } else {
                pathBuilder.append("/").append(str.replace(".", "/"));
            }
        }

        String path = pathBuilder.toString();
        return path;
    }

    public static File zipFolder(File folder, String zipName) throws Exception {
        File file = new File(folder, zipName + ".zip");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        zipFile(folder, folder.getName(), zipOut);
        zipOut.close();
        return file;
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
