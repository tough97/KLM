package cs.designer.swing.icons;


import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/3/12
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class IconManager {
    private final static boolean isDebug = false;
    public final static String EXTENSION_NAME = ".png";
    public final static String SELECTED_NAME = "selected";
    public final static String ROLLOVER_NAME = "rollover";
    public final static String CONNECTOR = "_";

    public static URL getIconUrl(final String iconFileName) {
        URL icon = IconManager.class.getResource(iconFileName);
        return icon;
    }

    public static Icon[] getRolloverIcons(String iconName) {

        Icon[] icons = new Icon[2];
        try {
            icons[0] = new ImageIcon(IconManager.getIconUrl(iconName + EXTENSION_NAME));
            icons[1] = new ImageIcon(IconManager.getIconUrl(iconName + CONNECTOR +
                    ROLLOVER_NAME +
                    EXTENSION_NAME));
        } catch (NullPointerException e) {

        }

        return icons;

    }

    public static Icon[] getSelectedIcons(String iconName) {

        Icon[] icons = new Icon[3];
        try {
            icons[0] = new ImageIcon(IconManager.getIconUrl(iconName + EXTENSION_NAME));
            icons[1] = new ImageIcon(IconManager.getIconUrl(iconName + CONNECTOR +
                    ROLLOVER_NAME +
                    EXTENSION_NAME));
            icons[2] = new ImageIcon(IconManager.getIconUrl(iconName + CONNECTOR +
                    SELECTED_NAME +
                    EXTENSION_NAME));
        } catch (NullPointerException e) {

        }

        return icons;

    }

}
