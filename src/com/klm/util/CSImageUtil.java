/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author gang-liu
 */
public class CSImageUtil {

    public static final int WIDTH = 128;
    public static final int HEIGHT = 128;
    public static final double LIGHT_WEIGHT = 0.95;

    public static BufferedImage loadBufferedImage(final URL fileURL) throws
            CSUtilException {
        try {
            return ImageIO.read(fileURL);
        } catch (Exception ex) {
            throw new CSUtilException(ex);
        }
    }

    public static void saveBufferedImage(final String fName,
            final BufferedImage bi) throws CSUtilException {
        try {
            final File file = new File(fName);
            final String extension = fName.substring(fName.lastIndexOf(".") + 1);
            ImageIO.write(bi, extension, file);
        } catch (Exception ex) {
            throw new CSUtilException(ex);
        }
    }

    public static BufferedImage createBufferedImage(final int width,
            final int height, final Color color, final int trans) {
        final BufferedImage ret = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        final WritableRaster raster = ret.getRaster();
        for (int x = 0; x < raster.getWidth(); x++) {
            for (int y = 0; y < raster.getHeight(); y++) {
                raster.setPixel(x, y, new int[]{color.getRed(), color.getGreen(),
                            color.getBlue(), trans});
            }
        }
        return ret;
    }

