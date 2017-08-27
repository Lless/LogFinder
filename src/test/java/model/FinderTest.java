package model;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static model.Finder.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class FinderTest {
    @Test
    public void find11In111211Is014() {
        Reader text = new StringReader("111211");
        String pattern = "11";
        List<Long> expected = new ArrayList<>();
        expected.add(0L);
        expected.add(1L);
        expected.add(4L);
        List<Long> actual = find(text, pattern);
        assertEquals(expected.size(),actual.size());
        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void findMissingInTextIsEmpty() {
        Reader text = new StringReader("aaa");
        String pattern = "bb";
        assertTrue(find(text,pattern).isEmpty());
    }
}