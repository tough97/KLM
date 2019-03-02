package cs.designer.view.controller;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import cs.designer.swing.tool.AbstractPickUtil;
import cs.designer.swing.tool.Generatorable;
import cs.designer.swing.tool.PickUtil;
import cs.designer.swing.tool.SubSurfaceGenerator;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rongyang
 */
public class SubSurfaceControler implements DisplayControlable {
    public enum SubSurfaceOperType {
        DRAW, TRANSLATION, RESIZE_UP, RESIZE_DOWN, RESIZE_LEFT, RESIZE_RITHT
    }

    private DisplayView view;
    private AbstractPickUtil pickTool;
    private SurfaceMouseControler mouseControler;
    private SubSurfaceGenerator surfaceGenerator;
    private SubSurfaceOperType operType = SubSurfaceOperType.DRAW;
    private Point3d lastDraggePoint;

    public SubSurfaceControler(DisplayView view) {
        this.view = view;
        surfaceGenerator = new SubSurfaceGenerator((HousePlanView) view);
        pickTool = new PickUtil(view);
    }

    public void registerController(TransformGroup contrGroup, boolean addListenerable) {
        mouseControler = new SurfaceMouseControler(view);
        if (addListenerable) {
            view.getViewCanvas().addMouseListener(mouseControler);
            view.getViewCanvas().addMouseMotionListener(mouseControler);
            view.getViewCanvas().addMouseWheelListener(mouseControler);
        }
    }

    public DisplayView getView() {
        return this.view;
    }

    public Generatorable getGenerator() {
        return surfaceGenerator;
    }

    public void reset() {
        mouseControler.reset();

    }

    public MouseControler getMouseControler() {
        return mouseControler;
    }

    public AbstractPickUtil getPickTool() {
        return pickTool;
    }

    public void setOperType(SubSurfaceOperType operType) {
        this.operType = operType;
    }

    class SurfaceMouseControler extends MouseControler {
        private static final double INTER_VALUES = 0.5;
        private Surface3D pickSurface = null;
        private Surface3D lastPickSurface = null;
        private Cursor defaultCursor;
        private Rectangle2D rectangleCursorUp, rectangleCursorDown,
                rectangleCursorLeft, rectangleCursorRight;


        public SurfaceMouseControler(DisplayView view) {
            super(view);
            defaultCursor = view.getViewCanvas().getCursor();
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            if (pickSurface != null) {
                Point3d mouseMovePoint3d = null;
                try {
                    mouseMovePoint3d = pickSurface.getClickedPointOnSurface(me, view.getViewCanvas());
                } catch (CSHouseException e) {
                    e.printStackTrace();
                }
                Point2D mousePoint = new Point2D.Double(mouseMovePoint3d.x, mouseMovePoint3d.y);
//                if (rectangleCursorUp != null
//                        && rectangleCursorUp.contains(mousePoint)) {
//                    this.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
//                    operType = SubSurfaceOperType.RESIZE_UP;
//                } else if (rectangleCursorDown != null && rectangleCursorDown.contains(mousePoint)) {
//                    this.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
//                    operType = SubSurfaceOperType.RESIZE_DOWN;
//                } else if (rectangleCursorLeft != null && rectangleCursorLeft.contains(mousePoint)) {
//                    this.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
//                    operType = SubSurfaceOperType.RESIZE_LEFT;
//                } else if (rectangleCursorRight != null && rectangleCursorRight.contains(mousePoint)) {
//                    this.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
//                    operType = SubSurfaceOperType.RESIZE_RITHT;
//                } else
                if (surfaceGenerator.getDrawShape() != null
                        && surfaceGenerator.getDrawShape().contains(mousePoint)) {
                    this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                    operType = SubSurfaceOperType.TRANSLATION;

                } else {
                    this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                    operType = SubSurfaceOperType.DRAW;
                }
            }

        }

