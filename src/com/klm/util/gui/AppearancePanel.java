/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util.gui;

import javax.media.j3d.Appearance;
import javax.swing.JScrollPane;

/**
 *
 * @author gang-liu
 */
public class AppearancePanel extends JScrollPane{
    
    private Appearance app;
    
    public AppearancePanel(){
        super();
        setSize(200, 500);
    }
    
    public void setAppearance(final Appearance app){
        this.app = app;
    }
    
    public Appearance getAppearance(){
        return app;
    }
    
    
    
}
