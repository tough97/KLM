package cs.designer.screen.impi;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.io.net.HouseSourceNetIO;
import cs.designer.main.KLMDesktop;
import cs.designer.screen.CoBorwser;
import cs.designer.screen.KLMScreen;
import cs.designer.screen.SrceenDisplayer;
import cs.designer.swing.bean.HouseBean;
import cs.designer.swing.ui.WaitingDialog;
import cs.designer.view.viewer.HousePlanView;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 8/28/12
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchPlanScreen extends KLMScreen {
    public static final String PAGE_URL = ClientNetIO.SERVER_HOST + "/client/house.jsp";
    private CoBorwser webBrowser;

    public SearchPlanScreen(final JPanel parentPanel) {
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
        webBrowser.navigate(PAGE_URL);
        addComdListener();
        add(webBrowser, BorderLayout.CENTER);
    }

    private void addComdListener() {
        webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command.endsWith("house")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 2) {
                        final String houseId = parameters[0].toString();
                        final String authorId = parameters[1].toString();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                loadHouse(houseId, authorId);
                            }
                        });
                    }

                } else if (command.endsWith("reload")) {
                    ClientNetIO.testNetStatus();
                    updateScreen();
                }else if(command.endsWith("default")){
                    setLocation();
                }
            }
        });
    }

    private void setLocation() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (KLMDesktop.LOCATION != null) {
                    webBrowser.executeJavascript(JWebBrowser.
                            createJavascriptFunctionCall("set_default_location",
                                    KLMDesktop.LOCATION.getString("province") + "省", KLMDesktop.LOCATION.getString("city") + "市"));
                }
            }
        });
    }

    private void loadHouse(final String houseId, final String authorId) {
        final HouseBean bean = new HouseBean(houseId);
        bean.setOwnerUserCode(authorId);
        final SrceenDisplayer displayer = (SrceenDisplayer) getParent();
        displayer.displaySrceen(SrceenDisplayer.DESIGNER);
        final HousePlanView view = displayer.getDesignerScreen().getCanvasPanel().getPlanview();
        final HouseSourceNetIO houseSourceNetIO = new HouseSourceNetIO(view, bean);
        WaitingDialog.show(SwingUtilities.getWindowAncestor(view),
                houseSourceNetIO, "考拉猫正在努力为您加载......", "加载成功", "加载失败");
    }

}
