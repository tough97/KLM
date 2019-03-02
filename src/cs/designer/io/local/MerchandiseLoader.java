package cs.designer.io.local;


import com.klm.cons.impl.CSHouseException;
import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.persist.CSPersistException;
import com.klm.persist.impl.LocalStorage;
import com.klm.persist.impl.LocalTempStorage;
import com.klm.persist.impl.SurfaceMaterial;
import com.klm.persist.meta.BufferedImageMeta;
import com.klm.persist.meta.ModelMeta;
import cs.designer.swing.resources.ResourcesPath;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.MalformedURLException;
import java.util.UUID;


public class MerchandiseLoader {

    public Merchandise load(final File modelFile) {
        Merchandise merchandise = null;
        try {
            String fileName = modelFile.getName().toLowerCase();
            if (fileName.endsWith(
                    FileExtensionUtill.LOAD_MODEL_EXTENSION_NAME)) {
                ModelMeta model = new ModelMeta();
                final OBJLoader loader = new OBJLoader();
                model.setBaseModel(loader.load(modelFile.toURI().toURL()).getSceneGroup());
                model.showOutLines();
                merchandise = new Furniture(UUID.randomUUID().toString(), model);
            } else {
                merchandise = new SurfaceMaterial("display", 0.2f, 0.2f, false, new BufferedImageMeta(ImageIO.
                        read(modelFile)));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CSPersistException e) {
            e.printStackTrace();
        }
        return merchandise;
    }

}



