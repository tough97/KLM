package cs.designer.io.net;

import cs.designer.swing.bean.LayoutHouseBean;
import cs.designer.swing.bean.PageBean;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/29/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class LayoutHouseNetIO extends ClientNetIO implements NetOperation {
    public static final String SERVER_URL_PATTERNS = "/house-service";
    private LayoutHouseBean layout;

    public enum OperationType {
        list_com_layout
    }

    public enum Properties {
        house_layout_code, compound_code
    }

    public LayoutHouseNetIO(final LayoutHouseBean layout) {
        this.layout = layout;
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

    public void getPages(final PageBean pages) {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.list_com_layout.toString()));

            reqEntity.addPart(Properties.house_layout_code.toString(), new StringBody(layout.getCode()));
            reqEntity.addPart(Properties.compound_code.toString(), new StringBody(layout.getCompoundCode()));
            reqEntity.addPart(QUERY_RS_COUNT, new StringBody("true"));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void list(final PageBean pages) {


    }

    public static void main(String[] args) {
        final LayoutHouseBean layoutHouse = new LayoutHouseBean();
        layoutHouse.setCompoundCode("001");
        layoutHouse.setCode("001");
        final NetOperation clientNetIO = new LayoutHouseNetIO(layoutHouse);
        clientNetIO.getPages(null);


    }
}
