package cs.designer.swing.tool;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Wall;
import com.klm.cons.impl.WallShape;
import com.klm.util.CSUtilException;
import cs.designer.module.AuxiliaryObject;
import cs.designer.module.Pan;
import cs.designer.module.TempWall;
import cs.designer.module.WallModel;
import cs.designer.swing.property.PropertyPanel;
import cs.designer.utils.PointWithAngleMagnetism;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import javax.media.j3d.Node;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * @author rongyang
 */
public class WallGenerator implements Generatorable {


    private float xStart = Float.POSITIVE_INFINITY;
    private float yStart = Float.POSITIVE_INFINITY;
    private WallModel wallModel;
    private PropertyPanel propertyPanel;
    private WallManager wallManager;
    private HousePlanView view;
    private AbstractPickUtil pickTool;
    private boolean movePointable = false;
    private Wall pickWall;
    private AuxiliaryObject auxiliaryObject;


    public WallGenerator(final DisplayView view) {
        this.view = (HousePlanView) view;
        wallManager = new WallManager(this.view);
        this.propertyPanel = ((HousePlanView) view).getPropertyPanel();
        this.pickTool = new WallPickUtil(view);
        auxiliaryObject = new AuxiliaryObject();
        view.getObjRoot().addChild(auxiliaryObject);
    }

    public void produceWall(final MouseEvent me, float mouseX, float mouseY) {
        auxiliaryObject.setVisible(true);
        if (MouseEvent.BUTTON1 == me.getButton()) {
            if (this.xStart == Float.POSITIVE_INFINITY
                    && this.yStart == Float.POSITIVE_INFINITY) {
                this.xStart = mouseX;
                this.yStart = mouseY;
                if (pickWall != null) {
                    float[] satartPoint = auxiliaryObject.getAnchorPoint(pickWall, mouseX, mouseY, 1E-3F);
                    if (satartPoint != null) {
                        this.xStart = satartPoint[0];
                        this.yStart = satartPoint[1];
                    }
                }

            } else {
                if (wallModel != null) {
                    mouseX = wallModel.getWallShape().getXEnd();
                    mouseY = wallModel.getWallShape().getYEnd();
                }
                if (pickWall != null) {
                    float[] endtPoint = auxiliaryObject.getAnchorPoint(pickWall, mouseX, mouseY, 1E-3F);
                    if (endtPoint != null) {
                        mouseX = endtPoint[0];
                        mouseY = endtPoint[1];
                    }
                }

                final WallShape tempWallShape = new
                        WallShape(this.xStart, this.yStart, mouseX, mouseY, Pan.getPan().getThickness());

                try {
                    wallManager.addWall(createTempWall(tempWallShape, true, 1E-3F));
                } catch (CSHouseException e) {
                    e.printStackTrace();
                } catch (CSUtilException e) {
                    e.printStackTrace();
                }
                this.xStart = mouseX;
                this.yStart = mouseY;
            }
        }

    }

    public void produceWallModel(MouseEvent me, float mouseX, float mouseY) {
        auxiliaryObject.setTranslation(mouseX, mouseY);
        Node pickResult = pickTool.getPickResult(me.getX(), me.getY());
        pickWall = pickResult == null ? null : (Wall) pickResult;
        auxiliaryObject.getAnchorPoint(pickWall, mouseX, mouseY, 0.3f);
        if (this.xStart != Float.POSITIVE_INFINITY
                && this.yStart != Float.POSITIVE_INFINITY) {
            WallShape tempWallShape = new WallShape(xStart, yStart, mouseX, mouseY, Pan.getPan().getThickness());
            float distance = (float) Point2D.distance(xStart, yStart,
                    mouseX, mouseY);
            if (Pan.getPan().isMagnetismEnabled()) {
                float[] magnetismPoint = getMagnetismPoint(mouseX, mouseY, 0.2f);
                if (magnetismPoint == null) {

                    PointWithAngleMagnetism endPoint = new PointWithAngleMagnetism(xStart, yStart,
                            mouseX, mouseY, distance);
                    tempWallShape.setXEnd(endPoint.getX());
                    tempWallShape.setYEnd(endPoint.getY());
                } else {
                    tempWallShape.setXEnd(magnetismPoint[0]);
                    tempWallShape.setYEnd(magnetismPoint[1]);
                    final PointWithAngleMagnetism startPoint =
                            new PointWithAngleMagnetism(magnetismPoint[0], magnetismPoint[1],
                                    xStart, yStart, distance);
                    if (startPoint.getAngle1(startPoint.getX(), startPoint.getY(),
                            magnetismPoint[0], magnetismPoint[1]) % 90 == 0) {
                        tempWallShape.setXStart(startPoint.getX());
                        tempWallShape.setYStart(startPoint.getY());
                        movePointable = true;
                    } else {
                        movePointable = false;
                    }
                }
            }

            if (this.wallModel == null) {
                this.wallModel = new WallModel(tempWallShape);
                this.wallManager.addWallModel(wallModel);
            } else {
                wallModel.setWallShape(tempWallShape);
                propertyPanel.setPropertys(wallModel);
            }

        }

    }

