package com.klm.persist.meta;

import javax.media.j3d.Material;
import javax.media.j3d.NodeComponent;
import javax.vecmath.Color3f;
import java.awt.*;
import java.io.*;

public class MaterialMeta extends Material implements Serializable {
    private static final long serialVersionUID = 100;
    private Float opticalDensity;
    private Integer illuminationModel;
    private Float sharpness;


    public MaterialMeta() {
        super();
    }

    public MaterialMeta(final Material material) {
        super();
        setCapability(Material.ALLOW_COMPONENT_READ);
        setCapability(Material.ALLOW_COMPONENT_WRITE);
        final Color3f ambientColor = new Color3f();
        material.getAmbientColor(ambientColor);
        setAmbientColor(ambientColor);
        final Color3f emissiveColor = new Color3f();
        material.getEmissiveColor(emissiveColor);
        setEmissiveColor(emissiveColor);
        final Color3f diffuseColor = new Color3f();
        material.getDiffuseColor(diffuseColor);
        setDiffuseColor(diffuseColor);
        final Color3f specularColor = new Color3f();
        material.getSpecularColor(specularColor);
        setSpecularColor(specularColor);
        setShininess(material.getShininess());
        setLightingEnable(material.getLightingEnable());
        setColorTarget(material.getColorTarget());
    }

    public void setOpticalDensity(final float opticalDensity) {
        this.opticalDensity = opticalDensity;
    }


    public float getOpticalDensity() {
        if (this.opticalDensity != null) {
            return this.opticalDensity;
        } else {
            throw new IllegalStateException("Optical density not set");
        }
    }


    public boolean isOpticalDensitySet() {
        return this.opticalDensity != null;
    }


    public void setIlluminationModel(int illuminationModel) {
        this.illuminationModel = illuminationModel;
    }


    public int getIlluminationModel() {
        if (this.illuminationModel != null) {
            return this.illuminationModel;
        } else {
            throw new IllegalStateException("Optical density not set");
        }
    }


    public boolean isIlluminationModelSet() {
        return this.illuminationModel != null;
    }


    public void setSharpness(float sharpness) {
        this.sharpness = sharpness;
    }


    public float getSharpness() {
        if (this.sharpness != null) {
            return this.sharpness;
        } else {
            throw new IllegalStateException("Sharpness not set");
        }
    }


    public boolean isSharpnessSet() {
        return this.sharpness != null;
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        final Color3f tempColor = new Color3f();
        getAmbientColor(tempColor);
        oos.writeObject(new Color3f(tempColor));
        getEmissiveColor(tempColor);
        oos.writeObject(new Color3f(tempColor));
        getDiffuseColor(tempColor);
        oos.writeObject(new Color3f(tempColor));
        getSpecularColor(tempColor);
        oos.writeObject(new Color3f(tempColor));
        oos.writeFloat(getShininess());
        oos.writeBoolean(getLightingEnable());
        oos.writeInt(getColorTarget());

    }

    private void readObject(final ObjectInputStream objectInputStream)
            throws ClassNotFoundException, IOException {
        setAmbientColor((Color3f) objectInputStream.readObject());
        setEmissiveColor((Color3f) objectInputStream.readObject());
        setDiffuseColor((Color3f) objectInputStream.readObject());
        setSpecularColor((Color3f) objectInputStream.readObject());
        float shininess = objectInputStream.readFloat();
        setShininess(shininess);
        boolean enables = objectInputStream.readBoolean();
        setLightingEnable(enables);
        int target = objectInputStream.readInt();
        setColorTarget(target);

    }


    @Override
    public NodeComponent cloneNodeComponent(final boolean forceDuplicate) {
        MaterialMeta material = new MaterialMeta();
        material.duplicateNodeComponent(this, forceDuplicate);
        material.opticalDensity = this.opticalDensity;
        material.illuminationModel = this.illuminationModel;
        material.sharpness = this.sharpness;
        return material;
    }
}