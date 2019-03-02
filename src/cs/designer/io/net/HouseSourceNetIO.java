package cs.designer.io.net;

import com.klm.cons.impl.House;
import cs.designer.swing.bean.HouseBean;
import cs.designer.view.viewer.HousePlanView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/1/12
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class HouseSourceNetIO extends ClientNetIO {
    public static final String SERVER_URL_PATTERNS = "/house-service";
    private HousePlanView view;
    private HouseBean houseBean;

    public HouseSourceNetIO(final HousePlanView view,
                            HouseBean houseBean) {
        this.view = view;
        this.houseBean = houseBean;

    }

    public void run() {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE,
                    new StringBody("house_file"));
            reqEntity.addPart("house_id",
                    new StringBody(houseBean.getCode()));
            reqEntity.addPart("user_id",
                    new StringBody(houseBean.getOwnerUserCode()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = getMessage().getResponseObject();
            if (responseObject instanceof House) {
                view.setCurrentHouse((House) responseObject);
                view.setHouseBean(houseBean.clone());

            }else {
                setStatusCode(StatusCode.notFind);
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