    private float[] getMagnetismPoint(float x, float y, float limint) {
        for (final TempWall tempWall : TempWall.walls.values()) {
            final WallShape wallShape = tempWall.getWallShape();
            if (wallShape.containsWallStartAt(x, y, limint)) {
                return new float[]{wallShape.getXStart(), wallShape.getYStart()};
            } else if (wallShape.containsWallEndAt(x, y, limint)) {
                return new float[]{wallShape.getXEnd(), wallShape.getYEnd()};
            }

        }
        return null;

    }

    private TempWall createTempWall(final WallShape addwallShape, boolean updateable,
                                    float margin)
            throws CSHouseException, CSUtilException {
        TempWall wallEndAtStart = null;
        TempWall wallStartAtStart = null;
        TempWall wallStartAtEnd = null;
        TempWall wallEndAtEnd = null;
        for (final TempWall tempWall : TempWall.walls.values()) {
            final WallShape wallShape = tempWall.getWallShape();
            if (wallShape != addwallShape
                    && wallShape.getPreWall() == null
                    && wallShape.containsWallStartAt(addwallShape.getXEnd(),
                    addwallShape.getYEnd(), margin)) {
                wallEndAtStart = tempWall;
            } else if (wallShape != addwallShape
                    && wallShape.getPreWall() == null
                    && wallShape.containsWallStartAt(addwallShape.getXStart(),
                    addwallShape.getYStart(), margin)) {
                wallStartAtStart = tempWall;
            } else if (wallShape != addwallShape
                    && wallShape.getPostWall() == null
                    && wallShape.containsWallEndAt(addwallShape.getXStart(),
                    addwallShape.getYStart(), margin)) {
                wallStartAtEnd = tempWall;
            } else if (wallShape != addwallShape
                    && wallShape.getPostWall() == null
                    && wallShape.containsWallEndAt(addwallShape.getXEnd(),
                    addwallShape.getYEnd(), margin)) {
                wallEndAtEnd = tempWall;
            }
            //

        }
        TempWall addteTempWall = new TempWall(addwallShape, this.view.getCurrentFloor());
        if (wallEndAtStart != null) {
            addteTempWall.getWallShape().
                    setXEnd(wallEndAtStart.getWallShape().getXStart());
            addteTempWall.getWallShape().
                    setYEnd(wallEndAtStart.getWallShape().getYStart());
            wallEndAtStart.setPreWall(addteTempWall, updateable);
            addteTempWall.setPostWall(wallEndAtStart, updateable);
        }
        if (wallEndAtEnd != null) {
            addteTempWall.getWallShape().
                    setXEnd(wallEndAtEnd.getWallShape().getXEnd());
            addteTempWall.getWallShape().
                    setYEnd(wallEndAtEnd.getWallShape().getYEnd());
            addteTempWall.setPostWall(wallEndAtEnd, updateable);
            wallEndAtEnd.setPostWall(addteTempWall, updateable);
        }
        if (wallStartAtEnd != null) {
            addteTempWall.getWallShape().
                    setXStart(wallStartAtEnd.getWallShape().getXEnd());
            addteTempWall.getWallShape().
                    setYStart(wallStartAtEnd.getWallShape().getYEnd());
            wallStartAtEnd.setPostWall(addteTempWall, updateable);
            addteTempWall.setPreWall(wallStartAtEnd, updateable);
        }
        if (wallStartAtStart != null) {
            addteTempWall.getWallShape().
                    setXStart(wallStartAtStart.getWallShape().getXStart());
            addteTempWall.getWallShape().
                    setYStart(wallStartAtStart.getWallShape().getYStart());
            addteTempWall.setPreWall(wallStartAtStart, updateable);
            wallStartAtStart.setPreWall(addteTempWall, updateable);
        }
        if (Pan.getPan().isMagnetismEnabled()) {
            setWallAngleInDegrees(addteTempWall);
        }
        return addteTempWall;
    }

