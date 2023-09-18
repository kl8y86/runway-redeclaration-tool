module uk.ac.soton.ecs.runwayredeclarationtool {
    requires javafx.controls;
    requires javafx.fxml;
    requires ormlite.jdbc;
    requires java.sql;
    requires xstream;

    opens uk.ac.soton.ecs.runwayredeclarationtool to javafx.fxml, Hashing;
    opens uk.ac.soton.ecs.runwayredeclarationtool.ui to javafx.fxml;
    opens uk.ac.soton.ecs.runwayredeclarationtool.data.models to ormlite.jdbc, xstream;
    exports uk.ac.soton.ecs.runwayredeclarationtool;
    exports uk.ac.soton.ecs.runwayredeclarationtool.ui;
    exports uk.ac.soton.ecs.runwayredeclarationtool.data.models;
    exports uk.ac.soton.ecs.runwayredeclarationtool.data.repositories;
}