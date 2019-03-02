package cs.designer.swing.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/11/12
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserHouseBean implements CoBean {
    private String userID;
    private List<HouseBean> userHouses;

    public UserHouseBean() {
        userHouses = new ArrayList<HouseBean>();
    }

    public UserHouseBean(final String userID) {
        this();
        this.userID = userID;
    }

    public void addHouse(final HouseBean house) {
        userHouses.add(house);
    }

    public List<HouseBean> getUserHouses() {
        return userHouses;
    }

    public String createDescription() {
        return null;
    }

    public String getCode() {
        return userID;
    }

    public String getName() {
        return "";
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
