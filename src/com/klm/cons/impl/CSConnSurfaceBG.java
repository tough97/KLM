/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;


import javax.media.j3d.Node;

/**
 *
 * @author gang-liu
 */
public class CSConnSurfaceBG extends CSBranchGroup{
    public CSConnSurfaceBG(){
        super();
    }

    @Override
    public void addChild(final Node child){
        if(child instanceof Surface3D){
            super.addChild(child);
        }
    }

}
