package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileFinder;
import util.TextFinder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SearchManager {
    private static final Logger log = LoggerFactory.getLogger(SearchManager.class);

    private Input input;
    private Consumer<File> doWithResults;

    private TextFinder finder;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ConcurrentHashMap<File, Integer[]> Entries = new ConcurrentHashMap<>();

    private static SearchManager searchManager;

    private SearchManager(Input input, Consumer<File> doWithResults) {
        this.input = input;
        finder = new TextFinder(input.getPattern());
        this.doWithResults = doWithResults;
    }

    public static void startSearch(Input input, Consumer<File> toDo) {
        if (searchManager != null) {
            if (searchManager.input.equals(input)) return;
            else close();
        }
        searchManager = new SearchManager(input, toDo);
        log.info("Search started");
        searchManager.executorService.submit(
                () -> new FileFinder(input.getFolder(), input.getExtension()).find(searchManager::searchTextInFile)
        );
    }

    public static void close() {
        log.warn("Closing search");
        if (searchManager == null) return;
        searchManager.executorService.shutdownNow();
        searchManager.finder.close();
    }

    private void searchTextInFile(File f) {
        executorService.submit(() -> {
            try {
                log.debug("Search pattern in " + f.getAbsolutePath());
                Reader reader = new FileReader(f);
                List<Integer> indices = finder.find(reader);
                if ((indices != null) && !indices.isEmpty()) {
                    doWithResults.accept(f);
                    log.info("Pattern found in " + f.getAbsolutePath());
                    Integer[] entries = new Integer[indices.size()];
                    entries = indices.toArray(entries);
                    Entries.put(f, entries);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static SearchManager getManager() {
        return searchManager;
    }

    public Integer[] getEntries(File file) {
        return Entries.get(file);
    }

    public Input getInput() {
        return input;
    }
}
