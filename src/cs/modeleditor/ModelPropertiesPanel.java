package cs.modeleditor;

import com.klm.persist.CSPersistException;
import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.SurfaceMaterial;
import com.klm.persist.meta.BufferedImageMeta;
import cs.designer.swing.tool.VerticalLayout;
import cs.designer.view.viewer.MerchandiseEditorView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 11/26/11
 * Time: 1:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModelPropertiesPanel extends JPanel implements ChangeListener, ActionListener {
    public final static double MAX_VALUES = 100.0f;
    public final static double MINI_VALUES = 0f;
    private JSpinner lengtValue;
    private JSpinner thicknessValue;
    private JSpinner heightValue;
    private JRadioButton installationBtn;
    private JTextField nameValue;
    private JTextField uintNameValue;
    private JCheckBox isBorder;
    private double scale = 1;
    private MerchandiseEditorView view;


    public ModelPropertiesPanel(final MerchandiseEditorView view) {
        setLayout(new VerticalLayout(5));
        this.view = view;
        init();
    }

    private void init() {
        final JPanel sizePropertiespPanel = new JPanel();
        sizePropertiespPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        nameValue = new JTextField(10);
        uintNameValue = new JTextField(10);
        installationBtn = new JRadioButton("安装");
        lengtValue = new JSpinner(new SpinnerNumberModel(0, MINI_VALUES, MAX_VALUES, 0.1));
        thicknessValue = new JSpinner(new SpinnerNumberModel(0, MINI_VALUES, MAX_VALUES, 0.1));
        heightValue = new JSpinner(new SpinnerNumberModel(0, MINI_VALUES, MAX_VALUES, 0.1));
        lengtValue.addChangeListener(this);
        heightValue.addChangeListener(this);
        thicknessValue.addChangeListener(this);
        sizePropertiespPanel.add(createValuesPanel(new JLabel("长度"), lengtValue));
        sizePropertiespPanel.add(createValuesPanel(new JLabel("厚度"), thicknessValue));
        sizePropertiespPanel.add(createValuesPanel(new JLabel("高度"), heightValue));
        add(sizePropertiespPanel);
        final JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(createValuesPanel(new JLabel("名称:"), nameValue));
        infoPanel.add(createValuesPanel(new JLabel("单位"), uintNameValue));
        final JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        commandPanel.add(installationBtn);
        isBorder = new JCheckBox("是否有边框");
        commandPanel.add(isBorder);
        isBorder.addActionListener(this);
        add(infoPanel);
        add(commandPanel);
        lengtValue.setEnabled(false);
        thicknessValue.setEnabled(false);
        heightValue.setEnabled(false);
        isBorder.setEnabled(false);
        installationBtn.setEnabled(false);
        nameValue.setEditable(false);
        uintNameValue.setEditable(false);

    }

    private JPanel createValuesPanel(final JLabel title,
                                     final JComponent values) {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(title);
        panel.add(values);
        return panel;

    }

    public void setPreferreds() {
        final Merchandise merchandise = view.getCurrentMerchandise();
        if (merchandise instanceof Furniture) {
            Furniture furniture = (Furniture) merchandise;
            lengtValue.setValue(furniture.getModel().getLength());
            heightValue.setValue(furniture.getModel().getHeight());
            thicknessValue.setValue(furniture.getModel().getWidth());
            lengtValue.setEnabled(true);
            thicknessValue.setEnabled(true);
            heightValue.setEnabled(true);
            installationBtn.setEnabled(true);
            uintNameValue.setText(merchandise.getUnitName());
            nameValue.setEditable(true);
            uintNameValue.setEditable(true);
        } else if (merchandise instanceof SurfaceMaterial) {
            final SurfaceMaterial surfaceMaterial = (SurfaceMaterial) merchandise;
            lengtValue.setValue(surfaceMaterial.getWidth());
            heightValue.setValue(surfaceMaterial.getHeight());
            uintNameValue.setText(merchandise.getUnitName());
            lengtValue.setEnabled(true);
            heightValue.setEnabled(true);
            isBorder.setEnabled(true);
            nameValue.setEditable(true);
            uintNameValue.setEditable(true);

        }

    }

    public void stateChanged(final ChangeEvent e) {
        final Object source = e.getSource();
        if (source == lengtValue) {
            if (view.getCurrentMerchandise() instanceof Furniture) {
                setScale(getLengtValue() / ((Furniture) view.getCurrentMerchandise()).getModel().getLength());
            } else if (view.getCurrentMerchandise() instanceof SurfaceMaterial) {
                setSurfaceMaterialSize(getLengtValue(), getHeightValue());
            }

        } else if (source == heightValue) {
            if (view.getCurrentMerchandise() instanceof Furniture) {
                setScale(getHeightValue() / ((Furniture) view.getCurrentMerchandise()).getModel().getHeight());
            } else if (view.getCurrentMerchandise() instanceof SurfaceMaterial) {
                setSurfaceMaterialSize(getLengtValue(), getHeightValue());
            }

        } else if (source == thicknessValue) {
            if (view.getCurrentMerchandise() instanceof Furniture) {
                setScale(getThicknessValue() / ((Furniture) view.getCurrentMerchandise()).getModel().getWidth());
            }

        }
    }

    private void setSurfaceMaterialSize(double width, double height) {
        if (view.getCurrentMerchandise() instanceof SurfaceMaterial) {
            final SurfaceMaterial surfaceMaterial = (SurfaceMaterial) view.getCurrentMerchandise();
            try {
                view.setCurrentMerchandise(new SurfaceMaterial("display", width, height, false,
                        new BufferedImageMeta(surfaceMaterial.getBufferredImage())));
            } catch (CSPersistException e) {
                e.printStackTrace();
            }
        }

    }

    public double getLengtValue() {
        return Double.valueOf(lengtValue.getModel().
                getValue().toString());
    }

    public double getThicknessValue() {
        return Double.valueOf(thicknessValue.getModel().getValue().toString());
    }

    public double getHeightValue() {
        return Double.valueOf(heightValue.getModel().getValue().toString());
    }

    public boolean installationable() {
        return installationBtn.isSelected();
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        System.out.println("Scale = " + scale);
        this.scale = scale;
        if (scale != 1) {
            if (view.getCurrentMerchandise() instanceof Furniture) {
                lengtValue.getModel().setValue(((Furniture) view.getCurrentMerchandise()).getModel().getLength() * scale);
                thicknessValue.getModel().setValue(((Furniture) view.getCurrentMerchandise()).getModel().getWidth() * scale);
                heightValue.getModel().setValue(((Furniture) view.getCurrentMerchandise()).getModel().getHeight() * scale);
            }
        }
    }

    public void rest() {
        scale = 1;
    }

    public String getNameValue() {
        return nameValue.getText();
    }

    public String getUintName() {
        return uintNameValue.getText();
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == isBorder) {
            setImageBorder(isBorder.isSelected());
        }
    }

    private void setImageBorder(boolean isBorderable) {
        if (view.getCurrentMerchandise() instanceof SurfaceMaterial) {
            SurfaceMaterial currentMerMaterial = (SurfaceMaterial) view.getCurrentMerchandise();
            final BufferedImage image = currentMerMaterial.getBufferredImage();
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            final BufferedImage tempImage = new BufferedImage(width,
                    height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = tempImage.createGraphics();
            if (isBorderable) {
                graphics2D.setPaint(Color.WHITE);
                graphics2D.fill(new Rectangle2D.Double(0, 0, width,
                        height));
                graphics2D.drawImage(image, 2, 2, width - 2, height - 2, null);
            } else {
                graphics2D.drawImage(image, -2, -2, width + 2, height + 2, null);
            }
            try {
                currentMerMaterial.setImage(new BufferedImageMeta(tempImage), getLengtValue(), getHeightValue());
                view.setCurrentMerchandise(currentMerMaterial);
            } catch (CSPersistException e) {
                e.printStackTrace();
            }

        }

    }
}
