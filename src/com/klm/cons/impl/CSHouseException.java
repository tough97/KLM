/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;

/**
 *
 * @author gang-liu
 */
public class CSHouseException extends Exception{

    public CSHouseException(final Throwable t) {
        super(t);
    }
    
    public CSHouseException(final String msg) {
        super(msg);
    }
        
}
