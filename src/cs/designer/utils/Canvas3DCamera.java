package cs.designer.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/27/12
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */

public class Canvas3DCamera extends Canvas3D {
    public static final int PHOTO_WIDTH = 200;
    public static final int PHOTO_HEIGHT = 200;

    public Canvas3DCamera(GraphicsConfiguration graphicsConfiguration) {
        super(graphicsConfiguration, true);
    }


    public BufferedImage photograph() {
        return photograph(PHOTO_WIDTH,PHOTO_HEIGHT);

    }

    public BufferedImage photograph(int width, int height) {
        getScreen3D()
                .setSize(width, height);
        getScreen3D().setPhysicalScreenHeight(
                0.0254 / 90 * height);

        getScreen3D().setPhysicalScreenWidth(
                0.0254 / 90 * width);

        RenderedImage renderedImage = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_ARGB);
        ImageComponent2D imageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGB8,
                renderedImage);
        imageComponent.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
        setOffScreenBuffer(imageComponent);
        renderOffScreenBuffer();
        waitForOffScreenRendering();

        return imageComponent.getImage();

    }

    @Override
    public void postSwap() {
    }
}