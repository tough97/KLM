package cs.designer.swing.list;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImagePanel extends JPanel {

    public static final int DEFAULT_WIDTH = 100;
    public static final int DEFAULT_HEIGHT = 100;
    private BufferedImage image = null;
    private Point pBegin = new Point();
    private Point pEnd = new Point();

    public ImagePanel() {
        super();
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public ImagePanel(BufferedImage bufferedImage) {
        super();
        image = bufferedImage;
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        countImagePoint(image);

    }

    public void setImage(BufferedImage bufferedImage) {
        image = bufferedImage;
        countImagePoint(image);
        repaint();
    }

    public void setImage(URL url) {
        try {
            image = ImageIO.read(url);
            setImage(image);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void setImage(InputStream inputImage) {
        try {
            image = ImageIO.read(inputImage);
            setImage(image);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public BufferedImage getImage() {
        return this.image;
    }

//
    private void countImagePoint(BufferedImage image) {
        float imageWidthHeight = (float) image.getWidth()
                / (float) image.getHeight();
        float panelWidthHeight = (float) getWidth() / (float) getHeight();
        Dimension panleSize = getSize();
        Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());

        if (imageWidthHeight > panelWidthHeight) {
            pBegin.x = 0;
            pEnd.x = panleSize.width;
            float height = (float) imageSize.height / (float) imageSize.width * panleSize.width;
            pBegin.y = (int) ((panleSize.height - height) / 2);
            pEnd.y = pBegin.y + (int) height;
        } else {
            pBegin.y = 0;
            pEnd.y = panleSize.height;
            float width = (float) panleSize.height * ((float) imageSize.width / (float) imageSize.height);
            pBegin.x = (panleSize.width - (int) width) / 2;
            pEnd.x = pBegin.x + (int) width;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (image != null) {
            countImagePoint(image);
            g2.drawImage(image, pBegin.x, pBegin.y, pEnd.x, pEnd.y,
                    0, 0, image.getWidth(), image.getHeight(), this);
//            g2.drawImage(image, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, this);
        }
    }
}