package ru.itis.utils;

import javafx.geometry.Point2D;

import java.io.IOException;
import java.io.Serializable;

public class MyPoint2D implements Serializable {
    Point2D myPoint ;

    public MyPoint2D(double x, double y) {
        myPoint = new Point2D(x,y) ;
    }

    public Point2D getPoint() {
        return myPoint ;
    }

    public void setPoint(Point2D point2D) {
        myPoint = point2D;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeDouble(myPoint.getX());
        out.writeDouble(myPoint.getY());
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        myPoint = new Point2D(in.readDouble(), in.readDouble()) ;
    }
}
