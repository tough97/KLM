package cs.designer.screen.impi;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import com.klm.cons.impl.CSHouseException;
import com.klm.persist.CSPersistException;
import com.klm.persist.impl.LocalStorage;
import cs.designer.io.local.FileExtensionUtill;
import cs.designer.io.net.ClientNetIO;
import cs.designer.io.net.ExcelFileNetIO;
import cs.designer.io.net.HouseFileNetIO;
import cs.designer.main.KLMDesktop;
import cs.designer.screen.CoBorwser;
import cs.designer.screen.KLMScreen;
import cs.designer.screen.SrceenDisplayer;
import cs.designer.swing.TopPanel;
import cs.designer.swing.bean.FileBean;
import cs.designer.swing.bean.UserBean;
import cs.designer.swing.ui.*;
import cs.designer.view.viewer.HousePlanView;
import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/10/12
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class RenovationScreen extends KLMScreen implements LoginTask, AuthorizTask {
    public static String PAGE_URL = ClientNetIO.SERVER_HOST + "/client/renovation.jsp";
    private CoBorwser borwser;

    public RenovationScreen(JPanel parentPanel) {
        super(parentPanel);
        setLayout(new BorderLayout());
    }

    @Override
    public void updateScreen() {
        if (borwser != null) {
            remove(borwser);
        }
        borwser = null;
        borwser = new CoBorwser();
        borwser.setBarsVisible(false);
        addComdListener();
        add(borwser, BorderLayout.CENTER);
        borwser.navigate(PAGE_URL);
    }

    private void addComdListener() {
        borwser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command.endsWith("result")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        String result = parameters[0].toString();
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), result, "",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (command.endsWith("load")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final JSONObject houseInfo = JSONObject.fromObject(parameters[0]);
                        final String houseFilePath = houseInfo.getString("HOUSE_FILE");
                        final String houseId = houseInfo.getString("HOUSE_ID");


                        try {
                            final FileBean fileBean = new FileBean(new File(LocalStorage.getLocalStorage().getSubDir(LocalStorage.TEMPRORY_DIR), houseId + FileExtensionUtill.HOUSE_EXTEBSUIB_NAME));
                            fileBean.setServerPath(houseFilePath);
                            final SrceenDisplayer displayer = (SrceenDisplayer) getParent();
                            displayer.displaySrceen(SrceenDisplayer.DESIGNER);
                            final HousePlanView view = displayer.getDesignerScreen().getCanvasPanel().getPlanview();
                            final ClientNetIO client = new HouseFileNetIO(fileBean, view);
                            client.start();
                        } catch (CSPersistException e1) {
                            e1.printStackTrace();
                        } catch (CSHouseException e1) {
                            e1.printStackTrace();
                        }

                    }

                } else if (command.endsWith("output")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final JSONObject fileInfo = JSONObject.fromObject(parameters[0]);
                        final String houseFilePath = fileInfo.getString("EXCEL_FILE");
                        try {
                            final FileBean fileBean = new FileBean(new File(LocalStorage.getLocalStorage().getSubDir(LocalStorage.TEMPRORY_DIR), System.currentTimeMillis() + FileExtensionUtill.HOUSE_EXTEBSUIB_NAME));
                            fileBean.setServerPath(houseFilePath);
                            System.out.println(fileBean.getSourceFile());
                            final ClientNetIO client = new ExcelFileNetIO(fileBean);
                            client.start();
                        } catch (CSPersistException e1) {
                            e1.printStackTrace();
                        } catch (CSHouseException e1) {
                            e1.printStackTrace();
                        }

                    }

                } else if (command.endsWith("login")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            login();
                            setAuthorizationMobile();
                        }
                    });
                } else if (command.endsWith("init")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            borwser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("check_authorization", KLMDesktop.checkAuthorizedlocation()));
                            setAuthorizationMobile();
                        }
                    });
                    if (LoginDialog.USER.getCode() != null && LoginDialog.USER.getCode().length() != 0) {
                        setUserId(LoginDialog.USER);
                    }
                } else if (command.endsWith("reload")) {
                    ClientNetIO.testNetStatus();
                    updateScreen();
                } else if (command.endsWith("default")) {
                    setLocation();
                } else if (command.endsWith("mobile")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            checkAuthorizationMobile();
                        }
                    });
                }
            }
        });
    }


    public void finishUp(final UserBean bean) {
        TopPanel.setUserName(bean.getName());
        TopPanel.setSelectButton(SrceenDisplayer.RENOVATION);
        setUserId(bean);
    }

    private void setUserId(final UserBean bean) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                borwser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("set_user_id", bean.getCode()));
            }
        });
    }

    public void login() {
        final Window window = SwingUtilities.getWindowAncestor(this);
        final LoginDialog loginDialog = new LoginDialog(window, this);
        loginDialog.setVisible(true);
    }


    private void setLocation() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (KLMDesktop.LOCATION != null) {
                    borwser.executeJavascript(JWebBrowser.
                            createJavascriptFunctionCall("set_default_location",
                                    KLMDesktop.LOCATION.getString("province") + "省", KLMDesktop.LOCATION.getString("city") + "市"));
                }
            }
        });
    }

    private void checkAuthorizationMobile() {
        final Window window = SwingUtilities.getWindowAncestor(this);
        final JDialog dialog = new AuthorizPhoneDialog(window, this);
        dialog.setVisible(true);
    }

    private void setAuthorizationMobile() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (LoginDialog.USER.getCode() != null && LoginDialog.USER.getCode().length() != 0) {
                    borwser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("check_authorization_mobile", LoginDialog.USER.isVerified()));
                }
            }
        });
    }

    public void authorizUp() {
        setAuthorizationMobile();
    }
}
