package cs.designer.swing.undo;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.cons.impl.Wall;
import com.klm.cons.impl.WallShape;
import com.klm.util.CSUtilException;
import cs.designer.module.TempWall;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/17/12
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class Surface3DUndoEditImpl implements HouseUndoEditable {
    private Surface3D currentSurface;
    private Surface3D parentSurface;
    private WallShape wallShape;
    private Integer faceFlage;

    public Surface3DUndoEditImpl(Surface3D currentSurface) {
        this.currentSurface = currentSurface;
        parentSurface = currentSurface.getParentSurface();
        final Wall wall = (Wall) parentSurface.getFirstParentOf(Wall.class);
        if (wall != null
                &&parentSurface!=null) {
            wallShape = wall.getWallShape();
            faceFlage = wall.getSurfaceFlag(parentSurface);
        }
    }

    public synchronized void add() {
        addSurface(currentSurface);

    }

    public synchronized void remove() {
        removeSurface(currentSurface);

    }

    private void addSurface(final Surface3D currentSurface) {
        if (wallShape != null) {
            Wall wall = TempWall.walls.get(wallShape) == null ? null : TempWall.walls.get(wallShape).getWall();
            if (wall != null&&faceFlage!=null) {
                parentSurface = wall.getSurface(faceFlage);
            }
        }
        try {
            if (parentSurface != null) {
                parentSurface.addSubSurface(currentSurface);
            }
        } catch (CSHouseException e) {
            e.printStackTrace();
        } catch (CSUtilException e) {
            e.printStackTrace();
        }

    }

    private void removeSurface(final Surface3D currentSurface) {
        if (currentSurface != null) {
            try {
                currentSurface.detachFromParent();
            } catch (CSHouseException e) {
                e.printStackTrace();
            }
        }
    }
}
