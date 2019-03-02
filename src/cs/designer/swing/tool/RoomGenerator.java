package cs.designer.swing.tool;

import com.klm.cons.impl.*;
import com.klm.persist.impl.LocalStorage;
import com.klm.persist.impl.SurfaceMaterial;
import com.klm.persist.meta.BufferedImageMeta;
import com.klm.util.RealNumberOperator;
import cs.designer.swing.resources.ResourcesPath;
import cs.designer.swing.undo.HouseEdit;
import cs.designer.swing.undo.RoomBuilderUndoEditor;
import cs.designer.utils.ShapeUtill;
import cs.designer.view.viewer.HousePlanView;

import javax.imageio.ImageIO;
import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author rongyang
 */
public class RoomGenerator implements Generatorable {
    private final static Color3f FLOOR_COLOR = new Color3f(new Color(120, 120, 120));
    private final static Color3f CEILING_COLOR = new Color3f(new Color(168, 168, 168));
    public final static Material FLOOR_MATERIAL =
            new Material(FLOOR_COLOR, FLOOR_COLOR, FLOOR_COLOR, FLOOR_COLOR, 1);
    public final static Material CEILING_MATERIAL =
            new Material(CEILING_COLOR, CEILING_COLOR, CEILING_COLOR, CEILING_COLOR, 60);
    private List<Shape> roomContours;
    private HousePlanView view;
    public static int roomCount = 1;


    public RoomGenerator(final HousePlanView view) {
        roomContours = new ArrayList<Shape>();
        this.view = view;
        createRoom();
    }

    private void createRoom() {
        final Area area = new Area();
        BasicStroke basicStroke = new BasicStroke(1E-3F);
        for (Wall wall : view.getFloorWalls()) {
            area.add(new Area(basicStroke.createStrokedShape(new Line2D.Float(wall.getWallShape().getXStart(),
                    wall.getWallShape().getYStart(), wall.getWallShape().getXEnd(), wall.getWallShape().getYEnd()))));
        }
        roomContours = ShapeUtill.getChildPath(area);
        roomContours.remove(roomContours.size() - 1);

    }

    private Set<Surface3D> getRoomSurfaces(Shape roomcurrent) {
        final Set<Surface3D> roomSurfaces = new HashSet<Surface3D>();
        for (Wall tempWall : view.getFloorWalls()) {
            float[][] wallPoints = tempWall.getWallShape().getPoints();
            if (containsShapeAtWithMargin(roomcurrent, wallPoints[0][0], wallPoints[0][1], 1E-3f)
                    && containsShapeAtWithMargin(roomcurrent, wallPoints[1][0], wallPoints[1][1], 1E-3f)) {
                roomSurfaces.add(tempWall.getSurface(Wall.FRONT_SURFACE_INDEX));
            } else if (containsShapeAtWithMargin(roomcurrent, wallPoints[2][0], wallPoints[2][1], 1E-3f)
                    && containsShapeAtWithMargin(roomcurrent, wallPoints[3][0], wallPoints[3][1], 1E-3f)) {
                roomSurfaces.add(tempWall.getSurface(Wall.BACK_SURFACE_INDEX));
            }
        }
        return roomSurfaces;
    }

    public Shape selectRoom(Point3d selectPoint) {
        for (Shape shape : roomContours) {
            if (containsShapeAtWithMargin(shape, selectPoint.getX(),
                    selectPoint.getZ(), 0)) {
                return shape;
            }
        }
        return null;
    }

    private Room buildRoom(String roomName, Shape roomCurrent)
            throws CSHouseException {
        Room buildRoom = new Room(roomName, getRoomSurfaces(roomCurrent), translateroomContourPoints(roomCurrent));
        view.getPropertyPanel().setPropertys(buildRoom);
        buildRoom.removeChild(buildRoom.getCeilingDown());
        buildRoom.getFloorUp().setSurfaceAppMaterial(FLOOR_MATERIAL);
        buildRoom.getCeilingDown().setSurfaceAppMaterial(CEILING_MATERIAL);
        return buildRoom;
    }

    private boolean containsShapeAtWithMargin(final Shape shape,
                                              double x, double y, float margin) {
        if (margin == 0) {
            return shape.contains(x, y);
        } else {
            return shape.intersects(x - margin, y - margin, 2 * margin, 2 * margin);
        }
    }

    private Point3d[] translateroomContourPoints(Shape roomContour) {
        final PathIterator pi = roomContour.getPathIterator(null);
        final List<Point3d> points = new ArrayList<Point3d>();
        final double[] coords = new double[2];
        Point3d lastPoint = new Point3d();
        Set<Point3d> pointSet = new HashSet<Point3d>();
        while (!pi.isDone()) {
            final int type = pi.currentSegment(coords);
            if (type == PathIterator.SEG_LINETO) {
                Point3d currentPoint = new Point3d(RealNumberOperator.roundNumber(coords[0], 4), 0,
                        RealNumberOperator.roundNumber(coords[1], 4));
                if (lastPoint.distance(currentPoint) > 0.0001 &&
                        !pointSet.contains(currentPoint)) {
                    pointSet.add(currentPoint);
                    points.add(currentPoint);
                }
                lastPoint = currentPoint;
            }
            pi.next();
        }
        Point3d[] roomContourPoints = new Point3d[points.size()];

        for (int i = 0; i < roomContourPoints.length; i++) {
            roomContourPoints[i] = points.get(i);

        }
        return roomContourPoints;
    }

    public void mousePressed(final Point3d mousePoint, final Floor currentFloor) {
        try {
            Shape selectRoomContour = selectRoom(mousePoint);
            if (selectRoomContour != null) {
                Room newRoom = buildRoom("房间" + roomCount, selectRoomContour);
                currentFloor.addRoom(newRoom);
                HouseEdit.getHouseEditor().joinObject(new RoomBuilderUndoEditor(view.getCurrentFloor(), newRoom));
                roomCount++;
                roomContours.remove(selectRoomContour);
                selectRoomContour = null;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void reset() {
        roomContours.clear();
    }
}
