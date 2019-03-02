package cs.designer.module;

import com.klm.cons.impl.*;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.LocalStorage;
import com.klm.persist.impl.SurfaceMaterial;
import com.klm.persist.meta.BufferedImageMeta;
import com.klm.util.CSUtilException;
import cs.designer.swing.resources.ResourcesPath;

import javax.imageio.ImageIO;
import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author rongyang
 */
public class TempWall {
    public final static float WALL_THICKNESS = 0.3f; //thickness
    public static Map<WallShape, TempWall> walls = new HashMap<WallShape, TempWall>();
    private Wall wall;
    private TempWall preWall;
    private TempWall postWall;
    private Set<TempWall> subWalls;
    private final Floor currentFloor;

    public TempWall(final WallShape wallShape, final Floor currentFloor)
            throws CSHouseException, CSUtilException {
        this(new Wall(wallShape), currentFloor);
    }

    public TempWall(final Wall wall,
                    final Floor currentFloor)
            throws CSHouseException, CSUtilException {
        this.wall = wall;
        this.subWalls = new HashSet<TempWall>();
        walls.put(wall.getWallShape(), this);
        this.currentFloor = currentFloor;
    }


    public WallShape getWallShape() {
        return this.wall.getWallShape();
    }

    public Wall getWall() {
        return this.wall;
    }

    public void update() {
        try {
            if (this.wall != null) {
                Surface3D frontFace = this.wall.getSurface(Wall.FRONT_SURFACE_INDEX);
                Surface3D backFace = this.wall.getSurface(Wall.BACK_SURFACE_INDEX);
                final SurfaceMaterial frontMaterial = frontFace.getSurfaceMaterial();
                final SurfaceMaterial backMaterial = backFace.getSurfaceMaterial();
                final Set<Surface3D> frontSubSurfaces =
                        frontFace.getSubSurfaces();
                final Set<Surface3D> backSubSurfaces = backFace.getSubSurfaces();
                final Set<Furniture> onFrontFurnitures = frontFace.getFurnitureAttached();
                final Set<Furniture> onBackFurnitures = backFace.getFurnitureAttached();
                wall.setWallShape(getWallShape());
                frontFace = this.wall.getSurface(Wall.FRONT_SURFACE_INDEX);
                backFace = this.wall.getSurface(Wall.BACK_SURFACE_INDEX);
                for (final Surface3D subSurface : frontSubSurfaces) {
                    if (frontFace.testSubSurface(subSurface)) {
                        subSurface.detachFromParent();
                        frontFace.addSubSurface(subSurface);
                        subSurface.createConnectiveSurfaces();
                    }
                }
                for (final Surface3D subSurface : backSubSurfaces) {
                    if (backFace.testSubSurface(subSurface)) {
                        subSurface.detachFromParent();
                        backFace.addSubSurface(subSurface);
                        subSurface.createConnectiveSurfaces();
                    }
                }
                for (final Furniture furniture : onFrontFurnitures) {
                    furniture.detachFromParent();
                    frontFace.getAttachmentBG().addChild(furniture);
                }
                for (final Furniture furniture : onBackFurnitures) {
                    furniture.detachFromParent();
                    backFace.getAttachmentBG().addChild(furniture);
                }
                if (frontMaterial != null) {
                    frontFace.setSurfaceMaterial(frontMaterial);
                }
                if (backMaterial != null) {
                    backFace.setSurfaceMaterial(backMaterial);
                }
            }
        } catch (CSHouseException e) {
            e.printStackTrace();

        } catch (CSUtilException e) {
            e.printStackTrace();

        }
    }

    public void setPostWall(final TempWall postWall, boolean updateable) {
        if (postWall != this.postWall) {
            if (postWall == null) {
                this.wall.getWallShape().setPostWall(null);
            } else {
                this.getWallShape().setPostWall(postWall.getWallShape());
                if (updateable) {
                    postWall.update();
                }
            }
            this.postWall = postWall;
            if (updateable) {
                update();
            }
        }
    }

    public void setPreWall(final TempWall preWall, boolean updateable) {
        if (preWall != this.preWall) {
            if (preWall == null) {
                this.wall.getWallShape().setPreWall(null);
            } else {
                this.getWallShape().setPreWall(preWall.getWallShape());
                if (updateable) {
                    preWall.update();
                }
            }
            this.preWall = preWall;
            if (updateable) {
                update();
            }
        }
    }

