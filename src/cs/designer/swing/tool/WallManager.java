package cs.designer.swing.tool;


import com.klm.cons.impl.CSHouseException;
import com.klm.util.CSUtilException;
import cs.designer.module.TempWall;
import cs.designer.module.WallModel;
import cs.designer.swing.undo.HouseEdit;
import cs.designer.swing.undo.WallInUndoEditor;
import cs.designer.utils.ComputeUtill;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import java.awt.*;
import java.util.HashSet;

public class WallManager {
    private HousePlanView view;

    public WallManager(DisplayView view) {
        this.view = (HousePlanView) view;


    }

    protected void addWall(final TempWall tempWal) throws CSHouseException, CSUtilException {
        if (tempWal.getWallShape().getLength() > 0.1f) {
            this.view.getCurrentFloor().addWall(tempWal.getWall());
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        splitWall(tempWal, 1E-3F);
                    } catch (CSHouseException e) {
                        e.printStackTrace();
                    } catch (CSUtilException e) {
                        e.printStackTrace();
                    }
                }
            });

            HouseEdit.getHouseEditor().joinObject(new WallInUndoEditor(this.view.getCurrentFloor(), tempWal));
        }
    }

    public void splitWall(TempWall newWall, float limint)
            throws CSHouseException, CSUtilException {
        for (TempWall wall : new HashSet<TempWall>(TempWall.walls.values())) {
            if (newWall != wall
                    && newWall.getPostWall() != wall
                    && newWall.getPreWall() != wall
                    && wall.getCurrentFloor().containsWall(wall.getWall())) {
                float[] intersectionPoint =
                        ComputeUtill.computeIntersection(wall.getWallShape().getStartPoint(), wall.getWallShape().getEndPoint(),
                                newWall.getWallShape().getStartPoint(), newWall.getWallShape().getEndPoint());
                if (intersectionPoint != null
                        && newWall.getWallShape().containsWallBaseAt(intersectionPoint[0],
                        intersectionPoint[1], limint)
                        && wall.getWallShape().containsWallBaseAt(intersectionPoint[0],
                        intersectionPoint[1], limint)) {
                    if (wall.getWallShape().containsdPoint(intersectionPoint[0], intersectionPoint[1], limint)
                            && !wall.getWallShape().containsWallStartAt(intersectionPoint[0], intersectionPoint[1], limint)
                            && !wall.getWallShape().containsWallEndAt(intersectionPoint[0], intersectionPoint[1], limint)) {
                        wall.setSplitWall(intersectionPoint);
                    }
                    if (newWall.getWallShape().containsdPoint(intersectionPoint[0], intersectionPoint[1], limint)
                            && !newWall.getWallShape().containsWallStartAt(intersectionPoint[0], intersectionPoint[1], limint)
                            && !newWall.getWallShape().containsWallEndAt(intersectionPoint[0], intersectionPoint[1], limint)) {
                        newWall.setSplitWall(intersectionPoint);

                    }


                }

            }
        }
    }

    protected void removeWallModel(final WallModel wallModel) {
        this.view.getCurrentFloor().getFloorTrans().removeChild(wallModel);
    }

    protected void addWallModel(final WallModel wallModel) {
        this.view.getCurrentFloor().getFloorTrans().addChild(wallModel);
    }


}