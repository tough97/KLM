package cs.designer.swing.bean;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/3/12
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProviderBean extends UserBean {
    private String phoneNumber;
    private String address;
    private float disconut;

    public ProviderBean() {
        super();
    }

    public ProviderBean(String code) {
        super(code);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getDisconut() {
        return disconut;
    }

    public void setDisconut(float disconut) {
        this.disconut = disconut;
    }
}
