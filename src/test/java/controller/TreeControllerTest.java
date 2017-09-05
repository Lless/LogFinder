package controller;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TreeControllerTest {
    private TreeView<String> view;
    private TreeController controller;

    @BeforeClass
    public static void InitializeJavaFX(){
        new JFXPanel();
    }
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() throws IOException{
        view = new TreeView<>();
        controller = new TreeController(view,folder.getRoot());
    }

    @Test
    public void DisplaysFileInRoot() throws IOException{
        File file = folder.newFile("someFile");

        controller.addFile(file);

        String result = view.getRoot().getChildren().get(0).getValue();
        assertEquals(file.getName(),result);
    }

    @Test
    public void DisplaysFileInFolder() throws IOException{
        File subFolder = folder.newFolder("someFolder");
        File file = new File (subFolder,"someFile");

        controller.addFile(file);

        TreeItem<String> branch = view.getRoot().getChildren().get(0);
        String leafName = branch.getChildren().get(0).getValue();
        String branchName = branch.getValue();

        assertEquals(file.getName(),leafName);
        assertEquals(subFolder.getName(),branchName);
    }

    @Test
    public void DisplaysWithComparing() throws IOException{
        File a = folder.newFile("a");
        File b = folder.newFile("b");

        controller.addFile(b);
        controller.addFile(a);

        String first = view.getRoot().getChildren().get(0).getValue();
        String second = view.getRoot().getChildren().get(1).getValue();

        assertEquals(a.getName(),first);
        assertEquals(b.getName(),second);
    }

    @Test
    public void GetsFile() throws IOException{
        File file = folder.newFile("someFile");

        controller.addFile(file);
        TreeItem<String> item = view.getRoot().getChildren().get(0);

        File result = controller.getFile(item);

        assertEquals(file,result);
    }
}