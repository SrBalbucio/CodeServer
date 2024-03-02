package org.codeserver.utils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class PathUtils {

    public static String generatePath(TreePath node){
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < node.getPathCount(); i++) {
            DefaultMutableTreeNode mtn = ((DefaultMutableTreeNode) node.getPath()[i]);
            String str = mtn.getUserObject().toString();
            if ((node.getPathCount() - 1) == i && mtn.getChildCount() <= 0) {
                pathBuilder.append("/").append(str);
            } else{
                pathBuilder.append("/").append(str.replace(".", "/"));
            }
        }

        String path = pathBuilder.toString();
        return path;
    }
}
