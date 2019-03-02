package cs.designer.swing.bean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/5/12
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrderBean implements CoBean {
    private String orderId;
    private String state;
    private UserBean user;
    private JSONArray merchandises;
    private String  consigneeName;
    private String consigneeMobile;
    private String consigneeAddress;
    private String cityCode;
    private String provinceCode;
    private String sendTime;
    private boolean completeAble;
    public String createDescription() {
        return "";
    }

    public String getCode() {
        return orderId;
    }

    public String getName() {
        return "";
    }

    public String getOrderId() {
        return orderId;
    }

    public String getState() {
        return state;
    }

    public UserBean getUser() {
        return user;
    }

    public List<MerchandiseBean> getMerchandises() {
        return merchandises;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setMerchandises(JSONArray merchandises) {
        this.merchandises = merchandises;
    }

    public JSONArray getMerchandisesInfo() {
        return merchandises;
    }

    public void setConsigneeAddress(final String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }

    public String getConsigneeAddress() {
        System.out.println(consigneeAddress);
        return consigneeAddress;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public String getConsigneeMobile() {
        return consigneeMobile;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public void setConsigneeMobile(String consigneeMobile) {
        this.consigneeMobile = consigneeMobile;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setCompleteAble(boolean completeAble) {
        this.completeAble = completeAble;
    }

    public boolean isCompleteAble() {
        return completeAble;
    }
}
