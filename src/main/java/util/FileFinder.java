package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class FileFinder {
    private static final Logger log = LoggerFactory.getLogger(FileFinder.class);
    private File dir;
    private String extension;
    private Queue<File> dirList;

    public FileFinder(File dir, String extension) {
        this.dir = dir;
        this.extension = extension;
        dirList = new LinkedList<>();
        dirList.add(dir);
    }

    public void find(Consumer<File> toDo) {
        log.info("Trying to find *." + extension + " files in " + dir.getAbsolutePath());
        while (!dirList.isEmpty()) {
            File curDir = dirList.poll();
            log.debug("Search files in " + curDir.getName());
            try {
                for (File f : curDir.listFiles((dir, name) -> name.matches(".*\\." + extension)))
                    toDo.accept(f);
                dirList.addAll(Arrays.asList(curDir.listFiles(File::isDirectory)));
            } catch (NullPointerException e) {
                log.warn("Exception while looking in dir " + curDir.getName() + ":" + e.getMessage(), e);
            }
        }
    }
}