/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;

import com.klm.util.RealNumberOperator;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

/**
 *
 * @author gang-liu
 */
public class TranslationConstraint extends TransformConstraint {

    @Override
    public boolean isLegalTransform(Transform3D trans) {
        Point3d data = new Point3d(TESTING_DATA);
        trans.transform(data);
        final Point3d newData = new Point3d(RealNumberOperator.roundTuple3d(data,
                RealNumberOperator.DOUBLE_PRECISION));
        final boolean ret = data.equals(newData);
        if(!ret){printStatement();}
        return ret;
    }
    
//        public static void main(String[] args) {
//        final Transform3D rot = new Transform3D();
//        final Transform3D scale = new Transform3D();
//        final Random rand = new Random();
//        final TransformConstraint noTrans = new TranslationConstraint();
//        for (int i = 0; i < 100; i++) {
//            rot.rotX(rand.nextDouble());
//            scale.setScale(rand.nextDouble());
//            if (rand.nextBoolean()) {
//                rot.mul(scale);
//            } else {
//                rot.mul(scale, rot);
//            }
//            System.out.println(noTrans.isLegalTransform(rot));
//        }
//    }
    
}
