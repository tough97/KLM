package com.klm.material.impl;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.impl.Furniture;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 11/20/11
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class FurnitureBehavior extends Behavior {

    private static final double ROTATE_ANGLE_PI = Math.PI / 4;
    private static final double ROTATE_ANGLE_NEGA_PI = Math.PI / -4;

    private static final Transform3D ROTATION_Y_CLOCK = new Transform3D();
    private static final Transform3D ROTATION_Y_COUNT_CLOCK = new Transform3D();
    private static final int POST = 1;

    static {
        ROTATION_Y_CLOCK.rotY(ROTATE_ANGLE_PI);
        ROTATION_Y_COUNT_CLOCK.rotY(ROTATE_ANGLE_NEGA_PI);
    }

    private static final WakeupCriterion[] WAKEUP_EVENTS;

    static {
        WAKEUP_EVENTS = new WakeupCriterion[]{
                new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED),
                new WakeupOnAWTEvent(MouseEvent.MOUSE_WHEEL),
                new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED),
                new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED),
                new WakeupOnBehaviorPost(new FurnitureBehavior(null), POST)
        };
    }

    private static final WakeupOr WAKEUP_CRITERION = new WakeupOr(WAKEUP_EVENTS);

    private int XDiff = 0, YDiff = 0;
    private Furniture furniture;
    private Surface3D surface;
    private Canvas3D canvas;
    private PickCanvas pickCanvas;

    public FurnitureBehavior(final Furniture furniture) {
        super();
        setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
        setEnable(false);
        this.furniture = furniture;
    }

    public void addToSurface(final Surface3D surface, final Tuple3d postion) throws CSHouseException {
        if (this.surface != null) {
            this.surface.getAttachmentBG().removeChild(this);
        }
        this.surface = surface;
        final Locale locale = surface.getLocale();
        if (locale == null) {
            throw new CSHouseException(surface + " has not been visible yet, addToSurface operation not permited now");
        }
        final VirtualUniverse vu = locale.getVirtualUniverse();
        if (vu instanceof SimpleUniverse) {
            canvas = ((SimpleUniverse) vu).getCanvas();
        }
    }

    @Override
    public void initialize() {
        wakeupOn(WAKEUP_CRITERION);
    }

    @Override
    public void processStimulus(Enumeration criteria) {
        System.out.println(" I am Called----------------");
        final StringBuilder sb = new StringBuilder();
        while (criteria.hasMoreElements()) {
            final Object criteriaElement = criteria.nextElement();
            sb.append(criteriaElement.toString()).append(" ");
            if (criteriaElement instanceof WakeupOnAWTEvent) {
                final AWTEvent[] events = ((WakeupOnAWTEvent) criteriaElement).getAWTEvent();
                sb.append(" there are ").append(events.length).append(" events");
                for (final AWTEvent event : events) {
                    if (event instanceof MouseEvent) {
                        final MouseEvent mouseEvent = (MouseEvent) event;
                        switch (mouseEvent.getID()) {
                            case MouseEvent.MOUSE_PRESSED:
                                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                                    sb.append(" mouse left button clicked");
                                    try {
                                        updateScreenError(mouseEvent);
                                    } catch (CSHouseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case MouseEvent.MOUSE_DRAGGED:
                                if (mouseEvent.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
                                    sb.append(" mouse dragged");
                                    try {
                                        final Point3d position = surface.getClickedPointOnSurface(new Point(mouseEvent.getX() - XDiff, mouseEvent.getY() - YDiff), canvas);
                                        furniture.setPositionOnParent(new Vector3d(position));
                                        updateScreenError((MouseEvent) event);
                                    } catch (CSHouseException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
        wakeupOn(WAKEUP_CRITERION);
    }

    @Override
    public void setEnable(final boolean enable){
        super.setEnable(enable);
        if(enable){
            postId(POST);
        }
    }

    void resetDifferences(){
        XDiff = 0;
        YDiff = 0;
    }

    private void updateScreenError(final MouseEvent mouseEvent) throws CSHouseException {
//        final Point3d selectedPoint = surface.getClickedPointOnSurface(new Point(mouseEvent.getX(), mouseEvent.getY()), canvas);
//        final Point3d furniturePosition = furniture.getPositionOnParent();
//        final Transform3D modelingTrans = surface.getRawShapeToWorldTrans();
//        modelingTrans.transform(selectedPoint);
//        modelingTrans.transform(furniturePosition);
//        final Point2d surfaceProject = getWorldCoordinateOnScreen(selectedPoint);
//        final Point2d furnitureProject = getWorldCoordinateOnScreen(furniturePosition);
//        XDiff = (int) (surfaceProject.getX() - furnitureProject.getX());
//        YDiff = (int) (surfaceProject.getY() - furnitureProject.getY());
    }

    private Point2d getWorldCoordinateOnScreen(final Point3d vWorldCoordinate) {
        final Transform3D viewingTrans = new Transform3D();
        canvas.getImagePlateToVworld(viewingTrans);
        viewingTrans.invert();
        viewingTrans.transform(vWorldCoordinate);

        final Point2d ret = new Point2d();
        canvas.getPixelLocationFromImagePlate(vWorldCoordinate, ret);
        return ret;
    }

}
