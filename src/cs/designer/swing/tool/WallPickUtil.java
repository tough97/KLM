package cs.designer.swing.tool;

import com.klm.cons.impl.Surface3D;
import com.klm.cons.impl.Wall;
import com.sun.j3d.utils.picking.PickResult;
import cs.designer.view.viewer.DisplayView;

import javax.media.j3d.Node;

public class WallPickUtil extends AbstractPickUtil {

    public WallPickUtil(final DisplayView view) {
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

    }

    @Override
    public synchronized void updateScene(int xpos, int ypos) {
        PickResult pickResult;
        try {
            if (pickCanvas != null)
                pickCanvas.setShapeLocation(xpos, ypos);
            pickResult = pickCanvas.pickClosest();
            if (pickResult != null) {
                if (pickResult.getNode(PickResult.BRANCH_GROUP) instanceof Surface3D) {
                    Node pickWall = getResult(pickResult.getNode(PickResult.BRANCH_GROUP), Wall.class);
                        result = pickWall;
                }
            } else {
                result = null;
            }

        } catch (NullPointerException e) {

        }
    }
}
