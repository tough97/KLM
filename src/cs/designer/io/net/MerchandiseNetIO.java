package cs.designer.io.net;

import com.klm.persist.impl.LocalStorage;
import cs.designer.swing.bean.MerchandiseBean;
import cs.designer.swing.bean.MerchandiseBrandBean;
import cs.designer.swing.bean.PageBean;
import cs.designer.swing.bean.ProviderBean;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/7/12
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseNetIO extends ClientNetIO implements NetOperation {
    public static final String SERVER_URL_PATTERNS = "/merchandise";
    public static final int UPLOAD_MERCHANDISE_INFO = 0;
    public static final int SELECT_MERCHANDISE_INFO = 3;
    public static final int UPDATE_MERCHANDISE_HOT = 12;
    public static final String REQUEST_TYPE = "pvkmrt";
    public static final String BARCODE = "pvkmb";
    public static final String RETAILER_ID = "pvkmri";
    public static final String NAME = "pvkmn";
    public static final String IMAGE = "pvkmig";
    public static final String BRAND_ID = "pvkmbi";
    public static final String DESCRIPTION = "pvkmdn";
    public static final String DISCOUNT = "pvkmdt";
    public static final String PRICE = "pvkmpe";
    public static final String CAT_ID = "pvkmcd";
    public static final String SOURCE_FILE = "pvkmse";
    public static final String STYLE_ID = "pvkmsd";

    private MerchandiseBean merchandiseBean;

    public MerchandiseNetIO(final MerchandiseBean bean) {
        this.merchandiseBean = bean;
    }


    public void select() {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(REQUEST_TYPE, new StringBody(String.valueOf(SELECT_MERCHANDISE_INFO)));
            reqEntity.addPart(BARCODE, new StringBody(merchandiseBean.getCode()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            final JSONObject responseObject = getResponseJSon(response);
            if (responseObject != null && !responseObject.isEmpty()) {
                merchandiseBean.setName(responseObject.getString("MERCHANDISE_NAME"));
                final MerchandiseBrandBean brandBean = new MerchandiseBrandBean(responseObject.getString("BRAND_ID"));
                final ProviderBean provider = new ProviderBean();
                provider.setUserID(responseObject.getString("PROVIDER_ID"));
                merchandiseBean.setBrand(brandBean);
                merchandiseBean.setProvider(provider);
                merchandiseBean.setUnitPrice(responseObject.getDouble("PRICE"));
                merchandiseBean.setAmount(responseObject.getDouble("DISCOUNT"));
                merchandiseBean.setIconPath(responseObject.getString("MERCHANDISE_S_ICON_PATH"));
                merchandiseBean.setDiscount(responseObject.getDouble("DISCOUNT"));
                final String sourcePath = responseObject.getString("MERCHANDISE_FILE_PATH");
                final String extensionName = sourcePath.substring(sourcePath.lastIndexOf("."));
                if (extensionName.endsWith(LocalStorage.FURNITURE_EXT)) {
                    merchandiseBean.setType(MerchandiseBean.MerchandiseType.furniture);
                } else if (extensionName.endsWith(LocalStorage.SURFACE_MATERIAL_EXT)) {
                    merchandiseBean.setType(MerchandiseBean.MerchandiseType.surfaceMaterial);
                }
            }

        } catch (final UnsupportedEncodingException e) {

        } catch (IOException e) {
        }
    }

    public void list(PageBean pages) {
    }

    public void getPages(PageBean pageBean) {
    }

    public boolean upload() {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(REQUEST_TYPE, new StringBody(String.valueOf(UPLOAD_MERCHANDISE_INFO)));
            reqEntity.addPart(NAME, new StringBody(merchandiseBean.getName(), Charset.forName(HTTP.UTF_8)));
            reqEntity.addPart(STYLE_ID, new StringBody(merchandiseBean.getStyleId()));
            reqEntity.addPart(BRAND_ID, new StringBody(merchandiseBean.getBrand().getCode()));
            reqEntity.addPart(DESCRIPTION, new StringBody(merchandiseBean.getDescription(),Charset.forName(HTTP.UTF_8)));
            reqEntity.addPart(IMAGE, new StringBody(merchandiseBean.getIconPath()));
            reqEntity.addPart(CAT_ID, new StringBody(merchandiseBean.getCategoryId()));
            reqEntity.addPart(SOURCE_FILE, new StringBody(merchandiseBean.getSourceFilePath()));
            reqEntity.addPart(PRICE, new StringBody(String.valueOf(merchandiseBean.getUnitPrice())));
            reqEntity.addPart(DISCOUNT, new StringBody(String.valueOf(merchandiseBean.getDiscount())));
            reqEntity.addPart(RETAILER_ID, new StringBody(String.valueOf(merchandiseBean.getProvider().getCode())));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            final JSONObject responseObject = getResponseJSon(response);
            if(responseObject!=null){
                System.out.println(responseObject);
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void delete() {

    }

    public void update() {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(REQUEST_TYPE, new StringBody(String.valueOf(UPDATE_MERCHANDISE_HOT)));
            reqEntity.addPart(BARCODE, new StringBody(merchandiseBean.getCode()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
        } catch (final UnsupportedEncodingException e) {

        } catch (IOException e) {
        }


    }

    public static void main(String[] args) {
        MerchandiseBean merchandiseBean = new MerchandiseBean("a310171f-0c7c-486e-9da9-c55c84275c47");
        NetOperation operation = new MerchandiseNetIO(merchandiseBean);
        operation.select();


    }

}
