package cs.designer.view.controller;

import cs.designer.module.BaseFace;
import cs.designer.swing.property.PropertyPanel;
import cs.designer.swing.tool.Generatorable;
import cs.designer.swing.tool.WallGenerator;
import cs.designer.view.viewer.DisplayView;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author rongyang
 */
public class PlanControler implements DisplayControlable {

    private PropertyPanel propertyPanel;
    private WallGenerator wallGenerator;
    private DisplayView view;
    private TransformGroup contrGroup;
    private PlanMouseControler mouseControler;
    private BaseFace baseFace;


    public PlanControler(final DisplayView view, final PropertyPanel propertyPanel) {
        this.view = view;
        this.baseFace = new BaseFace();
        this.propertyPanel = propertyPanel;
    }

    public void registerController(final TransformGroup contrGroup, boolean addListenerable) {
        this.contrGroup = contrGroup;
        this.wallGenerator = new WallGenerator(view);
        view.getObjRoot().addChild(baseFace);
        mouseControler = new PlanMouseControler(view);
        mouseControler.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 2000));
        if (addListenerable) {
            view.getViewCanvas().addMouseListener(mouseControler);
            view.getViewCanvas().addMouseMotionListener(mouseControler);
            view.getViewCanvas().addMouseWheelListener(mouseControler);
        }
    }

    public MouseControler getControler() {
        return mouseControler;
    }

    public DisplayView getView() {
        return this.view;
    }

    public Generatorable getGenerator() {
        return wallGenerator;
    }

    public WallGenerator getWallGenerator() {
        return wallGenerator;
    }

    public BaseFace getBaseFace() {
        return baseFace;
    }

    public void reset() {
        wallGenerator.reset();

    }


    class PlanMouseControler extends MouseControler {


        public PlanMouseControler(DisplayView view) {
            super(view);

        }


        @Override
        public void mousePressed(MouseEvent me) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                if (view.getOperateType() == DisplayView.OperateType.DRAW_PLAN) {
                    Point3d mousePoint3d = new Point3d(baseFace.getClickedPointOnSurface(me, view.getViewCanvas()));
                    wallGenerator.produceWall(me, (float)mousePoint3d.x, (float) mousePoint3d.z);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                if (view.getOperateType() == DisplayView.OperateType.DRAW_PLAN) {
                    Point3d mousePoint3d =
                            new Point3d(baseFace.getClickedPointOnSurface(me, view.getViewCanvas()));
                    wallGenerator.produceWallModel(me, (float) mousePoint3d.getX(), (float) mousePoint3d.getZ());
                }
            }
        }



        @Override
        public void controlerMousePressed(MouseEvent me) {
            if (view.getViewType()
                    == DisplayView.ViewType.DRAW_PLANVIEW) {
                    mousePressed(me);
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
    }
}
