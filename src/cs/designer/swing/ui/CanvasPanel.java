/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.designer.swing.ui;

import cs.designer.swing.property.PropertyPanel;
import cs.designer.view.viewer.HousePlanView;

import javax.swing.*;
import java.awt.*;


/**
 * @author rongyang
 */
public class CanvasPanel extends JPanel {

    private HousePlanView planview;
    private PropertyPanel propertyPanel;

    public CanvasPanel() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        propertyPanel = new PropertyPanel();
        planview = new HousePlanView(propertyPanel);
        add(BorderLayout.CENTER, planview);
        add(BorderLayout.SOUTH, propertyPanel);
        propertyPanel.setPropertys(planview);
    }

    public HousePlanView getPlanview() {
        return planview;
    }

    public PropertyPanel getPropertyPanel() {
        return propertyPanel;
    }
}
