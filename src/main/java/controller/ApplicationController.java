package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import model.FileManager;
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

    private File folder;
    private Map<Tab, TabController> tabControllersMap = new HashMap<>();
    private Map<File, Tab> tabFinder = new HashMap<>();
    private TreeController treeController;

    @FXML
    private void initialize() {
        fileTree.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldvalue, newvalue) -> setText(newvalue));
        Platform.runLater(() -> filepath.requestFocus());
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldvalue, newvalue) -> {
            if (tabPane.getTabs().isEmpty())
                disableButtons(true);
        });
    }


    private void disableButtons(boolean flag) {
        log.debug("Buttons " + (flag ? "disabled" : "enabled"));
        next.disableProperty().setValue(flag);
        prev.disableProperty().setValue(flag);
        selectAll.disableProperty().setValue(flag);
    }

    private void setText(TreeItem<String> item) {
        disableButtons(false);
        File file = treeController.getFile(item);
        log.info("File selected: " + file.getAbsolutePath());
        if (tabFinder.containsKey(file)) {
            Tab tab = tabFinder.get(file);
            tabPane.getSelectionModel().select(tab);
            return;
        }
        TabController controller = new TabController(tabPane, file, FileManager.getEntries(file), FileManager.getPattern().length());
        tabControllersMap.put(controller.getTab(), controller);
        tabFinder.put(file, controller.getTab());
        controller.setText();

    }

    @FXML
    private void showNext() {
        Tab selected = tabPane.getSelectionModel().getSelectedItem();
        tabControllersMap.get(selected).markNext();
        log.info("bthNext pressed");
    }

    @FXML
    private void showPrev() {
        Tab selected = tabPane.getSelectionModel().getSelectedItem();
        tabControllersMap.get(selected).markPrev();
        log.info("btnPrev pressed");
    }

    @FXML
    private void selectAll() {
        Tab selected = tabPane.getSelectionModel().getSelectedItem();
        tabControllersMap.get(selected).markAll();
        log.info("btnSelectAll pressed");
    }

    private void addFile(File f) {
        Platform.runLater(
                () -> treeController.addFile(f)
        );
    }

    @FXML
    private void start() {
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
                log.debug("Folder not selected");
                openFolder();
                return;
            }
            File newDir = new File(filepath.getText());
            if (!newDir.exists() || !(newDir.isDirectory())) {
                log.info("Selected incorrect folder: " + filepath.getText());
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
        treeController = new TreeController(fileTree, folder);
        log.info("Trying to start search");
        FileManager.getResults(extention.getText(), pattern.getText(), folder, this::addFile);
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
        log.info("Selected folder " + folder.getAbsolutePath());
    }
}
