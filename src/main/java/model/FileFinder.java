package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

//Todo: Test this!
//Todo: Add logging
public class FileFinder {

    private File catalog;
    private String filename;
    private List<File> files;

    public FileFinder(File catalog, String filename) {
        this.catalog = catalog;
        this.filename = filename;
    }

    public List<File> find() {
        if (files == null)
            try {
                files = Files.walk(catalog.toPath())
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(f -> f.getName().matches(filename))
                        .collect(Collectors.toList());
            } catch (IOException ex) {
                System.out.println(ex);
            }
        return files;
    }
}
