/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author gang-liu
 */
public class DimensionUtil {

    public static final int PRECISION = 7;
    public static final Vector3d X_AXES_D = new Vector3d(1.0, 0.0, 0.0);
    public static final Vector3d Y_AXES_D = new Vector3d(0.0, 1.0, 0.0);
    public static final Vector3d Z_AXES_D = new Vector3d(0.0, 0.0, 1.0);
    public static final Vector3d Z_AXES_NEGA_D = new Vector3d(0.0, 0.0, -1.0);
    public static final Vector3f X_AXES_F = new Vector3f(1f, 0f, 0f);
    public static final Vector3f Y_AXES_F = new Vector3f(0f, 1f, 0f);
    public static final Vector3f Z_AXES_F = new Vector3f(0f, 0f, 1f);
    public static final Vector3f Z_AXES_NEGA_F = new Vector3f(0f, 0f, -1f);
    public static final Transform3D REVERSE_Y = new Transform3D();

    static {
        REVERSE_Y.rotY(Math.PI);
    }

    /*
     * Generates Transform that makes V to be transformed to (0.0, 0.0, 1.0);
     */
    public static Transform3D getTransformToZ(final Vector3f v) throws
            CSUtilException {
        final Matrix3f r = createRotationMatrix(v);
        final Transform3D trans = new Transform3D(r, new Vector3f(0f, 0f, 0f),
                1f);
        final Vector3f testV = new Vector3f(v.x, v.y, v.z);
        trans.transform(testV);
        if (!RealNumberOperator.compareTwoTuple3f(Z_AXES_F, testV)) {
            throw new CSUtilException("Not able to calculate Transform for " +
                    v.toString() + " to (0.0, 0.0, 1.0) result is " + testV);
        }
        return trans;
    }

    public static Transform3D getTransformToNegaZ(final Vector3f v) throws
            CSUtilException {
        final Matrix3f r = createRotationMatrix(v);
        final Transform3D trans = new Transform3D(r, new Vector3f(0f, 0f, 0f),
                1f);
        final Transform3D addTrans = new Transform3D();
        addTrans.rotY(Math.PI);
        addTrans.mul(trans);
        final Vector3f testV = new Vector3f(v.x, v.y, v.z);
        addTrans.transform(testV);
        if (!RealNumberOperator.compareTwoTuple3f(Z_AXES_NEGA_F, testV)) {
            throw new CSUtilException("Not able to calculate Transform for " +
                    v.toString() + " to (0.0, 0.0, -1.0) result is " + testV);
        }
        return addTrans;
    }

    /*
     * Returns Transform3D that rotates this Vector from it's current position
     * to (0.0, 0.0, 1.0)
     */
    public static Transform3D getTransformToZ(final Vector3d v) throws
            CSUtilException {
        v.normalize();
        if (RealNumberOperator.compareTwoTuple3d(v, Z_AXES_D)) {
            return new Transform3D();
        }
        final Matrix3d r = createRotationMatrix(v);
        final Transform3D trans = new Transform3D(r, new Vector3d(0.0, 0.0, 0.0),
                1.0);
        final Vector3d testV = new Vector3d(v.x, v.y, v.z);
        trans.transform(testV);
        if (!RealNumberOperator.compareTwoTuple3d(Z_AXES_D, testV)) {
            throw new CSUtilException("Not able to calculate Transform for " +
                    v.toString() + " to (0.0, 0.0, -1.0) result is " + testV);
        }
        return trans;
    }

