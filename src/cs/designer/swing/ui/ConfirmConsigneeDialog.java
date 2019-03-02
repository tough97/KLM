package cs.designer.swing.ui;

import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import cs.designer.io.net.ClientNetIO;
import cs.designer.io.net.NetOperation;
import cs.designer.io.net.OrderNetIO;
import cs.designer.screen.CoBorwser;
import cs.designer.swing.bean.OrderBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/17/12
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfirmConsigneeDialog extends JDialog {
    public static final String URL = ClientNetIO.SERVER_HOST + "/client/confirm_order.jsp";
    public static final int DEFAULT_WIDTH = 610;
    public static final int DEFAULT_HEIGHT = 550;
    private CoBorwser borwser;
    private JSONArray orderMerchandises;
    private String state;


    public static void show(final Window parent, JSONArray orderMerchandises, String state) {
        final JDialog consigneeDialog = new ConfirmConsigneeDialog(parent, orderMerchandises, state);
        consigneeDialog.setVisible(true);

    }

    public ConfirmConsigneeDialog(final Window parent, JSONArray orderMerchandises, String state) {
        super(parent, "");
        this.state = state;
        this.orderMerchandises = orderMerchandises;
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
        this.borwser = new CoBorwser();
        getContentPane().add(BorderLayout.CENTER, borwser);
        borwser.addParameters("id", LoginDialog.USER.getCode());
        borwser.navigate(URL);
        addComdListener();

    }


    private void addComdListener() {
        borwser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void commandReceived(WebBrowserCommandEvent e) {
                String command = e.getCommand();
                if (command.endsWith("submit")) {
                    Object[] parameters = e.getParameters();
                    if (parameters != null && parameters.length == 1) {
                        final JSONObject consigneeAddressInfo = JSONObject.fromObject(parameters[0]);
                        final OrderBean orderBean = new OrderBean();
                        orderBean.setState(state);
                        orderBean.setUser(LoginDialog.USER);
                        orderBean.setMerchandises(orderMerchandises);
                        orderBean.setConsigneeAddress(consigneeAddressInfo.getString("CONSIGNEE_ADDRESS"));
                        orderBean.setConsigneeName(consigneeAddressInfo.getString("CONSIGNEE_NAME"));
                        orderBean.setConsigneeMobile(consigneeAddressInfo.getString("CONSIGNEE_MOBILE"));
                        orderBean.setSendTime(consigneeAddressInfo.getString("SEND_TIME"));
                        orderBean.setCityCode(consigneeAddressInfo.getString("CITY_CODE"));
                        orderBean.setProvinceCode(consigneeAddressInfo.getString("PROVINCE_CODE"));
                        orderBean.setCompleteAble(Boolean.valueOf(consigneeAddressInfo.getString("COMPLETE_ABLE")));
                        final NetOperation operation = new OrderNetIO(orderBean);
                        operation.upload();
                        setVisible(false);
                    }
                }
            }
        });
    }

}
