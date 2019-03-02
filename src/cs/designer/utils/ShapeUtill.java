/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.designer.utils;

import com.klm.util.RealNumberOperator;

import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author rongyang
 */
public class ShapeUtill {

    public static List<Shape> getChildPath(Shape shape) {
        Path2D temp = new Path2D.Double();
        Point2d moveToPoint = null;
        List<Shape> childList = new ArrayList<Shape>();
        if (null != shape) {
            PathIterator pi = shape.getPathIterator(null);
            while (!pi.isDone()) {
                double[] current = new double[2];
                int type = pi.currentSegment(current);
                switch (type) {
                    case PathIterator.SEG_MOVETO:
                        temp = new Path2D.Double();
                        childList.add(temp);
                        moveToPoint = new Point2d(current[0], current[1]);
                        temp.moveTo(current[0], current[1]);
                        break;
                    case PathIterator.SEG_LINETO:
                        temp.lineTo(current[0], current[1]);
                        break;
                    case PathIterator.SEG_CLOSE:
                        temp.lineTo(moveToPoint.x, moveToPoint.y);
                        break;

                }
                pi.next();
            }
        }
        return childList;
    }

    public static Shape translateShape(final Shape shape) {
        Path2D temp = new Path2D.Float();
        Set<Shape> shapes = new HashSet<Shape>();
        Point2f moveToPoint = null;
        Point2f lineToPoint = null;
        Point2f lastLineToPoint = null;
        int countPoint = 0;
        if (null != shape) {
            PathIterator pi = shape.getPathIterator(null, 0.08);
            while (!pi.isDone()) {
                float[] current = new float[3];
                int type = pi.currentSegment(current);
                switch (type) {
                    case PathIterator.SEG_MOVETO:
                        temp = new Path2D.Float();
                        moveToPoint = new Point2f(current[0], current[1]);
                        temp.moveTo(current[0], current[1]);
                        countPoint++;
                        break;
                    case PathIterator.SEG_LINETO:
                        lineToPoint = new Point2f(current[0], current[1]);
                        if (!moveToPoint.equals(lineToPoint)
                                && !lineToPoint.equals(lastLineToPoint)) {
                            temp.lineTo(current[0], current[1]);
                            countPoint++;
                            lastLineToPoint = lineToPoint;
                        }
                        break;
                    case PathIterator.SEG_CLOSE:
                        temp.lineTo(moveToPoint.x, moveToPoint.y);
                        break;
                }
                if (countPoint > 3) {
                    shapes.add(shape);
                }
                pi.next();
            }

        }
        for (final Shape subShape : shapes) {
            if (subShape.getBounds2D().getHeight() * subShape.getBounds2D().getWidth()
                    > temp.getBounds2D().getHeight() * temp.getBounds2D().getWidth()) {
                temp = new Path2D.Float(subShape);
            }
        }
        return temp;
    }

    public static Shape createRectangle(final Point3d startPoint, final Point3d endPoint) {
        double xStart = startPoint.x;
        double yStart = startPoint.y;
        double xEnd = endPoint.x;
        double yEnd = endPoint.y;
        if (startPoint.x > endPoint.x) {
            xStart = endPoint.x;
            xEnd = startPoint.x;
        }
        if (startPoint.y > endPoint.y) {
            yStart = endPoint.y;
            yEnd = startPoint.y;
        }
        double w = Math.abs(xEnd - xStart);
        double h = Math.abs(yEnd - yStart);
        Rectangle2D.Double rectangle =
                new Rectangle2D.Double(xStart, yStart, w, h);
        return rectangle;
    }

    public static Shape createEllipse(final Point3d startPoint, final Point3d endPoint) {
        double xStart = startPoint.x;
        double yStart = startPoint.y;
        double xEnd = endPoint.x;
        double yEnd = endPoint.y;
        if (startPoint.x > endPoint.x) {
            xStart = endPoint.x;
            xEnd = startPoint.x;
        }
        if (startPoint.y > endPoint.y) {
            yStart = endPoint.y;
            yEnd = startPoint.y;
        }
        double w = xEnd - xStart;
        double h = yEnd - yStart;
        Ellipse2D.Double ellipse =
                new Ellipse2D.Double(xStart, yStart, w, h);
        return ellipse;
    }