    public void setSplitWall(float[] splitPoint)
            throws CSHouseException, CSUtilException {
        if (this.subWalls.size() == 0) {
            splitWall(this, splitPoint);
        } else {
            for (TempWall child : new HashSet<TempWall>(subWalls)) {
                if (child.getWallShape().containsdPoint(splitPoint[0], splitPoint[1], 1E-3F)) {
                    if (splitWall(child, splitPoint)) {
                        subWalls.remove(child);
                    }
                    break;
                }

            }

        }


    }

    private boolean splitWall(TempWall splitWall, float[] splitPoint) {
        boolean success = true;
        TempWall wallAtStart = splitWall.getPreWall();
        boolean joinedAtEndOfWallAtStart =
                wallAtStart != null
                        && wallAtStart.getPostWall() == splitWall;
        boolean joinedAtStartOfWallAtStart =
                wallAtStart != null
                        && wallAtStart.getPreWall() == splitWall;
        TempWall wallAtEnd = splitWall.getPostWall();
        boolean joinedAtEndOfWallAtEnd =
                wallAtEnd != null
                        && wallAtEnd.getPostWall() == splitWall;
        boolean joinedAtStartOfWallAtEnd =
                wallAtEnd != null
                        && wallAtEnd.getPreWall() == splitWall;

        WallShape[] subwallShapes = splitWall.getWallShape().splitWall(splitPoint);
        TempWall firstWall = null;
        TempWall secondWall = null;
        try {
            if (subwallShapes != null) {
                final Surface3D frontFace = splitWall.getWall().getSurface(Wall.FRONT_SURFACE_INDEX);
                final Surface3D backFace = splitWall.getWall().getSurface(Wall.BACK_SURFACE_INDEX);
                final SurfaceMaterial frontMaterial = frontFace.getSurfaceMaterial();
                final SurfaceMaterial backMaterial = backFace.getSurfaceMaterial();
                final Set<Surface3D> frontSubSurfaces =
                        frontFace.getSubSurfaces();
                final Set<Surface3D> backSubSurfaces = backFace.getSubSurfaces();
                firstWall = new TempWall(subwallShapes[0], currentFloor);
                secondWall = new TempWall(subwallShapes[1], currentFloor);
                final Surface3D firstWallFront = firstWall.getWall().getSurface(Wall.FRONT_SURFACE_INDEX);
                final Surface3D firstWallBack = firstWall.getWall().getSurface(Wall.BACK_SURFACE_INDEX);
                final Surface3D secondWallFront = secondWall.getWall().getSurface(Wall.FRONT_SURFACE_INDEX);
                final Surface3D secondWallBack = secondWall.getWall().getSurface(Wall.BACK_SURFACE_INDEX);
                for (final Surface3D subSurface : frontSubSurfaces) {
                    if (firstWallFront.testSubSurface(subSurface)) {
                        subSurface.detachFromParent();
                        firstWallFront.addSubSurface(subSurface);
                        subSurface.createConnectiveSurfaces();
                    } else if (secondWallFront.testSubSurface(subSurface)) {
                        subSurface.detachFromParent();
                        secondWallFront.addSubSurface(subSurface);
                        subSurface.createConnectiveSurfaces();
                    }

                }
                for (final Surface3D subSurface : backSubSurfaces) {
                    if (firstWallBack.testSubSurface(subSurface)) {
                        subSurface.detachFromParent();
                        firstWallBack.addSubSurface(subSurface);
                        subSurface.createConnectiveSurfaces();
                    } else if (secondWallBack.testSubSurface(subSurface)) {
                        subSurface.detachFromParent();
                        secondWallBack.addSubSurface(subSurface);
                        subSurface.createConnectiveSurfaces();
                    }
                }
                if (frontMaterial != null) {
                    firstWallFront.setSurfaceMaterial(frontMaterial);
                    secondWallFront.setSurfaceMaterial(frontMaterial);
                }
                if (backMaterial != null) {
                    firstWallBack.setSurfaceMaterial(backMaterial);
                    secondWallBack.setSurfaceMaterial(backMaterial);
                }
            } else {
                return false;
            }
        } catch (CSHouseException e) {
            e.printStackTrace();
            success = false;
        } catch (CSUtilException e) {
            e.printStackTrace();
            success = false;
        } catch (NumberFormatException e) {
            success = false;
            e.printStackTrace();
        } finally {
            if (success && firstWall != null
                    && secondWall != null) {
                if (joinedAtEndOfWallAtStart) {
                    wallAtStart.postWall = firstWall;
                } else if (joinedAtStartOfWallAtStart) {
                    wallAtStart.preWall = firstWall;
                }

                if (joinedAtEndOfWallAtEnd) {
                    wallAtEnd.postWall = secondWall;
                } else if (joinedAtStartOfWallAtEnd) {
                    wallAtEnd.preWall = secondWall;
                }
                firstWall.preWall = preWall;
                secondWall.postWall = postWall;
                firstWall.postWall = secondWall;
                secondWall.preWall = firstWall;
                subWalls.add(firstWall);
                subWalls.add(secondWall);
                walls.put(firstWall.getWallShape(), firstWall);
                walls.put(secondWall.getWallShape(), secondWall);
                splitWall.getCurrentFloor().addWall(firstWall.getWall());
                splitWall.getCurrentFloor().addWall(secondWall.getWall());

                deleteWall(splitWall, true);
            }
        }
        return success;


    }

