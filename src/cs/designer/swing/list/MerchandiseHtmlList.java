package cs.designer.swing.list;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.main.KLMDesktop;
import cs.designer.screen.CoBorwser;
import cs.designer.swing.TopPanel;
import cs.designer.swing.bean.MerchandiseBean;
import cs.designer.swing.bean.UserBean;
import cs.designer.swing.ui.LoginDialog;
import cs.designer.swing.ui.LoginTask;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/5/12
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseHtmlList extends JPanel implements LoginTask {
    public static final Map<String, MerchandiseBean> RECENTLY_MERCHANDISE = new HashMap<String, MerchandiseBean>();
    public static final String MER_LIST_URL = ClientNetIO.SERVER_HOST + "/client/model_area.jsp";
    private CoBorwser borwser;

    public MerchandiseHtmlList() {
        super();
        updateScreen();
    }

    private void updateScreen() {
        this.borwser = new CoBorwser();
        this.borwser.setButtonBarVisible(true);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, borwser);
        addComdListener();
        if (LoginDialog.USER.getCode() != null && LoginDialog.USER.getCode().length() != 0) {
            borwser.addParameters("userid", LoginDialog.USER.getCode());
        }
        borwser.navigate(MER_LIST_URL);
    }

    private void addComdListener() {
        borwser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command.endsWith("collection_mer")) {
                    if (LoginDialog.USER.getCode() == null
                            || LoginDialog.USER.getCode().length() == 0) {

                        login();
                    } else {
                        showCollection(LoginDialog.USER.getCode());
                    }

                } else if (command.endsWith("set_recently_mer")) {
                    setRecentlyMerchandise();
                } else if (command.endsWith("remove_collection")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final JSONObject message = JSONObject.fromObject(parameters[0]);
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), message.getString("message"), "",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (command.endsWith("authorizedlocation")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            borwser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("set_authorized_location", KLMDesktop.checkAuthorizedlocation()));
                        }
                    });

                } else if (command.endsWith("reload")) {
                    ClientNetIO.testNetStatus();
                    updateScreen();
                }
            }
        });
    }

    public void login() {
        final Window window = SwingUtilities.getWindowAncestor(this);
        final LoginDialog loginDialog = new LoginDialog(window, this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loginDialog.setVisible(true);
            }
        });

    }

    public CoBorwser getBorwser() {
        return borwser;
    }

    public void finishUp(final UserBean bean) {
        showCollection(bean.getCode());
        TopPanel.setUserName(bean.getName());
    }

    private void showCollection(final String userId) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                borwser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("show_collection", userId));
            }
        });
    }

    private void setRecentlyMerchandise() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JSONArray recentlymers = new JSONArray();
                for (MerchandiseBean bean : RECENTLY_MERCHANDISE.values()) {
                    recentlymers.add(bean.toJSONObject());
                }
                borwser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("set_recently_mer", recentlymers.toString()));
            }
        });
    }


}
