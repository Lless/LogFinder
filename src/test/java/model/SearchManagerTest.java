package model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import util.FileFinder;
import util.TextFinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class SearchManagerTest {
    private Input input = mock(Input.class);
    private FileFinder fileFinder= mock(FileFinder.class);
    private TextFinder textFinder = mock(TextFinder.class);
    private File a;
    private Consumer<File> emptyConsumer= (f)->{};

    public SearchManagerTest(){
        doAnswer((r) -> {
            Consumer<File> consumer = r.getArgument(0);
            consumer.accept(a);
            return null;
        }).when(fileFinder).find(any());
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void managerExist() {
        SearchManager.startSearch(input, emptyConsumer);
        assertFalse(SearchManager.getManager() == null);
    }

    @Test
    public void noDuplicateSearch() {
        SearchManager.startSearch(input, emptyConsumer);
        SearchManager remembered = SearchManager.getManager();
        SearchManager.startSearch(input, emptyConsumer);
        SearchManager actual = SearchManager.getManager();
        assertTrue(remembered.equals(actual));
    }

    @Test
    public void newSearchWithAnotherInput(){
        Input input2 = mock(Input.class);
        SearchManager.startSearch(input, emptyConsumer);
        SearchManager remembered = SearchManager.getManager();
        SearchManager.startSearch(input2, emptyConsumer);
        SearchManager actual = SearchManager.getManager();
        assertFalse(remembered.equals(actual));
    }

    @Test
    public void avoidUnnecessary() throws IOException, InterruptedException {
        a = folder.newFile("someFile");
        when((textFinder.find(any()))).thenReturn(new ArrayList<>());

        List<File> result = new ArrayList<>();
        SearchManager.startSearch(input, result::add, textFinder, fileFinder);
        Thread.sleep(100);

        assertTrue(result.isEmpty());
    }

    @Test
    public void includeNecessary() throws IOException, InterruptedException {
        a = folder.newFile("someFile");

        List<Integer> Entries = new ArrayList<>();
        Entries.add(0);
        when((textFinder.find(any()))).thenReturn(Entries);

        List<File> result = new ArrayList<>();
        SearchManager.startSearch(input, result::add, textFinder, fileFinder);
        Thread.sleep(100);

        assertFalse(result.isEmpty());
    }
}