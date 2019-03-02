package cs.designer.swing.undo;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.cons.impl.Wall;
import com.klm.util.CSUtilException;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.vecmath.Point3d;
import java.awt.geom.Area;

public class WallPropertyUndoEditor extends AbstractUndoableEdit {
    private Surface3D holeSurface;
    private Wall currentWall;
    private Surface3D wallFace;

    public WallPropertyUndoEditor(final Point3d[] holeCoords, final Wall currentWall, Surface3D wallFace) {
        holeSurface = getHoleSurface(holeCoords, currentWall, wallFace);
        this.currentWall = currentWall;
        this.wallFace = wallFace;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        currentWall.removeHole(holeSurface);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        System.out.println("wallredo");
        try {
            currentWall.drillHole(holeSurface.getIdentifiedCoords(), wallFace);
        } catch (CSHouseException e) {
            e.printStackTrace();
        } catch (CSUtilException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPresentationName() {
        return "wall";
    }

    private Surface3D getHoleSurface(final Point3d[] holeCoords, final Wall currentWall, Surface3D wallFace) {
        Surface3D holeSurface = null;
        try {
            holeSurface = new Surface3D(holeCoords);
            for (Surface3D subSurface : wallFace.getSubSurfaces()) {
                if (new Area(subSurface.getIdentifiedShape()).equals(new Area(holeSurface.getIdentifiedShape()))) {
                    return holeSurface;
                }
            }
        } catch (CSHouseException e) {

        }
        return null;

    }
}
