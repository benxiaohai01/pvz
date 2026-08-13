module com.bxh.pvz {
    requires javafx.controls;
    requires javafx.graphics;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    opens com.bxh.pvz.config to com.fasterxml.jackson.databind;

    exports com.bxh.pvz.launcher;
}
