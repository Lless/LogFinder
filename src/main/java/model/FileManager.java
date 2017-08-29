package model;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//todo: test
public class FileManager {
    private String extention;
    private String pattern;
    private File directory;
    private List<FileModel> results;

    private final TextFinder finder;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static FileManager fileManager;

    private FileManager(String extention, String pattern, File directory, List<FileModel> results) {
        this.extention = extention;
        this.pattern = pattern;
        this.directory = directory;
        this.results = results;
        finder = new TextFinder(pattern);
    }

    public static void getResults(String extention, String pattern, File directory, List<FileModel> results) {
        if ((fileManager == null) || !fileManager.extention.equals(extention) ||
                (fileManager.directory != directory) || !fileManager.pattern.equals(pattern)) {
            if (fileManager != null) close();
            fileManager = new FileManager(extention, pattern, directory, results);
            fileManager.startSearch();
        }
    }

    public static void close() {
        fileManager.executorService.shutdownNow();
        try {
            fileManager.finder.close();
            fileManager.executorService.awaitTermination(1, TimeUnit.MINUTES);
            fileManager.results.clear();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startSearch() {
        List<File> files = new FileFinder(directory, extention).find();
        for (File f : files)
            executorService.submit(() -> {
                try {
                    Reader reader = new FileReader(f);
                    List<Long> indices = finder.find(reader);
                    if (!indices.isEmpty())
                        synchronized (results) {
                            results.add(new FileModel(f, indices));
                        }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
    }
}
