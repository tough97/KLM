package cs.designer.swing.undo;

import com.klm.cons.impl.Surface3D;
import com.klm.cons.impl.Wall;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/9/12
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class HoleInUnDoEditor extends AbstractUndoableEdit {
    private HouseUndoEditable addRemveHole;

    public HoleInUnDoEditor(final Wall wall,
                            final Surface3D holeFace,
                            int surfaceFlag) {
        addRemveHole = new HoleUndoEditImpl(wall, holeFace, surfaceFlag);

    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        addRemveHole.remove();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        addRemveHole.add();
    }
}
