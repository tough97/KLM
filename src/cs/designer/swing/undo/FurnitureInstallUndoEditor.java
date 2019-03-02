package cs.designer.swing.undo;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.impl.Furniture;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/9/12
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FurnitureInstallUndoEditor extends AbstractUndoableEdit {
    private Furniture furniture;

    public FurnitureInstallUndoEditor(final Furniture furniture) {
        this.furniture = furniture;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        try {
            furniture.unInstall();
        } catch (CSHouseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            furniture.install();
        } catch (CSHouseException e) {
            e.printStackTrace();
        }
    }
}
