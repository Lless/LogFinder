package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FileManager {
    private static final Logger log = LoggerFactory.getLogger(FileManager.class);
    private Input input;
    private Consumer<File> doWithResults;
    private TextFinder finder;

    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ConcurrentHashMap<File, Integer[]> fileInfos = new ConcurrentHashMap<>();
    private static FileManager fileManager;

    private FileManager(Input input, Consumer<File> doWithResults) {
        this.input = input;
        finder = new TextFinder(input.getPattern());
        this.doWithResults = doWithResults;
    }

    public static void startSearch(Input input, Consumer<File> toDo) {
        if (fileManager != null) {
            if (fileManager.input.equals(input)) return;
            else close();
        }
        fileManager = new FileManager(input, toDo);
        log.info("Search started");
        fileManager.executorService.submit(
                () -> new FileFinder(input.getFolder(), input.getExtension()).find(fileManager::searchTextInFile)
        );
    }

    public static void close() {
        log.warn("Closing search");
        fileManager.executorService.shutdownNow();
        fileManager.finder.close();
    }

    private void searchTextInFile(File f) {
        executorService.submit(() -> {
            try {
                log.debug("Search pattern in " + f.getAbsolutePath());
                Reader reader = new FileReader(f);
                List<Integer> indices = finder.find(reader);
                if ((indices != null) && !indices.isEmpty()) {
                    doWithResults.accept(f);
                    log.info("Pattern founded in " + f.getAbsolutePath());
                    Integer[] entries = new Integer[indices.size()];
                    entries = indices.toArray(entries);
                    fileInfos.put(f, entries);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static Integer[] getEntries(File file) {
        return (fileManager != null) ? fileManager.fileInfos.get(file) : null;
    }

    public static Input getInput() {
        return (fileManager != null) ? fileManager.input : null;
    }
}
