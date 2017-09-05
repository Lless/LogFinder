package controller;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TabControllerTest {
    private TabController controller;
    private InlineCssTextArea area;

    private String text = "abaae";

    @BeforeClass
    public static void initializeJavaFX() {
        new JFXPanel();
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void initializeController() throws IOException, InterruptedException {
        File file = folder.newFile("fileName");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file))) {
            writer.write(text);
        }

        TabPane pane = new TabPane();
        BiConsumer<Tab, File> consumer = (t, f) -> {
        };
        controller = new TabController(pane, file, new Integer[]{0, 2, 3}, 1, consumer);
        controller.setText();
        Thread.sleep(200);

        area = (InlineCssTextArea) pane.getTabs().get(0).getContent();
    }

    @Test
    public void textDisplaying() {
        assertEquals(text, area.getText());
    }

    @Test
    public void markingNext() {
        controller.markNext();
        assertNotEquals("", area.getStyleOfChar(2));
    }

    @Test
    public void markingPrev() {
        controller.markPrev();
        assertNotEquals("", area.getStyleOfChar(3));
    }

    @Test
    public void markingNextAndPrev() {
        controller.markNext();
        controller.markPrev();
        assertEquals("", area.getStyleOfChar(2));
        assertNotEquals("", area.getStyleOfChar(0));
    }

    @Test
    public void allMarking() {
        controller.markAll();

        assertNotEquals("", area.getStyleOfChar(1));
        assertNotEquals("", area.getStyleOfChar(3));
    }

    @Test
    public void allUnMarking() {
        controller.markAll();
        controller.markAll();

        assertEquals("", area.getStyleOfChar(1));
        assertEquals("", area.getStyleOfChar(3));
    }

    @Test
    public void allUnMarkingIfMarkingNext() {
        controller.markAll();
        controller.markNext();

        assertEquals("", area.getStyleOfChar(1));
        assertEquals("", area.getStyleOfChar(3));
        assertNotEquals("", area.getStyleOfChar(2));
    }
}