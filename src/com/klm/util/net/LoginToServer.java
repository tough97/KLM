package com.klm.util.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 1/16/12
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginToServer {

    static final int SC_ACCEPT = 202;
    static final int SC_NOT_ACCEPTED = 406;
    static final String SEPARATOR = "#";

    private static String USER_ID;
    private static String USER_EMAIL;

    public static String getUSER_ID() {
        return USER_ID;
    }

    public static void setUSER_ID(String USER_ID) {
        LoginToServer.USER_ID = USER_ID;
    }

    public static String getUSER_EMAIL() {
        return USER_EMAIL;
    }

    public static void setUSER_EMAIL(String USER_EMAIL) {
        LoginToServer.USER_EMAIL = USER_EMAIL;
    }

    public static boolean loginToServer(final String userEmail, final String userPassword) throws IOException {
        final HttpClient client = new DefaultHttpClient();

        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("USER_EMAIL", userEmail));
        params.add(new BasicNameValuePair("USER_PASSWORD", userPassword));
        final HttpPost post = new HttpPost(ServerUtil.SERVER_HOST + "/login");
        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        final byte[] buff = new byte[1024 * 20];
        final HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() == SC_ACCEPT) {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                final InputStream in = entity.getContent();
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                while (in.read(buff) > 0) {
                    out.write(buff);
                    out.flush();
                }
                in.close();
                out.close();
                final String tS = new String(out.toByteArray());
                final String[] ret = new String(tS.getBytes("ISO-8859-1"), "UTF8").split(SEPARATOR);
                System.out.println(" ID  = "+ret[0]);
                System.out.println("Name = "+ret[1]);
                return true;
            }
            return false;
        } else {
            System.out.println("登录失败");
            return false;
        }
    }

    public static void main(String[] arsg) throws IOException {
        System.out.println(loginToServer("nan@kaolamao.com", "1212"));
    }

}
