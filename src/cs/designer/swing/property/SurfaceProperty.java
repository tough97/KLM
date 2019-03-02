package cs.designer.swing.property;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Floor;
import com.klm.cons.impl.Surface3D;
import com.klm.util.CSUtilException;
import com.klm.util.RealNumberOperator;
import cs.designer.module.TempSubSurface;
import cs.designer.swing.ToolbarPanel;
import cs.designer.swing.icons.IconManager;
import cs.designer.swing.tool.SubSurfaceGenerator;
import cs.designer.swing.ui.CoButton;
import cs.designer.swing.ui.SwingTool;
import cs.designer.view.controller.DisplayControlable;
import cs.designer.view.controller.SubSurfaceControler;
import cs.designer.view.viewer.DisplayView;

import javax.swing.*;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 12/31/11
 * Time: 9:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class SurfaceProperty extends Property implements ActionListener {


    private JTextField lengthValues;
    private JTextField widthValues;
    private JSpinner depthValues;
    private JButton toHoleBtn;
    private JButton toSubSurfaceBtn;
    private SubSurfaceControler controler;


    public SurfaceProperty(JPanel parentPanel) {
        super(parentPanel);
        init();

    }

    private void init() {
        final JLabel lengthLable = new JLabel("长:");
        lengthValues = new JTextField(4);
        final JLabel widthLable = new JLabel("宽:");
        widthValues = new JTextField(4);
        final JLabel depthLable = new JLabel("厚:");
        depthValues = new JSpinner();
        toHoleBtn = new JButton("挖 洞");
        lengthValues.setEditable(false);
        widthValues.setEditable(false);
        toSubSurfaceBtn = new JButton("分 割");
        final JPanel sizepPanel = new JPanel();
        sizepPanel.setBackground(SwingTool.THEME_COLOR);
        sizepPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        sizepPanel.add(createSubSizePanel(lengthLable, lengthValues));
        sizepPanel.add(createSubSizePanel(widthLable, widthValues));
        sizepPanel.add(createSubSizePanel(depthLable, depthValues));
        lengthValues.setHorizontalAlignment(JTextField.CENTER);
        widthValues.setHorizontalAlignment(JTextField.CENTER);
        add(sizepPanel);
        add(toSubSurfaceBtn);
        add(toHoleBtn);
        toHoleBtn.addActionListener(this);
        toSubSurfaceBtn.addActionListener(this);
    }

    private JPanel createSubSizePanel(final JLabel titleLabel,
                                      final Component component) {
        final JPanel subSizePanel = new JPanel();
        subSizePanel.setBackground(SwingTool.THEME_COLOR);
        titleLabel.setOpaque(false);
        subSizePanel.add(titleLabel);
        subSizePanel.add(component);
        final JLabel uintName = new JLabel("m");
        uintName.setOpaque(false);
        subSizePanel.add(uintName);
        return subSizePanel;
    }

    @Override
    public void clear() {

    }

    @Override
    public void setPerty(final Object object) {
        try {
            if (object instanceof TempSubSurface) {
                displayTool(true);
                final TempSubSurface tempSubSurfac = (TempSubSurface) object;
                Point3d toParentPoint = tempSubSurfac.getBaseSurface().getCenterToParent();
                if (toParentPoint != null) {
                    final SpinnerNumberModel depthValuesModel = new SpinnerNumberModel();
                    depthValuesModel.setMinimum(-Math.abs(tempSubSurfac.getBaseSurface().getCenterToParent().z));
                    depthValuesModel.setStepSize(Math.abs(tempSubSurfac.getBaseSurface().getCenterToParent().z) / 4);
                    depthValuesModel.setValue(tempSubSurfac.getDepth());
                    depthValues.setModel(depthValuesModel);
                    lengthValues.setText(String.valueOf(RealNumberOperator.roundNumber(tempSubSurfac.getLingth(), 2)));
                    widthValues.setText(String.valueOf(RealNumberOperator.roundNumber(tempSubSurfac.getwidth(), 2)));
                }

            } else if (object instanceof Surface3D) {
                displayTool(false);
                Surface3D surface = (Surface3D) object;
                Surface3D parentSurface = surface.getParentSurface();
                Point3d toParentPoint = surface.getCenterToParent();
                double parentZAxis = parentSurface == null ? 0 : surface.calculateSurfaceNormal().getZ();
                parentZAxis = parentZAxis * toParentPoint.z == 0 ? 0 : parentZAxis * toParentPoint.z;
                depthValues.setValue(parentZAxis);
                Rectangle2D identifiedShapeBounds = surface.getIdentifiedShape().getBounds2D();
                lengthValues.setText(String.valueOf(RealNumberOperator
                        .roundNumber(identifiedShapeBounds.getWidth(), 2)));
                widthValues.setText(String.valueOf(RealNumberOperator.roundNumber(identifiedShapeBounds.getHeight(), 2)));

            }


        } catch (CSHouseException e) {
            e.printStackTrace();
        }
    }

    private void displayTool(boolean displayable) {
        toHoleBtn.setVisible(displayable);
        toSubSurfaceBtn.setVisible(displayable);


        lengthValues.setEditable(false);
        widthValues.setEditable(false);
        depthValues.setEnabled(displayable);

    }

    @Override
    public void setModifyControler(final DisplayControlable controler) {
        this.controler = (SubSurfaceControler) controler;
    }

    public void actionPerformed(ActionEvent event) {
        try {
            if (event.getSource() == toSubSurfaceBtn) {
                final SubSurfaceGenerator generator =
                        ((SubSurfaceGenerator) (this.controler.getGenerator()));
                if (generator != null) {
                    float depth = Float.valueOf(depthValues.getValue().toString());
                    generator.getCurrentTempSubSurface().setDepth(depth);
                    generator.toSubSurface();
                    controler.setOperType(SubSurfaceControler.SubSurfaceOperType.DRAW);
                      this.controler.getView().setOperateType(DisplayView.OperateType.DEFAULT);
                }

            } else if (event.getSource() == toHoleBtn) {
                ((SubSurfaceGenerator) (this.controler.getGenerator())).toHole();
                this.controler.getView().setOperateType(DisplayView.OperateType.DEFAULT);
            }
        } catch (CSHouseException e) {
            e.printStackTrace();
        } catch (CSUtilException e) {
            e.printStackTrace();
        }

    }


}