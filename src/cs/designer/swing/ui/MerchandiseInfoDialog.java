package cs.designer.swing.ui;

import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Room;
import com.klm.persist.Merchandise;
import com.klm.util.RealNumberOperator;
import com.klm.util.impl.MerchandiseInfo;
import cs.designer.io.net.ClientNetIO;
import cs.designer.screen.CoBorwser;
import cs.designer.swing.TopPanel;
import cs.designer.swing.bean.UserBean;
import cs.designer.view.viewer.HousePlanView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/12/12
 * Time: 9:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseInfoDialog extends JDialog implements LoginTask {
    public static final int DEFAULT_WIDTH = 840;
    public static final int DEFAULT_HEIGHT = 440;
    public static final String PAGE_URL = ClientNetIO.SERVER_HOST + "/client/mer_info.jsp";
    private Merchandise merchandise;
    private CoBorwser borwser;
    private HousePlanView view;


    public static void show(final Window parent,
                            final Merchandise merchandise,
                            final HousePlanView view) {
        final JDialog merchandiseInfoDialog = new MerchandiseInfoDialog(parent, merchandise, view);
        merchandiseInfoDialog.setVisible(true);
    }

    public MerchandiseInfoDialog(final Window parent,
                                 final Merchandise merchandise,
                                 final HousePlanView view) {
        super(parent, "");
        this.merchandise = merchandise;
        this.view = view;
        setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        int locationX = getParent().getLocation().x + (getParent().getWidth()
                - getWidth()) / 2;
        int locationY = getParent().getLocation().y + (getParent().getHeight()
                - getHeight()) / 2;
        setLocation(locationX, locationY);
        init();
        setResizable(false);
    }

    private void init() {
        getContentPane().setLayout(new BorderLayout());
        this.borwser = null;
        this.borwser = new CoBorwser();
        addComdListener();
        getContentPane().add(BorderLayout.CENTER, borwser);
        final Map<String, String> parameters = new HashMap<String, String>();

        if (merchandise != null && merchandise.getId().length() != 0) {
            parameters.put("merid", merchandise.getId());
        }
        if (LoginDialog.USER.getCode() != null && LoginDialog.USER.getCode().length() != 0) {
            parameters.put("userid", LoginDialog.USER.getCode());
        }
        borwser.navigate(CoBorwser.getPostDataUrl(PAGE_URL, parameters));
    }

    private void addComdListener() {
        borwser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command.endsWith("login")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            login();
                        }
                    });

                } else if (command.endsWith("merchandise")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final JSONObject merchandise = JSONObject.fromObject(parameters[0]);
                        merchandise.accumulate("AMOUNT", getMerchandiseAmount());
                        merchandise.remove("MERCHANDISE_DESCRIPTION");
                        ShoppingCart.addMerchandise(MerchandiseInfoDialog.this.merchandise.getId(), merchandise);
                    }

                } else if (command.endsWith("purchasing")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final JSONObject merchandise = JSONObject.fromObject(parameters[0]);
                        merchandise.accumulate("AMOUNT", getMerchandiseAmount());
                        final JSONArray orderMerchandises = new JSONArray();
                        orderMerchandises.add(merchandise);
                        ConfirmConsigneeDialog.show((Window) getParent(), orderMerchandises, "已下单");
                    }

                } else if (command.endsWith("collection")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final String message = parameters[0].toString();
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), message, "",
                                JOptionPane.INFORMATION_MESSAGE);
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                borwser.executeJavascript("set_user_id('" + bean.getCode() + "')");
                TopPanel.setUserName(bean.getName());
            }
        });
    }

    private double getMerchandiseAmount() {
        Double amount = 0.0;
        final Map<Room, MerchandiseInfo> roomMerchandiseInfoMap;
        try {
            if (view != null) {
                roomMerchandiseInfoMap = view.getCurrentFloor().getFloorMerchandiseInfo();
                for (final Room room : roomMerchandiseInfoMap.keySet()) {
                    Double sub = roomMerchandiseInfoMap.get(room).
                            getQuantityCounter().get(this.merchandise);
                    if (sub == null) {
                        sub = 0.0;
                    }
                    amount += sub;
                }


            }
        } catch (CSHouseException e) {
            e.printStackTrace();
        }
        return RealNumberOperator.roundNumber(amount, 2);
    }
}
