/**
 * Copyright (c) 2011 co-soft. All Rights Reserved.
 */
package cs.designer.view.viewer;

import com.klm.cons.impl.*;
import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.util.CSUtilException;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import cs.designer.module.Pan;
import cs.designer.module.TempWall;
import cs.designer.swing.ActionCommandKey;
import cs.designer.swing.ControlResponser;
import cs.designer.swing.ToolbarPanel;
import cs.designer.swing.bean.HouseBean;
import cs.designer.swing.property.PropertyPanel;
import cs.designer.swing.tool.*;
import cs.designer.swing.ui.MerchandiseInfoDialog;
import cs.designer.swing.undo.*;
import cs.designer.view.controller.*;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.util.*;

/**
 * Unit used to draw and display the 3D scene
 *
 * @author rongyang
 * @version Oct-18-2011 1.0.0.0
 */
public class HousePlanView extends DisplayView implements ActionListener {
    private JPopupMenu rightButtonMenu;
    private JMenuItem[] menuItems;
    private int[] canceledMenuItemGroup = new int[]{0, 1};
    private int[] surfaceMenuItemGroup = new int[]{2, 4, 5};
    private int[] furnitureMenuItemGroup = new int[]{2, 3, 5};
    private PropertyPanel propertyPanel;
    private PlanControler planControler;
    private ModelControler modelControler;
    private VirtualViewController virtualViewController;
    private SubSurfaceControler surfaceControler;
    private ViewControler planViewControler;
    private ControlerManager controlerManager;
    private int mouseButton;
    private RoomGenerator roomGenerator;
    private Canvas3D miniMap;
    private House currentHouse;
    private Floor currentFloor;
    private HouseBean houseBean;
    private AbstractPickUtil pickTool;
    private ToolbarPanel toolPanl;
    private ControlResponser controlResponser;


