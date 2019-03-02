/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.designer.view.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author rongyang
 */
public abstract class KeyboardControler extends KeyAdapter {

    public abstract void controlerkeyTyped(KeyEvent ke);

    public abstract void controlerkeyPressed(KeyEvent ke);

    public abstract void controlerkeyReleased(KeyEvent ke);
}
