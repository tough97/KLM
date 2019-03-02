/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.persist;

import com.klm.cons.impl.CSBranchGroup;
import com.klm.material.LightSource;
import com.klm.material.impl.CSLightSource;
import com.klm.persist.impl.MerchandiseProvider;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gang-liu
 */

public abstract class Merchandise extends CSBranchGroup implements Serializable {
    protected String name;
    protected String id;
    protected String description;
    protected MerchandiseProvider mp = new MerchandiseProvider();
    protected double installationPrice = 0.0;
    protected double unitPrice = 0.0;
    protected double logisticsPrice = 0.0;
    protected String unitName;
    protected String providerID;
    public static  final Map<String,Merchandise> merchandises =new HashMap<String,Merchandise>();
//    protected CSLightSource light;

    private static final long serialVersionUID = 100;
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("name", String.class), new ObjectStreamField("id", String.class),
            new ObjectStreamField("description", String.class), new ObjectStreamField("mp", MerchandiseProvider.class),
            new ObjectStreamField("installationPrice", double.class), new ObjectStreamField("unitPrice", double.class),
            new ObjectStreamField("logisticsPrice", double.class), new ObjectStreamField("unitName", String.class),
            new ObjectStreamField("providerID", String.class)
    };

    public Merchandise() {
    }

    public Merchandise(final String id) {
        this.id = id;
        initiate();
    }

    public double getLogisticsPrice() {
        return logisticsPrice;
    }

    public void setLogisticsPrice(double logisticsPrice) {
        this.logisticsPrice = logisticsPrice;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MerchandiseProvider getMp() {
        return mp;
    }

    public void setMp(MerchandiseProvider mp) {
        this.mp = mp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getInstallationPrice() {
        return installationPrice;
    }

    public void setInstallationPrice(double installationPrice) {
        this.installationPrice = installationPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

//    public void addLight(final int lightType) {
//        if (light != null) {
//            removeLight();
//        }
//        light = new CSLightSource(lightType);
//        addChild(light);
//    }
//
//    public void removeLight() {
//        removeChild((CSLightSource)light);
//    }
//
//    public LightSource getLight() {
//        return light;
//    }

    public abstract String getFileName();

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Merchandise) {
            return getId() == ((Merchandise) obj).getId() && obj.getClass().equals(this.getClass());
        } else {
            return false;
        }
    }

//    private void writeObject(final ObjectOutputStream oos) throws IOException,
//            NamedObjectException {
//
//    }

//    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
//        System.out.println("123123");
//        ois.defaultReadObject();
//        if (ois.available() > 0) {
//            lights = (HashSet<LightSource>) ois.readObject();
//            if (lights == null) {
//                lights = new HashSet<LightSource>();
//            }
//        }
//    }

    @Override
    public int hashCode() {
        return getId().hashCode() + getClass().hashCode();
    }

    public abstract Merchandise clone();
}
