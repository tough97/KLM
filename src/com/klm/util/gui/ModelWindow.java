/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util.gui;

import com.klm.exhib.ModelDisplayer;
import com.klm.util.CSUtilException;
//import com.klm.util.impl.OBJModelImportor;
import java.awt.AWTException;
import java.awt.Color;
import javax.media.j3d.Node;
import javax.swing.JFrame;

/**
 * This class is used to display 
 * @author gang-liu
 */
public class ModelWindow extends JFrame {

    public static final String TITLE = "考拉猫--展示你的梦想";

    public ModelWindow(final int width, final int height, final Node model) throws
            AWTException {
        super(TITLE);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final ModelDisplayer md = new ModelDisplayer();
        md.setModel(model);
        md.addLights(ModelDisplayer.createExhibitionLights(Color.white));
        getContentPane().add(md);
        setVisible(true);
    }

    public static void main(String[] args) throws AWTException, CSUtilException {
//        final JFrame frame = new ModelWindow(600, 400, new OBJModelImportor().
//                importFromFile("/home/gang-liu/Desktop/lbjn/lanbojini.obj"));
    }
}
