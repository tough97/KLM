package com.klm.persist.impl;


import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.CSTransformGroup;
import com.klm.cons.impl.Surface3D;
import com.klm.cons.impl.Wall;
import com.klm.material.Installable;
import com.klm.persist.Merchandise;
import com.klm.persist.meta.ModelMeta;
import com.sun.j3d.utils.scenegraph.io.NamedObjectException;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Furniture extends Merchandise implements Installable, Serializable {
    public static final String EXTENSION = ".fu";
    public static final String FILE_NAME_CONNECTOR = "_";
    public static final String NULL_SURFACE_MATERIAL = "@nf";

    private CSTransformGroup rotScaleTG;
    private CSTransformGroup translationTG;
    private ModelMeta model;
    private boolean installed = false;
    private Surface3D installHoleSurface = null;
    private static final long serialVersionUID = 100;

    public Furniture(final String id) {
        super(id);
        init();
    }

    public Furniture(final String id, final ModelMeta model) {
        super(id);
        init();
        this.model = model;
        rotScaleTG.addChild(model);
    }

    private void init() {
        rotScaleTG = new CSTransformGroup();
        translationTG = new CSTransformGroup();
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        addChild(translationTG);
        rotScaleTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        rotScaleTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        rotScaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        rotScaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        translationTG.addChild(rotScaleTG);

    }

    public Transform3D getTranslationTransform() {
        final Transform3D ret = new Transform3D();
        translationTG.getTransform(ret);
        return ret;
    }

    public void setTranslationTransform(final Transform3D trans) {
        translationTG.setTransform(trans);
    }

    public Tuple3d getPositionOnParent() {
        final Point3d ret = new Point3d(0, 0, 0);
        final Transform3D trans = getTranslationTransform();
        trans.transform(ret);
        return ret;
    }

    public void setPositionOnParent(final Tuple3d position) {
        final Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(position));
        translationTG.setTransform(trans);
    }

    public Transform3D getRotationOnParent() {
        final Transform3D ret = new Transform3D();
        rotScaleTG.getTransform(ret);
        return ret;
    }

    public void setRotationOnParent(Transform3D rotation) {
        rotScaleTG.setTransform(rotation);
    }

    public Transform3D getModelToVWorldTrans() {
        final Transform3D ret = new Transform3D();
        model.getLocalToVworld(ret);
        return ret;
    }

    public TransformGroup getTranslationTG() {
        return translationTG;
    }

    public TransformGroup getRotScaleTG() {
        return rotScaleTG;
    }

    public Surface3D getParentSurface() {
        Node parent = this;
        while ((parent = parent.getParent()) != null) {
            if (parent instanceof Surface3D) {
                return (Surface3D) parent;
            }
        }
        return null;
    }

    public void attachToSurface(final Surface3D surface) {
        try {
            attachToSurface(surface, new Vector3d(0, 0, 0));
        } catch (CSHouseException ex) {
            ex.printStackTrace();
        }
    }

    public void attachToSurface(final Surface3D surface, final Vector3d position) throws CSHouseException {
        detachFromParent();
        surface.getAttachmentBG().addChild(this);
    }

    public Surface3D detachFromParent() {
        final Surface3D parentSurface = getParentSurface();
        if (parentSurface != null) {
            parentSurface.getAttachmentBG().removeChild(this);
//            final BranchGroup dummy = new BranchGroup();
//            parentSurface.getAttachmentBG().addChild(dummy);
//            parentSurface.getAttachmentBG().removeChild(dummy);
        }
        return parentSurface;
    }

    public void install() throws CSHouseException {
        if (!installed) {
            final Transform3D t = translationTG.getTransform();
            t.mul(rotScaleTG.getTransform());
            final Point3d[] coords = model.getIdentifiedInstallCoords();
            for (final Point3d point : coords) {
                t.transform(point);
            }
            final Surface3D parentSurface = getParentSurface();
            final Wall wall = (Wall) parentSurface.getFirstParentOf(Wall.class);
            if (wall != null) {
                try {
                    installHoleSurface = wall.drillHole(coords, parentSurface);
                    final Vector3d transV = new Vector3d();
                    t.get(transV);
                    transV.setZ(model.getWidth() * parentSurface.calculateSurfaceNormal().getZ() / -2);
                    model.hideOutLines();
                    translationTG.addTranslation(transV);
                } catch (Exception ex) {
                    throw new CSHouseException(ex);
                }
                installed = true;
            }
        }
    }

    public void unInstall() throws CSHouseException {
        if (installed) {
            if (installHoleSurface == null) {
                throw new CSHouseException("IS not found");
            }
            final Vector3d currentPosition = new Vector3d(getPositionOnParent());
            currentPosition.setZ(0.0);
            setPositionOnParent(currentPosition);
            final Wall wall = (Wall) installHoleSurface.getFirstParentOf(Wall.class);
            wall.removeHole(installHoleSurface);
            installHoleSurface = null;
            installed = false;
        }
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(final boolean installed){
        this.installed = installed;
    }

    @Override
    public String getFileName() {
        final String id = getId();
        final StringBuilder sb = new StringBuilder(getId());
        sb.append(FILE_NAME_CONNECTOR).append(serialVersionUID).append(EXTENSION);
        return sb.toString();
    }

    public ModelMeta getModel() {
        return model;
    }

    public void setModel(final ModelMeta model) {
        rotScaleTG.removeAllChildren();
        this.model = model;
        rotScaleTG.addChild(model);
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException,
            NamedObjectException {
        oos.writeObject(model);
        oos.writeBoolean(installed);
    }

    private void readObject(final ObjectInputStream objectInputStream)
            throws ClassNotFoundException, IOException {
        initiate();
        init();
        setModel((ModelMeta) objectInputStream.readObject());
        installed = objectInputStream.readBoolean();
    }

    @Override
    public boolean equals(final Object obj){
        if(obj instanceof Furniture){
            return getId() == ((Merchandise) obj).getId() && obj.getClass().equals(this.getClass());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
        return getId().hashCode() + getClass().hashCode();
    }

    public Furniture clone(){
    final Furniture cloneFurniture=new Furniture(this.id,  this.model.clone());
    cloneFurniture.name= this.name;
    cloneFurniture.description=this.description;
    cloneFurniture.mp = this.mp;
    cloneFurniture.installationPrice = this.installationPrice;
    cloneFurniture.unitPrice = this.unitPrice;
    cloneFurniture.logisticsPrice = this.logisticsPrice;
    cloneFurniture.unitName=this.unitName;
    cloneFurniture.providerID=this.providerID;
    return cloneFurniture;
    }
    protected  void finalize(){
        System.out.println(getId());
    }
}
