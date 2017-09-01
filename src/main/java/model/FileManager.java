package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class FileManager {
    private static final Logger log = LoggerFactory.getLogger(FileManager.class);
    private String extention;
    private String pattern;
    private File directory;
    private TextFinder finder;
    private Consumer<File> toDo;

    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ConcurrentHashMap<File,Integer[]> fileInfos = new ConcurrentHashMap<>();
    private static FileManager fileManager;

    private FileManager(String extention, String pattern, File directory, Consumer<File> toDo) {
        this.extention = extention;
        this.pattern = pattern;
        this.directory = directory;
        finder = new TextFinder(pattern);
        this.toDo = toDo;
    }

    public static void getResults(String extention, String pattern, File directory, Consumer<File> toDo) {
        if ((fileManager == null) || !fileManager.extention.equals(extention) ||
                (fileManager.directory != directory) || !fileManager.pattern.equals(pattern)) {
            if (fileManager != null) close();
            fileManager = new FileManager(extention, pattern, directory, toDo);
            fileManager.startSearch();
        }
    }

    public static void close() {
        log.warn("Closing search");
        fileManager.executorService.shutdownNow();
        fileManager.finder.close();
    }

    private void startSearch() {
        log.info("Search started");
        List<File> files = new FileFinder(directory, extention).find();
        for (File f : files)
            executorService.submit(() -> {
                try {
                    log.debug("Search pattern in "+f.getAbsolutePath());
                    Reader reader = new FileReader(f);
                    List<Integer> indices = finder.find(reader);
                    if ((indices != null) && !indices.isEmpty()){
                        toDo.accept(f);
                        Integer[] entries = new Integer[indices.size()];
                        entries = indices.toArray(entries);
                        fileInfos.put(f,entries);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        log.info("Search ended");
    }
    public static Integer[] getEntries(File file){
        return fileManager.fileInfos.get(file);
    }
    public static String getPattern(){
        return fileManager.pattern;
    }
}
