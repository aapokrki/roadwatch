module fi.tuni.roadwatch {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens fi.tuni.roadwatch to javafx.fxml;
    exports fi.tuni.roadwatch;
}