/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.persist.impl;

import com.klm.persist.CSPersistException;
import com.klm.persist.Merchandise;
import com.klm.persist.meta.BufferedImageMeta;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.Texture;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gang-liu
 */
public class SurfaceMaterial extends Merchandise implements Serializable {
    private BufferedImageMeta image;
    private Texture imageTexture;
    private double width;
    private double height;
    private boolean stretch = false;
    private boolean reflective = false;
    public static final String EXTENSION = ".sm";
    public static final String FILE_NAME_CONNECTOR = "_";
    public static final String NULL_SURFACE_MATERIAL = "@ns";

    private static final long serialVersionUID = 100;

    public SurfaceMaterial(final String id) {
        super(id);
        setMp(new MerchandiseProvider());
    }

    public SurfaceMaterial(final String id, double width, double height, boolean reflective, BufferedImageMeta image) {
        super(id);
        this.width = width;
        this.height = height;
        this.reflective = reflective;
        this.image = image;
        imageTexture = new TextureLoader(this.image.getImage()).getTexture();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public BufferedImageMeta getImage() {
        return image;
    }

    public BufferedImage getBufferredImage() {
        return image.getImage();
    }

    public void setBufferedImage(final BufferedImage image, final double width, final double height) throws CSPersistException {
        this.image = new BufferedImageMeta(image);
        imageTexture = new TextureLoader(image).getTexture();
        this.width = width;
        this.height = height;
        this.height = height;
    }

    public void setImage(final BufferedImageMeta image, final double width, final double height) {
        this.image = image;
        imageTexture = new TextureLoader(image.getImage()).getTexture();
        this.width = width;
        this.height = height;
    }

    public Texture getImageTexture() {
        return imageTexture;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public boolean isReflective() {
        return reflective;
    }

    public void setReflective(final boolean reflective) {
        this.reflective = reflective;
    }

    public String getFileName() {
        final String id = getId();
        final StringBuilder sb = new StringBuilder(getId());
        sb.append(FILE_NAME_CONNECTOR).append(serialVersionUID).append(EXTENSION);
        return sb.toString();
    }

    public boolean isStretch() {
        return stretch;
    }

    public void setStretch(boolean stretch) {
        this.stretch = stretch;
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException{
        oos.writeObject(image);
        oos.writeDouble(width);
        oos.writeDouble(height);
        oos.writeBoolean(reflective);
        oos.flush();
    }

    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        image = (BufferedImageMeta) ois.readObject();
        width = ois.readDouble();
        height = ois.readDouble();
        reflective = ois.readBoolean();

        imageTexture = new TextureLoader(image.getImage()).getTexture();
        System.gc();
    }

    @Override
    public boolean equals(final Object obj){
        if(obj instanceof SurfaceMaterial){
            return getId() == ((Merchandise) obj).getId() && obj.getClass().equals(this.getClass());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
        return getId().hashCode() + getClass().hashCode();
    }

    @Override
    public SurfaceMaterial clone(){
        final SurfaceMaterial cloneSurfaceMaterial=
                new SurfaceMaterial(this.id,this.width,this.height,this.reflective,this.image);
    cloneSurfaceMaterial.name= this.name;
    cloneSurfaceMaterial.description=this.description;
    cloneSurfaceMaterial. mp = this.mp;
    cloneSurfaceMaterial.installationPrice = this.installationPrice;
    cloneSurfaceMaterial.unitPrice = this.unitPrice;
    cloneSurfaceMaterial.logisticsPrice = this.logisticsPrice;
    cloneSurfaceMaterial.unitName=this.unitName;
    cloneSurfaceMaterial.providerID=this.providerID;
       return  cloneSurfaceMaterial;
    }
}