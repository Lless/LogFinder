package util;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TreeItem;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TreeItemComparatorTest {
    private TreeItem<String> item1;
    private TreeItem<String> item2;

    @BeforeClass
    public static void InitializeJavaFX(){
        new JFXPanel();
    }

    @Before
    public void initItems(){
        item1 = new TreeItem<>("c");
        item2 = new TreeItem<>("a");
    }

    @Test
    public void ItemWithChildIsFirst(){
        TreeItem<String> child = new TreeItem<>("b");
        item1.getChildren().add(child);

        int result = new TreeItemComparator().compare(item1,item2);

        assertTrue(result < 0);
    }

    @Test
    public void CompareNamesForLeafs(){
        int result = new TreeItemComparator().compare(item1,item2);

        assertTrue(result > 0);
    }

    @Test
    public void CompareNamesForBranches(){
        TreeItem<String> child1 = new TreeItem<>("b");
        item1.getChildren().add(child1);
        TreeItem<String> child2 = new TreeItem<>("b");
        item2.getChildren().add(child2);

        int result = new TreeItemComparator().compare(item1,item2);

        assertTrue(result > 0);
    }
}