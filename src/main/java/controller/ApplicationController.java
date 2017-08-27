package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;

import java.io.File;


public class ApplicationController {
    @FXML
    private TextField extention;

    @FXML
    private TextField pattern;

    @FXML
    private TextField filepath;

    @FXML
    private TextArea text;

    @FXML
    private TreeView fileTree;

    private File directory;

    @FXML
    private void initialize(){
        fileTree.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldvalue, newvalue) -> this.setText(newvalue) );
    }

    //Todo: implement
    private void setText(Number index){
        text.appendText("pressed tree node №"+index);
    }

    @FXML
    private void showNext(){
        text.appendText("showNextPressed\n");
    }

    @FXML
    private void showPrev(){
        text.appendText("showPrevPressed\n");
    }

    @FXML
    private void selectAll(){
        text.appendText("SelectAllPressed\n");
    }

    @FXML
    private void startSearch(){
        text.appendText("startSearchPressed\n");
    }

    @FXML
    private void openFolder(){
        text.appendText("openFolderPressed\n");
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose file directory");
        directory = chooser.showDialog(null);
        filepath.setText(directory.getAbsolutePath());
    }

}
