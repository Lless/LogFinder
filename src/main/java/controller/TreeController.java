package controller;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import util.TreeItemComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class TreeController {
    private static final Logger log = LoggerFactory.getLogger(TreeController.class);

    private Map<File, TreeItem<String>> folders = new HashMap<>();
    private Map<TreeItem<String>, File> files = new HashMap<>();
    private TreeItemComparator comparator = new TreeItemComparator();

    TreeController(TreeView<String> treeView, File mainFolder) {
        treeView.setRoot(new TreeItem<>(mainFolder.getName()));
        folders.put(mainFolder, treeView.getRoot());
    }

    void addFile(File f) {
        File parent = f.getParentFile();
        TreeItem<String> newNode = new TreeItem<>(f.getName());
        TreeItem<String> parentNode;
        if (folders.containsKey(parent)) {
            parentNode = folders.get(parent);
            parentNode.getChildren().add(newNode);
        } else parentNode = addFolder(parent, newNode);
        parentNode.getChildren().sort(comparator);
        files.put(newNode, f);
        log.debug("Added file "+f.getName());
    }

    private TreeItem<String> addFolder(File f, TreeItem<String> nextNode) {
        File parent = f.getParentFile();
        TreeItem<String> newNode = new TreeItem<>(f.getName());
        newNode.getChildren().add(nextNode);
        TreeItem<String> parentNode;
        if (folders.containsKey(parent)) {
            parentNode = folders.get(parent);
            parentNode.getChildren().add(newNode);
        } else parentNode = addFolder(parent, newNode);
        parentNode.getChildren().sort(comparator);
        folders.put(f, newNode);
        log.debug("Added folder "+f.getName());
        return newNode;
    }

    File getFile(TreeItem<String> item) {
        return files.get(item);
    }
}
