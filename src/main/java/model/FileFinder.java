package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

class FileFinder {
    private static final Logger log = LoggerFactory.getLogger(FileFinder.class);
    private File dir;
    private String extention;
    private List<File> files;

    FileFinder(File dir, String extention) {
        this.dir = dir;
        this.extention = extention;
    }

    List<File> find() {
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
    }
}