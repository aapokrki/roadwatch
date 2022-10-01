package fi.tuni.javafxproject;

import com.sothawo.mapjfx.Projection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        // MapView
        String mapFxmlFile = "fxml/map-view.fxml";
        FXMLLoader mapFxmlLoader = new FXMLLoader();
        Parent rootNode = mapFxmlLoader.load(getClass().getResourceAsStream(mapFxmlFile));

        final MapController mapController = mapFxmlLoader.getController();
        final Projection projection = getParameters().getUnnamed().contains("wgs84")
                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        mapController.initMapAndControls(projection);
        Pane mapView = (Pane) rootNode;

        // HelloApp
        String helloFxmlFile = "fxml/hello-view.fxml";
        FXMLLoader appFxmlLoader = new FXMLLoader(HelloApplication.class.getResource(helloFxmlFile));
        Scene appScene = new Scene(appFxmlLoader.load());

        final HelloController helloController = appFxmlLoader.getController();
        helloController.initializeMap(mapView);

        primaryStage.setTitle("Hello!");
        primaryStage.setScene(appScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}