package cs.designer.swing.tool;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.impl.Furniture;
import com.sun.j3d.internal.J3dUtilsI18N;
import cs.designer.swing.undo.HouseEdit;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import javax.media.j3d.Transform3D;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.LinkedList;


/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/31/12
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class FurnitureLocator implements MouseListener,
        MouseMotionListener, MouseWheelListener {

    private boolean listener = false;
    protected LinkedList mouseq;
    protected boolean enable = true;
    private Furniture locatorFurniture;
    private boolean moveable = false;
    private DisplayView view;
    private Point3d lastLocator = new Point3d();
    private double toSurfaceDesc;
    private Point3d lastPickPoint = new Point3d();

    public FurnitureLocator(DisplayView view, Furniture furniture) {
        if (view.getViewCanvas() != null) {
            view.getViewCanvas().addMouseListener(this);
            view.getViewCanvas().addMouseMotionListener(this);
            view.getViewCanvas().addMouseWheelListener(this);
        }
        this.locatorFurniture = furniture;
        this.view = view;
        listener = true;
        locatorFurniture.getTranslationTransform().transform(lastLocator);
        if (locatorFurniture.getParentSurface() != null
                && furniture.getModel() != null) {
            Surface3D surface3D = locatorFurniture.getParentSurface();
            toSurfaceDesc = surface3D.calculateSurfaceNormal().getZ()
                    * locatorFurniture.getModel().getWidth() / 2;
        }
    }

    public void addListener(Component c) {
        if (!listener) {
            throw new IllegalStateException(J3dUtilsI18N.getString("Behavior0"));
        }
        c.addMouseListener(this);
        c.addMouseMotionListener(this);
        c.addMouseWheelListener(this);
    }

    public boolean isMoveable() {
        return moveable;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1
                && view.getOperateType() != DisplayView.OperateType.DELETE_OBJECT) {
            moveable = ((HousePlanView) view).getSelectObject(null) == locatorFurniture ? true : false;
            if (locatorFurniture.isInstalled()) {
                moveable = false;
            }
            if (locatorFurniture.getParentSurface() != null
                    && locatorFurniture.getModel() != null) {
                Surface3D surface3D = locatorFurniture.getParentSurface();
                toSurfaceDesc = surface3D.calculateSurfaceNormal().getZ()
                        * locatorFurniture.getModel().getWidth() / 2;
            }
            if (moveable) {
                ((HousePlanView) view).getPropertyPanel().setPropertys(locatorFurniture);
                Surface3D parentSurface = locatorFurniture.getParentSurface();
                if (parentSurface != null) {
                    try {
                        lastLocator = parentSurface.getClickedPointOnSurface(e, view.getViewCanvas());
                        lastPickPoint = lastLocator;
//                        HouseEdit.getHouseEditor().joinObject(new LocatorUndoableEdit(lastLocator));
                    } catch (CSHouseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        if (isMoveable()) {
            Surface3D parentSurface = locatorFurniture.getParentSurface();
            if (parentSurface != null) {
                try {
                    Point3d onParentSurfacePoint = parentSurface.getClickedPointOnSurface(e, view.getViewCanvas());
                    if (onParentSurfacePoint.distance(lastLocator) < 5) {
                        Vector3d moveVector = new Vector3d(onParentSurfacePoint.x, onParentSurfacePoint.y,
                                toSurfaceDesc);
                        Transform3D transform3D = new Transform3D();
                        transform3D.set(moveVector);
                        locatorFurniture.setTranslationTransform(transform3D);
                        lastLocator = new Point3d(moveVector);
                    }
                } catch (CSHouseException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void setEnable(boolean state) {
        this.enable = state;
        if (!enable && (mouseq != null)) {
            mouseq.clear();
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    class LocatorUndoableEdit extends AbstractUndoableEdit {
        private Transform3D lastLocation;

        public LocatorUndoableEdit(final Point3d locationPoint) {
            lastLocation = new Transform3D();
            lastLocation.set(new Vector3d(locationPoint.getX(),locationPoint.getY(),toSurfaceDesc));
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            doLocation();

        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            doLocation();
        }

        private void doLocation() {
            final Transform3D currentLocation = locatorFurniture.getTranslationTransform();
            locatorFurniture.setTranslationTransform(lastLocation);
            lastLocation = currentLocation;
        }
    }

    protected void finalize() {
        locatorFurniture.detachFromParent();
        locatorFurniture = null;
    }
}
