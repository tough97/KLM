package cs.designer.swing.ui;


import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

public class CoButton extends JButton {
    public final static int DEFAULT_ICON = 0;
    public final static int ROLLOVER_ICON = 1;
    public final static int SELECT_ICON = 2;


    public CoButton(final ImageIcon[] icons, final String text) {
        super(text);
        setForeground(new Color(99, 12, 12));
        setIcon(icons[DEFAULT_ICON]);
        final Icon selectIcon = icons[SELECT_ICON] == null
                ? icons[DEFAULT_ICON] : icons[SELECT_ICON];
        final Icon rolloverIcon = icons[ROLLOVER_ICON] == null
                ? icons[DEFAULT_ICON] : icons[ROLLOVER_ICON];
        setSelectedIcon(selectIcon);
        setRolloverIcon(rolloverIcon);
        setIconTextGap(-(int) (getPreferredSize().getWidth() - 4) / 2);
    }

    public CoButton(final String text, final ImageIcon icon) {
        super(text, icon);
        setContentAreaFilled(false);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setBorder(null);
    }

    public CoButton(final Icon[] icons) {
        setContentAreaFilled(false);
        setBorder(null);
        setIcon(icons[DEFAULT_ICON]);
        final Icon selectIcon = icons[SELECT_ICON];
        final Icon rolloverIcon = icons[ROLLOVER_ICON] == null
                ? icons[DEFAULT_ICON] : icons[ROLLOVER_ICON];
        setSelectedIcon(selectIcon);
        setRolloverIcon(rolloverIcon);
        setMargin(new Insets(0, 0, 0, 0));


    }

    public CoButton(final Icon icon) {
        super(icon);
        setContentAreaFilled(false);
        setBorder(null);
        setMargin(new Insets(0, 0, 0, 0));

    }

    public CoButton(final ImageIcon icon, final String text, final int leftGap) {
        super(text);
        setForeground(new Color(99, 12, 12));
        setFocusPainted(false);
        setIcon(icon);
        setIconTextGap(-(int) (getPreferredSize().getWidth() - 4) / 2 + leftGap);
        setBorder(null);
        setMargin(new Insets(0, 0, 0, 0));

    }

    public CoButton(String text) {
        super(text);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setContentAreaFilled(false);
        setBorder(null);
        setIconTextGap(-(int) (getPreferredSize().getWidth() - 4) / 2);
    }
}
