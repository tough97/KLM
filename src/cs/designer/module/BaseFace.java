package cs.designer.module;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.CSTransformGroup;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.CSPersistException;
import com.klm.persist.impl.SurfaceMaterial;
import com.klm.persist.meta.BufferedImageMeta;
import com.klm.util.CSUtilException;
import cs.designer.swing.resources.ResourcesPath;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author rongyang
 */
public class BaseFace extends BranchGroup {

    public final static int DEFAULT_WIDTH = 50;
    public final static double DEFAULT_SURFACE_HEIGHT = -0.2f;
    public final static double LINE_DISTANCE_SURFACE = DEFAULT_SURFACE_HEIGHT + 0.05;
    private final static Color3f DESIGN_BG_COLOR = new Color3f(new Color(195, 194, 194));
    public final static Material DESIGN_BG_MATERIAL =
            new Material(DESIGN_BG_COLOR, DESIGN_BG_COLOR, DESIGN_BG_COLOR, DESIGN_BG_COLOR, 90);
    private Surface3D baseShape;
    private BranchGroup auxiliarylLneGroup;
    private boolean visible = true;
    private boolean lineVisible = true;
    private float width = DEFAULT_WIDTH;
    private Surface3D planViewSurface;
    private CSTransformGroup translationTG;
    private Color backColor = new Color(195, 194, 194);


    public BaseFace() {
        init();
        createShape();
    }

    public BaseFace(float width) {
        init();
        this.width = width;
        createShape();
    }

    private void init() {
        setCapability(BranchGroup.ALLOW_PARENT_READ);
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        //
        auxiliarylLneGroup = new BranchGroup();
        auxiliarylLneGroup.setCapability(BranchGroup.ALLOW_PARENT_READ);
        auxiliarylLneGroup.setCapability(BranchGroup.ALLOW_DETACH);
        auxiliarylLneGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        auxiliarylLneGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        auxiliarylLneGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        //
        translationTG = new CSTransformGroup();
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        //
        translationTG.setTranslation(new Vector3d(0, DEFAULT_SURFACE_HEIGHT, 0));
        //
        translationTG.addChild(auxiliarylLneGroup);
        addChild(translationTG);
        setPickable(false);

    }

    private void createShape() {
        Point3d[] coords = {new Point3d(width, 0, width),
                new Point3d(width, 0, -width),
                new Point3d(-width, 0, -width),
                new Point3d(-width, 0, width)

        };
        try {
            baseShape = new Surface3D(coords);
            baseShape.setSurfaceAppMaterial(DESIGN_BG_MATERIAL);
        } catch (CSHouseException e) {
            e.printStackTrace();
        }
        translationTG.addChild(baseShape);
        auxiliarylLneGroup.addChild(createline(100, 1));
    }


    public void displayLine(boolean visible) {
        if (lineVisible != visible) {
            if (visible) {
                translationTG.addChild(auxiliarylLneGroup);
            } else {
                auxiliarylLneGroup.detach();
            }
            this.lineVisible = visible;
        }
    }