    public static Transform3D getTransformToNegaZ(final Vector3d v) throws
            CSUtilException {
        v.normalize();
        if (RealNumberOperator.compareTwoTuple3d(v, Z_AXES_NEGA_D)) {
            final Transform3D trans = new Transform3D();
            trans.rotY(Math.PI);
            return trans;
        }
        final Matrix3d r = createRotationMatrix(v);
        final Transform3D trans = new Transform3D(r, new Vector3d(0.0, 0.0, 0.0),
                1.0);
        final Transform3D addTrans = new Transform3D();
        addTrans.rotY(Math.PI);
        addTrans.mul(trans);
        final Vector3d testV = new Vector3d(v.x, v.y, v.z);
        addTrans.transform(testV);
        if (!RealNumberOperator.compareTwoTuple3d(testV, Z_AXES_NEGA_D)) {
            throw new CSUtilException("Not able to calculate Transform for " +
                    v.toString() + " to (0.0, 0.0, -1.0) result is " + testV);
        }
        return addTrans;
    }

    public static Matrix3f createRotationMatrix(final Vector3f LOS) throws
            CSUtilException {
        LOS.normalize();
        final Matrix3f m = new Matrix3f();
        final float[] row3 = createRowThree(LOS);
        m.setM20(row3[0]);
        m.setM21(row3[1]);
        m.setM22(row3[2]);

        float[] row2 = createRowTwo(Y_AXES_F, LOS);
        if (row2 == null) {
            row2 = createRowTwo(Z_AXES_F, LOS);
            if (row2 == null) {
                row2 = createRowTwo(X_AXES_F, LOS);
            }
        }
        if (row2 == null) {
            throw new CSUtilException("Can not form a transformation matrix in case LOS is " +
                    LOS.toString());
        } else {
            m.setM10(row2[0]);
            m.setM11(row2[1]);
            m.setM12(row2[2]);
        }

        final float[] row1 = createRowOne(row2, row3);
        m.setM00(row1[0]);
        m.setM01(row1[1]);
        m.setM02(row1[2]);

        return m;
    }

    public static Matrix3d createRotationMatrix(final Vector3d LOS) throws
            CSUtilException {
        LOS.normalize();
        final Matrix3d m = new Matrix3d();
        final double[] row3 = createRowThree(LOS);
        m.setM20(row3[0]);
        m.setM21(row3[1]);
        m.setM22(row3[2]);

        double[] row2 = createRowTwo(Y_AXES_D, LOS);
        if (row2 == null) {
            row2 = createRowTwo(Z_AXES_D, LOS);
            if (row2 == null) {
                row2 = createRowTwo(X_AXES_D, LOS);
            }
        }
        if (row2 == null) {
            throw new CSUtilException("Can not form a transformation matrix in case LOS is " +
                    LOS.toString());
        } else {
            m.setM10(row2[0]);
            m.setM11(row2[1]);
            m.setM12(row2[2]);
        }

        final double[] row1 = createRowOne(row2, row3);
        m.setM00(row1[0]);
        m.setM01(row1[1]);
        m.setM02(row1[2]);

        return m;
    }

    private static float[] createRowThree(final Vector3f LOS) {
        final float magnitude = LOS.length();
        final float[] ret = new float[]{LOS.x / magnitude, LOS.y / magnitude,
                LOS.z /
                        magnitude};
        return ret;
    }

    private static double[] createRowThree(final Vector3d LOS) {
        final double magnitude = LOS.length();
        final double[] ret = new double[]{LOS.x / magnitude, LOS.y / magnitude,
                LOS.z /
                        magnitude};
        return ret;
    }

    private static float[] createRowTwo(final Vector3f upw, final Vector3f out) {
        final float d = out.dot(upw) / upw.length();
        final float[] ret = new float[]{(upw.x - d * out.x), (upw.y - d * out.y),
                (upw.z - d *
                        out.z)};
        if ((ret[0] * ret[0] + ret[1] * ret[1] + ret[2] * ret[2]) == 0f) {
            return null;
        } else {
            final Vector3f temp = new Vector3f(ret[0], ret[1], ret[2]);
            temp.normalize();
            ret[0] = temp.x;
            ret[1] = temp.y;
            ret[2] = temp.z;
            return ret;
        }
    }

