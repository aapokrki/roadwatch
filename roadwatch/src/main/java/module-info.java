module fi.tuni.roadwatch {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.sothawo.mapjfx;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.databind;

    opens fi.tuni.roadwatch to javafx.fxml;
    exports fi.tuni.roadwatch;
}