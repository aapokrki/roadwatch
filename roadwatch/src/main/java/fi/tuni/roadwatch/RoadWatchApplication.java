package fi.tuni.roadwatch;

import com.sothawo.mapjfx.Projection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class RoadWatchApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(RoadWatchApplication.class.getResource("fxml/roadwatch.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        final RoadWatchController roadWatchController = fxmlLoader.getController();

        roadWatchController.loadMap();
        roadWatchController.setSessionData(roadWatchController);
        stage.setTitle("RoadWatch");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}