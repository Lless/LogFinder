package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//TODO: change implementation.
//TODO: fix  bug with whitespaces
class TextFinder {
    static List<Long> find(Readable text, String pattern) {
        Scanner scanner = new Scanner(text);
        List<Long> indexes = new ArrayList<>();
        long offset = 0;
        while (scanner.hasNext()) {
            String string = scanner.next();
            int currentIndex = -1;
            do {
                currentIndex = string.indexOf(pattern, currentIndex + 1);
                if (currentIndex == -1) {
                    offset += string.length();
                    break;
                } else {
                    indexes.add(offset + currentIndex);
                }
            } while (true);
        }
        return indexes;
    }
}