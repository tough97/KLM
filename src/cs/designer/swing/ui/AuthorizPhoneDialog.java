package cs.designer.swing.ui;

import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.io.net.NetOperation;
import cs.designer.io.net.UserPhoneAuthorizNetIO;
import cs.designer.screen.CoBorwser;
import cs.designer.swing.bean.UserBean;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/11/12
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizPhoneDialog extends JDialog {
    private final static String REGISTER_URL = ClientNetIO.SERVER_HOST + "/client/phone_authoriz.jsp";
    private String code = null;
    private CoBorwser borwser;
    private AuthorizTask authorizTask;
    private String userMobile;

    public AuthorizPhoneDialog(final Window parent, final AuthorizTask authorizTask) {
        super(parent, "");
        this.authorizTask = authorizTask;
        setModal(true);
        setSize(370, 343);
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
                if (command.endsWith("code")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        code = parameters[0].toString();

                    }
                } else if (command.endsWith("authoriz")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 2) {
                        String authorizCode = parameters[0].toString();
                        if (code!=null&&authorizCode.endsWith(code)) {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "验证成功", "",
                                    JOptionPane.INFORMATION_MESSAGE);
                            LoginDialog.USER.setVerified(true);
                            LoginDialog.USER.setMobile(parameters[1].toString());
                            NetOperation operation = new UserPhoneAuthorizNetIO(LoginDialog.USER);
                            operation.update();
                            authorizTask.authorizUp();
                            setVisible(false);

                        } else {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "验证码不正确", "",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    }
                }
            }
        });
    }

}