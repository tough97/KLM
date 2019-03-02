package cs.modeleditor;

import com.klm.persist.CSPersistException;
import com.klm.persist.impl.LocalStorage;
import com.klm.persist.impl.SurfaceMaterial;
import com.klm.persist.meta.BufferedImageMeta;
import cs.designer.io.local.MerchandiseSaveLoader;
import cs.designer.io.net.MerchandiseNetIO;
import cs.designer.io.net.NetOperation;
import cs.designer.io.net.FileNetIO;
import cs.designer.swing.bean.FileBean;
import cs.designer.swing.bean.MerchandiseBean;
import cs.designer.swing.bean.MerchandiseBrandBean;
import cs.designer.swing.bean.ProviderBean;
import cs.designer.swing.tool.CoFilenameFilter;
import cs.designer.swing.tool.VerticalLayout;
import cs.designer.utils.ImageScalerImageUtill;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/5/12
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class BatchUploadPanel extends JPanel implements ActionListener {
    public static final int IMAGE_WIDTH = 708;  //material
    public static final float MATERIAL_WIDTH = 3.2f;
    public static final int MATERIAL_FILE = 0;
    public static final int IMAGE_FILE = 1;
    public static final String[] IMAGE_TYPE = {".jpg", ".png", ".gif"};
    private JTextField filePath;
    private JButton chooseBtn;
    private JButton uploadBtn;
    private JTextField status;
    private JTextField uintNameValue;

    public BatchUploadPanel() {
        super();
        init();

    }

    private void init() {
        setLayout(new VerticalLayout());
        uintNameValue = new JTextField(10);
        filePath = new JTextField(20);
        chooseBtn = new JButton("选择路径");
        uploadBtn = new JButton("开始上传");
        status = new JTextField(20);
        final JPanel uintNamepanel = new JPanel();
        uintNamepanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        uintNamepanel.add(new JLabel("单位"));
        uintNamepanel.add(uintNameValue);
        final JPanel filePanel = new JPanel();
        filePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(filePath);
        filePanel.add(chooseBtn);
        add(filePanel);
        final JPanel uploadPanel = new JPanel();
        uploadPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        uploadPanel.add(uploadBtn);
        add(uintNamepanel);
        add(uploadPanel);
        add(status);
        chooseBtn.addActionListener(this);
        uploadBtn.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chooseBtn) {
            final JFileChooser fileLoader = new JFileChooser();
            fileLoader.setDialogTitle("打开...");
            fileLoader.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileLoader.showOpenDialog(JOptionPane.getRootFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = fileLoader.getSelectedFile();
                filePath.setText(selectedFile.getPath());
            }
        } else if (e.getSource() == uploadBtn) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    upload();
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "上传成功", "",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });

        }

    }

    private void upload() {
        File rootDir = new File(filePath.getText());
        if (!rootDir.exists()) {
            return;
        }
        try {
            final File[] subDirs = rootDir.listFiles();
            for (File subDir : subDirs) {
                SAXReader reader = new SAXReader();
                Document document = null;
                document = reader.read(new File(subDir, "property.xml"));
                Element rootElm = document.getRootElement();
                java.util.List list = rootElm.elements();
                Object property = list.get(0);
                Element element = (Element) property;
                float price = Float.valueOf(element.elementText("price"));
                final String brandId = element.elementText("brandId");
                final String description = element.elementText("description");
                final String categoryId = element.elementText("categoryId");
                float discount = Float.valueOf(element.elementText("discount"));
                final String retailerId = element.elementText("retailerId");
                final String uintName = uintNameValue.getText();
                final File[] imageFiles = subDir.listFiles(new CoFilenameFilter(IMAGE_TYPE));
                for (File imageFile : imageFiles) {
                    status.setText(imageFile.getPath());
                    File[] sourceFiles = getSourceFiles(imageFile, uintName);
                    final String merchandiseName = imageFile.getParentFile().getName() + imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
                    FileBean[] fileBeans = uploadTempFiles(sourceFiles);
                    final FileBean materialFile = fileBeans[MATERIAL_FILE];
                    final FileBean materialImageFile = fileBeans[IMAGE_FILE];
                    System.out.println(materialFile.getServerPath());
                    System.out.println(materialImageFile.getServerPath());
                    final MerchandiseBean merchandiseBean = new MerchandiseBean();
                    merchandiseBean.setName(merchandiseName);
                    merchandiseBean.setStyleId("1");
                    merchandiseBean.setBrand(new MerchandiseBrandBean(brandId));
                    merchandiseBean.setDescription(description);
                    merchandiseBean.setIconPath(materialImageFile.getServerPath());
                    merchandiseBean.setCategoryId(categoryId);
                    merchandiseBean.setSourceFilePath(materialFile.getServerPath());
                    merchandiseBean.setUnitPrice(price);
                    merchandiseBean.setDiscount(discount);
                    merchandiseBean.setProvider(new ProviderBean(retailerId));
                    final NetOperation operation = new MerchandiseNetIO(merchandiseBean);
                    operation.upload();
                    imageFile.delete();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized File[] getSourceFiles(final File imageFile, final String uintName) throws IOException, CSPersistException {
        final File[] sourceFiles = new File[2];
        BufferedImage image = ImageIO.read(imageFile);
        image = ImageScalerImageUtill.scaleImage(image, IMAGE_WIDTH);
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        double imageRatio = (double) height / (double) width;
        final String name = imageFile.getParentFile().getName() + imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
        final File materialDir = new File(imageFile.getParentFile(), name);
        materialDir.mkdirs();
        final SurfaceMaterial material = new SurfaceMaterial(name, MATERIAL_WIDTH,
                MATERIAL_WIDTH * imageRatio, false, new BufferedImageMeta(image));
        material.setUnitName(uintName);
        final File materialFile = new File(materialDir, name + LocalStorage.SURFACE_MATERIAL_EXT);
        final MerchandiseSaveLoader saveLoader = new MerchandiseSaveLoader(materialFile);
        saveLoader.setObject(material);
        //
        String imageExtensionName = getExtensionName(imageFile.getName());
        final File materialImageFile = new File(materialDir, imageFile.getName());
        ImageIO.write(material.getBufferredImage(), imageExtensionName, materialImageFile);
        sourceFiles[MATERIAL_FILE] = materialFile;
        sourceFiles[IMAGE_FILE] = imageFile;
        return sourceFiles;
    }

    private String getExtensionName(final String fileName) {
        final String extensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extensionName;
    }

    private FileBean[] uploadTempFiles(File[] files) {
        FileBean[] fileBeans = new FileBean[files.length];
        for (int index = 0; index < files.length; index++) {
            final FileBean fileBean = new FileBean(files[index]);
            NetOperation operation = new FileNetIO(fileBean);
            operation.upload();
            fileBeans[index] = fileBean;
        }
        return fileBeans;
    }

}


