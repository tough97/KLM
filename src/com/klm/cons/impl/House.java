package com.klm.cons.impl;


import com.klm.material.impl.MaterialColor;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 11/24/11
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class House extends CSBranchGroup {

    private static final long serialVersionUID = 100;
    private static boolean log = false;

    public static boolean writeLog() {
        return log;
    }

    public static void setWriteLog(final boolean log) {
        House.log = log;

    }

    private Map<String, Floor> floorMap = new HashMap<String, Floor>();
    private Set<Light> houseLights = new HashSet<Light>();
    private String houseID = "";
    private String houseDescription = "";
    public static final Color3f NATURE_SUN_LIGHT = new Color3f(new Color(90, 90, 90));
    public static final Bounds LIGHT_BOUNDS = new BoundingSphere(new Point3d(0, 0, 0), 40.0);

    public House() {
        super();
        initialLights();
    }

    public String getHouseID() {
        return houseID;
    }

    public void setHouseID(String houseID) {
        this.houseID = houseID;
    }

    public Floor getFloor(final String name) {
        return floorMap.get(name);
    }

    public Set<Floor> getFloors() {
        final Set<Floor> ret = new HashSet<Floor>();
        for (final Floor floor : floorMap.values()) {
            ret.add(floor);
        }
        return ret;
    }

    public int getNumOfFloors() {
        return floorMap.size();
    }

    public void addFloor(final String floorName, final Floor floor) {
        floorMap.put(floorName, floor);
        if (floor.getName() == null || floor.getName().equals("")) {
            floor.setName(floorName);
        }
        addChild(floor);
    }

    public void removeFloor(final String floorName) {
        final Floor floor = floorMap.get(floorName);
        if (floor != null) {
            floorMap.remove(floorName);
            floor.detach();
        }
    }

    private void initialLights() {
        houseLights = new HashSet<Light>();
        houseLights.add(new AmbientLight(NATURE_SUN_LIGHT));

//        houseLights.add(new PointLight(new Color3f(new Color(190, 190, 190)), new Point3f(0f, 6f, 0f), new Point3f(.2f, .2f, .2f)));
//        houseLights.add(new DirectionalLight(true, new Color3f(new Color(0x999999)), new Vector3f(-0.1f, -0.05f, -0.1f)));

        for (final Light light : houseLights) {
            light.setEnable(true);
            light.setInfluencingBounds(LIGHT_BOUNDS);
            addChild(light);
        }
    }


//            new ObjectStreamField("floorMap", Map.class), new ObjectStreamField("houseID", String.class),
//            new ObjectStreamField("houseDescription", String.class)

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.writeBoolean(floorMap.isEmpty());
        if (!floorMap.isEmpty()) {
            oos.writeObject(floorMap);
        }
        oos.writeObject(houseID);
        oos.writeObject(houseDescription);
        oos.flush();
    }

    private void readObject(final ObjectInputStream ois)
            throws IOException, ClassNotFoundException {
        initiate();
        Wall.systemWallLookup.clear();
        if (!ois.readBoolean()) {
            floorMap = (Map<String, Floor>) ois.readObject();
        } else {
            floorMap = new HashMap<String, Floor>();
        }
        houseID = (String) ois.readObject();
        houseDescription = (String) ois.readObject();
        for (Floor floor : floorMap.values()) {
            addChild(floor);
        }
//        ois.close();
        System.gc();
        initialLights();
    }

    public static double getInM(final long byteNum) {
        return (1.0 * byteNum) / (1024 * 1024);
    }

}
