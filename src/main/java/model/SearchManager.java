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
    private TextFinder textFinder;
    private FileFinder fileFinder;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ConcurrentHashMap<File, Integer[]> Entries = new ConcurrentHashMap<>();

    private static SearchManager searchManager;

    private SearchManager(Input input, Consumer<File> doWithResults) {
        this.input = input;
        this.doWithResults = doWithResults;
        textFinder = new TextFinder(input.getPattern());
        fileFinder = new FileFinder(input.getFolder(), input.getExtension());
    }

    private void startSearch() {
        log.info("Search started");
        searchManager.executorService.submit(
                () -> fileFinder.find(searchManager::searchTextInFile));
    }

    public static void startSearch(Input input, Consumer<File> toDo) {
        if (searchManager != null) {
            if (searchManager.input.equals(input)) return;
            else close();
        }
        searchManager = new SearchManager(input, toDo);
        searchManager.startSearch();
    }

    //for using in test
    static void startSearch(Input input, Consumer<File> toDo, TextFinder textFinder, FileFinder fileFinder) {
        if (searchManager != null) {
            if (searchManager.input.equals(input)) return;
            else close();
        }
        searchManager = new SearchManager(input, toDo);
        searchManager.fileFinder = fileFinder;
        searchManager.textFinder = textFinder;
        searchManager.startSearch();
    }

    public static void close() {
        log.warn("Closing search");
        if (searchManager == null) return;
        searchManager.executorService.shutdownNow();
        searchManager.textFinder.close();
    }

    private void searchTextInFile(File f) {
        executorService.submit(() -> {
            try {
                log.debug("Search pattern in " + f.getAbsolutePath());
                Reader reader = new FileReader(f);
                List<Integer> indices = textFinder.find(reader);
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
