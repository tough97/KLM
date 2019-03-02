package cs.designer.view.controller;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import cs.designer.swing.tool.CoOrbitBehavior;
import cs.designer.view.viewer.DisplayView;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author rongyang
 */
public abstract class MouseControler extends CoOrbitBehavior {

    public MouseControler(DisplayView view) {
        super(view.getViewCanvas(), OrbitBehavior.REVERSE_ALL);
        super.setRotFactors(0.8,0.8);
    }


    public abstract void controlerMousePressed(MouseEvent me);

    public abstract void controlerMouseMoved(MouseEvent me);

    public abstract void controlerMouseDragged(MouseEvent me);

    public abstract void controlerMouseWheelMoved(MouseWheelEvent mwe);

    public abstract void controlerMouseReleased(MouseEvent me);

    public abstract void reset();
}
