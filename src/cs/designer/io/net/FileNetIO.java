package cs.designer.io.net;

import com.sun.corba.se.spi.orb.StringPair;
import cs.designer.swing.bean.FileBean;
import cs.designer.swing.bean.PageBean;
import cs.designer.utils.FileUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/5/12
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileNetIO extends ClientNetIO implements NetOperation {
    public static final String SERVER_URL_PATTERNS = "/temp-file";
    private FileBean fileBean;

    public FileNetIO(final FileBean fileBean) {
        this.fileBean = fileBean;
    }

    public boolean upload() {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        boolean success = false;
        final FileBody file = new FileBody(fileBean.getSourceFile());
        reqEntity.addPart("file", file);
        post.setEntity(reqEntity);
        final HttpResponse response = requestToServer(post);
        final JSONObject responseObject = getResponseJSon(response);
        if (responseObject != null) {
            final String url = responseObject.getString("url");
            fileBean.setServerPath(url);
            success = true;
        } else {
            setStatusCode(StatusCode.notFind);
        }
        if (getStatusCode() != StatusCode.normal) {
            success = false;
        }
        return success;
    }

    public void delete() {

    }

    public void update() {

    }

    public void select() {


    }

    public void list(PageBean pages) {

    }

    public void getPages(PageBean pageBean) {

    }
}
