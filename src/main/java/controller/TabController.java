package controller;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import model.FileInfo;
import org.fxmisc.richtext.InlineCssTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class TabController {
    private static final Logger log = LoggerFactory.getLogger(TabController.class);
    private Long[] indices;
    private File file;
    private int curIndex;
    private final int markLength;
    private boolean allMarked = false;
    private InlineCssTextArea textArea;
    private Tab tab;

    TabController(TabPane tabPane, FileInfo info, int markLength) {
        Tab tab = new Tab();
        tab.setText(info.getFile().getName());
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
        this.indices = new Long[info.getIndices().size()];
        this.indices = info.getIndices().toArray(indices);
        this.file = info.getFile();
    }

    Tab getTab() {
        return tab;
    }

    void markNext() {
        unmark();
        curIndex = ++curIndex % indices.length;
        mark(curIndex);
    }

    void markPrev() {
        unmark();
        curIndex = (--curIndex+ indices.length) % indices.length;
        mark(curIndex);
    }

    void markAll() {
        unmark();
        if (!allMarked)
            for (int i = 0; i < indices.length; i++)
                mark(i);
        mark(curIndex);
        allMarked = !allMarked;
    }

    private void mark(int index) {
    }

    private void unmark() {
    }

    void setText() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null)
                textArea.appendText(line);
            textArea.appendText("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
