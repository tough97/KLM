package cs.designer.swing.list;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.CSPersistException;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.SurfaceMaterial;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import cs.designer.io.net.MerchandiseNetIO;
import cs.designer.io.net.NetOperation;
import cs.designer.screen.impi.DesignerScreen;
import cs.designer.swing.bean.MerchandiseBean;
import cs.designer.swing.tool.FurnitureLocator;
import cs.designer.swing.undo.FurnitureInUndoEditor;
import cs.designer.swing.undo.HouseEdit;
import cs.designer.swing.undo.SurfaceMaterialUndoEditor;
import cs.designer.view.viewer.HousePlanView;

import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.swing.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import static com.klm.persist.impl.LocalStorage.getLocalStorage;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/7/12
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class HtmlListDropTargetListener implements DropTargetListener {
    private HousePlanView view;
    private DesignerScreen designerScreen;

    public HtmlListDropTargetListener(final HousePlanView view,
                                      final DesignerScreen designerScreen) {
        this.view = view;
        this.designerScreen = designerScreen;

    }

    public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {

    }

    public void dragOver(DropTargetDragEvent dropTargetDragEvent) {

    }

    public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {

    }

    public void dragExit(DropTargetEvent dropTargetEvent) {


    }

    public void drop(final DropTargetDropEvent dropTargetDropEvent) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    final String merchandiseId = designerScreen.getControlPanel().getMerchandiseList().
                            getBorwser().executeJavascriptWithResult("return get_select_mer()").toString();

                    if (merchandiseId != null && merchandiseId.length() > 0) {
                        final MerchandiseBean merchandiseBean = new MerchandiseBean(merchandiseId);
                        NetOperation operation = new MerchandiseNetIO(merchandiseBean);
                        operation.select();
                        operation.update();
                        MerchandiseHtmlList.RECENTLY_MERCHANDISE.put(merchandiseId, merchandiseBean);
                        final Point mousePoint = dropTargetDropEvent.getLocation();
                        final Node pickObject = pickObject(mousePoint);
                        final Surface3D surface3D = pickObject instanceof Surface3D ? (Surface3D) pickObject : null;
                        if (surface3D != null) {
                            if (merchandiseBean.getType() == MerchandiseBean.MerchandiseType.surfaceMaterial) {
                                final SurfaceMaterial surfaceMaterial = (SurfaceMaterial) getLocalStorage().readMerchandise
                                        (merchandiseBean.getCode(), SurfaceMaterial.class, surface3D);
                                merchandiseBean.updateProject(surfaceMaterial);
                                if (surfaceMaterial.getImage() != null) {
                                    HouseEdit.getHouseEditor().joinObject(new SurfaceMaterialUndoEditor(surface3D));
                                    surfaceMaterial.setReflective(false);
                                    if (surface3D.isConnectiveSurface()) {
                                        Surface3D parentSurface3D = (Surface3D) surface3D.getFirstParentOf(Surface3D.class);
                                        if (parentSurface3D != null) {
                                            for (Surface3D connectiveSurface : parentSurface3D.getConnectiveSurfaces()) {
                                                connectiveSurface.setSurfaceMaterial(surfaceMaterial);
                                            }
                                        }
                                    } else {
                                        surface3D.setSurfaceMaterial(surfaceMaterial);
                                    }
                                }
                            }
                            if (merchandiseBean.getType() == MerchandiseBean.MerchandiseType.furniture) {
                                Furniture furniture = ((Furniture) getLocalStorage()
                                        .readMerchandise(merchandiseBean.getCode(), Furniture.class, surface3D));
                                merchandiseBean.updateProject(furniture);
                                Point3d plantPoint = surface3D.getClickedPointOnSurface(dropTargetDropEvent.
                                        getLocation(), view.getViewCanvas());
                                setInitPoint(plantPoint, furniture, surface3D);
                                FurnitureLocator locator = new FurnitureLocator(view, furniture);
                                HouseEdit.getHouseEditor().joinObject(new FurnitureInUndoEditor(furniture));
                            }
                        }


                    }
                } catch (CSHouseException e) {
                    e.printStackTrace();
                } catch (CSPersistException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private Surface3D pickObject(final Point point) {
        Surface3D pickresult = null;
        final PickCanvas pickCanvas =
                new PickCanvas(view.getViewCanvas(), view.getCurrentHouse());
        pickCanvas.setMode(PickCanvas.GEOMETRY);
        pickCanvas.setShapeLocation(point.x, point.y);

        final PickResult pr = pickCanvas.pickClosest();
        if (pr != null) {
            pickresult = pr.getNode(PickResult.BRANCH_GROUP) instanceof Surface3D
                    ? (Surface3D) pr.getNode(PickResult.BRANCH_GROUP) : null;

        }
        return pickresult;
    }

    private void setInitPoint(final Point3d initPoint,
                              final Furniture furniture,
                              final Surface3D parentSurface) {
        final Transform3D moveTransform3D = new Transform3D();
        final Vector3d movePoint = new Vector3d(initPoint.getX(), initPoint.getY(), 0);
        if (furniture.getModel() != null) {
            final Vector3d surfaceNormal = parentSurface.calculateSurfaceNormal();
            movePoint.setZ(surfaceNormal.getZ()
                    * furniture.getModel().getWidth() / 2);
            furniture.getModel().toXYPlan(parentSurface.calculateSurfaceNormal());
        }
        moveTransform3D.set(movePoint);
        furniture.setTranslationTransform(moveTransform3D);
        parentSurface.getAttachmentBG().addChild(furniture);
    }


}
