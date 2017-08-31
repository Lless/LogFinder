package controller;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.FileInfo;
import model.TreeItemComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TreeController {
    private static final Logger log = LoggerFactory.getLogger(TreeController.class);

    private Map<File, TreeItem<String>> folders = new HashMap<>();
    private Map<TreeItem<String>, FileInfo> files = new HashMap<>();
    private TreeItemComparator comparator = new TreeItemComparator();

    TreeController(TreeView<String> treeView, File mainFolder) {
        treeView.setRoot(new TreeItem<>(mainFolder.getName()));
        folders.put(mainFolder, treeView.getRoot());
    }

    private void addFileToTree(FileInfo f) {
        File parent = f.getFile().getParentFile();
        TreeItem<String> newNode = new TreeItem<>(f.getFile().getName());
        TreeItem<String> parentNode;
        if (folders.containsKey(parent)) {
            parentNode = folders.get(parent);
            parentNode.getChildren().add(newNode);
        } else parentNode = addFolderToTree(parent, newNode);
        parentNode.getChildren().sort(comparator);
        files.put(newNode, f);

    }

    private TreeItem<String> addFolderToTree(File f, TreeItem<String> nextNode) {
        File parent = f.getParentFile();
        TreeItem<String> newNode = new TreeItem<>(f.getName());
        newNode.getChildren().add(nextNode);
        TreeItem<String> parentNode;
        if (folders.containsKey(parent)) {
            parentNode = folders.get(parent);
            parentNode.getChildren().add(newNode);
        } else parentNode = addFolderToTree(parent, newNode);
        parentNode.getChildren().sort(comparator);
        folders.put(f, newNode);
        return newNode;
    }

    void consumeFileInfo(FileInfo info) {
        Platform.runLater(() -> addFileToTree(info));
    }

    FileInfo getFileInfo(TreeItem<String> item) {
        return files.get(item);
    }
}
