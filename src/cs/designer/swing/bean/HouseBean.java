package cs.designer.swing.bean;

import cs.designer.swing.bean.CoBean;
import cs.designer.swing.icons.IconManager;
import cs.designer.swing.resources.ResourcesPath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/22/12
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class HouseBean implements CoBean {
    public static BufferedImage DEFAULT_IMAGE;
    private String code;
    private String ownerUserCode;
    private BufferedImage sneapView;
    private String description;
    private LayoutHouseBean houseLayout;
    private String compoundCode;
    private byte[] houseSource;

    static {
        try {
            DEFAULT_IMAGE = ImageIO.read(ResourcesPath.getResourcesUrl("klm.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HouseBean(final String houseID) {
        this.code = houseID;

    }

    public HouseBean() {
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return "";
    }

    public String getOwnerUserCode() {
        return ownerUserCode;
    }

    public BufferedImage getSneapView() {
        if (sneapView == null) {
            return DEFAULT_IMAGE;
        }
        return sneapView;
    }

    public String getDescription() {
        return description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setOwnerUserCode(String ownerUserCode) {
        this.ownerUserCode = ownerUserCode;
    }

    public void setSneapView(BufferedImage sneapView) {
        this.sneapView = sneapView;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String createDescription() {
        String tooltip = "<html>" +
                "<table>" +
                "<tr>";
        tooltip += "<td width='100%' height='100%''>" +
                "描述" + description +
                "</td>" +
                "</tr>";
        return tooltip + "</table></html>";
    }

    public LayoutHouseBean getHouseLayout() {
        return houseLayout;
    }


    public void setHouseLayout(LayoutHouseBean houseLayout) {
        this.houseLayout = houseLayout;
    }

    public byte[] getHouseSource() {
        return houseSource;
    }

    public void setHouseSource(byte[] houseSource) {
        this.houseSource = houseSource;
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public void setCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
    }

    public HouseBean clone() {
        final HouseBean cloneHouseBean = new HouseBean();
        cloneHouseBean.code = code;
        cloneHouseBean.ownerUserCode = ownerUserCode;
        cloneHouseBean.sneapView = sneapView;
        cloneHouseBean.description = description;
        cloneHouseBean.houseLayout = houseLayout;
        cloneHouseBean.compoundCode = compoundCode;
        cloneHouseBean.houseSource = houseSource;
        return cloneHouseBean;

    }
}
