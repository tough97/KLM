package cs.designer.swing.ui;

import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.screen.CoBorwser;
import cs.designer.swing.bean.UserBean;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/30/12
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoginDialog extends JDialog {
    private final static String LOGIN_URL = ClientNetIO.SERVER_HOST + "/client/login.jsp";

    public static UserBean USER = new UserBean();
    private CoBorwser borwser;
    private LoginTask task;


    public LoginDialog(final Window parent, final LoginTask task) {
        super(parent, "登录");
        this.task = task;
        setModal(true);
        setSize(469, 413);
        setResizable(false);
        init();
        int locationX = parent.getLocation().x + (parent.getWidth()
                - getWidth()) / 2;
        int locationY = parent.getLocation().y + (parent.getHeight()
                - getHeight()) / 2;

        setLocation(locationX, locationY);
        setResizable(false);

    }

    private void init() {
        getContentPane().setLayout(new BorderLayout());
        this.borwser = new CoBorwser();
        addComdListener();
        getContentPane().add(BorderLayout.CENTER, borwser);
        borwser.navigate(LOGIN_URL);
    }

    private void addComdListener() {
        borwser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command.endsWith("user")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 3) {
                        final String userId = parameters[0].toString();
                        final String userName = parameters[1].toString();
                        final String userEmail = parameters[2].toString();
                        USER.setUserID(userId);
                        USER.setUserName(userName);
                        USER.setUserEmail(userEmail);
                        setVisible(false);
                        task.finishUp(USER);
                    }
                } else if (command.endsWith("erro")) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "用户名或密码错误", "",
                            JOptionPane.ERROR_MESSAGE);
                } else if (command.endsWith("register")) {
                    setVisible(false);
                    RegisterDialog registerDialog = new RegisterDialog((Window) getParent(), task);
                    registerDialog.setVisible(true);
                } else if (command.endsWith("forget_pw")) {
                    try {
                        URI uri = new URL(ClientNetIO.SERVER_HOST + "/forget_pw.jsp").toURI();
                        Desktop.getDesktop().browse(uri);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else if (command.endsWith("reload")) {
                    ClientNetIO.testNetStatus();
                    borwser.navigate(LOGIN_URL);
                } else if (command.endsWith("info")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 2) {
                        final String userMobile = parameters[0].toString();
                        final Boolean userVerified = Boolean.valueOf(parameters[1].toString());
                        USER.setMobile(userMobile);
                        USER.setVerified(userVerified);
                    }
                }
            }
        });
    }


}