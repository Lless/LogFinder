package model;

import java.io.File;
import java.util.List;

public class FileInfo {
    private File file;
    private List<Integer> indices;

    FileInfo(File file, List<Integer> indices) {
        this.file = file;
        this.indices = indices;
    }

    public File getFile() {
        return file;
    }

    public List<Integer> getIndices() {
        return indices;
    }
}
