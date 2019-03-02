/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;

import com.klm.exhib.ModelDisplayer;
import com.klm.material.impl.MaterialColor;
import com.klm.persist.CSPersistException;
import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.LocalStorage;
import com.klm.persist.impl.SurfaceMaterial;
import com.klm.persist.meta.AppearanceMeta;
import com.klm.persist.meta.BufferedImageMeta;
import com.klm.persist.meta.MaterialMeta;
import com.klm.persist.meta.Shape3DMeta;
import com.klm.util.CSUtilException;
import com.klm.util.DimensionUtil;
import com.klm.util.RealNumberOperator;
import com.klm.util.impl.MerchandiseInfo;
import com.sun.j3d.utils.geometry.*;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.io.*;
import java.util.*;
import java.util.List;


/**
 * Surface3D is used to represent a flat surface in 3D space. when created, it
 * calculates the
 * <br><b>Identified shape</b> : The origin shape of this Surface, this shape centered at
 * (0.0, 0.0, 0.0) and stick on XY Plane, it is used to describe Surface geometry
 * information. 2D shape information can be retrieved at this stage</br>
 * <br><b>Initialized Shape</b> : The Surface On Parent information, stick on XY plane but
 * not necessarily centered on (0.0, 0.0, 0.0). 2D shape information can be retrieved
 * at this stage</br>
 * <br><b>Finalized Shape</b> : describes how the Surface finally displayed on its parent
 * 2D shape information is not available here, but end user can interact with this
 * TransformGroup</br>
 * <br>
 * <p/>
 * </br>
 *
 * @author Gang Liu
 * @version 2.0
 */
public final class Surface3D extends CSBranchGroup {
    public static final Vector3d Z_AXIS_POSITIVE = new Vector3d(0.0, 0.0, 1.0);
    public static final Vector3d Z_AXIS_NEGATIVE = new Vector3d(0.0, 0.0, -1.0);
    public static final Vector3d DEFAULT_SCALE = new Vector3d(1.0, 1.0, 1.0);
    public static final Point3d ORIGIN_POINT = new Point3d(0.0, 0.0, 0.0);
    public static final Color3f HINT_ERROR = new Color3f(Color.RED);
    public static final Color3f HINT_PROCESS = new Color3f(Color.WHITE);

    private CSConnSurfaceBG connectBG;
    private Shape3D rawShape;
    private BranchGroup subBG;
    private BranchGroup attachmentBG;
    private SurfaceMaterial surfaceMaterial;
    private Shape3D indicatorShape;
    private boolean visible = true;
    private Point3d[] identifyCoord;
    private boolean customizable = false;

    private static final long serialVersionUID = 100;
    //    private static final ObjectStreamField[] serialPersistentFields = {
//            new ObjectStreamField("visible", boolean.class), new ObjectStreamField("customizable", boolean.class)
//    };
    /*
     * Surface3D inner structure, Only Finalized TransformGroup
     * should be visible and operatable to outside
     */
    public static final int INITIAL_ROT_SCA_TG = 0;
    public static final int INITIAL_TRANS_TG = 1;
    public static final int CUTOMIZABLE_TRANS_TG = 2;
    public CSTransformGroup[] surfaceTG;
    /*
     * Texures
     */
    public static final int SURFACE_MATERIAL_INDEX = 0;
    public static final int SURFACE_EFFECT_INDEX = 1;
    public static final int SURFACE_INDICATE_INDEX = 2;

    /**
     * Create a Surface3D according to given Coordinates
     *
     * @param coords Coordinates must be on the same plane, Right handed rule can be ignored here
     * @throws CSHouseException
     */
    public Surface3D(final Point3d[] coords) throws CSHouseException {
        initiate();
        init();
        setCoordToParent(coords);
        updateSurface();
    }

    /**
     * Gets the Identified position coordinates of this Surface3D, Identified Coordinates could be
     * used to describe the outline shape of this Surface3D these coordinates form a Shape centered
     * at Origin Point (0.0, 0.0, 0.0) at parent Coordinate System and paralleled to XY plane of
     * that coordinate System as well
     *
     * @return The Identified Coordinates of this Surface3D
     * @throws CSHouseException
     */
    public Point3d[] getIdentifiedCoords() throws CSHouseException {
        if (identifyCoord == null) {
            return null;
        }
        final Point3d[] ret = new Point3d[identifyCoord.length];
        for (int index = 0; index < ret.length; index++) {
            ret[index] = new Point3d(identifyCoord[index]);
        }
        return ret;
    }

    /**
     * Gets the Coordinates projected on Parent of this Surface3D, Coordinates On Parent located
     * on Parent System coordinate XY Plane it is used to describe how the Surface3D moved on Parent
     *
     * @return The Coordinates projected to parent of this Surface3D
     * @throws CSHouseException
     */
    public Point3d[] getCoordsProOnParent() throws CSHouseException {
        final Point3d[] ret = getCoordsToParent();
        for (final Point3d p : ret) {
            p.setZ(0.0);
        }
        return ret;
    }

    /**
     * Gets the Coordinates moved on parent Coordinate System of this Surface3D. coordinates moved depend
     * on two Transforms <b>How the Surface3D initialized (Constructed coordinates )</b> it is not project
     * but a relatively moved in Parent
     *
     * @return Coordinates initialized and moved on parent of this Surface3D
     * @throws CSHouseException
     */
    public Point3d[] getCoordsOnParent() throws CSHouseException {
        final Point3d[] ret = getIdentifiedCoords();
        final Transform3D translation = new Transform3D();
        translation.setTranslation(surfaceTG[INITIAL_TRANS_TG].getTranslation());
        for (final Point3d point : ret) {
            translation.transform(point);
            point.setZ(0.0);
        }
        return ret;
    }

    /**
     * Returns the <b>To Parent</b> coordinates of this Surface3D.
     * This is How this Surface3D finalized on parent Coordinates System.
     *
     * @return Coordinates To Parent
     * @throws CSHouseException *--------CustomizableTG
     *                          ------------InitRotTG
     *                          ----------------InitTransTG
     */
    public Point3d[] getCoordsToParent() throws CSHouseException {
        final Point3d[] ret = getIdentifiedCoords();
        final Transform3D transToParent = getTransformToParent();
        for (final Point3d pt : ret) {
            transToParent.transform(pt);
        }
        return ret;
    }

    /**
     * @return Returns Transform3D transforms the <b>Identified Position</b>
     *         of this Surface3D to<b>To Parent</b>
     */
    public Transform3D getTransformToParent() {
        final Transform3D trans = surfaceTG[CUTOMIZABLE_TRANS_TG].getTransform();
        trans.mul(surfaceTG[INITIAL_ROT_SCA_TG].getTransform());
        trans.mul(surfaceTG[INITIAL_TRANS_TG].getTransform());
        return trans;
    }

    /**
     * @return Returns nomral from current Surface3D to its parent
     * @throws CSHouseException
     */
    public Vector3d getNormalToParent() throws CSHouseException {
        final Vector3d ret = DimensionUtil.getNormal(getIdentifiedCoords());
        getTransformToParent().transform(ret);
        return ret;
    }

