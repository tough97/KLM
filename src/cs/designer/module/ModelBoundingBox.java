package cs.designer.module;

import com.klm.persist.meta.AppearanceMeta;
import com.klm.persist.meta.Shape3DMeta;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;

public class ModelBoundingBox extends BranchGroup {

    public static final double TOLERANCE = 0.00001;
    public static final int FRONT = 0;
    public static final int BACK = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int TOP = 4;
    public static final int BOTTOM = 5;
    public static final Color SELECTED_COLOR = new Color(1f, 1f, 1f, 0.5f);
    public static final Color ORDINARY_COLOR = new Color(0, 121, 121);
    public static final float TRANSPARENTCY_OF_SELECTION = 0.4f;
    public static final Appearance ORDINARY_APPEARANCE = new AppearanceMeta();
    public static final Appearance SELECTED_APPEARANCE = new AppearanceMeta();

    static {
        PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        Material m = new Material(new Color3f(ORDINARY_COLOR), new Color3f(
                ORDINARY_COLOR), new Color3f(ORDINARY_COLOR), new Color3f(
                ORDINARY_COLOR), 64.0f);
        ORDINARY_APPEARANCE.setMaterial(m);
        ORDINARY_APPEARANCE.setPolygonAttributes(pa);

        pa = new PolygonAttributes();
        pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        m = new Material(new Color3f(SELECTED_COLOR), new Color3f(
                SELECTED_COLOR), new Color3f(SELECTED_COLOR), new Color3f(
                SELECTED_COLOR), 64.0f);
        SELECTED_APPEARANCE.setMaterial(m);
        SELECTED_APPEARANCE.setPolygonAttributes(pa);
        final TransparencyAttributes ta = new TransparencyAttributes();

        ta.setTransparency(TRANSPARENTCY_OF_SELECTION);
        ta.setTransparencyMode(TransparencyAttributes.FASTEST);
        SELECTED_APPEARANCE.setTransparencyAttributes(ta);
    }

    public static final Vector3d[] NORMALS = {
            new Vector3d(0.0, 0.0, -1.0), new Vector3d(0.0, 0.0, 1.0),
            new Vector3d(1.0, 0.0, 0.0), new Vector3d(-1.0, 0.0, 0.0),
            new Vector3d(0.0, 1.0, 0.0), new Vector3d(0.0, -1.0, 0.0)
    };

    public ModelBoundingBox(final Point3d lower, final Point3d upper) {
        super();
        lower.setX(lower.x - TOLERANCE);
        lower.setY(lower.y - TOLERANCE);
        lower.setZ(lower.z - TOLERANCE);
        upper.setX(upper.x + TOLERANCE);
        upper.setY(upper.y + TOLERANCE);
        upper.setZ(upper.z + TOLERANCE);
        setCapability(ALLOW_DETACH);
        setCapability(ALLOW_CHILDREN_WRITE);
        setCapability(ALLOW_CHILDREN_READ);
        setCapability(ALLOW_CHILDREN_EXTEND);


        insertChild(createShape3D(new Point3d[]{
                new Point3d(upper.x, upper.y, upper.z),
                new Point3d(lower.x, upper.y, upper.z),
                new Point3d(lower.x, lower.y, upper.z),
                new Point3d(upper.x, lower.y, upper.z)}), FRONT);
        insertChild(createShape3D(new Point3d[]{
                new Point3d(upper.x, lower.y, lower.z),
                new Point3d(lower.x, lower.y, lower.z),
                new Point3d(lower.x, upper.y, lower.z),
                new Point3d(upper.x, upper.y, lower.z),}), BACK);
        insertChild(createShape3D(new Point3d[]{
                new Point3d(lower.x, lower.y, lower.z),
                new Point3d(lower.x, lower.y, upper.z),
                new Point3d(lower.x, upper.y, upper.z),
                new Point3d(lower.x, upper.y, lower.z)}), LEFT);
        insertChild(createShape3D(new Point3d[]{
                new Point3d(upper.x, upper.y, lower.z),
                new Point3d(upper.x, upper.y, upper.z),
                new Point3d(upper.x, lower.y, upper.z),
                new Point3d(upper.x, lower.y, lower.z)}), RIGHT);

        insertChild(createShape3D(new Point3d[]{
                new Point3d(upper.x, upper.y, lower.z),
                new Point3d(lower.x, upper.y, lower.z),
                new Point3d(lower.x, upper.y, upper.z),
                new Point3d(upper.x, upper.y, upper.z)}), TOP);

        insertChild(createShape3D(new Point3d[]{
                new Point3d(upper.x, lower.y, upper.z),
                new Point3d(lower.x, lower.y, upper.z),
                new Point3d(lower.x, lower.y, lower.z),
                new Point3d(upper.x, lower.y, lower.z),}), BOTTOM);

    }

