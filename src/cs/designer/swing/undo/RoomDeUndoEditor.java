package cs.designer.swing.undo;

import com.klm.cons.impl.Floor;
import com.klm.cons.impl.Room;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/16/12
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoomDeUndoEditor extends AbstractUndoableEdit {
    private HouseUndoEditable addRemoveRoom;

    public RoomDeUndoEditor(final Floor currentFloor, final Room currentRoom) {
        addRemoveRoom = new RoomUndoEditImpl(currentFloor, currentRoom);
    }


    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        addRemoveRoom.add();

    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        addRemoveRoom.remove();
    }

}
