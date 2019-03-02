package cs.designer.view.controller;

import com.klm.cons.impl.Room;
import cs.designer.module.BaseFace;
import cs.designer.swing.property.PropertyPanel;
import cs.designer.swing.resources.ResourcesPath;
import cs.designer.swing.tool.Generatorable;
import cs.designer.swing.tool.ViewButtonControls;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;
import cs.designer.view.viewer.MiniMap;
import net.sf.ezmorph.object.MapToDateMorpher;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/18/12
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ViewControler implements DisplayControlable, ViewControlable {
    public final static float DEFAULT_DISTANCE = 700;    //Distance
    private PropertyPanel propertyPanel;
    private HousePlanView view;
    private static BufferedImage obitBg;
    private TransformGroup contrGroup;
    private PlanViewMouseControler mouseControler;
    private BaseFace baseFace;
    public double yDistance;
    public final static float UINT = 20;
    private double minViewerDistance = 10;
    private double maxViewerDistance = 1200;

    static {
        try {

            obitBg = ImageIO.read(ResourcesPath.getResourcesUrl("log.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ViewControler(final HousePlanView view, final PropertyPanel propertyPanel) {
        this.view = view;
        this.propertyPanel = propertyPanel;
        baseFace = view.getPlanControler().getBaseFace();

    }

    public void registerController(TransformGroup contrGroup, boolean addListenerable) {
        this.contrGroup = contrGroup;
        mouseControler = new PlanViewMouseControler(view);
        mouseControler.setSchedulingBounds(DisplayView.BOUNDS);
        if (addListenerable) {
            view.getViewCanvas().addMouseListener(mouseControler);
            view.getViewCanvas().addMouseMotionListener(mouseControler);
            view.getViewCanvas().addMouseWheelListener(mouseControler);
        }
        setViewDistance(0, DEFAULT_DISTANCE, 0);
    }

    public DisplayView getView() {
        return view;
    }

    public Generatorable getGenerator() {
        return null;
    }

    public void zoomIn(float zoomUint) {
        mouseControler.zoomView(zoomUint);
    }

    public void reset() {
        setViewDistance(0, DEFAULT_DISTANCE, 0);
    }

    public MouseControler getControler() {
        return mouseControler;
    }

    public void setViewDistance(double xDistance, double yDistance, double zDistance) {
        if (view.getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
            final Transform3D trans = new Transform3D();
            trans.set(new Vector3d(xDistance, yDistance, zDistance));
            Transform3D rot = new Transform3D();
            rot.rotX(-Math.PI / 2);
            trans.mul(rot);
            view.getViewTransformGroup().setTransform(trans);
            this.yDistance = yDistance;
        }
    }

    public void moveView(Vector3d vector3d) {
        final Transform3D trans = new Transform3D();
        trans.set(vector3d);
        Transform3D lastTrans = new Transform3D();
        view.getViewTransformGroup().getTransform(lastTrans);
        trans.mul(lastTrans);
        view.getViewTransformGroup().setTransform(trans);
    }

    public Vector3d getCurrentViewVector() {
        final Transform3D lastTrans = new Transform3D();
        view.getViewTransformGroup().getTransform(lastTrans);
        final Vector3d moveVector = new Vector3d();
        lastTrans.get(moveVector);
        return moveVector;
    }

    public void restoreDistance() {
        setViewDistance(0, yDistance, 0);
    }

    public void changeView() {
        if (view.getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
            orbitView();

        } else if (view.getViewType() == DisplayView.ViewType.ORBIT_VIEW) {
            virtuallView();
        } else if (view.getViewType() == DisplayView.ViewType.VIRTUAL_VIEW) {
            drawPlanView();


        }
    }

    public void virtuallView() {
        if (view.getViewType() !=
                DisplayView.ViewType.VIRTUAL_VIEW) {
            view.setViewType(DisplayView.ViewType.VIRTUAL_VIEW);
            view.setOperateType(DisplayView.OperateType.DEFAULT);
            View view = this.view.getVieScreenUniverse().getViewer().getView();
            view.setFieldOfView(Math.PI/2);
            this.view.getVirtualViewController().resetVirtualView();
            this.view.getPlanControler().getBaseFace().setVisible(false);
            this.view.getPlanControler().getBaseFace().displayLine(false);
            this.view.getPlanControler().getGenerator().reset();
            this.view.getMiniMap().setVisible(true);
//            this.view.setBackGroupColor(DisplayView.DEFAULT_BACK_COLOR);
            ((MiniMap) this.view.getMiniMap()).setAvatarVisible(true);
            displayCeiling(true);
            propertyPanel.setPropertys(view);
            propertyPanel.setModifyControler(this.view.getVirtualViewController());
        }
    }

    public void drawPlanView() {
        if (view.getViewType() !=
                DisplayView.ViewType.DRAW_PLANVIEW) {
            view.setViewType(DisplayView.ViewType.DRAW_PLANVIEW);
            View view = this.view.getVieScreenUniverse().getViewer().getView();
            view.setFieldOfView(0.05);
            this.view.getPlanControler().getBaseFace().setVisible(true);
            this.view.getPlanControler().getBaseFace().displayLine(true);
//            this.view.getPlanControler().getBaseFace().setBaseShapeAppMaterial(BaseFace.DESIGN_BG_MATERIAL);
            this.view.getMiniMap().setVisible(false);
            ((MiniMap) this.view.getMiniMap()).setAvatarVisible(false);
            this.view.setPickToolable(false);
            displayCeiling(false);
        }
        restoreDistance();
        this.view.setOperateType(DisplayView.OperateType.DEFAULT);
        propertyPanel.setPropertys(view);
        propertyPanel.setModifyControler(this.view.getPlanViewControler());

    }

    public void orbitView() {
        if (view.getViewType() != DisplayView.ViewType.ORBIT_VIEW) {
            view.setViewType(DisplayView.ViewType.ORBIT_VIEW);
            view.setOperateType(DisplayView.OperateType.ORBIT);
            final View view = this.view.getVieScreenUniverse().getViewer().getView();
            final BoundingSphere bounds = (BoundingSphere) this.view.getCurrentFloor().getBounds();
            Point3d centerPoint = new Point3d();
            bounds.getCenter(centerPoint);
            this.view.getModelControler().getControler().setRotationCenter(centerPoint);
            Transform3D transform3D = new Transform3D();
            transform3D.set(new Vector3d(centerPoint.x, 3 * centerPoint.y, 2 * bounds.getRadius()));
            this.view.getModelControler().setMaxViewerDistance(20 * bounds.getRadius());
            this.view.getViewTransformGroup().setTransform(transform3D);
            this.view.getModelControler().getControler().setHomeTransform(transform3D);
            view.setFieldOfView(1);
            this.view.viewScreenUniverse.getViewingPlatform().
                    setViewPlatformBehavior(this.view.getModelControler().getControler());
            this.view.getMiniMap().setVisible(true);
//            this.view.setBackGroupColor(DisplayView.DEFAULT_BACK_COLOR);

            this.view.getPlanControler().getBaseFace().setVisible(false);
            this.view.getPlanControler().getBaseFace().displayLine(false);
            this.view.getPlanControler().getGenerator().reset();
            ((MiniMap) this.view.getMiniMap()).setAvatarVisible(false);
            if (this.view.getOperateType() == DisplayView.OperateType.ORBIT) {
                this.view.setPickToolable(true);
            } else if (this.view.getOperateType() == DisplayView.OperateType.ADD_SUBSURFACE ||
                    this.view.getOperateType() == DisplayView.OperateType.MAKE_HOLE) {
                this.view.setPickToolable(false);
            }
            displayCeiling(false);
            propertyPanel.setPropertys(view);
            propertyPanel.setModifyControler(this.view.getModelControler());

        }
    }

    public void moveUp() {
        switch (view.getViewType()) {
            case DRAW_PLANVIEW:
                Vector3d currentVector = view.getPlanViewControler().getCurrentViewVector();
                currentVector.add(ViewButtonControls.MOVE_UP);
                view.getPlanViewControler().setViewDistance(currentVector.x, currentVector.y, currentVector.z);
                break;
            case ORBIT_VIEW:
                break;
            case VIRTUAL_VIEW:
                view.getVirtualViewController().getVirtualView().stepForward();
                break;
            default:
                throw (new RuntimeException("Unknown viewType"));
        }

    }

    public void moveDown() {
        switch (view.getViewType()) {
            case DRAW_PLANVIEW:
                Vector3d currentVector = view.getPlanViewControler().getCurrentViewVector();
                currentVector.add(ViewButtonControls.MOVE_DOWN);
                view.getPlanViewControler().setViewDistance(currentVector.x, currentVector.y, currentVector.z);
                break;
            case ORBIT_VIEW:

                break;
            case VIRTUAL_VIEW:
                view.getVirtualViewController().getVirtualView().stepBackward();
                break;
            default:
                throw (new RuntimeException("Unknown viewType"));
        }

    }

    public void moveLeft() {
        switch (view.getViewType()) {
            case DRAW_PLANVIEW:
                Vector3d currentVector = view.getPlanViewControler().getCurrentViewVector();
                currentVector.add(ViewButtonControls.MOVE_LEFT);
                view.getPlanViewControler().setViewDistance(currentVector.x, currentVector.y, currentVector.z);
                break;
            case ORBIT_VIEW:

                break;
            case VIRTUAL_VIEW:
                view.getVirtualViewController().getVirtualView().stepLeft();
                break;
            default:
                throw (new RuntimeException("Unknown viewType"));
        }

    }

    public void moveRight() {
        switch (view.getViewType()) {
            case DRAW_PLANVIEW:
                Vector3d currentVector = view.getPlanViewControler().getCurrentViewVector();
                currentVector.add(ViewButtonControls.MOVE_RIGHT);
                view.getPlanViewControler().setViewDistance(currentVector.x, currentVector.y, currentVector.z);
                break;
            case ORBIT_VIEW:

                break;
            case VIRTUAL_VIEW:
                view.getVirtualViewController().getVirtualView().stepRight();
                break;
            default:
                throw (new RuntimeException("Unknown viewType"));
        }

    }

    public void moveRest() {
        setViewDistance(0, DEFAULT_DISTANCE, 0);
    }

    private void displayCeiling(boolean visible) {
        TransformGroup floorTrans = view.getCurrentFloor().getFloorTrans();
        for (int index = 0; index < floorTrans.numChildren(); index++) {
            Node floorChild = floorTrans.getChild(index);
            if (floorChild instanceof Room) {
                Room room = (Room) floorChild;
                if (visible) {
                    room.removeChild(room.getCeilingDown());
                    room.addChild(room.getCeilingDown());
                } else {
                    room.removeChild(room.getCeilingDown());
                }
            }
        }
    }

    class PlanViewMouseControler extends MouseControler {
        private Point3d lastDraggePoint;


        public PlanViewMouseControler(DisplayView view) {
            super(view);
        }

        public void zoomView(float zoomUint) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                double tempDistance = yDistance;
                tempDistance += UINT * zoomUint;
                if (tempDistance < maxViewerDistance && tempDistance > minViewerDistance) {
                    final Vector3d currentViewVector = getCurrentViewVector();
                    setViewDistance(currentViewVector.x, tempDistance, currentViewVector.z);
                }

            }
        }


        @Override
        public void mouseDragged(MouseEvent me) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                if (view.getOperateType() == DisplayView.OperateType.DEFAULT) {
                    view.getViewCanvas().setCursor(new Cursor(Cursor.HAND_CURSOR));
                    Point3d draggePoint = baseFace.getClickedPointOnSurface(me, view.getViewCanvas());
                    if (lastDraggePoint != null) {
                        Vector3d moveVector = new Vector3d((lastDraggePoint.x - draggePoint.x) * 0.2, 0,
                                (lastDraggePoint.z - draggePoint.z) * 0.2);
                        moveView(moveVector);
                        view.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    }
                    lastDraggePoint = draggePoint;

                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (view.getOperateType() == DisplayView.OperateType.DEFAULT) {
                lastDraggePoint = null;
                view.getViewCanvas().setCursor(DisplayView.defaultCursor);
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            zoomView(mwe.getWheelRotation());
        }

        @Override
        public void controlerMousePressed(MouseEvent me) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                if (view.getOperateType() == DisplayView.OperateType.DRAW_PLAN) {
                    mousePressed(me);
                }
            }
        }

        @Override
        public void controlerMouseMoved(MouseEvent me) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                if (view.getOperateType() == DisplayView.OperateType.DRAW_PLAN) {
                    mouseMoved(me);
                }
            }
        }

        @Override
        public void controlerMouseDragged(MouseEvent me) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                if (view.getOperateType() == DisplayView.OperateType.DRAW_PLAN) {
                    mouseDragged(me);
                }
            }
        }

        @Override
        public void controlerMouseWheelMoved(MouseWheelEvent mwe) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                mouseWheelMoved(mwe);

            }
        }

        @Override
        public void controlerMouseReleased(MouseEvent me) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                if (view.getOperateType() == DisplayView.OperateType.DRAW_PLAN) {
                    mouseReleased(me);
                }
            }
        }

        @Override
        public void reset() {
        }

        @Override
        public void getHomeTransform(Transform3D homeTransform) {

        }
    }

}
