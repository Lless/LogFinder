package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import model.FileManager;
import model.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ApplicationController {
    @FXML
    private TextField extension;

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

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private Map<Tab, TabController> tabControllersMap = new HashMap<>();
    private Map<File, Tab> tabFinder = new HashMap<>();
    private TreeController treeController;

    @FXML
    private void initialize() {
        fileTree.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldvalue, newvalue) -> showText(newvalue));
        Platform.runLater(() -> filepath.requestFocus());
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

    @FXML
    private void openFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose file folder");
        File currentFolder = new File(filepath.getText());
        if (currentFolder.exists()) chooser.setInitialDirectory(currentFolder);
        log.info("Choose folder dialog displayed");
        File newFolder = chooser.showDialog(null);
        if (newFolder != null) filepath.setText(newFolder.toString());
    }

    @FXML
    private void start() {
        log.info("btnStart pressed");
        try {
            Input input = new Input();
            input.setExtension(extension);
            input.setFolder(filepath);
            input.setPattern(pattern);
            treeController = new TreeController(fileTree, input.getFolder());
            if ((FileManager.getInput() != null) && (input.getPattern().equals(FileManager.getInput().getPattern())))
                tabFinder.clear();
            log.info("Trying to start search");
            FileManager.startSearch(input, this::addFile);
        } catch (WrongInputException e) {
            log.info(e.getMessage());
            e.getControl().requestFocus();
        }
    }

    private void addFile(File f) {
        Platform.runLater(
                () -> treeController.addFile(f)
        );
    }

    private void disableButtons(boolean flag) {
        log.debug("Buttons " + (flag ? "disabled" : "enabled"));
        next.disableProperty().setValue(flag);
        prev.disableProperty().setValue(flag);
        selectAll.disableProperty().setValue(flag);
    }

    private void onCloseTab(Tab tab, File file) {
        tabControllersMap.remove(tab);
        tabFinder.remove(file, tab);
        if (tabPane.getTabs().isEmpty())
            disableButtons(true);
    }

    private void showText(TreeItem<String> item) {
        disableButtons(false);
        File file = treeController.getFile(item);
        if (file == null) return;
        log.info("File selected: " + file.getAbsolutePath());
        if (tabFinder.containsKey(file)) {
            Tab tab = tabFinder.get(file);
            tabPane.getSelectionModel().select(tab);
            return;
        }
        TabController controller = new TabController(tabPane, file, FileManager.getEntries(file), FileManager.getInput().getPattern().length(), this::onCloseTab);
        tabControllersMap.put(controller.getTab(), controller);
        tabFinder.put(file, controller.getTab());
        controller.setText();
    }
}
