package com.shadow;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

public class DesignModels {

    public Shape getRect(Point2D startPoint, Point2D endPoint, Paint stroke, Paint fill) {

        double x = Math.min(startPoint.getX(), endPoint.getX());
        double y = Math.min(startPoint.getY(), endPoint.getY());

        double width = Math.abs(endPoint.getX() - startPoint.getX());
        double height = Math.abs(endPoint.getY() - startPoint.getY());

        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setStroke(stroke);
        rect.setFill(fill);

        return rect;
    }

    public Shape getEllipse(Point2D startPoint, Point2D endPoint, Paint stroke, Paint fill) {

        double x = Math.min(startPoint.getX(), endPoint.getX());
        double y = Math.min(startPoint.getY(), endPoint.getY());

        double width = Math.abs(endPoint.getX() - startPoint.getX());
        double height = Math.abs(endPoint.getY() - startPoint.getY());

        double centerX = x + width / 2;
        double centerY = y + height / 2;

        //double radius = Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2)) / 2;
        Ellipse ellipse = new Ellipse(centerX, centerY, width / 2, height / 2);
        ellipse.setStroke(stroke);
        ellipse.setFill(fill);
        //return new Circle(centerX, centerY, radius);

        return ellipse;

    }

    public Shape drawPolygon(ObservableList<Double> polygonPoints) {

        double[] primitiveDoubleArray = new double[polygonPoints.size()];

        for (int i = 0; i < polygonPoints.size(); i++) {
            primitiveDoubleArray[i] = polygonPoints.get(i);
        }

        Polygon polygon = new Polygon(primitiveDoubleArray);

        System.out.println(polygon);

        return polygon;
    }
}
