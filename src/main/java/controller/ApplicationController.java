package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import model.SearchManager;
import model.Input;
import model.WrongInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.lang.Thread.MAX_PRIORITY;

public class ApplicationController {
    @FXML
    private TextField extension;
    @FXML
    private TextField pattern;
    @FXML
    private TextField filepath;

    @FXML
    private TreeView<String> fileTree;

    @FXML
    private TabPane tabPane;
    @FXML
    private Button next;
    @FXML
    private Button prev;
    @FXML
    private Button all;

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private OutputController outputController;
    private TreeController treeController;

    @FXML
    private void initialize() {
        Thread.currentThread().setPriority(MAX_PRIORITY);
        outputController = new OutputController(tabPane, next, prev, all);
        fileTree.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldvalue, newvalue) -> outputController.showText(treeController.getFile(newvalue)));
        Platform.runLater(() -> filepath.requestFocus());
    }

    @FXML
    private void openFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose file folder");
        File currentFolder = new File(filepath.getText());
        if (currentFolder.exists()) chooser.setInitialDirectory(currentFolder);
        log.info("Choose folder dialog displayed");
        File newFolder = chooser.showDialog(null);
        if (newFolder != null) filepath.setText(newFolder.toString());
        start();
    }

    @FXML
    private void start() {
        log.info("Checking arguments");
        Input input = new Input();
        try {
            input.setExtension(extension);
            input.setFolder(filepath);
            input.setPattern(pattern);
        } catch (WrongInputException e) {
            log.info(e.getMessage());
            e.getControl().requestFocus();
            if (e.getControl() == filepath) openFolder();
            return;
        }
        SearchManager manager = SearchManager.getManager();
        if (manager != null) {
            if (input.equals(manager.getInput()))
                return;
            if (!input.getPattern().equals(manager.getInput().getPattern()))
                outputController.removeInactiveTabsInfo();
        }
        treeController = new TreeController(fileTree, input.getFolder());
        SearchManager.startSearch(input, this::addFile);
    }

    private void addFile(File f) {
        Platform.runLater(
                () -> treeController.addFile(f)
        );
    }
}
