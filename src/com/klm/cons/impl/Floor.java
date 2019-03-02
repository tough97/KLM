package com.klm.cons.impl;

import com.klm.util.impl.MerchandiseInfo;

import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 11/16/11
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class Floor extends CSBranchGroup implements Serializable{
    private TransformGroup floorTrans = new TransformGroup();
    private static final long serialVersionUID = 100;
    private Set<Wall> wallSet = new HashSet<Wall>();
    private Set<Room> roomSet = new HashSet<Room>();

    public Floor() {
        super();
        floorTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        floorTrans.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        floorTrans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        addChild(floorTrans);
    }

    public void addRoom(final Room room){
        floorTrans.addChild(room);
        roomSet.add(room);
    }

    public boolean contains(final Room room) {
        return (room.getParent() == floorTrans);
    }
      public boolean containsWall(final Wall wall) {
        return wallSet.contains(wall);
    }
    public void addWall(final Wall wall) {
        if(!wallSet.contains(wall)){
        floorTrans.addChild(wall);
        wallSet.add(wall);
        }
    }

    public void removeRoom(final Room room, boolean removeWalable) {
        final Set<Wall> roomWalls = room.getWalls();
        floorTrans.removeChild(room);
         Room.removeRoom(room);
        for (final Wall wall : roomWalls) {
            wall.removeReferenceRoom(room);
            if (wall.getReferedRoom().isEmpty()) {
                if(removeWalable){
                    removeWall(wall);
                }
                roomSet.remove(room);
            }
        }
    }

    public void removeWall(final Wall wall) {
        if (wallSet.contains(wall)) {
            floorTrans.removeChild(wall);
            wallSet.remove(wall);
            for(Room room:roomSet){
                if(room.getWalls().contains(wall)){
                    room.removeWall(wall);
                }
            }
            Wall.systemWallLookup.remove(new Integer(wall.getWallID()));
        }
    }

    public TransformGroup getFloorTrans() {
        return floorTrans;
    }

    public Map<Room, MerchandiseInfo> getFloorMerchandiseInfo() throws CSHouseException {
        final Map<Room, MerchandiseInfo> ret = new HashMap<Room, MerchandiseInfo>();
        final Set<Surface3D> unRoomedSurfaces = new HashSet<Surface3D>();

        ret.put(null, new MerchandiseInfo());

        for(final Wall wall : wallSet){
            unRoomedSurfaces.add(wall.getSurface(Wall.FRONT_SURFACE_INDEX));
            unRoomedSurfaces.add(wall.getSurface(Wall.BACK_SURFACE_INDEX));
        }

        for(final Room room : roomSet){
            final Set<Surface3D> wallSurfaces = room.getWallSurfaces();
            for(final Surface3D surface3D : wallSurfaces) {
                unRoomedSurfaces.remove(surface3D);
            }
            ret.put(room, room.gerMerchandiseInfo());
        }

        for(final Surface3D surface3D : unRoomedSurfaces){
            ret.get(null).addMerchandiseCounter(surface3D.getMerchandiseInfo());
        }

        return ret;
    }

    public Set<Room> getRooms() {
        return roomSet;
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.writeObject(wallSet);
        System.out.println("I am writing "+roomSet.size()+" rooms to file");
        oos.writeObject(roomSet);
        final Transform3D trans = new Transform3D();
        floorTrans.getTransform(trans);
        final double[] data = new double[16];
        trans.get(data);
        oos.writeObject(data);
        oos.flush();
    }

    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        initiate();
        wallSet = (HashSet<Wall>) ois.readObject();
        roomSet = (HashSet<Room>) ois.readObject();
        floorTrans = new TransformGroup();
        floorTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        floorTrans.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        floorTrans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        addChild(floorTrans);
        final double[] data = (double[]) ois.readObject();
        floorTrans.setTransform(new Transform3D(data));

        for (final Wall wall : wallSet) {
            floorTrans.addChild(wall);
        }

        for (final Room room : roomSet) {
            floorTrans.addChild(room);
        }
        System.gc();
    }

    protected void finalize() {
        this.wallSet.clear();
        this.roomSet.clear();
    }

}
