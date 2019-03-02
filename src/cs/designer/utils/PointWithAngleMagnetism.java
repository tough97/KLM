package cs.designer.utils;

import com.klm.util.RealNumberOperator;

public class PointWithAngleMagnetism {

    private static final int STEP_COUNT = 24; // 15 degrees step
    private float x;
    private float y;
    private double angle1;

    public PointWithAngleMagnetism(float xStart, float yStart, float x, float y,
                                   float maxLengthDelta) {
        this.x = x;
        this.y = y;
        if (xStart == x) {
            // Apply magnetism to the length of the line joining start point to magnetized point
            this.y = yStart + (float) (maxLengthDelta * Math.signum(y - yStart));
        } else if (yStart == y) {
            // Apply magnetism to the length of the line joining start point to magnetized point
            this.x = xStart + (float) (maxLengthDelta * Math.signum(x - xStart));
        } else { // xStart != x && yStart != y
            double angleStep = 2 * Math.PI / STEP_COUNT;
            // Caution : pixel coordinate space is indirect !
            double angle = Math.atan2(yStart - y, x - xStart);
            // Compute previous angle closest to a step angle (multiple of angleStep)
            double previousStepAngle = Math.floor(angle / angleStep) * angleStep;
            double tanAngle1;
            double angle2;
            double tanAngle2;
            // Compute the tan of previousStepAngle and the next step angle
            if (Math.tan(angle) > 0) {
                angle1 = previousStepAngle;
                tanAngle1 = Math.tan(previousStepAngle);
                angle2 = previousStepAngle + angleStep;
                tanAngle2 = Math.tan(previousStepAngle + angleStep);
            } else {
                // If slope is negative inverse the order of the two angles
                angle1 = previousStepAngle + angleStep;
                tanAngle1 = Math.tan(previousStepAngle + angleStep);
                angle2 = previousStepAngle;
                tanAngle2 = Math.tan(previousStepAngle);
            }
            // Search in the first quarter of the trigonometric circle,
            // the point (xEnd1,yEnd1) or (xEnd2,yEnd2) closest to point
            // (xEnd,yEnd) that belongs to angle 1 or angle 2 radius
            double firstQuarterTanAngle1 = Math.abs(tanAngle1);
            double firstQuarterTanAngle2 = Math.abs(tanAngle2);
            float xEnd1 = Math.abs(xStart - x);
            float yEnd2 = Math.abs(yStart - y);
            float xEnd2 = 0;
            // If angle 2 is greater than 0 rad
            if (firstQuarterTanAngle2 > 1E-10) {
                // Compute the abscissa of point 2 that belongs to angle 1 radius at
                // y2 ordinate
                xEnd2 = (float) (yEnd2 / firstQuarterTanAngle2);
            }
            float yEnd1 = 0;
            // If angle 1 is smaller than PI / 2 rad
            if (firstQuarterTanAngle1 < 1E10) {
                // Compute the ordinate of point 1 that belongs to angle 1 radius at
                // x1 abscissa
                yEnd1 = (float) (xEnd1 * firstQuarterTanAngle1);
            }

            // Apply magnetism to the smallest distance
            double magnetismAngle;
            if (Math.abs(xEnd2 - xEnd1) < Math.abs(yEnd1 - yEnd2)) {
                magnetismAngle = angle2;
                this.x = xStart + (float) ((yStart - y) / tanAngle2);
            } else {
                magnetismAngle = angle1;
                this.y = yStart - (float) ((x - xStart) * tanAngle1);
            }
            this.x = (float) RealNumberOperator.roundNumber(xStart + (maxLengthDelta * Math.cos(magnetismAngle)), 6);
            this.y = (float) RealNumberOperator.roundNumber(yStart - (float) (maxLengthDelta * Math.sin(magnetismAngle)), 6);
        }
    }

    /**
     * Returns the abscissa of end point computed with magnetism.
     */
    public float getX() {
        return this.x;
    }

    /**
     * Returns the ordinate of end point computed with magnetism.
     */
    public float getY() {
        return this.y;
    }

    public Integer getAngle1(float xStart, float yStart, float xEnd, float yEnd) {
        int angle = (int) Math.round(Math.toDegrees(Math.atan2(
                yStart - yEnd, xEnd - xStart)));
        return angle;

    }


}
