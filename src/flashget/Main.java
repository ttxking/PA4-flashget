package flashget;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("flashget.fxml"));
        primaryStage.setTitle("File Downloader");
        // load theme
        root.getStylesheets().add("/flashget/theme.css");
        // load image
        Image icon = new Image(getClass().getResourceAsStream("/image/1200px-Circle-icons-download.svg.png"));
        // set application icon
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(new Scene(root, 939, 200));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



