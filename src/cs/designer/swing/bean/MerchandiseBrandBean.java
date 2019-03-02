package cs.designer.swing.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 6/5/12
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseBrandBean implements CoBean {
    private MerchandiseCategoryBean categoryBean;
    private String brandId;
    private String brandName;
    private List<MerchandiseBean> merchandises;

    public MerchandiseBrandBean() {
        merchandises = new ArrayList<MerchandiseBean>();
    }

    public MerchandiseBrandBean(final String brandId) {
        this();
        this.brandId = brandId;

    }

    public MerchandiseBrandBean(final String brandId, final String brandName) {
        this(brandId);
        this.brandName = brandName;
    }

    public String createDescription() {
        return "";
    }

    public String getCode() {
        return brandId;
    }

    public String getName() {
        return brandName;
    }

    public List<MerchandiseBean> getMerchandises() {
        return merchandises;
    }

    public void addMerchandise(final MerchandiseBean merchandiseBean) {
        this.merchandises.add(merchandiseBean);
        merchandiseBean.setBrand(this);
    }

    public String getCategoryId() {
        return categoryBean.getCode();
    }

    public void setCategory(final MerchandiseCategoryBean categoryBean) {
        this.categoryBean = categoryBean;
    }

    public MerchandiseCategoryBean getCategoryBean() {
        return categoryBean;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    public String toString(){
        return brandName;
    }
}
