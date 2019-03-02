package cs.designer.swing.tool;

import javax.media.j3d.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/6/12
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class PickDragBehavior extends Behavior {

    private WakeupCriterion[] mouseEvents;
    private WakeupOr mouseCriterion;
    private int x, y;
    private int x_last, y_last;
    private double x_angle, y_angle;
    private double x_factor, y_factor;
    private Transform3D modelTrans;
    private Transform3D transformX;
    private Transform3D transformY;
    private TransformGroup transformGroup;
    private BranchGroup branchGroup;
    private Canvas3D canvas3D;

    public PickDragBehavior(final Canvas3D canvas3D,
                            final BranchGroup branchGroup,
                            final TransformGroup transformGroup) {
        this.canvas3D = canvas3D;
        this.branchGroup = branchGroup;
        this.transformGroup = transformGroup;

        modelTrans = new Transform3D();
        transformX = new Transform3D();
        transformY = new Transform3D();

    }

    public void initialize() {
        x = 0;
        y = 0;
        x_last = 0;
        y_last = 0;
        x_angle = 0;
        y_angle = 0;
        x_factor = .02;
        y_factor = .02;

        mouseEvents = new WakeupCriterion[2];
        mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        mouseCriterion = new WakeupOr(mouseEvents);
        wakeupOn(mouseCriterion);
    }

    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] event;
        int id;
        int dx, dy;

        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                for (int i = 0; i < event.length; i++) {
                    id = event[i].getID();
                    if (id == MouseEvent.MOUSE_DRAGGED) {

                        x = ((MouseEvent) event[i]).getX();
                        y = ((MouseEvent) event[i]).getY();

                        dx = x - x_last;
                        dy = y - y_last;

                        x_angle = dy * y_factor;
                        y_angle = dx * x_factor;

                        transformX.rotX(x_angle);
                        transformY.rotY(y_angle);

                        modelTrans.mul(transformX, modelTrans);
                        modelTrans.mul(transformY, modelTrans);

                        transformGroup.setTransform(modelTrans);

                        x_last = x;
                        y_last = y;
                    }
                }
            }
        }
        wakeupOn(mouseCriterion);
    }
}
