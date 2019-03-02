package com.klm.util.net;

import com.klm.cons.impl.House;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 1/16/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class HouseNetIO implements Runnable {

    public static final String POSET_HOUSE_ACTION = "/add_house";
    public static final String POSET_MERCHANDISE_ACTION = "/add-merchandise";
    public static final int BUFFER_SIZE = 1024 * 20;

    private static final Logger logger = Logger.getLogger(HouseNetIO.class);

    private House house;
    private String userID;
    private int code;
    private JProgressBar bar;
    private String action;

    public HouseNetIO(final House house, final String userID,
                      final JProgressBar bar, final String action) {
        this.house = house;
        this.userID = userID;
        this.bar = bar;
        this.action = action;
    }


//    @Override
    public void run() {
//        HttpURLConnection connection = null;
        try {
            final File tmpFile = File.createTempFile(
                    String.valueOf(System.currentTimeMillis()),
                    house.getHouseID() == null ? "" : house.getHouseID(),
                    new File(System.getProperty("java.io.tmpdir")));
            final HttpClient client = new DefaultHttpClient();
            final HttpPost post = new HttpPost(ServerUtil.SERVER_HOST+action);
            final MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("ui", new StringBody(userID));
            reqEntity.addPart("fb", new FileBody(tmpFile));
            post.setEntity(reqEntity);
            System.out.println("post.getRequestLine() = "+post.getRequestLine());
            System.out.println("post.toString() = "+post.toString());
            client.execute(post);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.ERROR, ex);
        }
    }

    private byte[] getBytes(final Serializable obj) throws IOException {
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        final ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        return byteOut.toByteArray();
    }

}
