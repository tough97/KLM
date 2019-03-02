/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.designer.view.controller;

import com.sun.j3d.utils.universe.ViewingPlatform;
import cs.designer.swing.tool.Generatorable;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import javax.media.j3d.*;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;

/**
 * @author rongyang
 */
public class ModelControler implements DisplayControlable, ViewControlable {

    private DisplayView view;
    private TransformGroup contrGroup;
    private double minViewerDistance = MODEL_MIN_DISIANCE;
    private double maxViewerDistance = MODEL_MAX_DISTANCE;
    private double modelTranslateUint = maxViewerDistance / 5;
    private ModelMouseControler controler;
    private double viewDistance = 0;
    private double resateViewDistance;

    public ModelControler(DisplayView view) {
        this.view = view;
    }

    public void registerController(TransformGroup contrGroup, boolean addListenerable) {
        this.contrGroup = contrGroup;
        BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), Float.MAX_VALUE);
        controler = new ModelMouseControler(view);
        controler.setSchedulingBounds(bounds);
        if (addListenerable) {
            view.getViewCanvas().addMouseWheelListener(controler);
            view.getViewCanvas().addMouseListener(controler);
            view.getViewCanvas().addMouseMotionListener(controler);
        }
        ViewingPlatform viewingPlatform = view.getVieScreenUniverse().getViewingPlatform();
        viewingPlatform.setViewPlatformBehavior(controler);
        initViewerDistance();
    }

    public DisplayView getView() {
        return view;
    }

    public Generatorable getGenerator() {
        return null;
    }

    public MouseControler getControler() {
        return controler;
    }

    private void initViewerDistance() {
        final Transform3D trans = new Transform3D();
        view.getViewTransformGroup().getTransform(trans);
        final Matrix4d matrix = new Matrix4d();
        trans.get(matrix);
        viewDistance = matrix.m23;
    }

    public void setViewDistance(double distance) {
        final Transform3D trans = new Transform3D();
        trans.set(new Vector3d(0, 0, distance));
        view.getViewTransformGroup().setTransform(trans);
        viewDistance = distance;
        resateViewDistance = distance;
    }

    public void setMaxViewerDistance(double maxViewerDistance) {
        this.maxViewerDistance = maxViewerDistance;
    }

    public void setMinViewerDistance(double minViewerDistance) {
        this.minViewerDistance = minViewerDistance;
    }

    public void reset() {
        final Transform3D homeTrans = new Transform3D();
        controler.getHomeTransform(homeTrans);
        final Transform3D trans = new Transform3D();
        trans.set(new Vector3d(0, 0, resateViewDistance));
        homeTrans.mul(trans);
        view.getViewTransformGroup().
                setTransform(homeTrans);
    }

    public void moveUp() {
        controler.setLatitude(0.1);
    }

    public void moveDown() {
        controler.setLatitude(-0.1);
    }

    public void moveLeft() {
        controler.setLongditude(0.1);
    }

    public void moveRight() {
        controler.setLongditude(-0.1);

    }

    public void moveRest() {
        Transform3D homeTrans = new Transform3D();
        controler.getHomeTransform(homeTrans);
        view.getViewTransformGroup().
                setTransform(homeTrans);

    }

    class ModelMouseControler extends MouseControler {


        public ModelMouseControler(DisplayView view) {
            super(view);
            setZoomFactor(MODEL_TRANSLATE_UINT);
        }

        @Override
        public void mousePressed(MouseEvent me) {
            if (view.getViewType()
                    == HousePlanView.ViewType.ORBIT_VIEW) {
                super.mousePressed(me);
            }
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            if (view.getViewType()
                    == HousePlanView.ViewType.ORBIT_VIEW) {
                super.mouseMoved(me);
            }
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (view.getViewType()
                    == HousePlanView.ViewType.ORBIT_VIEW
                    &&view.getOperateType()
                    != HousePlanView.OperateType.ADD_SUBSURFACE
                    &&view.getOperateType()
                    !=HousePlanView.OperateType.MOVE_FURNITURE) {
                super.mouseDragged(me);
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            if (view.getViewType()
                    == HousePlanView.ViewType.ORBIT_VIEW) {
                double tempDistance = viewDistance;
                tempDistance += modelTranslateUint * mwe.getWheelRotation();
                if (tempDistance < maxViewerDistance
                        && tempDistance > minViewerDistance) {
                    super.mouseWheelMoved(mwe);
                    viewDistance = tempDistance;
                }
            }
        }

        @Override
        public void controlerMousePressed(MouseEvent me) {
        }

        @Override
        public void controlerMouseMoved(MouseEvent me) {
        }

        @Override
        public void controlerMouseDragged(MouseEvent me) {
        }

        @Override
        public void controlerMouseWheelMoved(MouseWheelEvent mwe) {
            mouseWheelMoved(mwe);
        }

        @Override
        public void controlerMouseReleased(MouseEvent me) {
            mouseReleased(me);
        }

        @Override
        public void reset() {
            super.resetView();
            super.integrateTransforms();
        }


    }

}

class AutomaticRotator extends RotationInterpolator implements MouseListener, MouseMotionListener {

    private TransformGroup contrGroup;

    public AutomaticRotator(TransformGroup contrGroup) {
        super(new Alpha(-1, Alpha.INCREASING_ENABLE,
                0, 0,
                5000, 0, 0,
                0, 0, 0), contrGroup);
        this.contrGroup = contrGroup;
    }

    public void beginAutomaticRotat(boolean automaticRotat) {
    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mousePressed(MouseEvent me) {
        if (!this.getEnable()) {
            this.setEnable(true);
        } else {
            this.setEnable(false);
        }
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public void mouseDragged(MouseEvent me) {
        if (this.getEnable()) {
            this.setEnable(false);
        }
    }

    public void mouseMoved(MouseEvent me) {
    }
}

