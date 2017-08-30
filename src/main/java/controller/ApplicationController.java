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
    private TabPane pane;

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

    private void consumeFileInfo(FileInfo info) {
        Platform.runLater(() -> addFileToTree(info));
    }

    private TextArea addNewTab(String name) {
        Tab tab = new Tab();
        tab.setText(name);
        TextArea text = new TextArea();
        text.editableProperty().setValue(false);
        text.wrapTextProperty().setValue(true);
        tab.setContent(text);
        pane.getTabs().add(tab);
        pane.getSelectionModel().select(tab);
        return text;
    }

    private void enableButtons(boolean flag){
        next.disableProperty().setValue(flag);
        prev.disableProperty().setValue(flag);
        selectAll.disableProperty().setValue(flag);
    }


    private void setText(TreeItem<String> item) {
        if (files.containsKey(item)) {
            enableButtons(true);
            FileInfo info = files.get(item);
            TextArea text = addNewTab(info.getFile().getName());
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
    private void showNext() { }

    @FXML
    private void showPrev() { }

    @FXML
    private void selectAll() { }

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
