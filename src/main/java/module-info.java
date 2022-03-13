module com.example.admir {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens com.example.admir to javafx.fxml;
    exports com.example.admir;
    exports controller;
    opens controller to javafx.fxml;
    exports domain;
    opens domain to javafx.fxml;


}