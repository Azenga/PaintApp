package com.shadow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Shape;
import org.junit.Test;

import static org.junit.Assert.*;

public class DesignModelsTest {

    DesignModels models = new DesignModels();

    @Test
    public void drawPolygon(){
        ObservableList<Double> polyPoints = FXCollections.observableArrayList(120.5, 120.5, 200.5, 250.5, 130.5, 250.5);
        assertTrue(models.drawPolygon(polyPoints) instanceof Shape);
    }

}