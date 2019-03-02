package cs.designer.view.viewer;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;
import cs.designer.swing.icons.IconManager;
import cs.designer.swing.resources.ResourcesPath;
import cs.designer.utils.Canvas3DCamera;
import cs.designer.view.controller.DisplayControlable;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * @author rongyang
 */
public abstract class DisplayView extends JPanel {
    public final static BoundingSphere BOUNDS = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 500.0);
    public static final Color DEFAULT_BACK_COLOR= new Color(153, 153, 255);
    public static Cursor defaultCursor = Cursor.getDefaultCursor();
    protected Canvas3D viewCanvas;
    public TransformGroup controlTg;
    private BranchGroup objRoot;
    public TransformGroup viewTransformGroup;
    private Canvas3DCamera camera3D;

    public enum ViewType {

        DRAW_PLANVIEW, ORBIT_VIEW, VIRTUAL_VIEW
    }

    public enum OperateType {

        DRAW_PLAN, CREATE_ROOM, ADD_SUBSURFACE, MAKE_HOLE, ORBIT, DEFAULT, DELETE_OBJECT, MOVE_FURNITURE
    }

    private ViewType viewType = ViewType.DRAW_PLANVIEW;
    protected OperateType operateType = OperateType.DRAW_PLAN;

    public enum ContrType {

        ROTATER, ZOOMER, TRANSLATE
    }

    public SimpleUniverse viewScreenUniverse;
    public Viewer userViewer = null;
    private Color backGroundColor = DEFAULT_BACK_COLOR;
    private Background background;

    public DisplayView() {
        createSceen();
    }

    public abstract void setEnabled(ContrType contrType,
                                    boolean enabled);

    private void createSceen() {

        setLayout(new BorderLayout());
        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();
        viewCanvas = new Canvas3D(config);
        viewCanvas.setDoubleBufferEnable(true);
        add(viewCanvas);
        objRoot = new BranchGroup();
        background = new Background(new Color3f(backGroundColor));
        background.setCapability(Background.ALLOW_COLOR_READ);
        background.setCapability(Background.ALLOW_COLOR_WRITE);
        background.setCapability(Background.ALLOW_IMAGE_READ);
        background.setCapability(Background.ALLOW_IMAGE_WRITE);
        background.setCapability(Background.ALLOW_IMAGE_SCALE_MODE_READ);
        background.setCapability(Background.ALLOW_IMAGE_SCALE_MODE_WRITE);
        background.setApplicationBounds(BOUNDS);
        objRoot.addChild(background);

        //
        objRoot.setCapability(BranchGroup.ALLOW_DETACH);
        objRoot.setCapability(BranchGroup.ALLOW_PARENT_READ);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        objRoot.setCapability(BranchGroup.ENABLE_COLLISION_REPORTING);
        objRoot.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
        objRoot.setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
        //
        controlTg = new TransformGroup();
        controlTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        controlTg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        controlTg.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        controlTg.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        //
        createViewScreenUniverse();
    }

    public void displayScreen() {
        objRoot.addChild(controlTg);
        viewScreenUniverse.addBranchGraph(objRoot);
        camera3D = new Canvas3DCamera(SimpleUniverse.getPreferredConfiguration());
        getUserViewer().getView().addCanvas3D(camera3D);
    }

    public BranchGroup getObjRoot() {
        return objRoot;
    }

    private void createViewScreenUniverse() {
        final ViewingPlatform viewPlatform = new ViewingPlatform();
        viewPlatform.setCapability(Node.ALLOW_BOUNDS_WRITE);
        viewPlatform.setCapability(Node.ALLOW_BOUNDS_READ);
        viewPlatform.setBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));
        userViewer =
                new com.sun.j3d.utils.universe.Viewer(viewCanvas);
        userViewer.getView().setBackClipDistance(1000);
        userViewer.getView().setFrontClipDistance(0.1);
        viewScreenUniverse = new SimpleUniverse(viewPlatform, userViewer);
        viewScreenUniverse.getViewer().getView().setUserHeadToVworldEnable(true);
        viewScreenUniverse.getViewer().getView().setTrackingEnable(true);
        //
        ViewingPlatform viewingPlatform = viewScreenUniverse.getViewingPlatform();
        viewTransformGroup = viewingPlatform.getViewPlatformTransform();
        viewingPlatform.setNominalViewingTransform();

    }

    public abstract TransformGroup getControllerGroup();

    public abstract void setController(final DisplayControlable controller);

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
        switch (operateType) {
            case DEFAULT:
                getViewCanvas().setCursor(defaultCursor);
                break;
            case DRAW_PLAN:
//                getViewCanvas().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                getViewCanvas().setCursor(defaultCursor);
                break;
            case ORBIT:
                getViewCanvas().setCursor(new Cursor(Cursor.HAND_CURSOR));
                break;
            case ADD_SUBSURFACE:
                getViewCanvas().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                break;
            case DELETE_OBJECT:
                if (viewType == ViewType.DRAW_PLANVIEW) {
                    getViewCanvas().setCursor(createCustomCursor("delete-16-16.png", "delete-32-32.png", "delete", defaultCursor));
                }
                break;
            case CREATE_ROOM:
                getViewCanvas().setCursor(createCustomCursor("build-16-16.png", "build-32-32.png", "build", defaultCursor));
                break;
            default:
                break;
        }
    }

    public OperateType getOperateType() {
        return operateType;
    }

    protected void setViewType(final ViewType operateType) {
        this.viewType = operateType;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public SimpleUniverse getVieScreenUniverse() {
        return viewScreenUniverse;
    }

    public Canvas3D getViewCanvas() {
        return viewCanvas;
    }

    public void setBackGroupColor(final Color color) {
        background.setColor(new Color3f(color));
        background.setImage(null);
        this.backGroundColor=color;
    }

    public Color getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroupImage(final BufferedImage image) {
        background.setImage(new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, image));
        background.setImageScaleMode(Background.SCALE_FIT_ALL);
    }

    public void removeBackground() {
        background.setColor(new Color3f(Color.BLACK));
    }

    public void setModel(final Node node) {
        removeModel();
        final BranchGroup bg = new BranchGroup();
        bg.setCapability(BranchGroup.ALLOW_DETACH);
        bg.setCapability(Group.ALLOW_PICKABLE_READ);
        bg.setCapability(Group.ALLOW_PICKABLE_WRITE);
        bg.setCapability(Group.ENABLE_PICK_REPORTING);
        bg.addChild(node);
        controlTg.addChild(bg);
    }

    public void removeModel() {
        for (int index = 0; index < controlTg.numChildren(); index++) {
            if ((controlTg.getChild(index) instanceof BranchGroup)) {
                controlTg.removeChild((BranchGroup) controlTg.getChild(
                        index));
            }
        }
    }

    public TransformGroup getViewTransformGroup() {
        return viewTransformGroup;
    }

    public Viewer getUserViewer() {
        return userViewer;
    }

    protected Cursor createCustomCursor(final String smallCursorImageFileName,
                                        final String largeCursorImageFileName,
                                        String cursorName,
                                        Cursor defaultCursor) {
        if (GraphicsEnvironment.isHeadless()) {
            return defaultCursor;
        }
        Dimension cursorSize = getToolkit().getBestCursorSize(16, 16);
        URL cursorImageResource;
        if (cursorSize.width == 0) {
            return defaultCursor;
        } else {
            if (cursorSize.width > 16) {
                cursorImageResource = ResourcesPath.getResourcesUrl(largeCursorImageFileName);
            } else {
                cursorImageResource = ResourcesPath.getResourcesUrl(smallCursorImageFileName);
            }
            try {
                BufferedImage cursorImage = ImageIO.read(cursorImageResource);
                return getToolkit().createCustomCursor(cursorImage,
                        new Point(Math.round(cursorSize.width * 0.5f),
                                Math.round(cursorSize.height * 0.5f)),
                        cursorName);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Unknown resource " + cursorImageResource);
            }
        }
    }

    public Canvas3DCamera getCamera3D() {
        return camera3D;
    }

}
