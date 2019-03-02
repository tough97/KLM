package cs.designer.swing.undo;

import com.klm.persist.impl.Furniture;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/12/12
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class FurnitureDeUndoEditor extends AbstractUndoableEdit {
    private HouseUndoEditable furnitureUndoEditable;

    public FurnitureDeUndoEditor(final Furniture furniture) {
        furnitureUndoEditable = new FurnitureUndoEditImpl(furniture);

    }

    public void redo() {
        super.redo();
        furnitureUndoEditable.remove();
    }

    public void undo() {
        super.undo();
        furnitureUndoEditable.add();
    }
}