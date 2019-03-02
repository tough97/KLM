package cs.designer.swing.undo;

import com.klm.cons.impl.Surface3D;
import com.klm.persist.impl.SurfaceMaterial;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/8/12
 * Time: 10:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class SurfaceMaterialUndoEditor extends AbstractUndoableEdit {
    private Surface3D surface;
    private SurfaceMaterial material;

    public SurfaceMaterialUndoEditor(final Surface3D surface) {
        this.surface = surface;
        this.material = surface.getSurfaceMaterial();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        doMaterial();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        doMaterial();
    }

    private void doMaterial() {
        if (surface != null) {
            final SurfaceMaterial currentSurfaceMaterial = surface.getSurfaceMaterial();
            if (material != null) {
                if (surface.isConnectiveSurface()) {
                    final Surface3D parentSurface3D = (Surface3D) surface.getFirstParentOf(Surface3D.class);
                    for (final Surface3D connectiveSurface : parentSurface3D.getConnectiveSurfaces()) {
                        connectiveSurface.setSurfaceMaterial(material);
                    }
                } else {
                    surface.setSurfaceMaterial(material);
                }

            } else {
                if (surface.isConnectiveSurface()) {
                    final Surface3D parentSurface3D = (Surface3D) surface.getFirstParentOf(Surface3D.class);
                    for (final Surface3D connectiveSurface : parentSurface3D.getConnectiveSurfaces()) {
                        connectiveSurface.removeSurfaceMaterial();
                    }
                } else {
                    surface.removeSurfaceMaterial();
                }
            }
            material = currentSurfaceMaterial;
        }
    }
}
