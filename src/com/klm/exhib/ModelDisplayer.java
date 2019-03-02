/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.exhib;

import com.klm.util.CSUtilException;
import com.klm.util.impl.CSModelUtil;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gang-liu
 */
public class ModelDisplayer extends JPanel implements MouseListener,
        MouseWheelListener, MouseMotionListener, ActionListener {

    public static final String OPEN_LOCAL_MODEL = "OLM";
    public static final int OPEN_LOCAL_MODEL_INDEX = 0;
    public static final String OPEN_REMOTE_MODEL = "ORM";
    public static final int OPEN_REMOTE_MODEL_INDEX = 1;
    public static final String EDIT_SURFACE_TEXTURE = "EST";
    public static final int EDIT_SURFACE_TEXTURE_INDEX = 2;
    public static final String ABOUT_US = "AU";
    public static final int ABOUT_US_INDEX = 3;
    public static final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().
            createCustomCursor(new BufferedImage(16, 16,
                    BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
    public static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();
    public static final String ROOT_FLAG = "RF";
    public static final String LIGHT_FLAG = "LI";
    public static final Transform3D DEFAULT_V_TRANS = new Transform3D(
            new double[]{
                    1.0, 0.0, 0.0, 0.0,
                    0.0, 1.0, 0.0, 0.0,
                    0.0, 0.0, 1.0, 2.14,
                    0.0, 0.0, 0.0, 1.0});
    public static final Transform3D MOVE_FORWARD = new Transform3D(
            new double[]{
                    1.0, 0.0, 0.0, 0.0,
                    0.0, 1.0, 0.0, 0.0,
                    0.0, 0.0, 1.0, -0.2,
                    0.0, 0.0, 0.0, 1.0});
    public static final Transform3D MOVE_BACKWARD = new Transform3D(
            new double[]{
                    1.0, 0.0, 0.0, 0.0,
                    0.0, 1.0, 0.0, 0.0,
                    0.0, 0.0, 1.0, 0.2,
                    0.0, 0.0, 0.0, 1.0});
    public static final Transform3D MOVE_LEFT = new Transform3D(
            new double[]{
                    1.0, 0.0, 0.0, -0.2,
                    0.0, 1.0, 0.0, 0.0,
                    0.0, 0.0, 1.0, 0.0,
                    0.0, 0.0, 0.0, 1.0});
    public static final Transform3D MOVE_RIGHT = new Transform3D(
            new double[]{
                    1.0, 0.0, 0.0, 0.2,
                    0.0, 1.0, 0.0, 0.0,
                    0.0, 0.0, 1.0, 0.2,
                    0.0, 0.0, 0.0, 1.0});
    public static final Transform3D ROTATION_X_POS = new Transform3D();
    public static final Transform3D ROTATION_X_NEGA = new Transform3D();
    public static final Transform3D ROTATION_Y_POS = new Transform3D();
    public static final Transform3D ROTATION_Y_NEGA = new Transform3D();
    public static final Transform3D ROTATION_Z_POS = new Transform3D();
    public static final Transform3D ROTATION_Z_NEGA = new Transform3D();
    static{
        ROTATION_X_NEGA.rotX(-1 * Math.PI/10);
        ROTATION_X_POS.rotX(Math.PI/10);
        ROTATION_Y_NEGA.rotY(-1 * Math.PI/10);
        ROTATION_Y_POS.rotY(Math.PI/10);
        ROTATION_Z_NEGA.rotZ(-1 * Math.PI/10);
        ROTATION_Z_POS.rotZ(Math.PI/10);
    }
    
    public static final Point3d ORIGIN_D = new Point3d(0.0, 0.0, 0.0);
    public static final Point3f ORIGIN_F = new Point3f(0.0f, 0.0f, 0.0f);
    public static final Bounds IDENTICAL_BOUND = new BoundingSphere(ORIGIN_D,
            1.0);
    public static final double DEFAULT_SCALE_RATE = 0.8;
    public static final double DEFAULT_MODEL_RANGE = 0.8;
    public static final double MINIMUN_VIEWER_DISTANCE = DEFAULT_MODEL_RANGE +
            0.2;
    public static final double MAXIMUM__VIEWER_DISTANCE =
            MINIMUN_VIEWER_DISTANCE + 15.0;
    public static final Point3f DEFAULT_ATTENUUATION = new Point3f(0.1f, 0.1f,
            0.0f);
    public static final float DEFAULT_ANGLE = (float) (Math.PI / 5);
    protected PickCanvas pickCanvas;
    protected BranchGroup modelContainer;
    protected BranchGroup root;
    protected TransformGroup rotTrans;
    protected TransformGroup transTrans;
    protected TransformGroup viewTrans;
    protected TransformGroup exhibTrans;
    protected MouseEvent onPressMouseListener = null;
    protected Behavior mouseRotator;
    protected Behavior mouseTranslate;
    protected Robot robot;
    protected SimpleUniverse su;
    protected Background background;
    protected Canvas3D canvas;
    protected Transform3D initViewTrans = new Transform3D();
    protected JPopupMenu rightBMenu = new JPopupMenu();
    protected JMenuItem[] menuItems = new JMenuItem[4];

    public ModelDisplayer() throws AWTException {
        super();
        init();
        viewTrans.setTransform(DEFAULT_V_TRANS);
        initViewTrans = DEFAULT_V_TRANS;
    }

    public ModelDisplayer(final Transform3D initVTrans) throws AWTException {
        super();
        init();
        viewTrans.setTransform(initVTrans);
        initViewTrans = initVTrans;
    }

    public void removeModel() {
        for (int index = 0; index < modelContainer.numChildren(); index++) {
            final Node child = modelContainer.getChild(index);
            if (child instanceof BranchGroup) {
                modelContainer.removeChild(child);
            }
        }
    }

    public void setModel(final Node node, final boolean resize) {
        removeModel();
        if (resize) {
            exhibTrans.setTransform(getIdentityTransforms(node));
        }
        final BranchGroup bg = new BranchGroup();
        bg.setCapability(BranchGroup.ALLOW_DETACH);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        bg.addChild(node);
        modelContainer.addChild(bg);
    }

    public void setModel(final Node node) {
        setModel(node, true);
    }

    public boolean hasMode() {
        return modelContainer.numChildren() != 0;
    }

    public boolean isBlank() {
        for (int index = 0; index < modelContainer.numChildren(); index++) {
            if (modelContainer.getChild(index) instanceof Group) {
                return false;
            }
        }
        return true;
    }

    public void resetRotation() {
        rotTrans.setTransform(new Transform3D());
    }

    public void resetTranslation() {
        transTrans.setTransform(new Transform3D());
    }

    public void resetTransforms() {
        resetRotation();
        resetTranslation();
        viewTrans.setTransform(initViewTrans);
    }

    public void setBackGroundColor(final Color color) {
        background.setColor(new Color3f(color));
    }

    public void addLight(final Light light, final Group targetGroup) {
        final LightNode lightNode = new LightNode(light);
        targetGroup.addChild(lightNode);
    }

    public void addLights(final Light[] lights, final Group targetGroup) {
        final LightNode lightNode = new LightNode(lights);
        targetGroup.addChild(lightNode);
    }

    public void removeLights(final Group targetGroup) {
        for (int index = 0; index < targetGroup.numChildren(); index++) {
            final Node child = targetGroup.getChild(index);
            if (child instanceof LightNode) {
                targetGroup.removeChild(child);
            }
        }
    }

    public Set<Light> getAllLights(final Group targetGroup) {
        final Set<Light> ret = new HashSet<Light>();
        for (int index = 0; index < targetGroup.numChildren(); index++) {
            final Node child = targetGroup.getChild(index);
            if (child instanceof LightNode) {
                ret.addAll(((LightNode) child).getAllLights());
            }
        }
        return ret;
    }

    public void turnOffAllLights(final Group targetGroup) {
        for (int index = 0; index < targetGroup.numChildren(); index++) {
            final Node child = targetGroup.getChild(index);
            if (child instanceof LightNode) {
                ((LightNode) child).turnOffLights();
            }
        }
    }

    public void turnOnAllLights(final Group targetGroup) {
        for (int index = 0; index < targetGroup.numChildren(); index++) {
            final Node child = targetGroup.getChild(index);
            if (child instanceof LightNode) {
                ((LightNode) child).turnOnLights();
            }
        }
    }

    public void addLight(final Light light) {
        addLight(light, root);
    }

    public void addLights(final Light[] lights) {
        addLights(lights, root);
    }

    public void removeLights() {
        removeLights(root);
    }

    public Set<Light> getAllLights() {
        return getAllLights(root);
    }

    public void turnOffAllLights() {
        turnOffAllLights(root);
    }

    public void turnOnAllLights() {
        turnOnAllLights(root);
    }

    public void stepForward() {
        final Transform3D trans = new Transform3D();
        viewTrans.getTransform(trans);
        trans.mul(MOVE_FORWARD);
        final Point3d test = new Point3d(0.0, 0.0, 0.0);
        trans.transform(test);
        if (test.distance(ORIGIN_D) > MINIMUN_VIEWER_DISTANCE) {
            viewTrans.setTransform(trans);
        }
    }

    public void stepBackward() {
        final Transform3D trans = new Transform3D();
        viewTrans.getTransform(trans);
        trans.mul(MOVE_BACKWARD);
        final Point3d test = new Point3d(0.0, 0.0, 0.0);
        trans.transform(test);
        if (test.distance(ORIGIN_D) < MAXIMUM__VIEWER_DISTANCE) {
            viewTrans.setTransform(trans);
        }
    }

    /*
     * Package Level Methods
     */
    Transform3D getExibTransform3D() {
        final Transform3D trans = new Transform3D();
        exhibTrans.getTransform(trans);
        return trans;
    }

    /*
     * Private Methods..........................................................
     */
    private void init() throws AWTException {
        menuItems[OPEN_LOCAL_MODEL_INDEX] = new JMenuItem("打开本地模型");
        menuItems[OPEN_LOCAL_MODEL_INDEX].addActionListener(this);
        menuItems[OPEN_LOCAL_MODEL_INDEX].setActionCommand(OPEN_LOCAL_MODEL);
        rightBMenu.add(menuItems[OPEN_LOCAL_MODEL_INDEX]);

        menuItems[OPEN_REMOTE_MODEL_INDEX] = new JMenuItem("打开远程模型");
        menuItems[OPEN_REMOTE_MODEL_INDEX].setEnabled(false);
        menuItems[OPEN_REMOTE_MODEL_INDEX].setActionCommand(OPEN_REMOTE_MODEL);
        rightBMenu.add(menuItems[OPEN_REMOTE_MODEL_INDEX]);

        menuItems[EDIT_SURFACE_TEXTURE_INDEX] = new JMenuItem("修改表面");
        menuItems[EDIT_SURFACE_TEXTURE_INDEX].setVisible(false);
        menuItems[EDIT_SURFACE_TEXTURE_INDEX].setActionCommand(
                EDIT_SURFACE_TEXTURE);
        rightBMenu.add(menuItems[EDIT_SURFACE_TEXTURE_INDEX]);

        menuItems[ABOUT_US_INDEX] = new JMenuItem("关于我们");
        menuItems[ABOUT_US_INDEX].addActionListener(this);
        menuItems[ABOUT_US_INDEX].setActionCommand(ABOUT_US);
        rightBMenu.add(menuItems[ABOUT_US_INDEX]);

        modelContainer = new BranchGroup();
        modelContainer.setCapability(BranchGroup.ALLOW_DETACH);
        modelContainer.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        modelContainer.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        modelContainer.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        modelContainer.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
        modelContainer.setCapability(Node.ENABLE_PICK_REPORTING);
        modelContainer.setUserData(ROOT_FLAG);

        rotTrans = new TransformGroup();
        rotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        rotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        exhibTrans = new TransformGroup();
        exhibTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        exhibTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        transTrans = new TransformGroup();
        transTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        transTrans.addChild(rotTrans);
        rotTrans.addChild(exhibTrans);
        exhibTrans.addChild(modelContainer);
        root = new BranchGroup();
        root.setCapability(BranchGroup.ALLOW_DETACH);
        root.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        root.addChild(transTrans);

        final BoundingSphere bs = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                10000.0);
        mouseRotator = new MouseRotate(rotTrans);
        mouseRotator.setSchedulingBounds(bs);
        rotTrans.addChild(mouseRotator);
        mouseTranslate = new MouseTranslate(transTrans);
        mouseTranslate.setSchedulingBounds(bs);
        rotTrans.addChild(mouseTranslate);

        background = new Background(new Color3f(Color.WHITE));
        background.setApplicationBounds(bs);
        background.setCapability(Background.ALLOW_COLOR_READ);
        background.setCapability(Background.ALLOW_COLOR_WRITE);
        root.addChild(background);

        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setDoubleBufferEnable(canvas.getDoubleBufferAvailable());
        su = new SimpleUniverse(canvas);
        viewTrans = su.getViewingPlatform().getViewPlatformTransform();
        su.getViewingPlatform().getViewers()[0].getView().setWindowResizePolicy(
                View.VIRTUAL_WORLD);

        pickCanvas = new PickCanvas(canvas, modelContainer);
        pickCanvas.setMode(PickCanvas.GEOMETRY);

        su.addBranchGraph(root);
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        robot = new Robot();
    }

    private Transform3D getIdentityTransforms(final Node node) {
        final Point3f lower = new Point3f();
        final Point3f upper = new Point3f();
        CSModelUtil.parseNodeBoundaries(node, lower, upper);
        Point3f center = CSModelUtil.getCenter(lower, upper);

        Point3f lowerTemp = new Point3f(lower);
        Point3f upperTemp = new Point3f(upper);
        Point3f centerTemp = new Point3f(center);

        final Transform3D scale = new Transform3D();
        while (lowerTemp.distance(centerTemp) > DEFAULT_MODEL_RANGE &&
                upperTemp.distance(centerTemp) > DEFAULT_MODEL_RANGE) {
            final Transform3D adjustScale = new Transform3D();
            adjustScale.setScale(DEFAULT_SCALE_RATE);
            scale.mul(adjustScale);
            lowerTemp = new Point3f(lower);
            upperTemp = new Point3f(upper);
            centerTemp = new Point3f(center);
            scale.transform(centerTemp);
            scale.transform(upperTemp);
            scale.transform(lowerTemp);
        }

        final Transform3D translation = new Transform3D();
        center.negate();
        translation.setTranslation(new Vector3f(center));
        scale.mul(translation);

        scale.transform(upper);
        scale.transform(lower);

        return scale;
    }

    /*
     * Implemented Methods......................................................
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                final Point3d p3d = new Point3d();
                canvas.getPixelLocationInImagePlate(e.getX(), e.getY(), p3d);
                break;
            case MouseEvent.BUTTON2:
                resetTransforms();
                break;
            case MouseEvent.BUTTON3:
                rightBMenu.show(e.getComponent(), e.getX(), e.getY());
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pickCanvas.setShapeLocation(e);
        final PickResult result = pickCanvas.pickClosest();
        if (result != null) {
            switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    mouseRotator.setEnable(true);
                    onPressMouseListener = e;
                    setCursor(BLANK_CURSOR);
                    break;
                case MouseEvent.BUTTON3:
                    mouseTranslate.setEnable(true);
                    ((MouseTranslate) mouseTranslate).setFactor(0.006);
                    break;
            }
        } else {
            mouseRotator.setEnable(false);
            mouseTranslate.setEnable(false);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseRotator.setEnable(true);
        mouseTranslate.setEnable(true);
        if (onPressMouseListener != null) {
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (onPressMouseListener != null) {
                final Point point = this.getLocationOnScreen();
                robot.mouseMove(onPressMouseListener.getX() + point.x,
                        onPressMouseListener.getY() + point.y);
                onPressMouseListener = null;
                setCursor(DEFAULT_CURSOR);
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!mouseTranslate.getEnable()) {
            mouseTranslate.setEnable(true);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (mouseTranslate.getEnable() && e.getButton() == MouseEvent.BUTTON3) {
            mouseTranslate.setEnable(false);
            transTrans.setTransform(new Transform3D());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            stepForward();
        } else if (e.getWheelRotation() > 0) {
            stepBackward();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseTranslate.getEnable()) {
            final Point3d center = new Point3d(ORIGIN_D);
            final Transform3D trans = new Transform3D();
            transTrans.getTransform(trans);
            trans.transform(center);
        }
    }

    public static Light createAmbientLight(final Color color) {
        final Light result = new AmbientLight(true, new Color3f(color));
        result.setCapability(Light.ALLOW_COLOR_READ);
        result.setCapability(Light.ALLOW_COLOR_WRITE);
        result.setCapability(Light.ALLOW_STATE_READ);
        result.setCapability(Light.ALLOW_STATE_WRITE);
        result.setInfluencingBounds(new BoundingSphere(
                new Point3d(0.0, 0.0, 0.0), 100.0));
        return result;
    }

    public static Light createSpotLight(final Color color,
                                        final Point3f position, final Point3f attenuation,
                                        final Vector3f direction, float spreadAngle, float concentration) {
        final Light result = new SpotLight(new Color3f(color), position,
                attenuation, direction, spreadAngle, concentration);
        result.setInfluencingBounds(new BoundingSphere(
                new Point3d(0.0, 0.0, 0.0), 100.0));
        return result;
    }

    public static Vector3f getDirection(final Point3f from, final Point3f to) {
        final Vector3f v = new Vector3f(from.x - to.x, from.y - to.y, from.z -
                to.z);
        v.normalize();
        return v;
    }

    public static Vector3d createDirection(final Point3d from, final Point3d to) {
        final Vector3d v = new Vector3d(from.x - to.x, from.y - to.y, from.z -
                to.z);
        v.normalize();
        return v;
    }

    public static Light[] createExhibitionLights(final Color color) {
        final Light[] lights = new Light[6];
        lights[0] = createAmbientLight(color);
        float x = -1.2f;
        Point3f position = new Point3f(0f, 2f, 0f);
        lights[1] = ModelDisplayer.createSpotLight(color, position,
                ModelDisplayer.DEFAULT_ATTENUUATION,
                ModelDisplayer.getDirection(position, ORIGIN_F),
                ModelDisplayer.DEFAULT_ANGLE, 0f);
        position = new Point3f(-1f, 2f, -1f);
        lights[2] = ModelDisplayer.createSpotLight(color, position,
                ModelDisplayer.DEFAULT_ATTENUUATION,
                ModelDisplayer.getDirection(position, ORIGIN_F),
                ModelDisplayer.DEFAULT_ANGLE, 0f);
        position = new Point3f(1f, 2f, -1f);
        lights[3] = ModelDisplayer.createSpotLight(color, position,
                ModelDisplayer.DEFAULT_ATTENUUATION,
                ModelDisplayer.getDirection(position, ORIGIN_F),
                ModelDisplayer.DEFAULT_ANGLE, 0f);
        position = new Point3f(1f, 2f, 1f);
        lights[4] = ModelDisplayer.createSpotLight(color, position,
                ModelDisplayer.DEFAULT_ATTENUUATION,
                ModelDisplayer.getDirection(position, ORIGIN_F),
                ModelDisplayer.DEFAULT_ANGLE, 0f);
        position = new Point3f(-1f, 2f, 1f);
        lights[5] = ModelDisplayer.createSpotLight(color, position,
                ModelDisplayer.DEFAULT_ATTENUUATION,
                ModelDisplayer.getDirection(position, ORIGIN_F),
                ModelDisplayer.DEFAULT_ANGLE, 0f);
        return lights;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();
        if (source instanceof JMenuItem) {
            final JMenuItem item = (JMenuItem) source;
            final String command = item.getActionCommand();
            if (command.equals(OPEN_LOCAL_MODEL)) {
            } else if (command.equals(OPEN_REMOTE_MODEL)) {
            } else if (command.equals(ABOUT_US)) {
            }
        }
    }

    public static void main(String[] args) throws AWTException, com.klm.cons.impl.CSHouseException,
            IOException, CSUtilException, Exception {
        final JFrame f = new JFrame();
        f.setSize(400, 400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final ModelDisplayer md = new ModelDisplayer();
        f.getContentPane().add(md);

        final BranchGroup bg = new BranchGroup();
        final Font3D font = new Font3D(new Font("Helvetica", Font.BOLD, 1),
                new FontExtrusion());
        final Text3D text = new Text3D(font, new String("天地不仁,万物刍狗"),
                new Point3f(-2.0f, 0.0f, 0.0f));
//        bg.addChild(createTestingScene());
        bg.addChild(new Shape3D(text));
        md.setModel(bg, false);
        md.setBackGroundColor(Color.BLACK);

        f.setVisible(true);
        md.addLights(new Light[]{
                ModelDisplayer.createAmbientLight(Color.WHITE),
                ModelDisplayer.createSpotLight(Color.ORANGE, new Point3f(
                        0.0f, 10.0f, 0.0f), new Point3f(.1f, .1f, 0.0f),
                        new Vector3f(0.0f, -1.0f, 0.0f),
                        (float) Math.PI / 10, 0.0f)});
        md.setBackGroundColor(Color.ORANGE);
    }

    private static BranchGroup createTestingScene() {
        final BranchGroup root = new BranchGroup();
        final TransformGroup tg = new TransformGroup();
        final Transform3D trans = new Transform3D();
        final Transform3D rot = new Transform3D();
        trans.setTranslation(new Vector3d(0.6, 0.0, 0.0));
        rot.rotY(Math.PI / 4);
        final Transform3D combi = new Transform3D();
        combi.mul(trans, rot);
        trans.invert();
        System.out.println(trans);
        combi.mul(trans);

        root.addChild(tg);
        tg.addChild(new ColorCube(0.2));
        tg.setTransform(combi);

        root.addChild(createAxies(5.0));

        return root;
    }

    public static BranchGroup createAxies(final double length) {
        final BranchGroup root = new BranchGroup();
        final Shape3D shape = new Shape3D();
        final GeometryArray ga = new LineArray(6, LineArray.COORDINATES);
        ga.setCoordinates(0, new Point3d[]{new Point3d(-1 * length, 0.0, 0.0),
                new Point3d(length, 0.0, 0.0),
                new Point3d(0.0, -1 * length, 0.0), new Point3d(0.0, length,
                0.0), new Point3d(0.0, 0.0, -1 * length), new Point3d(0.0,
                0.0, length)});
        shape.setGeometry(ga);
        root.addChild(shape);
        return root;
    }
}
