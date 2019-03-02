/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;

import com.klm.material.impl.MaterialColor;
import com.klm.util.CSUtilException;
import com.klm.util.DimensionUtil;
import com.klm.util.impl.MerchandiseInfo;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author gang-liu
 */
public class Wall extends CSBranchGroup {

    public static final int FRONT_SURFACE_INDEX = 0;
    public static final int BACK_SURFACE_INDEX = 1;
    public static final int TOP_SURFACE_INDEX = 2;
    public static final int BOTTOM_SURFACE_INDEX = 3;
    public static final int LEFT_SURFACE_INDEX = 4;
    public static final int RIGHT_SURFACE_INDEX = 5;
    public static final double WALL_DEFAULT_HEIGHT = 3.2;
    public static final int WALL_BASE_START = 0;
    public static final int WALL_BASE_END = 1;
    public static final int FRONT_FACE_START = 2;
    public static final int FRONT_FACE_END = 3;
    public static final int BACK_FACE_START = 4;
    public static final int BACK_FACE_END = 5;
    private static double wallHeight = WALL_DEFAULT_HEIGHT;

    private static final MaterialColor DEFAULT_WALL_COLOR = new MaterialColor(new Color(0x333333), new Color(0xffffff), new Color(0x555555));

    static Map<Integer, Wall> systemWallLookup = new HashMap<Integer, Wall>();

    public static int getWallCnt() {
        return systemWallLookup.size();
    }

    static Wall getWall(final int wallID) throws CSHouseException {
        final Wall ret = systemWallLookup.get(new Integer(wallID));
        if (ret == null) {
            throw new CSHouseException("Wall record is not found in System " + ret.toString() + " with " + ret.getWallID());
        }
        return ret;
    }

    private int wallID;
    private double wallWidth = 0.0;
    private TransformGroup wallTransTG;
    private Map<Surface3D, Surface3D> holeMap;
    private Set<Room> referedRoom = new HashSet<Room>();
    private WallShape wallShape;

    private static final long serialVersionUaID = 100;

    public static double getWallHeight() {
        return wallHeight;
    }

    public static void setWallHeight(final double height) {
        wallHeight = height;
    }

    public Wall() {
        initiate();
        init();
        setWallID(systemWallLookup.size());
        systemWallLookup.put(new Integer(getWallID()), this);
    }

    public Wall(final WallShape wallShape) throws CSHouseException, CSUtilException {
        this.wallShape = wallShape;
        init();
        setWallPoints(wallShape.getWallPoint3ds());
        setWallID(systemWallLookup.size());
        systemWallLookup.put(new Integer(getWallID()), this);
    }

    public Wall(final Point3d[] wallPoints) throws CSHouseException,
            CSUtilException, IOException {
        init();
        setWallPoints(wallPoints);
        setWallID(systemWallLookup.size());
        systemWallLookup.put(new Integer(getWallID()), this);
    }

    public static void cleanSystemWallLookUp(){
        systemWallLookup = new HashMap<Integer, Wall>();
    }

    private void setWallID(final int wallID) {
        this.wallID = wallID;
    }

    public void setWallShape(final WallShape walLShape) throws CSHouseException, CSUtilException {
        this.wallShape = walLShape;
        setWallPoints(walLShape.getWallPoint3ds());
    }

    public WallShape getWallShape() {
        return wallShape;
    }

    public void setWallPoints(final Point3d[] wallPoints) throws CSHouseException, CSUtilException {
        parseWorldCoordinates(wallPoints);
        createSurfaces(wallPoints);

        final Line2D line = new Line2D.Double(wallPoints[FRONT_FACE_START].x,
                wallPoints[FRONT_FACE_START].z, wallPoints[FRONT_FACE_END].x,
                wallPoints[FRONT_FACE_END].z);
        final Point2D point = new Point2D.Double(wallPoints[BACK_FACE_START].x,
                wallPoints[BACK_FACE_START].z);
        wallWidth = line.ptLineDist(point);
    }

