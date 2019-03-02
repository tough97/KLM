package cs.designer.module;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Surface3D;
import com.klm.cons.impl.Wall;
import com.klm.persist.CSPersistException;
import com.klm.persist.impl.SurfaceMaterial;
import com.klm.persist.meta.BufferedImageMeta;
import cs.designer.swing.resources.ResourcesPath;

import javax.imageio.ImageIO;
import javax.vecmath.Point3d;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/26/12
 * Time: 11:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class SurfaceMaterialDisplayFace {
    public final static double DEFAULT_WIDTH = 1;
    public final static double DEFAULT_HEIGHT =1;
    private Surface3D surface;

    public SurfaceMaterialDisplayFace(final SurfaceMaterial loadMaterial) {
        init(loadMaterial);
    }

    private void init(final SurfaceMaterial loadMaterial) {
        Point3d[] coords = {new Point3d(-DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 2, 0),
                new Point3d(-DEFAULT_WIDTH / 2, -DEFAULT_HEIGHT / 2, 0),
                new Point3d(DEFAULT_WIDTH / 2, -DEFAULT_HEIGHT / 2, 0),
                new Point3d(DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 2, 0)};
        try {
            surface = new Surface3D(coords);
            surface.setSurfaceMaterial(loadMaterial);
        } catch (CSHouseException e) {
            e.printStackTrace();
        }
    }

    public Surface3D getSurface() {
        return surface;
    }
}
