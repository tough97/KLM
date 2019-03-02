package com.klm.material.impl;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 4/30/12
 * Time: 7:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class MaterialColor {

    private Color darkColor;
    private Color originalColor;
    private Color brightColor;

    public MaterialColor(){
        darkColor = new Color(0xaaaaaa);
        originalColor = new Color(0xcccccc);
        brightColor = new Color(0xffffff);
    }

    public MaterialColor(final MaterialColor color){
        this.darkColor = new Color(color.getDarkColor().getRGB());
        this.originalColor = new Color(color.getOriginalColor().getRGB());
        this.darkColor = new Color(color.getBrightColor().getRGB());
    }

    public MaterialColor(final Color darkColor, final Color originalColor, final Color brightColor) {
        this.darkColor = darkColor;
        this.originalColor = originalColor;
        this.brightColor = brightColor;
    }

    public Color getDarkColor() {
        return darkColor;
    }

    public void setDarkColor(Color darkColor) {
        this.darkColor = darkColor;
    }

    public Color getOriginalColor() {
        return originalColor;
    }

    public void setOriginalColor(Color originalColor) {
        this.originalColor = originalColor;
    }

    public Color getBrightColor() {
        return brightColor;
    }

    public void setBrightColor(Color brightColor) {
        this.brightColor = brightColor;
    }

    public MaterialColor darker(){
        darkColor = new Color((int)(darkColor.getRGB() * 0.8));
        originalColor = new Color((int)(originalColor.getRGB() * 0.8));
        darkColor = new Color((int)(darkColor.getRGB() * 0.8));
        return this;
    }

    public MaterialColor brighter(){
        darkColor = new Color(((int)(darkColor.getRGB() * 1.2) > 255 ? 255 : (int)(darkColor.getRGB() * 1.2)));
        originalColor = new Color(((int)(originalColor.getRGB() * 1.2) > 255 ? 255 : (int)(originalColor.getRGB() * 1.2)));
        brightColor = new Color(((int)(brightColor.getRGB() * 1.2) > 255 ? 255 : (int)(brightColor.getRGB() * 1.2)));
        return this;
    }

}
