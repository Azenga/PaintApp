package com.shadow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.io.*;

public class FilesHandler {

    DesignModels designModels = new DesignModels();

    Paint stroke = Color.BLACK;
    Paint fill = Color.TRANSPARENT;


    public FilesHandler() {
    }


    public ObservableList<Shape> getFileShapes(String fileName) {

        ObservableList<Shape> shapes = FXCollections.observableArrayList();

        BufferedReader reader = getFileReader(fileName);

        String sline = null;

        try {
            while ((sline = reader.readLine()) != null) {
                String[] words = sline.split(" ");

                if (words[0].equals("LINE")) {

                    Point2D startPoint = new Point2D(Double.parseDouble(words[1].trim()), Double.parseDouble(words[2].trim()));
                    Point2D endPoint = new Point2D(Double.parseDouble(words[3].trim()), Double.parseDouble(words[4].trim()));
                    Line line = new Line(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
                    line.setStroke(stroke);
                    line.setFill(stroke);
                    shapes.add(line);

                } else if (words[0].equals("RECTANGLE")) {

                    Point2D startPoint = new Point2D(Double.parseDouble(words[1].trim()), Double.parseDouble(words[2].trim()));
                    Point2D endPoint = new Point2D(Double.parseDouble(words[3].trim()), Double.parseDouble(words[4].trim()));

                    shapes.add(designModels.getRect(startPoint, endPoint, stroke, fill));


                } else if (words[0].equals("ELLIPSE")) {

                    Point2D startPoint = new Point2D(Double.parseDouble(words[1].trim()), Double.parseDouble(words[2].trim()));
                    Point2D endPoint = new Point2D(Double.parseDouble(words[3].trim()), Double.parseDouble(words[4].trim()));

                    shapes.add(designModels.getEllipse(startPoint, endPoint, stroke, fill));

                } else if (words[0].equals("PEN")){
                    stroke = Paint.valueOf(words[1].trim());
                } else if (words[0].equals("FILL")){
                    fill = Paint.valueOf(words[1].trim());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return shapes;
    }

    private BufferedReader getFileReader(String fileName) {

        File file = new File(fileName);

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new FileReader(
                            file
                    )
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return reader;
    }

    public void saveShapes(ObservableList<Shape> fileShapes, String fileName) {
        PrintWriter writer = getFileWriter(fileName);

        for (Shape shape : fileShapes) {

            writer.println("PEN " + shape.getStroke());
            writer.println("FILL " + shape.getFill());

            if (shape instanceof Line) {
                String line = String.format("LINE %.2f %.2f %.2f %.2f",
                        ((Line) shape).getStartX(), ((Line) shape).getStartY(),
                        ((Line) shape).getEndX(), ((Line) shape).getEndY()
                );
                writer.println(line);

            } else if (shape instanceof Rectangle) {
                String rect = String.format("RECTANGLE %.2f %.2f %.2f %.2f", ((Rectangle) shape).getX(), ((Rectangle) shape).getY(),
                        ((Rectangle) shape).getX() + ((Rectangle) shape).getWidth(),
                        ((Rectangle) shape).getY() + ((Rectangle) shape).getHeight());
                writer.println(rect);

            } else if (shape instanceof Ellipse) {

                String ellipse = String.format("ELLIPSE %.2f %.2f %.2f %.2f",
                        ((Ellipse) shape).getCenterX() - ((Ellipse) shape).getRadiusX(),
                        ((Ellipse) shape).getCenterY() - ((Ellipse) shape).getRadiusY(),
                        ((Ellipse) shape).getCenterX() + ((Ellipse) shape).getRadiusX(),
                        ((Ellipse) shape).getCenterY() + ((Ellipse) shape).getRadiusY()
                );
                writer.println(ellipse);

            } else {
                writer.println(shape);
            }
        }
        writer.close();
    }

    private PrintWriter getFileWriter(String fileName) {
        File file = new File(fileName);
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(file)
                    )
            );

        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }

        return writer;
    }
}
