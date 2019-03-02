package com.klm.material;

import com.klm.cons.impl.CSHouseException;

import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.vecmath.Tuple3d;
import javax.xml.crypto.dsig.Transform;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 11/24/11
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Installable {

    public void install() throws CSHouseException;
    public void unInstall() throws CSHouseException;
    public boolean isInstalled() throws CSHouseException;
    public Tuple3d getPositionOnParent() throws CSHouseException;
    public void setPositionOnParent(final Tuple3d position) throws CSHouseException;
    public Node getParent() throws CSHouseException;
    public Transform3D getRotationOnParent();
    public void setRotationOnParent(final Transform3D rotation);
}
