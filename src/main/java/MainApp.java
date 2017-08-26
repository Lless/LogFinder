import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created for the greater good on 26.08.2017.
 */
public class MainApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        String fxmlfile = "fxml/application.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream(fxmlfile));
        stage.setTitle("Log Finder");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