    /**
     * @return All the Surface3Ds attached on current one as Sub-Surfaces
     */
    public Set<Surface3D> getSubSurfaces() {
        final Set<Surface3D> ret = new HashSet<Surface3D>();
        for (int index = 0; index < subBG.numChildren(); index++) {
            if (subBG.getChild(index) instanceof Surface3D) {
                ret.add((Surface3D) subBG.getChild(index));
            }
        }
        return ret;
    }

    /**
     * @return The CSTransformGroup allows API caller to navigate this Surface3D
     *         On parent Surface
     * @see CSTransformGroup
     */
    public CSTransformGroup getCutomzableTG() {
        return customizable ? surfaceTG[CUTOMIZABLE_TRANS_TG] : null;
    }

    public boolean isCustomizable() {
        return customizable;
    }

    public void setCustomizable(final boolean customizable) {
        this.customizable = customizable;
    }

    /**
     * Sets the color of this Surface3D appearance, could be used to debug or without
     * Light scene
     *
     * @param color
     */
    public void setColor(final Color color) {
        final ColoringAttributes ca = rawShape.getAppearance().
                getColoringAttributes();
        ca.setColor(new Color3f(color));
    }

    public void setLightenColor(final MaterialColor color) {
        final Material material = rawShape.getAppearance().getMaterial();
        material.setAmbientColor(new Color3f(color.getDarkColor()));
        material.setDiffuseColor(new Color3f(color.getOriginalColor()));
        material.setSpecularColor(new Color3f(color.getBrightColor()));
    }

    public void setLightenColor(final Color darkColor, final Color originalColor, final Color brightColor) {
        final Material material = rawShape.getAppearance().getMaterial();
        material.setAmbientColor(new Color3f(darkColor));
        material.setDiffuseColor(new Color3f(originalColor));
        material.setSpecularColor(new Color3f(brightColor));
    }

    final Color getDarkColor() {
        final Color3f color = new Color3f();
        rawShape.getAppearance().getMaterial().getAmbientColor(color);
        return new Color(color.getX(), color.getY(), color.getZ());
    }

    final Color getOriginalColor() {
        final Color3f color = new Color3f();
        rawShape.getAppearance().getMaterial().getDiffuseColor(color);
        return new Color(color.getX(), color.getY(), color.getZ());
    }

    final Color getBrightColor() {
        final Color3f color = new Color3f();
        rawShape.getAppearance().getMaterial().getSpecularColor(color);
        return new Color(color.getX(), color.getY(), color.getZ());
    }


    /**
     * @return the Connective Surface3Ds for this Surface3D connected to Parent
     */
    public Set<Surface3D> getConnectiveSurfaces() {
        final Set<Surface3D> ret = new HashSet<Surface3D>();
        for (int index = 0; index < connectBG.numChildren(); index++) {
            ret.add((Surface3D) connectBG.getChild(index));
        }
        return ret;
    }

    public void setSurfaceMaterial(final SurfaceMaterial surfaceMaterial, final Color blendColor) {
        final TextureUnitState materialTus = initSurfaceMaterial(surfaceMaterial);
        final TextureAttributes ta = materialTus.getTextureAttributes();
        ta.setTextureMode(TextureAttributes.MODULATE);
    }

    /**
     * Sets the SurfaceMaterial of this Surface3D. this method focus on
     *
     * @param surfaceMaterial
     * @see SurfaceMaterial
     */
    public void setSurfaceMaterial(final SurfaceMaterial surfaceMaterial) {
        if (surfaceMaterial.isReflective()) {
            final TextureUnitState materialTus = initSurfaceMaterial(surfaceMaterial);
            materialTus.getTextureAttributes().setTextureMode(TextureAttributes.REPLACE);
            Texture effectTexture = null;
            Node parent = getParent();
            while (effectTexture == null && parent != null) {
                //if this is floor or ceiling
                if (parent instanceof Room) {
                    effectTexture = Room.getFloorTexture();
                } else if (parent instanceof Wall) {
                    effectTexture = Room.getWallTexture();
                }
                parent = parent.getParent();
            }

            if (effectTexture != null) {
                final TextureUnitState tus = rawShape.getAppearance().getTextureUnitState(SURFACE_EFFECT_INDEX);
                tus.setTexture(effectTexture);
                final double[] surfaceDimension = getSurfaceDimension();
                final TexCoordGeneration tcg = tus.getTexCoordGeneration();
                tcg.setPlaneS(new Vector4f((float) (1 / surfaceDimension[0]), 0f, 0f, 0f));
                tcg.setPlaneT(new Vector4f(0f, (float) (1 / surfaceDimension[0]), 0f, 0f));

                final Transform3D trans = new Transform3D();
                trans.setTranslation(new Vector3d(.5, .5, 0));
                tus.getTextureAttributes().setTextureTransform(trans);
                tus.getTextureAttributes().setTextureMode(TextureAttributes.MODULATE);
            }
        } else {
            setSurfaceMaterial(surfaceMaterial, getOriginalColor());
        }
    }

    /**
     *
     */
    public TextureUnitState getTextureUnitState(final int index) throws CSHouseException {
        final TextureUnitState[] tus = rawShape.getAppearance().getTextureUnitState();
        if (index < 0 || index >= tus.length) {
            throw new CSHouseException("index out of bound (0 - " + tus.length + ") : but " + index + " found");
        } else {
            return tus[index];
        }
    }

    /**
     * Return the SurfaceMaterial of this Surface3D
     *
     * @return SurfaceMaterial
     * @see SurfaceMaterial
     */
    public SurfaceMaterial getSurfaceMaterial() {
        return surfaceMaterial;
    }

