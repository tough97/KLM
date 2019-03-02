package cs.designer.io.local;


import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Floor;
import com.klm.cons.impl.House;
import com.klm.cons.impl.Wall;
import com.klm.util.CSUtilException;
import cs.designer.module.TempWall;
import cs.designer.view.viewer.HousePlanView;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class HouseSaveLoader extends SaveLoader {
    private HousePlanView view;
    private boolean sucessLoad = true;

    public HouseSaveLoader(File file) {
        super(file);
    }

    public HouseSaveLoader(final File file, final HousePlanView view) {
        this(file);
        this.view = view;
    }

    public HouseSaveLoader(String filePath) {
        this(new File(filePath));
    }

    public HouseSaveLoader(URL url) {
        this(url.getFile());
    }

    @Override
    public House getObject() {
        House house = (House) loadObject();
        return house;
    }

    @Override
    public void setObject(Serializable object) {
        if (object instanceof House) {
            super.saveObject(object);
        }
    }

    public void run() {
        object = loadObject();
        if (object == null) {
            sucessLoad = false;
        }
        if (view != null) {
            view.setCurrentHouse((House) object);
        }
    }

    public boolean isSucessLoad() {
        return sucessLoad;
    }
}
