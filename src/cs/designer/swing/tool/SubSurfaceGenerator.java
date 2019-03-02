package cs.designer.swing.tool;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.cons.impl.Wall;
import com.klm.util.CSUtilException;
import cs.designer.module.Pan;
import cs.designer.module.TempSubSurface;
import cs.designer.swing.undo.HoleInUnDoEditor;
import cs.designer.swing.undo.HouseEdit;
import cs.designer.swing.undo.Surface3DInUndoEditor;
import cs.designer.utils.ShapeUtill;
import cs.designer.view.viewer.HousePlanView;

import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rongyang
 */
public class SubSurfaceGenerator implements Generatorable {

    private Surface3D baseSurface;
    private Point3d startPoint;
    private Point3d endPoint;
    private TempSubSurface currentTempSubSurface;
    private HousePlanView view;

    public SubSurfaceGenerator(HousePlanView view) {
        this.view = view;
    }

    public void setEndPoint(Point3d endPoint) throws CSHouseException {
        if (startPoint != null) {
            this.endPoint = endPoint;
            currentTempSubSurface.setCoordinates(createFacePoints(startPoint, endPoint));
            view.getPropertyPanel().setPropertys(currentTempSubSurface);
        }
    }

    public TempSubSurface getCurrentTempSubSurface() {
        return currentTempSubSurface;
    }

    public void translateSubSurface(Transform3D trans) {
        if (this.currentTempSubSurface != null) {
            currentTempSubSurface.settranslate(trans);
            trans.transform(startPoint);
            trans.transform(endPoint);
        }

    }

    public void setBaseSurface(Surface3D baseSurface) {
        this.baseSurface = baseSurface;
        this.currentTempSubSurface = new TempSubSurface(baseSurface);
    }

    public void setStartPoint(Point3d startPoint) {
        this.startPoint = startPoint;
    }


    public void toSubSurface() throws CSHouseException, CSUtilException {
        if (currentTempSubSurface.getCoordinates() != null
                && currentTempSubSurface.getCoordinates().length >= 3) {
            final Point3d[] coordinates = currentTempSubSurface.getCoordinates();
            Surface3D subSurface = new Surface3D(coordinates);
            if (baseSurface.testSubSurface(subSurface)) {
                baseSurface.addSubSurface(subSurface);
                final Color3f color = new Color3f(new Color(153, 153, 153));
                subSurface.setSurfaceAppMaterial(new Material(color, color, color, color, 60));
                if (currentTempSubSurface.getDepth() != 0) {
                    subSurface.createConnectiveSurfaces();
                    for (final Surface3D connective : subSurface.getConnectiveSurfaces()) {
                        connective.setSurfaceAppMaterial(new Material(color, color, color, color, 60));

                    }
                }
                currentTempSubSurface.clear();
                HouseEdit.getHouseEditor().joinObject(new Surface3DInUndoEditor(subSurface));
            }
        }

    }

    public void toHole() throws CSHouseException, CSUtilException {
        Wall wall = (Wall) baseSurface.getFirstParentOf(Wall.class);
        if (wall != null) {
            final Surface3D holeFace = wall.drillHole(currentTempSubSurface.
                    getCoordinates(), baseSurface);
            if (holeFace != null) {
                currentTempSubSurface.clear();
                HouseEdit.getHouseEditor().joinObject(new HoleInUnDoEditor(wall, holeFace, wall.getSurfaceFlag(baseSurface)));
            }

        }
    }

    public Point3d getStartPoint() {
        return startPoint;
    }

    public Point3d getEndPoint() {
        return endPoint;
    }

    public void reSizeUp(double yChange) {
        if (startPoint != null && endPoint != null) {

            this.endPoint.setY(endPoint.y + yChange);
            try {
                currentTempSubSurface.setCoordinates(createFacePoints(startPoint, endPoint));
                view.getPropertyPanel().setPropertys(currentTempSubSurface);
            } catch (CSHouseException e) {
                e.printStackTrace();
            }

        }


    }

    public void reSizeDown(double yChange) {
        if (startPoint != null && endPoint != null) {
            this.startPoint.setY(startPoint.y + yChange);
            try {
                currentTempSubSurface.setCoordinates(createFacePoints(startPoint, endPoint));
                view.getPropertyPanel().setPropertys(currentTempSubSurface);
            } catch (CSHouseException e) {
                e.printStackTrace();
            }

        }
    }

    public void reSizeLeft(double xChange) {
        if (startPoint != null && endPoint != null) {
            this.endPoint.setX(endPoint.x + xChange);
            try {
                currentTempSubSurface.setCoordinates(createFacePoints(startPoint, endPoint));
                view.getPropertyPanel().setPropertys(currentTempSubSurface);
            } catch (CSHouseException e) {
                e.printStackTrace();
            }

        }
    }

    public void reSizeRight(double xChange) {
        if (startPoint != null && endPoint != null) {
            this.startPoint.setX(startPoint.x + xChange);
            try {
                currentTempSubSurface.setCoordinates(createFacePoints(startPoint, endPoint));
                view.getPropertyPanel().setPropertys(currentTempSubSurface);
            } catch (CSHouseException e) {
                e.printStackTrace();
            }

        }
    }

    private Point3d[] createFacePoints(final Point3d startPoint,
                                       final Point3d endPoint) throws CSHouseException {
        Point3d[] facePoints = null;
        Shape drawShape = null;
        if (Pan.getPan().getPanType() == Pan.PanType.RECTAMGLE) {
            drawShape = ShapeUtill.createRectangle(startPoint, endPoint);

        } else if (Pan.getPan().getPanType() == Pan.PanType.ELLIPSE) {
            drawShape = ShapeUtill.createEllipse(startPoint, endPoint);

        } else if (Pan.getPan().getPanType() == Pan.PanType.POLYGON) {
            drawShape = ShapeUtill.createPolygon(startPoint, endPoint, Pan.getPan().getPolygonSideNmb());

        }
        if (testSubSurfaceInParent(drawShape)) {
            facePoints = translateShape(drawShape);
        }

        return facePoints;
    }

    private boolean testSubSurfaceInParent(Shape subSurfaceShape)
            throws CSHouseException {

        return baseSurface.getIdentifiedShape().
                contains(subSurfaceShape.getBounds2D());

    }

    private Point3d[] translateShape(final Shape shape) {
        final List<Point3d> points = new ArrayList<Point3d>();
        if (null != shape) {
            PathIterator pi = shape.getPathIterator(null, 1E-3F);
            while (!pi.isDone()) {
                double[] current = new double[2];
                if (pi.currentSegment(current) ==
                        PathIterator.SEG_LINETO) {
                    points.add(new Point3d(current[0], current[1], 0));
                }
                pi.next();
            }
        }
        final Point3d[] shapePoints = new Point3d[points.size()];

        for (int i = 0; i < shapePoints.length; i++) {
            shapePoints[i] = points.get(i);

        }
        return shapePoints;
    }


    public void reset() {
        Surface3D baseSurface = null;
        Point3d startPoint = null;
        TempSubSurface currentTempSubSurface = null;
    }

    public Shape getDrawShape() {
        Shape drawShape = null;
        if (currentTempSubSurface != null) {
            drawShape = currentTempSubSurface.getSubSurfaceShape();
        }
        return drawShape;
    }
}
