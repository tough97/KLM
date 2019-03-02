/**
 * Copyright (c) 2011 co-soft. All Rights Reserved.
 */
package cs.designer.swing.property;

import com.klm.cons.impl.Room;
import com.klm.cons.impl.Surface3D;
import com.klm.cons.impl.Wall;
import com.klm.persist.impl.Furniture;
import cs.designer.module.Pan;
import cs.designer.module.TempSubSurface;
import cs.designer.module.WallModel;
import cs.designer.swing.ui.SwingTool;
import cs.designer.view.controller.*;
import cs.designer.view.viewer.HousePlanView;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Housing properties panel
 *
 * @author rongyang
 * @version Oct-18-2011 1.0.0.0
 */
public class PropertyPanel extends JPanel {
    private Property surfaceProperty;
    private Property wallPropertyPanel;
    private Property panProperty;
    private Property roomProperty;
    private Property housePlanViewProperty;
    private Property furnitureProperty;

    public PropertyPanel() {
        init();
    }

    private void init() {
        setPreferredSize(new Dimension(0,30));
        surfaceProperty = new SurfaceProperty(this);
        wallPropertyPanel = new WallProperty(this);
        roomProperty = new RoomProperty(this);
        panProperty = new PanProperty(this);
        housePlanViewProperty = new HouseViewProperty(this);
        furnitureProperty = new FurnitureProperty(this);
        //
        final LayoutManager propertyLayout = new FlowLayout(FlowLayout.CENTER, 10, 0);
        surfaceProperty.setLayout(propertyLayout);
        wallPropertyPanel.setLayout(propertyLayout);
        roomProperty.setLayout(propertyLayout);
        panProperty.setLayout(propertyLayout);
        housePlanViewProperty.setLayout(propertyLayout);
        furnitureProperty.setLayout(propertyLayout);
        setLayout(new BorderLayout());
        setBackground(SwingTool.THEME_COLOR);

    }

    public void setPropertys(final Object object) {
        if (object == null) {
            removeAll();
            updateUI();
        } else if (object instanceof WallModel ||
                object instanceof Wall) {
            setPropertys(wallPropertyPanel, object);
        } else if (object instanceof Room) {
            setPropertys(roomProperty, object);
        } else if (object instanceof Pan) {
            setPropertys(panProperty, object);
        } else if (object instanceof TempSubSurface ||
                object instanceof Surface3D) {
            setPropertys(surfaceProperty, object);
        } else if (object instanceof HousePlanView) {
            setPropertys(housePlanViewProperty, object);
        } else if (object instanceof Furniture) {
            setPropertys(furnitureProperty, object);
        }

    }

    private void setPropertys(final Property property,
                              final Object object) {
        property.display();
        property.setPerty(object);
        property.setBackground(SwingTool.THEME_COLOR);
    }

    public void setModifyControler(final DisplayControlable controler) {
        if (controler instanceof SubSurfaceControler) {
            surfaceProperty.setModifyControler(controler);
        } else if (controler instanceof PlanControler) {
            wallPropertyPanel.setModifyControler(controler);
        } else if (controler instanceof ViewControler) {
            housePlanViewProperty.setModifyControler(controler);

        } else if (controler instanceof ViewControlable) {
            housePlanViewProperty.setModifyControler(controler);
        }
    }
}
