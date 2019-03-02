package cs.designer.swing.undo;

import com.klm.cons.impl.Surface3D;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/17/12
 * Time: 10:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class Surface3DInUndoEditor extends AbstractUndoableEdit {
    private HouseUndoEditable addRemoveSurface;

    public Surface3DInUndoEditor(final Surface3D currentSurface) {
        addRemoveSurface = new Surface3DUndoEditImpl(currentSurface);

    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        addRemoveSurface.remove();

    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        addRemoveSurface.add();
    }

}
