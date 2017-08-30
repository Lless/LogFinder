package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import model.FileInfo;
import model.FileManager;
import model.TreeItemComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ApplicationController {
    @FXML
    private TextField extention;

    @FXML
    private TextField pattern;

    @FXML
    private TextField filepath;

    @FXML
    private TextArea text;

    @FXML
    private TreeView<String> fileTree;

    @FXML
    private Button next;

    @FXML
    private Button prev;

    @FXML
    private Button selectAll;

    private File directory;
    private Map<File, TreeItem<String>> folders = new HashMap<>();
    private Map<TreeItem<String>, FileInfo> files = new HashMap<>();
    private TreeItemComparator comparator = new TreeItemComparator();
    @FXML
    private void initialize() {
        fileTree.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldvalue, newvalue) -> this.setText(newvalue));
        Platform.runLater(() -> filepath.requestFocus());
        /*text.textProperty().addListener(
                (observable, oldvalue, newvalue) -> text.setScrollTop(Double.MAX_VALUE));*/
    }

    private void addFileToTree(FileInfo f) {
        File parent = f.getFile().getParentFile();
        TreeItem<String> newNode = new TreeItem<>(f.getFile().getName());
        TreeItem<String> parentNode = folders.containsKey(parent) ?
                folders.get(parent) : addFolderToTree(parent);
        parentNode.getChildren().add(newNode);
        parentNode.getChildren().sort(comparator);
        files.put(newNode, f);

    }

    private TreeItem<String> addFolderToTree(File f) {
        File parent = f.getParentFile();
        TreeItem<String> newNode = new TreeItem<>(f.getName());
        TreeItem<String> parentNode = folders.containsKey(parent) ?
                folders.get(parent) : addFolderToTree(parent);
        parentNode.getChildren().add(newNode);
        parentNode.getChildren().sort(comparator);
        folders.put(f, newNode);
        return newNode;
    }

    private void consumeFileInfo(FileInfo info) {
        Platform.runLater(() -> addFileToTree(info));
    }

    private void setText(TreeItem<String> item) {
        text.clear();
        if (files.containsKey(item)) {
            FileInfo info = files.get(item);
            text.appendText("choosed file " + info.getFile().getName() + "\n");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(info.getFile()));
                String line;
                while ((line = reader.readLine()) != null)
                    text.appendText(line);
                text.appendText("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Long l : info.getIndices())
                text.appendText(l.toString() + " ");
        }
    }

    private void startSearch() {
        if (!files.isEmpty()) files.clear();
        if (!folders.isEmpty()) folders.clear();
        fileTree.setRoot(new TreeItem<>(directory.getName()));
        folders.put(directory, fileTree.getRoot());
        FileManager.getResults(extention.getText(), pattern.getText(), directory, this::consumeFileInfo);
    }

    @FXML
    private void showNext() {
        text.appendText("showNextPressed\n");
    }

    @FXML
    private void showPrev() {
        text.appendText("showPrevPressed\n");
    }

    @FXML
    private void selectAll() {
        text.appendText("SelectAllPressed\n");
    }

    //todo: test
    @FXML
    private void checkFields() {
        if (extention.getText().isEmpty()) {
            extention.requestFocus();
            return;
        }
        if ((directory == null) ||
                (!directory.getName().equals(filepath.getText()))) {
            if (filepath.getText().isEmpty()) {
                filepath.requestFocus();
                openFolder();
                return;
            }
            File newDir = new File(filepath.getText());
            if (!newDir.exists() || !(newDir.isDirectory())) {
                filepath.setText("");
                filepath.requestFocus();
                openFolder();
                return;
            }
            directory = newDir;
        }
        if (pattern.getText().isEmpty()) {
            pattern.requestFocus();
            return;
        }
        startSearch();
    }

    @FXML
    private void openFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose file directory");
        if (directory != null) chooser.setInitialDirectory(directory);
        directory = chooser.showDialog(null);
        filepath.setText(directory.getAbsolutePath());
    }

}
