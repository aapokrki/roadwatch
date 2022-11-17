module fi.tuni.roadwatch {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.sothawo.mapjfx;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.commons.io;
    requires java.xml;
    requires org.slf4j;
    requires java.sql;

    opens fi.tuni.roadwatch to javafx.fxml;
    exports fi.tuni.roadwatch;
    //exports fi.tuni.roadwatch.controllers;
    //opens fi.tuni.roadwatch.controllers to javafx.fxml;
}