    private void setWallAngleInDegrees(final TempWall tempWall) {
        final TempWall preWall = tempWall.getPreWall();
        final TempWall postWall = tempWall.getPostWall();
        if (preWall != null) {
            if (preWall.getPostWall() == tempWall) {
                final WallShape wallShape = tempWall.getWallShape();
                final PointWithAngleMagnetism point = new PointWithAngleMagnetism(wallShape.getXEnd(), wallShape.getYEnd(),
                        wallShape.getXStart(), wallShape.getYStart(),
                        (float) Point2D.distance(wallShape.getXEnd(), wallShape.getYEnd(),
                                preWall.getWallShape().getXEnd(), preWall.getWallShape().getYEnd()));
                if (movePointable)
                    moveWallStartPoint(tempWall, point.getX(), point.getY());

            } else if (preWall.getPreWall() == tempWall) {
                final WallShape wallShape = tempWall.getWallShape();
                final PointWithAngleMagnetism point = new PointWithAngleMagnetism(wallShape.getXEnd(), wallShape.getYEnd(),
                        wallShape.getXStart(), wallShape.getYStart(),
                        (float) Point2D.distance(wallShape.getXEnd(), wallShape.getYEnd(),
                                preWall.getWallShape().getXStart(), preWall.getWallShape().getYStart()));
                if (movePointable)
                    moveWallStartPoint(tempWall, point.getX(), point.getY());

            }
        } else if (postWall != null) {
            if (postWall.getPostWall() == tempWall) {
                final WallShape wallShape = tempWall.getWallShape();
                final PointWithAngleMagnetism point = new PointWithAngleMagnetism(wallShape.getXStart(), wallShape.getYStart(),
                        wallShape.getXEnd(), wallShape.getYEnd(),
                        (float) Point2D.distance(wallShape.getXEnd(), wallShape.getYEnd(),
                                wallShape.getXStart(), wallShape.getYStart()));
                if (movePointable)
                    moveWallEndPoint(tempWall, point.getX(), point.getY());

            } else if (postWall.getPreWall() == tempWall) {
                final WallShape wallShape = tempWall.getWallShape();
                final PointWithAngleMagnetism point = new PointWithAngleMagnetism(wallShape.getXStart(), wallShape.getYStart(),
                        wallShape.getXEnd(), wallShape.getYEnd(),
                        (float) Point2D.distance(wallShape.getXEnd(), wallShape.getYEnd(),
                                wallShape.getXStart(), wallShape.getYStart()));
                if (movePointable)
                    moveWallEndPoint(tempWall, point.getX(), point.getY());

            }
        }
    }

    private void moveWallStartPoint(final TempWall tempWall, float xStart, float yStart) {
        float oldXStart = tempWall.getWallShape().getXStart();
        float oldYStart = tempWall.getWallShape().getYStart();
        tempWall.getWallShape().setXStart(xStart);
        tempWall.getWallShape().setYStart(yStart);
        TempWall wallAtStart = tempWall.getPreWall();
        TempWall postWall = tempWall.getPostWall();
        if (wallAtStart != null) {
            if (wallAtStart.getPreWall() == tempWall
                    && (wallAtStart.getPostWall() != tempWall
                    || (wallAtStart.getWallShape().getXStart() == oldXStart
                    && wallAtStart.getWallShape().getYStart() == oldYStart))) {
                wallAtStart.getWallShape().setXStart(xStart);
                wallAtStart.getWallShape().setYStart(yStart);
            } else if (wallAtStart.getPostWall() == tempWall
                    && (wallAtStart.getPreWall() != tempWall
                    || (wallAtStart.getWallShape().getXEnd() == oldXStart
                    && wallAtStart.getWallShape().getYEnd() == oldYStart))) {
                wallAtStart.getWallShape().setXEnd(xStart);
                wallAtStart.getWallShape().setYEnd(yStart);
            }
            wallAtStart.update();
            tempWall.update();
        }
    }

    private void moveWallEndPoint(TempWall tempWall, float xEnd, float yEnd) {
        float oldXEnd = tempWall.getWallShape().getXEnd();
        float oldYEnd = tempWall.getWallShape().getYEnd();
        tempWall.getWallShape().setXEnd(xEnd);
        tempWall.getWallShape().setYEnd(yEnd);
        TempWall postWall = tempWall.getPostWall();
        if (postWall != null) {
            if (postWall.getPreWall() == tempWall
                    && (postWall.getPostWall() != tempWall
                    || (postWall.getWallShape().getXStart() == oldXEnd
                    && postWall.getWallShape().getYStart() == oldYEnd))) {
                postWall.getWallShape().setXStart(xEnd);
                postWall.getWallShape().setYStart(yEnd);
            } else if (postWall.getPostWall() == tempWall
                    && (postWall.getPreWall() != tempWall
                    || (postWall.getWallShape().getXEnd() == oldXEnd
                    && postWall.getWallShape().getYEnd() == oldYEnd))) {
                postWall.getWallShape().setXEnd(xEnd);
                postWall.getWallShape().setYEnd(yEnd);
            }
            tempWall.update();
            postWall.update();
        }
    }


    public void reset() {
        xStart = Float.POSITIVE_INFINITY;
        yStart = Float.POSITIVE_INFINITY;
        if (wallModel != null) {
            wallManager.removeWallModel(wallModel);
            this.wallModel = null;
        }
        auxiliaryObject.setVisible(false);
    }

}
