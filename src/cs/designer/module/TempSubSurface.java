package cs.designer.module;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.util.DimensionUtil;
import com.klm.util.RealNumberOperator;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class TempSubSurface {

    private Point3d[] coordinates;
    private double depth = 0;
    private Vector3d baseSurfaceNormal;
    private Surface3D baseSurface;
    private Shape subSurfaceShape;

    public TempSubSurface(final Surface3D baseSurface) {
        this.baseSurface = baseSurface;
        this.baseSurfaceNormal = baseSurface.calculateSurfaceNormal();
        System.out.println("baseSurfaceNormal = "+baseSurfaceNormal);
        if(baseSurfaceNormal.length() == 0){
            System.out.println("Go here");
        }

        /**
         * Modified Time 2013-5-27
         * Modified by Gang Liu
         * Bug fixed : ceiling sub-surface will have opposite normal against it's parent
         *              so ceiling sub-surface causes problem, the following code was added
         *              this code was simply added to prevent ceiling sub-surface problem
         */

        try{
            final Vector3d baseSurfaceNormalNoParent = baseSurface.getNormalToParent();
            if(baseSurfaceNormalNoParent.getY() < 0.0 && baseSurfaceNormal.getZ() < 0.0){
                baseSurfaceNormal.setZ(baseSurfaceNormal.getZ() * -1);
            }
        } catch (final CSHouseException e){
            e.printStackTrace();
        }
    }

    public void setCoordinates(final Point3d[] coordinates) {
        if (coordinates != null && coordinates.length >= 3) {
            this.coordinates = coordinates;
            checkNormal(this.coordinates, baseSurfaceNormal);
            final List<Integer> stripCount = new ArrayList<Integer>();
            stripCount.add(new Integer(this.coordinates.length));
            final int[] stripCounts = new int[stripCount.size()];
            final Point3d[] indicatorShapeCoords = new Point3d[this.coordinates.length];
            for (int index = 0; index < indicatorShapeCoords.length; index++) {
                indicatorShapeCoords[index] = new Point3d(this.coordinates[index].x,
                        this.coordinates[index].y,
                        this.coordinates[index].z + (baseSurfaceNormal.z * 0.01));
            }
            for (int index = 0; index < stripCounts.length; index++) {
                stripCounts[index] = stripCount.get(index).intValue();
            }
            final GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
            gi.setCoordinates(indicatorShapeCoords);
            gi.setStripCounts(stripCounts);
            gi.setContourCounts(new int[]{stripCounts.length});

            final NormalGenerator ng = new NormalGenerator();
            ng.generateNormals(gi);
            final Stripifier sp = new Stripifier();
            sp.stripify(gi);
            baseSurface.getIndicatorShape().setGeometry(gi.getGeometryArray());
            updateSubSurfaceShape();
        }
    }

    public void settranslate(final Transform3D trans) {
        for (Point3d coordinate : coordinates) {
            trans.transform(coordinate);
        }
        setCoordinates(coordinates);
    }

    private void checkNormal(final Point3d[] coordinates, Vector3d baseSurfaceNormal) {
        if (!RealNumberOperator.
                compareTwoTuple3d(DimensionUtil.getNormal(coordinates), baseSurfaceNormal)) {
            final Stack<Point3d> memo = new Stack<Point3d>();
            for (int index = 0; index < coordinates.length; index++) {
                memo.push(coordinates[index]);
            }
            for (int index = 0; index < coordinates.length; index++) {
                coordinates[index] = memo.pop();
            }
        }
    }

    private void updateSubSurfaceShape() {
        if (coordinates.length >= 3) {
            final Path2D path = new Path2D.Double();
            path.moveTo(coordinates[0].x, coordinates[0].y);
            for (int index = 1; index < coordinates.length; index++) {
                path.lineTo(coordinates[index].x, coordinates[index].y);
            }
            subSurfaceShape = path;
        }
    }

    public Point3d[] getCoordinates() {
        return this.coordinates;
    }


    public void setDepth(float depth) {
        this.depth = depth * baseSurfaceNormal.z;
        final Transform3D depthTransform3D = new Transform3D();
        depthTransform3D.set(new Vector3d(0, 0, this.depth));
        for (Point3d coordinate : coordinates) {
            depthTransform3D.transform(coordinate);
        }

    }

    public double getDepth() {
        return depth;
    }

    public double getLingth() {
        BoundingBox bous = (BoundingBox) baseSurface.getIndicatorShape().getBounds();
        Point3d lower = new Point3d();
        Point3d upper = new Point3d();
        bous.getLower(lower);
        bous.getUpper(upper);
        return (upper.x - lower.x);

    }

    public double getwidth() {
        BoundingBox bous = (BoundingBox) baseSurface.getIndicatorShape().getBounds();
        Point3d lower = new Point3d();
        Point3d upper = new Point3d();
        bous.getLower(lower);
        bous.getUpper(upper);
        return (upper.y - lower.y);

    }

    public Surface3D getBaseSurface() {
        return baseSurface;
    }

    public Shape getSubSurfaceShape() {
        return subSurfaceShape;
    }

    public void clear() {
        Point3d[] coordinates = null;
        depth = 0;
        baseSurface.getIndicatorShape().setGeometry(new Shape3D().getGeometry());
        subSurfaceShape = null;
    }
}
