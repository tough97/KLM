/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.designer.swing.tool;

import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import cs.designer.view.viewer.DisplayView;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.vecmath.Point3d;

/**
 * @author rongyang
 */
public abstract class AbstractPickUtil extends PickMouseBehavior {
    private BranchGroup parent;
    private DisplayView view;
    protected Node result;

    public AbstractPickUtil(DisplayView view) {
        super(view.getViewCanvas(), view.getObjRoot(), new BoundingSphere(new Point3d(0, 0, 0), 10000));
        this.view = view;
        setCapability(PickMouseBehavior.ALLOW_PICKABLE_READ);
        setCapability(PickMouseBehavior.ALLOW_PICKABLE_WRITE);
        this.parent = view.getObjRoot();
        this.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 10000));
        view.getObjRoot().addChild(this);
        setMode(PickTool.GEOMETRY);

    }

    public void setParent(BranchGroup parent) {
        this.parent.removeChild(this);
        parent.addChild(this);
    }

    public void detach() {
        parent.removeChild(this);
    }

    public DisplayView getDisplayView() {
        return view;
    }

    public abstract Node getPickResult(final Class pickClass, int x, int y);

    public abstract Node getPickResult(int x, int y);

    public abstract Node getPickResult(final Class pickClass);

    public Node getPickResult() {
        return result;
    }

    public abstract void clear();

    protected Node getResult(final Node result, final Class parentKlz) {
        if (result != null) {
            Node node = result.getParent();
            while (node != null) {
                if (parentKlz.getCanonicalName().equals(node.getClass().getCanonicalName())) {
                    return node;
                } else {
                    node = node.getParent();
                }
            }
            return node;
        }
        return null;
    }


}
