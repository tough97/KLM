/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.designer.view.viewer;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author gang liu
 */
public class VirtualView {

    private Movement vMove;
    private float height;
    private Transform3D defaultRransform;

    public VirtualView(final TransformGroup tg) {
        vMove = Movement.createDefasultViewingMovement(tg);
        defaultRransform = new Transform3D();
        tg.getTransform(defaultRransform);
    }

    public VirtualView(final TransformGroup tg, final float height) {
        this.height = height;
        vMove = Movement.createDefasultViewingMovement(tg);
        final TransformGroup vMoveTG = vMove.getHostTG();
        final Transform3D trans = new Transform3D();
        vMoveTG.getTransform(trans);
        final Transform3D addTran = new Transform3D();
        addTran.setTranslation(new Vector3f(0f, height, 0f));
        trans.mul(addTran);
        vMoveTG.setTransform(trans);
        defaultRransform=trans;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(final float height) {
        this.height = height;
        final Transform3D trans = vMove.getCurrentTransform();
        final Matrix4f m4f = new Matrix4f();
        trans.get(m4f);
        m4f.setM13(height);
        vMove.setCurrentTransform(new Transform3D(m4f));
        vMove.saveTransform();
    }

    public void lookDown() {
        vMove.rotXCounterClock();
        vMove.saveTransform();
    }

    public void lookUp() {
        vMove.rotXClock();
        vMove.saveTransform();
    }

    public void lookLeft() {
        final double xAngle = vMove.getxAngle();
        vMove.rotX(-1 * xAngle);
        vMove.rotYClock();
        vMove.rotX(xAngle);
        vMove.saveTransform();
    }

    public void lookRight() {
        final double xAngle = vMove.getxAngle();
        vMove.rotX(-1 * xAngle);
        vMove.rotYCounterClock();
        vMove.rotX(xAngle);
        vMove.saveTransform();
    }

    public void stepRight() {
        vMove.transformWithYNormalized(Movement.MOVE_X_FORWARD);
        vMove.saveTransform();
    }

    public void stepLeft() {
        vMove.transformWithYNormalized(Movement.MOVE_X_BACKWARD);
        vMove.saveTransform();
    }

    public void stepBackward() {
        vMove.transformWithYNormalized(Movement.MOVE_Z_FORWARD);
        vMove.saveTransform();
    }

    public void stepForward() {
        vMove.transformWithYNormalized(Movement.MOVE_Z_BACKWARD);
        vMove.saveTransform();
    }

    public void stepUp() {
        vMove.transformWithYNormalized(Movement.MOVE_Z_FORWARD);
        vMove.saveTransform();
    }

    public void stepDown() {
        vMove.transformWithYNormalized(Movement.MOVE_Z_BACKWARD);
        vMove.saveTransform();
    }

    public Transform3D getScreenLeftTransform() {
        final Transform3D ret = new Transform3D();
        final Vector3d transform = vMove.getV3Column(0);
        System.out.println("transform screen left = " + transform);
        transform.normalize();
        transform.scale(vMove.getMoveSpeed());
        ret.setTranslation(transform);
        return ret;
    }

    public Transform3D getScreenRightTransform() {
        final Transform3D ret = new Transform3D();
        final Vector3d transform = vMove.getV3Column(0);
        System.out.println("transform screen right = " + transform);
        transform.normalize();
        transform.negate();
        transform.scale(vMove.getMoveSpeed());
        ret.setTranslation(transform);
        return ret;
    }

    public Transform3D getScreenUpTransform() {
        final Transform3D ret = new Transform3D();
        final Vector3d transform = vMove.getV3Column(1);
        transform.normalize();
        transform.scale(vMove.getMoveSpeed());
        ret.setTranslation(transform);
        return ret;
    }

    public Transform3D getScreenDownTransform() {
        final Transform3D ret = new Transform3D();
        final Vector3d transform = vMove.getV3Column(1);
        transform.normalize();
        transform.negate();
        transform.scale(vMove.getMoveSpeed());
        ret.setTranslation(transform);
        return ret;
    }

    public Transform3D getScreenDeepTransform() {
        final Transform3D ret = new Transform3D();
        final Vector3d transform = vMove.getV3Column(2);
        transform.normalize();
        transform.scale(vMove.getMoveSpeed());
        ret.setTranslation(transform);
        return ret;
    }

    public Transform3D getScreenShallowTransform() {
        final Transform3D ret = new Transform3D();
        final Vector3d transform = vMove.getV3Column(2);
        transform.normalize();
        transform.negate();
        transform.scale(vMove.getMoveSpeed());
        ret.setTranslation(transform);
        return ret;
    }

    public void reset() {
        vMove.reset();
    }

    public void setTransform3D(Transform3D trans) {
        vMove.setCurrentTransform(trans);
    }

    public Transform3D getTransform3D() {
        return vMove.getCurrentTransform();
    }

    public void setMoveSpeed(final double  moveSpeed){
        vMove.setMoveSpeed(moveSpeed);
    }

    public void setRotateRate(final double rotateRate){
        vMove.setRotateRate(rotateRate);
    }

    public void setDefaultMoveSpeed(){
        vMove.setMoveSpeed(Movement.DEFAULT_SPEED);
    }

    public void setDefaultRotateRate(){
        vMove.setRotateRate(Movement.DEFAULT_ROTATE_RATE);
    }
    public double getxAngle() {
        return vMove.getxAngle();
    }

}
