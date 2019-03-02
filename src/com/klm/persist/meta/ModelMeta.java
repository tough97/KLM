package com.klm.persist.meta;

import com.klm.cons.impl.CSTransformGroup;
import com.klm.util.RealNumberOperator;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.scenegraph.io.NamedObjectException;
import cs.designer.module.ModelBoundingBox;
import cs.designer.utils.ShapeUtill;

import javax.media.j3d.*;
import javax.vecmath.*;
import javax.xml.crypto.dsig.Transform;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class ModelMeta extends BranchGroup implements Serializable {
    private BranchGroup baseModel;
    private Point3d lower;
    private Point3d upper;
    private Vector3d installVector = new Vector3d(0, 0, 1);
    private int countChild = 0;
    private ModelBoundingBox modelBoundingBox;
    private static final long serialVersionUID = 100;
    private boolean outLineVisible = false;
    private Point3d[] installCoords;
    private CSTransformGroup rotScaleTG;
    private CSTransformGroup translationTG;
    private double length;
    private double width;
    private double height;


    public ModelMeta() {
        super();
        init();
    }

    private void init() {
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ALLOW_DETACH);
        rotScaleTG = new CSTransformGroup();
        translationTG = new CSTransformGroup();
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        rotScaleTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        rotScaleTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        rotScaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        rotScaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        rotScaleTG.addChild(translationTG);
        addChild(rotScaleTG);
    }


    public Point3d[] getIdentifiedInstallCoords() {
        if (installCoords != null) {
            Point3d[] currtInstallCoords = new Point3d[installCoords.length];
            for (int index = 0; index < installCoords.length; index++) {
                currtInstallCoords[index] = new Point3d(installCoords[index].
                        getX(), installCoords[index].getY(), 0);
            }
            return currtInstallCoords;
        }
        return null;
    }

    public void showOutLines() {
        if (!outLineVisible) {
            if (modelBoundingBox == null) {
                modelBoundingBox = new ModelBoundingBox(lower, upper);
            }
            if (modelBoundingBox.getParent() == null) {
                translationTG.addChild(modelBoundingBox);
            }
            outLineVisible = true;
        }

    }

    public void hideOutLines() {
        if (outLineVisible) {
            modelBoundingBox.detach();
            outLineVisible = false;
        }

    }

    public void setInstallationSurface(Vector3d installationSurface) {
        this.installVector = installationSurface;
        final Transform3D rotateToInstallTransform3D = new Transform3D();
        if (ModelBoundingBox.NORMALS[ModelBoundingBox.FRONT].
                equals(installationSurface)) {
            //front
            rotateToInstallTransform3D.rotY(-Math.PI);
        } else if (ModelBoundingBox.NORMALS[ModelBoundingBox.BACK].
                equals(installationSurface)) {
            //back

        } else if (ModelBoundingBox.NORMALS[ModelBoundingBox.LEFT].
                equals(installationSurface)) {
            //left
            rotateToInstallTransform3D.rotY(-Math.PI / 2);

        } else if (ModelBoundingBox.NORMALS[ModelBoundingBox.RIGHT].
                equals(installationSurface)) {
            //right
            rotateToInstallTransform3D.rotY(Math.PI / 2);

        } else if (ModelBoundingBox.NORMALS[ModelBoundingBox.TOP].
                equals(installationSurface)) {
            //top
            rotateToInstallTransform3D.rotX(-Math.PI / 2);

        } else if (ModelBoundingBox.NORMALS[ModelBoundingBox.BOTTOM].
                equals(installationSurface)) {
            rotateToInstallTransform3D.rotX(Math.PI / 2);

            //bottom
        }
        rotScaleTG.setTransform(rotateToInstallTransform3D);
        calculateSize();
        moveCenterToZero();
    }

    public void setBaseModel(final BranchGroup baseModel) {
        translationTG.removeAllChildren();
        translationTG.setTransform(new Transform3D());
        rotScaleTG.setTransform(new Transform3D());
        this.baseModel = baseModel;
        translationTG.addChild(baseModel);
        this.countChild = baseModel.numChildren();
        lower = new Point3d();
        upper = new Point3d();
        final Shape3D shape = new Shape3D();
        shape.removeAllGeometries();
        for (int i = 0; i < countChild; i++) {
            shape.addGeometry(((Shape3D) baseModel.getChild(i)).getGeometry());
        }
        final BoundingBox bounds = (BoundingBox) shape.getBounds();
        bounds.getLower(lower);
        bounds.getUpper(upper);
        calculateSize();
        if (modelBoundingBox != null) {
            translationTG.removeChild(modelBoundingBox);
        }
        modelBoundingBox = new ModelBoundingBox(lower, upper);
        moveCenterToZero();
    }


    public double getLength() {
        return this.length;
    }

    public double getWidth() {
        return this.width;

    }

    public double getHeight() {
        return this.height;
    }

    public void setModelScale(double scale) {
        final Transform3D scaleTransform = new Transform3D();
        scaleTransform.setScale(scale);
        scaleTransform.mul(rotScaleTG.getTransform());
        rotScaleTG.setTransform(scaleTransform);
        calculateSize();
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException,
            NamedObjectException {
        oos.writeInt(countChild);
        oos.writeObject(lower);
        oos.writeObject(upper);
        final double[] rotScaleTGValues = new double[16];
        rotScaleTG.getTransform().get(rotScaleTGValues);
        oos.writeObject(rotScaleTGValues);
        final double[] translationTGValues = new double[16];
        translationTG.getTransform().get(translationTGValues);
        oos.writeObject(translationTGValues);
        for (int i = 0; i < countChild; i++) {
            oos.writeObject(baseModel.getChild(i));
        }
        oos.writeObject(installCoords);
        oos.writeObject(installVector);

    }

    private void readObject(final ObjectInputStream objectInputStream)
            throws ClassNotFoundException, IOException {
        init();
        countChild = objectInputStream.readInt();
        lower = (Point3d) objectInputStream.readObject();
        upper = (Point3d) objectInputStream.readObject();
        final double[] rotScaleTGValues = (double[]) objectInputStream.readObject();
        final Transform3D rotScale = new Transform3D();
        rotScale.set(rotScaleTGValues);
        rotScaleTG.setTransform(rotScale);
        final double[] translationTGValues = (double[]) objectInputStream.readObject();
        final Transform3D translation = new Transform3D();
        translation.set(translationTGValues);
        translationTG.setTransform(translation);
        baseModel = new BranchGroup();
        baseModel.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        baseModel.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        baseModel.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        baseModel.setCapability(BranchGroup.ALLOW_DETACH);
        for (int i = 0; i < countChild; i++) {
            final Shape3DMeta childShape = (Shape3DMeta) objectInputStream.readObject();
            baseModel.addChild(childShape);
        }
        if (baseModel != null) {
            translationTG.addChild(baseModel);
        }
        Object readInstallCoords = objectInputStream.readObject();
        if (readInstallCoords != null) {
            installCoords = (Point3d[]) readInstallCoords;
        }
        installVector = (Vector3d) objectInputStream.readObject();
        calculateSize();
    }

    public void calculateInstallCoords() {
        final Area area = new Area();
        final Transform3D trans = translationTG.getTransform();
        final Transform3D rot = rotScaleTG.getTransform();
        rot.mul(trans);
        for (int i = 0; i < countChild; i++) {
            final GeometryInfo info = new GeometryInfo((GeometryArray)
                    ((Shape3D) baseModel.getChild(i)).getGeometry());
            for (int index = 0; index < info.getCoordinates().length;
                 index = index + 3) {
                Point3f p1 = info.getCoordinates()[index];
                Point3f p2 = info.getCoordinates()[index + 1];
                Point3f p3 = info.getCoordinates()[index + 2];
                rot.transform(p1);
                rot.transform(p2);
                rot.transform(p3);
                area.add(new Area(getTriangle(p1, p2, p3)));
            }
        }
        installCoords = getPoint3ds(ShapeUtill.translateShape(area));
    }

    private Point3d[] getPoint3ds(final Shape shape) {
        final Stack<Point3d> points = new Stack<Point3d>();
        if (null != shape) {
            PathIterator pi = shape.getPathIterator(null, 0.001);
            while (!pi.isDone()) {
                double[] current = new double[2];
                if (pi.currentSegment(current) ==
                        PathIterator.SEG_LINETO) {
                    points.push(new Point3d(current[0], current[1], 0));
                }
                pi.next();
            }
        }
        final Point3d[] shapePoints = new Point3d[points.size()];

        for (int i = 0; i < shapePoints.length; i++) {
            shapePoints[i] = points.pop();
        }
        return shapePoints;
    }

    private Shape getTriangle(final Point3f p1,
                              final Point3f p2,
                              final Point3f p3) {
        Path2D path = new Path2D.Float();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.lineTo(p1.x, p1.y);
        return path;
    }

    private void moveCenterToZero() {
        final Vector3d movecenterVector3d = new Vector3d(-(upper.x + lower.x) / 2,
                -(upper.y + lower.y) / 2, -(upper.z + lower.z) / 2);
        final Transform3D moveToZeroTransform = new Transform3D();
        moveToZeroTransform.set(movecenterVector3d);
        translationTG.setTransform(moveToZeroTransform);
    }

    private void calculateSize() {
        if (this.lower != null && this.upper != null) {
            final Point3d currentLower = new Point3d(lower);
            final Point3d currentUpper = new Point3d(upper);
            rotScaleTG.getTransform().transform(currentLower);
            rotScaleTG.getTransform().transform(currentUpper);
            length = RealNumberOperator.roundNumber(Math.abs(currentUpper.x - currentLower.x), 2);
            width = RealNumberOperator.roundNumber(Math.abs(currentUpper.z - currentLower.z), 2);
            height = RealNumberOperator.roundNumber(Math.abs(currentUpper.y - currentLower.y), 2);
        }
    }

    public void toXYPlan(final Vector3d surfaceNormal) {
        if (surfaceNormal.z < 0) {
            Transform3D roscaleTransform3D = rotScaleTG.getTransform();
            Transform3D roate = new Transform3D();
            if (ModelBoundingBox.NORMALS[ModelBoundingBox.TOP].
                    equals(installVector)
                    || ModelBoundingBox.NORMALS[ModelBoundingBox.
                    BOTTOM].equals(installVector)) {
                roate.rotX(-Math.PI);

            } else {
                roate.rotY(-Math.PI);
            }
            roscaleTransform3D.mul(roate);
            rotScaleTG.setTransform(roscaleTransform3D);
        }
    }

    public void setModelBoundingBox(final ModelBoundingBox modelBoundingBox) {
        this.modelBoundingBox = modelBoundingBox;
        translationTG.addChild(modelBoundingBox);
    }

    public ModelMeta clone() {
        ModelMeta cloneModel = new ModelMeta();
        cloneModel.setBaseModel((BranchGroup) cloneNode(baseModel));
        cloneModel.rotScaleTG.setTransform(this.rotScaleTG.getTransform());
        cloneModel.translationTG.setTransform(this.translationTG.getTransform());
        cloneModel.lower = lower;
        cloneModel.upper = upper;
        cloneModel.installVector = installVector;
        cloneModel.countChild = countChild;
        cloneModel.modelBoundingBox = modelBoundingBox;
        cloneModel.outLineVisible = outLineVisible;
        cloneModel.installCoords = installCoords;
        cloneModel.length = length;
        cloneModel.width = width;
        cloneModel.height = height;
        return cloneModel;

    }

    private Node cloneNode(Node node) {
        synchronized (this.baseModel) {
            final BranchGroup cloneBaseModel = new BranchGroup();
            cloneBaseModel.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            cloneBaseModel.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            cloneBaseModel.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            cloneBaseModel.setCapability(BranchGroup.ALLOW_DETACH);
            for (int index=0;index<baseModel.numChildren();index++){
                 final Shape3DMeta clonedShape = (Shape3DMeta) baseModel.getChild(index).cloneNode(false);
                cloneBaseModel.addChild(clonedShape);
            }
            return cloneBaseModel;
        }
    }


}