package cs.designer.screen;

import chrriis.dj.nativeswing.swtimpl.components.*;
import cs.designer.io.net.ClientNetIO;
import cs.designer.io.net.IPAddressNetIO;
import cs.designer.swing.resources.ResourcesPath;
import net.sf.json.JSONObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 8/29/12
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoBorwser extends JWebBrowser {
    public static final String CLIENT_REQUSE_KEY = "Y2xpZW50";
    public static final String CLIENT_REQUSE_VALUES = "a2FvbGFtYW8xLjc=";

      public static final String ERRO_PAGE = "<!DOCTYPE html>\n" +
              "<html>\n" +
              "    <head>\n" +
              "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
              "        <style>\n" +
              "            * {\n" +
              "                padding: 0px;\n" +
              "                margin: 0px;\n" +
              "            }\n" +
              "\n" +
              "            .main {\n" +
              "                width: 300px;\n" +
              "                height: 200px;\n" +
              "                margin-left: auto;\n" +
              "                margin-right: auto;margin-top:250px;\n" +
              "            }\n" +
              "            .main input {\n" +
              "                width: 88px;\n" +
              "                height: 28px;\n" +
              "                color: sienna;\n" +
              "            }\n" +
              "        </style>\n" +
              "    </head>\n" +
              "    <body>\n" +
              "        <div class=\"main\">\n" +
              "           无法连接到服务器 <input type='button' value='刷新' onclick='sendNSCommand(\"reload\");'>\n" +
              "        </div>\n" +
              "    </body>\n" +
              "</html>";
    private Map<String, String> parameterMap = null;
    private String borwseURL = "";


    public CoBorwser() {
        super(JWebBrowser.destroyOnFinalization());
        init();
    }

    private void init() {
        setStatusBarVisible(false);
        setBarsVisible(false);
        setMenuBarVisible(false);
        setButtonBarVisible(false);
        parameterMap = new HashMap<String, String>();
        parameterMap.put(CLIENT_REQUSE_KEY, CLIENT_REQUSE_VALUES);
        setDefaultPopupMenuRegistered(false);
        final KeyboardFocusManager manager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyboardControlerManager());

    }

    public void addParameters(final String key, final String value) {
        parameterMap.put(key, value);
    }

    public void addParameters(final Map<String, String> parameters) {
        this.parameterMap.putAll(parameters);
    }

    public boolean navigate(final String url) {
        ClientNetIO.testNetStatus();
        if (ClientNetIO.getLinkableStatus()) {
            this.borwseURL = url;
            final WebBrowserNavigationParameters parameters = new WebBrowserNavigationParameters();
            parameters.setHeaders(parameterMap);
            return super.navigate(url, parameters);
        } else {
            super.setHTMLContent(ERRO_PAGE);
            return true;
        }
    }

    public void reloadPage() {
        navigate(this.borwseURL);
    }

    public static String getPostDataUrl(final String url,
                                        final Map<String, String> parameters) {
        String postData = "";
        if ((parameters == null) || (parameters.isEmpty())) {
            return postData;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append("?");
        for (String key : parameters.keySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            if ((key != null) && (key.length() > 0)) {
                sb.append(key);
                sb.append('=');
                sb.append(parameters.get(key));
            }
        }
        return sb.toString();
    }

    class KeyboardControlerManager implements KeyEventDispatcher {
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                final int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_F5:
//                        reloadPage();
                        break;
                    default:
                        break;
                }

            }
            return false;

        }
    }



}

