package cs.designer.swing.property;


import cs.designer.module.Pan;
import cs.designer.view.controller.DisplayControlable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanProperty extends Property implements ActionListener, ChangeListener {
    private JButton lineButton;
    private JButton rectangleButton;
    private JButton ellipseButton;
    private JButton polygonButton;
    private JLabel thicknessLabel;
    private JSpinner thicknessValues;
    private JSpinner polygonSideNmbValue;
    private JRadioButton magnetismBtn;

    public PanProperty(JPanel parentPanel) {
        super(parentPanel);
        init();
    }

    private void init() {
               setLayout(new FlowLayout(FlowLayout.CENTER));
        lineButton = new JButton("直线");
        rectangleButton = new JButton("矩形");
        ellipseButton = new JButton("圆形");
        polygonButton = new JButton("多边形");
        thicknessLabel = new JLabel("宽度:");
        magnetismBtn = new JRadioButton("自动对齐");
        final SpinnerNumberModel tihcknessModel = new SpinnerNumberModel();
        tihcknessModel.setMaximum(Pan.MAX_THICKNESS);
        tihcknessModel.setMinimum(Pan.MINI_THICKNESS);
        tihcknessModel.setStepSize(0.1f);
        tihcknessModel.setValue(Pan.getPan().getThickness());
        //
        final SpinnerNumberModel polygonSideNmbValueModel = new SpinnerNumberModel();
        polygonSideNmbValueModel.setMaximum(12);
        polygonSideNmbValueModel.setMinimum(4);
        polygonSideNmbValueModel.setStepSize(1);
        polygonSideNmbValueModel.setValue(Pan.getPan().getPolygonSideNmb());
        polygonSideNmbValue = new JSpinner(polygonSideNmbValueModel);
        //
        thicknessValues = new JSpinner(tihcknessModel);
        lineButton.addActionListener(this);
        rectangleButton.addActionListener(this);
        ellipseButton.addActionListener(this);
        polygonButton.addActionListener(this);
        polygonSideNmbValue.addChangeListener(this);
        thicknessValues.addChangeListener(this);
        magnetismBtn.addChangeListener(this);
        final ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(lineButton);
        buttonGroup.add(rectangleButton);
        buttonGroup.add(ellipseButton);
        buttonGroup.add(polygonButton);
        add(lineButton);
        add(rectangleButton);
        add(ellipseButton);
        add(polygonButton);
        add(polygonSideNmbValue);
        add(thicknessLabel);
        add(thicknessValues);
        add(magnetismBtn);

    }

    @Override
    public void clear() {
    }


    public void setPerty(Object object) {
        if (object instanceof Pan) {
            Pan pan = (Pan) object;
            thicknessValues.setValue(Pan.getPan().getThickness());
            magnetismBtn.setSelected(pan.isMagnetismEnabled());

        }

    }

    @Override
    public void setModifyControler(DisplayControlable controler) {

    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == lineButton) {
            Pan.getPan().setPanType(Pan.PanType.LINE);

        } else if (source == rectangleButton) {
            Pan.getPan().setPanType(Pan.PanType.RECTAMGLE);

        } else if (source == ellipseButton) {
            Pan.getPan().setPanType(Pan.PanType.ELLIPSE);

        } else if (source == polygonButton) {
            Pan.getPan().setPanType(Pan.PanType.POLYGON);

        }

    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == thicknessValues) {
            Pan.getPan().setThickness(Float.valueOf(thicknessValues.getValue().toString()));
        } else if (e.getSource() == magnetismBtn) {
            Pan.getPan().setMagnetismEnabled(magnetismBtn.isSelected());
        } if (e.getSource() == polygonSideNmbValue) {
             Pan.getPan().setPolygonSideNmb(Integer.valueOf(polygonSideNmbValue.getValue().toString()));
        }

    }
}
