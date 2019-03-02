package cs.designer.module;

import com.klm.cons.impl.WallShape;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import java.awt.*;

/**
 * @author rongyang
 */
public class WallModel extends BranchGroup {

    private WallShape wallShape;
    private Shape3D model;

    public WallModel(WallShape wallShape) {
        this.wallShape = wallShape;
        model = new Shape3D();
        Appearance appearance = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(new Color3f(Color.GRAY));
        appearance.setColoringAttributes(ca);
        model.setAppearance(appearance);
        initWall();
        updateWall();
        addChild(model);
    }

    private void initWall() {
        setCapability(BranchGroup.ALLOW_PARENT_READ);
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        //
        model.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
        model.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        model.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        model.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        model.setCapability(Shape3D.ENABLE_COLLISION_REPORTING);
        model.setCapability(Shape3D.ENABLE_PICK_REPORTING);
        model.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        model.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    }

    private Point3d[] getPoints() {
        float[][] WallShapePoint = wallShape.getPoints();
        Point3d[] points = {new Point3d(WallShapePoint[0][0], 0, WallShapePoint[0][1]),
                new Point3d(WallShapePoint[1][0], 1, WallShapePoint[1][1]),
                new Point3d(WallShapePoint[2][0], 1, WallShapePoint[2][1]),
                new Point3d(WallShapePoint[3][0], 1, WallShapePoint[3][1])};
        return points;
    }

    private Geometry createlineeometry(final Point3d[] coordinates) {
        IndexedQuadArray qa = new IndexedQuadArray(12,
                QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2, 24);
        int[] coordIndexs = {3, 2, 1, 0};
        qa.setCoordinates(0, coordinates);
        qa.setCoordinateIndices(0, coordIndexs);
        return qa;
    }

    public void setWallShape(WallShape wallShape) {
        this.wallShape = wallShape;
        updateWall();
    }

    private void updateWall() {
        model.setGeometry(createlineeometry(getPoints()));
    }

    public WallShape getWallShape() {
        return wallShape;
    }
}
