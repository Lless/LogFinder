package controller;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import model.SearchManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class OutputController {
    private static final Logger log = LoggerFactory.getLogger(OutputController.class);

    private Button next;
    private Button prev;
    private Button all;

    private TabPane tabPane;
    private Map<Tab, TabController> controllers = new HashMap<>();
    private Map<File, Tab> tabs = new HashMap<>();

    OutputController(TabPane tabPane, Button next, Button prev, Button all) {
        this.tabPane = tabPane;
        this.next = next;
        this.prev = prev;
        this.all = all;
        next.setOnAction((event)->showNext());
        prev.setOnAction((event)->showPrev());
        all.setOnAction((event)->showAll());
    }

    private void showNext() {
        log.info("bthNext pressed");
        Tab selected = tabPane.getSelectionModel().getSelectedItem();
        controllers.get(selected).markNext();
    }

    private void showPrev() {
        log.info("btnPrev pressed");
        Tab selected = tabPane.getSelectionModel().getSelectedItem();
        controllers.get(selected).markPrev();
    }

    private void showAll() {
        log.info("btnAll pressed");
        Tab selected = tabPane.getSelectionModel().getSelectedItem();
        controllers.get(selected).markAll();
    }

    private void disableButtons(boolean flag) {
        log.debug("Buttons " + (flag ? "disabled" : "enabled"));
        next.disableProperty().setValue(flag);
        prev.disableProperty().setValue(flag);
        all.disableProperty().setValue(flag);
    }

    void showText(File file) {
        if (file == null) return;
        log.info("File selected: " + file.getAbsolutePath());
        if (tabs.containsKey(file)) {
            tabPane.getSelectionModel().select(tabs.get(file));
            return;
        }
        SearchManager manager = SearchManager.getManager();
        int patLen = manager.getInput().getPattern().length();
        TabController controller = new TabController(tabPane, file, manager.getEntries(file), patLen , this::removeTabInfo);
        controllers.put(controller.getTab(), controller);
        tabs.put(file, controller.getTab());
        controller.setText();
        disableButtons(false);
    }

    private void removeTabInfo(Tab tab, File file){
        controllers.remove(tab);
        tabs.remove(file, tab);
        if (tabPane.getTabs().isEmpty())
            disableButtons(true);
    }
    void removeInactiveTabsInfo(){
        tabs.clear();
    }
}
