/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;

import com.klm.util.RealNumberOperator;
import java.util.Random;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

/**
 *
 * @author gang-liu
 */
public class RotationConstraint extends TransformConstraint {

    @Override
    public boolean isLegalTransform(Transform3D trans) {
        Vector3d data = new Vector3d(TESTING_DATA);
        trans.transform(data);
        data.normalize();
        data = new Vector3d(RealNumberOperator.roundTuple3d(data,
                RealNumberOperator.DOUBLE_PRECISION));
        final boolean ret = data.equals(TESTING_DATA);
        if(!ret){printStatement();}
        return ret;
    }

    public static void main(String[] args) {
        final Transform3D trans = new Transform3D();
        final Transform3D scale = new Transform3D();
        final Random rand = new Random();
        final TransformConstraint noRot = new RotationConstraint();
        for (int i = 0; i < 100; i++) {
            trans.setTranslation(new Vector3d(rand.nextDouble(),
                    rand.nextDouble(), rand.nextDouble()));
            scale.setScale(rand.nextDouble());
            if (rand.nextBoolean()) {
                trans.mul(scale);
            } else {
                trans.mul(scale, trans);
            }
            System.out.println(noRot.isLegalTransform(trans));
        }
    }
}
