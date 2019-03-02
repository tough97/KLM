package cs.designer.io.net;

import cs.designer.swing.bean.CompoundBean;
import cs.designer.swing.bean.LayoutHouseBean;
import cs.designer.swing.bean.PageBean;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/28/12
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class CompoundLayoutNetIO extends ClientNetIO implements NetOperation {
    public static final String SERVER_URL_PATTERNS = "/house-service";
    private CompoundBean compound;

    public enum OperationType {
        list_layout
    }

    public enum Properties {
        compound_code
    }

    public CompoundLayoutNetIO(final CompoundBean compound) {
        this.compound = compound;
    }

    public boolean upload() {
        return false;
    }

    public void delete() {
    }

    public void update() {
    }

    public void select() {

    }

    public void list(final PageBean pages) {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.list_layout.toString()));
            reqEntity.addPart(Properties.compound_code.toString(), new StringBody(compound.getCode()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = message.getResponseObject();
            if (responseObject != null) {
                final List<Map<String, byte[]>> layoutHouses = (List<Map<String, byte[]>>) responseObject;
                for (final Map<String, byte[]> layoutInfo : layoutHouses) {
                    String layoutHouseCode = new String(layoutInfo.get("LAYOUT_CODE"));
                    LayoutHouseBean compoundLayouts = new LayoutHouseBean(layoutHouseCode, layoutHouseCode);
                    compound.addHouseLayoitHouse(compoundLayouts);
                    compoundLayouts.setConstrucitionArea(toFloat(layoutInfo.get("CONSTRUCTION_AREA")));
                    compoundLayouts.setUtilizableArea(toFloat(layoutInfo.get("UTILIZABLE_AREA")));
                    compoundLayouts.setBadeRoomNum(toInt(layoutInfo.get("NUM_BEDROOM")));
                    compoundLayouts.setLivingRoomNum(toInt(layoutInfo.get("NUM_LIVINGROOM")));
                    compoundLayouts.setBathRoomNum(toInt(layoutInfo.get("NUM_BATHROOM")));
                    compoundLayouts.setKitchenRoomNum(toInt(layoutInfo.get("NUM_KITCHEN")));
                }
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getPages(final PageBean pages) {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.list_layout.toString()));
            reqEntity.addPart(Properties.compound_code.toString(), new StringBody(compound.getCode()));
            reqEntity.addPart(QUERY_RS_COUNT, new StringBody("true"));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = message.getResponseObject();
            System.out.println(responseObject);

        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CompoundBean compoundBean = new CompoundBean("001", "");
        CompoundLayoutNetIO layoutHouseNetIO = new CompoundLayoutNetIO(compoundBean);
        layoutHouseNetIO.getPages(null);

    }
}