    public Transform3D getWallTransform3D() {
        final Transform3D ret = new Transform3D();
        wallTransTG.getTransform(ret);
        return ret;
    }

    public Transform3D getWallRotation() {
        final Matrix3d matrix = new Matrix3d();
        getWallTransform3D().get(matrix);
        return new Transform3D(matrix, new Vector3d(0, 0, 0), 1.0);
    }

    public Transform3D getWallTranslation() {
        final Vector3d trans = new Vector3d();
        getWallTransform3D().get(trans);
        final Transform3D ret = new Transform3D();
        ret.setTranslation(trans);
        return ret;
    }

    public int getWallID() {
        return wallID;
    }

    public Integer getSurfaceFlag(final Surface3D surface) {
        for (int index = 0; index < wallTransTG.numChildren(); index++) {
            if (wallTransTG.getChild(index) == surface) {
                return new Integer(index);
            }
        }
        return null;
    }

    public Surface3D getSurface(final int surfaceFlag) {
        return (Surface3D) wallTransTG.getChild(surfaceFlag);
    }

    public Surface3D drillHole(final Point3d[] coords, final Surface3D frontSurface) throws
            CSHouseException, CSUtilException {
        long start = System.currentTimeMillis();
        long end;

        final Integer surfaceFlag = getSurfaceFlag(frontSurface);
        if (surfaceFlag != null) {
            final Surface3D backSurface = surfaceFlag.intValue() ==
                    Wall.FRONT_SURFACE_INDEX ? getSurface(
                    Wall.BACK_SURFACE_INDEX) : getSurface(
                    Wall.FRONT_SURFACE_INDEX);
            final Surface3D front = new Surface3D(coords);
            final Transform3D frontTransform3D = ((CSTransformGroup) frontSurface.getInitTransTG()).getTransform();
            final Transform3D backTransform3D = ((CSTransformGroup) backSurface.getInitTransTG()).getTransform();

            backTransform3D.invert();
            for (final Point3d point3d : coords) {
                frontTransform3D.transform(point3d);
                backTransform3D.transform(point3d);
                point3d.setZ(0);
            }

            final Surface3D back = new Surface3D(coords);
            if (frontSurface.testSubSurface(front) &&
                    backSurface.testSubSurface(back)) {
                final Vector3d frontTrans = new Vector3d(0.0, 0.0, wallWidth *
                        (frontSurface.calculateSurfaceNormal().z < 0.0 ? 0.51 :
                                -0.51));
                final Vector3d backTrans = new Vector3d(0.0, 0.0, wallWidth *
                        (backSurface.calculateSurfaceNormal().z < 0.0 ? 0.51 :
                                -0.51));
                frontSurface.addSubSurface(
                        front);
                backSurface.addSubSurface(back);
                front.setCustomizable(true);
                front.getCutomzableTG().addTranslation(frontTrans);
                front.setCustomizable(false);
                front.createConnectiveSurfaces();
                front.setVisible(false);
                back.setCustomizable(true);
                back.getCutomzableTG().addTranslation(backTrans);
                back.setCustomizable(false);
                back.createConnectiveSurfaces();                
                back.setVisible(false);

                for(final Surface3D conn : front.getConnectiveSurfaces()){
                    renderDefaultWallSurface(conn);
                }
                for(final Surface3D conn : back.getConnectiveSurfaces()){
                    renderDefaultWallSurface(conn);
                }
                holeMap.put(front, back);
                return front;
            }
        }
        return null;
    }

    public int getNumOfHoles() {
        return holeMap.size();
    }