        @Override
        public void mousePressed(MouseEvent me) {
            if (view.getOperateType() == DisplayView.OperateType.ADD_SUBSURFACE){
                if (me.getButton() == MouseEvent.BUTTON1) {
                    if (operType == SubSurfaceOperType.DRAW) {
                        pickSurface = pickTool.getPickResult(me.getX(), me.getY()) instanceof Surface3D ?
                                (Surface3D) pickTool.getPickResult(me.getX(), me.getY()) : null;
                        if (lastPickSurface != null &&
                                lastPickSurface != pickSurface) {
                            lastPickSurface.getIndicatorShape().setGeometry(new Shape3D().getGeometry());

                        }
                        if (pickSurface != null) {
                            if (pickSurface.isConnectiveSurface()) {
                                pickSurface = null;
                            }
                            try {
                                Point3d staPoint3d = pickSurface.getClickedPointOnSurface(me, view.getViewCanvas());
                                if (staPoint3d != null &&
                                        testPointInSurface(staPoint3d, pickSurface)) {
                                    surfaceGenerator.setBaseSurface(pickSurface);
                                    surfaceGenerator.setStartPoint(staPoint3d);
                                }
                            } catch (CSHouseException ex) {
                                ex.printStackTrace();
                            }
                        }
                        lastPickSurface = pickSurface;
                    } else if (operType != SubSurfaceOperType.DRAW
                            && pickSurface != null) {
                        try {
                            lastDraggePoint = pickSurface.getClickedPointOnSurface(me, view.getViewCanvas());
                        } catch (CSHouseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (view.getOperateType()
                    == DisplayView.OperateType.ADD_SUBSURFACE) {
                if (pickSurface != null) {
                    try {
                        Point3d draggePoint = pickSurface.getClickedPointOnSurface(me, view.getViewCanvas());
                        if (draggePoint != null) {
                            if (operType == SubSurfaceOperType.DRAW) {
                                if (surfaceGenerator.getStartPoint() == null) {
                                    surfaceGenerator.setBaseSurface(pickSurface);
                                    surfaceGenerator.setStartPoint(draggePoint);
                                } else {
                                    if (testPointInSurface(draggePoint, pickSurface)) {
                                        surfaceGenerator.setEndPoint(draggePoint);
                                    }
                                }
                            } else if (lastDraggePoint != null) {

                                if (operType == SubSurfaceOperType.TRANSLATION) {
                                    Transform3D trans = new Transform3D();
                                    trans.set(new Vector3d(draggePoint.x - lastDraggePoint.x,
                                            draggePoint.y - lastDraggePoint.y,
                                            draggePoint.z - lastDraggePoint.z));
                                    surfaceGenerator.translateSubSurface(trans);

                                }
//                                else if (operType == SubSurfaceOperType.RESIZE_UP) {
//
//                                    surfaceGenerator.reSizeUp(draggePoint.y - lastDraggePoint.y);
//
//
//                                } else if (operType == SubSurfaceOperType.RESIZE_DOWN) {
//                                    surfaceGenerator.reSizeDown(draggePoint.y - lastDraggePoint.y);
//
//
//                                } else if (operType == SubSurfaceControler.SubSurfaceOperType.RESIZE_LEFT) {
//                                    surfaceGenerator.reSizeLeft(draggePoint.x - lastDraggePoint.x);
//
//
//                                } else if (operType == SubSurfaceControler.SubSurfaceOperType.RESIZE_RITHT) {
//                                    surfaceGenerator.reSizeRight(draggePoint.x - lastDraggePoint.x);
//
//                                }

                                lastDraggePoint = draggePoint;
                            }

                        }
                    } catch (CSHouseException ex) {
                        Logger.getLogger(SubSurfaceControler.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }

        private void setCursor(Cursor cursor) {
            view.getViewCanvas().setCursor(cursor);

        }

        private boolean testPointInSurface(Point3d point, Surface3D surface)
                throws CSHouseException {
            return surface.getIdentifiedShape().contains(point.x, point.y);

        }

        @Override
        public void mouseReleased(MouseEvent me) {
//            if (surfaceGenerator.getDrawShape() != null) {
//                Point3d startPoint = surfaceGenerator.getStartPoint();
//                Point3d endPoint = surfaceGenerator.getEndPoint();
//                double xStart = startPoint.x;
//                double yStart = startPoint.y;
//                double xEnd = endPoint.getX();
//                double yEnd = endPoint.getY();
//                if (startPoint.x > endPoint.getX()) {
//                    xStart = endPoint.getX();
//                    xEnd = startPoint.x;
//                }
//                if (startPoint.y > endPoint.getY()) {
//                    yStart = endPoint.getY();
//                    yEnd = startPoint.y;
//                }
//                double width = xEnd - xStart;
//                double height = yEnd - yStart;
//                surfaceGenerator.getStartPoint().setX(xStart);
//                surfaceGenerator.getStartPoint().setY(yStart);
//                surfaceGenerator.getEndPoint().setX(xEnd);
//                surfaceGenerator.getEndPoint().setY(yEnd);
//                if (startPoint != null && endPoint != null) {
//                    rectangleCursorUp =
//                            new Rectangle2D.Double(xStart + INTER_VALUES, yStart + height - INTER_VALUES, width, INTER_VALUES);
//                    rectangleCursorDown =
//                            new Rectangle2D.Double(xStart + INTER_VALUES, yStart, width, INTER_VALUES);
//                    rectangleCursorRight =
//                            new Rectangle2D.Double(xStart, yStart, INTER_VALUES, height);
//                    rectangleCursorLeft =
//                            new Rectangle2D.Double(xStart + width - INTER_VALUES, yStart, INTER_VALUES, height);
//
//                }
//            }
        }

        @Override
        public void controlerMousePressed(MouseEvent me) {
            mousePressed(me);
        }

        @Override
        public void controlerMouseMoved(MouseEvent me) {
            mouseMoved(me);
        }

        @Override
        public void controlerMouseDragged(MouseEvent me) {
            mouseDragged(me);
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
            ((HousePlanView) view).setPickToolable(true);
            setOperType(SubSurfaceControler.SubSurfaceOperType.DRAW);
            surfaceGenerator = new SubSurfaceGenerator(((HousePlanView) view));
            if (pickSurface != null) {
                pickSurface.getIndicatorShape().setGeometry(new Shape3D().getGeometry());
            }
            lastDraggePoint = null;
            pickSurface = null;
            if (defaultCursor != null) {
                view.setCursor(defaultCursor);
            }
        }

        @Override
        public void getHomeTransform(Transform3D homeTransform) {

        }

        public boolean isINIndicatorShape(final Point2d mousePoint) {
            boolean ret = false;
            if (pickSurface != null &&
                    surfaceGenerator.getDrawShape() != null) {
                ret = surfaceGenerator.getDrawShape().
                        contains(new Point2D.Double(mousePoint.x, mousePoint.y));

            }
            return ret;

        }

    }
}
