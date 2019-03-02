package cs.designer.swing.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/23/12
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseCategoryBean implements CoBean {
    private MerchandiseCategoryBean parent;
    protected String categoryName;
    protected String categoryId;
    private List<MerchandiseCategoryBean> childCategory;
    private List<MerchandiseBrandBean> brands;

    public MerchandiseCategoryBean() {
        childCategory = new ArrayList<MerchandiseCategoryBean>();
        brands = new ArrayList<MerchandiseBrandBean>();
    }


    public MerchandiseCategoryBean(String categoryId,
                                   String categoryName) {
        this(categoryId);
        setCategoryName(categoryName);
    }

    public MerchandiseCategoryBean(final String id) {
        this();
        this.categoryId = id;

    }

    public void addSubCategory(final MerchandiseCategoryBean subCategory) {
        childCategory.add(subCategory);
        subCategory.parent = this;
    }

    public void addMerchandiseBran(final MerchandiseBrandBean brandBean) {
        this.brands.add(brandBean);
    }

    public List<MerchandiseCategoryBean> getChildCategory() {
        return childCategory;
    }

    public MerchandiseCategoryBean getParent() {
        return parent;
    }


    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setChildCategory(final List<MerchandiseCategoryBean> childCategory) {
        this.childCategory = childCategory;
    }

    public String createDescription() {
        return "";
    }

    public String getCode() {
        return categoryId;
    }

    public String getName() {
        return categoryName;
    }

    public List<MerchandiseBrandBean> getBrands() {
        return brands;
    }

    public String toString() {
        return categoryName;
    }
}
