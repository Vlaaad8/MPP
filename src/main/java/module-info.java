module org.example.mppjavafx {
    requires java.sql;
    requires java.desktop;
    requires io.grpc;
    requires annotations.api;
    requires io.grpc.stub;
    requires protobuf.java;
    requires io.grpc.protobuf;
    requires com.google.common;


    opens org.example.clientfx.mppjavafx to javafx.fxml;
    opens org.example.mppjavafx.controller to javafx.fxml;
    opens org.example.mppjavafx.domain to javafx.base;
}