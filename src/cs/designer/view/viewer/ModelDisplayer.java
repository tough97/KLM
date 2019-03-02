package cs.designer.view.viewer;

import cs.designer.swing.tool.AbstractPickUtil;
import cs.designer.swing.tool.ModelEditPickUtil;
import cs.designer.view.controller.DisplayControlable;
import cs.designer.view.controller.ModelControler;

import javax.media.j3d.TransformGroup;

/**
 * @author rongyang
 */
public class ModelDisplayer extends DisplayView {

    private boolean rotateable = true;
    private boolean zoomable = true;
    private boolean translateable = true;
    private DisplayControlable controller;
    private AbstractPickUtil pickUtil;

    public ModelDisplayer() {
        super();
        controller = new ModelControler(this);
        pickUtil = new ModelEditPickUtil(this);
        controller.registerController(controlTg, true);
        setViewType(ViewType.ORBIT_VIEW);
        displayScreen();
    }

    @Override
    public TransformGroup getControllerGroup() {
        return controlTg;
    }

    public void setEnabled(ContrType contrType, boolean enabled) {
        if (contrType == ContrType.ROTATER) {
            rotateable = enabled;
        } else if (contrType == ContrType.TRANSLATE) {
            translateable = enabled;
        } else if (contrType == ContrType.ZOOMER) {
            zoomable = enabled;
        }
    }

    public boolean isRotateable() {
        return rotateable;
    }

    public boolean isTranslateable() {
        return translateable;
    }

    public boolean isZoomable() {
        return zoomable;
    }

    @Override
    public void setController(DisplayControlable controller) {
        this.controller = controller;
        controller.registerController(controlTg, true);
    }

    public DisplayControlable getController() {
        return this.controller;
    }

    public AbstractPickUtil getPickUtil() {
        return pickUtil;
    }
}
