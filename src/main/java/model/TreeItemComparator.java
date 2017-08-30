package model;

import javafx.scene.control.TreeItem;

import java.util.Comparator;

public class TreeItemComparator implements Comparator<TreeItem<String>> {

    @Override
    public int compare(TreeItem<String> o1, TreeItem<String> o2) {
        boolean o1isFile = o1.getChildren().isEmpty();
        boolean o2isFile = o2.getChildren().isEmpty();
        if (o1isFile && !o2isFile) return 1;
        if (!o1isFile && o2isFile) return -1;
        return o1.getValue().compareTo(o2.getValue());
    }
}
