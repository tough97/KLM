package cs.designer.swing.undo;

import com.klm.cons.impl.Floor;
import com.klm.cons.impl.Room;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/17/12
 * Time: 10:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class RoomBuilderUndoEditor extends AbstractUndoableEdit {
    private Room currentRoom;
    private Floor currentFloor;

    public RoomBuilderUndoEditor(final Floor currentFloor, final Room currentRoom) {
        this.currentFloor = currentFloor;
        this.currentRoom = currentRoom;

    }


    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        this.currentFloor.removeRoom(currentRoom, false);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        this.currentFloor.addRoom(currentRoom);

    }

}