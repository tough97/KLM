package cs.designer.swing.ui;

import com.klm.util.RealNumberOperator;
import cs.designer.utils.PointWithAngleMagnetism;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/14/12
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImportPlanImageDialog extends JDialog {
    //    public final static Color BACK_COLOR=new Color(241,241,241);
    public final static int DEFAULT_UINT = 5; //1m=5ps;
    private BufferedImage planImage;
    private BufferedImage tempImage;
    private ImagePanel imagePanel;
    private HousePlanView view;
    private int uint = DEFAULT_UINT;
    private JButton importBtn;
    private JTextField lengthField;
    private float length;

    public ImportPlanImageDialog(HousePlanView view, final String title,
                                 final File planImageFile) {
        super(SwingUtilities.getWindowAncestor(view), title);
        this.view = view;
        setSize(790, 490);
        setModal(true);
        int locationX = getParent().getLocation().x + (getParent().getWidth()
                - getWidth()) / 2;
        int locationY = getParent().getLocation().y + (getParent().getHeight()
                - getHeight()) / 2;
        setLocation(locationX, locationY);
        init(planImageFile);

    }

    private void init(final File planImageFile) {
        imagePanel = new ImagePanel();
        try {
            planImage = ImageIO.read(planImageFile);
            imagePanel.setImage();
            JPanel controlPanel = new JPanel();
            controlPanel.setPreferredSize(new Dimension(790, 490));
            controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER,15,10));
            importBtn = new JButton("确定");
            lengthField = new JTextField(10);
            final JPanel descPanel = new JPanel();
            descPanel.setLayout(new BorderLayout());
            final JLabel descLabel = new JLabel("<html>说明:在户型图中截取一段长度，并输入相应实际长度(单位：毫米)</html>");
            descPanel.add(BorderLayout.CENTER, descLabel);
            descLabel.setPreferredSize(new Dimension(400,20));
            controlPanel.add(descLabel);
            controlPanel.add(new JLabel("比例尺"));
            controlPanel.add(lengthField);
            controlPanel.add(importBtn);
            importBtn.addActionListener(imagePanel);
            final JPanel imageBackPanel = new JPanel();
            imageBackPanel.setPreferredSize(new Dimension(700, 400));
            imageBackPanel.setLayout(new BorderLayout());
            imageBackPanel.add(BorderLayout.CENTER, imagePanel);
            getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
            getContentPane().add(imageBackPanel);
            getContentPane().add(controlPanel);
//            getContentPane().setBackground(BACK_COLOR);
//            controlPanel.setBackground(BACK_COLOR);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void setLengthValues(float length) {
        this.length = length;
        this.lengthField.setText(String.
                valueOf(RealNumberOperator.roundNumber(length, 1)));

    }

    private void hideImportPlanImageDialog() {
        setVisible(false);
    }

    class ImagePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
        private Line2D line;
        private Point2D lineStartPoint;
        float sx;
        float sy;

        public ImagePanel() {
            super();
            setSize(700, 400);
            line = new Line2D.Float();
            addMouseListener(this);
            addMouseMotionListener(this);
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }

        public void setImage() {
            if (planImage != null) {
                tempImage = reSizeImage(planImage, getWidth(), getHeight());
                grayImage(planImage);
            }
        }

        private void grayImage(BufferedImage image) {
            ColorConvertOp cco = new ColorConvertOp(ColorSpace
                    .getInstance(ColorSpace.CS_GRAY), null);
            cco.filter(image, image);
        }

        public BufferedImage reSizeImage(BufferedImage srcBufImage, int width, int height) {
            BufferedImage bufTarget = null;
            sx = (float) width / srcBufImage.getWidth();
            sy = (float) height / srcBufImage.getHeight();
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

        public void paint(Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            if (planImage != null) {
                g2d.drawImage(tempImage, 0, 0, this);
            }
            if (line != null) {
                BasicStroke basicStroke = new BasicStroke(8f);
                g2d.setStroke(basicStroke);
                g2d.draw(line);
            }
        }

        public void mouseClicked(MouseEvent me) {

        }

        public void mousePressed(MouseEvent me) {
            if (me.getButton() == MouseEvent.BUTTON1) {
                if (lineStartPoint == null) {
                    lineStartPoint = new Point2D.Float(me.getX(), me.getY());
                } else {
                    PointWithAngleMagnetism point = new PointWithAngleMagnetism((float) lineStartPoint.getX(),
                            (float) lineStartPoint.getY(),
                            me.getX(), me.getY(), (float) Point2D.distance(me.getX(), me.getY(), lineStartPoint.getX(),
                                    lineStartPoint.getY()));
                    line = new Line2D.Float(lineStartPoint, new Point2D.Float(point.getX(), point.getY()));
                    lineStartPoint = null;
                }

            } else {
                lineStartPoint = null;
                line = null;
            }
            repaint();
        }

        public void mouseReleased(MouseEvent me) {

        }

        public void mouseEntered(MouseEvent me) {

        }

        public void mouseExited(MouseEvent me) {

        }

        public void mouseDragged(MouseEvent me) {

        }

        public void mouseMoved(MouseEvent me) {
            if (lineStartPoint != null) {
                PointWithAngleMagnetism point = new PointWithAngleMagnetism((float) lineStartPoint.getX(),
                        (float) lineStartPoint.getY(),
                        me.getX(), me.getY(), (float) Point2D.distance(me.getX(), me.getY(), lineStartPoint.getX(),
                                lineStartPoint.getY()));
                line = new Line2D.Float(lineStartPoint, new Point2D.Float(point.getX(), point.getY()));
                setLengthValues((float) Point2D.distance(lineStartPoint.getX(),
                        lineStartPoint.getY(), point.getX(), point.getY()));
            }
            repaint();
        }

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == importBtn) {
                if (planImage != null) {
                    float actualLength = Float.valueOf(lengthField.getText()) / 1000;
                    float uint = actualLength / length;
                    view.getPlanControler().getBaseFace().addPlanview((double) (planImage.getWidth()) * uint * sx,
                            (double) (planImage.getHeight()) * uint * sy, planImage);
                    if (view.getViewType() != DisplayView.ViewType.DRAW_PLANVIEW) {
                        view.getPlanViewControler().drawPlanView();

                    }
                    hideImportPlanImageDialog();

                }
            }
        }
    }
}
