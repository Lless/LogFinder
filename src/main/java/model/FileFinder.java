package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

class FileFinder {
    private static final Logger log = LoggerFactory.getLogger(FileFinder.class);
    private File dir;
    private String extention;
    private List<File> files;
    private Queue<File> dirList;

    FileFinder(File dir, String extention) {
        this.dir = dir;
        this.extention = extention;
        dirList = new LinkedList<>();
        dirList.add(dir);
    }

    void find(Consumer<File> toDo) {
        log.info("Trying to find *." + extention + " files in " + dir.getAbsolutePath());
        while (!dirList.isEmpty()) {
            File curDir = dirList.poll();
            log.debug("Search files in " + curDir.getName());
            try {
                for (File f : curDir.listFiles((dir, name) -> name.matches(".*\\." + extention)))
                    toDo.accept(f);
                dirList.addAll(Arrays.asList(curDir.listFiles(File::isDirectory)));
            } catch (NullPointerException e) {
                log.error("problem while looking in dir " + curDir.getName(), e);
            }
        }
    }

    /*List<File> find() {
        log.info("Trying to find *." + extention + " files in " + dir.getAbsolutePath());
        if (files == null)
            try {
                files = Files.walk(dir.toPath())
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(f -> f.getName().matches(".*\\." + extention))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                log.error("Error in file finding", e);
            }
        return files;
    }*/
}