    /**
     * Tests if this Surface3D is an Connective Surface3D of another
     *
     * @return true if this Surface3D is connective Surface3D, false otherwise
     */
    public boolean isConnectiveSurface() {
        Node parent = getParent();
        while (parent != null) {
            if (parent instanceof CSConnSurfaceBG) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * Returns connective comrade surfaces of this Surface3D
     *
     * @return Set of comrades if this Surface3D is connective or null otherwise
     */
    public Set<Surface3D> getConnectiveComrades() {
        final Node parent = getParent();
        if (parent instanceof CSConnSurfaceBG) {
            final Set<Surface3D> ret = new HashSet<Surface3D>();
            for (int index = 0; index < ((CSConnSurfaceBG) parent).numChildren(); index++) {
                final Node child = ((CSConnSurfaceBG) parent).getChild(index);
                if (child instanceof Surface3D && child != this) {
                    ret.add((Surface3D) child);
                }
            }
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Returns the java.awt.Shape created by the Identified Coordinates
     *
     * @return java.awt.Shape for this Surface3D
     * @throws CSHouseException
     */
    public Shape getIdentifiedShape() throws CSHouseException {
        final Point3d[] coord = getIdentifiedCoords();
        try {
            return DimensionUtil.point3DToShape(coord);
        } catch (CSUtilException ex) {
            throw new CSHouseException(ex);
        }
    }

    /**
     * Returns the java.awt.Shape created by the Coordinates On Parent
     *
     * @return java.awt.Shape for this Surface3D
     * @throws CSHouseException
     */
    public Shape getInitilizedShape() throws CSHouseException {
        final Point3d[] coord = getCoordsOnParent();
        try {
            return DimensionUtil.point3DToShape(coord);
        } catch (CSUtilException ex) {
            throw new CSHouseException(ex);
        }
    }

    /**
     * Tests if given Surface3D could be added to current Surface3D as sub-surface
     * an Surface3D can be added to another as sub-surface only and if only the following
     * two conditions are met
     * <br>1- Given Surface is contained by holder</br>
     * <br>2- Given Surface does not overlap with any other current existing Sub-Surfaces</br>
     * <p/>
     * We have been stack on if sub-surface could be added to current surface3d with overlapping
     * or other different situation exists but this could not be solved reasonably by current design
     * and implementation capability. so this the second condition is hard now.
     * <p/>
     * Future implementation should make addSubsurface more dynamic and automatic
     *
     * @param surface to be added to this Surface3D
     * @return true if operation possible, false otherwise
     * @throws CSUtilException
     * @throws CSHouseException
     */
    public boolean testSubSurface(final Surface3D surface) throws
            CSUtilException, CSHouseException {
        final Area subSurfaceShape = new Area(surface.getInitilizedShape());
        final Area parentSurfaceShape = new Area(getIdentifiedShape());
        Area testingShape = new Area(subSurfaceShape);
        testingShape.intersect(parentSurfaceShape);
        if (!testingShape.equals(subSurfaceShape)) {
            return false;
        }
        for (final Surface3D neighbor : getSubSurfaces()) {
            final Area neighborShape = new Area(neighbor.getInitilizedShape());
            testingShape = new Area(subSurfaceShape);
            testingShape.intersect(neighborShape);
            if (!testingShape.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add given Surface3D as sub-surface to current one
     *
     * @param subSurface
     * @throws CSHouseException
     * @throws CSUtilException
     */
    public void addSubSurface(final Surface3D subSurface) throws
            CSHouseException, CSUtilException {
        if (isConnectiveSurface()) {
            return;
        }
        if (!testSubSurface(subSurface)) {
            throw new CSHouseException("Subsurface does not suit to parent");
        }
        subBG.addChild(subSurface);
        updateSurface();
    }

    /**
     * removes current Surface3D from its parent, returns boolean indicate if operation succeed
     *
     * @throws CSHouseException
     */
    public boolean detachFromParent() throws CSHouseException {
        final Surface3D parent = getParentSurface();
        if (parent == null) {
            return false;
        }
        parent.subBG.removeChild(this);
        parent.updateSurface();
        return true;
    }

    /**
     * Returns Coordinates on this Surface3D coordinate System clicked by mouse
     *
     * @param canvas event
     * @param event  of the scene
     * @return Point3D intersects with Mouse pick ray
     */
    public Point3d getClickedPointOnSurface(MouseEvent event, Canvas3D canvas) throws
            CSHouseException {
        return getClickedPointOnSurface(event.getPoint(), canvas);
    }

    /**
     * Returns Coordinates on this Surface3D coordinate System clicked by mouse
     *
     * @param canvas point
     * @param point  of the scene
     * @return Point3D intersects with Mouse pick ray
     */
    public Point3d getClickedPointOnSurface(final Point point,
                                            final Canvas3D canvas) throws CSHouseException {
        if ((canvas == null) || (point == null)) {
            throw new CSHouseException("Canvas or Event can not be null");
        }
        Point3d eyePos = new Point3d();
        Point3d mousePos = new Point3d();
        canvas.getCenterEyeInImagePlate(eyePos);
        canvas.getPixelLocationInImagePlate((int) point.getX(),
                (int) point.getY(), mousePos);
        Transform3D transform = new Transform3D();
        canvas.getImagePlateToVworld(transform);
        transform.transform(eyePos);
        transform.transform(mousePos);
        Vector3d direction = new Vector3d(eyePos);
        direction.sub(mousePos);
        // three points on the plane
        Point3d p1 = new Point3d(.5, -.5, .0);
        Point3d p2 = new Point3d(.5, .5, .0);
        Point3d p3 = new Point3d(-.5, .5, .0);
        Transform3D currentTransform = new Transform3D();
        rawShape.getLocalToVworld(currentTransform);
        currentTransform.transform(p1);
        currentTransform.transform(p2);
        currentTransform.transform(p3);
        Point3d intersection = getIntersection(eyePos, mousePos, p1, p2, p3);
        currentTransform.invert();
        currentTransform.transform(intersection);
        return intersection;
    }

    /**
     * Creates the Connective Surfaces of Surface3D from current Surface3D to it's
     * parent
     *
     * @throws CSHouseException
     */
    public boolean createConnectiveSurfaces() throws CSHouseException {
        final Point3d[] coordToParent = getCoordsToParentForConnection();
        if (coordToParent == null) {
            return false;
        } else {
            final Point3d[] coordOnParent = getCoordsOnParent();
            final Surface3D parent = getParentSurface();
            final boolean reverse = parent == null ? false : (parent.
                    calculateSurfaceNormal().z * calculateSurfaceNormal().z <
                    0.0 ?
                    true : false);

            for (int index = 0; index < coordOnParent.length; index++) {
                Surface3D newSurface;
                int upper = (index == (coordOnParent.length - 1)) ? 0 : index + 1;
                Point3d[] coords;
                final Point3d testCenter = new Point3d(ORIGIN_POINT);
                final Transform3D trans = getTransformToParent();
                trans.transform(testCenter);
                coords = reverse ? new Point3d[]{
                        coordOnParent[upper], coordOnParent[index],
                        coordToParent[index], coordToParent[upper]
                } : new Point3d[]{
                        coordToParent[upper], coordToParent[index],
                        coordOnParent[index], coordOnParent[upper]
                };
                if (DimensionUtil.getNormal(coords) == null) {
                    connectBG.addChild(new Surface3D(reverse ?
                            new Point3d[]{coords[2], coords[1], coords[0]} :
                            new Point3d[]{coords[0], coords[1], coords[2]}));
                    connectBG.addChild(new Surface3D(reverse ?
                            new Point3d[]{coords[3], coords[2], coords[0]} :
                            new Point3d[]{coords[0], coords[2], coords[3]}));
                } else {
                    connectBG.addChild(new Surface3D(coords));
                }
            }
            return true;
        }
    }

    /**
     * @return if the Surface3D is visible on parent, if a Surface3D is invisible, then the
     *         sub-surfaces of it will not be shown as well, but the connective Surfaces remain
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @return The Material of this Surface3D default appearance
     */
    public Material getSurfaceAppMaterial() {
        return rawShape.getAppearance().getMaterial();
    }

    /**
     * Sets the Material object to Surface3D private Appearce
     *
     * @param material
     */
    public void setSurfaceAppMaterial(final Material material) {
        if (material instanceof MaterialMeta) {
            rawShape.getAppearance().setMaterial(material);
        } else {
            rawShape.getAppearance().setMaterial(new MaterialMeta(material));
        }
    }

    /**
     * Sets the visibility of this Surface3D
     *
     * @param visible
     * @throws CSHouseException
     */
    public void setVisible(boolean visible) throws CSHouseException {
        if (this.visible != visible) {
            this.visible = visible;
            updateSurface();
        }
    }

    /**
     * Returns the normal of this Surface3D finally appear on Parent
     *
     * @return
     */
    public Vector3d calculateSurfaceNormal() {
        /**
         *
         * Modified Date : 2013-05-27
         * Modified by : Gang Liu
         * Bug Fixed : normal calculated by original method can return NaN values causing exception at invoker side
         * original code was commanded below.
         *
            final Vector3d v1 = new Vector3d(identifyCoord[2].x - identifyCoord[1].x,
                identifyCoord[2].y - identifyCoord[1].y, identifyCoord[2].z -
                identifyCoord[1].z);
            final Vector3d v2 = new Vector3d(identifyCoord[0].x - identifyCoord[1].x,
                identifyCoord[0].y - identifyCoord[1].y, identifyCoord[0].z -
                identifyCoord[1].z);
            v1.normalize();
            v2.normalize();
            final Vector3d test = new Vector3d(v1);
            test.add(v2);
            if (test.length() == 0) {
                return new Vector3d(0.0, 0.0, 0.0);
            } else {
                final Vector3d ret = new Vector3d();
                ret.cross(v1, v2);
                ret.normalize();
                System.out.println("v1 = " + v1 + " v2 = " + v2 + " ret = " + ret);
                return ret;
            }
         */

        final GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setCoordinates(identifyCoord);
        gi.setStripCounts(new int[]{identifyCoord.length});
        final NormalGenerator nomralGenerator = new NormalGenerator();
        nomralGenerator.generateNormals(gi);
        final Vector3f[] normals = gi.getNormals();
        for (final Vector3f nomral : normals) {
            if (nomral.length() != 0.0) {
                return new Vector3d(nomral);
            }
        }
        return new Vector3d();
    }

    /**
     * Returns the Surface3D holding this Surface3D as sub-surface
     *
     * @return Surface3D holding this Surface3D as sub-surface or Null if no such Surface3D is found
     */
    public Surface3D getParentSurface() {
        Node parent = getParent();
        while (!(parent instanceof Surface3D) && (parent != null)) {
            if (parent instanceof CSConnSurfaceBG) {
                return null;
            }
            parent = parent.getParent();
        }
        return (Surface3D) parent;
    }

    /**
     * Gets the Transform3D representing SurfaceMaterial movement on Surface3D
     *
     * @return The texture Transform3D navigating SurfaceMaterial
     */
    public Transform3D getSurfaceMaterialTransform() {
        final TextureUnitState materialTexture = rawShape.getAppearance().
                getTextureUnitState(SURFACE_MATERIAL_INDEX);
        final Transform3D rotTrans = new Transform3D();
        materialTexture.getTextureAttributes().getTextureTransform(rotTrans);
        return rotTrans;
    }

    /**
     * Sets the Transform3D representing SurfaceMaterial movement on Surface3D
     *
     * @param trans
     */
    public void setSurfaceMaterialTransform(final Transform3D trans) {
        final TextureUnitState materialTexture = rawShape.getAppearance().
                getTextureUnitState(SURFACE_MATERIAL_INDEX);
        materialTexture.getTextureAttributes().setTextureTransform(trans);
    }

    /**
     * Removes the SurfaceMaterial from Surface3D
     */
    public void removeSurfaceMaterial() {
        surfaceMaterial = null;
        final TextureUnitState materialTexture = rawShape.getAppearance().
                getTextureUnitState(SURFACE_MATERIAL_INDEX);
        materialTexture.setTexture(null);
        materialTexture.getTextureAttributes().setTextureTransform(
                new Transform3D());
    }

    public Shape3D getIndicatorShape() {
        return indicatorShape;
    }

    /**
     * Tests if current Surface3D can create Connective Surfaces to its parent
     * a Surface3D can create connective surfaces to parent if all the point at the
     * same XY plane side of the parent. that is, all points  Z value are all positive
     * or negative
     *
     * @return Coordinate To Parent Array if connective Surfaces are possible to present
     *         Null otherwise
     * @throws CSHouseException
     */
    public Point3d[] getCoordsToParentForConnection() throws CSHouseException {
        final Point3d[] ctp = getCoordsToParent();
        for (int index = 1; index < ctp.length; index++) {
            if (ctp[index].z * ctp[0].z < 0.0) {
                return null;
            }
        }
        return ctp;
    }

    /**
     * Gets the Surface3D bound area, a Bound area is the area of the outline shape of this Surface3D
     *
     * @return Bound area of current Surface3D
     */
    public double getBoundArea() {
        if (identifyCoord == null) {
            return 0.0;
        }
        double ret = 0.0;
        for (int index = 0; index < identifyCoord.length - 1; index++) {
            ret += identifyCoord[index].x * identifyCoord[index + 1].y -
                    identifyCoord[index].y * identifyCoord[index + 1].x;
        }
        ret += identifyCoord[identifyCoord.length - 1].x * identifyCoord[0].y -
                identifyCoord[identifyCoord.length - 1].y * identifyCoord[0].x;
        return ret / 2;
    }

    /**
     * The exact area value of this Surface3D, sub-surface is removed. this value could be used
     * to calculate Surface cost
     *
     * @return exact area of this Surface3D
     */
    public double getExactArea() {
        double ret = getBoundArea();
        if (ret == 0.0) {
            return 0.0;
        }
        for (final Surface3D subSurface : getSubSurfaces()) {
            ret -= subSurface.getBoundArea();
        }
        return ret;
    }

    /**
     * Gets the Transform3D from Surface3D identified Shape to world coordinate system transformation
     *
     * @return Transform3D
     */
    public Transform3D getRawShapeToWorldTrans() {
        final Transform3D ret = new Transform3D();
        rawShape.getLocalToVworld(ret);
        return ret;
    }

    public TransformGroup getInitTransTG() {
        return surfaceTG[INITIAL_TRANS_TG];
    }

    public BranchGroup getAttachmentBG() {
        return attachmentBG;
    }

    public Set<Furniture> getFurnitureAttached() {
        final Set<Furniture> ret = new HashSet<Furniture>();
        final Enumeration attachedKids = attachmentBG.getAllChildren();
        while (attachedKids.hasMoreElements()) {
            final Object child = attachedKids.nextElement();
            if (child instanceof Furniture) {
                ret.add((Furniture) child);
            }
        }
        return ret;
    }

    /**
     * Set<Furniture > losses furniture whiile saving and loading
     *
     * @return
     */
    public List<Furniture> getFurnitureAttachedList() {
        final List<Furniture> ret = new ArrayList<Furniture>();
        final Enumeration attachedKids = attachmentBG.getAllChildren();
        while (attachedKids.hasMoreElements()) {
            final Object child = attachedKids.nextElement();
            if (child instanceof Furniture) {
                ret.add((Furniture) child);
            }
        }
        return ret;
    }

    /**
     * This method returns MerchdiseInfo of this Surface3D MerchandiseInfo could be used to statistic the merchandise
     * quantity
     *
     * @return MerchdiseInfo
     * @throws CSHouseException
     */
    public MerchandiseInfo getMerchandiseInfo() throws CSHouseException {
        final MerchandiseInfo ret = new MerchandiseInfo();
        if (surfaceMaterial != null) {
            ret.addMerchandise(surfaceMaterial, new Double(Math.abs(getExactArea())));
        }
        for (int index = 0; index < attachmentBG.numChildren(); index++) {
            final Object child = attachmentBG.getChild(index);
            if (child instanceof Furniture) {
                ret.addMerchandise((Merchandise) child, 1.0);
            }
        }

        for (final Surface3D subSurface : getSubSurfaces()) {
            ret.addMerchandiseCounter(subSurface.getMerchandiseInfo());
        }

        for (final Surface3D connSurface : getConnectiveSurfaces()) {
            ret.addMerchandiseCounter(connSurface.getMerchandiseInfo());
        }
        return ret;
    }

    public Point3d getIdentiCenter() throws CSHouseException {
        final Point3d max = new Point3d(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        final Point3d min = new Point3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        for (final Point3d point3d : getIdentifiedCoords()) {
            if (point3d.x < min.x) {
                min.setX(point3d.x);
            } else if (point3d.x > max.x) {
                max.setX(point3d.x);
            }
            if (point3d.y < min.y) {
                min.setY(point3d.y);
            } else if (point3d.y > max.y) {
                max.setY(point3d.y);
            }
            if (point3d.z < min.z) {
                min.setZ(point3d.z);
            } else if (point3d.z > max.z) {
                max.setZ(point3d.z);
            }
        }

        max.setX(max.x + min.x);
        max.setY(max.y + min.y);
        max.setZ(max.z + min.z);
        return max;
    }

    public Point3d getCenterToParent() throws CSHouseException {
        final Transform3D trans = getTransformToParent();
        final Point3d center = getIdentiCenter();
        trans.transform(center);
        return center;
    }

    /**
     * This method takes world coordinate position and transform it to the coordinate in current Surface3D coordinate
     * System
     *
     * @param worldCoordinate
     * @return
     */
    public Point3d getLocalCoordFromWorld(final Point3d worldCoordinate) {
        final Point3d ret = new Point3d(worldCoordinate);
        final Transform3D localTrans = new Transform3D();
        rawShape.getLocalToVworld(localTrans);
        localTrans.invert();
        localTrans.transform(ret);
        return ret;
    }

    /**
     * @return Dimension of this Surface3D in 2D
     */
    public double[] getSurfaceDimension() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        for (final Point3d point : identifyCoord) {
            if (point.x > maxX) {
                maxX = point.x;
            } else if (point.x < minX) {
                minX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            } else if (point.y < minY) {
                minY = point.y;
            }
        }
        return new double[]{Math.abs(maxX - minX), Math.abs(maxY - minY)};
    }

    /*
    * Package Methods
    */

    Appearance getRawShapeAppearance() {
        return rawShape.getAppearance();
    }

    /*
    * Protected Methods-----------------------------------------------------------------
    */

    /**
     * Surface3D
     * --------ConnectBG
     * --------CustomizableTG
     * ------------InitRotTG
     * ----------------InitTransTG
     * ---------------------IndicateShape
     * ---------------------RawShape
     * ---------------------SubSurfaceBG
     * ---------------------AttachmentBG
     */
    private void init() {
        connectBG = new CSConnSurfaceBG();
        addChild(connectBG);

        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ALLOW_PICKABLE_READ);
        setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
        setCapability(BranchGroup.ENABLE_PICK_REPORTING);

        surfaceTG = new CSTransformGroup[]{
                new CSTransformGroup(), new CSTransformGroup(), new CSTransformGroup()
        };
        addChild(surfaceTG[CUTOMIZABLE_TRANS_TG]);
        surfaceTG[CUTOMIZABLE_TRANS_TG].addChild(surfaceTG[INITIAL_ROT_SCA_TG]);
        surfaceTG[INITIAL_ROT_SCA_TG].addChild(surfaceTG[INITIAL_TRANS_TG]);

        rawShape = new Shape3D();
        rawShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        rawShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        rawShape.setCapability(Shape3D.ALLOW_COLLISION_BOUNDS_READ);
        rawShape.setCapability(Shape3D.ALLOW_COLLISION_BOUNDS_WRITE);
        rawShape.setCapability(Shape3D.ALLOW_PICKABLE_READ);
        rawShape.setCapability(Shape3D.ALLOW_PICKABLE_WRITE);
        rawShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
        rawShape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        rawShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        rawShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        surfaceTG[INITIAL_TRANS_TG].addChild(rawShape);

        subBG = new BranchGroup();
        subBG.setCapability(BranchGroup.ALLOW_DETACH);
        subBG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        subBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        subBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        surfaceTG[INITIAL_TRANS_TG].addChild(subBG);

        attachmentBG = new BranchGroup();
        attachmentBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        attachmentBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        attachmentBG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        surfaceTG[INITIAL_TRANS_TG].addChild(attachmentBG);

        final AppearanceMeta rawShapeApp = new AppearanceMeta();
        rawShape.setAppearance(rawShapeApp);
        initAppearance(rawShapeApp);

        indicatorShape = new Shape3D();
        indicatorShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        indicatorShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        indicatorShape.setCapability(Shape3D.ALLOW_COLLIDABLE_READ);
        indicatorShape.setCapability(Shape3D.ALLOW_COLLIDABLE_WRITE);

        final Appearance indicShapeApp = new Appearance();
        indicShapeApp.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
        indicShapeApp.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
        indicShapeApp.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        indicShapeApp.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

        final PolygonAttributes indPa = new PolygonAttributes();
        indPa.setCullFace(PolygonAttributes.CULL_NONE);
        indicShapeApp.setPolygonAttributes(indPa);

        final ColoringAttributes indCas =
                new ColoringAttributes(new Color3f(Color.BLACK),
                        ColoringAttributes.FASTEST);
        final Material indM = new Material();
        indM.setLightingEnable(false);

        final TransparencyAttributes indicShapeTransparency =
                new TransparencyAttributes();
        indicShapeTransparency.setTransparency(0.8f);
        indicShapeTransparency.setTransparencyMode(
                TransparencyAttributes.FASTEST);
        final PolygonAttributes indicatorShapePA = new PolygonAttributes();
        indicatorShapePA.setCullFace(PolygonAttributes.CULL_NONE);
        indicShapeApp.setPolygonAttributes(indicatorShapePA);
        indicShapeApp.setColoringAttributes(indCas);
        indicShapeApp.setMaterial(indM);
        indicShapeApp.setTransparencyAttributes(indicShapeTransparency);
        indicatorShape.setAppearance(indicShapeApp);
        surfaceTG[INITIAL_TRANS_TG].addChild(indicatorShape);
    }

    private void initAppearance(final Appearance app) {
        app.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_MATERIAL_READ);
        app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        app.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
        app.setCapability(Appearance.ALLOW_POINT_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_POINT_ATTRIBUTES_WRITE);
        app.setCapability(Appearance.ALLOW_POINT_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_POINT_ATTRIBUTES_WRITE);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        final TextureUnitState[] tuss = new TextureUnitState[3];
        for (int index = 0; index < tuss.length; index++) {
            tuss[index] = new TextureUnitState();
            final TexCoordGeneration tcg = new TexCoordGeneration();
            tcg.setCapability(TexCoordGeneration.ALLOW_PLANE_READ);
            tcg.setCapability(TexCoordGeneration.ALLOW_PLANE_WRITE);
            tcg.setCapability(TexCoordGeneration.ALLOW_ENABLE_READ);
            tcg.setCapability(TexCoordGeneration.ALLOW_ENABLE_WRITE);
            tcg.setCapability(TexCoordGeneration.ALLOW_FORMAT_READ);
            tcg.setCapability(TexCoordGeneration.ALLOW_MODE_READ);
            tuss[index].setTexCoordGeneration(tcg);
            final TextureAttributes ta = new TextureAttributes();
            ta.setCapability(TextureAttributes.ALLOW_MODE_READ);
            ta.setCapability(TextureAttributes.ALLOW_MODE_WRITE);
            ta.setCapability(TextureAttributes.ALLOW_BLEND_COLOR_READ);
            ta.setCapability(TextureAttributes.ALLOW_BLEND_COLOR_WRITE);
            ta.setCapability(TextureAttributes.ALLOW_COMBINE_READ);
            ta.setCapability(TextureAttributes.ALLOW_COMBINE_WRITE);
            ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_READ);
            ta.setCapability(TextureAttributes.ALLOW_TRANSFORM_WRITE);
            tuss[index].setTextureAttributes(ta);
            tuss[index].setTexture(null);
            tuss[index].setCapability(TextureUnitState.ALLOW_STATE_READ);
            tuss[index].setCapability(TextureUnitState.ALLOW_STATE_WRITE);
        }
        app.setTextureUnitState(tuss);

        final PolygonAttributes pa = new PolygonAttributes();
        pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_READ);
        pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);
        app.setPolygonAttributes(pa);

        final PointAttributes rawShapePointAttributes = new PointAttributes();
        rawShapePointAttributes.setCapability(
                PointAttributes.ALLOW_ANTIALIASING_READ);
        rawShapePointAttributes.setCapability(
                PointAttributes.ALLOW_ANTIALIASING_WRITE);
        rawShapePointAttributes.setPointAntialiasingEnable(true);
        app.setPointAttributes(rawShapePointAttributes);

        final ColoringAttributes rawShapeColoringAttributes =
                new ColoringAttributes();
        rawShapeColoringAttributes.setCapability(
                ColoringAttributes.ALLOW_COLOR_READ);
        rawShapeColoringAttributes.setCapability(
                ColoringAttributes.ALLOW_COLOR_WRITE);
        rawShapeColoringAttributes.setColor(new Color3f(new Color(0xdddddd)));
        app.setColoringAttributes(rawShapeColoringAttributes);

        final TextureUnitState tus = app.getTextureUnitState(
                SURFACE_INDICATE_INDEX);
        final TexCoordGeneration tcg = tus.getTexCoordGeneration();
        final TextureAttributes ta = tus.getTextureAttributes();
        ta.setTextureMode(TextureAttributes.MODULATE);
        tcg.setPlaneS(new Vector4f(1f, 0f, 0f, 0f));
        tcg.setPlaneT(new Vector4f(0f, 1f, 0f, 0f));

        final MaterialMeta meta = new MaterialMeta();
        meta.setCapability(Material.ALLOW_COMPONENT_READ);
        meta.setCapability(Material.ALLOW_COMPONENT_WRITE);
        meta.setLightingEnable(true);
        app.setMaterial(meta);
    }

    private void setIdentifiedCoords(final Point3d[] coords) throws
            CSHouseException {
        /*
         * This check was placed here to guarantee all coordinates are on XY Plane but operations making random flat surface
         * can cause this condition fail occasionally.
         *
         * Modifier : Gang Liu
         */
//        for (final Point3d coord : coords) {
//            if (!RealNumberOperator.compareTwoDouble(coord.z, 0.0)) {
//                throw new CSHouseException("Identified coord about to be set is not on XY Plane " +
//                        coord);
//            }
//        }
        final Point3d center = DimensionUtil.getCenter(coords);
        if (!RealNumberOperator.compareTwoTuple3d(center, ORIGIN_POINT)) {
            throw new CSHouseException("Identified coordinate does not centered at Origin point " +
                    center);
        }
        this.identifyCoord = coords;
    }

    private void setCoordToParent(final Point3d[] coords) throws
            CSHouseException {
        if (coords.length < 3) {
            final StringBuilder sb = new StringBuilder("Can not form a Surface with less than three Points");
            for (final Point3d point : coords) {
                sb.append(" ").append(point.toString());
            }
            throw new CSHouseException(sb.toString());
        }
        final Vector3d normal = DimensionUtil.getNormal(coords);
        if (normal == null) {
            final GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
            gi.setCoordinates(coords);
            gi.setStripCounts(new int[]{coords.length});
            final NormalGenerator ng = new NormalGenerator();
            ng.generateNormals(gi);
            throw new CSHouseException("Not all points are on same Surface");
        }
        try {
            final Point3d[] tempCoods = new Point3d[coords.length];
            for (int index = 0; index < coords.length; index++) {
                tempCoods[index] = new Point3d(coords[index]);
            }
            final Transform3D rotation = normal.getZ() < 0.0 ? DimensionUtil.
                    getTransformToNegaZ(normal) : DimensionUtil.getTransformToZ(
                    normal);
            if (normal.getZ() < 0.0) {
                Point3d temp = null;
                rotation.mul(DimensionUtil.REVERSE_Y);
            }
            rotation.invert();
            surfaceTG[INITIAL_ROT_SCA_TG].setTransform(rotation);
            rotation.invert();

            for (final Point3d tc : tempCoods) {
                rotation.transform(tc);
            }

            final Transform3D translation = new Transform3D();
            final Point3d center = DimensionUtil.getCenter(tempCoods);
            translation.setTranslation(new Vector3d(center));
            surfaceTG[INITIAL_TRANS_TG].setTransform(translation);
            translation.invert();

            for (final Point3d tc : tempCoods) {
                translation.transform(tc);
            }
            setIdentifiedCoords(tempCoods);
        } catch (CSUtilException ex) {
            throw new CSHouseException(ex);
        }
    }

    private TextureUnitState initSurfaceMaterial(final SurfaceMaterial surfaceMaterial) {
        this.surfaceMaterial = surfaceMaterial;
        final TextureUnitState materialTexture = rawShape.getAppearance().getTextureUnitState(SURFACE_MATERIAL_INDEX);
        materialTexture.setTexture(this.surfaceMaterial.getImageTexture());
        if (this.surfaceMaterial.isStretch()) {
            final double[] surfaceDimension = getSurfaceDimension();
            this.surfaceMaterial.setWidth(surfaceDimension[0]);
            this.surfaceMaterial.setHeight(surfaceDimension[1]);
        }
        materialTexture.getTexCoordGeneration().setPlaneS(
                new Vector4f((float) (1 / this.surfaceMaterial.getWidth()), 0f, 0f,
                        0f));
        materialTexture.getTexCoordGeneration().setPlaneT(
                new Vector4f(0f, (float) (1 / this.surfaceMaterial.getHeight()), 0f,
                        0f));
        if (this.surfaceMaterial.isStretch()) {
            final Transform3D trans = new Transform3D();
            trans.setTranslation(new Vector3d(.5, .5, .0));
            materialTexture.getTextureAttributes().setTextureTransform(trans);
        }
        System.gc();
        return materialTexture;
    }

    private void reverseIdentifiedCoords() throws CSHouseException {
        Point3d temp;
        for (int start = 0, end = identifyCoord.length - 1; start <
                identifyCoord.length; start++, end--) {
            temp = identifyCoord[start];
            identifyCoord[start] = identifyCoord[end];
            identifyCoord[end] = temp;
        }
        updateSurface();
    }

    void updateSurface() throws CSHouseException {
        rawShape.removeAllGeometries();
        if (identifyCoord != null && visible) {
            final Set<Surface3D> subSurfaces = getSubSurfaces();
            final List<Point3d> coordList = new ArrayList<Point3d>();
            final List<Integer> stripCount = new ArrayList<Integer>();
            for (final Point3d point : identifyCoord) {
                coordList.add(point);
            }
            stripCount.add(new Integer(identifyCoord.length));

            Point3d[] tempData;
            for (final Surface3D subSurface : subSurfaces) {
                tempData = subSurface.getCoordsOnParent();
                for (final Point3d point : tempData) {
                    coordList.add(point);
                }
                stripCount.add(new Integer(tempData.length));
            }

            final Point3d[] coords = new Point3d[coordList.size()];
            for (int index = 0; index < coords.length; index++) {
                coords[index] = coordList.get(index);
            }

            final int[] stripCounts = new int[stripCount.size()];
            for (int index = 0; index < stripCounts.length; index++) {
                stripCounts[index] = stripCount.get(index).intValue();
            }

            final GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
            gi.setCoordinates(coords);
            gi.setStripCounts(stripCounts);
            gi.setContourCounts(new int[]{stripCounts.length});

            final NormalGenerator ng = new NormalGenerator();
            ng.generateNormals(gi);
            final Stripifier sp = new Stripifier();
            sp.stripify(gi);
            rawShape.addGeometry(gi.getGeometryArray());
        }
    }

    @Nullable
    private Point3d getIntersection(Point3d line1, Point3d line2,
                                    Point3d plane1, Point3d plane2, Point3d plane3) {
        Vector3d p1 = new Vector3d(plane1);
        Vector3d p2 = new Vector3d(plane2);
        Vector3d p3 = new Vector3d(plane3);
        Vector3d p2minusp1 = new Vector3d(p2);
        p2minusp1.sub(p1);
        Vector3d p3minusp1 = new Vector3d(p3);
        p3minusp1.sub(p1);
        Vector3d normal = new Vector3d();
        normal.cross(p2minusp1, p3minusp1);
        // The plane can be defined by p1, n + d = 0
        double d = -p1.dot(normal);
        Vector3d i1 = new Vector3d(line1);
        Vector3d direction = new Vector3d(line1);
        direction.sub(line2);
        double dot = direction.dot(normal);
        if (dot == 0) {
            return null;
        }
        double t = (-d - i1.dot(normal)) / (dot);
        Vector3d intersection = new Vector3d(line1);
        Vector3d scaledDirection = new Vector3d(direction);
        scaledDirection.scale(t);
        intersection.add(scaledDirection);
        Point3d intersectionPoint = new Point3d(intersection);
        return intersectionPoint;
    }

    private void mergeMerchandiseInfo(final Map<Merchandise, Double> subject, final Map<Merchandise, Double> object) {
        for (final Merchandise merchandise : object.keySet()) {
            if (subject.get(merchandise) == null) {
                subject.put(merchandise, object.get(merchandise));
            } else {
                subject.put(merchandise, new Double(
                        subject.get(merchandise).doubleValue() + object.get(merchandise).doubleValue()));
            }
        }
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        try {
            oos.writeBoolean(visible);
            oos.writeBoolean(customizable);

            //Material
            final Material material = rawShape.getAppearance().getMaterial();
            if (material == null) {
                oos.writeObject(null);
            } else {
                oos.writeObject(new MaterialMeta(material));
            }

            //Coordinates To Parents
            final Point3d[] ctp = getCoordsToParent();
            oos.writeInt(ctp.length);
            for (final Point3d point : ctp) {
                oos.writeObject(point);
            }

            //SubSurface
            final Set<Surface3D> subSurfaces = getSubSurfaces();
            oos.writeInt(subSurfaces == null ? 0 : subSurfaces.size());
            for (final Surface3D subSurface : subSurfaces) {
                oos.writeObject(subSurface);
            }

            //Connective Surfaces
            final Set<Surface3D> connSurfaces = getConnectiveSurfaces();
            oos.writeInt(connSurfaces == null ? 0 : connSurfaces.size());
            for (final Surface3D connSurface : connSurfaces) {
                oos.writeObject(connSurface);
            }

            //Writes Surface Material attached to this Surface3D
            if (surfaceMaterial != null) {
                oos.writeObject(surfaceMaterial.getId());
                oos.writeObject(transToDouble(getSurfaceMaterialTransform()));
            } else {
                oos.writeObject(SurfaceMaterial.NULL_SURFACE_MATERIAL);
            }

            //Writes Merchandises attached to this Surface3D to byte
            final List<Furniture> attchedFurniture = getFurnitureAttachedList();
            oos.writeInt(attchedFurniture.size());
            for (final Furniture furniture : attchedFurniture) {
                oos.writeObject(furniture.getId());
                oos.writeObject(furniture.getPositionOnParent());
                oos.writeObject(transToDouble(furniture.getRotationOnParent()));
                oos.writeBoolean(furniture.isInstalled());
            }
            if (visible) {
                oos.writeObject(new Shape3DMeta(rawShape.getGeometry()));
            }
            oos.flush();
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        initiate();
        System.gc();
        try {
            visible = ois.readBoolean();
            customizable = ois.readBoolean();
            final LocalStorage localStorage = LocalStorage.getLocalStorage();
            init();
            final Appearance app = new Appearance();
            initAppearance(app);
            final Object material = ois.readObject();
            if (material != null && material instanceof MaterialMeta) {
                app.setMaterial((Material) material);
            }
            rawShape.setAppearance(app);

            //Coordinates to Parents
            final Point3d[] pts = new Point3d[ois.readInt()];
            for (int index = 0; index < pts.length; index++) {
                pts[index] = (Point3d) ois.readObject();
            }
            try {
                setCoordToParent(pts);
            } catch (Exception ex) {
                throw new IOException(ex);
            }

            int subSurfaceSize = ois.readInt();
            for (int index = 0; index < subSurfaceSize; index++) {
                try {
                    addSubSurface((Surface3D) ois.readObject());
                } catch (Exception ex) {
                    throw new IOException(ex);
                }
            }

            int connSurfaceSize = ois.readInt();
            for (int index = 0; index < connSurfaceSize; index++) {
                connectBG.addChild((Surface3D) ois.readObject());
            }

            //Reads Surface Material

            final String surfaceMaterialId = (String) ois.readObject();
            SurfaceMaterial surfaceMateria = null;
            Transform3D trans = null;
            if (!surfaceMaterialId.equals(SurfaceMaterial.NULL_SURFACE_MATERIAL)) {
                surfaceMateria =
                        (SurfaceMaterial) localStorage.readMerchandise(surfaceMaterialId, SurfaceMaterial.class, this);
                trans = doubleToTrans((double[]) ois.readObject());
            }


            //Reads Merchandise
            final int furnitureCnt = ois.readInt();
            for (int index = 0; index < furnitureCnt; index++) {
                final Furniture furniture = (Furniture) localStorage.readMerchandise((String) ois.readObject(), Furniture.class, this);
                furniture.setPositionOnParent((Tuple3d) ois.readObject());
                furniture.setRotationOnParent(doubleToTrans((double[]) ois.readObject()));
                furniture.setInstalled(ois.readBoolean());
                attachmentBG.addChild(furniture);
            }
            Shape3D tempShape = null;
            try {
                if (visible) {
                    tempShape = (Shape3D) ois.readObject();
                }
            } catch (OptionalDataException e) {
                updateSurface();
            }
            if (tempShape != null) {
                rawShape.removeAllGeometries();
                rawShape.addGeometry(tempShape.getGeometry());
            } else {
                updateSurface();
            }
            if (!surfaceMaterialId.equals(SurfaceMaterial.NULL_SURFACE_MATERIAL) && surfaceMateria != null) {
                setSurfaceMaterial(surfaceMateria);
                setSurfaceMaterialTransform(trans);
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
        }
    }

    private Transform3D doubleToTrans(final double[] data) throws CSHouseException {
        if (data == null && data.length != 16) {
            throw new CSHouseException(
                    new IllegalArgumentException("Data forming Transform3D is illegal")
            );
        }
        return new Transform3D(data);
    }

    private double[] transToDouble(final Transform3D transform3D) {
        final double[] ret = new double[16];
        transform3D.get(ret);
        return ret;
    }

    /*
     * Testing Methods..............................................................
     */
    public static void main(String[] args) throws CSHouseException, AWTException, IOException, CSPersistException, ClassNotFoundException {
        final Surface3D s1 = new Surface3D(new Point3d[]{
                new Point3d(-2.0, -2.0, 0.0), new Point3d(2.0, -2.0, 0.0),
                new Point3d(2.0, 2.0, 0.0), new Point3d(-2.0, 2.0, 0.0)
        });

        final SurfaceMaterial sm = new SurfaceMaterial("001", 1.0, 1.0, false, new BufferedImageMeta(ImageIO.read(new File("/home/gang-liu/Pictures/4.jpg"))));
        sm.setStretch(true);
        sm.setReflective(false);
//        s1.setSurfaceMaterial(sm);

        s1.setLightenColor(Color.ORANGE, Color.YELLOW, Color.WHITE);
        final Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(0.5, 0.5, 0.0));
//        s1.setLightenColor(new Color(0x333333), new Color(0x777777), new Color(0xaaaaaa));
        s1.setLightenColor(Color.RED, Color.GREEN, Color.BLUE);
//        s1.setSurfaceMaterialTransform(trans);

        final BranchGroup root = new BranchGroup();
//        root.addChild(new ColorCube(0.3));

        final ModelDisplayer md = new ModelDisplayer();
        final JFrame jf = new JFrame();
        jf.setSize(new Dimension(700, 400));
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.getContentPane().add(md);

        root.addChild(ModelDisplayer.createAxies(20.0));
        root.addChild(s1);
//        Wall.renderDefaultWallSurface(s1);
        final Light light = new AmbientLight(new Color3f(new Color(0xffffff)));
        light.setEnable(true);
        light.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));

//        final Light lamb = new PointLight(new Color3f(Color.white), new Point3f(0.0f, 0.0f, 0.2f),
//                new Point3f(.2f, .2f, .2f));
        final Light lamb = new PointLight();
        lamb.setColor(new Color3f(Color.white));
        ((PointLight) lamb).setPosition(.0f, .0f, 1.8f);
        ((PointLight) lamb).setAttenuation(1.0f, 0.045f, 0.0075f);
        lamb.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        lamb.setEnable(true);

        final Light lamb1 = new PointLight();
        lamb1.setColor(new Color3f(Color.white));
        ((PointLight) lamb1).setPosition(.0f, 0.8f, 1.8f);
        ((PointLight) lamb1).setAttenuation(1.0f, 0.045f, 0.0075f);
        lamb1.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        lamb1.setEnable(true);
        final GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setCoordinates(new Point3d[]{
                new Point3d(-2.0, -2.0, 0.0), new Point3d(2.0, -2.0, 0.0),
                new Point3d(2.0, 2.0, 0.0), new Point3d(-2.0, 2.0, 0.0)
        });
        gi.setStripCounts(new int[]{4});
        gi.setContourCounts(new int[]{1});
        final NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gi);
        final Stripifier sp = new Stripifier();
        sp.stripify(gi);

        final TransformGroup tg1 = new TransformGroup();
        final Transform3D tr = new Transform3D();
        tr.setTranslation(new Vector3d(0.0, 0.01, 0.03));
        tg1.setTransform(tr);

        final Appearance app1 = new Appearance();
        final Material ma = new Material();
        ma.setLightingEnable(true);
        ma.setAmbientColor(new Color3f(Color.RED));
        ma.setDiffuseColor(new Color3f(Color.GREEN));
        ma.setSpecularColor(new Color3f(Color.BLUE));
        app1.setMaterial(ma);

//        tg1.addChild(new Shape3D(gi.getGeometryArray(), app1));


        final Sphere cb = new Sphere(0.04f);
        final Appearance app = cb.getAppearance();
        final Material m = new Material();
        m.setLightingEnable(true);
        m.setAmbientColor(new Color3f(Color.red));
        m.setDiffuseColor(new Color3f(Color.green));
        m.setSpecularColor(new Color3f(Color.BLUE));
        app.setMaterial(m);
        root.addChild(cb);
        root.addChild(tg1);

//        root.addChild(light);
        md.addLight(light);
        md.addLight(new DirectionalLight(true, new Color3f(Color.blue), new Vector3f(0.0f, -0.1f, -0.1f)));
        md.addLight(lamb);
        md.addLight(lamb1);
        final TransformGroup tg = new TransformGroup();
        final Transform3D ts = new Transform3D();
        ts.setTranslation(new Vector3d(0.0, 0.0, 0.2));
        tg.setTransform(ts);
        tg.addChild(new ColorCube(0.04));
        root.addChild(tg);

        md.setModel(root, false);
        md.setBackGroundColor(Color.ORANGE);

    }


}
