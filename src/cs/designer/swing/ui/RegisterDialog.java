package cs.designer.swing.ui;

import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.screen.CoBorwser;
import cs.designer.swing.bean.UserBean;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/13/12
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterDialog extends JDialog {
    private final static String REGISTER_URL = ClientNetIO.SERVER_HOST + "/client/register.jsp";
    private CoBorwser borwser;
    private LoginTask task;

    public RegisterDialog(final Window parent, LoginTask task) {
        super(parent, "用户注册");
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

    }

    private void init() {
        getContentPane().setLayout(new BorderLayout());
        this.borwser = new CoBorwser();
        addComdListener();
        getContentPane().add(BorderLayout.CENTER, borwser);
        borwser.navigate(REGISTER_URL);
    }

    private void addComdListener() {
        borwser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command != null && command.length() != 0 && command.endsWith("user")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 2) {
                        final String userId = parameters[0].toString();
                        final String userMail = parameters[1].toString();
                        LoginDialog.USER.setUserEmail(userMail);
                        LoginDialog.USER.setUserID(userId);
                        final UserBean userBean = new UserBean(userId);
                        userBean.setUserName(userMail);
                        task.finishUp(userBean);
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "注册成功", "",
                                JOptionPane.INFORMATION_MESSAGE);
                        setVisible(false);
                    } else if (command.endsWith("reload")) {
                        ClientNetIO.testNetStatus();
                        borwser.navigate(REGISTER_URL);
                    }


                }
            }
        });
    }

}