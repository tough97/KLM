/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util;

import java.io.File;
import java.net.URL;
import javax.media.j3d.BranchGroup;

/**
 *
 * @author gang-liu
 */
public abstract class ModelImportor {

    public abstract String getSupportedFormat();

    public abstract BranchGroup importFromURL(final URL url) throws
            CSUtilException;

    public BranchGroup importFromFile(final File file) throws CSUtilException {
        try {
            return importFromURL(file.toURI().toURL());
        } catch (Exception ex) {
            throw new CSUtilException(ex);
        }
    }

    public BranchGroup importFromFile(final String fName) throws CSUtilException {
        try {
            return importFromFile(new File(fName));
        } catch (Exception ex){
            throw new CSUtilException(ex);
        }
    }
}
