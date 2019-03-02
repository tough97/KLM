package cs.designer.swing.bean;

import cs.designer.swing.bean.CoBean;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/25/12
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageBean implements CoBean {
    private String info;
    private Object responseObject;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    public String createDescription() {
        return null;
    }

    public String getCode() {
        return "";
    }

    public String getName() {
        return "";
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }
}
