package cs.designer.swing.property;

import com.klm.cons.impl.CSHouseException;
import com.klm.persist.impl.Furniture;
import cs.designer.swing.undo.FurnitureInstallUndoEditor;
import cs.designer.swing.undo.HouseEdit;
import cs.designer.view.controller.DisplayControlable;

import javax.media.j3d.Transform3D;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/7/12
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class FurnitureProperty extends Property implements ActionListener {
    private JLabel nameValue;
    private JLabel lengtValue;
    private JLabel thicknessValue;
    private JLabel heightValue;
    private JButton installBtn;
    private JButton rotateWiseBtn;
    private JButton rotateAntiBtn;

//    private CoLable detailedInfoCommand;
    private Furniture furniture;


    public enum RotateType {
        rotateWise, rotateAnti
    }

    public FurnitureProperty(final JPanel parentPanel) {
        super(parentPanel);
        init();
    }

    private void init() {
        installBtn = new JButton("安装");
//        detailedInfoCommand = new CoLable("商品信息");
//        detailedInfoCommand.addActionListener(this);
        installBtn.addActionListener(this);
        rotateWiseBtn = new JButton("左旋转");
//        rotateWiseBtn.setRolloverIcon(new ImageIcon(IconManager.getIconUrl("rotateUp_rollover.png")));
        rotateAntiBtn = new JButton("右旋转");
//        rotateAntiBtn.setRolloverIcon(new ImageIcon(IconManager.getIconUrl("rotateDown_rollover.png")));
        rotateWiseBtn.addActionListener(this);
        rotateAntiBtn.addActionListener(this);
        nameValue = new JLabel();
        lengtValue = new JLabel();
        thicknessValue = new JLabel();
        heightValue = new JLabel();
        add(new JLabel("名 称:"));
        add(nameValue);
        add(createSizePanel());
        add(rotateWiseBtn);
        add(rotateAntiBtn);
        add(installBtn);
//        add(detailedInfoCommand);

    }

    private JPanel createSizePanel() {
        final JPanel sizePropertiespPanel = new JPanel();
        sizePropertiespPanel.add(new JLabel("长 度:"));
        sizePropertiespPanel.add(lengtValue);
        sizePropertiespPanel.add(new JLabel("厚 度:"));
        sizePropertiespPanel.add(thicknessValue);
        sizePropertiespPanel.add(new JLabel("高 度:"));
        sizePropertiespPanel.add(heightValue);
        JSeparator sepv = new JSeparator();
        sepv.setOrientation(JSeparator.VERTICAL);
        sizePropertiespPanel.add(sepv);
        sizePropertiespPanel.setOpaque(false);
        return sizePropertiespPanel;
    }

    @Override
    public void clear() {
    }

    @Override
    public void setPerty(Object object) {
        if (object instanceof Furniture) {
            furniture = (Furniture) object;
            nameValue.setText(furniture.getName());
            lengtValue.setText(String.valueOf(furniture.getModel().getLength()));
            thicknessValue.setText(String.valueOf(furniture.getModel().getWidth()));
            heightValue.setText(String.valueOf(furniture.getModel().getHeight()));
            installBtn.setEnabled(furniture.getModel().getIdentifiedInstallCoords() != null);

        }
    }

    @Override
    public void setModifyControler(DisplayControlable controler) {

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == installBtn) {
            try {
                HouseEdit.getHouseEditor().joinObject(new FurnitureInstallUndoEditor(furniture));
                furniture.install();
                furniture.getModel().hideOutLines();
            } catch (CSHouseException e1) {
                e1.printStackTrace();
            }
        }
//        else if (e.getSource() == detailedInfoCommand) {
//            if (furniture != null) {
////                MerchandiseInfoDialog.show(SwingUtilities.getWindowAncestor(this), furniture,);
//            }
//        }
        else if (e.getSource() == rotateAntiBtn) {
            calculateRotate(RotateType.rotateAnti);
        } else if (e.getSource() == rotateWiseBtn) {
            calculateRotate(RotateType.rotateWise);
        }
    }

    private void calculateRotate(final RotateType type) {
        if (furniture == null || furniture.isInstalled()) {
            return;
        }
        final Transform3D currentRotateTransform3D = furniture.getRotationOnParent();
        final Transform3D rotate = new Transform3D();
        switch (type) {
            case rotateWise:
                rotate.rotZ(Math.PI / 4);
                break;
            case rotateAnti:
                rotate.rotZ(-Math.PI / 4);
                break;
            default:
                break;
        }
        rotate.mul(currentRotateTransform3D);
        furniture.setRotationOnParent(rotate);
    }

}

