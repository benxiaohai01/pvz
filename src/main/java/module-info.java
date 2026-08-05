module com.pvz {
    requires javafx.controls;
    requires javafx.graphics;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    opens com.pvz.config to com.fasterxml.jackson.databind;
    opens com.pvz.model.level to com.fasterxml.jackson.databind;
    opens com.pvz.model.entity.plant to com.fasterxml.jackson.databind;
    opens com.pvz.model.entity.zombie to com.fasterxml.jackson.databind;

    exports com.pvz.launcher;
}
