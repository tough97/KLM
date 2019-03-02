package cs.designer.swing.ui;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.screen.CoBorwser;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/21/12
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommentDialog extends JDialog {
    private final static String REGISTER_URL = ClientNetIO.SERVER_HOST + "/client/comment.jsp";
    private CoBorwser borwser;

    public static void show(final Window parent) {
        final JDialog commentDialog = new CommentDialog(parent);
        commentDialog.setVisible(true);
    }

    public CommentDialog(final Window parent) {
        super(parent, "");
        setModal(true);
        setSize(700, 600);
        setResizable(false);
        init();
        int locationX = parent.getLocation().x + (parent.getWidth()
                - getWidth()) / 2;
        int locationY = parent.getLocation().y + (parent.getHeight()
                - getHeight()) / 2;

        setLocation(locationX, locationY);

    }

    private void init() {
        getContentPane().setLayout(new BorderLayout());
        this.borwser = null;
        this.borwser = new CoBorwser();
        getContentPane().add(BorderLayout.CENTER, borwser);
        borwser.navigate(REGISTER_URL);
        addComdListener();

    }

    private void addComdListener() {
        borwser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command != null && command.length() != 0 && command.endsWith("result")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final String result = parameters[0].toString();
                        showMessage(result);
                    }


                } else if (command != null && command.length() != 0 && command.endsWith("init")) {
                    setUserEmail();
                } else if (command.endsWith("reload")) {
                    ClientNetIO.testNetStatus();
                    borwser.navigate(REGISTER_URL);
                }
            }
        });
    }

    private void setUserEmail() {
        if (LoginDialog.USER.getUserEmail() != null) {
            final String userEmail = LoginDialog.USER.getUserEmail();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    borwser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("set_user_email", userEmail));
                }
            });
        }

    }

    private void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), message, "",
                        JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
            }
        });
    }

}