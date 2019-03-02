/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * @author gang-liu
 */
public class CSTransformGroup extends TransformGroup implements Serializable {
    private static final Boolean NON_SERIALIZABLE_IND = false;
    private Set<TransformConstraint> constraints =
            new HashSet<TransformConstraint>();

    private static final long serialVersionUID = 100;
    private ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("constraints", Set.class)
    };

    public CSTransformGroup() {
        super();
        setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    }

    public Node getFirstParentOf(final Class parentKlz) {
        Node parent = getParent();
        while (parent != null) {
            if (parentKlz.getCanonicalName().equals(parent.getClass().
                    getCanonicalName())) {
                return parent;
            } else {
                parent = parent.getParent();
            }
        }
        return parent;
    }

    public void addConstraints(final TransformConstraint tc) {
        if (!constraints.contains(tc)) {
            constraints.add(tc);
        }
    }

    public int getConstraintsCnt() {
        return constraints.size();
    }

    public Set<TransformConstraint> getTransformConstrains() {
        return constraints;
    }

    public Transform3D getTransform() {
        final Transform3D ret = new Transform3D();
        getTransform(ret);
        return ret;
    }

    public void addTranslation(final Vector3d translation) {
        final Vector3d currentTrans = getTranslation();
        currentTrans.setX(translation.getX());
        currentTrans.setY(translation.getY());
        currentTrans.setZ(translation.getZ());
        setTranslation(currentTrans);
    }

    public Vector3d getTranslation() {
        final Transform3D currentTrans = getTransform();
        final Vector3d ret = new Vector3d();
        currentTrans.get(ret);
        return ret;
    }

    public void setTranslation(final Vector3d translation) {
        Transform3D currentTrans = new Transform3D();
        final Matrix3f m = new Matrix3f();
        currentTrans.get(m);
        currentTrans = new Transform3D(m, translation, 1.0);
        setTransform(currentTrans);
    }

    public void addScale(final double x, final double y, final double z) {
        final Transform3D currentTrans = getTransform();
        final Transform3D newTransform = new Transform3D();
        newTransform.setScale(new Vector3d(x, y, z));
        currentTrans.mul(newTransform);
        setTransform(currentTrans);
    }

    public void addRotationX(final double x) {
        final Transform3D currentTrans = getTransform();
        final Transform3D newTransform = new Transform3D();
        newTransform.rotX(x);
        currentTrans.mul(newTransform);
        setTransform(currentTrans);
    }

    public void addRotationY(final double x) {
        final Transform3D currentTrans = getTransform();
        final Transform3D newTransform = new Transform3D();
        newTransform.rotY(x);
        currentTrans.mul(newTransform);
        setTransform(currentTrans);
    }

    public void addRotationZ(final double x) {
        final Transform3D currentTrans = getTransform();
        final Transform3D newTransform = new Transform3D();
        newTransform.rotZ(x);
        currentTrans.mul(newTransform);
        setTransform(currentTrans);
    }

    public Transform3D getTransformUpTo(final CSTransformGroup target) {
        final Transform3D ret = new Transform3D();
        final Stack<CSTransformGroup> stack = new Stack<CSTransformGroup>();
        CSTransformGroup csTG = this;
        stack.push(csTG);
        while (csTG != null && csTG != target) {
            csTG = csTG.getParentCSTG();
            stack.push(csTG);
        }
        while (!stack.isEmpty()) {
            ret.mul(stack.pop().getTransform());
        }
        return ret;
    }

    public CSTransformGroup getParentCSTG() {
        Node parent = getParent();
        while (!(parent instanceof CSTransformGroup) && (parent != null)) {
            parent = parent.getParent();
        }
        return parent == null ? null : (CSTransformGroup) parent;
    }

    @Override
    public void setTransform(final Transform3D trans) {
        if (!constraints.isEmpty()) {
            final Iterator<TransformConstraint> tc = constraints.iterator();
            while (tc.hasNext()) {
                if (!tc.next().isLegalTransform(trans)) {
                    return;
                }
            }
        }
        super.setTransform(trans);
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.writeObject(constraints);
        final double[] matrixData = new double[16];
        getTransform().get(matrixData);
        oos.writeObject(matrixData);
        oos.writeInt(numChildren());
        for(int index = 0; index < numChildren(); index++){
            final Node childNode = getChild(index);
            if(childNode instanceof Serializable){
                oos.writeObject(childNode);
            } else {
                oos.writeObject(NON_SERIALIZABLE_IND);
            }
        }
        oos.flush();
    }

    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        final Object cons = ois.readObject();
        this.constraints = cons == null ? null : (Set<TransformConstraint>) cons;
        final double[] matrixData = (double[]) ois.readObject();
        setMatrix4d(new Matrix4d(matrixData));
        final int numOfChild = ois.readInt();
    }

    public boolean equals(final CSTransformGroup target) {
        final Transform3D currentTrans = new Transform3D();
        final Transform3D targetTrans = new Transform3D();
        getTransform(targetTrans);
        target.getTransform(targetTrans);
        return currentTrans.equals(targetTrans) &&
                target.getTransformConstrains().containsAll(constraints) &&
                target.getConstraintsCnt() == constraints.size();
    }

    public boolean testLegalTransform(final Transform3D trans) {
        for (final TransformConstraint tc : constraints) {
            if (!tc.isLegalTransform(trans)) {
                return false;
            }
        }
        return true;
    }

    public Matrix4d getMatrix4d() {
        final Matrix4d ret = new Matrix4d();
        getTransform().get(ret);
        return ret;
    }

    public void setMatrix4d(final Matrix4d matrix) {
        setTransform(new Transform3D(matrix));
    }

    public static void main(String[] args) {
        final CSTransformGroup tg1 = new CSTransformGroup();
        final CSTransformGroup tg2 = new CSTransformGroup();
        final CSTransformGroup tg3 = new CSTransformGroup();
        final BranchGroup bg = new BranchGroup();
        tg1.addChild(tg2);
        tg2.addChild(tg3);
        final Transform3D t2 = new Transform3D();
        final Transform3D t3 = new Transform3D();
        t2.setTranslation(new Vector3d(0.0, 0.0, 1.0));
        t3.setTranslation(new Vector3d(0.0, 0.0, 2.0));
        tg2.setTransform(t2);
        tg3.setTransform(t3);
        tg3.addChild(bg);
        System.out.println(tg3.getTransformUpTo(tg1));

        System.out.println("-----------------------------------------------");

        final Transform3D trans = new Transform3D();
        bg.getLocalToVworld(trans);
        System.out.println(trans);
    }
}
