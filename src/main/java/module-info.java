module org.example.mppjavafx {
    requires java.sql;
    requires java.desktop;


    opens org.example.clientfx.mppjavafx to javafx.fxml;
    opens org.example.mppjavafx.controller to javafx.fxml;
    opens org.example.mppjavafx.domain to javafx.base;
}