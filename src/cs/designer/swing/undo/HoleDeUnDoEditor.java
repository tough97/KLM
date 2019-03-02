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
 * Time: 9:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class HoleDeUnDoEditor extends AbstractUndoableEdit{
    private HouseUndoEditable addRemveHole;

    public HoleDeUnDoEditor(final Wall wall,
                            final Surface3D holeFace,
                            int surfaceFlag) {
        addRemveHole = new HoleUndoEditImpl(wall, holeFace, surfaceFlag);

    }

    @Override
    public void undo() throws CannotUndoException
        {
        super.undo();
        addRemveHole.add();
    }

    @Override
    public void redo() throws CannotRedoException
        {
        super.redo();
        addRemveHole.remove();
    }
}