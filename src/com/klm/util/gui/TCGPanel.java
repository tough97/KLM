/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util.gui;

import java.awt.GridLayout;
import javax.media.j3d.TexCoordGeneration;
import javax.swing.JPanel;

/**
 *
 * @author gang-liu
 */
public class TCGPanel extends JPanel{
    private static final int PLAN_S = 0;
    private static final int PLAN_T = 1;
    private static final int PLAN_R = 2;
    private static final int PLAN_Q = 3;
    private static final int HORIZONTAL_GAP = 20;
    private static final int VERTICAL_GAP = 20;
    
    private TexCoordGeneration tcg;
    private JPanel[] vectorPanels = new JPanel[4];
    private JPanel otherParamPanels = new JPanel();
    
    public TCGPanel(){
        setLayout(new GridLayout(5, 1, HORIZONTAL_GAP, VERTICAL_GAP));
        
        for(int index = PLAN_S; index < PLAN_Q; index++){
            vectorPanels[index] = new JPanel();
            vectorPanels[index].setLayout(new GridLayout());
        }
    }
    
    
}
