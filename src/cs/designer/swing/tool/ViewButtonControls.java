package cs.designer.swing.tool;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/7/12
 * Time: 12:54 PM
 * To change this template use File | Settings | File Templates.
 */

import cs.designer.swing.icons.IconManager;
import cs.designer.view.controller.DisplayControlable;
import cs.designer.view.controller.ViewControler;
import cs.designer.view.controller.ViewControlable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

public class ViewButtonControls extends JPanel {
    private ViewControlable controlor;

    public enum MoveType {
        STILL, MOVING_UP, MOVING_DOWN,
        MOVING_LEFT, MOVING_RIGHT, MOVING_RESET
    }

    public final static float MOVE_VIEW_UINT = 0.5f;
    public final static Vector3d MOVE_LEFT = new Vector3d(-MOVE_VIEW_UINT, 0, 0);
    public final static Vector3d MOVE_RIGHT = new Vector3d(MOVE_VIEW_UINT, 0, 0);
    public final static Vector3d MOVE_DOWN = new Vector3d(0, 0, -MOVE_VIEW_UINT);
    public final static Vector3d MOVE_UP = new Vector3d(0, 0, MOVE_VIEW_UINT);

    public ViewButtonControls() {
        createCotrolorButtons();
    }

    public ViewButtonControls(DisplayControlable controlor) {
        this.controlor = (ViewControler) controlor;
        createCotrolorButtons();

    }

    private void createCotrolorButtons() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 25, 0));
        final JPanel xPenPanel = new JPanel();
        xPenPanel.setLayout(new VerticalLayout());
        xPenPanel.add(new NavigationButton(new ImageIcon(IconManager.getIconUrl("move_up.png")),
                MoveType.MOVING_UP));
        xPenPanel.add(new NavigationButton(new ImageIcon(IconManager.getIconUrl("move_down.png")),
                MoveType.MOVING_DOWN));
        final JPanel yPanel = new JPanel();
        yPanel.setLayout(new HorizontalLayout());
        yPanel.add(new NavigationButton(new ImageIcon(IconManager.getIconUrl("move_left.png")),
                MoveType.MOVING_LEFT));
        yPanel.add(new NavigationButton(new ImageIcon(IconManager.getIconUrl("move_right.png")),
                MoveType.MOVING_RIGHT));
        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new HorizontalLayout());
        centerPanel.add(new NavigationButton(new ImageIcon(IconManager.getIconUrl("reset.png")),
                MoveType.MOVING_RESET));

        add(xPenPanel);
        add(centerPanel);
        add(yPanel);

    }

    public void setControlor(ViewControlable controlor) {
        this.controlor = controlor;
    }

    private void calculateMotion(MoveType type) {
        if (controlor == null) {
            return;
        }
        switch (type) {
            case STILL:
                break;
            case MOVING_LEFT:
                controlor.moveLeft();
                break;
            case MOVING_RIGHT:
                controlor.moveRight();
                break;
            case MOVING_UP:
                controlor.moveUp();
                break;
            case MOVING_DOWN:
                controlor.moveDown();
                break;
            case MOVING_RESET:
                controlor.moveRest();
                break;
            default:
                throw (new RuntimeException("Unknown motion"));
        }
    }

    class NavigationButton extends JButton {
        private final MoveType moveType;

        public NavigationButton(Icon icon, final MoveType moveType) {
            super(icon);
            this.moveType = moveType;
            setPressedIcon(new ImageIcon(createImage(new FilteredImageSource(
                    ((ImageIcon) getIcon()).getImage().getSource(),
                    new RGBImageFilter() {
                        {
                            canFilterIndexColorModel = true;
                        }

                        public int filterRGB(int x, int y, int rgb) {
                            // Return darker color
                            int alpha = rgb & 0xFF000000;
                            int darkerRed = ((rgb & 0xFF0000) >> 1) & 0xFF0000;
                            int darkerGreen = ((rgb & 0x00FF00) >> 1) & 0x00FF00;
                            int darkerBlue = (rgb & 0x0000FF) >> 1;
                            return alpha | darkerRed | darkerGreen | darkerBlue;
                        }
                    }))));

            final Timer timer = new Timer(50, new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    calculateMotion(moveType);
                }
            });
            timer.setInitialDelay(0);

            addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent ev) {
                    if (getModel().isArmed()
                            && !timer.isRunning()) {
                        timer.restart();
                    } else if (!getModel().isArmed()
                            && timer.isRunning()) {
                        timer.stop();
                    }
                }
            });
            setFocusable(false);
            setBorder(null);
            setContentAreaFilled(false);

        }

        public MoveType getMoveType() {
            return moveType;
        }
    }
}
