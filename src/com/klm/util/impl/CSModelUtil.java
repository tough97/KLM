/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util.impl;

import com.klm.util.CSUtilException;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.scenegraph.io.SceneGraphFileReader;
import com.sun.j3d.utils.scenegraph.io.SceneGraphFileWriter;
import com.sun.j3d.utils.scenegraph.io.UnsupportedUniverseException;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.swing.JPanel;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author gang-liu
 */
public class CSModelUtil {

    public static double SHRINK_SCALE = 0.8;

    public static void parseNodeBoundaries(final Node node, final Point3d lower,
            final Point3d upper) {
        parseNodeBoundaries(node, doubleToFloat(lower), doubleToFloat(upper));
    }

    public static void parseNodeBoundaries(final Node node, final Point3f lower,
            final Point3f upper) {
        if (node instanceof Group) {
            for (int index = 0; index < ((Group) node).numChildren(); index++) {
                parseNodeBoundaries(((Group) node).getChild(index), lower, upper);
            }
        } else if (node instanceof Shape3D) {
            for (int index = 0; index < ((Shape3D) node).numGeometries(); index++) {
                final Geometry geo = ((Shape3D) node).getGeometry(index);
                if (geo instanceof GeometryArray) {
                    final GeometryInfo gi =
                            new GeometryInfo((GeometryArray) geo);
                    final Point3f[] points = gi.getCoordinates();
                    for (final Point3f point : points) {
                        if (point.x < lower.x) {
                            lower.x = point.x;
                        }
                        if (point.y < lower.y) {
                            lower.y = point.y;
                        }
                        if (point.z < lower.z) {
                            lower.z = point.z;
                        }

                        if (point.x > upper.x) {
                            upper.x = point.x;
                        }
                        if (point.y > upper.y) {
                            upper.y = point.y;
                        }
                        if (point.z > upper.z) {
                            upper.z = point.z;
                        }
                    }
                }
            }
        }
    }

    public static Point3f getCenter(final Point3f lower, final Point3f upper) {
        final Point3f ret = new Point3f((upper.x + lower.x) / 2, (upper.y +
                lower.y) / 2, (upper.z + lower.z) / 2);
        return ret;
    }

    /*
     * Claculate the Transform3D making Scene Object inside the Screen and at the center
     */
    public static Transform3D getInitTrans(final SimpleUniverse su,
            final Point3f lower, final Point3f upper) {
        final JPanel[] panels = su.getViewer().getJPanels();
        final Point2d pixPoint2d = new Point2d();
        if (panels != null && panels.length == 1) {
            final Transform3D trans = new Transform3D();
            final Point3f center = getCenter(lower, upper);
            center.negate();
            trans.setTranslation(new Vector3f(center));

            final Transform3D scaleTrans = new Transform3D();
            while (true) {
                su.getCanvas().getPixelLocationFromImagePlate(floatToDouble(
                        lower), pixPoint2d);
                final Point lowerPixPoint = point2DtoPoint(pixPoint2d);
                su.getCanvas().getPixelLocationFromImagePlate(floatToDouble(
                        lower), pixPoint2d);
                final Point upperPixPoint = point2DtoPoint(pixPoint2d);
                if (panels[0].contains(upperPixPoint) && (panels[0].contains(
                        lowerPixPoint))) {
                    break;
                } else {
                    final Transform3D addScale = new Transform3D();
                    addScale.setScale(SHRINK_SCALE);
                    scaleTrans.mul(addScale);
                }
            }

            trans.mul(scaleTrans);
            return trans;
        } else {
            return null;
        }
    }

    public static Point3d floatToDouble(final Point3f point) {
        return new Point3d((double) point.x, (double) point.y, (double) point.z);
    }

    public static Point3f doubleToFloat(final Point3d point) {
        return new Point3f((float) point.x, (float) point.y, (float) point.z);
    }

    public static Point point2DtoPoint(final Point2d point2d) {
        return new Point((int) point2d.x, (int) point2d.y);
    }

    public static void saveBranchGroup(final Set<BranchGroup> bgs,
            final String fName) throws IOException, UnsupportedUniverseException {
        final SceneGraphFileWriter writer = new SceneGraphFileWriter(new File(
                fName), null,
                false, "co-soft", "");
        for (final BranchGroup bg : bgs) {
            writer.writeBranchGraph(bg);
        }
        writer.close();
    }

    public static BranchGroup loadBranchGroups(final String fName) throws
            IOException {
        final BranchGroup root = new BranchGroup();
        root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        root.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        final SceneGraphFileReader reader = new SceneGraphFileReader(new File(
                fName));
        for (final BranchGroup bg : reader.readAllBranchGraphs()) {
            root.addChild(bg);
        }
        return root;
    }

    public static void saveBranchGroupThroughFile(final String fName,
            final BranchGroup bg) throws IOException,
            UnsupportedUniverseException {
        final File file = new File(fName);
        final SceneGraphFileWriter writer = new SceneGraphFileWriter(file, null,
                false,
                "IOUtil.saveBranchGroupThroughFile", "");
        writer.writeBranchGraph(bg);
        writer.close();
    }

    public static BranchGroup loadBranchGroupThroughFile(final String fName) throws
            IOException {
        final File file = new File(fName);
        final SceneGraphFileReader reader = new SceneGraphFileReader(file);
        final BranchGroup bgf = reader.readAllBranchGraphs()[0];
        reader.close();
        return bgf;
    }
    
    public static void parseNodeAppearance(final Node node){
        final Map<Appearance, Integer> map = new HashMap<Appearance, Integer>();
        if(node instanceof Group){
            for(int index = 0; index < ((Group)node).numChildren(); index++){
                parseNodeAppearance(((Group) node).getChild(index));
            }
        } else if(node instanceof Shape3D){
            final Appearance app = ((Shape3D) node).getAppearance();
            if(app != null){
                if(map.get(app) != null){
                    map.put(app, map.get(app) + 1);
                } else {
                    map.put(app, 1);
                }
            } else {
                System.out.println("There is no Appearance");
            }
        }        
        for(final Appearance app : map.keySet()){
            System.out.println("Appearance \""+app+"\" Appears "+map.get(
                    app)+" Times");
        }
    }
    
    public static void parseNodeByAppearance(final Node node){
        if(node instanceof Group){
            for(int index = 0; index < ((Group)node).numChildren(); index++){
                parseNodeByAppearance(((Group) node).getChild(index));
            }
        } else if(node instanceof Shape3D){
            final Appearance app = ((Shape3D) node).getAppearance();
            if(app != null){
//                printAppearance(app);
                System.out.println(app);
            } else {
                System.out.println("There is no Appearance");
            }
        }
    }
    
    public static void printAppearance(final Appearance app){
        final Material m = app.getMaterial();
        if(m != null){
            System.out.println("m.getShininess() = "+m.getShininess());
        } else {
            System.out.println("There is no Material");
        }
    }
    
    public static void main(String[] args) throws CSUtilException, IOException,
            UnsupportedUniverseException {
//        final String fName = "/home/gang-liu/Desktop/lanbojini.klm";
////        final BranchGroup bg = new OBJModelImportor().importFromFile(
//                "/home/gang-liu/Desktop/lbjn/lanbojini.obj");
        final Set<BranchGroup> bgs = new HashSet<BranchGroup>();
//        saveBranchGroupThroughFile(fName, bg);
//        parseNodeAppearance(bg);
        
    }
}
