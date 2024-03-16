module com.example.rapizz {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.rapizz to javafx.fxml;
    exports com.example.rapizz;
}