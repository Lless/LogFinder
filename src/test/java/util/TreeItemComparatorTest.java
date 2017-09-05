package util;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TreeItem;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TreeItemComparatorTest {
    private TreeItem<String> itemB;
    private TreeItem<String> itemA;

    @BeforeClass
    public static void InitializeJavaFX(){
        new JFXPanel();
    }

    @Before
    public void initItems(){
        itemB = new TreeItem<>("b");
        itemA = new TreeItem<>("a");
    }

    @Test
    public void ItemWithChildIsFirst(){
        TreeItem<String> child = new TreeItem<>("c");
        itemB.getChildren().add(child);

        int result = new TreeItemComparator().compare(itemB, itemA);

        assertTrue(result < 0);
    }

    @Test
    public void CompareNamesForLeafs(){
        int result = new TreeItemComparator().compare(itemB, itemA);

        assertTrue(result > 0);
    }

    @Test
    public void CompareNamesForBranches(){
        TreeItem<String> child = new TreeItem<>("c");
        itemB.getChildren().add(child);
        itemA.getChildren().add(child);

        int result = new TreeItemComparator().compare(itemB, itemA);

        assertTrue(result > 0);
    }
}