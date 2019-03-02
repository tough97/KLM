package cs.designer.view.controller;

import com.klm.cons.impl.Floor;
import cs.designer.swing.tool.Generatorable;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;
import cs.designer.view.viewer.Movement;
import cs.designer.view.viewer.VirtualView;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author rongyang
 */
public class VirtualViewController implements DisplayControlable, ViewControlable {

    public final static float DEFAULT_VIEW_HEIGHT = 1.7f;
    public final static double DEFAULT_VIEW_MINI_ANGLE = -Math.PI / 6;
    public final static double DEFAULT_VIEW_MAX_ANGLE = Math.PI / 6;
    private DisplayView view;
    private VirtualView virtualView;
    private VirtualViewKeyControler viewKeyControler;
    private MouseControler viewMouseControler;
    private Transform3D homeTransform;

    public VirtualViewController(DisplayView view) {
        this.view = view;
        homeTransform = new Transform3D();
        homeTransform.set(new Vector3f(0f, DEFAULT_VIEW_HEIGHT, 0f));
    }

    public void registerController(TransformGroup contrGroup, boolean addListenerable) {
        virtualView = new VirtualView(contrGroup, DEFAULT_VIEW_HEIGHT);
        viewMouseControler = new VirtualViewMouseControler(view);
        if (addListenerable) {
            view.getViewCanvas().addKeyListener(viewKeyControler);
        }


    }

    public DisplayView getView() {
        return view;
    }

    public Generatorable getGenerator() {
        return null;
    }

    public KeyboardControler getKeyboardControler() {
        return viewKeyControler;
    }

    public MouseControler getMouseControler() {
        return this.viewMouseControler;
    }

    public VirtualView getVirtualView() {
        return virtualView;
    }

    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    public void resetVirtualView() {
        virtualView.setTransform3D(homeTransform);
    }

    public void setHomeTransform(Transform3D homeTransform) {
        this.homeTransform = homeTransform;
        virtualView.reset();
        virtualView.setTransform3D(this.homeTransform);
    }

    public void reset() {
        homeTransform = new Transform3D();
        homeTransform.set(new Vector3f(0f, DEFAULT_VIEW_HEIGHT, 0f));
        setHomeTransform(homeTransform);
    }

    public void moveUp() {
        virtualView.stepForward();

    }

    public void moveDown() {
        virtualView.stepBackward();
    }

    public void moveLeft() {
        virtualView.stepLeft();
    }

    public void moveRight() {
        virtualView.stepRight();
    }

    public void moveRest() {
        homeTransform = new Transform3D();
        homeTransform.set(new Vector3f(0f, DEFAULT_VIEW_HEIGHT, 0f));
        virtualView.setTransform3D(this.homeTransform);
    }

    class VirtualViewKeyControler extends KeyboardControler {

        public VirtualViewKeyControler() {
        }

        @Override
        public void controlerkeyTyped(KeyEvent ke) {
            keyTyped(ke);
        }

        @Override
        public void controlerkeyPressed(KeyEvent ke) {
            keyPressed(ke);
        }

        @Override
        public void controlerkeyReleased(KeyEvent ke) {
            keyReleased(ke);

        }

        public void keyTyped(KeyEvent ke) {
        }

        public void keyPressed(KeyEvent ke) {
            if (view.getViewType()
                    == HousePlanView.ViewType.VIRTUAL_VIEW) {
                int keyCode = ke.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        virtualView.stepForward();
                        break;
                    case KeyEvent.VK_A:
                        virtualView.stepLeft();
                        break;
                    case KeyEvent.VK_S:
                        virtualView.stepBackward();
                        break;
                    case KeyEvent.VK_D:
                        virtualView.stepRight();
                }
            }
        }

        public void keyReleased(KeyEvent ke) {
        }
    }

    class VirtualViewMouseControler extends MouseControler {

        private int lastDraggedX = 0;
        private int lastDraggedY = 0;

        public VirtualViewMouseControler(DisplayView view) {
            super(view);
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (view.getViewType()
                    == HousePlanView.ViewType.VIRTUAL_VIEW) {
                int currentX = me.getX();
                int currentY = me.getY();
                if (currentX > lastDraggedX) {
                    virtualView.lookRight();
                } else if (currentX < lastDraggedX) {
                    virtualView.lookLeft();
                } else if (currentY > lastDraggedY) {
                    double tempAngle = virtualView.getxAngle() - Movement.DEFAULT_ROTATE_RATE;
                    if (tempAngle > DEFAULT_VIEW_MINI_ANGLE
                            && tempAngle < DEFAULT_VIEW_MAX_ANGLE) {
                        virtualView.lookDown();
                    }
                } else if (currentY < lastDraggedY) {
                    double tempAngle = virtualView.getxAngle() + Movement.DEFAULT_ROTATE_RATE;
                    if (tempAngle > DEFAULT_VIEW_MINI_ANGLE
                            && tempAngle < DEFAULT_VIEW_MAX_ANGLE) {
                        virtualView.lookUp();
                    }
                }
                lastDraggedX = currentX;
                lastDraggedY = currentY;
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            if (view.getViewType()
                    == HousePlanView.ViewType.VIRTUAL_VIEW) {
                if (mwe.getWheelRotation() > 0) {
                    virtualView.stepBackward();
                } else {
                    virtualView.stepForward();
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
            mouseDragged(me);

        }

        @Override
        public void controlerMouseWheelMoved(MouseWheelEvent mwe) {
            mouseWheelMoved(mwe);
        }

        @Override
        public void controlerMouseReleased(MouseEvent me) {

        }

        @Override
        public void reset() {
            lastDraggedX = 0;
            lastDraggedY = 0;

        }

        @Override
        public void getHomeTransform(Transform3D homeTransform) {


        }
    }
}
