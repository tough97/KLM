package cs.designer.swing.property;


import com.klm.cons.impl.Floor;
import cs.designer.view.controller.DisplayControlable;

import javax.swing.*;
import java.awt.*;

public abstract class Property extends JPanel {
    private JPanel parentPanel;

    public Property(JPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public abstract void clear();

    public abstract void setPerty(Object object);

    public abstract void setModifyControler(DisplayControlable controler);

    public void display() {
        parentPanel.removeAll();
        parentPanel.add(this);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                parentPanel.updateUI();
            }
        });


    }

}
