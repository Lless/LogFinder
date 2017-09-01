package controller;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class TabController {
    private static final Logger log = LoggerFactory.getLogger(TabController.class);
    private Integer[] entries;
    private File file;
    private int curIndex;
    private final int markLength;
    private boolean allMarked = false;
    private InlineCssTextArea textArea;
    private Tab tab;

    TabController(TabPane tabPane, File file, Integer[] entries, int markLength) {
        Tab tab = new Tab();
        tab.setText(file.getName());
        InlineCssTextArea text = new InlineCssTextArea();
        text.editableProperty().setValue(false);
        text.wrapTextProperty().setValue(true);
        tab.setContent(text);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        curIndex = 0;
        textArea = text;
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
        unmark();
        if (!allMarked)
            for (int i = 0; i < entries.length; i++)
                mark(i);
        mark(curIndex);
        allMarked = !allMarked;
    }

    private void mark(int index) {
        textArea.positionCaret(entries[index]);
        setStyle(entries[index], "-fx-background-fill: lightblue;");
    }

    private void unmark() {
        if (allMarked) {
            for (Integer from : entries)
                setStyle(from, "");
        }
        setStyle(entries[curIndex], "");
    }

    private void setStyle(int from, String style) {
        textArea.setStyle(from, from + markLength, style);
    }

    void setText() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                textArea.appendText(line);
                textArea.appendText("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
