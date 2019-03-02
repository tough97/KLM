/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.math.BigDecimal;

/**
 *
 * @author gang-liu
 */
public class RealNumberOperator {

    public static final int FLOAT_PRECISION = 4;
    public static final int DOUBLE_PRECISION = 10;

    public static float roundNumber(final float number, final int precision) {
        BigDecimal bd = new BigDecimal(number);
        return bd.setScale(precision, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static double roundNumber(final double number, final int precision) {
        if (Double.isNaN(number)) {
            System.out.println("NaN");
        } else if (Double.isInfinite(number)) {
            System.out.println("Infinite");
        }
        BigDecimal bd = new BigDecimal(number);
        return bd.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static int getIntegerDigitCount(final float value) {
        int castedValue = (int) value;
        castedValue = Math.abs(castedValue);
        int ret = 1;
        while (castedValue > 10) {
            castedValue /= 10;
            ret++;
        }
        return ret;
    }

    public static Tuple3f roundTuple3f(final Tuple3f nums, final int precision) {
        final float newX = roundNumber(nums.x, precision);
        final float newY = roundNumber(nums.y, precision);
        final float newZ = roundNumber(nums.z, precision);
        nums.setX(newX);
        nums.setY(newY);
        nums.setZ(newZ);
        return nums;
    }

    public static Tuple3d roundTuple3d(final Tuple3d nums, final int precision) {
        final double newX = roundNumber(nums.x, precision);
        final double newY = roundNumber(nums.y, precision);
        final double newZ = roundNumber(nums.z, precision);
        nums.setX(newX);
        nums.setY(newY);
        nums.setZ(newZ);
        return nums;
    }

    public static float[] roundArray(final float[] arr, final int precision) {
        for (int index = 0; index < arr.length; index++) {
            arr[index] = roundNumber(arr[index], precision);
        }
        return arr;
    }

    public static Shape roundShape(final Shape shape, final int precision) {
        final PathIterator pi = shape.getPathIterator(null, 1.0);
        Path2D ret = null;
        float[] coords = new float[2];
        while (!pi.isDone()) {
            pi.currentSegment(coords);
            coords = roundArray(coords, precision);
            if (ret == null) {
                ret = new Path2D.Float();
                ret.moveTo(coords[0], coords[1]);
            } else {
                ret.lineTo(coords[0], coords[1]);
            }
            pi.next();
        }
        return ret;
    }

    public static Vector3f roundVector3f(final Vector3f vector,
            final int precision) {
        vector.setX(roundNumber(vector.getX(), precision));
        vector.setY(roundNumber(vector.getY(), precision));
        vector.setZ(roundNumber(vector.getZ(), precision));
        return vector;
    }

    public static Vector3d roundVector3d(final Vector3d vector,
            final int precision) {
        vector.setX(roundNumber(vector.getX(), precision));
        vector.setY(roundNumber(vector.getY(), precision));
        vector.setZ(roundNumber(vector.getZ(), precision));
        return vector;
    }

    public static Point2D roundPoint2D(final Point2D point, final int precision) {
        point.setLocation(roundNumber(point.getX(), precision),
                roundNumber(point.getY(), precision));
        return point;
    }

    public static boolean compareTwoTuple3f(final Tuple3f t1, final Tuple3f t2) {
        if (roundNumber(t1.x, FLOAT_PRECISION) != roundNumber(t2.x,
                FLOAT_PRECISION)) {
            return false;
        } else if (roundNumber(t1.y, FLOAT_PRECISION) != roundNumber(t2.y,
                FLOAT_PRECISION)) {
            return false;
        } else {
            return roundNumber(t1.z, FLOAT_PRECISION) == roundNumber(t2.z,
                    FLOAT_PRECISION);
        }
    }

    public static boolean compareTwoTuple3d(final Tuple3d t1, final Tuple3d t2) {
        if (roundNumber(t1.x, FLOAT_PRECISION) != roundNumber(t2.x,
                FLOAT_PRECISION)) {
            return false;
        } else if (roundNumber(t1.y, FLOAT_PRECISION) != roundNumber(t2.y,
                FLOAT_PRECISION)) {
            return false;
        } else {
            return roundNumber(t1.z, FLOAT_PRECISION) == roundNumber(t2.z,
                    FLOAT_PRECISION);
        }
    }

    public static boolean compareTwoDouble(final double num1, final double num2) {
        return RealNumberOperator.roundNumber(num1, FLOAT_PRECISION) ==
                RealNumberOperator.roundNumber(num2, FLOAT_PRECISION);
    }

    public static boolean compareTwoFloat(final float num1, final float num2) {
        return RealNumberOperator.roundNumber(num1, FLOAT_PRECISION) ==
                RealNumberOperator.roundNumber(num2, FLOAT_PRECISION);
    }

    public static void main(String[] args) {
    }
}
