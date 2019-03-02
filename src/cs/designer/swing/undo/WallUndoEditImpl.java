package cs.designer.swing.undo;
import cs.designer.module.TempWall;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/16/12
 * Time: 8:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class WallUndoEditImpl implements HouseUndoEditable {
    private TempWall tempWall;

    public WallUndoEditImpl(final TempWall tempWall) {
        this.tempWall = tempWall;

    }

    public synchronized void add() {
        tempWall.addToParent();

    }

    public synchronized void remove() {
        tempWall.removeFromParent(true);
    }
    protected void finalize(){
        this.tempWall=null;
    }

}