    private static double[] createRowTwo(final Vector3d upw, final Vector3d out) {
        final double d = out.dot(upw) / upw.length();
        final double[] ret = new double[]{(upw.x - d * out.x), (upw.y - d *
                out.y), (upw.z - d *
                out.z)};
        if ((ret[0] * ret[0] + ret[1] * ret[1] + ret[2] * ret[2]) == 0f) {
            return null;
        } else {
            final Vector3d temp = new Vector3d(ret[0], ret[1], ret[2]);
            temp.normalize();
            ret[0] = temp.x;
            ret[1] = temp.y;
            ret[2] = temp.z;
            return ret;
        }
    }

    private static float[] createRowOne(final float[] row1, final float[] row2) {
        final Vector3f r1V = new Vector3f(row1);
        final Vector3f r2V = new Vector3f(row2);
        final Vector3f retV = new Vector3f();
        retV.cross(r1V, r2V);
        retV.normalize();
        final float[] ret = new float[]{retV.x, retV.y, retV.z};
        return ret;
    }

    private static double[] createRowOne(final double[] row1,
                                         final double[] row2) {
        final Vector3d r1V = new Vector3d(row1);
        final Vector3d r2V = new Vector3d(row2);
        final Vector3d retV = new Vector3d();
        retV.cross(r1V, r2V);
        retV.normalize();
        final double[] ret = new double[]{retV.x, retV.y, retV.z};
        return ret;
    }

    /*
     * returns null if the given points are not on the same plane
     * or the Vector3d of indicate the nromal of the plane these points form
     */
    public static Vector3f getNormal(final Point3f[] points) {

        if ((points == null) || (points.length < 3)) {
            return null;
        }
        Vector3f ret = null;
        for (int index = 0; index < points.length; index++) {
            final int lower = (index == 0) ? points.length - 1 : index - 1;
            final int upper = (index == (points.length - 1)) ? 0 : index + 1;
            final Vector3f v1 = new Vector3f((points[lower].x - points[index].x),
                    (points[lower].y - points[index].y),
                    (points[lower].z - points[index].z));
            final Vector3f v2 = new Vector3f((points[upper].x - points[index].x),
                    (points[upper].y - points[index].y),
                    (points[upper].z - points[index].z));


            v1.cross(v2, v1);
            v1.normalize();
            RealNumberOperator.roundVector3f(v1,
                    RealNumberOperator.FLOAT_PRECISION);

            if (ret == null) {
                ret = v1;
            } else {
                if (!ret.equals(v1)) {
                    return null;
                }
            }
        }
        return ret;
    }

    public static Vector3d getNormal(final Point3d[] points){

        final GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setCoordinates(points);
        gi.setStripCounts(new int[]{points.length});
        final NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gi);

        final Vector3f[] normals = gi.getNormals();
        for(int index = 1; index < normals.length; index++){
            if(!RealNumberOperator.compareTwoTuple3f(normals[0], normals[index])){
                return null;
            }
        }
        return new Vector3d(normals[0].x, normals[0].y, normals[0].z);

    }

