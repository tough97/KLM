package cs.designer.screen.impi;

import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.screen.CoBorwser;
import cs.designer.screen.KLMScreen;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/21/12
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndexScreen extends KLMScreen {
    public static final String PAGE_URL = ClientNetIO.SERVER_HOST + "/client/index.jsp";
    private CoBorwser webBrowser;

    public IndexScreen(JPanel parentPanel) {
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