    public HousePlanView(PropertyPanel propertyPanel) {
        super();
        this.propertyPanel = propertyPanel;
        controlerManager = new ControlerManager();
        rightButtonMenu = new JPopupMenu();
        final JMenuItem canceledMenuItem = new JMenuItem("取消");
        final JMenuItem continueMenuItem = new JMenuItem("继续");
        final JMenuItem deleteObjectMenuItem = new JMenuItem("删除");
        final JMenuItem installMenuItem = new JMenuItem("安装");
        final JMenuItem detailedInfoMenuItem = new JMenuItem("商品信息");
        final JMenuItem rotationMenuItem = new JMenuItem("旋转");
        menuItems = new JMenuItem[]{canceledMenuItem, continueMenuItem, deleteObjectMenuItem,
                installMenuItem, rotationMenuItem, detailedInfoMenuItem};
        continueMenuItem.setActionCommand(ActionCommandKey.CONTINUE);
        canceledMenuItem.setActionCommand(ActionCommandKey.CANCEL);
        deleteObjectMenuItem.setActionCommand(ActionCommandKey.DELETE_OBJECT);
        installMenuItem.setActionCommand(ActionCommandKey.INSTALLATION);
        detailedInfoMenuItem.setActionCommand(ActionCommandKey.DETAILED_INFO);
        rotationMenuItem.setActionCommand(ActionCommandKey.ROTATION_SURFACE_MATERIAL);
        deleteObjectMenuItem.addActionListener(this);
        installMenuItem.addActionListener(this);
        detailedInfoMenuItem.addActionListener(this);
        canceledMenuItem.addActionListener(this);
        continueMenuItem.addActionListener(this);
        rotationMenuItem.addActionListener(this);
        rightButtonMenu.setSize(40, 40);
        rightButtonMenu.setLightWeightPopupEnabled(false);
        createCanvas();
        createViewScreenUniverse();
        addCanvas();
        //
        this.currentHouse = new House();
        currentFloor = new Floor();
        currentFloor.setName("1th");
        this.currentHouse.addFloor("1th", currentFloor);
        //
        planControler = new PlanControler(this, propertyPanel);
        planControler.registerController(controlTg, false);
        modelControler = new ModelControler(this);
        modelControler.registerController(controlTg, false);
        virtualViewController = new VirtualViewController(this);
        virtualViewController.registerController(viewScreenUniverse.getViewingPlatform().
                getViewPlatformTransform(), true);
        surfaceControler = new SubSurfaceControler(this);
        surfaceControler.registerController(controlTg, false);
        planViewControler = new ViewControler(this, propertyPanel);
        planViewControler.registerController(controlTg, false);

        this.propertyPanel.setModifyControler(surfaceControler);
        this.propertyPanel.setModifyControler(planControler);
        this.propertyPanel.setModifyControler(planViewControler);
        //
        this.pickTool = new PickUtil(this);
        displayScreen();
        setViewType(ViewType.DRAW_PLANVIEW);
        miniMap.setVisible(false);
        setModel(this.currentHouse);
        final KeyboardFocusManager manager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyboardControlerManager());
        setOperateType(OperateType.DRAW_PLAN);

    }

    private void addCanvas() {
        controlResponser = new ControlResponser(this);
        toolPanl = new ToolbarPanel(controlResponser);
        miniMap = new MiniMap(userViewer, viewScreenUniverse);
        miniMap.addMouseListener(controlerManager);
        miniMap.addMouseMotionListener(controlerManager);
        miniMap.addMouseWheelListener(controlerManager);
        SpringLayout layout = new SpringLayout();
        layout.putConstraint(SpringLayout.NORTH, viewCanvas, 45,
                SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, viewCanvas, 0,
                SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, viewCanvas, 0,
                SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.EAST, viewCanvas, 0,
                SpringLayout.EAST, this);

        layout.putConstraint(SpringLayout.NORTH, miniMap, 45,
                SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, miniMap, -200,
                SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.SOUTH, miniMap, 200,
                SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, miniMap, 0,
                SpringLayout.EAST, this);
        //
        layout.putConstraint(SpringLayout.NORTH, toolPanl, 0,
                SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, toolPanl, 0,
                SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.EAST, toolPanl, 0,
                SpringLayout.EAST, this);
        setLayout(layout);
        add(toolPanl);
        add(miniMap);
        add(viewCanvas);

    }

    private void createCanvas() {
        GraphicsConfiguration configuration =
                SimpleUniverse.getPreferredConfiguration();
        viewCanvas = new Canvas3D(configuration);
        viewCanvas.addMouseListener(controlerManager);
        viewCanvas.addMouseMotionListener(controlerManager);
        viewCanvas.addMouseWheelListener(controlerManager);
        viewCanvas.setDoubleBufferEnable(true);
        //
    }

    private void createViewScreenUniverse() {
        final ViewingPlatform viewPlatform = new ViewingPlatform();
        viewPlatform.setCapability(Node.ALLOW_BOUNDS_WRITE);
        viewPlatform.setCapability(Node.ALLOW_BOUNDS_READ);
        viewPlatform.setBounds(new BoundingSphere(new Point3d(0, 0, 0), 500));
        userViewer =
                new com.sun.j3d.utils.universe.Viewer(viewCanvas);
        userViewer.getView().setFrontClipDistance(0.1);
        userViewer.getView().setFieldOfView(0.05);
        userViewer.getView().setBackClipDistance(500);
        viewScreenUniverse = new SimpleUniverse(viewPlatform, userViewer);
        viewScreenUniverse.getViewer().getView().setUserHeadToVworldEnable(true);
        viewScreenUniverse.getViewer().getView().setTrackingEnable(true);
        //
        ViewingPlatform viewingPlatform = viewScreenUniverse.getViewingPlatform();
        viewTransformGroup = viewingPlatform.getViewPlatformTransform();
        viewingPlatform.setNominalViewingTransform();

    }

    public void addDropTargetListener(DropTargetListener dropTargetListener) {
        new DropTarget(this, dropTargetListener);
    }

    public void clear() {
        currentHouse.removeFloor(currentFloor.getName());
        currentFloor = null;
        currentFloor = new Floor();
        currentFloor.setName("1th");
        this.currentHouse.addFloor("1th", currentFloor);
        planControler.reset();
        surfaceControler.reset();
        virtualViewController.reset();
        HouseEdit.getHouseEditor().reset();
        TempWall.clearTempWalls();
        Merchandise.merchandises.clear();
        Wall.cleanSystemWallLookUp();
        Pan.getPan().reset();
        RoomGenerator.roomCount = 1;
        planViewControler.drawPlanView();
        setOperateType(DisplayView.OperateType.DRAW_PLAN);
        planControler.getBaseFace().rest();
        houseBean = new HouseBean();
        setBackGroupColor(DEFAULT_BACK_COLOR);
        System.gc();

    }

    public Node getSelectObject(MouseEvent me) {
        Node selectObject = null;
        Node pickObject = me == null ? pickTool.getPickResult() :
                pickTool.getPickResult(me.getX(), me.getY());
        if (getViewType() == ViewType.DRAW_PLANVIEW) {
            if (pickObject instanceof Surface3D) {
                pickObject = pickTool.getPickResult(Wall.class);
                if (pickObject instanceof Wall) {
                    selectObject = pickObject;

                } else {
                    pickObject = pickTool.getPickResult(Room.class);
                    if (pickObject instanceof Room) {
                        selectObject = pickObject;
                    }
                }
            } else if (pickObject instanceof Furniture) {
                selectObject = pickObject;
            }

        } else if (getViewType() == ViewType.ORBIT_VIEW
                || getViewType() == ViewType.VIRTUAL_VIEW) {
            if (pickObject instanceof Surface3D) {
                Surface3D pickSurface = (Surface3D) pickObject;
                selectObject = pickObject;
            } else if (pickObject instanceof Furniture) {
                selectObject = pickObject;
                setOperateType(OperateType.MOVE_FURNITURE);
            }

        }
        return selectObject;
    }

    public void deleteSelectObject(MouseEvent me) {
        Node selectObject = getSelectObject(me);
        if (selectObject instanceof Furniture) {
            Furniture pickFurniture = (Furniture) selectObject;
            HouseEdit.getHouseEditor().joinObject(new FurnitureDeUndoEditor(pickFurniture));
            if (pickFurniture.isInstalled()) {
                try {
                    pickFurniture.unInstall();
                } catch (CSHouseException e) {
                    e.printStackTrace();
                }
            }
            pickFurniture.detachFromParent();
        } else if (selectObject instanceof Wall) {
            final Wall pickWall = (Wall) selectObject;
            currentFloor.removeWall(pickWall);
            HouseEdit.getHouseEditor().
                    joinObject(new WalDeUndoEditor(TempWall.walls.get(pickWall.getWallShape())));
            TempWall.walls.get(pickWall.getWallShape()).removeFromParent(true);
        } else if (selectObject instanceof Room) {
            final Room deleteRoom = (Room) selectObject;
            HouseEdit.getHouseEditor().joinObject(new RoomDeUndoEditor(currentFloor, deleteRoom));
            currentFloor.removeRoom(deleteRoom, true);
        } else if (selectObject instanceof Surface3D) {
            try {
                Surface3D selectSurface = (Surface3D) selectObject;
                if (((Surface3D) selectObject).isConnectiveSurface()) {
                    Surface3D parent = (Surface3D) selectSurface.getFirstParentOf(Surface3D.class);
                    if (parent != null) {
                        Wall wall = (Wall) parent.getFirstParentOf(Wall.class);
                        if (wall != null &&
                                wall.getOppositionSurface(parent) != null) {
                            final Surface3D holeFace = wall.getOppositionSurface(parent);
                            HouseEdit.getHouseEditor().joinObject(new HoleDeUnDoEditor(wall, holeFace,
                                    wall.getSurfaceFlag(holeFace.getParentSurface())));
                            wall.removeHole(holeFace);
                        } else {
                            HouseEdit.getHouseEditor().joinObject(new Surface3DDeUndoEditor(parent));
                            parent.detachFromParent();
                        }
                    }
                }
                if (selectSurface.getParentSurface() != null) {
                    HouseEdit.getHouseEditor().joinObject(new Surface3DDeUndoEditor(selectSurface));
                    selectSurface.detachFromParent();
                }

            } catch (CSHouseException e) {
                e.printStackTrace();
            }
        }
        getPropertyPanel().setPropertys(this);
        pickTool.clear();
    }

    public void installFurniture() {
        if (pickTool.getPickResult() instanceof Furniture) {
            Furniture pickFurniture = (Furniture) pickTool.getPickResult();
            try {
                HouseEdit.getHouseEditor().joinObject(new FurnitureInstallUndoEditor(pickFurniture));
                pickFurniture.install();
                pickFurniture.getModel().hideOutLines();
                setOperateType(DisplayView.OperateType.DEFAULT);
                pickTool.clear();
            } catch (CSHouseException e) {
                e.printStackTrace();
            }

        }
    }

    public PropertyPanel getPropertyPanel() {
        return this.propertyPanel;
    }

    public House getCurrentHouse() {
        return this.currentHouse;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    public ControlResponser getControlResponser() {
        return controlResponser;
    }

    public ToolbarPanel getToolPanl() {
        return toolPanl;
    }

    public void setCurrentHouse(final House currentHouse) {
        if (currentHouse != null) {
            setModel(currentHouse);
            this.currentHouse = currentHouse;
            setOperateType(DisplayView.OperateType.DRAW_PLAN);
            currentFloor = this.currentHouse.getFloor("1th");
            planControler.reset();
            planViewControler.setViewDistance(0, ViewControler.DEFAULT_DISTANCE, 0);
            surfaceControler.reset();
            virtualViewController.reset();
            HouseEdit.getHouseEditor().reset();
            TempWall.walls.clear();
            planViewControler.drawPlanView();
            Pan.getPan().reset();
            initHouse(currentHouse);
        }
    }

    public HouseBean getHouseBean() {
        if (houseBean == null) {
            houseBean = new HouseBean();
        }
        return houseBean;
    }


    public void setHouseBean(final HouseBean houseBean) {
        this.houseBean = houseBean;
    }


    private void initHouse(final House house) {
        if (house != null) {
            for (Floor floor : house.getFloors()) {
                for (Room room : floor.getRooms()) {
                    setSurfaceFurnitureLocator(room.getCeilingDown());
                    setSurfaceFurnitureLocator(room.getFloorUp());
                }
                for (Wall wall : getFloorWalls(floor)) {
                    try {
                        TempWall tempWall = new TempWall(wall, floor);
                        setWallFurnitureLocator(wall);
                    } catch (CSHouseException e) {
                        e.printStackTrace();
                    } catch (CSUtilException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (TempWall tempWall : TempWall.walls.values()) {
                final WallShape postWallShape = tempWall.getWallShape().getPostWall();
                final WallShape preWallShape = tempWall.getWallShape().getPreWall();
                if (postWallShape != null) {
                    tempWall.setPostWall(TempWall.walls.get(postWallShape), false);
                }
                if (preWallShape != null) {
                    tempWall.setPreWall(TempWall.walls.get(preWallShape), false);
                }
            }
        }

    }

    private void setWallFurnitureLocator(final Wall wall) {
        for (int wallFaceIndex = 0; wallFaceIndex < 6; wallFaceIndex++) {
            setSurfaceFurnitureLocator(wall.getSurface(wallFaceIndex));
        }

    }

    private void setSurfaceFurnitureLocator(final Surface3D surface3D) {
        for (final Furniture furniture : surface3D.getFurnitureAttachedList()) {
            new FurnitureLocator(this, furniture);
        }
        for (final Surface3D subSurface : surface3D.getSubSurfaces()) {
            setSurfaceFurnitureLocator(subSurface);
        }
    }

    public void setViewType(ViewType operateType) {
        super.setViewType(operateType);
        propertyPanel.setPropertys(this);
        if (operateType == DisplayView.ViewType.DRAW_PLANVIEW) {
            propertyPanel.setModifyControler(planViewControler);
        } else if (operateType == DisplayView.ViewType.VIRTUAL_VIEW) {
            propertyPanel.setModifyControler(virtualViewController);
        }
    }

    public void setOperateType(OperateType operateType) {
        if (operateType == OperateType.DEFAULT) {
            OperateType currentType = getOperateType();
            if (currentType == OperateType.DRAW_PLAN) {
                planControler.reset();
            } else if (currentType == OperateType.ADD_SUBSURFACE) {
                surfaceControler.reset();
            }
            propertyPanel.setPropertys(this);
        }
        super.setOperateType(operateType);

    }

    public Set<Wall> getFloorWalls() {
        return getFloorWalls(this.currentFloor);
    }


    public Set<Wall> getFloorWalls(Floor currentFloor) {
        final Set<Wall> floorWalls = new HashSet<Wall>();
        for (Enumeration e = currentFloor.getFloorTrans().getAllChildren(); e.hasMoreElements();) {
            Object child = e.nextElement();
            if (child instanceof Wall) {
                floorWalls.add((Wall) child);
            }
        }
        return floorWalls;
    }

    private Set<Room> getFloorRooms(Floor currentFloor) {
        final Set<Room> floorRooms = new HashSet<Room>();
        for (int index = 0; index < currentFloor.getFloorTrans().numChildren(); index++) {
            if (currentFloor.getChild(index) instanceof Room) {
                floorRooms.add((Room) currentFloor.getChild(index));
            }
        }
        return floorRooms;
    }

    public AbstractPickUtil getPickUtil() {
        return pickTool;
    }

    public PlanControler getPlanControler() {
        return planControler;
    }

    public ViewControler getPlanViewControler() {
        return planViewControler;
    }

    public VirtualViewController getVirtualViewController() {
        return virtualViewController;
    }

    public SubSurfaceControler getSurfaceControler() {
        return surfaceControler;
    }

    public ModelControler getModelControler() {
        return modelControler;
    }

    public Canvas3D getMiniMap() {
        return miniMap;
    }


    public void setPickToolable(boolean able) {
        surfaceControler.getPickTool().setPickable(able);
    }

    public int getMouseButton() {
        return mouseButton;
    }

    @Override
    public void setEnabled(ContrType contrType, boolean enabled) {
    }

    @Override
    public TransformGroup getControllerGroup() {
        return controlTg;
    }

    public void actionPerformed(ActionEvent e) {
        final String command = e.getActionCommand();
        if (ActionCommandKey.DELETE_OBJECT == command) {
            deleteSelectObject(null);

        } else if (ActionCommandKey.INSTALLATION == command) {
            installFurniture();

        } else if (ActionCommandKey.DETAILED_INFO == command) {
            final Node selectObject = getSelectObject(null);
            Merchandise merchandise = null;
            if (selectObject instanceof Surface3D) {
                Surface3D selectSurface = (Surface3D) selectObject;
                merchandise = selectSurface.getSurfaceMaterial();
            } else if (selectObject instanceof Furniture) {
                merchandise = (Merchandise) selectObject;
            }
            if (merchandise != null) {
                MerchandiseInfoDialog.show(SwingUtilities.getWindowAncestor(this), merchandise, this);
            }
        } else if (ActionCommandKey.CANCEL == command) {
            if (getViewType() == DisplayView.ViewType.ORBIT_VIEW) {
                if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                    surfaceControler.reset();
                }
                setOperateType(OperateType.ORBIT);
                propertyPanel.setPropertys(surfaceControler.getView());
            } else if (getViewType() == DisplayView.ViewType.VIRTUAL_VIEW) {
                if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                    surfaceControler.reset();
                }
                setOperateType(DisplayView.OperateType.DEFAULT);
                propertyPanel.setPropertys(surfaceControler.getView());
            }

        } else if (ActionCommandKey.ROTATION_SURFACE_MATERIAL == command) {
            final Node selectObject = getSelectObject(null);
            if (selectObject instanceof Surface3D) {
                Surface3D selectSurface = (Surface3D) selectObject;
                if (selectSurface != null) {
                    final Transform3D rotation = new Transform3D();
                    rotation.rotZ(Math.PI / 4);
                    rotation.mul(selectSurface.getSurfaceMaterialTransform());
                    selectSurface.setSurfaceMaterialTransform(rotation);

                }
            }

        }

    }


    class KeyboardControlerManager implements KeyEventDispatcher {
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                final int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        if (getViewType() == DisplayView.ViewType.VIRTUAL_VIEW)
                            virtualViewController.getVirtualView().stepForward();
                        break;
                    case KeyEvent.VK_S:
                        if (getViewType() == DisplayView.ViewType.VIRTUAL_VIEW)
                            virtualViewController.getVirtualView().stepBackward();
                        break;
                    case KeyEvent.VK_D:
                        if (getViewType() == DisplayView.ViewType.VIRTUAL_VIEW)
                            virtualViewController.getVirtualView().stepRight();
                    case KeyEvent.VK_Z:
                        if (e.isControlDown()) {
                            pickTool.clear();
                            HouseEdit.getHouseEditor().undo();
                            toolPanl.checkDoEdit();
                        }
                        break;
                    case KeyEvent.VK_Y:
                        if (e.isControlDown()) {
                            HouseEdit.getHouseEditor().redo();
                            toolPanl.checkDoEdit();
                            pickTool.clear();
                        }
                        break;
                    case KeyEvent.VK_A:
                        if (getViewType() == DisplayView.ViewType.VIRTUAL_VIEW) {
                            virtualViewController.getVirtualView().stepLeft();
                        }

                        break;
                    case KeyEvent.VK_DELETE:
                        deleteSelectObject(null);
                        break;
                    default:
                        break;
                }

            }
            return false;

        }
    }

    @Override
    public void setController(DisplayControlable controller) {
    }

    public void createRoom() {
        if (getViewType() == ViewType.DRAW_PLANVIEW) {
            setOperateType(OperateType.CREATE_ROOM);
            roomGenerator = new RoomGenerator(this);
        } else {
            planViewControler.drawPlanView();
            setOperateType(OperateType.CREATE_ROOM);
            roomGenerator = new RoomGenerator(this);

        }
    }


    class ControlerManager extends MouseAdapter {

        public ControlerManager() {
            super();
        }

        @Override
        public void mousePressed(MouseEvent me) {
            mouseButton = me.getButton();
            if (mouseButton == MouseEvent.BUTTON1) {
                if (getViewType() == ViewType.DRAW_PLANVIEW) {
                    if (getOperateType() == OperateType.CREATE_ROOM) {
                        if (!(getSelectObject(me) instanceof Room)) {
                            roomGenerator.mousePressed(planControler.getBaseFace().
                                    getClickedPointOnSurface(me, viewCanvas), getCurrentFloor());
                        }
                    } else if (getOperateType() == OperateType.DRAW_PLAN) {
                        planControler.getControler().controlerMousePressed(me);
                    } else if (getOperateType() == OperateType.DELETE_OBJECT) {
                        deleteSelectObject(me);
                    }
                } else if (getViewType() == ViewType.ORBIT_VIEW) {
                    if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                        surfaceControler.getMouseControler().controlerMousePressed(me);
                    } else if (getOperateType() == OperateType.ORBIT) {
                        modelControler.getControler().controlerMousePressed(me);
                    }
                } else if (getViewType() == ViewType.VIRTUAL_VIEW) {
                    if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                        surfaceControler.getMouseControler().controlerMousePressed(me);
                    }

                }
                if (me.getComponent().equals(miniMap)) {
                    planViewControler.virtuallView();
                    Point3d viewPoint = planControler.getBaseFace().
                            getClickedPointOnSurface(me, miniMap);
                    Transform3D transform3D = new Transform3D();
                    transform3D.set(new Vector3d(viewPoint.x, VirtualViewController.
                            DEFAULT_VIEW_HEIGHT, viewPoint.z));
                    virtualViewController.setHomeTransform(transform3D);
                }
                if (getOperateType() != DisplayView.OperateType.ADD_SUBSURFACE) {
                    setSelectObjectProperty(me);
                }

            } else if (mouseButton == MouseEvent.BUTTON3) {
                if (getOperateType() != OperateType.DEFAULT) {
                    if (getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
                        if (getOperateType() == OperateType.DRAW_PLAN) {
                            planControler.reset();
                            propertyPanel.setPropertys(planControler.getView());
                        } else {
                            setOperateType(OperateType.DEFAULT);
                        }
                    }
                }
                if (getViewType() == DisplayView.ViewType.ORBIT_VIEW ||
                        getViewType() == DisplayView.ViewType.VIRTUAL_VIEW) {
                    showRightButtonMenu(me);
                }
            }

        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (getViewType()
                    == ViewType.ORBIT_VIEW) {
                if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                    surfaceControler.getMouseControler().controlerMouseReleased(me);
                }
            } else if (getViewType() == ViewType.DRAW_PLANVIEW) {
                planViewControler.getControler().mouseReleased(me);
            }
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            if (getViewType() == ViewType.DRAW_PLANVIEW) {
                planControler.getControler().controlerMouseMoved(me);
            } else if (getViewType() == ViewType.ORBIT_VIEW) {
                if (getOperateType() == OperateType.ORBIT) {
                    modelControler.getControler().controlerMouseMoved(me);
                } else if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                    surfaceControler.getMouseControler().controlerMouseMoved(me);
                }
            } else if (getViewType() == ViewType.VIRTUAL_VIEW) {
                if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                    surfaceControler.getMouseControler().controlerMouseMoved(me);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (getMouseButton() == MouseEvent.BUTTON1) {
                if (getViewType() == ViewType.ORBIT_VIEW) {
                    if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                        surfaceControler.getMouseControler().controlerMouseDragged(me);
                    } else if (getOperateType() == OperateType.DEFAULT) {
                        modelControler.getControler().controlerMouseDragged(me);
                        getViewCanvas().setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                } else if (getViewType() == ViewType.VIRTUAL_VIEW) {
                    if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                        surfaceControler.getMouseControler().controlerMouseDragged(me);
                    } else if (getOperateType() == OperateType.DEFAULT) {
                        virtualViewController.getMouseControler().controlerMouseDragged(me);
                    }
                } else if (getViewType() == ViewType.DRAW_PLANVIEW) {
                    planViewControler.getControler().mouseDragged(me);
                }
            }
        }


        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            if (getViewType() == ViewType.DRAW_PLANVIEW) {
                planViewControler.getControler().controlerMouseWheelMoved(mwe);
            } else if (getViewType() == ViewType.ORBIT_VIEW) {
                modelControler.getControler().controlerMouseWheelMoved(mwe);
            } else if (getViewType() == ViewType.VIRTUAL_VIEW) {
                virtualViewController.getMouseControler().mouseWheelMoved(mwe);
            }
        }

        private void setSelectObjectProperty(MouseEvent me) {
            Object selectObject = getSelectObject(me);
            switch (getViewType()) {
                case DRAW_PLANVIEW:
                    if (getOperateType() == DisplayView.OperateType.DEFAULT ||
                            getOperateType() == DisplayView.OperateType.MOVE_FURNITURE) {
                        if (selectObject != null) {
                            if (selectObject instanceof Furniture) {
                                setOperateType(DisplayView.OperateType.MOVE_FURNITURE);
                            } else {
                                setOperateType(OperateType.DEFAULT);
                                planControler.reset();
                            }
                            getPropertyPanel().setPropertys(selectObject);
                        } else {
                            getPropertyPanel().setPropertys(planViewControler.getView());
                            planControler.reset();
                        }
                    }
                    break;
                case ORBIT_VIEW:
                    if (selectObject != null) {
                        if (selectObject instanceof Furniture) {
                            setOperateType(DisplayView.OperateType.MOVE_FURNITURE);
                            getPropertyPanel().setPropertys(selectObject);
                        } else if (selectObject instanceof Surface3D) {
                            if (getOperateType() != DisplayView.OperateType.ADD_SUBSURFACE) {
                                Surface3D selectSurface = (Surface3D) selectObject;
                                if (selectSurface.getParentSurface() != null) {
                                    getPropertyPanel().setPropertys(selectObject);
                                } else {
                                    if (!selectSurface.isConnectiveSurface()) {
                                        getPropertyPanel().setPropertys(planViewControler.getView());
                                        setOperateType(DisplayView.OperateType.DEFAULT);
                                    } else {
                                        getPropertyPanel().setPropertys(((Surface3D) selectObject)
                                                .getFirstParentOf(Surface3D.class));
                                    }
                                }
                            }
                        }
                    } else {
                        getPropertyPanel().setPropertys(virtualViewController.getView());
                    }
                    break;
                case VIRTUAL_VIEW:
                    if (selectObject instanceof Furniture) {
                        setOperateType(DisplayView.OperateType.MOVE_FURNITURE);
                        getPropertyPanel().setPropertys(selectObject);
                    } else if (selectObject instanceof Surface3D) {
                        if (getOperateType() != DisplayView.OperateType.ADD_SUBSURFACE) {
                            Surface3D selectSurface = (Surface3D) selectObject;
                            if (selectSurface.getParentSurface() != null) {
                                getPropertyPanel().setPropertys(selectObject);
                            } else {
                                if (!selectSurface.isConnectiveSurface()) {
                                    getPropertyPanel().setPropertys(virtualViewController.getView());
                                    setOperateType(DisplayView.OperateType.DEFAULT);
                                } else {
                                    getPropertyPanel().setPropertys(((Surface3D) selectObject).
                                            getFirstParentOf(Surface3D.class));
                                }
                            }
                        }

                    }
                    break;
                default:
                    break;
            }
        }

    }

    private void showRightButtonMenu(MouseEvent me) {
        int[] menuItemGroup = null;
        switch (getViewType()) {
            case DRAW_PLANVIEW:
                break;
            case ORBIT_VIEW:
                if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                    menuItemGroup = canceledMenuItemGroup;
                } else if (getSelectObject(null) instanceof Furniture) {
                    menuItemGroup = furnitureMenuItemGroup;
                } else if (getSelectObject(null) instanceof Surface3D) {
                    Surface3D surface3D = (Surface3D) getSelectObject(null);
                    if (surface3D.getParentSurface() == null) {
                        if (surface3D.isConnectiveSurface()
                                || surface3D.getSurfaceMaterial() != null) {
                            menuItemGroup = surfaceMenuItemGroup;
                        }
                    } else {
                        menuItemGroup = surfaceMenuItemGroup;
                    }
                }
                break;
            case VIRTUAL_VIEW:
                if (getOperateType() == OperateType.ADD_SUBSURFACE) {
                    menuItemGroup = canceledMenuItemGroup;
                } else if (getSelectObject(null) instanceof Furniture) {
                    menuItemGroup = furnitureMenuItemGroup;
                } else if (getSelectObject(null) instanceof Surface3D) {
                    Surface3D surface3D = (Surface3D) getSelectObject(null);
                    if (surface3D.getParentSurface() == null) {
                        if (surface3D.isConnectiveSurface()
                                || surface3D.getSurfaceMaterial() != null) {
                            menuItemGroup = surfaceMenuItemGroup;
                        }
                    } else {
                        menuItemGroup = surfaceMenuItemGroup;
                    }

                }
                break;
            default:
                break;
        }
        if (menuItemGroup != null) {
            rightButtonMenu.removeAll();
            for (int index : menuItemGroup) {
                rightButtonMenu.add(menuItems[index]);
            }
            rightButtonMenu.show(getViewCanvas(), me.getX(), me.getY());
        }

    }

}
