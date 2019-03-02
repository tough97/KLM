package cs.designer.swing.ui;

import cs.designer.io.local.FileExtensionUtill;
import cs.designer.io.local.LocalFileManage;
import cs.designer.swing.list.ImagePanel;
import cs.designer.utils.FileUtil;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 5/8/12
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class PhotoDialog extends JDialog implements ActionListener {
    private ImagePanel imagePanel;
    private HousePlanView view;
    private JButton photoBtn;
    private JButton savePohotBtn;
    private BufferedImage photoImage;

    public PhotoDialog(final HousePlanView view,
                       final String title) {
        super(SwingUtilities.getWindowAncestor(view), title);
        this.view = view;
        setSize(790, 490);
        setResizable(false);
        int locationX = getParent().getLocation().x + (getParent().getWidth()
                - getWidth()) / 2;
        int locationY = getParent().getLocation().y + (getParent().getHeight()
                - getHeight()) / 2;
        setLocation(locationX, locationY);
        init();

    }

    private void init() {
        imagePanel = new ImagePanel();
        JPanel controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(200, 490));
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        photoBtn = new JButton("拍照");
        savePohotBtn = new JButton("保存");
        photoBtn.addActionListener(this);
        savePohotBtn.addActionListener(this);
        final JPanel descPanel = new JPanel();
        descPanel.setLayout(new BorderLayout());
        controlPanel.add(photoBtn);
        controlPanel.add(savePohotBtn);
        final JPanel imageBackPanel = new JPanel();
        imageBackPanel.setPreferredSize(new Dimension(700, 400));
        imageBackPanel.setLayout(new BorderLayout());
        imageBackPanel.add(BorderLayout.CENTER, imagePanel);
        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        getContentPane().add(imageBackPanel);
        getContentPane().add(controlPanel);
        setPhotoImage();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == photoBtn) {
            setPhotoImage();
        }else if(e.getSource()==savePohotBtn){
            savePhotoImage();
        }
    }

    private void setPhotoImage() {
        if (view.getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
            view.getPlanControler().getBaseFace().displayLine(false);
        }
        photoImage = view.getCamera3D().photograph(1024, 768);
        imagePanel.setImage(FileUtil.reSizeImage(photoImage, 700, 400));
        if (view.getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
            view.getPlanControler().getBaseFace().displayLine(true);
        }
    }

    private void savePhotoImage() {
        final JFileChooser filesaver = new JFileChooser();
        filesaver.setDialogTitle("保存...");
        filesaver.addChoosableFileFilter(new FileNameExtensionFilter(
                "*.jpg;", "jpg"));
        int returnVal = filesaver.showSaveDialog(JOptionPane.getRootFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = filesaver.getSelectedFile();
            if (!selectedFile.getName().endsWith(".jpg")) {
                selectedFile = new File(selectedFile.getPath() +
                        ".jpg");
            }
            if (!selectedFile.exists()) {
                try {
                    ImageIO.write(photoImage, "JPG", selectedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                int userSelect = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                        "是否覆盖当前文件？",
                        "是否覆盖",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (userSelect == JOptionPane.OK_OPTION) {
                    try {
                        ImageIO.write(photoImage, "JPG", selectedFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}