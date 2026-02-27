module com.example.javafxh2app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;   // JDBC

    opens com.example.javafxh2app to javafx.fxml;
    opens com.example.javafxh2app.controllers to javafx.fxml;

    exports com.example.javafxh2app;
    exports com.example.javafxh2app.controllers;
    exports com.example.javafxh2app.models;
    exports com.example.javafxh2app.db;
}


