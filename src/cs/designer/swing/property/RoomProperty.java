package cs.designer.swing.property;


import com.klm.cons.impl.Floor;
import com.klm.cons.impl.Room;
import com.klm.util.RealNumberOperator;
import com.nilo.plaf.nimrod.NimRODTextFieldUI;
import cs.designer.view.controller.DisplayControlable;

import javax.swing.*;
import javax.swing.plaf.TextUI;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class RoomProperty extends Property implements FocusListener {
    private JLabel nameLabel;
    private JTextField nameValues;
    private JLabel sizeLabel;
    private JTextField sizeValues;
    private Room currentRoom;
    private JButton reName;

    public RoomProperty(JPanel parentPanel) {
        super(parentPanel);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                init();
            }
        });
    }

    private void init() {
        nameLabel = new JLabel("房间名称:");
        nameValues = new JTextField(5);
        sizeLabel = new JLabel("面积:");
        sizeValues = new JTextField(5);
        reName=new JButton("修改");
        add(nameLabel);
        add(nameValues);
        add(reName);
        add(sizeLabel);
        add(sizeValues);
        nameValues.addFocusListener(this);
        sizeValues.setEditable(false);
        sizeValues.setHorizontalAlignment(JTextField.CENTER);
        nameValues.setHorizontalAlignment(JTextField.CENTER);
    }

    @Override
    public void clear() {
        nameValues.setText("");
        sizeValues.setText("");
    }

    @Override
    public void setPerty(Object object) {
        if (object instanceof Room) {
            final Room room = (Room) object;
            nameValues.setText(room.getRoomName());
            sizeValues.setText(String.valueOf(RealNumberOperator.roundNumber(room.getCeilingDown().getBoundArea(), 2)));
            currentRoom = room;
        }
    }

    @Override
    public void setModifyControler(DisplayControlable controler) {

    }

    public void focusGained(FocusEvent focusEvent) {
        if (focusEvent.getSource() == nameValues) {
            currentRoom.setRoomName(nameValues.getText());
        }
    }

    public void focusLost(FocusEvent focusEvent) {
        if (focusEvent.getSource() == nameValues) {
            currentRoom.setRoomName(nameValues.getText());
        }
    }
}
