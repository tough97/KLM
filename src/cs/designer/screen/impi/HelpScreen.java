package cs.designer.screen.impi;

import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.screen.CoBorwser;
import cs.designer.screen.KLMScreen;

import java.awt.BorderLayout;
import javax.swing.JPanel;


/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 8/28/12
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class HelpScreen extends KLMScreen {
    public static final String PAGE_URL = ClientNetIO.SERVER_HOST + "/client/help_center.jsp";
    private CoBorwser webBrowser;

    public HelpScreen(JPanel parentPanel) {
        super(parentPanel);
    }

    @Override
    public void updateScreen() {
        if (webBrowser != null) {
            remove(webBrowser);
        }
        webBrowser = null;
        setLayout(new BorderLayout());
        webBrowser = new CoBorwser();
        addComdListener();
        webBrowser.setBarsVisible(false);
        webBrowser.navigate(PAGE_URL);
        add(webBrowser, BorderLayout.CENTER);
    }

    private void addComdListener() {
        webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command.endsWith("reload")) {
                    ClientNetIO.testNetStatus();
                    updateScreen();
                }
            }
        });
    }
}
