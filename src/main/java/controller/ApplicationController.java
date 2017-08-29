package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import model.FileManager;
import model.FileModel;

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
    private ObservableList<FileModel> files;

    @FXML
    private void initialize(){
        fileTree.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldvalue, newvalue) -> this.setText(newvalue) );
        Platform.runLater( () -> filepath.requestFocus() );
        files = FXCollections.observableArrayList();
    }

    //Todo: implement
    private void setTreeNode(File f){}

    private void setText(Number index){
        text.appendText("pressed tree node №"+index);
    }

    private void startSearch(){
        FileManager.getResults(extention.getText(),pattern.getText(),directory,files);
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
    //todo: test
    @FXML
    private void checkFields(){
        if(extention.getText().isEmpty()) {
            extention.requestFocus();
            return;
        }
        if ((directory == null) ||
                (!directory.getName().equals(filepath.getText())) ){
            if (filepath.getText().isEmpty()){
                filepath.requestFocus();
                openFolder();
                return;
            }
            File newDir = new File(filepath.getText());
            if (!newDir.exists() || ! (newDir.isDirectory())){
                filepath.setText("");
                filepath.requestFocus();
                openFolder();
                return;
            }
            directory = newDir;
        }
        if (pattern.getText().isEmpty()){
            pattern.requestFocus();
            return;
        }
        startSearch();
    }

    @FXML
    private void openFolder(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose file directory");
        if (directory != null) chooser.setInitialDirectory(directory);
        directory = chooser.showDialog(null);
        filepath.setText(directory.getAbsolutePath());
    }

}