    public TempWall getPreWall() {
        return preWall;
    }

    public TempWall getPostWall() {
        return postWall;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    public Set<TempWall> getSubWalls() {
        return subWalls;
    }

    public void removeFromParent(boolean detachJoinedWall) {
        if (detachJoinedWall) {
            for (TempWall otherWall : TempWall.walls.values()) {
                if (this == otherWall.getPreWall()) {
                    otherWall.getWallShape().setPreWall(null);
                    otherWall.update();
                } else if (this == otherWall.getPostWall()) {
                    otherWall.getWallShape().setPostWall(null);
                    otherWall.update();
                }
            }
        }
        currentFloor.removeWall(wall);
        walls.remove(getWallShape());
        Set<TempWall> subWalls = getSubWalls();
        for (TempWall subWall : subWalls) {
            subWall.removeFromParent(detachJoinedWall);
        }

    }

    public void addToParent() {
        boolean joinedAtEndOfWallAtStart =
                this.preWall != null
                        && this.preWall.getPostWall() == this;
        boolean joinedAtStartOfWallAtStart =
                this.preWall != null
                        && this.preWall.getPreWall() == this;
        boolean joinedAtEndOfWallAtEnd =
                this.postWall != null
                        && postWall.getPostWall() == this;
        boolean joinedAtStartOfWallAtEnd =
                this.postWall != null
                        && postWall.getPreWall() == this;
        if (this.preWall != null && walls.containsValue(preWall)) {
            if (joinedAtEndOfWallAtStart) {
                preWall.getWallShape().setPostWall(getWallShape());
                getWallShape().setPreWall(preWall.getWallShape());
                preWall.update();

            } else if (joinedAtStartOfWallAtStart) {
                preWall.getWallShape().setPreWall(getWallShape());
                getWallShape().setPreWall(preWall.getWallShape());
                preWall.update();

            }
        }
        if (this.postWall != null && walls.containsValue(postWall)) {
            if (joinedAtStartOfWallAtEnd) {
                postWall.getWallShape().setPreWall(getWallShape());
                getWallShape().setPostWall(postWall.getWallShape());
                postWall.update();
            } else if (joinedAtEndOfWallAtEnd) {
                postWall.getWallShape().setPostWall(getWallShape());
                getWallShape().setPostWall(postWall.getWallShape());
                postWall.update();
            }
        }

        if (getSubWalls().size() == 0) {
            walls.put(getWallShape(), this);
            currentFloor.addWall(wall);

        } else {
            Set<TempWall> subWalls = getSubWalls();
            for (TempWall subWall : subWalls) {
                subWall.addToParent();
            }
        }

        update();
    }


    private void deleteWall(final TempWall tempWall,
                            boolean detachJoinedWall) {
        if (detachJoinedWall) {
            for (TempWall otherWall : walls.values()) {
                if (tempWall.equals(otherWall.getPreWall())) {
                    otherWall.setPreWall(null, true);
                } else if (tempWall.equals(otherWall.getPostWall())) {
                    otherWall.setPostWall(null, true);
                }
            }
        }
        walls.remove(tempWall.getWallShape());
        tempWall.getWall().detach();
        tempWall.currentFloor.removeWall(tempWall.getWall());

    }

    public static void clearTempWalls() {
        for (TempWall tempWall : walls.values()) {
            tempWall.getWall().detach();
            tempWall = null;
        }
        walls.clear();

    }

    protected void finalize() {
        walls.remove(this);
        wall.detach();
        this.currentFloor.removeWall(this.wall);
        this.wall = null;
    }
}
