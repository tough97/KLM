/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.persist.impl;


import java.io.ObjectStreamField;
import java.io.Serializable;

/**
 * @author gang-liu
 */
public class MerchandiseProvider implements Serializable {
    private String id;
    private String name;
    private String city;
    private String province;
    private String mobile;
    private String email;
    private String QQ;

    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("id", String.class), new ObjectStreamField("name", String.class),
            new ObjectStreamField("city", String.class), new ObjectStreamField("province", String.class),
            new ObjectStreamField("mobile", String.class), new ObjectStreamField("email", String.class),
            new ObjectStreamField("QQ", String.class)
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQQ() {
        return QQ;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }

}
