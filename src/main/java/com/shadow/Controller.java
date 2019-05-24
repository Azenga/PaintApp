package com.shadow;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    /*
     * Enumation containing all the shapes that can be drawn in the Pane
     */
    enum Design {
        Rectangle, Ellipse, Triangle, Line, Brush, Polygon
    }

    //Holds the current design to be drawn else null
    private Design design = null;


    //Area to be drawn
    private AnchorPane drawingPane;

    @FXML
    private JFXButton drawRectBtn, drawEllipseBtn, drawTriangleBtn, drawPolygonBtn, drawLineBtn, drawBrushBtn;

    @FXML
    private JFXButton fillButton, strokeBtn, zoomInBtn, zoomOutBtn;

    private Point2D startPoint, endPoint;

    private Map<String, ObservableList<Shape>> fileShapesMap;
    private Shape mShape = null;
    private DesignModels designModels = null;
    private ObservableList<Double> polygonPoints;

    private Paint stroke = Color.BLACK;
    private Paint fill = Color.TRANSPARENT;

    @FXML
    private MenuItem saveAsMenuItem;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private TabPane tabPane;


    private FilesHandler filesHandler;


    /**
     * The first method called when the FXML file gets registered
     */
    public void initialize() {

        filesHandler = new FilesHandler();

        fileShapesMap = new HashMap<>();

        designModels = new DesignModels();

        drawingPane = (AnchorPane) tabPane.getSelectionModel().getSelectedItem().getContent();

        String key = tabPane.getSelectionModel().getSelectedItem().getText();
        fileShapesMap.put(key, FXCollections.observableArrayList());

        drawingPane.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedEventHandler);
        drawingPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragEventHandler);
        drawingPane.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
        drawingPane.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickEventHandler);
        drawingPane.addEventHandler(KeyEvent.KEY_PRESSED, keyPressedEventHandler);

    }

    /**
     * Handles click events on the design buttons
     *
     * @param event is used to grab the source of the event to update the global design field correctly
     */
    @FXML
    public void shapeButtonClicked(Event event) {

        if (event.getSource() == drawLineBtn) design = Design.Line;
        else if (event.getSource() == drawBrushBtn) design = Design.Brush;
        else if (event.getSource() == drawRectBtn) design = Design.Rectangle;
        else if (event.getSource() == drawEllipseBtn) design = Design.Ellipse;
        else if (event.getSource() == drawTriangleBtn) design = Design.Triangle;
        else if (event.getSource() == drawPolygonBtn) {
            design = Design.Polygon;
            polygonPoints = FXCollections.observableArrayList();
        }

    }

    @FXML
    public void colorBtnClicked(Event event) {
        if (event.getSource() == strokeBtn) {
            System.out.println("Stroke Registered");
            java.awt.Color color = JColorChooser.showDialog(null, "Pick a stroke", java.awt.Color.BLACK);

            if (color != null) {

                Paint paint = Paint.valueOf(String.format("rgba(%d, %d, %d, %.2f)", color.getRed(), color.getGreen(), color.getBlue(), 1.0f));

                stroke = paint;
            }

        } else if (event.getSource() == fillButton) {

            System.out.println("Fill Registered");
            java.awt.Color color = JColorChooser.showDialog(null, "Pick a Fill", java.awt.Color.BLACK);

            if (color != null) {
                Paint paint = Paint.valueOf(String.format("rgba(%d, %d, %d, %.2f)", color.getRed(), color.getGreen(), color.getBlue(), 1.0f));

                fill = paint;
            }
        }
    }


    @FXML
    public void openFileFromSystem() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("text/*", "vec");
        fileChooser.setSelectedExtensionFilter(filter);

        File file = fileChooser.showOpenDialog((tabPane.getScene()).getWindow());

        if (file != null) {

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefSize(640, 570);
            anchorPane.setMinSize(640, 570);
            anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedEventHandler);
            anchorPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragEventHandler);
            anchorPane.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
            anchorPane.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickEventHandler);
            anchorPane.addEventHandler(KeyEvent.KEY_PRESSED, keyPressedEventHandler);

            Tab newTab = new Tab(file.getName(), anchorPane);

            fileShapesMap.put(file.getName(), filesHandler.getFileShapes(file.getAbsolutePath()));

            tabPane.getTabs().add(newTab);

            tabPane.getSelectionModel().selectLast();

            drawingPane = (AnchorPane) tabPane.getSelectionModel().getSelectedItem().getContent();

            paintPane();

        }


    }

    @FXML
    public void saveShapesToFile(Event event) {
        String key = tabPane.getSelectionModel().getSelectedItem().getText();

        String fileName = "";

        if (event.getSource() == saveAsMenuItem || key.equals("Untitled")) {

            fileName = JOptionPane.showInputDialog("Filename");
            tabPane.getSelectionModel().getSelectedItem().setText(fileName);

        } else {
            fileName = key;
        }

        filesHandler.saveShapes(fileShapesMap.get(key), fileName);
    }


    @FXML
    public void addNewTab() {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(640, 570);
        anchorPane.setMinSize(640, 570);
        anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedEventHandler);
        anchorPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragEventHandler);
        anchorPane.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
        anchorPane.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickEventHandler);
        anchorPane.addEventHandler(KeyEvent.KEY_PRESSED, keyPressedEventHandler);

        Tab newTab = new Tab("Untitled", anchorPane);

        fileShapesMap.put("untitled", FXCollections.observableArrayList());

        tabPane.getTabs().add(newTab);

        tabPane.getSelectionModel().selectLast();

        drawingPane = (AnchorPane) tabPane.getSelectionModel().getSelectedItem().getContent();

        paintPane();
    }

    private void paintPane() {

        drawingPane = (AnchorPane) tabPane.getSelectionModel().getSelectedItem().getContent();

        ObservableList<Shape> theFileShapes = fileShapesMap.get(tabPane.getSelectionModel().getSelectedItem().getText());

        drawingPane.getChildren().clear();

        if (theFileShapes != null) {
            if (theFileShapes.size() > 0) {
                for (Shape shape : theFileShapes) {
                    if (!drawingPane.getChildren().contains(shape)) drawingPane.getChildren().add(shape);
                }
            }
        }
        if (mShape != null) {
            drawingPane.getChildren().add(mShape);
        }

        mShape = null;

    }

    @FXML
    public void keyPressedOnThePane(Event event) {
        if (((KeyEvent) event).getCode() == KeyCode.SPACE) {
            System.out.println("Space Typed");
            if (design != null) {
                if (!design.equals(Design.Polygon) || polygonPoints.size() < 4) return;
                mShape = designModels.drawPolygon(polygonPoints);

                paintPane();

            }
        }
    }

    /*
     *Handles mouse pressed events on the drawing area => Pane
     */

    private EventHandler<MouseEvent> mousePressedEventHandler = event -> {

        if (design == null) return;

        switch (design) {
            case Line:
                startPoint = new Point2D(event.getX(), event.getY());
                endPoint = startPoint;
                break;
            case Rectangle:
                startPoint = new Point2D(event.getX(), event.getY());
                endPoint = startPoint;
                break;
            case Ellipse:
                startPoint = new Point2D(event.getX(), event.getY());
                endPoint = startPoint;
                break;

            case Polygon:

                if (polygonPoints != null) {
                    if (polygonPoints.size() < 0) startPoint = new Point2D(event.getX(), event.getY());
                    polygonPoints.addAll(event.getX(), event.getSceneY());
                }

                break;
        }

    };

    private EventHandler<MouseEvent> mouseDragEventHandler = event -> {
        if (design == null) return;

        endPoint = new Point2D(event.getX(), event.getY());

        switch (design) {
            case Line:

                if (startPoint != null && endPoint != null) {
                    Shape line = new Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
                    line.setStroke(stroke);
                    line.setFill(stroke);
                    mShape = line;
                    paintPane();
                }

                break;
            case Rectangle:

                if (startPoint != null && endPoint != null) {
                    mShape = designModels.getRect(startPoint, endPoint, stroke, fill);
                    paintPane();
                }

                break;

            case Ellipse:

                if (startPoint != null && endPoint != null) {
                    mShape = designModels.getEllipse(startPoint, endPoint, stroke, fill);
                    paintPane();
                }

                break;
        }
    };

    private EventHandler<MouseEvent> mouseReleasedEventHandler = event -> {

        if (design == null || design.equals(Design.Polygon)) return;

        String key = tabPane.getSelectionModel().getSelectedItem().getText();

        switch (design) {
            case Line:

                Line line = new Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
                line.setStroke(stroke);
                line.setFill(stroke);
                fileShapesMap.get(key).add(line);

                break;

            case Rectangle:
                Shape shape = designModels.getRect(startPoint, endPoint, stroke, fill);
                fileShapesMap.get(key).add(shape);
                break;

            case Ellipse:
                Shape ellipse = designModels.getEllipse(startPoint, endPoint, stroke, fill);
                fileShapesMap.get(key).add(ellipse);
                break;
        }

        design = null;
        startPoint = null;
        endPoint = null;

    };


    private EventHandler<MouseEvent> mouseClickEventHandler = event -> System.out.println("Mouse CLicked");

    public EventHandler<KeyEvent> keyPressedEventHandler = event -> {
        if (event.getCode() == KeyCode.SPACE) {
            System.out.println("Space Typed");
            if (design != null) {
                if (!design.equals(Design.Polygon) || polygonPoints.size() < 4) return;
                mShape = designModels.drawPolygon(polygonPoints);

                paintPane();

            }
        }
    };


}