//    public static Vector3d getNormal(final Point3d[] points) {
//
//        if ((points == null) || (points.length < 3)) {
//            return null;
//        }
//        Vector3d ret = null;
//        for (int index = 0; index < points.length; index++) {
//            final int lower = (index == 0) ? points.length - 1 : index - 1;
//            final int upper = (index == (points.length - 1)) ? 0 : index + 1;
//            final Vector3d v1 = new Vector3d((points[lower].x - points[index].x),
//                    (points[lower].y - points[index].y),
//                    (points[lower].z - points[index].z));
//            final Vector3d v2 = new Vector3d((points[upper].x - points[index].x),
//                    (points[upper].y - points[index].y),
//                    (points[upper].z - points[index].z));
//
//            v1.cross(v1, v2);
//            if (v1.length() == 0) {
//                continue;
//            }
//            v1.normalize();
//
//            RealNumberOperator.roundVector3d(v1,
//                    RealNumberOperator.DOUBLE_PRECISION);
//
//
//            if (ret == null) {
//                ret = v1;
//            } else {
//                if (!ret.equals(v1)) {
//                    return null;
//                }
//            }
//        }
//        return ret;
//    }

    public static Vector3f getNormal(final Shape3D shape) {
        final GeometryInfo gi = new GeometryInfo((GeometryArray) shape.
                getGeometry());
        Vector3f ret = gi.getNormals()[0];
        if (ret == null) {
            final NormalGenerator ng = new NormalGenerator();
            ng.generateNormals(gi);
        }
        ret = gi.getNormals()[0];
        return ret;
    }

    public static Shape3D createPlane(final Point3f[] points) throws
            CSUtilException {
        return createPlane(points, PolygonAttributes.CULL_NONE);
    }

    public static Shape3D createPlane(final Point3f[] points, final int culling)
            throws
            CSUtilException {
        final GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setCoordinates(points);
        gi.setStripCounts(new int[]{points.length});

        final NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gi);
        final Stripifier sp = new Stripifier();
        sp.stripify(gi);

        final Vector3f[] normals = gi.getNormals();
        final Vector3f normal = normals[0];
        for (int i = 1; i < normals.length; i++) {
            if (!normal.equals(normals[i])) {
                throw new CSUtilException("Points are not on the same plane");
            }
        }
        gi.setStripCounts(new int[]{points.length});

        final Appearance app = new Appearance();
        final Material m = new Material();
        m.setAmbientColor(new Color3f(Color.WHITE));
        m.setDiffuseColor(new Color3f(Color.WHITE));
        m.setEmissiveColor(new Color3f(Color.WHITE));
        app.setMaterial(m);
        final PolygonAttributes pa = new PolygonAttributes();
        pa.setCullFace(culling);
        pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        app.setPolygonAttributes(pa);

        final Shape3D ret = new Shape3D(gi.getGeometryArray(), app);
        return ret;
    }

    public static Point3f[] create3DPoints(List<float[]> coords) {
        final Point3f[] ret = new Point3f[coords.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new Point3f(coords.get(i)[0], coords.get(i)[1], 0f);
        }
        return ret;
    }

    public static boolean hasSubPath(final Shape inputShape) {
        final PathIterator pi = inputShape.getPathIterator(null);
        final double[] data = new double[6];
        int moveToCnt = 0;
        while (!pi.isDone()) {
            if (pi.currentSegment(data) == PathIterator.SEG_MOVETO) {
                moveToCnt++;
                if (moveToCnt > 1) {
                    return true;
                }
            }
            pi.next();
        }
        return false;
    }

    public static Set<Shape> getSubPath(final Shape inputShape) {
        final Set<Shape> ret = new HashSet<Shape>();
        final PathIterator pi = inputShape.getPathIterator(null);
        final double[] coords = new double[6];
        GeneralPath retShape = null;
        while (pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    retShape = new GeneralPath();
                    break;
                case PathIterator.SEG_CLOSE:
                    if (retShape != null) {
                        retShape.closePath();
                        ret.add(retShape);
                    }
                    break;
                case PathIterator.SEG_CUBICTO:
                    if (retShape != null) {
                        retShape.curveTo(coords[0], coords[1], coords[2],
                                coords[3], coords[4], coords[5]);
                    }
                    break;
                case PathIterator.SEG_QUADTO:
                    if (retShape != null) {
                        retShape.quadTo(coords[0], coords[1], coords[2],
                                coords[3]);
                    }
                    break;
                case PathIterator.SEG_LINETO:
                    if (retShape != null) {
                        retShape.lineTo(coords[0], coords[1]);
                    }
                    break;
                default:
                    break;
            }
            pi.next();
        }
        return ret;
    }

    public static Point3d[] shapeToPoint3d(final Shape shape) throws
            CSUtilException {
        final PathIterator pi = shape.getPathIterator(null, 1.0);
        final List<Point3d> coordList = new ArrayList<Point3d>();
        final double[] coords = new double[2];
        while (!pi.isDone()) {
            final int movement = pi.currentSegment(coords);
            if (movement == PathIterator.SEG_LINETO) {
                coordList.add(new Point3d(coords[0], coords[1], 0.0));
            }
            pi.next();
        }
        final Point3d[] coordinates = new Point3d[coordList.size()];
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = coordList.get(i);
        }
        return coordinates;
    }

    public static Shape point3DToShape(final Point3d[] points) throws
            CSUtilException {
        if (points.length < 3) {
            throw new CSUtilException("Points does not form a Plane");
        }
        final Path2D ret = new Path2D.Double();
        ret.moveTo(points[0].x, points[0].y);
        final double z = RealNumberOperator.roundNumber(points[0].z,
                RealNumberOperator.FLOAT_PRECISION);
        for (int index = 1; index < points.length; index++) {
            final double pz = RealNumberOperator.roundNumber(points[index].z,
                    RealNumberOperator.FLOAT_PRECISION);
            if (pz != z) {
                throw new CSUtilException(
                        "This Plane does not paralle to XY plane -- dirty data exists " +
                                pz + " against " + z);
            }
            ret.lineTo(points[index].x, points[index].y);
        }
        ret.closePath();
        return ret;
    }

    public static double calculateShape(final Shape shape, final double flatness) throws
            CSUtilException {
        final PathIterator pi =
                new FlatteningPathIterator(shape.getPathIterator(null), flatness);
        final double[] currentData = new double[6];
        double previousData[] = null;
        double ret = 0.0;
        while (pi.isDone()) {
            switch (pi.currentSegment(currentData)) {
                case PathIterator.SEG_MOVETO | PathIterator.SEG_LINETO:
                    if (previousData == null) {
                        previousData = new double[2];
                    } else {
                        ret += previousData[0] * currentData[1] - currentData[0] *
                                previousData[1];
                    }
                    previousData[0] = currentData[0];
                    previousData[1] = currentData[1];
                    break;
                case PathIterator.SEG_CUBICTO | PathIterator.SEG_QUADTO:
                    throw new CSUtilException(
                            "Pathe contains information rather than line to");
                case PathIterator.SEG_CLOSE:
                    break;
            }
            pi.next();
        }
        ret /= 2;
        return Math.abs(ret);
    }

    public static void reversePoints(final Point3d[] coords) {
        if (coords != null){
            Point3d temp;
            for (int start = 0, end = coords.length - 1; start < end; start++, end--) {
                temp = coords[start];
                coords[start] = coords[end];
                coords[end] = temp;
            }
        }
    }

    public static Point3d getCenter(final Point3d[] coords) {
        Point3d lower = new Point3d(coords[0]);
        Point3d upper = new Point3d(coords[0]);
        for (final Point3d coord : coords) {
            if (coord.x < lower.x) {
                lower.x = coord.x;
            } else if (coord.x > upper.x) {
                upper.x = coord.x;
            }
            if (coord.y < lower.y) {
                lower.y = coord.y;
            } else if (coord.y > upper.y) {
                upper.y = coord.y;
            }
            if (coord.z < lower.z) {
                lower.z = coord.z;
            } else if (coord.z > upper.z) {
                upper.z = coord.z;
            }
        }
        final Point3d center = new Point3d();
        center.add(lower, upper);
        center.scale(.5);
        return center;
    }

    public void reverseArray(final Point3d[] points) {
        Point3d temp;
        for (int start = 0, end = points.length - 1; start < points.length; start++, end--) {
            temp = points[start];
            points[start] = points[end];
            points[end] = temp;
        }
    }

    public static void main(String[] args) {
        final Point3d[] coords = {
                new Point3d(-1.0, 0.0, -1.0), new Point3d(1.0, 0.0, -1.0),
                new Point3d(1.0, 0.0, 1.0), new Point3d(-1.0, 0.0, 1.0)
        };
        final Vector3d normal = getNormal(coords);
        System.out.println("Normal = " + normal);
    }

}
