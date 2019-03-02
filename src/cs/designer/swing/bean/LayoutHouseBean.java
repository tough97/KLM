package cs.designer.swing.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/28/12
 * Time: 10:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class LayoutHouseBean implements CoBean {
    private String code;
    private String name;
    private String description;
    private float construcitionArea;
    private float utilizableArea;
    private int badeRoomNum;
    private int bathRoomNum;
    private int livingRoomNum;
    private int kitchenRoomNum;
    private String compoundCode;


    public LayoutHouseBean() {
    }

    public LayoutHouseBean(final String code,
                           final String name) {
        this.code = code;
        this.name = name;
    }

    public String createDescription() {
        return "";
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public float getConstrucitionArea() {
        return construcitionArea;
    }

    public float getUtilizableArea() {
        return utilizableArea;
    }

    public int getBadeRoomNum() {
        return badeRoomNum;
    }

    public int getBathRoomNum() {
        return bathRoomNum;
    }

    public int getLivingRoomNum() {
        return livingRoomNum;
    }

    public int getKitchenRoomNum() {
        return kitchenRoomNum;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setConstrucitionArea(float construcitionArea) {
        this.construcitionArea = construcitionArea;
    }

    public void setUtilizableArea(float utilizableArea) {
        this.utilizableArea = utilizableArea;
    }

    public void setBadeRoomNum(int badeRoomNum) {
        this.badeRoomNum = badeRoomNum;
    }

    public void setBathRoomNum(int bathRoomNum) {
        this.bathRoomNum = bathRoomNum;
    }

    public void setLivingRoomNum(int livingRoomNum) {
        this.livingRoomNum = livingRoomNum;
    }

    public void setKitchenRoomNum(int kitchenRoomNum) {
        this.kitchenRoomNum = kitchenRoomNum;
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public void setCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
    }

    public String toString() {
        return code;
    }
}
