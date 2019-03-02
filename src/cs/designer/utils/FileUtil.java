package cs.designer.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/29/12
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {

    public static BufferedImage reSizeImage(BufferedImage srcBufImage, int width, int height) {
        BufferedImage bufTarget = null;
        float sx = (float) width / srcBufImage.getWidth();
        float sy = (float) height / srcBufImage.getHeight();
        int type = srcBufImage.getType();
        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = srcBufImage.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(width,
                    height);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            bufTarget = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else
            bufTarget = new BufferedImage(width, height, type);

        Graphics2D g = bufTarget.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(srcBufImage, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return bufTarget;
    }

    public static byte[] imageToBytes(final BufferedImage image) {
        final ByteArrayOutputStream bao = new ByteArrayOutputStream(1024 * 64);
        try {
            ImageIO.write(image, "jpg", bao);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bao.toByteArray();

    }

    public static byte[] fileTobytes(final File file) throws IOException {
        byte[] buffer = new byte[1024 * 1024 * 20];
        final ByteArrayOutputStream bao = new ByteArrayOutputStream(buffer.length);
        final InputStream reader = new FileInputStream(file);
        int byteRead;
        while ((byteRead = reader.read(buffer)) > 0) {
            bao.write(buffer, 0, byteRead);
            bao.flush();
        }
        bao.close();
        reader.close();
        return bao.toByteArray();
    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            filename.trim();
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return "." + filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public static String toMD5(final String info) {
        MessageDigest encryptor = null;
        String ret = null;
        try {
            encryptor = MessageDigest.getInstance("MD5");
            encryptor.update(info.getBytes(), 0, info.length());
            ret = new BigInteger(1, encryptor.digest()).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ret;

    }
    public static byte[] objectToBytes(final Serializable objSerializable) {
        final ByteArrayOutputStream bao = new ByteArrayOutputStream(1024 * 1024 * 20);
        try {
            final ObjectOutputStream oos =
                    new ObjectOutputStream(bao);
            oos.writeObject(objSerializable);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bao.toByteArray();

    }


}
