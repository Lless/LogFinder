package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiConsumer;

class TabController {
    private static final Logger log = LoggerFactory.getLogger(TabController.class);
    private Integer[] entries;
    private File file;
    private int curIndex;
    private final int markLength;
    private boolean allMarked = false;
    private InlineCssTextArea textArea;
    private Tab tab;

    TabController(TabPane tabPane, File file, Integer[] entries, int markLength, BiConsumer<Tab, File> onclose) {
        Tab tab = new Tab();
        tab.setText(file.getName());
        tab.setOnClosed((event) -> onclose.accept(tab, file));
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        curIndex = 0;
        this.markLength = markLength;
        this.tab = tab;
        this.entries = entries;
        this.file = file;
    }

    Tab getTab() {
        return tab;
    }

    void markNext() {
        unmark();
        curIndex = ++curIndex % entries.length;
        mark(curIndex);
    }

    void markPrev() {
        unmark();
        curIndex = (--curIndex + entries.length) % entries.length;
        mark(curIndex);
    }

    void markAll() {
        textArea.setStyle(0, textArea.getText().length(),
                allMarked ? "" : "-fx-background-fill: lightblue;");
        allMarked = !allMarked;
    }

    private void mark(int index) {
        textArea.positionCaret(entries[index]);
        setStyle(entries[index], "-fx-background-fill: lightblue;");
    }

    private void unmark() {
        if (allMarked) markAll();
        setStyle(entries[curIndex], "");
    }

    private void setStyle(int from, String style) {
        textArea.setStyle(from, from + markLength, style);
    }

    void setText() {
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(fileReader)) {
                    char[] buf = new char[10 * 1024 * 1024];
                    int haveRead;
                    StringBuilder sb = new StringBuilder();
                    while ((haveRead = reader.read(buf)) != -1) {
                        if (haveRead != buf.length)
                            buf = Arrays.copyOf(buf, haveRead);
                        sb.append(buf);
                    }
                    textArea = new InlineCssTextArea();
                    textArea.appendText(sb.toString());
                    textArea.editableProperty().setValue(false);
                    textArea.wrapTextProperty().setValue(true);
                    Platform.runLater(()->tab.setContent(textArea));
                    log.info("Displaying text completed");
                    return sb.toString();
                } catch (IOException e) {
                    log.error("Error while reading file", e);
                    return null;
                }
            }
        };
        Thread th = new Thread(task);
        th.start();
    }
}
