package com.klm.persist.meta;

import com.klm.persist.CSPersistException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 12/1/11
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class BufferedImageMeta implements Serializable {

    public static final String DEFAULT_FORMAT = "jpg";
    public static final int MAX_IMAGE_SIZE = 2 * 1024 * 1024;

    private byte[] bufferedImageBytes;
    private static final long serialVersionUID = 100;
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("bufferedImageBytes", byte[].class)
    };

    public BufferedImageMeta(final BufferedImage image) throws CSPersistException {
        bufferedImageBytes = toBytes(image);
    }

    public BufferedImageMeta(final byte[] bufferredImageBytes) throws CSPersistException {
        this.bufferedImageBytes = bufferredImageBytes;
    }


    public byte[] getBufferedImageBytes() {
        return bufferedImageBytes;
    }

    public BufferedImage getImage() {
        try {
            return fromBytes(bufferedImageBytes);
        } catch (CSPersistException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getSize() {
        return bufferedImageBytes == null ? -1 : bufferedImageBytes.length;
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.writeObject(bufferedImageBytes);
        oos.flush();
    }

    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
      bufferedImageBytes= (byte[]) ois.readObject();
    }

    private static BufferedImage fromBytes(final byte[] imageByte) throws CSPersistException {
        if (imageByte.length > MAX_IMAGE_SIZE) {
            throw new CSPersistException("Can not process image size greater than 3M");
        }
        try {
            return ImageIO.read(new ByteArrayInputStream(imageByte));
        } catch (Exception ex) {
            throw new CSPersistException(ex);
        }
    }

    private static byte[] toBytes(final BufferedImage image) throws CSPersistException {
        final ByteArrayOutputStream bao = new ByteArrayOutputStream(MAX_IMAGE_SIZE);
        try {
            ImageIO.write(image, DEFAULT_FORMAT, bao);
            return bao.toByteArray();
        } catch (Exception ex) {
            throw new CSPersistException(ex);
        }
    }

}
