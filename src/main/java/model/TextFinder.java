package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.max;

//TODO: change implementation.
class TextFinder {

    TextFinder(String pattern){
        this.pattern = pattern;
    }

    private final String pattern;
    private volatile boolean stop = false;

    List<Long> find(Reader textReader) {
        char[] buf = new char[max(pattern.length()*5,1024)]; //keeps loaded chars
        char[] smBuf = new char[pattern.length()-1];//keeps last part of previous buffer

        List<Long> indices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(textReader)){
            //number of symbols that we have read before
            long offset = 0;
            reader.read(smBuf,0,smBuf.length);
            int haveRead;
            while ((haveRead = reader.read(buf,0,buf.length)) != -1){
                buf = Arrays.copyOfRange(buf,0,haveRead);//actualize buffer length
                String string = new StringBuffer().append(smBuf).append(buf).toString();
                final int strLen = string.length();
                int currentIndex = -1;
                do { // do-while to find all inclusions in buffer
                    //Actual substring finder. uses naive algorithm.
                    currentIndex = string.indexOf(pattern, currentIndex + 1);
                    if (currentIndex == -1) {
                        offset += haveRead;
                        //save last part of current buffer. it can contain part of pattern.
                        smBuf = Arrays.copyOfRange(string.toCharArray(),haveRead,strLen);
                        System.out.println(smBuf);
                        break;
                    } else {
                        indices.add(offset + currentIndex);
                    }
                    if (stop) return indices;
                } while (true);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return indices;
    }
    void close(){
        stop = true;
    }
}