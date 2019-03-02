package cs.designer.swing.bean;

import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.MerchandiseProvider;
import com.klm.persist.impl.SurfaceMaterial;
import com.klm.persist.meta.ModelMeta;
import cs.designer.module.ModelBoundingBox;
import net.sf.json.JSONObject;

import javax.vecmath.Point3d;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/22/12
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseBean implements CoBean {
    public enum MerchandiseType {
        surfaceMaterial, furniture
    }

    private String name;
    private String id;
    private String description;
    private MerchandiseProvider mp = new MerchandiseProvider();
    private double installationPrice = 0.0;
    private double unitPrice = 0.0;
    private double logisticsPrice = 0.0;
    private double amount = 0.0;
    private double discount = 1.0;
    private String unitName;
    private BufferedImage sneapView;
    private ProviderBean provider;
    private MerchandiseBrandBean brand;
    private MerchandiseType type;
    private byte[] modelSource;
    private String categoryId;
    private String iconPath;
    private String styleId;
    private String sourceFilePath;


    public MerchandiseBean() {

    }

    public MerchandiseBean(String id) {
        this.id = id;
    }

    public MerchandiseBean(String id, String name) {
        this(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public MerchandiseProvider getMp() {
        return mp;
    }

    public double getInstallationPrice() {
        return installationPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getLogisticsPrice() {
        return logisticsPrice;
    }

    public String getUnitName() {
        return unitName;
    }

    public BufferedImage getSneapView() {
        if (sneapView == null) {
            return HouseBean.DEFAULT_IMAGE;
        }
        return sneapView;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMp(MerchandiseProvider mp) {
        this.mp = mp;
    }

    public void setInstallationPrice(double installationPrice) {
        this.installationPrice = installationPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setLogisticsPrice(double logisticsPrice) {
        this.logisticsPrice = logisticsPrice;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Merchandise createObject() {
        Merchandise merchandise = null;
        if (type == MerchandiseType.furniture) {
            merchandise = new Furniture(getCode());
            ModelBoundingBox modelBoundingBox = new ModelBoundingBox(new Point3d(-0.25, -0.25, -0.25),
                    new Point3d(0.25, 0.25, 0.25));
            final ModelMeta defaultModel = new ModelMeta();
            defaultModel.setBaseModel(modelBoundingBox);
            ((Furniture) merchandise).setModel(defaultModel);
        } else if (type == MerchandiseType.surfaceMaterial) {
            merchandise = new SurfaceMaterial(getCode());
        }
        if (merchandise != null) {
            merchandise.setName(getName());
            merchandise.setDescription(getDescription());
            merchandise.setUnitName(getUnitName());
            merchandise.setUnitPrice(getUnitPrice());
        }
        return merchandise;

    }

    public String createDescription() {
        String tooltip = "<html>"
                + "<table width='100%',height='100%'>"
                + "<tr>名 称:" + getName() + "</tr>"
                + "<tr>品 牌:" + getBrandName() + "</tr>";
        return tooltip + "</table>";
    }

    public String getCode() {
        return id;
    }

    public void setSneapView(BufferedImage sneapView) {
        this.sneapView = sneapView;
    }

    public ProviderBean getProvider() {
        return provider;
    }

    public void setProvider(ProviderBean provider) {
        this.provider = provider;
    }

    public String getBrandName() {
        return brand.getName();
    }

    public void setBrandName(final String brandName) {
        if (this.brand == null) {
            this.brand = new MerchandiseBrandBean();
        }
        this.brand.setBrandName(brandName);
    }

    public MerchandiseType getType() {
        return type;
    }

    public void setType(MerchandiseType type) {
        this.type = type;
    }

    public byte[] getModelSource() {
        return modelSource;
    }

    public void setModelSource(byte[] modelSource) {
        this.modelSource = modelSource;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public MerchandiseBrandBean getBrand() {
        return brand;
    }

    public void setBrand(final MerchandiseBrandBean brand) {
        this.brand = brand;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return discount;
    }

    public String getStyleId() {
        return styleId;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public void updateProject(final Merchandise merchandise) {
        merchandise.setName(getName());
        merchandise.setDescription(getDescription());
        merchandise.setUnitName(getUnitName());
        merchandise.setUnitPrice(getUnitPrice());
    }

    public JSONObject toJSONObject() {
        final JSONObject merchandiseJsonObject = new JSONObject();
        merchandiseJsonObject.accumulate("MERCHANDISE_NAME", getName());
        merchandiseJsonObject.accumulate("MERCHANDISE_ID", getCode());
        merchandiseJsonObject.accumulate("ICON", getIconPath());
        merchandiseJsonObject.accumulate("PRICE", getUnitPrice());
        merchandiseJsonObject.accumulate("DISCOUNT", getDiscount());
        return merchandiseJsonObject;
    }
}
