package cs.designer.main;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import cs.designer.io.net.ClientNetIO;
import cs.designer.io.net.IPAddressNetIO;
import cs.designer.swing.TopPanel;
import cs.designer.swing.resources.ResourcesPath;
import cs.designer.swing.ui.SwingTool;
import cs.designer.screen.SrceenDisplayer;
import net.sf.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 8/28/12
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class KLMDesktop extends JFrame implements ActionListener {
    public static KLMDesktop execute;
    public static final int LOADING_TIME = 70;
    public static final String[] AUTHORIZED_LOCATIONS = {"云南"};
    public static JSONObject LOCATION = null;
    private static JProgressBar progressBar = new JProgressBar();
    private static int count;
    private static Timer timer;
    private SrceenDisplayer displayer;
    private TopPanel toolBarPanel;

    static {
        SwingTool.initUI();
    }

    public KLMDesktop() {
        super("考拉猫家装设计平台");
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            setIconImage(ImageIO.read(ResourcesPath.getResourcesUrl("client_logo.png")));
        } catch (IOException e) {

        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ScreenWindow();
            }
        });
        if (ClientNetIO.testNetStatus()) {
            IPAddressNetIO ipAddressNetIO = new IPAddressNetIO();
            LOCATION = ipAddressNetIO.getIPAddress();
        }
        displayer = new SrceenDisplayer();
        toolBarPanel = new TopPanel(this);
    }

    private void loading() {
        getContentPane().removeAll();
        setLocation(0, 0);
        setSize(new Dimension(getToolkit().getScreenSize().width + 10,
                getToolkit().getScreenSize().height + 10));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.CENTER, displayer);
        getContentPane().add(BorderLayout.NORTH, toolBarPanel);
        displayer.displaySrceen(SrceenDisplayer.INDEX);
        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void actionPerformed(ActionEvent e) {
        displayer.displaySrceen(e.getActionCommand());
    }

    class ScreenWindow extends JFrame {
        ScreenWindow() {
            super("考拉猫家装设计平台");
            try {
                setIconImage(ImageIO.read(ResourcesPath.getResourcesUrl("client_logo.png")));
            } catch (IOException e) {

            }
            initScreenWindow();
        }

        private void initScreenWindow() {
            setUndecorated(true);
            final Container container = getContentPane();
            container.setLayout(new BorderLayout());
            JPanel panel = new JPanel() {
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    try {
                        final BufferedImage image = ImageIO.
                                read(ResourcesPath.getResourcesUrl("splashScreen.png"));
                        g.drawImage(image,
                                0, 0, getWidth(), getHeight(), this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            };
            progressBar.setMaximum(60);
            progressBar.setBackground(Color.WHITE);
            progressBar.setForeground(Color.RED);
            progressBar.setPreferredSize(new Dimension(0, 5));
            progressBar.updateUI();
            container.add(BorderLayout.CENTER, panel);
            container.add(BorderLayout.SOUTH, progressBar);
            loadProgressBar();
            setSize(399, 244);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void loadProgressBar() {
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    count++;
                    progressBar.setValue(count);
                    if (count == LOADING_TIME) {
                        timer.stop();
                        setVisible(false);
                        loading();

                    }
                }
            };
            timer = new Timer(LOADING_TIME, al);
            timer.start();
        }


    }

    public static boolean checkAuthorizedlocation() {
        if (LOCATION != null) {
            String currentProvince = LOCATION.getString("province");
            for (String authorizedLocation : AUTHORIZED_LOCATIONS) {
                if (currentProvince.endsWith(authorizedLocation)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                execute = new KLMDesktop();

            }
        });
        NativeInterface.runEventPump();

    }


}

