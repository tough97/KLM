package cs.designer.screen.impi;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.io.net.HouseSourceNetIO;
import cs.designer.screen.CoBorwser;
import cs.designer.screen.KLMScreen;
import cs.designer.screen.SrceenDisplayer;
import cs.designer.swing.TopPanel;
import cs.designer.swing.bean.HouseBean;
import cs.designer.swing.bean.UserBean;
import cs.designer.swing.ui.LoginTask;
import cs.designer.swing.ui.WaitingDialog;
import cs.designer.view.viewer.HousePlanView;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/6/12
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class PersonalScreen extends KLMScreen implements LoginTask {
    public static final String PAGE_URL = ClientNetIO.SERVER_HOST + "/client/personal.jsp";
    private String userId = null;
    private CoBorwser webBrowser;

    public PersonalScreen(JPanel parentPanel) {
        super(parentPanel);
        setLayout(new BorderLayout());

    }

    @Override
    public void updateScreen() {
        if (webBrowser != null) {
            remove(webBrowser);
        }
        webBrowser = null;
        webBrowser = new CoBorwser();
        webBrowser.setBarsVisible(false);
        if (userId != null) {
            webBrowser.addParameters("id", userId);
            webBrowser.navigate(PAGE_URL);
            add(webBrowser, BorderLayout.CENTER);
        }
        addComdListener();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private void addComdListener() {
        webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command.endsWith("remove_order")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final String orderId = parameters[0].toString();
                        if (orderId.length() != 0) {
                            int userSelect = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                                    "是否确定删除此订单？",
                                    "",
                                    JOptionPane.YES_NO_CANCEL_OPTION);
                            if (userSelect == JOptionPane.OK_OPTION) {
                                removeOrder(orderId);
                            }
                        }
                    }

                } else if (command.endsWith("remove_house")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final String houseId = parameters[0].toString();
                        if (houseId.length() != 0) {
                            int userSelect = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                                    "是否确定删除？",
                                    "",
                                    JOptionPane.YES_NO_CANCEL_OPTION);
                            if (userSelect == JOptionPane.OK_OPTION) {
                                removeHouse(houseId);
                            }
                        }
                    }

                } else if (command.endsWith("house")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 2) {
                        final String houseId = parameters[0].toString();
                        final String authorId = parameters[1].toString();
                        final HouseBean bean = new HouseBean(houseId);
                        bean.setOwnerUserCode(authorId);
                        final SrceenDisplayer displayer = (SrceenDisplayer) getParent();
                        displayer.displaySrceen(SrceenDisplayer.DESIGNER);
                        final HousePlanView view = displayer.getDesignerScreen().getCanvasPanel().getPlanview();
                        final HouseSourceNetIO houseSourceNetIO = new HouseSourceNetIO(view, bean);
                        WaitingDialog.show(SwingUtilities.getWindowAncestor(view),
                                houseSourceNetIO, "考拉猫正在努力为您加载......", "加载成功", "加载失败");
                    }

                } else if (command.endsWith("reload")){
                    ClientNetIO.testNetStatus();
                    updateScreen();
                }
            }
        });
    }

    public void finishUp(UserBean bean) {
        userId = bean.getCode();
        TopPanel.setUserName(bean.getName());
        TopPanel.setSelectButton(SrceenDisplayer.PERSONAL_CENTER);
        display();
    }

    private void removeOrder(final String orderId) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                webBrowser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("remove_order", orderId));
            }
        });

    }

    private void removeHouse(final String houseId) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                webBrowser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("remove_user_house", houseId));
            }
        });

    }
}
