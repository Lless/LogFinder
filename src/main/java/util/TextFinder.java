package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.max;

public class TextFinder {
    private static final Logger log = LoggerFactory.getLogger(TextFinder.class);

    public TextFinder(String pattern) {
        this.pattern = pattern;
        log.debug("Trying to find pattern: " + pattern);
    }

    private final String pattern;
    private volatile boolean stop = false;

    public List<Integer> find(Reader textReader) {
        char[] buf = new char[max(pattern.length() * 5, 1024)]; //keeps loaded chars
        char[] smBuf = new char[pattern.length() - 1];//keeps last part of previous buffer

        List<Integer> indices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(textReader)) {
            //number of symbols that we have read before
            int offset = 0;
            reader.read(smBuf, 0, smBuf.length);
            int haveRead;
            while ((haveRead = reader.read(buf, 0, buf.length)) != -1) {
                buf = Arrays.copyOfRange(buf, 0, haveRead);//actualize buffer length
                String string = new StringBuffer().append(smBuf).append(buf).toString();
                log.trace("\nBuffer: " + string + "\nHas offset " + Long.toString(offset) + "\n\n");
                final int strLen = string.length();
                int currentIndex = -1;
                do { // do-while to find all inclusions in buffer
                    //Actual substring finder. uses naive algorithm.
                    currentIndex = string.indexOf(pattern, currentIndex + 1);
                    if (currentIndex == -1) {
                        offset += haveRead;
                        //save last part of current buffer. it can contain part of pattern.
                        smBuf = Arrays.copyOfRange(string.toCharArray(), haveRead, strLen);
                        break;
                    } else {
                        indices.add(offset + currentIndex);
                    }
                    if (stop) return indices;
                } while (true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indices;
    }

    public void close() {
        stop = true;
    }
}