package cs.designer.view.viewer;

import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.SurfaceMaterial;
import cs.designer.module.SurfaceMaterialDisplayFace;
import cs.designer.swing.tool.AbstractPickUtil;
import cs.designer.swing.tool.ModelEditPickUtil;
import cs.designer.view.controller.DisplayControlable;
import cs.designer.view.controller.ModelControler;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import javax.xml.crypto.dsig.Transform;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/12/12
 * Time: 9:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseEditorView extends DisplayView {
    private Merchandise currentMerchandise;
    private ModelControler controller;
    private AbstractPickUtil pickUtil;

    public MerchandiseEditorView() {
        super();
        controller = new ModelControler(this);
        pickUtil = new ModelEditPickUtil(this);
        controller.registerController(controlTg, true);
        setViewType(ViewType.ORBIT_VIEW);
        setBackGroupColor(Color.WHITE);
        addLights(getObjRoot());
        displayScreen();
    }

    public void setCurrentMerchandise(final Merchandise currentMerchandise) {
        this.currentMerchandise = currentMerchandise;
        if (currentMerchandise instanceof Furniture) {
            Furniture currentFurniture = (Furniture) currentMerchandise;
            setModel(currentFurniture);
            final Point3d centPoint = new Point3d();
            final BoundingSphere bounds = (BoundingSphere) currentFurniture.getBounds();
            bounds.getCenter(centPoint);
            controller.getControler().setRotationCenter(centPoint);
            double radius = bounds.getRadius();
            controller.setViewDistance(2* radius);
            controller.setMinViewerDistance(radius);
            controller.setMaxViewerDistance(8 * radius);
            final Transform3D homeTrans = new Transform3D();
            controller.getView().getViewTransformGroup().getTransform(homeTrans);
            controller.getControler().setHomeTransform(homeTrans);
        } else if (currentMerchandise instanceof SurfaceMaterial) {
            SurfaceMaterial currentSurfaceMaterial = (SurfaceMaterial) currentMerchandise;
            setModel(new SurfaceMaterialDisplayFace(currentSurfaceMaterial).getSurface());
            final Point3d centPoint = new Point3d();
            controller.getControler().setRotationCenter(centPoint);
            controller.setViewDistance(3);
            controller.setMinViewerDistance(1);
            controller.setMaxViewerDistance(8);
            final Transform3D homeTrans = new Transform3D();
            controller.getView().getViewTransformGroup().getTransform(homeTrans);
            controller.getControler().setHomeTransform(homeTrans);
        }

    }

    public static void addLights(Group node) {
        Light[] lights = {
                new DirectionalLight(new Color3f(0.9f, 0.9f, 0.9f), new Vector3f(1.732f, -0.8f, -1)),
                new DirectionalLight(new Color3f(0.9f, 0.9f, 0.9f), new Vector3f(-1.732f, -0.8f, -1)),
                new DirectionalLight(new Color3f(0.9f, 0.9f, 0.9f), new Vector3f(0, -0.8f, 1)),
                new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f))};

        for (Light light : lights) {
            light.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));
            node.addChild(light);
        }

    }

    public Merchandise getCurrentMerchandise() {
        return currentMerchandise;
    }

    @Override
    public void setEnabled(ContrType contrType, boolean enabled) {

    }

    @Override
    public TransformGroup getControllerGroup() {
        return controlTg;
    }

    @Override
    public void setController(DisplayControlable controller) {
        this.controller = (ModelControler) controller;
        controller.registerController(controlTg, true);
    }


    public AbstractPickUtil getPickUtil() {
        return pickUtil;
    }

    public DisplayControlable getController() {
        return this.controller;
    }


}
