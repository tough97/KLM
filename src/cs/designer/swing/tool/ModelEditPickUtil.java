package cs.designer.swing.tool;

import com.klm.persist.meta.ModelMeta;
import com.sun.j3d.utils.picking.PickResult;
import cs.designer.module.ModelBoundingBox;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.MerchandiseEditorView;

import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/12/12
 * Time: 11:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModelEditPickUtil extends AbstractPickUtil {
    public ModelEditPickUtil(final DisplayView view) {
        super(view);
    }

    @Override
    public Node getPickResult(final Class pickClass, int x, int y) {
        updateScene(x, y);
        return getPickResult(pickClass);
    }

    @Override
    public Node getPickResult(int x, int y) {
        updateScene(x, y);
        return result;
    }

    @Override
    public Node getPickResult(final Class pickClass) {
        return getResult(result, pickClass);
    }

    @Override
    public void clear() {

    }

    @Override
    public void updateScene(int xpos, int ypos) {
        PickResult pickResult = null;
        try {
            if (pickCanvas != null) {
                pickCanvas.setShapeLocation(xpos, ypos);
                pickResult = pickCanvas.pickClosest();
                if (pickResult != null) {
                    if (pickResult.getNode(PickResult.SHAPE3D) != null) {
                        result = pickResult.getNode(PickResult.SHAPE3D);
                        if (result != null) {
                            final ModelBoundingBox boundingBox =
                                    (ModelBoundingBox) getResult(result, ModelBoundingBox.class);
                            boundingBox.setSelectedShape((Shape3D) result);
                            if (boundingBox != null) {
                                final ModelMeta model = (ModelMeta) getResult(result, ModelMeta.class);
                                model.setInstallationSurface(boundingBox.getSelectedVector());
                                ((MerchandiseEditorView) getDisplayView()).getController().reset();
                            }

                        }
                    }
                } else {
                    result = null;
                }
            }


        } catch (NullPointerException e) {

        }

    }
}
