package cs.modeleditor;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.CSPersistException;
import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.LocalStorage;
import com.klm.persist.impl.SurfaceMaterial;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import cs.designer.swing.bean.CoBean;
import cs.designer.swing.bean.MerchandiseBean;
import cs.designer.swing.tool.FurnitureLocator;
import cs.designer.swing.undo.HouseEdit;
import cs.designer.swing.undo.SurfaceMaterialUndoEditor;
import cs.designer.view.viewer.HousePlanView;
import cs.designer.view.viewer.MerchandiseEditorView;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
* Created by IntelliJ IDEA.
* User: rongyang
* Date: 1/10/12
* Time: 10:09 AM
* To change this template use File | Settings | File Templates.
*/
public class PreviewDropTargetListener implements DropTargetListener {
    private HousePlanView houseView;
    private MerchandiseEditorView merchandiseEditorView;

    public PreviewDropTargetListener(final HousePlanView houseView,
                                     final MerchandiseEditorView merchandiseEditorView) {
        this.houseView = houseView;
        this.merchandiseEditorView = merchandiseEditorView;

    }

    public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {

    }

    public void dragOver(DropTargetDragEvent dropTargetDragEvent) {

    }

    public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {

    }

    public void dragExit(DropTargetEvent dropTargetEvent) {


    }

    public void drop(DropTargetDropEvent dropTargetDropEvent) {
        Transferable transferable = dropTargetDropEvent.getTransferable();
        String mimeType = "application/x-java-jvm-local-objectref;class="
                + new CoCheckBox().getClass().getName();
        try {
            final DataFlavor dataFlavor = new DataFlavor(mimeType);
            final CoBean bean = (CoBean) transferable.getTransferData(dataFlavor);

            if (bean instanceof MerchandiseBean) {
                final MerchandiseBean merchandiseBean = (MerchandiseBean) bean;
                final Point mousePoint = dropTargetDropEvent.getLocation();
                final Surface3D surface3D = pickObject(mousePoint);
                if (surface3D != null) {
                    if (merchandiseBean.getType() == MerchandiseBean.MerchandiseType.surfaceMaterial) {
                        final SurfaceMaterial surfaceMaterial = (SurfaceMaterial) readMerchandise(merchandiseBean.getModelSource());
                        surfaceMaterial.setName(merchandiseBean.getName());
                        if (surfaceMaterial.getImage() != null) {
                            HouseEdit.getHouseEditor().joinObject(new SurfaceMaterialUndoEditor(surface3D));
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
                        Furniture furniture = (Furniture) readMerchandise(merchandiseBean.getModelSource());
                        furniture.setName(merchandiseBean.getName());
                        Point3d plantPoint = surface3D.getClickedPointOnSurface(dropTargetDropEvent.
                                getLocation(), houseView.getViewCanvas());
                        setInitPoint(plantPoint, furniture, surface3D);
                        FurnitureLocator locator = new FurnitureLocator(houseView, furniture);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CSHouseException e) {
            e.printStackTrace();
        }
    }

    private Surface3D pickObject(final Point point) {
        Surface3D pickresult = null;
        final PickCanvas pickCanvas =
                new PickCanvas(houseView.getViewCanvas(), houseView.getCurrentHouse());
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

    private Merchandise readMerchandise(byte[] source) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(source);
        ObjectInputStream ois = null;
        Object object = null;
        try {
            ois = new ObjectInputStream(byteArrayInputStream);
            object = ois.readObject();
            ois.close();
            byteArrayInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (object instanceof Merchandise) {
            return (Merchandise) object;
        }
        return null;

    }

}
