package cs.designer.swing.tool;

import com.klm.cons.impl.Surface3D;
import com.klm.persist.impl.Furniture;
import com.klm.persist.meta.Shape3DMeta;
import com.sun.j3d.utils.picking.PickResult;
import cs.designer.module.ModelBoundingBox;
import cs.designer.module.SelectedSign;
import cs.designer.swing.tool.AbstractPickUtil;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/12/12
 * Time: 8:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PickUtil extends AbstractPickUtil {
    private static Node lastPickObject;
    private static SelectedSign selectedSign = new SelectedSign();

    public PickUtil(final DisplayView view) {
        super(view);
    }

    @Override
    public Node getPickResult(final Class pickClass, int x, int y) {
        updateScene(x, y);
        return getResult(result, pickClass);
    }

    @Override
    public Node getPickResult(int x, int y) {
        updateScene(x, y);
        return result;
    }

    @Override
    public Node getPickResult(final Class pickClass) {
        return getResult(result, pickClass);
    }

    @Override
    public void clear() {
        if (lastPickObject instanceof Furniture) {
            if(((Furniture) lastPickObject).getModel()!=null){
            ((Furniture) lastPickObject).getModel().hideOutLines();
            }
        }
        lastPickObject = null;
        result = null;
    }

    @Override
    public synchronized void updateScene(int xpos, int ypos) {
        PickResult pickResult = null;
        try {
            HousePlanView view = (HousePlanView) getDisplayView();
            if (view.getMouseButton() == MouseEvent.BUTTON1) {
                if (pickCanvas != null)
                    pickCanvas.setShapeLocation(xpos, ypos);
                pickResult = pickCanvas.pickClosest();
                if (pickResult != null) {
                    if (pickResult.getNode(PickResult.SHAPE3D) instanceof Shape3DMeta) {
                        result = getResult(pickResult.getNode(PickResult.SHAPE3D), Furniture.class);
                    } else if (pickResult.getNode(PickResult.BRANCH_GROUP) instanceof Surface3D) {
                        final Surface3D pickSurface = (Surface3D) pickResult.getNode(PickResult.BRANCH_GROUP);
                        if (getDisplayView().getOperateType() !=DisplayView.OperateType.ADD_SUBSURFACE) {
                            selectedSign.setParent(pickSurface, xpos, ypos, getDisplayView());
                        }
                        result = pickSurface;

                    } else {
                        result = null;
                    }
                } else {
                    result = null;
                }
                setSelectStatus();
            }

        } catch (NullPointerException e) {

        }
    }

    public void setSelectStatus() {
        if (lastPickObject instanceof Furniture
                && lastPickObject != result) {
            ((Furniture) lastPickObject).getModel().hideOutLines();
        }
        if (result instanceof Furniture
                && result != lastPickObject) {
            ((Furniture) result).getModel().showOutLines();
        }
        lastPickObject = result;
    }

}
