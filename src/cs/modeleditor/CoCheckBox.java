package cs.modeleditor;

import cs.designer.swing.bean.CoBean;
import cs.designer.swing.bean.HouseBean;
import cs.designer.swing.bean.MerchandiseBean;
import cs.designer.swing.list.ImagePanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class CoCheckBox extends JCheckBox
        implements ChangeListener, MouseListener {
    private ImagePanel imagePanel;
    private CoBean bean;

    public CoCheckBox() {
        super();
        init();
    }

    public CoCheckBox(final CoBean bean) {
        super();
        init();
        setBean(bean);
    }

    private void init() {
        setBorder(null);
        imagePanel = new ImagePanel();
        setLayout(new GridLayout(0, 1, 0, 0));
        add(imagePanel);
        addChangeListener(this);
        addMouseListener(this);
        setToolTipText("");
        setOpaque(false);
        setBackground(Color.WHITE);
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(215, 213, 213), 1));

    }

    public void setBean(final CoBean bean) {
        if (bean instanceof MerchandiseBean) {
            imagePanel.setImage(((MerchandiseBean) bean).
                    getSneapView());
        } else if (bean instanceof HouseBean) {
            imagePanel.setImage(((HouseBean) bean).
                    getSneapView());

        }
        createDragsource(bean);
        this.bean = bean;

    }

    public void stateChanged(ChangeEvent ce) {
        CoCheckBox cb = (CoCheckBox) ce.getSource();
        if (cb.isSelected() == true) {
            imagePanel.setBorder(null);
        }
    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mousePressed(MouseEvent me) {


    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 2));
    }

    public void mouseExited(MouseEvent me) {
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(215, 213, 213), 1));
    }


    private void createDragsource(final CoBean bean) {
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
                    public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
                        CoBeanTransferable merchandisTransferable =
                                new CoBeanTransferable(bean);
                        dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, merchandisTransferable);

                    }
                });
    }



    public CoBean getBean() {
        return bean;
    }
}