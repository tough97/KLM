package cs.designer.io.net;

import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/10/12
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class IPAddressNetIO extends ClientNetIO {
    public static final String SINA_SERVER_ADDRESS = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip";

    public IPAddressNetIO() {

    }

    public JSONObject getIPAddress() {
        JSONObject ipAddressInfo = null;
        final HttpPost post = new HttpPost(SINA_SERVER_ADDRESS);
        final HttpResponse response = requestToServer(post);
        if (response != null) {
            final HttpEntity entity = response.getEntity();
            try {
                if (entity != null) {
                    InputStream in  = entity.getContent();
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
                        final String ipInfo = result.toString();
                        ipAddressInfo = JSONObject.
                                fromObject(new String(ipInfo.substring(ipInfo.lastIndexOf("=") + 2,
                                        ipInfo.length() - 1)));
//                            System.out.println(ipInfo);
                    }
                }
            } catch (IOException e) {

            }
        }
        return ipAddressInfo;
    }

    public static void main(String[] args) throws IOException {
        IPAddressNetIO ipAddressNetIO = new IPAddressNetIO();
        ipAddressNetIO.getIPAddress();
    }
}
