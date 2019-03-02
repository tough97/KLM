package cs.designer.swing.ui;

import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import cs.designer.swing.ToolbarPanel;
import cs.designer.swing.resources.ResourcesPath;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/15/12
 * Time: 8:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwingTool {
    public static NimRODLookAndFeel nf;
    public static NimRODTheme nt;
    public static Color THEME_COLOR = new Color(218, 219, 219);//r232 g226 b221

    public static void initUI() {
        nt = new NimRODTheme(ResourcesPath.getResourcesUrl("Snow.theme"));
        int r = THEME_COLOR.getRed();
        int g = THEME_COLOR.getGreen();
        int b = THEME_COLOR.getBlue();
        nt.setWhite(THEME_COLOR);
        nt.setPrimary1(new Color((r > 20 ? r - 20 : 0), (g > 20 ? g - 20 : 0), (b > 20 ? b - 20 : 0)));
        nt.setPrimary2(new Color((r > 20 ? r - 20 : 0), (g > 20 ? g - 20 : 0), (b > 20 ? b - 20 : 0)));
        nt.setPrimary3(new Color((r > 20 ? r - 20 : 0), (g > 20 ? g - 20 : 0), (b > 20 ? b - 20 : 0)));
        nt.setMenuOpacity(195);
        nf = new NimRODLookAndFeel();
        nf.setCurrentTheme(nt);
        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(nf);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

}




