package fi.tuni.javafxproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class FxmlLoader {
    private Pane view;

    public Pane getPage(String fileName){
        try {
            URL fileUrl = HelloApplication.class.getResource(fileName);
            if(fileUrl == null) {
                throw new java.io.FileNotFoundException("FXML file cant be found");
            }
            view = FXMLLoader.load(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }
}
