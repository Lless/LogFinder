package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

class FileFinder {

    private File dir;
    private String extention;
    private List<File> files;

    FileFinder(File dir, String extention) {
        this.dir = dir;
        this.extention = extention;
    }

    List<File> find() {
        if (files == null)
            try {
                files = Files.walk(dir.toPath())
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(f -> f.getName().matches(".*\\." + extention))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        return files;
    }
}