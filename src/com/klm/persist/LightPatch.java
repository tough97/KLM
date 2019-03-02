package com.klm.persist;

import com.klm.material.LightSource;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 4/30/12
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class LightPatch {

    public static void main(String[] args) throws Exception{
        final File folder =new File("/home/gang-liu/Desktop/MERCHANDISE");
        if(!folder.isDirectory()){
            throw  new Exception(folder.getCanonicalPath()+" is not a directory");
        }
        for(final File merchandiseFile : folder.listFiles()){
            final ObjectInputStream in = new ObjectInputStream(new FileInputStream(merchandiseFile));
            final Object obj = in.readObject();
            in.close();

            final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(merchandiseFile));
            final Class clz = obj.getClass();
            final Field field = clz.getField("lights");
            if(field == null){
                throw new Exception("Light field is not found");
            } else{
                field.setAccessible(true);
                field.set(obj, new HashSet<LightSource>());
                field.setAccessible(false);
            }
            out.writeObject(obj);
        }
    }

}
