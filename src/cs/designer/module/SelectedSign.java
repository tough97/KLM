package cs.designer.module;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.CSTransformGroup;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.meta.AppearanceMeta;
import com.klm.persist.meta.Shape3DMeta;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import cs.designer.view.viewer.DisplayView;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3d;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/27/12
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class SelectedSign extends BranchGroup {
    private CSTransformGroup translationTG;
    private Surface3D parent;

    public SelectedSign() {
        init();

    }

    private void init() {
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ALLOW_DETACH);
        translationTG = new CSTransformGroup();
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        addChild(translationTG);
        translationTG.addChild(createline(0.2));
    }

    private Shape3D createline(double length) {
        final Shape3D shape = new Shape3D();
        LineArray la = new LineArray(4, LineArray.COORDINATES);
        la.setCoordinate(0, new Point3d(0, length / 2, 0));
        la.setCoordinate(1, new Point3d(0, -length / 2, 0));
        la.setCoordinate(2, new Point3d(-length / 2, 0, 0));
        la.setCoordinate(3, new Point3d(length / 2, 0, 0));
        shape.setGeometry(la);
        Appearance appearance = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(new Color3f(Color.RED));
        appearance.setColoringAttributes(ca);
        LineAttributes sla = new LineAttributes();
        sla.setLineWidth(1);
        appearance.setLineAttributes(sla);
        shape.setAppearance(appearance);
        return shape;
    }

    public void setParent(final Surface3D parent) {
        if (this.parent == null) {
            parent.getAttachmentBG().addChild(this);
        } else {
            this.parent.getAttachmentBG().removeChild(this);
            parent.getAttachmentBG().addChild(this);
        }
        this.parent = parent;
    }

    public void setLocaPoint(final Point3d locaPoint) {
        translationTG.setTranslation(new Vector3d(locaPoint.getX(),
                locaPoint.getY(), 0.1));
    }

    public void setParent(final Surface3D parent,
                          final Point3d locaPoint) {
        setParent(parent);
        setLocaPoint(locaPoint);
    }

    public void setParent(final Surface3D parent,
                          int mouseX, int mouseY,
                          final DisplayView view) {
        setParent(parent);
        setLocaPoint(mouseX, mouseY, view);
    }

    public void setLocaPoint(int mouseX, int mouseY, final DisplayView view) {
        if (this.parent != null) {
            try {
                final Point3d locaPoint = parent.getClickedPointOnSurface(new Point(mouseX, mouseY),
                        view.getViewCanvas());
                if (locaPoint != null) {
                    setLocaPoint(locaPoint);
                }
            } catch (CSHouseException e) {
                e.printStackTrace();
            }

        }
    }

}
