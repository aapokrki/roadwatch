module fi.tuni.javafxproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.sothawo.mapjfx;
    requires javafx.graphics;



    opens fi.tuni.javafxproject to javafx.fxml, javafx.graphics;
    exports fi.tuni.javafxproject;
}