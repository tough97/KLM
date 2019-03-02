package cs.designer.io.net;

import cs.designer.swing.bean.CompoundBean;
import cs.designer.swing.bean.HouseBean;
import cs.designer.swing.bean.PageBean;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/11/12
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class CompoundHouseNetIO extends ClientNetIO implements NetOperation {
    public static final String SERVER_URL_PATTERNS = "/house-service";
    private CompoundBean compound;


    public enum OperationType {
        list_com_house
    }

    public enum Properties {
        compound_code
    }

    public CompoundHouseNetIO(final CompoundBean compound) {
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
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.list_com_house.toString()));
            reqEntity.addPart(Properties.compound_code.toString(), new StringBody(compound.getCode()));
            if (pages != null) {
                reqEntity.addPart(PAGE_NUMBER, new StringBody(String.valueOf(pages.getCurrentPage())));
                reqEntity.addPart(RECORDS_PER_PAGE, new StringBody(String.valueOf(pages.getPageSize())));
            }
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = message.getResponseObject();
            if (response.getStatusLine().getStatusCode() == SC_ACCEPT) {
                final List<Map<String, byte[]>> compoundHouses = (List<Map<String, byte[]>>) responseObject;
                compound.getHouses().clear();
                for (final Map<String, byte[]> houseInfo : compoundHouses) {
                    final HouseBean house = new HouseBean();
                    house.setCode(new String(houseInfo.get("HOUSE_CODE"), "UTF-8"));
                    house.setOwnerUserCode(new String(houseInfo.get("USER_ID"), "UTF-8"));
                    house.setDescription(new String(houseInfo.get("HOUSE_DESCRIPTION"),"UTF-8"));
                    house.setSneapView(ImageIO.read(new ByteArrayInputStream(houseInfo.get("HOUSE_ICON_VIEW"))));
                    compound.addHouse(house);
                }

            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getPages(final PageBean pages) {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.list_com_house.toString()));
            reqEntity.addPart(QUERY_RS_COUNT, new StringBody("true"));
            reqEntity.addPart(Properties.compound_code.toString(), new StringBody(compound.getCode()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = message.getResponseObject();
            if (responseObject!=null) {
                pages.setDataSize(Integer.valueOf(responseObject.toString()));
                System.out.println(responseObject);
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final CompoundBean compoundBean = new CompoundBean("002", "");
        final NetOperation compoundHouse = new CompoundHouseNetIO(compoundBean);
        compoundHouse.getPages(null);
    }
}
