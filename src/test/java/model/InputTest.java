package model;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class InputTest {
    private TextField field;
    private Input input;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void InitializeJavaFX(){
        new JFXPanel();
    }
    @Before
    public void setField() {
        field = new TextField();
        input = new Input();
    }

    @Test(expected = WrongInputException.class)
    public void emptyExtIsInvalid() throws WrongInputException {
        field.setText("");
        input.setExtension(field);
    }

    @Test
    public void notEmptyExtIsValid() throws WrongInputException{
        field.setText("a");
        input.setExtension(field);
        assertEquals(field.getText(),input.getExtension());
    }

    @Test(expected = WrongInputException.class)
    public void emptyPatternIsInvalid() throws WrongInputException {
        field.setText("");
        input.setPattern(field);
    }

    @Test
    public void notEmptyPatternIsValid() throws WrongInputException{
        field.setText("a");
        input.setPattern(field);
        assertEquals(field.getText(),input.getPattern());
    }

    @Test(expected = WrongInputException.class)
    public void emptyFolderIsInvalid() throws WrongInputException {
        field.setText("");
        input.setPattern(field);
    }

    @Test(expected = WrongInputException.class)
    public void notExistingFolderIsInvalid() throws WrongInputException{
        field.setText("a");
        input.setFolder(field);
    }

    @Test(expected = WrongInputException.class)
    public void existingNotFolderIsInvalid() throws WrongInputException,IOException{
        File f = folder.newFile("a");
        f.createNewFile();
        field.setText(f.getAbsolutePath());

        input.setFolder(field);
    }

    @Test
    public void existingFolderIsValid() throws WrongInputException,IOException{
        File f = folder.newFolder("a");
        f.mkdir();
        field.setText(f.getAbsolutePath());

        input.setFolder(field);

        assertEquals(field.getText(),input.getFolder().getAbsolutePath());
    }
}