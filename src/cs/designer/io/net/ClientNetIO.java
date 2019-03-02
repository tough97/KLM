package cs.designer.io.net;

import cs.designer.swing.bean.MessageBean;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/25/12
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ClientNetIO extends Thread {
    private static boolean linkable = false;
    public static final String SERVER_HOST = "http://www.kaolamao.com";
    public static final String OPERATION_TYPE = "operation_type";
    public static final String RECORDS_PER_PAGE = "rec_per_page";
    public static final String PAGE_NUMBER = "page_num";

    public enum StatusCode {
        notFind, netError, normal, versionError, systemError
    }

    protected StatusCode statusCode = StatusCode.normal;
    static final int SC_ACCEPT = 202;
    protected MessageBean message;
    protected boolean debug = false;

    public ClientNetIO() {
        message = new MessageBean();
    }

    protected HttpResponse requestToServer(final HttpEntityEnclosingRequestBase request) {
        final HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(HTTP.DEFAULT_CONTENT_CHARSET, HTTP.UTF_8);
        try {
            if (debug) {
                System.out.println("execute: " + request.getRequestLine());
            }
            final HttpResponse response = client.execute(request);
            return response;
        } catch (IOException e) {

                statusCode = StatusCode.netError;

        } catch (Exception e) {
            statusCode = StatusCode.netError;
        }

        return null;
    }

    public MessageBean getMessage() {
        return message;
    }

    protected float toFloat(final byte[] data) {
        return Float.valueOf(new String(data));
    }

    protected int toInt(final byte[] data) {
        return Integer.valueOf(new String(data));
    }


    protected void setMessageInfo(final HttpResponse response) {
        try {
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == SC_ACCEPT) {
                    final HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        InputStream in = entity.getContent();
                        if (in != null) {
                            final ObjectInputStream ois =
                                    new ObjectInputStream(in);
                            final Object responseObject = ois.readObject();
                            if (debug) {
                                System.out.println(responseObject);
                            }
                            message.setResponseObject(responseObject);
                            ois.close();
                            in.close();
                        }
                    }

                } else {
                    statusCode = StatusCode.systemError;
                }
            }
        } catch (ClassNotFoundException e) {
            statusCode = StatusCode.versionError;
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            statusCode = StatusCode.versionError;
            e.printStackTrace();

        } catch (IOException e) {
            statusCode = StatusCode.netError;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = StatusCode.systemError;
        }
    }

    protected JSONObject getResponseJSon(final HttpResponse response) {
        JSONObject responseObject = null;
        try {
            if (response != null) {
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream in = entity.getContent();
                    if (in != null) {
                        final StringBuffer result = new StringBuffer();
                        final BufferedReader rd = new BufferedReader(new InputStreamReader(in, HTTP.UTF_8));
                        String tempLine = rd.readLine();
                        while (tempLine != null) {
                            result.append(tempLine);
                            tempLine = rd.readLine();
                        }
                        in.close();
                        rd.close();
                        responseObject = JSONObject.fromObject(result.toString());
                    }
                }
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    protected void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static boolean getLinkableStatus() {
        return linkable;
    }

    public static boolean testNetStatus() {
        URL url = null;
        try {
            url = new URL(SERVER_HOST);
            try {
                InputStream in = url.openStream();
                in.close();
                linkable = true;
            } catch (IOException e) {
                linkable = false;
            }
        } catch (MalformedURLException e) {
            linkable = false;
        }
        return linkable;
    }
}
