package cs.designer.swing.undo;

import com.klm.persist.impl.Furniture;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/12/12
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class FurnitureInUndoEditor extends AbstractUndoableEdit {
    private HouseUndoEditable furnitureUndoEditable;

    public FurnitureInUndoEditor(final Furniture furniture) {
        furnitureUndoEditable = new FurnitureUndoEditImpl(furniture);

    }

    public void redo() {
        super.redo();
        furnitureUndoEditable.add();
    }

    public void undo() {
        super.undo();
        furnitureUndoEditable.remove();
    }
}
