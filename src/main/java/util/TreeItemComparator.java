package util;

import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class TreeItemComparator implements Comparator<TreeItem<String>> {
    private static final Logger log = LoggerFactory.getLogger(TreeItemComparator.class);

    @Override
    public int compare(TreeItem<String> o1, TreeItem<String> o2) {
        boolean o1isFile = o1.getChildren().isEmpty();
        boolean o2isFile = o2.getChildren().isEmpty();
        log.debug("Comparing " + o1.getValue() + " vs " + o2.getValue());
        if (o1isFile && !o2isFile) return 1;
        if (!o1isFile && o2isFile) return -1;
        return o1.getValue().compareTo(o2.getValue());
    }
}
