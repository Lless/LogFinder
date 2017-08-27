package model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileFinderTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void find() {
        try {
            String pattern = "log";
            File maindir = folder.newFolder("dir");
            maindir.mkdir();
            File first = createFile(maindir,"first.log");
            File second = createFile(maindir,"second.txt");
            File somedir = new File(maindir,"somedir");
            somedir.mkdir();
            File third = createFile(somedir,"third.log");
            File forth = createFile(somedir,"forth.txt");
            List<File> expected = new ArrayList<>();
            expected.add(first);
            expected.add(third);
            List<File> actual = new FileFinder(maindir,pattern).find();
            assertEquals(expected.size(),actual.size());
            assertTrue(actual.containsAll(expected));
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private File createFile(File dir, String name) throws IOException{
        File file = new File(dir,name);
        file.createNewFile();
        return file;
    }
}