package com.klm.transform.impl;

import com.klm.transform.TransformConstraint;
import com.klm.util.RealNumberOperator;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 11/20/11
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class HorrizontalTransConstraint extends TransformConstraint{

    @Override
    public boolean isLegalTransform(Transform3D trans) {
        final Point3d point = new Point3d(0, 0, 0);
        trans.transform(point);
        return RealNumberOperator.compareTwoDouble(point.getZ(), 0.0);
    }
}
