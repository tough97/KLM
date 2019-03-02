package cs.designer.io.local;

import com.klm.cons.impl.House;
import com.klm.persist.Merchandise;

import java.io.File;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/2/12
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseSaveLoader extends SaveLoader {
    public MerchandiseSaveLoader(final File file) {
        super(file);

    }

    @Override
    public Merchandise getObject() {
        Merchandise merchandise = (Merchandise) loadObject();
        return merchandise;
    }

    @Override
    public void setObject(Serializable object) {
        if (object instanceof Merchandise) {
            super.saveObject(object);
        }
    }
}
