package cs.designer.swing.property;

import com.klm.cons.impl.Floor;
import com.klm.cons.impl.Wall;
import com.klm.util.RealNumberOperator;
import cs.designer.module.WallModel;
import cs.designer.view.controller.DisplayControlable;

import javax.swing.*;
import java.awt.*;


public class WallProperty extends Property {
    private JTextField lengthField;
    private JLabel lengthName;
    //
    private JTextField thicknessField;
    private JLabel thicknessName;
    //
    private JTextField heightField;
    private JLabel heightName;


    public WallProperty(JPanel parentPanel) {
        super(parentPanel);
        init();
    }

    private void init() {

        lengthName = new JLabel(" 长度:");
        lengthField = new JTextField(5);
        thicknessName = new JLabel("厚度:");
        thicknessField = new JTextField(5);
        heightName = new JLabel("高度:");
        heightField = new JTextField(5);
        lengthField.setHorizontalAlignment(JTextField.CENTER);
        thicknessField.setHorizontalAlignment(JTextField.CENTER);
        heightField.setHorizontalAlignment(JTextField.CENTER);
        add(lengthName);
        add(lengthField);
        add(thicknessName);
        add(thicknessField);
        add(heightName);
        add(heightField);
        lengthField.setEditable(false);
        thicknessField.setEditable(false);
        heightField.setEditable(false);
    }

    @Override
    public void clear() {
        lengthField.setText("");
        thicknessField.setText("");
        heightField.setText("");
    }

    @Override
    public void setPerty(Object object) {
        if (object instanceof WallModel) {
            final WallModel wall = (WallModel) object;
            lengthField.setText(String.valueOf(RealNumberOperator.
                    roundNumber(wall.getWallShape().getLength(), 2)));
            thicknessField.setText(String.valueOf(wall.getWallShape().getThickness()));
            heightField.setText(String.valueOf(Wall.getWallHeight()));
        } else if (object instanceof Wall) {
            final Wall wall = (Wall) object;
            lengthField.setText(String.valueOf(RealNumberOperator.
                    roundNumber(wall.getWallShape().getLength(), 2)));
            thicknessField.setText(String.valueOf(wall.getWallShape().getThickness()));
            heightField.setText(String.valueOf(Wall.getWallHeight()));

        }

    }

    @Override
    public void setModifyControler(final DisplayControlable controler) {

    }
}
