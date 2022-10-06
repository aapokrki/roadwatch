package fi.tuni.roadwatch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RoadWatch extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RoadWatch.class.getResource("roadwatch.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("RoadWatch");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}