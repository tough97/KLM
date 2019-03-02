package com.klm.cons;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 12/6/11
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class GroupConstrains implements Serializable {

    private Set<Class> acceptClasses = new HashSet<Class>();
    private Set<Class> deniedClasses = new HashSet<Class>();

    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("acceptClasses", Set.class),
            new ObjectStreamField("deniedClasses", Set.class)
    };

    public GroupConstrains(){
        acceptClasses.add(Serializable.class);
    }

    public boolean accepts(final Object obj) {
        if (obj == null) {
            return false;
        }
        return acceptClasses.contains(obj.getClass());
    }

    public boolean setAcceptable(final Class clz) {
        if (!deniedClasses.contains(clz)) {
            acceptClasses.add(clz);
            return true;
        } else {
            return false;
        }
    }

    public boolean setDeniable(final Class clz) {
        boolean ret = false;
        if (acceptClasses.contains(clz)) {
            ret = true;
            acceptClasses.remove(clz);
        }
        if (!deniedClasses.contains(clz)) {
            ret = true;
            deniedClasses.add(clz);
        }
        return ret;
    }

}
