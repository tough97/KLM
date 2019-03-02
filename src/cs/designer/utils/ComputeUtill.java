/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.designer.utils;

import java.awt.geom.Point2D;

/**
 * @author rongyang
 */
public class ComputeUtill {
    public static float[] computeIntersection(float[] point1,
                                              float[] point2, float[] point3, float[] point4) {
        float x = point2[0];
        float y = point2[1];
        float alpha1 = (point2[1] - point1[1]) / (point2[0] - point1[0]);
        float alpha2 = (point4[1] - point3[1]) / (point4[0] - point3[0]);
        if (alpha1 != alpha2) {
            // If first line is vertical
            if (Math.abs(alpha1) > 1E5) {
                if (Math.abs(alpha2) < 1E5) {
                    x = point1[0];
                    float beta2 = point4[1] - alpha2 * point4[0];
                    y = alpha2 * x + beta2;
                }
                // If second line is vertical
            } else if (Math.abs(alpha2) > 1E5) {
                if (Math.abs(alpha1) < 1E5) {
                    x = point3[0];
                    float beta1 = point2[1] - alpha1 * point2[0];
                    y = alpha1 * x + beta1;
                }
            } else {
                boolean sameSignum = Math.signum(alpha1) == Math.signum(alpha2);
                if ((sameSignum && (Math.abs(alpha1) > Math.abs(alpha2) ? alpha1 / alpha2 : alpha2 / alpha1) > 1.0001)
                        || (!sameSignum && Math.abs(alpha1 - alpha2) > 1E-5)) {
                    float beta1 = point2[1] - alpha1 * point2[0];
                    float beta2 = point4[1] - alpha2 * point4[0];
                    x = (beta2 - beta1) / (alpha1 - alpha2);
                    y = alpha1 * x + beta1;
                }
            }
        }
        return new float[]{x, y};
    }

    public static float[] getIntersection(float[] point1,
                                          float[] point2, float[] point3,
                                          float[] point4, float limint) {
        float x = point2[0];
        float y = point2[1];
        float alpha1 = (point2[1] - point1[1]) / (point2[0] - point1[0]);
        float alpha2 = (point4[1] - point3[1]) / (point4[0] - point3[0]);
        if (alpha1 != alpha2) {
            // If first line is vertical
            if (Math.abs(alpha1) > limint) {
                if (Math.abs(alpha2) < limint) {
                    x = point1[0];
                    float beta2 = point4[1] - alpha2 * point4[0];
                    y = alpha2 * x + beta2;
                }
                // If second line is vertical
            } else if (Math.abs(alpha2) > limint) {
                if (Math.abs(alpha1) < limint) {
                    x = point3[0];
                    float beta1 = point2[1] - alpha1 * point2[0];
                    y = alpha1 * x + beta1;
                }
            } else {
                boolean sameSignum = Math.signum(alpha1) == Math.signum(alpha2);
                if ((sameSignum && (Math.abs(alpha1) > Math.abs(alpha2) ? alpha1 / alpha2 : alpha2 / alpha1) > 1.0001)
                        || (!sameSignum && Math.abs(alpha1 - alpha2) > 1E-5)) {
                    float beta1 = point2[1] - alpha1 * point2[0];
                    float beta2 = point4[1] - alpha2 * point4[0];
                    x = (beta2 - beta1) / (alpha1 - alpha2);
                    y = alpha1 * x + beta1;
                }
            }
        }
        return new float[]{x, y};
    }


    public static void computeIntersection(float[] point1, float[] point2,
                                           float[] point3, float[] point4, float limit) {
        float alpha1 = (point2[1] - point1[1]) / (point2[0] - point1[0]);
        float alpha2 = (point4[1] - point3[1]) / (point4[0] - point3[0]);
        // If the two lines are not parallel
        if (alpha1 != alpha2) {
            float x = point1[0];
            float y = point1[1];

            // If first line is vertical
            if (Math.abs(alpha1) > 1E5) {
                if (Math.abs(alpha2) < 1E5) {
                    x = point1[0];
                    float beta2 = point4[1] - alpha2 * point4[0];
                    y = alpha2 * x + beta2;
                }
                // If second line is vertical
            } else if (Math.abs(alpha2) > 1E5) {
                if (Math.abs(alpha1) < 1E5) {
                    x = point3[0];
                    float beta1 = point2[1] - alpha1 * point2[0];
                    y = alpha1 * x + beta1;
                }
            } else {
                boolean sameSignum = Math.signum(alpha1) == Math.signum(alpha2);
                if ((sameSignum && (Math.abs(alpha1) > Math.abs(alpha2) ? alpha1 / alpha2 : alpha2 / alpha1) > 1.0001)
                        || (!sameSignum && Math.abs(alpha1 - alpha2) > 1E-5)) {
                    float beta1 = point2[1] - alpha1 * point2[0];
                    float beta2 = point4[1] - alpha2 * point4[0];
                    x = (beta2 - beta1) / (alpha1 - alpha2);
                    y = alpha1 * x + beta1;
                }
            }
            if (Point2D.distanceSq(x, y, point1[0], point1[1]) < limit * limit) {
                point1[0] = x;
                point1[1] = y;
            }
        }
    }

}
