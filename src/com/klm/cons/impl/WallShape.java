package com.klm.cons.impl;

import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.geom.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WallShape implements Serializable {

    private float xStart;
    private float yStart;
    private float xEnd;
    private float yEnd;
    private WallShape preWall;
    private WallShape postWall;
    private float thickness;
    private transient float[][] pointsCache;
    private static final long serialVersionUID = 100;

    public WallShape(float xStart, float yStart, float xEnd, float yEnd, float thickness) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.thickness = thickness;
    }

    public float getXStart() {
        return this.xStart;
    }

    public void setXStart(float xStart) {
        if (xStart != this.xStart) {
            this.xStart = xStart;
            clearPointsCache();
        }
    }

    public float getYStart() {
        return this.yStart;
    }

    public void setYStart(float yStart) {
        if (yStart != this.yStart) {
            this.yStart = yStart;
            clearPointsCache();
        }
    }

    public float[] getStartPoint() {
        float[] startPoint = {xStart, yStart};
        return startPoint;
    }

    public float[] getEndPoint() {
        float[] endPoint = {xEnd, yEnd};
        return endPoint;
    }

    public float getXEnd() {
        return this.xEnd;
    }

    public void setXEnd(float xEnd) {
        if (xEnd != this.xEnd) {
            this.xEnd = xEnd;
            clearPointsCache();
        }
    }

    public float getYEnd() {
        return this.yEnd;
    }

    public void setYEnd(float yEnd) {
        if (yEnd != this.yEnd) {
            this.yEnd = yEnd;
            clearPointsCache();
        }
    }

    public WallShape getPreWall() {
        return this.preWall;
    }

    public void setPreWall(WallShape preWall) {
        setPreWall(preWall, true);
    }

    private void setPreWall(WallShape wallAtStart, boolean detachJoinedWallAtStart) {
        if (wallAtStart != this.preWall) {
            WallShape oldWallAtStart = this.preWall;
            this.preWall = wallAtStart;
            clearPointsCache();
            if (detachJoinedWallAtStart) {
                detachJoinedWall(oldWallAtStart);
            }
        }
    }

    public WallShape getPostWall() {
        return this.postWall;
    }

    public void setPostWall(WallShape postWall) {
        setPostWall(postWall, true);
    }

    private void setPostWall(WallShape wallAtEnd, boolean detachJoinedWallAtEnd) {
        if (wallAtEnd != this.postWall) {
            WallShape oldWallAtEnd = this.postWall;
            this.postWall = wallAtEnd;
            clearPointsCache();

            if (detachJoinedWallAtEnd) {
                detachJoinedWall(oldWallAtEnd);
            }
        }
    }

    private void detachJoinedWall(WallShape joinedWall) {
        if (joinedWall != null) {
            if (joinedWall.getPreWall() == this) {
                joinedWall.setPostWall(null, false);
            } else if (joinedWall.getPostWall() == this) {
                joinedWall.setPostWall(null, false);
            }
        }
    }

    public float getThickness() {
        return this.thickness;
    }

    public void setThickness(float thickness) {
        if (thickness != this.thickness) {
            this.thickness = thickness;
            clearPointsCache();
        }
    }

    private void clearPointsCache() {
        this.pointsCache = null;
        if (this.preWall != null) {
            this.preWall.pointsCache = null;
        }
        if (this.postWall != null) {
            this.postWall.pointsCache = null;
        }
    }

    public float[][] getPoints() {
        if (this.pointsCache == null) {
            final float epsilon = 0.01f;
            float[][] wallPoints = getBaseFacePoints();
            int leftSideStartPointIndex = 0;
            int rightSideStartPointIndex = wallPoints.length - 1;
            int leftSideEndPointIndex = wallPoints.length / 2 - 1;
            int rightSideEndPointIndex = wallPoints.length / 2;
            float limit = this.thickness;
            if (this.preWall != null) {
                float[][] wallAtStartPoints = this.preWall.getBaseFacePoints();
                int wallAtStartLeftSideStartPointIndex = 0;
                int wallAtStartRightSideStartPointIndex = wallAtStartPoints.length - 1;
                int wallAtStartLeftSideEndPointIndex = wallAtStartPoints.length / 2 - 1;
                int wallAtStartRightSideEndPointIndex = wallAtStartPoints.length / 2;
                boolean wallAtStartJoinedAtEnd = this.preWall.getPostWall() == this
                        // Check the coordinates when walls are joined to each other at both ends
                        && (this.preWall.getPreWall() != this
                        || (this.preWall.xEnd == this.xStart
                        && this.preWall.yEnd == this.yStart));
                boolean wallAtStartJoinedAtStart = this.preWall.getPreWall() == this
                        // Check the coordinates when walls are joined to each other at both ends
                        && (this.preWall.getPostWall() != this
                        || (this.preWall.xStart == this.xStart
                        && this.preWall.yStart == this.yStart));
                if (wallAtStartJoinedAtEnd) {
                    computeIntersection(wallPoints[leftSideStartPointIndex],
                            wallPoints[leftSideStartPointIndex + 1],
                            wallAtStartPoints[wallAtStartLeftSideEndPointIndex],
                            wallAtStartPoints[wallAtStartLeftSideEndPointIndex - 1], limit);
                    computeIntersection(wallPoints[rightSideStartPointIndex],
                            wallPoints[rightSideStartPointIndex - 1],
                            wallAtStartPoints[wallAtStartRightSideEndPointIndex],
                            wallAtStartPoints[wallAtStartRightSideEndPointIndex + 1], limit);

                    // If the computed start point of this wall and the computed end point of the wall at start
                    // are equal to within epsilon, share the exact same point to avoid computing errors on areas
                    if (this.preWall.pointsCache != null) {
                        if (Math.abs(wallPoints[leftSideStartPointIndex][0] -
                                this.preWall.pointsCache[wallAtStartLeftSideEndPointIndex][0]) < epsilon
                                && Math.abs(wallPoints[leftSideStartPointIndex][1] -
                                this.preWall.pointsCache[wallAtStartLeftSideEndPointIndex][1]) < epsilon) {
                            wallPoints[leftSideStartPointIndex] = this.preWall.pointsCache[wallAtStartLeftSideEndPointIndex];
                        }
                        if (Math.abs(wallPoints[rightSideStartPointIndex][0] -
                                this.preWall.pointsCache[wallAtStartRightSideEndPointIndex][0]) < epsilon
                                && Math.abs(wallPoints[rightSideStartPointIndex][1] -
                                this.preWall.pointsCache[wallAtStartRightSideEndPointIndex][1]) < epsilon) {
                            wallPoints[rightSideStartPointIndex] =
                                    this.preWall.pointsCache[wallAtStartRightSideEndPointIndex];
                        }
                    }
                } else if (wallAtStartJoinedAtStart) {
                    computeIntersection(wallPoints[leftSideStartPointIndex],
                            wallPoints[leftSideStartPointIndex + 1],
                            wallAtStartPoints[wallAtStartRightSideStartPointIndex],
                            wallAtStartPoints[wallAtStartRightSideStartPointIndex - 1], limit);
                    computeIntersection(wallPoints[rightSideStartPointIndex], wallPoints[rightSideStartPointIndex - 1],
                            wallAtStartPoints[wallAtStartLeftSideStartPointIndex],
                            wallAtStartPoints[wallAtStartLeftSideStartPointIndex + 1], limit);

                    // If the computed start point of this wall and the computed start point of the wall at start
                    // are equal to within epsilon, share the exact same point to avoid computing errors on areas
                    if (this.preWall.pointsCache != null) {
                        if (Math.abs(wallPoints[leftSideStartPointIndex][0] -
                                this.preWall.pointsCache[wallAtStartRightSideStartPointIndex][0]) < epsilon
                                && Math.abs(wallPoints[leftSideStartPointIndex][1] -
                                this.preWall.pointsCache[wallAtStartRightSideStartPointIndex][1]) < epsilon) {
                            wallPoints[leftSideStartPointIndex] = this.preWall.pointsCache[wallAtStartRightSideStartPointIndex];
                        }
                        if (this.preWall.pointsCache != null
                                && Math.abs(wallPoints[rightSideStartPointIndex][0] -
                                this.preWall.pointsCache[wallAtStartLeftSideStartPointIndex][0]) < epsilon
                                && Math.abs(wallPoints[rightSideStartPointIndex][1] -
                                this.preWall.pointsCache[wallAtStartLeftSideStartPointIndex][1]) < epsilon) {
                            wallPoints[rightSideStartPointIndex] =
                                    this.preWall.pointsCache[wallAtStartLeftSideStartPointIndex];
                        }
                    }
                }
            }

            // If wall is joined to a wall at its end,
            // compute the intersection between their outlines
            if (this.postWall != null) {
                float[][] wallAtEndPoints = this.postWall.getBaseFacePoints();
                int wallAtEndLeftSideStartPointIndex = 0;
                int wallAtEndRightSideStartPointIndex = wallAtEndPoints.length - 1;
                int wallAtEndLeftSideEndPointIndex = wallAtEndPoints.length / 2 - 1;
                int wallAtEndRightSideEndPointIndex = wallAtEndPoints.length / 2;
                boolean wallAtEndJoinedAtStart = this.postWall.getPreWall() == this
                        // Check the coordinates when walls are joined to each other at both ends
                        && (this.postWall.getPostWall() != this
                        || (this.postWall.xStart == this.xEnd
                        && this.postWall.yStart == this.yEnd));
                boolean wallAtEndJoinedAtEnd = this.postWall.getPostWall() == this
                        // Check the coordinates when walls are joined to each other at both ends
                        && (this.postWall.getPreWall() != this
                        || (this.postWall.xEnd == this.xEnd
                        && this.postWall.yEnd == this.yEnd));
                if (wallAtEndJoinedAtStart) {
                    computeIntersection(wallPoints[leftSideEndPointIndex],
                            wallPoints[leftSideEndPointIndex - 1],
                            wallAtEndPoints[wallAtEndLeftSideStartPointIndex],
                            wallAtEndPoints[wallAtEndLeftSideStartPointIndex + 1], limit);
                    computeIntersection(wallPoints[rightSideEndPointIndex],
                            wallPoints[rightSideEndPointIndex + 1],
                            wallAtEndPoints[wallAtEndRightSideStartPointIndex],
                            wallAtEndPoints[wallAtEndRightSideStartPointIndex - 1], limit);

                    // If the computed end point of this wall and the computed start point of the wall at end
                    // are equal to within epsilon, share the exact same point to avoid computing errors on areas
                    if (this.postWall.pointsCache != null) {
                        if (Math.abs(wallPoints[leftSideEndPointIndex][0] -
                                this.postWall.pointsCache[wallAtEndLeftSideStartPointIndex][0]) < epsilon
                                && Math.abs(wallPoints[leftSideEndPointIndex][1] -
                                this.postWall.pointsCache[wallAtEndLeftSideStartPointIndex][1]) < epsilon) {
                            wallPoints[leftSideEndPointIndex] = this.postWall.pointsCache[wallAtEndLeftSideStartPointIndex];
                        }
                        if (Math.abs(wallPoints[rightSideEndPointIndex][0] -
                                this.postWall.pointsCache[wallAtEndRightSideStartPointIndex][0]) < epsilon
                                && Math.abs(wallPoints[rightSideEndPointIndex][1] -
                                this.postWall.pointsCache[wallAtEndRightSideStartPointIndex][1]) < epsilon) {
                            wallPoints[rightSideEndPointIndex] =
                                    this.postWall.pointsCache[wallAtEndRightSideStartPointIndex];
                        }
                    }
                } else if (wallAtEndJoinedAtEnd) {
                    computeIntersection(wallPoints[leftSideEndPointIndex], wallPoints[leftSideEndPointIndex - 1],
                            wallAtEndPoints[wallAtEndRightSideEndPointIndex],
                            wallAtEndPoints[wallAtEndRightSideEndPointIndex + 1], limit);
                    computeIntersection(wallPoints[rightSideEndPointIndex],
                            wallPoints[rightSideEndPointIndex + 1],
                            wallAtEndPoints[wallAtEndLeftSideEndPointIndex],
                            wallAtEndPoints[wallAtEndLeftSideEndPointIndex - 1], limit);

                    // If the computed end point of this wall and the computed start point of the wall at end
                    // are equal to within epsilon, share the exact same point to avoid computing errors on areas
                    if (this.postWall.pointsCache != null) {
                        if (Math.abs(wallPoints[leftSideEndPointIndex][0] -
                                this.postWall.pointsCache[wallAtEndRightSideEndPointIndex][0]) < epsilon
                                && Math.abs(wallPoints[leftSideEndPointIndex][1] -
                                this.postWall.pointsCache[wallAtEndRightSideEndPointIndex][1]) < epsilon) {
                            wallPoints[leftSideEndPointIndex] = this.postWall.pointsCache[wallAtEndRightSideEndPointIndex];
                        }
                        if (Math.abs(wallPoints[rightSideEndPointIndex][0] -
                                this.postWall.pointsCache[wallAtEndLeftSideEndPointIndex][0]) < epsilon
                                && Math.abs(wallPoints[rightSideEndPointIndex][1] -
                                this.postWall.pointsCache[wallAtEndLeftSideEndPointIndex][1]) < epsilon) {
                            wallPoints[rightSideEndPointIndex] = this.postWall.pointsCache[wallAtEndLeftSideEndPointIndex];
                        }
                    }
                }
            }
            // Cache shape
            this.pointsCache = wallPoints;
        }
        float[][] points = new float[this.pointsCache.length][];
        for (int i = 0; i < this.pointsCache.length; i++) {
            points[i] = this.pointsCache[i].clone();
        }
        return points;
    }

    private float[][] getBaseFacePoints() {
        double angle = Math.atan2(this.yEnd - this.yStart,
                this.xEnd - this.xStart);
        float dx = (float) Math.sin(angle) * this.thickness / 2;
        float dy = (float) Math.cos(angle) * this.thickness / 2;
        return new float[][]{
                {this.xStart + dx, this.yStart - dy},
                {this.xEnd + dx, this.yEnd - dy},
                {this.xEnd - dx, this.yEnd + dy},
                {this.xStart - dx, this.yStart + dy}};
    }

    private void computeIntersection(float[] point1, float[] point2,
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

    public boolean intersectsRectangle(float x0, float y0, float x1, float y1) {
        Rectangle2D rectangle = new Rectangle2D.Float(x0, y0, 0, 0);
        rectangle.add(x1, y1);
        return getShape().intersects(rectangle);
    }

    public boolean containsWallBaseAt(float x, float y, float margin) {
        Line2D baseLine = new Line2D.Float(getXStart(), getYStart(),
                getXEnd(), getYEnd());
        return containsShapeAtWithMargin(baseLine, x, y, margin);
    }

    public boolean containsWallFrontAt(float x, float y, float margin) {
        float[][] wallPoints = getPoints();
        Line2D frontLine = new Line2D.Float(wallPoints[0][0], wallPoints[0][1],
                wallPoints[1][0], wallPoints[1][1]);
        return containsShapeAtWithMargin(frontLine, x, y, margin);
    }

    public boolean containsWallBackAt(float x, float y, float margin) {
        float[][] wallPoints = getPoints();
        Line2D backLine = new Line2D.Float(wallPoints[2][0], wallPoints[2][1],
                wallPoints[3][0], wallPoints[3][1]);
        return containsShapeAtWithMargin(backLine, x, y, margin);
    }

    public boolean containsWallEndAt(float x, float y, float margin) {
        float[][] wallPoints = getPoints();
        Line2D endLine = new Line2D.Float(wallPoints[wallPoints.length / 2 - 1][0], wallPoints[wallPoints.length / 2 - 1][1],
                wallPoints[wallPoints.length / 2][0], wallPoints[wallPoints.length / 2][1]);
        return containsShapeAtWithMargin(endLine, x, y, margin);
    }

    public boolean containsWallStartAt(float x, float y, float margin) {
        float[][] wallPoints = getPoints();
        Line2D startLine = new Line2D.Float(wallPoints[0][0], wallPoints[0][1],
                wallPoints[wallPoints.length - 1][0], wallPoints[wallPoints.length - 1][1]);
        return containsShapeAtWithMargin(startLine, x, y, margin);
    }

    public boolean containsdPoint(float x, float y, float margin) {
        return containsShapeAtWithMargin(getShape(), x, y, margin);
    }

    private boolean containsShapeAtWithMargin(Shape shape, float x, float y, float margin) {
        if (margin == 0) {
            return shape.contains(x, y);
        } else {
            return shape.intersects(x - margin, y - margin, 2 * margin, 2 * margin);
        }
    }

    /**
     * Returns the shape matching this wall.
     */
    public Shape getShape() {
        float[][] wallPoints = getPoints();
        Path2D wallPath = new Path2D.Float();
        wallPath.moveTo(wallPoints[0][0], wallPoints[0][1]);
        for (int i = 1; i < wallPoints.length; i++) {
            wallPath.lineTo(wallPoints[i][0], wallPoints[i][1]);
        }
        wallPath.lineTo(wallPoints[0][0], wallPoints[0][1]);
        return wallPath;
    }

    public float getLength() {
        return (float) Point2D.distance(this.xStart, this.yStart, this.xEnd, this.yEnd);
    }

    public Point3d[] getWallPoint3ds() {
        float[][] wallShapePoints = getPoints();
        final Point3d[] wallPoints = new Point3d[6];
        wallPoints[Wall.FRONT_FACE_START] = new Point3d(wallShapePoints[0][0], 0, wallShapePoints[0][1]);
        wallPoints[Wall.FRONT_FACE_END] = new Point3d(wallShapePoints[1][0], 0, wallShapePoints[1][1]);
        wallPoints[Wall.BACK_FACE_START] = new Point3d(wallShapePoints[3][0], 0, wallShapePoints[3][1]);
        wallPoints[Wall.BACK_FACE_END] = new Point3d(wallShapePoints[2][0], 0, wallShapePoints[2][1]);
        wallPoints[Wall.WALL_BASE_START] = new Point3d(getXStart(), 0, getYStart());
        wallPoints[Wall.WALL_BASE_END] = new Point3d(getXEnd(), 0, getYEnd());
        return wallPoints;

    }

    public boolean isSameWall(WallShape wall) {
        return new Area(getShape()).equals(new Area(wall.getShape()));
    }

    public WallShape coppyWallShape() {
        WallShape coppyWallShape = new WallShape(xStart, yStart, xEnd, yEnd, thickness);
        coppyWallShape.postWall = null;
        coppyWallShape.preWall = null;
        return coppyWallShape;

    }

    public WallShape[] splitWall(float[] splitPoint) {
        boolean joinedAtEndOfWallAtStart =
                preWall != null
                        && preWall.getPostWall() == this;
        boolean joinedAtStartOfWallAtStart =
                preWall != null
                        && preWall.getPreWall() == this;
        boolean joinedAtEndOfWallAtEnd =
                postWall != null
                        && postWall.getPostWall() == this;
        boolean joinedAtStartOfWallAtEnd =
                postWall != null
                        && postWall.getPreWall() == this;

        final WallShape firstWall = coppyWallShape();
        final WallShape secondWall = coppyWallShape();

        firstWall.setXEnd(splitPoint[0]);
        firstWall.setYEnd(splitPoint[1]);
        secondWall.setXStart(splitPoint[0]);
        secondWall.setYStart(splitPoint[1]);
        WallShape[] subWalls = null;
        if (firstWall.getLength() > 0.1 && secondWall.getLength() > 0.1) {
            firstWall.setPreWall(preWall);
            secondWall.setPostWall(postWall);
            if (joinedAtEndOfWallAtStart) {
                preWall.setPostWall(firstWall, false);
            } else if (joinedAtStartOfWallAtStart) {
                preWall.setPreWall(firstWall, false);
            }

            if (joinedAtEndOfWallAtEnd) {
                postWall.setPostWall(secondWall, false);
            } else if (joinedAtStartOfWallAtEnd) {
                postWall.setPreWall(secondWall, false);
            }
            firstWall.setPostWall(secondWall, true);
            secondWall.setPreWall(firstWall, true);
            subWalls = new WallShape[]{firstWall, secondWall};
        }
        return subWalls;
    }

}
