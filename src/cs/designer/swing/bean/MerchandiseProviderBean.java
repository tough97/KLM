package cs.designer.swing.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/13/12
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseProviderBean implements CoBean {
    private MerchandiseBean merchandise;
    private Map<ProviderBean, Float> providerPrice;

    public MerchandiseProviderBean() {
        providerPrice = new HashMap<ProviderBean, Float>();

    }

    public MerchandiseProviderBean(final MerchandiseBean merchandise) {
        this();
        this.merchandise = merchandise;

    }

    public void addProvider(final ProviderBean provider, float price) {
        providerPrice.put(provider, price);
    }

    public MerchandiseBean getMerchandise() {
        return merchandise;
    }

    public Map<ProviderBean, Float> getProviderPrice() {
        return providerPrice;
    }

    public void setMerchandise(MerchandiseBean merchandise) {
        this.merchandise = merchandise;
    }

    public String createDescription() {
        return "";
    }

    public String getCode() {
        return merchandise.getCode();
    }

    public String getName() {
        return merchandise.getName();
    }
}
