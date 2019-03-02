package cs.designer.io.net;

import cs.designer.swing.bean.PageBean;
import cs.designer.swing.bean.UserBean;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/12/12
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserPhoneAuthorizNetIO extends ClientNetIO implements NetOperation {
    public static final String SERVER_URL_PATTERNS = "/authen";
    public static final String REQUEST_TYPE = "pkart";
    public static final String USER_MOBILE = "pkaum";
    public static final String USER_ID = "pkaui";

    private UserBean userBean;

    public UserPhoneAuthorizNetIO(final UserBean userBean) {
        super();
        this.userBean = userBean;
    }

    public boolean upload() {
        return false;
    }

    public void delete() {
    }

    public void update() {

        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(REQUEST_TYPE,
                    new StringBody("10"));
            reqEntity.addPart(USER_ID,
                    new StringBody(userBean.getCode()));
            reqEntity.addPart(USER_MOBILE,
                    new StringBody(userBean.getMobile()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = getMessage().getResponseObject();
            if (responseObject != null) {
                System.out.println(responseObject);
            }
        } catch (final UnsupportedEncodingException e) {

        } catch (IOException e) {
        }
    }

    public void select() {
    }

    public void list(PageBean pages) {
    }

    public void getPages(PageBean pageBean) {
    }


}