    public boolean removeHole(final Surface3D holeSurface) {
        for (final Surface3D keyHoleSurface : holeMap.keySet()) {
            if ((keyHoleSurface == holeSurface) ||
                    holeMap.get(keyHoleSurface) == holeSurface) {
                keyHoleSurface.detach();
                holeMap.get(keyHoleSurface).detach();
                holeMap.remove(keyHoleSurface);
                try {
                    ((Surface3D) wallTransTG.getChild(FRONT_SURFACE_INDEX)).updateSurface();
                    ((Surface3D) wallTransTG.getChild(BACK_SURFACE_INDEX)).updateSurface();
                } catch (Exception ex) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public Surface3D getOppositionSurface(final Surface3D holeSurface) {
        for (final Surface3D keyHoleSurface : holeMap.keySet()) {
            if (keyHoleSurface == holeSurface) {
                return holeMap.get(keyHoleSurface);
            } else if (holeMap.get(keyHoleSurface) == holeSurface) {
                return keyHoleSurface;
            }
        }
        return null;
    }

    public void removeAllSurfaces() {
        wallTransTG.removeAllChildren();
    }

    public Set<Room> getReferedRoom() {
        return referedRoom;
    }

    public void addRoomReference(final Room room) {
        referedRoom.add(room);
    }

    public void removeReferenceRoom(final Room room) {
        referedRoom.remove(room);
    }

    public MerchandiseInfo getMerchandiseInfo() throws CSHouseException {
        final MerchandiseInfo ret = new MerchandiseInfo();
        ret.addMerchandiseCounter(getSurface(Wall.FRONT_SURFACE_INDEX).getMerchandiseInfo());
        ret.addMerchandiseCounter(getSurface(Wall.BACK_SURFACE_INDEX).getMerchandiseInfo());
        return ret;
    }

    /*
    * Protected Methods==========================================================
    */
    protected void createSurfaces(final Point3d[] wallPoints) throws CSHouseException {
        wallTransTG.removeAllChildren();
        Surface3D surface;
        /*
         * Create front Surface
         */
        Point3d[] coords = {
                new Point3d(wallPoints[FRONT_FACE_START]),
                new Point3d(wallPoints[FRONT_FACE_START].x,
                        wallPoints[FRONT_FACE_START].y + wallHeight,
                        wallPoints[FRONT_FACE_START].z),
                new Point3d(wallPoints[FRONT_FACE_END].x,
                        wallPoints[FRONT_FACE_END].y + wallHeight,
                        wallPoints[FRONT_FACE_END].z),
                new Point3d(wallPoints[FRONT_FACE_END])
        };
        surface = new Surface3D(coords);
        surface.setLightenColor(DEFAULT_WALL_COLOR);
        renderDefaultWallSurface(surface);
        wallTransTG.insertChild(surface, FRONT_SURFACE_INDEX);

        /*
         * Create back Surface
         */

        coords = new Point3d[]{
                new Point3d(wallPoints[BACK_FACE_END]),
                new Point3d(wallPoints[BACK_FACE_END].x,
                        wallPoints[BACK_FACE_END].y +
                                wallHeight, wallPoints[BACK_FACE_END].z),
                new Point3d(wallPoints[BACK_FACE_START].x,
                        wallPoints[BACK_FACE_START].y + wallHeight,
                        wallPoints[BACK_FACE_START].z),
                new Point3d(wallPoints[BACK_FACE_START])
        };
        surface = new Surface3D(coords);
        surface.setLightenColor(DEFAULT_WALL_COLOR);
        renderDefaultWallSurface(surface);
        wallTransTG.insertChild(surface, BACK_SURFACE_INDEX);

        /*
         * Creates Top Surface
         */
        coords = new Point3d[]{
                new Point3d(wallPoints[BACK_FACE_START].x,
                        wallPoints[BACK_FACE_START].y + wallHeight,
                        wallPoints[BACK_FACE_START].z),
                new Point3d(wallPoints[BACK_FACE_END].x,
                        wallPoints[BACK_FACE_END].y +
                                wallHeight, wallPoints[BACK_FACE_END].z),
                new Point3d(wallPoints[FRONT_FACE_END].x,
                        wallPoints[FRONT_FACE_END].y + wallHeight,
                        wallPoints[FRONT_FACE_END].z),
                new Point3d(wallPoints[FRONT_FACE_START].x,
                        wallPoints[FRONT_FACE_START].y + wallHeight,
                        wallPoints[FRONT_FACE_START].z),};
        surface = new Surface3D(coords);
        surface.setLightenColor(DEFAULT_WALL_COLOR);
        surface.getSurfaceAppMaterial().setLightingEnable(false);
        wallTransTG.insertChild(surface, TOP_SURFACE_INDEX);

        /*
         * Create Bottom Surface
         */
        coords = new Point3d[]{
                new Point3d(wallPoints[BACK_FACE_START]),
                new Point3d(wallPoints[FRONT_FACE_START]),
                new Point3d(wallPoints[FRONT_FACE_END]),
                new Point3d(wallPoints[BACK_FACE_END])
        };
        surface = new Surface3D(coords);
        surface.setLightenColor(DEFAULT_WALL_COLOR);
        wallTransTG.insertChild(surface, BOTTOM_SURFACE_INDEX);


        /*
         * Create Left Surface
         */
        coords = new Point3d[]{
                new Point3d(wallPoints[FRONT_FACE_START]),
                new Point3d(wallPoints[BACK_FACE_START]),
                new Point3d(wallPoints[BACK_FACE_START].x,
                        wallPoints[BACK_FACE_START].y + wallHeight,
                        wallPoints[BACK_FACE_START].z),
                new Point3d(wallPoints[FRONT_FACE_START].x,
                        wallPoints[FRONT_FACE_START].y + wallHeight,
                        wallPoints[FRONT_FACE_START].z),};
        surface = new Surface3D(coords);
        surface.setLightenColor(DEFAULT_WALL_COLOR);
        renderDefaultWallSurface(surface);
        wallTransTG.insertChild(surface, LEFT_SURFACE_INDEX);

        /*
         * Create Right Surface
         */
        coords = new Point3d[]{
                new Point3d(wallPoints[BACK_FACE_END]),
                new Point3d(wallPoints[FRONT_FACE_END]),
                new Point3d(wallPoints[FRONT_FACE_END].x,
                        wallPoints[FRONT_FACE_END].y + wallHeight,
                        wallPoints[FRONT_FACE_END].z),
                new Point3d(wallPoints[BACK_FACE_END].x,
                        wallPoints[BACK_FACE_END].y +
                                wallHeight, wallPoints[BACK_FACE_END].z)
        };
        surface = new Surface3D(coords);
        surface.setLightenColor(DEFAULT_WALL_COLOR);
        renderDefaultWallSurface(surface);
        wallTransTG.insertChild(surface, RIGHT_SURFACE_INDEX);
    }

    protected void renderDefaultWallSurface(final Material material) {
        material.setAmbientColor(new Color3f(Color.white));
        material.setColorTarget(Material.AMBIENT);
        material.setLightingEnable(true);
    }

    static void renderDefaultWallSurface(final Surface3D surface){
        final Material surfaceMaterial = surface.getSurfaceAppMaterial() == null ?
                new Material() : surface.getSurfaceAppMaterial();
        surfaceMaterial.setLightingEnable(true);
        surfaceMaterial.setEmissiveColor(new Color3f(new Color(0x777777)));
        surfaceMaterial.setSpecularColor(new Color3f(new Color(0xaaaaaa)));
        surfaceMaterial.setColorTarget(Material.AMBIENT_AND_DIFFUSE);
        if(surface.getSurfaceAppMaterial() == null){
            surface.getRawShapeAppearance().setMaterial(surfaceMaterial);
        }
    }

    protected void parseWorldCoordinates(final Point3d[] wallPoints) throws CSUtilException {

        final Vector3d wallHVector = getVector(wallPoints[WALL_BASE_START], wallPoints[WALL_BASE_END]);
        final Vector3d wallVVector = getVector(wallPoints[WALL_BASE_START], new Point3d(wallPoints[WALL_BASE_START].x, wallPoints[WALL_BASE_START].y + WALL_DEFAULT_HEIGHT, wallPoints[WALL_BASE_START].z));
        final Vector3d wallNormal = new Vector3d();
        wallNormal.cross(wallHVector, wallVVector);
        final Transform3D rotation = DimensionUtil.getTransformToZ(wallNormal);

        for (final Point3d point3d : wallPoints) {
            rotation.transform(point3d);
        }
        rotation.invert();

        final Point3d wallCenter = getCenterPoint(wallPoints[WALL_BASE_START], wallPoints[WALL_BASE_END]);
        final Transform3D translation = new Transform3D();
        translation.setTranslation(new Vector3d(wallCenter));
        translation.invert();

        for (final Point3d point3d : wallPoints) {
            translation.transform(point3d);
        }
        translation.invert();
        rotation.mul(translation);
        wallTransTG.setTransform(rotation);
    }

    protected void init() {
        holeMap = new HashMap<Surface3D, Surface3D>();
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        wallTransTG = new TransformGroup();
        wallTransTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        wallTransTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        wallTransTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        wallTransTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        wallTransTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        addChild(wallTransTG);
    }

    protected Point3d getCenterPoint(final Point3d p1, final Point3d p2) {
        return new Point3d((p1.x + p2.x) / 2, (p1.y + p2.y) / 2, (p1.z + p2.z) /
                2);
    }

    protected Vector3d getVector(final Point3d start, final Point3d end) {
        final Vector3d ret = new Vector3d((end.x - start.x), (end.y - start.y),
                (end.z - start.z));
        ret.normalize();
        return ret;
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.writeDouble(wallWidth);
        oos.writeBoolean(holeMap.isEmpty());
        if (!holeMap.isEmpty()) {
            oos.writeObject(holeMap);
        }
        oos.writeInt(wallID);
        oos.writeObject(wallShape);

        final double[] matrixData = new double[16];
        final Transform3D trans = new Transform3D();
        wallTransTG.getTransform(trans);
        trans.get(matrixData);

        for (final double data : matrixData) {
            oos.writeDouble(data);
        }

        //Todo saves Surface3D of all Surfaces here
        for (int index = FRONT_SURFACE_INDEX; index <= RIGHT_SURFACE_INDEX; index++) {
            final Object surface = wallTransTG.getChild(index);
            if (surface instanceof Surface3D) {
                oos.writeObject(surface);
            }
        }
        oos.flush();
    }

    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        initiate();
        final Runtime wallRunTime = Runtime.getRuntime();
        final long start = System.currentTimeMillis();

        wallWidth = ois.readDouble();
        if (!ois.readBoolean()) {
            holeMap = (Map<Surface3D, Surface3D>) ois.readObject();
        } else {
            holeMap = new HashMap<Surface3D, Surface3D>();
        }
        wallID = ois.readInt();
        wallShape = (WallShape) ois.readObject();

        referedRoom = new HashSet<Room>();
        wallTransTG = new TransformGroup();
        wallTransTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        wallTransTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        wallTransTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        wallTransTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        wallTransTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        addChild(wallTransTG);
        final double[] matrixData = new double[16];
        for (int index = 0; index < matrixData.length; index++) {
            matrixData[index] = ois.readDouble();
        }
        wallTransTG.setTransform(new Transform3D(matrixData));

        for (int index = FRONT_SURFACE_INDEX; index <= RIGHT_SURFACE_INDEX; index++) {
            wallTransTG.insertChild((Surface3D) ois.readObject(), index);
        }
        systemWallLookup.put(getWallID(), this);
        System.gc();
    }

}
