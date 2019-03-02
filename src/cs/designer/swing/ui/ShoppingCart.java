package cs.designer.swing.ui;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.screen.CoBorwser;
import cs.designer.swing.TopPanel;
import cs.designer.swing.bean.UserBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/14/12
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShoppingCart extends JDialog implements LoginTask {
    public static final String URL = ClientNetIO.SERVER_HOST + "/client/shopping_cart.jsp";
    public static final int DEFAULT_WIDTH = 925;
    public static final int DEFAULT_HEIGHT = 650;
    public static final Map<String, JSONObject> cart = new HashMap<String, JSONObject>();
    private CoBorwser borwser;


    public static void show(final Window parent) {
        final JDialog shoppingCart = new ShoppingCart(parent);
        shoppingCart.setVisible(true);
    }

    public ShoppingCart(final Window parent) {
        super(parent, "");
        setResizable(false);
        setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        int locationX = getParent().getLocation().x + (getParent().getWidth()
                - getWidth()) / 2;
        int locationY = getParent().getLocation().y + (getParent().getHeight()
                - getHeight()) / 2;
        setLocation(locationX, locationY);
        init();
    }

    private void init() {
        getContentPane().setLayout(new BorderLayout());
        this.borwser = new CoBorwser() {
            public void reloadPage() {
                super.reloadPage();
                if (LoginDialog.USER.getCode() != null) {
                    setUserId(LoginDialog.USER);
                }
                fillMerchandise();
            }
        };
        getContentPane().add(BorderLayout.CENTER, borwser);
        borwser.navigate(URL);
        addComdListener();
    }

    public static void addMerchandise(final String merchandiseId,
                                      final JSONObject merchandise) {
        cart.put(merchandiseId, merchandise);
        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "添加成功", "",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void fillMerchandise() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                borwser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("fill_mer", JSONArray.fromObject(cart.values()).toString()));
            }
        });
    }

    private void addComdListener() {
        borwser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command.endsWith("login")) {
                    login();
                } else if (command.endsWith("selected")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 2) {
                        final JSONArray selecteds = JSONArray.fromObject(parameters[0]);
                        final String state = parameters[1].toString();
                        if (selecteds != null && selecteds.size() != 0) {
                            JSONArray orderMerchandises = new JSONArray();
                            for (int index = 0; index < selecteds.size(); index++) {
                                orderMerchandises.add(cart.get(selecteds.getJSONObject(index).getString("id")));
                            }
                            ConfirmConsigneeDialog.show((Window) getParent(), orderMerchandises, state);
                        } else {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "请选择商品", "",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                } else if (command.endsWith("remove")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final JSONArray selecteds = JSONArray.fromObject(parameters[0]);
                        if (selecteds != null && selecteds.size() != 0) {
                            JSONArray orderMerchandises = new JSONArray();
                            for (int index = 0; index < selecteds.size(); index++) {
                                orderMerchandises.add(cart.remove(selecteds.getJSONObject(index).getString("id")));
                            }
                            fillMerchandise();
                        } else {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "请选择商品", "",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }

                } else if (command.endsWith("init")) {
                    if (LoginDialog.USER.getCode() != null) {
                        setUserId(LoginDialog.USER);
                    }
                    fillMerchandise();

                } else if (command.endsWith("submit_bargain")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 2) {
                        JSONObject meroObject = JSONObject.fromObject(parameters[0]);
                        System.out.println(meroObject);
                        final String state = parameters[1].toString();
                        JSONArray orderMerchandises = new JSONArray();
                        JSONObject currentMerchandise = cart.get(meroObject.getString("id"));
                        currentMerchandise.remove("PRICE");
                        currentMerchandise.accumulate("PRICE", meroObject.getString("price"));
                        orderMerchandises.add(currentMerchandise);
                        ConfirmConsigneeDialog.show((Window) getParent(), orderMerchandises, state);
                    }

                } else if (command.endsWith("modify_amout")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 2) {
                        String merId = parameters[0].toString();
                        float amout = Float.valueOf(parameters[1].toString());
                        JSONObject merchandiseObject = cart.get(merId);
                        if (merchandiseObject != null && !merchandiseObject.isEmpty()) {
                            merchandiseObject.remove("AMOUNT");
                            merchandiseObject.accumulate("AMOUNT", amout);
                        }

                    }

                }
            }

        });
    }

    public void login() {
        final Window window = SwingUtilities.getWindowAncestor(this);
        final LoginDialog loginDialog = new LoginDialog(window, this);
        loginDialog.setVisible(true);
    }

    public void finishUp(final UserBean bean) {
        setUserId(bean);
        TopPanel.setUserName(bean.getName());

    }

    private void setUserId(final UserBean bean) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
//                borwser.executeJavascript("set_user_id('" + bean.getCode() + "')");
                   borwser.executeJavascript(JWebBrowser.createJavascriptFunctionCall("set_user_id",bean.getCode()));
            }
        });
    }
}
