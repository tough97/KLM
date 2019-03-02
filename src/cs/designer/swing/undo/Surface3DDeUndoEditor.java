package cs.designer.swing.undo;

import com.klm.cons.impl.Surface3D;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/18/12
 * Time: 12:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class Surface3DDeUndoEditor extends AbstractUndoableEdit {
    private HouseUndoEditable addRemoveSurface;

    public Surface3DDeUndoEditor(final Surface3D currentSurface) {
        addRemoveSurface = new Surface3DUndoEditImpl(currentSurface);

    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        addRemoveSurface.add();

    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        addRemoveSurface.remove();
    }
}