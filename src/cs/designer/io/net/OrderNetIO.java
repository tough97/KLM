package cs.designer.io.net;

import cs.designer.swing.bean.*;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/5/12
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrderNetIO extends ClientNetIO implements NetOperation {
    public static final String SERVER_URL_PATTERNS = "/order";
    public static final String REQUEST_TYPE = "pvkort";
    public static final String MERCHANDISES = "pvkms";
    public static final String ORDER_USER_ID = "pvkud";
    public static final String ORDER_STATE = "pvkos";
    public static final String ORDER_CITY_CODE = "pvkocc";
    public static final String ORDER_PROVINCE_CODE = "pvkopc";
    public static final String ORDER_ADDRESS = "pvkoa";
    public static final String CONSIGNEE_NAME = "pvkcn";
    public static final String CONSIGNEE_MOBILE = "pvkcm";
    public static final String SEND_TIME = "pvkst";
    public static final String COMPLETE_ABLE = "pvkca";


    private OrderBean orderBean;

    public OrderNetIO(OrderBean order) {
        super();
        this.orderBean = order;
    }

    public boolean upload() {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(REQUEST_TYPE,
                    new StringBody("0"));
            reqEntity.addPart(MERCHANDISES,
                    new StringBody(orderBean.getMerchandisesInfo().toString(), Charset.forName(HTTP.UTF_8)));
            reqEntity.addPart(ORDER_USER_ID,
                    new StringBody(orderBean.getUser().getCode()));
            //
            reqEntity.addPart(ORDER_STATE,
                    new StringBody(orderBean.getState(),
                            Charset.forName(HTTP.UTF_8)));
            reqEntity.addPart(ORDER_CITY_CODE,
                    new StringBody(orderBean.getCityCode()));
            reqEntity.addPart(ORDER_PROVINCE_CODE,
                    new StringBody(orderBean.getProvinceCode()));
              reqEntity.addPart(CONSIGNEE_NAME,
                    new StringBody(orderBean.getConsigneeName(), Charset.forName(HTTP.UTF_8)));
             reqEntity.addPart(CONSIGNEE_MOBILE,
                    new StringBody(orderBean.getConsigneeMobile(), Charset.forName(HTTP.UTF_8)));
                reqEntity.addPart(CONSIGNEE_MOBILE,
                    new StringBody(orderBean.getConsigneeMobile(), Charset.forName(HTTP.UTF_8)));
            reqEntity.addPart(SEND_TIME,
                    new StringBody(orderBean.getSendTime(), Charset.forName(HTTP.UTF_8)));
                reqEntity.addPart(ORDER_ADDRESS,
                    new StringBody(orderBean.getConsigneeAddress(), Charset.forName(HTTP.UTF_8)));

            reqEntity.addPart(COMPLETE_ABLE,
                    new StringBody(String.valueOf(orderBean.isCompleteAble()), Charset.forName(HTTP.UTF_8)));

            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            final JSONObject responseObject = getResponseJSon(response);
            if (responseObject != null) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), responseObject.getString("result"), "",
                        JOptionPane.INFORMATION_MESSAGE);

            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
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

    public static void main(String[] args) {


    }

}