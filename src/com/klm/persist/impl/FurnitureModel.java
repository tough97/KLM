package com.klm.persist.impl;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public abstract class FurnitureModel extends BranchGroup{

    public abstract double getLength();
    public abstract double getWidth();
    public abstract double getHeight();

    public abstract Point3d[] getIdentifiedInstallCoords();

    public abstract void showOutLines();
    public abstract void hideOutLines();
    public abstract void settRotateInstallationVector(Vector3d installationVector);
    public abstract Vector3d getRotateInstallationVector();

    public  FurnitureModel(){
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ALLOW_DETACH);
    }

}
