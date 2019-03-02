package cs.designer.io.net;

import com.klm.cons.impl.House;
import cs.designer.io.local.HouseSaveLoader;
import cs.designer.io.local.LocalFileManage;
import cs.designer.swing.bean.FileBean;
import cs.designer.swing.bean.PageBean;
import cs.designer.swing.ui.WaitingDialog;
import cs.designer.view.viewer.HousePlanView;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import javax.swing.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/12/12
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class HouseFileNetIO extends ClientNetIO {
    public static final String SERVER_URL_PATTERNS = "/file";

    private FileBean fileBean;
    private HousePlanView view;

    public HouseFileNetIO(final FileBean fileBean, final HousePlanView view) {
        this.fileBean = fileBean;
        this.view = view;
    }

    public void run() {
        if (!fileBean.getSourceFile().exists()) {
            final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
            final MultipartEntity reqEntity = new MultipartEntity();
            try {
                reqEntity.addPart("pvkfr",
                        new StringBody("0"));
                reqEntity.addPart("pvkfp",
                        new StringBody(fileBean.getServerPath()));
                post.setEntity(reqEntity);
                final HttpResponse response = requestToServer(post);
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream in = entity.getContent();
                    if (in != null) {
                        final OutputStream os = new FileOutputStream(fileBean.getSourceFile());
                        int bytesRead = 0;
                        byte[] buffer = new byte[1024 * 2];
                        while ((bytesRead = in.read(buffer, 0, 1024 * 2)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        os.close();
                        in.close();
                    }

                }

            } catch (final UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileBean.getSourceFile().exists()) {
            LocalFileManage.importLocalFile(fileBean.getSourceFile(), view);
        }

    }
}
