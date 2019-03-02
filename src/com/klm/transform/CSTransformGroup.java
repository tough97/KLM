/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.transform;

import java.util.HashSet;
import java.util.Set;
import javax.media.j3d.TransformGroup;

/**
 *
 * @author gang-liu
 */
public abstract class CSTransformGroup extends TransformGroup{
    
    private Set<TransformConstraint> constraints = new HashSet<TransformConstraint>();
    
    public CSTransformGroup(){
        super();
        setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    }
    
    
    
}
