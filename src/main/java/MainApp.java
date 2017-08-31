import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {
    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlfile = "fxml/application.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream(fxmlfile));
        stage.setTitle("Log Finder");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("png/Log128.png")));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("png/Log20.png")));
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest((event) -> FileManager.close());
        log.info("App started");
        stage.show();
    }
}
