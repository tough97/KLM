package cs.designer.swing.undo;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.CSPersistException;
import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.LocalStorage;
import com.klm.persist.impl.SurfaceMaterial;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/12/12
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class FurnitureUndoEditImpl implements HouseUndoEditable {
    private Surface3D parent;
    private Furniture furniture;
    private boolean installed;

    public FurnitureUndoEditImpl(final Furniture furniture) {
        this.furniture = furniture;
        this.parent = furniture.getParentSurface();
        installed = furniture.isInstalled();
    }

    public void add() {
        if (parent != null) {
            furniture.detachFromParent();
            parent.getAttachmentBG().addChild(furniture);
            if (installed) {
                try {
                    furniture.install();
                } catch (CSHouseException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void remove() {
        if (installed) {
            try {
                furniture.unInstall();
            } catch (CSHouseException e) {
                e.printStackTrace();
            }
        }
        furniture.detachFromParent();
    }


    protected void  finalize(){
        Merchandise.merchandises.remove(furniture.getId());
    }
}
