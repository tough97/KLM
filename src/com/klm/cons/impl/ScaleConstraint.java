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
public class ScaleConstraint extends TransformConstraint {

    protected final Point3d ORIGIN_POINT = new Point3d(0.0, 0.0, 0.0);  

    @Override
    public boolean isLegalTransform(Transform3D trans) {
        Point3d start = new Point3d(ORIGIN_POINT);
        Point3d end = new Point3d(TESTING_DATA);        
        trans.transform(start);
        trans.transform(end);
        final double origineLength = 1.0;
        final double currentLength = RealNumberOperator.roundNumber(
                end.distance(start), RealNumberOperator.DOUBLE_PRECISION);
        if(origineLength != currentLength){printStatement();}
        return (origineLength == currentLength);
    }
    
//        public static void main(String[] args) {
//        final Transform3D rot = new Transform3D();
//        final Transform3D trans = new Transform3D();
//        final Random rand = new Random();
//        final TransformConstraint noTrans = new ScaleConstraint();
//        for (int i = 0; i < 100; i++) {
//            rot.rotX(rand.nextDouble());
//            trans.setTranslation(new Vector3d(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
//            if (rand.nextBoolean()) {
//                rot.mul(trans);
//            } else {
//                rot.mul(trans, rot);
//            }
//            System.out.println(noTrans.isLegalTransform(rot));
//        }
//    }
}