    public Shape3D getFaceFromVector3d(final Vector3d vector) {
        for (int index = 0; index < NORMALS.length; index++) {
            if (NORMALS[index].equals(vector)) {
                return (Shape3D) getChild(index);
            }
        }
        return null;
    }

    public boolean setSelectedShape(final Vector3d vector) {
        vector.normalize();
        boolean ret = false;
        for (int index = 0; index < NORMALS.length; index++) {
            Appearance app = new AppearanceMeta();
            if (NORMALS[index].equals(vector)) {
                app.duplicateNodeComponent(SELECTED_APPEARANCE, true);
                ((Shape3D) getChild(index)).setAppearance(app);
                ret = true;
            } else {
                app.duplicateNodeComponent(ORDINARY_APPEARANCE, true);
                ((Shape3D) getChild(index)).setAppearance(app);
            }
        }
        return ret;
    }

    public boolean setSelectedShape(final Shape3D selectedShape) {
        boolean ret = false;
        for (int index = 0; index < numChildren(); index++) {
            final Node child = getChild(index);
            if (child instanceof Shape3D) {
                Appearance app = new AppearanceMeta();
                if (child == selectedShape) {
                    app.duplicateNodeComponent(SELECTED_APPEARANCE, true);
                    ((Shape3D) child).setAppearance(app);
                    ret = true;
                } else {
                    app.duplicateNodeComponent(ORDINARY_APPEARANCE, true);
                    ((Shape3D) child).setAppearance(app);
                }
            }
        }
        return ret;
    }

    public Vector3d getNormalFromShape3D(final Shape3D candidateShape) {
        for (int index = 0; index < numChildren(); index++) {
            if (getChild(index) == (candidateShape)) {
                return NORMALS[index];
            }
        }
        return null;
    }

    public Shape3D getSelectedShape() {
        for (int index = 0; index < numChildren(); index++) {
            final Node child = getChild(index);
            if (child instanceof Shape3D) {
                if (((Shape3D) child).getAppearance().getPolygonAttributes().
                        getPolygonMode() == PolygonAttributes.POLYGON_FILL) {
                    return (Shape3D) child;
                }
            }
        }
        return null;
    }

    public Vector3d getSelectedVector() {
        for (int index = 0; index < numChildren(); index++) {
            final Node child = getChild(index);
            if (child instanceof Shape3D) {
                if (((Shape3D) child).getAppearance().getPolygonAttributes().
                        getPolygonMode() == PolygonAttributes.POLYGON_FILL) {
                    return NORMALS[index];
                }
            }
        }
        return null;
    }

    private Shape3DMeta createShape3D(final Point3d[] coords) {

        final GeometryInfo gi = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
        gi.setCoordinates(coords);
        final NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gi);

        final Appearance app = new AppearanceMeta();
        app.duplicateNodeComponent(ORDINARY_APPEARANCE, true);
        final Shape3D ret = new Shape3D(gi.getGeometryArray(), app);
        ret.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
        ret.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        ret.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        ret.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        ret.setCapability(Shape3D.ENABLE_COLLISION_REPORTING);
        ret.setCapability(Shape3D.ENABLE_PICK_REPORTING);
        return new Shape3DMeta(ret);
    }

    private Shape3D getShape3D(final int index) {
        return (Shape3D) getChild(index);
    }

    public void setAllFaceAppearance(Appearance appearance) {
        for (int index = 0; index < numChildren(); index++) {
            final Node child = getChild(index);
            if (child instanceof Shape3D) {
                ((Shape3D) child).setAppearance(appearance);
            }
        }

    }

}
