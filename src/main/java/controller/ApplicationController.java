package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import model.FileInfo;
import model.FileManager;
import model.TreeItemComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ApplicationController {
    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);
    @FXML
    private TextField extention;

    @FXML
    private TextField pattern;

    @FXML
    private TextField filepath;

    @FXML
    private TabPane tabPane;

    @FXML
    private TreeView<String> fileTree;

    @FXML
    private Button next;

    @FXML
    private Button prev;

    @FXML
    private Button selectAll;

    private int currentPatternLength;
    private File folder;
    private Map<File, TreeItem<String>> folders = new HashMap<>();
    private Map<TreeItem<String>, FileInfo> files = new HashMap<>();
    private Map<Tab, TabController> tabMap = new HashMap<>();
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

    private void disableButtons(boolean flag) {
        log.debug("Buttons " + (flag ? "disabled" : "enabled"));
        next.disableProperty().setValue(flag);
        prev.disableProperty().setValue(flag);
        selectAll.disableProperty().setValue(flag);
    }

    private void setText(TreeItem<String> item) {
        if (files.containsKey(item)) {
            disableButtons(false);
            log.info("File selected: " + files.get(item).getFile().getAbsolutePath());
            TabController controller = new TabController(tabPane, files.get(item), currentPatternLength);
            tabMap.put(controller.getTab(), controller);
            controller.setText();
        }
    }

    private void startSearch() {
        if (!files.isEmpty()) files.clear();
        if (!folders.isEmpty()) folders.clear();
        currentPatternLength = pattern.getText().length();
        fileTree.setRoot(new TreeItem<>(folder.getName()));
        folders.put(folder, fileTree.getRoot());
        FileManager.getResults(extention.getText(), pattern.getText(), folder, this::consumeFileInfo);
    }

    @FXML
    private void showNext() {
        tabMap.get(tabPane.getSelectionModel().getSelectedItem()).markNext();
        log.info("bthNext pressed");
    }

    @FXML
    private void showPrev() {
        tabMap.get(tabPane.getSelectionModel().getSelectedItem()).markPrev();
        log.info("btnPrev pressed");
    }

    @FXML
    private void selectAll() {
        tabMap.get(tabPane.getSelectionModel().getSelectedItem()).markAll();
        log.info("btnSelectAll pressed");
    }

    @FXML
    private void checkFields() {
        log.info("btnStart pressed");
        if (extention.getText().isEmpty()) {
            extention.requestFocus();
            log.debug("Extention is empty");
            return;
        }
        if ((folder == null) ||
                (!folder.getName().equals(filepath.getText()))) {
            if (filepath.getText().isEmpty()) {
                filepath.requestFocus();
                log.debug("File not selected");
                openFolder();
                return;
            }
            File newDir = new File(filepath.getText());
            if (!newDir.exists() || !(newDir.isDirectory())) {
                log.info("Selected incorrect file: " + filepath.getText());
                filepath.setText("");
                filepath.requestFocus();
                openFolder();
                return;
            }
            folder = newDir;
        }
        if (pattern.getText().isEmpty()) {
            log.debug("Pattern is empty");
            pattern.requestFocus();
            return;
        }
        log.info("Trying to start search");
        startSearch();
    }

    @FXML
    private void openFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose file folder");
        if (folder != null) chooser.setInitialDirectory(folder);
        log.info("Choose folder dialog displayed");
        File newFolder = chooser.showDialog(null);
        if (newFolder == null) {
            log.info("Folder not selected");
            return;
        }
        folder = newFolder;
        filepath.setText(folder.getAbsolutePath());
        log.info("Selected folder" + folder.getAbsolutePath());
    }
}
