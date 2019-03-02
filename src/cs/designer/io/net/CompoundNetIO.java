package cs.designer.io.net;

import cs.designer.swing.bean.CompoundBean;
import cs.designer.swing.bean.LocationBean;
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
 * Date: 2/27/12
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompoundNetIO extends ClientNetIO implements NetOperation {
    public static final String SERVER_URL_PATTERNS = "/house-service";

    public enum OperationType {
        list_compounds
    }

    public enum Properties {
        city_code, rec_per_page, COMPOUND_CODE, COMPOUND_NAME
    }

    private LocationBean location;

    public CompoundNetIO(final LocationBean location) {
        this.location = location;
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
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.list_compounds.toString()));
            reqEntity.addPart(Properties.city_code.toString(), new StringBody(location.getCode()));
            if (pages != null) {
                reqEntity.addPart(PAGE_NUMBER, new StringBody(String.valueOf(pages.getCurrentPage())));
                reqEntity.addPart(RECORDS_PER_PAGE, new StringBody(String.valueOf(pages.getPageSize())));
            }
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = message.getResponseObject();
            if (responseObject != null) {
                location.getJurisdicLocations().clear();
                final List<Map<String, byte[]>> cityCompounds = (List<Map<String, byte[]>>) responseObject;
                for (final Map<String, byte[]> cityCompound : cityCompounds) {
                    final String compundCode = new String(cityCompound.get(Properties.COMPOUND_CODE.toString()), "UTF8");
                    final String compundName = new String(cityCompound.get(Properties.COMPOUND_NAME.toString()), "UTF8");
                    location.addjurisdicLocation(new CompoundBean(compundCode, compundName,
                            LocationBean.LocationType.COMPOUND));
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
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.list_compounds.toString()));
            reqEntity.addPart(QUERY_RS_COUNT, new StringBody("true"));
            reqEntity.addPart(Properties.city_code.toString(), new StringBody(location.getCode()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = message.getResponseObject();
            if (responseObject != null) {
                if (pages != null) {
                    pages.setDataSize(Integer.valueOf(responseObject.toString()));
                }
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        LocationBean city = new LocationBean("296", "");
        CompoundNetIO compoundNetIO = new CompoundNetIO(city);
        compoundNetIO.getPages(null);

    }

}
