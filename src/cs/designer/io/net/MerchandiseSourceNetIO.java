package cs.designer.io.net;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.House;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.CSPersistException;
import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.FurnitureModel;
import com.klm.persist.impl.LocalStorage;
import com.klm.persist.impl.SurfaceMaterial;
import cs.designer.swing.bean.MerchandiseBean;
import cs.designer.swing.undo.HouseEdit;
import cs.designer.swing.undo.SurfaceMaterialUndoEditor;
import cs.designer.view.viewer.HousePlanView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import javax.imageio.ImageIO;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;
import javax.xml.crypto.dsig.Transform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/2/12
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseSourceNetIO extends ClientNetIO {
    public static final String SERVER_URL_PATTERNS = "/merchandise-service";
    private Merchandise merchandise;
    private Surface3D surface3D;

    public MerchandiseSourceNetIO(final Merchandise merchandise,
                                  final Surface3D surface3D) {
        this.merchandise = merchandise;
        this.surface3D = surface3D;
    }

    public void run() {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE,
                    new StringBody("get_mer_file"));
            reqEntity.addPart("merch_id",
                    new StringBody(merchandise.getId()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = getMessage().getResponseObject();
            if (responseObject != null) {
                Merchandise loadMerchandise = (Merchandise) responseObject;
                Merchandise.merchandises.put(loadMerchandise.getId(), loadMerchandise.clone());
                if (merchandise instanceof Furniture) {
                    final Furniture furniture = (Furniture) merchandise;
                    Furniture loadFurniture = (Furniture) loadMerchandise;
                    furniture.setModel(loadFurniture.getModel().clone());
                    final Vector3d movePoint = new Vector3d();
                    furniture.getTranslationTransform().get(movePoint);
                    movePoint.setZ(surface3D.calculateSurfaceNormal().z * loadFurniture.getModel().getWidth() / 2);
                    final Transform3D trans = new Transform3D();
                    trans.set(movePoint);
                    furniture.getTranslationTG().setTransform(trans);
                    loadFurniture.setId(merchandise.getId());
                    furniture.setUnitName(loadFurniture.getUnitName());
                    LocalStorage.getLocalStorage().writeMerchandize(loadFurniture);
                    furniture.getModel().toXYPlan(surface3D.calculateSurfaceNormal());
                    loadFurniture=null;
                } else if (merchandise instanceof SurfaceMaterial) {
                     SurfaceMaterial loadSurfaceMaterial = (SurfaceMaterial) loadMerchandise;
                    loadSurfaceMaterial.setId(merchandise.getId());
                    loadSurfaceMaterial.setUnitName(merchandise.getUnitName());
                    HouseEdit.getHouseEditor().joinObject(new SurfaceMaterialUndoEditor(surface3D));
                    surface3D.setSurfaceMaterial(loadSurfaceMaterial);
                    LocalStorage.getLocalStorage().writeMerchandize(loadSurfaceMaterial);
                    loadSurfaceMaterial=null;
                }
            }

        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (CSPersistException e) {
            e.printStackTrace();
        } catch (CSHouseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    protected void finalize() {
        getMessage().setResponseObject(null);
    }
}