    public void setVisible(boolean visible) {
        try {
            if (isVisible() != visible) {
                baseShape.setVisible(visible);
                this.visible = visible;
            }
        } catch (CSHouseException e) {
            e.printStackTrace();
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void addPlanview(double width, double height, BufferedImage planImage) {
        try {
            SurfaceMaterial subSurfaceMaterial = new SurfaceMaterial("planview");
            subSurfaceMaterial.setBufferedImage(planImage, width, height);
            Point3d[] coords = {new Point3d(-width / 2, height / 2, 0),
                    new Point3d(-width / 2, -height / 2, 0),
                    new Point3d(width / 2, -height / 2, 0),
                    new Point3d(width / 2, height / 2, 0)};
            planViewSurface = new Surface3D(coords);
            planViewSurface.setSurfaceMaterial(subSurfaceMaterial);
            final Color3f color = new Color3f(new Color(204, 204, 204));
            planViewSurface.setSurfaceAppMaterial(new Material(color, color, color, color, 60));
            Transform3D transform = new Transform3D();
            transform.set(new Vector3d(0.5f, 0.5f, 0));
            Transform3D rot = new Transform3D();
            rot.rotZ(-Math.PI);
            transform.mul(rot);
            planViewSurface.setSurfaceMaterialTransform(transform);
            baseShape.addSubSurface(planViewSurface);


        } catch (CSPersistException e) {
            e.printStackTrace();
        } catch (CSUtilException e) {
            e.printStackTrace();
        } catch (CSHouseException e) {
            e.printStackTrace();
        }

    }

    public void rest() {
        if (planViewSurface != null) {
            try {
                planViewSurface.detachFromParent();
            } catch (CSHouseException e) {
                e.printStackTrace();
            }
        }
        setBackColor(DESIGN_BG_COLOR.get());

    }

    private Shape3D createline(int noOfLines, double size) {
        final Shape3D shape = new Shape3D();
        double lineLength = noOfLines * size / 2;
        LineArray la = new LineArray(noOfLines * 4, LineArray.COORDINATES);
        int count = 0;
        for (int i = 0; i < noOfLines; i++) {
            la.setCoordinate(count, new Point3d(-lineLength, 0, i * size - lineLength));
            count++;
            la.setCoordinate(count, new Point3d(lineLength, 0, i * size - lineLength));
            count++;
        }
        for (int i = 0; i < noOfLines; i++) {
            la.setCoordinate(count, new Point3d(i * size - lineLength, 0, -lineLength));
            count++;
            la.setCoordinate(count, new Point3d(i * size - lineLength, 0, lineLength));
            count++;
        }
        shape.setGeometry(la);
        Appearance appearance = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(new Color3f(new Color(204, 204, 204)));
        appearance.setColoringAttributes(ca);
        LineAttributes sla = new LineAttributes();
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

    public Point3d getClickedPointOnSurface(MouseEvent event, Canvas3D canvas) {
        return getClickedPointOnSurface(event.getPoint(), canvas);
    }

    public Point3d getClickedPointOnSurface(final Point point, Canvas3D canvas) {
        Point3d eyePos = new Point3d();
        Point3d mousePos = new Point3d();
        canvas.getCenterEyeInImagePlate(eyePos);
        canvas.getPixelLocationInImagePlate((int) (point.getX() + 0.5),
                (int) (point.getY() + 0.5), mousePos);
        Transform3D transform = new Transform3D();
        canvas.getImagePlateToVworld(transform);
        transform.transform(eyePos);
        transform.transform(mousePos);
        Vector3d direction = new Vector3d(eyePos);
        direction.sub(mousePos);
        // three points on the plane
        Point3d p1 = new Point3d(-50, 0, 50);
        Point3d p2 = new Point3d(-50, 0, -50);
        Point3d p3 = new Point3d(50, 0, -50);
        Transform3D currentTransform = new Transform3D();
        baseShape.getLocalToVworld(currentTransform);
        currentTransform.transform(p1);
        currentTransform.transform(p2);
        currentTransform.transform(p3);
        Point3d intersection = getIntersection(eyePos, mousePos,
                p1, p2, p3);
        if (intersection != null) {
            currentTransform.invert();
            currentTransform.transform(intersection);
            return intersection;
        } else {
            return null;
        }
    }

    Point3d getIntersection(Point3d line1, Point3d line2,
                            Point3d plane1, Point3d plane2, Point3d plane3) {
        Vector3d p1 = new Vector3d(plane1);
        Vector3d p2 = new Vector3d(plane2);
        Vector3d p3 = new Vector3d(plane3);
        Vector3d p2minusp1 = new Vector3d(p2);
        p2minusp1.sub(p1);
        Vector3d p3minusp1 = new Vector3d(p3);
        p3minusp1.sub(p1);
        Vector3d normalPoint = new Vector3d();
        normalPoint.cross(p2minusp1, p3minusp1);
        // The plane can be defined by p1, n + d = 0
        double d = -p1.dot(normalPoint);
        Vector3d i1 = new Vector3d(line1);
        Vector3d direction = new Vector3d(line1);
        direction.sub(line2);
        double dot = direction.dot(normalPoint);
        if (dot == 0) {
            return null;
        }
        double t = (-d - i1.dot(normalPoint)) / (dot);
        Vector3d intersection = new Vector3d(line1);
        Vector3d scaledDirection = new Vector3d(direction);
        scaledDirection.scale(t);
        intersection.add(scaledDirection);
        Point3d intersectionPoint = new Point3d(intersection);
        return intersectionPoint;
    }

    public void setBaseShapeMaterial(final SurfaceMaterial material) {
        this.baseShape.setSurfaceMaterial(material);

    }

    public void setBackColor(final Color color) {
        final Color3f color3f = new Color3f(color);
        this.backColor = color;
        this.baseShape.setSurfaceAppMaterial(new Material(color3f, color3f, color3f, color3f, 90));
    }


    public void setBaseShapeAppMaterial(final Material material) {
        this.baseShape.setSurfaceAppMaterial(material);
    }

    public Color getBackColor() {
        return backColor;
    }
}
