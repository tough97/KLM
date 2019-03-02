package cs.designer.swing.undo;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.cons.impl.Wall;
import com.klm.util.CSUtilException;

import javax.vecmath.Point3d;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/9/12
 * Time: 8:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class HoleUndoEditImpl implements HouseUndoEditable {
    private Wall wall;
    private Surface3D holeFace;
    private int surfaceFlag;
    private Point3d[] holeFaceCoods;

    public HoleUndoEditImpl(final Wall wall,
                            final Surface3D holeFace,
                            int surfaceFlag) {
        this.wall = wall;
        this.holeFace = holeFace;
        this.surfaceFlag = surfaceFlag;
        try {
            this.holeFaceCoods = holeFace.getCoordsOnParent();
        } catch (CSHouseException e) {
            e.printStackTrace();
        }
    }

    public void add() {
        try {
            holeFace = wall.drillHole(holeFaceCoods,
                    wall.getSurface(surfaceFlag));
        } catch (CSHouseException e) {
            e.printStackTrace();
        } catch (CSUtilException e) {
            e.printStackTrace();
        }


    }

    public void remove() {
        wall.removeHole(holeFace);
    }
}
