package cs.designer.module;

import com.klm.cons.impl.CSTransformGroup;
import com.klm.cons.impl.Wall;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import cs.designer.utils.ComputeUtill;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/25/12
 * Time: 10:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuxiliaryObject extends BranchGroup {
    private CSTransformGroup translationTG;
    private CSTransformGroup anchorPointTG;
    private boolean visible = true;
    private boolean anchorPointVisible = false;
    private BranchGroup auxiliaryLineGroup;
    private BranchGroup anchorPointGroup;

    public AuxiliaryObject() {
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
        //anchorPointTG
        anchorPointTG = new CSTransformGroup();
        anchorPointTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        anchorPointTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        anchorPointTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        anchorPointTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        addChild(anchorPointTG);
        auxiliaryLineGroup = new BranchGroup();
        auxiliaryLineGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        auxiliaryLineGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        auxiliaryLineGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        auxiliaryLineGroup.setCapability(BranchGroup.ALLOW_DETACH);
        //
        anchorPointGroup = new BranchGroup();
        anchorPointGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        anchorPointGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        anchorPointGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        anchorPointGroup.setCapability(BranchGroup.ALLOW_DETACH);
        //
        auxiliaryLineGroup.addChild(createline(0, 0, 100));
        final Appearance appearance = new Appearance();
        final ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(new Color3f(Color.RED));
        appearance.setColoringAttributes(ca);
        final Sphere sphere = new Sphere(0.1f, appearance);
        anchorPointGroup.addChild(sphere);
        translationTG.addChild(auxiliaryLineGroup);
    }

    public void setTranslation(float x, float z) {
        final Transform3D transform = new Transform3D();
        transform.set(new Vector3d(x, Wall.WALL_DEFAULT_HEIGHT + 0.1f, z));
        translationTG.setTransform(transform);
    }

    public float[] getAnchorPoint(final Wall wall,
                                  float mousex, float mouseY, float limint) {
        setAnchorPointVisible(wall != null);
        if (wall != null) {
            float[] currentPoint = null;
            if (wall.getWallShape().containsWallStartAt(mousex, mouseY, 0.1f)) {
                currentPoint = wall.getWallShape().getStartPoint();
            } else if (wall.getWallShape().containsWallEndAt(mousex, mouseY, 0.1f)) {
                currentPoint = wall.getWallShape().getEndPoint();
            } else if (wall.getWallShape().containsdPoint(mousex, mouseY, 0.1f)) {
                float[] XLineStart = {mousex, mouseY * 0.5f};
                float[] XLineEnd = {mousex, mouseY * 1.5f};
                float[] YLineStart = {mousex * 0.5f, mouseY};
                float[] YLineEnd = {mousex * 1.5f, mouseY};
                float[] intersectionPointX = ComputeUtill.computeIntersection(XLineStart, XLineEnd,
                        wall.getWallShape().getStartPoint(),
                        wall.getWallShape().getEndPoint());
                float[] intersectionPointY = ComputeUtill.computeIntersection(YLineStart, YLineEnd,
                        wall.getWallShape().getStartPoint(),
                        wall.getWallShape().getEndPoint());
                if (Math.abs(Math.abs(wall.getWallShape().getXStart())
                        - Math.abs(wall.getWallShape().getXEnd())) < 0.1f) {
                    currentPoint = new float[]{wall.getWallShape().getXStart(), intersectionPointY[1]};

                } else if (Math.abs(Math.abs(wall.getWallShape().getYStart())
                        - Math.abs(wall.getWallShape().getYEnd())) < 0.1f) {
                    currentPoint = new float[]{intersectionPointX[0], wall.getWallShape().getYStart()};
                } else {
                    currentPoint = new float[]{(intersectionPointX[0] + intersectionPointY[0]) / 2,
                            (intersectionPointX[1] + intersectionPointY[1]) / 2};
                }

            }
            if (currentPoint != null) {
                final Transform3D transform = new Transform3D();
                transform.set(new Vector3d(currentPoint[0], Wall.WALL_DEFAULT_HEIGHT + limint, currentPoint[1]));
                anchorPointTG.setTransform(transform);
            }
            return currentPoint;

        }
        return null;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            if (visible) {
                translationTG.addChild(auxiliaryLineGroup);
            } else {
                translationTG.removeAllChildren();
            }
        }
    }

    private void setAnchorPointVisible(boolean visible) {
        if (this.anchorPointVisible != visible) {
            this.anchorPointVisible = visible;
            if (visible) {
                anchorPointTG.addChild(anchorPointGroup);
            } else {
                anchorPointGroup.detach();
                anchorPointTG.removeAllChildren();
            }
        }
    }

    private Shape3D createline(float centerX, float centerZ, double length) {
        final Shape3D shape = new Shape3D();
        LineArray la = new LineArray(4, LineArray.COORDINATES);
        la.setCoordinate(0, new Point3d(centerX, 0, centerZ + length / 2));
        la.setCoordinate(1, new Point3d(centerX, 0, centerZ - length / 2));
        la.setCoordinate(2, new Point3d(centerX - length / 2, 0, centerZ));
        la.setCoordinate(3, new Point3d(centerX + length / 2, 0, centerZ));
        shape.setGeometry(la);
        Appearance appearance = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(new Color3f(new Color(213, 212, 212)));
        appearance.setColoringAttributes(ca);
        LineAttributes sla = new LineAttributes();
        sla.setLinePattern(LineAttributes.PATTERN_DASH);
        sla.setLineWidth(0.1f);
        appearance.setLineAttributes(sla);
        final TransparencyAttributes ta = new TransparencyAttributes();
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        ta.setTransparency(0f);
        appearance.setTransparencyAttributes(ta);
        shape.setAppearance(appearance);
        return shape;
    }

    public void reset() {

    }

}
