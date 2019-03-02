/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;

import com.klm.material.impl.MaterialColor;
import com.klm.util.DimensionUtil;
import com.klm.util.RealNumberOperator;
import com.klm.util.impl.MerchandiseInfo;
import com.sun.j3d.utils.image.TextureLoader;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static javax.imageio.ImageIO.*;

/**
 * @author gang-liu
 */
public class Room extends CSBranchGroup {
    public static final Vector3d Y_UP = new Vector3d(0, 1, 0);
    public static final Vector3d Y_DOWN = new Vector3d(0, -1, 0);
    public static final double FLOOR_THICKNESS = 0.1;
    public static final double CEILING_THICKNESS = 0.2;
    public static final double ERROR_ADJUSTMENT = 0.001;

    private static final Map<String, Room> appScopeRooms = new HashMap<String, Room>();
    private static final long serialVersionUID = 100;

    private static Texture WALL_TEXTURE = null;
    private static Texture FLOOR_TEXTURE = null;
    private static final MaterialColor DEFAULT_FLOOR_COLOR = new MaterialColor(new Color(0x222222), new Color(0x444444), new Color(0x666666));
    private static final MaterialColor DEFAULT_CEILING_COLOR = new MaterialColor(new Color(0x333333), new Color(0x444444), new Color(0x555555));

    static {
        try {
            WALL_TEXTURE = new TextureLoader(ImageIO.read(new File("com/klm/reft/wall.jpg"))).getTexture();
            FLOOR_TEXTURE = new TextureLoader(ImageIO.read(new File("com/klm/reft/floor.jpg"))).getTexture();
        } catch (IOException e) {
        }
    }

    static void resetAppScopeRooms() throws IOException {
        appScopeRooms.clear();
    }

    static Room getAppScopeRoom(final String roomName) {
        return appScopeRooms.get(roomName);
    }

    static void addRoom(final Room room) throws CSHouseException {
        final String roomName = room.getRoomName();
        if (appScopeRooms.get(roomName) != null) {
            throw new CSHouseException("Can not recreate room " + roomName + " while record still exsits");
        }
    }

    static void removeRoom(final Room room) {
        appScopeRooms.remove(room.getName());
    }

    private String roomName;
    private Map<Wall, Integer> wallLookup = new HashMap<Wall,Integer>();
    private Point3d[] roomOutlinePoints;
    private Surface3D floorUp, floorDown;
    private Surface3D ceilingDown;
    private Set<Light> roomLights = new HashSet<Light>();

