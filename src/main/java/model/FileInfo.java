package model;

import java.io.File;
import java.util.List;

public class FileInfo {
    private File file;
    private List<Long> indices;

    FileInfo(File file, List<Long> indices) {
        this.file = file;
        this.indices = indices;
    }

    public File getFile() {
        return file;
    }

    public List<Long> getIndices() {
        return indices;
    }
}
