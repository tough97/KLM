package cs.designer.swing.bean;

import com.klm.cons.impl.WallShape;
import com.klm.persist.meta.Shape3DMeta;
import com.sun.j3d.utils.scenegraph.io.NamedObjectException;
import cs.designer.swing.bean.CoBean;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.io.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/25/12
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserBean implements CoBean, Serializable {
    private String userID;
    private String userName;
    private String password;
    private String userEmail;
    private String cityName;
    private String contactMode;
    private String companyName;
    private String mobile;
    private boolean verified=false;
    private static final long serialVersionUaID = 100;

    public UserBean() {

    }

    public UserBean(final String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String createDescription() {
        return null;
    }

    public String getCode() {
        return userID;
    }

    public String getName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getContactMode() {
        return contactMode;
    }

    public void setContactMode(String contactMode) {
        this.contactMode = contactMode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void reset() {
        this.userID = null;
        this.userName = null;
        this.password = null;
        this.userEmail = null;
        this.cityName = null;
        this.contactMode = null;
        this.companyName=null;
    }

    public UserBean clone() {
        final UserBean userBean = new UserBean();
        userBean.userID = this.userID;
        userBean.userName = this.userName;
        userBean.password = this.password;
        userBean.userEmail = this.userEmail;
        userBean.cityName = this.cityName;
        userBean.contactMode = this.contactMode;
        userBean.companyName=this.companyName;
        return userBean;
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException,
            NamedObjectException {
        oos.writeObject(userID);
        oos.writeObject(userEmail);

    }

    private void readObject(final ObjectInputStream objectInputStream)
            throws ClassNotFoundException, IOException {
        final Object tempUserID = objectInputStream.readObject();
        final Object tempUserEmail = objectInputStream.readObject();
        this.userID = tempUserID == null ? null : tempUserID.toString();
        this.userEmail = tempUserEmail == null ? null : tempUserEmail.toString();

    }

}