    public Room(final String roomName, final Set<Surface3D> surfaces, final Point3d[] rolp) throws CSHouseException {
        initiate();
        if (appScopeRooms.get(roomName) != null) {
            throw new CSHouseException("Can not recreate room " + roomName);
        }
        for (final Surface3D surface : surfaces) {
            final Node parent = surface.getFirstParentOf(Wall.class);
            if (parent == null) {
                throw new CSHouseException(new IllegalArgumentException("Surface does not belong to a wall"));
            } else {
                final Wall parentWall= (Wall) parent;
                wallLookup.put(parentWall,parentWall.getSurfaceFlag(surface));
                ((Wall) parent).addRoomReference(this);
            }
        }
        this.roomName = roomName;
        this.roomOutlinePoints = rolp;
        Vector3d roomOutLineNormal = DimensionUtil.getNormal(roomOutlinePoints);
        if (roomOutLineNormal == null) {
            final StringBuilder sb = new StringBuilder("Following Room outline points " +
                    "are not on the same plane :").append("\n");
            for (final Point3d point3d : rolp) {
                sb.append(point3d.toString()).append("\n");
            }
            sb.append("\n");
            if (House.writeLog()) {
                Logger.getLogger(Room.class).debug(sb.toString());
            } else {
                throw new CSHouseException(sb.toString());
            }
        }
        if (RealNumberOperator.compareTwoTuple3d(roomOutLineNormal, Y_DOWN)) {
        } else if (RealNumberOperator.compareTwoTuple3d(roomOutLineNormal, Y_UP)) {
            DimensionUtil.reversePoints(this.roomOutlinePoints);
        } else {
            final StringBuilder sb = new StringBuilder("Room can not create floor or " +
                    "ceiling due to un-horrizontal room out line points : ").append("\n");
            for (final Point3d point3d : rolp) {
                sb.append(point3d.toString()).append("\n");
            }
            sb.append("\n");
            if (House.writeLog()) {
                Logger.getLogger(Room.class).debug(sb.toString());
            } else {
                throw new CSHouseException(sb.toString());
            }
        }

        for (final Point3d point : roomOutlinePoints) {
            point.setY(ERROR_ADJUSTMENT);
        }

        floorDown = new Surface3D(roomOutlinePoints);
        setFloorMaterial(floorDown.getSurfaceAppMaterial());
        addChild(floorDown);

        for (final Point3d point : roomOutlinePoints) {
            point.setY(Wall.getWallHeight());
        }
        ceilingDown = new Surface3D(roomOutlinePoints);
        setCeilingMaterial(ceilingDown.getSurfaceAppMaterial());
        addChild(ceilingDown);
        DimensionUtil.reversePoints(roomOutlinePoints);
        ceilingDown.setLightenColor(DEFAULT_CEILING_COLOR);

        for (final Point3d point : roomOutlinePoints) {
            point.setY(ERROR_ADJUSTMENT + FLOOR_THICKNESS);
        }
        floorUp = new Surface3D(roomOutlinePoints);
        setFloorMaterial(floorUp.getSurfaceAppMaterial());
        floorUp.setLightenColor(DEFAULT_FLOOR_COLOR);
        addChild(floorUp);
        addRoom(this);
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(final String roomName) {
        this.roomName = roomName;
    }

    public MerchandiseInfo gerMerchandiseInfo() throws CSHouseException {
        final MerchandiseInfo ret = new MerchandiseInfo();
        ret.addMerchandiseCounter(floorUp.getMerchandiseInfo());
        ret.addMerchandiseCounter(floorDown.getMerchandiseInfo());
        ret.addMerchandiseCounter(ceilingDown.getMerchandiseInfo());
       for(Wall wall:wallLookup.keySet()){
            final Surface3D wallFace=wall.getSurface( wallLookup.get(wall));

            ret.addMerchandiseCounter(wallFace.getMerchandiseInfo());
        }
        return ret;
    }

    public Set<Wall> getWalls() {
        final Set<Wall> ret = new HashSet<Wall>();
        for (final Wall wall : wallLookup.keySet()) {
            ret.add(wall);
        }
        return ret;
    }
    public void removeWall(final Wall wall){
        wallLookup.remove(wall);

    }

    public Set<Surface3D> getWallSurfaces() {
        Set<Surface3D> surfaces=new HashSet<Surface3D>();
         for(Wall wall:wallLookup.keySet()){
            final Surface3D wallFace=wall.getSurface( wallLookup.get(wall));

            surfaces.add(wallFace);
        }
        return surfaces;
    }

    public Surface3D getCeilingDown() {
        return ceilingDown;
    }

    public Surface3D getFloorDown() {
        return floorDown;
    }

    public Surface3D getFloorUp() {
        return floorUp;
    }

    public void setLightEnable(final boolean enable) {
        for (final Light light : roomLights) {
            light.setEnable(enable);
        }
    }

    public static Texture getWallTexture() {
        return WALL_TEXTURE;
    }

    public static Texture getFloorTexture() {
        return FLOOR_TEXTURE;
    }

    //Private Methods---------------------------------------------------------------------------------------------------
    private void initialLights() {
        roomLights = new HashSet<Light>();
        roomLights.add(new AmbientLight(House.NATURE_SUN_LIGHT));

        for (final Light light : roomLights) {
            light.setEnable(true);
            light.setInfluencingBounds(House.LIGHT_BOUNDS);
            addChild(light);
        }
    }

    private void setFloorMaterial(final Material material) {
        material.setAmbientColor(new Color3f(Color.white));
        material.setColorTarget(Material.EMISSIVE);
//        material.setShininess(10.0f);
        material.setLightingEnable(true);
    }

    private void setCeilingMaterial(final Material material) {
        material.setAmbientColor(new Color3f(Color.white));
        material.setColorTarget(Material.AMBIENT);
        material.setLightingEnable(true);
    }

    //Implemented Methods-----------------------------------------------------------------------------------------------
    private void writeObject(final ObjectOutputStream oos) throws IOException {

        oos.writeObject(roomName);
        oos.writeInt(roomOutlinePoints.length);
        System.out.println("I am writing " + roomOutlinePoints.length + " points to file");
        for (final Point3d point : roomOutlinePoints) {
            oos.writeObject(point);
        }
        oos.writeObject(floorUp);
        oos.writeObject(floorDown);
        oos.writeObject(ceilingDown);

        oos.writeInt(wallLookup.size());
        for (final Wall wall : wallLookup.keySet()) {
            oos.writeInt(wall.getWallID());
            oos.writeInt(wallLookup.get(wall));
        }
        oos.flush();
    }

    /*
   new ObjectStreamField("roomName", String.class), new ObjectStreamField("roomOutlinePoints", Point3d[].class),
           new ObjectStreamField("floorUp", Surface3D.class), new ObjectStreamField("floorDown", Surface3D.class),
           new ObjectStreamField("ceilingDown", Surface3D.class)
    */
    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        initiate();

        roomName = (String) ois.readObject();
        roomOutlinePoints = new Point3d[ois.readInt()];
        for (int index = 0; index < roomOutlinePoints.length; index++) {
            roomOutlinePoints[index] = (Point3d) ois.readObject();
        }
//        System.out.println("I am reading " + roomOutlinePoints.length + " points from file");
//        if (roomOutlinePoints.length == 3) {
//            System.out.println("----");
//        }
        floorUp = (Surface3D) ois.readObject();
        floorDown = (Surface3D) ois.readObject();
        ceilingDown = (Surface3D) ois.readObject();
        addChild(floorUp);
        addChild(floorDown);
        addChild(ceilingDown);

        wallLookup = new HashMap<Wall, Integer>();
        try {
            int countWall=ois.readInt();
            for (int index = 0; index < countWall; index++) {
                final Wall wall = Wall.getWall(ois.readInt());
                int wallFaceIndex=ois.readInt();
                wallLookup.put(wall, wallFaceIndex);
                wall.addRoomReference(this);
            }
//            initialLights();
        } catch (Exception ex) {
            Logger.getLogger(Room.class).error(ex);
        } finally {
            System.gc();
        }
    }

}
