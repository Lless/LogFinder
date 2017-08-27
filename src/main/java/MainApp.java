import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //Todo: Icon!
    @Override
    public void start(Stage stage) throws Exception {
        String fxmlfile = "fxml/application.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream(fxmlfile));
        stage.setTitle("Log Finder");
        stage.getIcons().add(new Image("file:resources/png/Log64.png"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
