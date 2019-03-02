package cs.designer.swing.undo;

import com.klm.cons.impl.CSHouseException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/16/12
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HouseUndoEditable {
    public void add();

    public void remove();
}
