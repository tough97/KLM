package cs.designer.io.net;

import cs.designer.swing.bean.HouseBean;
import cs.designer.swing.bean.PageBean;
import cs.designer.swing.bean.UserHouseBean;
import cs.designer.utils.FileUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/29/12
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserHouseNetIO extends ClientNetIO implements NetOperation {
    public static final String SERVER_URL_PATTERNS = "/house-service";

    public enum OperationType {
        upload, list_user_h, delete, update, house_info
    }

    public enum Properties {
        user_id, house_id, house_file, house_view_icon,
        compound_code, house_description, house_layout_code

    }

    private UserHouseBean userHouse;

    public UserHouseNetIO(final UserHouseBean userHouse) {
        super();
        this.userHouse = userHouse;
    }

    public UserHouseNetIO(final String userId, final String houseId) {
        userHouse = new UserHouseBean(userId);
        userHouse.addHouse(new HouseBean(houseId));

    }

    public void run() {
        upload();
    }

    public boolean upload() {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        boolean success = false;
        try {
            HouseBean house = userHouse.getUserHouses().get(0);
            reqEntity.addPart(OPERATION_TYPE,
                    new StringBody(OperationType.upload.toString()));
            reqEntity.addPart(Properties.user_id.toString(),
                    new StringBody(house.getOwnerUserCode()));
            if (house.getCode() != null && house.getCode().length() != 0) {
                reqEntity.addPart(Properties.house_id.toString(),
                        new StringBody(house.getCode(), Charset.forName(HTTP.UTF_8)));
            }
            reqEntity.addPart(Properties.house_description.toString(),
                    new StringBody(house.getDescription(), Charset.forName(HTTP.UTF_8)));
            reqEntity.addPart(Properties.house_layout_code.toString(),
                    new StringBody("house_layout_code ", Charset.forName(HTTP.UTF_8)));
            reqEntity.addPart(Properties.compound_code.toString(),
                    new StringBody(house.getCompoundCode()));
            reqEntity.addPart(Properties.house_file.toString(),
                    new ByteArrayBody(house.getHouseSource(), ""));
            reqEntity.addPart(Properties.house_view_icon.toString(),
                    new ByteArrayBody(FileUtil.imageToBytes(house.getSneapView()), ""));
            post.setEntity(reqEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final HttpResponse response = requestToServer(post);
        setMessageInfo(response);
        Object responseObject = message.getResponseObject();
        if (responseObject != null) {
            if (userHouse.getUserHouses().get(0).getCode() == null) {
                userHouse.getUserHouses().get(0).setCode(responseObject.toString());
            }
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
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.delete.toString()));
            reqEntity.addPart(Properties.user_id.toString(), new StringBody(this.userHouse.getCode()));
            reqEntity.addPart(Properties.house_id.toString(), new StringBody(userHouse.getUserHouses().get(0).getCode()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {


    }

    public void select() {

        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.house_info.toString()));
            reqEntity.addPart(Properties.house_id.toString(), new StringBody(userHouse.getUserHouses().get(0).getCode()));
            reqEntity.addPart(Properties.user_id.toString(), new StringBody(userHouse.getCode()));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = message.getResponseObject();
            System.out.println(responseObject);
            if (responseObject != null) {
                final Map<String, byte[]> houseInfo = (Map<String, byte[]>) responseObject;
                System.out.println(houseInfo);

            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void getPages(final PageBean pages) {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.list_user_h.toString()));
            reqEntity.addPart(Properties.user_id.toString(), new StringBody(this.userHouse.getCode()));
            reqEntity.addPart(QUERY_RS_COUNT, new StringBody("true"));
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = message.getResponseObject();
            if (responseObject != null) {
                pages.setDataSize(Integer.valueOf(responseObject.toString()));
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void list(final PageBean pages) {
        final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
        final MultipartEntity reqEntity = new MultipartEntity();
        try {
            reqEntity.addPart(OPERATION_TYPE, new StringBody(OperationType.list_user_h.toString()));
            reqEntity.addPart(Properties.user_id.toString(), new StringBody(this.userHouse.getCode()));
            if (pages != null) {
                reqEntity.addPart(PAGE_NUMBER, new StringBody(String.valueOf(pages.getCurrentPage())));
                reqEntity.addPart(RECORDS_PER_PAGE, new StringBody(String.valueOf(pages.getPageSize())));
            }
            post.setEntity(reqEntity);
            final HttpResponse response = requestToServer(post);
            setMessageInfo(response);
            Object responseObject = message.getResponseObject();
            if (responseObject != null) {
                final List<Map<String, byte[]>> userHouses = (List<Map<String, byte[]>>) responseObject;
                for (final Map<String, byte[]> userHouse : userHouses) {
                    final String houseId = new String(userHouse.get("HOUSE_CODE"));
                    final BufferedImage houseIconView = ImageIO.read(new ByteArrayInputStream(userHouse.get("HOUSE_ICON_VIEW")));
                    final HouseBean house = new HouseBean(houseId);
                    house.setOwnerUserCode(this.userHouse.getCode());
                    house.setSneapView(houseIconView);
                    this.userHouse.addHouse(house);
                }
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public UserHouseBean getUserHouse() {
        return userHouse;
    }

    public static void main(String[] args) {
        UserHouseNetIO userHouseNetIO = new UserHouseNetIO("1", "1a3598f8-3223-41ec-ba10-a083bb93bff1");
        userHouseNetIO.select();


    }
}