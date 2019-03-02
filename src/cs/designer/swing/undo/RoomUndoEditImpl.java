package cs.designer.swing.undo;

import com.klm.cons.impl.Floor;
import com.klm.cons.impl.Room;
import com.klm.cons.impl.Wall;
import cs.designer.module.TempWall;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/17/12
 * Time: 8:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class RoomUndoEditImpl implements HouseUndoEditable {
    private Room currentRoom;
    private Floor currentFloor;
    private List<WallUndoEditImpl> addRemoveWalls;


    public RoomUndoEditImpl(final Floor currentFloor, final Room currentRoom) {
        this.currentFloor = currentFloor;
        this.currentRoom = currentRoom;
        addRemoveWalls = new ArrayList<WallUndoEditImpl>();
        for (Wall wall : currentRoom.getWalls()) {
            TempWall tempWall = TempWall.walls.get(wall.getWallShape());
            if (tempWall != null) {
                WallUndoEditImpl addRemoveWall = new WallUndoEditImpl(tempWall);
                addRemoveWalls.add(addRemoveWall);
            }
        }

    }

    public void add() {
        addRoom(currentRoom);

    }

    public void remove() {
        removeRoom(currentRoom);

    }

    private void addRoom(Room addRoom) {
        this.currentFloor.addRoom(addRoom);
        for (WallUndoEditImpl addRemoveWall : addRemoveWalls) {
            addRemoveWall.add();
        }

    }

    private void removeRoom(final Room removeRoom) {
        this.currentFloor.removeRoom(removeRoom,true);
        for (WallUndoEditImpl addRemoveWall : addRemoveWalls) {
            addRemoveWall.remove();
        }
    }
}
