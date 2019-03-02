/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;
import java.io.ObjectStreamField;
import java.io.Serializable;

/**
 * @author gang-liu
 */
public abstract class TransformConstraint implements Serializable {

    protected final Vector3d TESTING_DATA = new Vector3d(0.0, 0.0, 1.0);
    private static final long serialVersionUID = 100;
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("TESTING_DATA", Vector3d.class)
    };

    public abstract boolean isLegalTransform(final Transform3D trans);

    public void printStatement() {
        System.out.println("Illegal " + getClass().getSimpleName() +
                " condition trigerred");
    }
}
