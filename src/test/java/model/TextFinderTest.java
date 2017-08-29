package model;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TextFinderTest {
    @Test
    public void find11In111211Is014() {
        Reader text = new StringReader("111211");
        String pattern = "11";
        List<Long> expected = new ArrayList<>();
        expected.add(0L);
        expected.add(1L);
        expected.add(4L);
        List<Long> actual = new TextFinder(pattern).find(text);
        assertEquals(expected.size(),actual.size());
        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void find1in222isEmpty(){
        Reader text = new StringReader("222");
        String pattern = "1";
        List<Long> result = new TextFinder(pattern).find(text);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findWithDelimiters() {
        Reader text = new StringReader("aa \nbb");
        String pattern = "a \nb";
        List<Long> result = new TextFinder(pattern).find(text);
        assertFalse(result.isEmpty());
    }
}