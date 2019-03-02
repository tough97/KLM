/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.designer.view.viewer;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;

/**
 *
 * @author gang-liu
 */
public class Movement {

    public static final Vector3f MOVE_X_FORWARD = new Vector3f(0.5f, 0.0f, 0.0f);
    public static final Vector3f MOVE_Y_FORWARD = new Vector3f(0.0f, 0.5f, 0.0f);
    public static final Vector3f MOVE_Z_FORWARD = new Vector3f(0.0f, 0.0f, 0.5f);
    public static final Vector3f MOVE_X_BACKWARD =
            new Vector3f(-1.0f, 0.0f, 0.0f);
    public static final Vector3f MOVE_Y_BACKWARD =
            new Vector3f(0.0f, -1.0f, 0.0f);
    public static final Vector3f MOVE_Z_BACKWARD =
            new Vector3f(0.0f, 0.0f, -1.0f);
    public static final double DEFAULT_SPEED = 0.25;
    public static final double DEFAULT_ROTATE_RATE = Math.PI / 180;
    private double xAngle, yAngle, zAngle;
    private double moveSpeed, rotateRate;
    private TransformGroup hostTG;
    private Transform3D currentTransform;
    private Transform3D initialTransform;

    public Movement(final TransformGroup hostTG) {
        this.hostTG = hostTG;
        currentTransform = Movement.getTransform3D(hostTG);
        initialTransform = Movement.getTransform3D(hostTG);
        moveSpeed = DEFAULT_SPEED;
        rotateRate = DEFAULT_ROTATE_RATE;
        xAngle = 0.0;
        yAngle = 0.0;
        zAngle = 0.0;
    }

    public void reset() {
        currentTransform = new Transform3D(initialTransform);
        saveTransform();
        xAngle = 0.0;
        yAngle = 0.0;
        zAngle = 0.0;
    }

    public void setX(double x) {
    }

    public void setMoveSpeed(final double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void setRotateRate(final double rotateRate) {
        this.rotateRate = rotateRate;
    }

    public double getRotateRate() {
        return rotateRate;
    }

    public TransformGroup getHostTG() {
        return hostTG;
    }

    public double getxAngle() {
        return xAngle;
    }

    public double getyAngle() {
        return yAngle;
    }

    public double getzAngle() {
        return zAngle;
    }

    public void setHostTG(TransformGroup hostTG) {
        this.hostTG = hostTG;
    }

    public void setxAngle(double xAngle) {
        this.xAngle = xAngle;
    }

    public void setyAngle(double yAngle) {
        this.yAngle = yAngle;
    }

    public void setzAngle(double zAngle) {
        this.zAngle = zAngle;
    }

    /*
     * Thworitically, this angle can be positive or negative
     * while positive means move counter-clockwise and negative
     * mean clock wise
     */
    public void rotX(final double angle) {
        xAngle += angle;
        normalizeXAngle();
        final Transform3D trans = new Transform3D();
        trans.rotX(angle);
        currentTransform.mul(trans);
    }

    public void rotXClock() {
        rotX(rotateRate);
    }

    public void rotXCounterClock() {
        rotX(-1 * rotateRate);
    }

    public void rotY(final double angle) {
        yAngle += angle;
        normalizeYAngle();
        final Transform3D trans = new Transform3D();
        trans.rotY(angle);
        currentTransform.mul(trans);
    }

    public void rotYClock() {
        rotY(rotateRate);
    }

    public void rotYCounterClock() {
        rotY(-1 * rotateRate);
    }

    public void rotZ(final double angle) {
        zAngle += angle;
        normalizeZAngle();
        final Transform3D trans = new Transform3D();
        trans.rotZ(angle);
        currentTransform.mul(trans);
    }

    public void rotZClock() {
        rotZ(rotateRate);
    }

    public void rotZCounterClock() {
        rotZ(-1 * rotateRate);
    }

    public void transform(final Vector3f vector) {
        Transform3D trans = new Transform3D();
        final Vector3f scaleVector = new Vector3f((float) (vector.getX()
                * moveSpeed), (float) (vector.getY() * moveSpeed), (float) (vector.
                getZ() * moveSpeed));
        trans.set(scaleVector);
        currentTransform.mul(trans);
    }

    public void transformWithYNormalized(final Vector3f vector) {
        final double tempAngle = xAngle;
        rotX(-1 * tempAngle);
        transform(vector);
        rotX(tempAngle);
//        System.out.println("x angle = "+xAngle);
    }

    //sets the current transform into host transform group
    public void saveTransform() {
        hostTG.setTransform(currentTransform);
    }

    public void normalizeXAngle() {
        xAngle %= Math.PI * 2;
    }

    public void normalizeYAngle() {
        yAngle %= Math.PI * 2;
    }

    public void normalizeZAngle() {
        zAngle %= Math.PI * 2;
    }

    public void normalizeAngles() {
        normalizeXAngle();
        normalizeYAngle();
        normalizeZAngle();
    }

    public Transform3D getCurrentTransform() {
        return currentTransform;
    }

    public void setCurrentTransform(final Transform3D currentTransform) {
        this.currentTransform = currentTransform;
        saveTransform();
    }

    private Transform3D mulCurrentTransform(final Transform3D original) {
        final Transform3D currentTrans = Movement.getTransform3D(hostTG);
        currentTrans.mul(original);
        return currentTrans;
    }

    public static Transform3D getTransform3D(final TransformGroup tg) {
        final Transform3D ret = new Transform3D();
        tg.getTransform(ret);
        return ret;
    }

    public static Movement createDefasultViewingMovement(final TransformGroup tg) {
        final Movement ret = new Movement(tg);
        ret.transform(MOVE_Z_BACKWARD);
        return ret;
    }

    public static Vector3d getXSpeedVector(final double speed) {
        final Vector3d ret = new Vector3d(1.0 * speed, 0.0, 0.0);
        return ret;
    }

    public static Vector3d getYSpeedVector(final double speed) {
        final Vector3d ret = new Vector3d(0.0, 1.0 * speed, 0.0);
        return ret;
    }

    public static Vector3d getZSpeedVector(final double speed) {
        final Vector3d ret = new Vector3d(0.0, 0.0, 1.0 * speed);
        return ret;
    }

    public Vector3d getV3Row(final int rowIndex) {
        final Matrix3d matrix = new Matrix3d();
        currentTransform.get(matrix);
        final Vector3d ret = new Vector3d();
        matrix.getRow(rowIndex, ret);
        return ret;
    }

    public Vector3d getV3Column(final int colIndex) {
        final Matrix3d matrix = new Matrix3d();
        currentTransform.get(matrix);
        final Vector3d ret = new Vector3d();
        matrix.getColumn(colIndex, ret);
        return ret;
    }

    public Vector4d getV4Row(final int rowIndex) {
        final Matrix4d matrix = new Matrix4d();
        currentTransform.get(matrix);
        final Vector4d ret = new Vector4d();
        matrix.getRow(rowIndex, ret);
        return ret;
    }

    public Vector4d getV4Column(final int colIndex) {
        final Matrix4d matrix = new Matrix4d();
        currentTransform.get(matrix);
        final Vector4d ret = new Vector4d();
        matrix.getColumn(colIndex, ret);
        return ret;
    }
}
