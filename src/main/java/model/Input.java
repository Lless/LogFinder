package model;

import controller.WrongInputException;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.Objects;

public class Input {
    private String extension;
    private String pattern;
    private File folder;

    public void setExtension(TextField input) throws WrongInputException {
        String extension = input.getText();
        if (extension.isEmpty())
            throw new WrongInputException("Extension is wrong!", input);
        else this.extension = extension;
    }

    public void setPattern(TextField input) throws WrongInputException {
        String pattern = input.getText();
        if (pattern.isEmpty())
            throw new WrongInputException("Pattern is wrong!", input);
        else this.pattern = pattern;
    }

    public void setFolder(TextField input) throws WrongInputException {
        File folder = new File(input.getText());
        if (!folder.exists()) throw new WrongInputException("No such folder!", input);
        if (!folder.isDirectory()) throw new WrongInputException("File is not a folder!", input);
        this.folder = folder;
    }

    public File getFolder() {
        return folder;
    }

    public String getExtension() {
        return extension;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Input checker = (Input) o;
        return Objects.equals(extension, checker.extension) &&
                Objects.equals(pattern, checker.pattern) &&
                Objects.equals(folder, checker.folder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extension, pattern, folder);
    }
}