    public static Shape createPolygon(final Point3d startPoint,
                                      final Point3d endPoint, int npoints) {
        double xStart = startPoint.x;
        double yStart = startPoint.y;
        double xEnd = endPoint.x;
        double yEnd = endPoint.y;
        if (startPoint.x > endPoint.x) {
            xStart = endPoint.x;
            xEnd = startPoint.x;
        }
        if (startPoint.y > endPoint.y) {
            yStart = endPoint.y;
            yEnd = startPoint.y;
        }
        double w = xEnd - xStart;
        double h = yEnd - yStart;
        return getPolygonPoints(npoints, (xStart + xEnd) / 2, (yEnd + yStart) / 2, w);
    }

    private static Shape getPolygonPoints(int npoints, double centX, double centY, double width) {
        double angle = 2 * Math.PI / npoints;
        double startAngle = (Math.PI - angle) / 2;
        Path2D path = new Path2D.Float();
        path.moveTo(centX + width * Math.cos(startAngle),
                centY + width * Math.sin(startAngle));
        for (int i = 1; i < npoints; i++) {
            path.lineTo(centX + width * Math.cos(startAngle + i * angle),
                    centY + width * Math.sin(startAngle + i * angle));
        }
        path.lineTo(centX + width * Math.cos(startAngle),
                centY + width * Math.sin(startAngle));

        return path;
    }

    public final static void showShapePath(final Shape shape) {
        PathIterator pi = shape.getPathIterator(null);
        while (!pi.isDone()) {
            float[] current = new float[6];
            int type = pi.currentSegment(current);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    System.out.println("move to " + current[0] + ", " + current[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    System.out.println("line to " + current[0] + ", " + current[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    System.out.println("quadratic to " + current[0] + ", " + current[1]
                            + ", " + current[2] + ", " + current[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    System.out.println("cubic to " + current[0] + ", " + current[1] + ", "
                            + current[2] + ", " + current[3] + ", " + current[4] + ", " + current[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    System.out.println("close");
                    break;
            }
            pi.next();

        }
        System.out.println("**************************************");
    }

    public static Point2D getIntersectPoint(Line2D l1, Line2D l2) {

        double x = ((l1.getX1() - l1.getX2()) * (l2.getX1() * l2.getY2() - l2.getX2() * l2.getY1()) - (l2.getX1() - l2.getX2()) * (l1.getX1() * l1.getY2() - l1.getX2() * l1.getY1()))
                / ((l2.getX1() - l2.getX2()) * (l1.getY1() - l1.getY2()) - (l1.getX1() - l1.getX2()) * (l2.getY1() - l2.getY2()));

        double y = ((l1.getY1() - l1.getY2()) * (l2.getX1() * l2.getY2() - l2.getX2() * l2.getY1()) - (l1.getX1() * l1.getY2() - l1.getX2() * l1.getY1()) * (l2.getY1() - l2.getY2()))
                / ((l1.getY1() - l1.getY2()) * (l2.getX1() - l2.getX2()) - (l1.getX1() - l1.getX2()) * (l2.getY1() - l2.getY2()));

        System.out.println("他们的交点为: (" + x + "," + y + ")");
        return new Point2D.Double(x, y);
    }

    public static Line2D getShapeLines(Shape shape) {
        Point2D p1 = new Point2D.Float();
        Point2D p2 = new Point2D.Float();
        if (null != shape) {
            PathIterator pi = shape.getPathIterator(null, 1.0);
            while (pi.isDone() == false) {
                float[] current = new float[3];
                int type = pi.currentSegment(current);
                switch (type) {
                    case PathIterator.SEG_MOVETO:
                        p1 = new Point2D.Float(current[0], current[1]);
                        break;
                    case PathIterator.SEG_LINETO:
                        p2 = new Point2D.Float(current[0], current[1]);
                        break;
                }
                pi.next();
            }
        }
        return new Line2D.Float(p1, p2);
    }


    public static Shape getInsertPointPath(final Shape shape, final Point2D insertPoint) {
        Path2D path = new Path2D.Double();
        Point2D starPoint = new Point2D.Float();
        Point2D endPoint = new Point2D.Float();
        PathIterator pi = shape.getPathIterator(null);
        while (!pi.isDone()) {
            float[] current = new float[2];
            pi.currentSegment(current);
            int type = pi.currentSegment(current);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    path.moveTo(current[0], current[1]);
                    starPoint = new Point2D.Float(current[0], current[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    endPoint = new Point2D.Float(current[0], current[1]);
                    if (starPoint.distance(endPoint) < starPoint.distance(insertPoint)) {
                        path.lineTo(current[0], current[1]);
                    } else {
                        path.lineTo(insertPoint.getX(), insertPoint.getY());
                    }
                    path.lineTo(insertPoint.getX(), insertPoint.getY());

                    break;
            }
            pi.next();
        }
        return path;

    }
}