    public static void transparentImage(final String fName, final int trans) throws
            IOException {
        final String ext = "png";
        final String newFileName = fName.substring(0, fName.lastIndexOf(".")) +
                "_TR." + ext;
        final BufferedImage origin = ImageIO.read(new File(fName));
        final BufferedImage newImage = new BufferedImage(origin.getWidth(),
                origin.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final WritableRaster originRaster = origin.getRaster();
        final WritableRaster newImageRaster = newImage.getRaster();
        for (int x = 0; x < originRaster.getWidth(); x++) {
            for (int y = 0; y < newImageRaster.getHeight(); y++) {
                final int[] pix = originRaster.getPixel(x, y, new int[3]);
                newImageRaster.setPixel(x, y, new int[]{pix[0], pix[1], pix[2],
                            trans});
            }
        }
        ImageIO.write(newImage, ext, new File(newFileName));
    }

    public static String blackWhiteImage(final String fName) throws IOException {
        final String fExt = fName.substring(fName.lastIndexOf(".") + 1);
        final String newFileName = fName.substring(0, fName.lastIndexOf(".")) +
                "_BW." + fExt;
        final BufferedImage origin = ImageIO.read(new File(fName));
        final WritableRaster raster = origin.getRaster();
        for (int x = 0; x < raster.getWidth(); x++) {
            for (int y = 0; y < raster.getHeight(); y++) {
                final int[] pix = new int[3];
                raster.getPixel(x, y, pix);
                int avg = getAverage(pix);
                pix[0] = (int) (avg / .5);
                pix[1] = (int) (avg / .5);
                pix[2] = (int) (avg / .5);
                raster.setPixel(x, y, pix);
            }
        }
        ImageIO.write(origin, fExt, new File(newFileName));
        return newFileName;
    }

    public static String exploreImage(final String fName) throws IOException {
        final String fExt = fName.substring(fName.lastIndexOf(".") + 1);
        final String newFileName = fName.substring(0, fName.lastIndexOf(".")) +
                "_EX." + fExt;
        final BufferedImage origin = ImageIO.read(new File(fName));
        final WritableRaster raster = origin.getRaster();
        for (int x = 0; x < raster.getWidth(); x++) {
            for (int y = 0; y < raster.getHeight(); y++) {
                final int[] pix = new int[3];
                raster.getPixel(x, y, pix);
                for (int i = 0; i < pix.length; i++) {
                    pix[i] = 255 - pix[i];
                }
                raster.setPixel(x, y, pix);
            }
        }
        ImageIO.write(origin, fExt, new File(newFileName));
        return newFileName;
    }

    public static int getAverage(final int[] values) {
        int sum = 0;
        for (int index = 0; index < values.length; index++) {
            sum += values[index];
        }
        return ((int) sum / values.length);
    }

    public static BufferedImage createTestingBufferedImage() {
        final BufferedImage bi = new BufferedImage(WIDTH, HEIGHT,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = (Graphics2D) bi.getGraphics();
        for (int i = 0; i < 4; i++) {
            int startHeight = (i / 4) * HEIGHT;
            Color color = null;
            RescaleOp rescaleOP = null;
            final float[] scale = {1f, 1f, 1f, 1f};
            final float[] offset = new float[4];
            switch (i) {
                case 0:
                    color = Color.white;
                    rescaleOP = new RescaleOp(scale, offset, null);
                    break;
                case 1:
                    color = Color.white;
                    offset[3] = 0.5f;
                    rescaleOP = new RescaleOp(scale, offset, null);
                    break;
                case 2:
                    color = Color.black;
                    offset[3] = 0.5f;
                    rescaleOP = new RescaleOp(scale, offset, null);
                    break;
                case 3:
                    color = Color.black;
                    rescaleOP = new RescaleOp(scale, offset, null);
                    break;
                default:
                    break;
            }
            g2d.drawImage(bi, rescaleOP, 0, startHeight);
        }

        return bi;
    }

    public static String blendImage(final String fName, Color blendColor) throws
            IOException {
        final BufferedImage input = ImageIO.read(new File(fName));
        final WritableRaster reader = input.getRaster();
        final BufferedImage output = new BufferedImage(reader.getWidth(), reader.
                getHeight(), input.getType());
        final String fExt = fName.substring(fName.lastIndexOf(".") + 1);
        final String newFileName = fName.substring(0, fName.lastIndexOf(".")) +
                "_BLD." + fExt;        
        final WritableRaster writer = output.getRaster();
        for (int x = 0; x < reader.getWidth(); x++) {
            for (int y = 0; y < reader.getHeight(); y++) {
                final int[] litValues = new int[3];
                reader.getPixel(x, y, litValues);
                int litAvg = getAverage(litValues);
                final double adjustment = ((litAvg * 1.0 - 122)/122)*255;
//                System.out.println(adjustment);
                final int[] mixedRGB = new int[]{
                    blendColor.getRed() + (int)(adjustment),
                    blendColor.getGreen() + (int)(adjustment),
                    blendColor.getBlue() + (int)(adjustment)
                };
                
                for(int i = 0; i < mixedRGB.length; i++){
                    mixedRGB[i] = mixedRGB[i] > 255 ? 255 : mixedRGB[i];
                    mixedRGB[i] = mixedRGB[i] < 0 ? 0 : mixedRGB[i];
                }
//                System.out.println(litAvg+" + ("+blendColor.getRed()+" "+blendColor.getGreen()+" "+blendColor.getBlue()+") = ("+mixedRGB[0]+" "+mixedRGB[1]+" "+mixedRGB[2]+")");
                writer.setPixel(x, y, mixedRGB);
            }
        }
        ImageIO.write(output, fExt, new File(newFileName));
        return newFileName;
    }
    
    
    private static int blendValue(int RGB, final int lightAvg){        
        final int value =  (lightAvg - 122) + RGB;
        return value > 255 ? 255 : value;
//        return (int) (lightAvg * LIGHT_WEIGHT + RGB > 255 ? 255 : lightAvg * LIGHT_WEIGHT + RGB);
    }
    
    public static void main(String[] args) throws IOException, CSUtilException {
//        final String fName = "/home/gang-liu/Desktop/Material/gaoguangtu2.jpg";
        final String fName = "/home/gang-liu/Desktop/images.jpg";
//        System.out.println(blendImage(fName, Color.RED));
//        transparentImage(fName, 50);        
        System.out.println(blendImage(args[0], Color.RED));
    }
}