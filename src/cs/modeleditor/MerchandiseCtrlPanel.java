package cs.modeleditor;

import com.klm.persist.Merchandise;
import com.klm.persist.impl.Furniture;
import com.klm.persist.impl.LocalStorage;
import com.klm.persist.impl.SurfaceMaterial;
import cs.designer.io.local.MerchandiseLoader;
import cs.designer.io.local.MerchandiseSaveLoader;
import cs.designer.io.local.SaveLoader;
import cs.designer.io.net.MerchandiseNetIO;
import cs.designer.io.net.NetOperation;
import cs.designer.swing.bean.MerchandiseBean;
import cs.designer.utils.FileUtil;
import cs.designer.view.viewer.MerchandiseEditorView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA. User: rongyang Date: 1/12/12 Time: 9:36 AM To
 * change this template use File | Settings | File Templates.
 */
public class MerchandiseCtrlPanel extends JPanel implements ActionListener {

    public static final String SAVE_FILE = "save";
    public static final String LOAD_FILE = "load";
    public static final String IMPORT_FILE = "import";
    public static final String PRE_VIEW = "preview";
    private File lastOpenDir;
    private File lastSaveDir;
    private JPanel tooPanel;
    private MerchandiseEditorView displayer;
    private MerchandiseLoader saveLoader;
    private ModelPropertiesPanel propertiesPanel;
    private JTabbedPane tabbedPane;
    private MerchandisePreviewPanel previewPanel;
    private ModelPerviewPanel modelPerviewPanel;


    public MerchandiseCtrlPanel(final MerchandiseEditorView view) {
        tooPanel = new TooPanel(this);
        this.displayer = view;
        init();
    }

    private void init() {
        saveLoader = new MerchandiseLoader();
        propertiesPanel = new ModelPropertiesPanel(displayer);
        modelPerviewPanel = new ModelPerviewPanel(this, propertiesPanel);
        displayer.setBackGroupColor(Color.GRAY);
        previewPanel = new MerchandisePreviewPanel();
        tabbedPane = new JTabbedPane();
        final Dimension size = new Dimension(400, 600);
        tabbedPane.setPreferredSize(size);
        modelPerviewPanel.setPreferredSize(size);
        previewPanel.setPreferredSize(size);
        tabbedPane.addTab("模型预览", new JScrollPane(modelPerviewPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        tabbedPane.addTab("预览", new JScrollPane(previewPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
          tabbedPane.addTab("批量上传", new JScrollPane(new BatchUploadPanel(),
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        add(tabbedPane);
    }

    public void actionPerformed(final ActionEvent ae) {
        String command = ae.getActionCommand();
        if (LOAD_FILE == command) {
            loadFile();
        } else if (SAVE_FILE == command) {
            preViewMerchandise();
            saveFile();
        } else if (PRE_VIEW == command) {
            preViewMerchandise();
        } else if (IMPORT_FILE == command) {
            importFile();
        }
    }


    public MerchandiseEditorView getDisplayer() {
        return displayer;
    }

    private void loadFile() {
        final JFileChooser fileLoader = new JFileChooser();
        fileLoader.setDialogTitle("打开...");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("材质(*.jpg;*.png;*.gif;*.bmp)",
                "jpg", "gif", "png", "bmp");
        if (lastOpenDir != null) {
            fileLoader.setCurrentDirectory(lastOpenDir);
        }
        fileLoader.addChoosableFileFilter(filter);
        fileLoader.addChoosableFileFilter(new FileNameExtensionFilter(
                "3D模型文件(*.obj;)", "obj"));
        int returnVal = fileLoader.showOpenDialog(JOptionPane.getRootFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileLoader.getSelectedFile();
            lastOpenDir = selectedFile.getParentFile();
            Merchandise currentMerchandise = saveLoader.load(selectedFile);
            if (currentMerchandise != null) {
                displayer.setCurrentMerchandise(currentMerchandise);
                (propertiesPanel).setPreferreds();
            }
        }

    }

    private void importFile() {
        final JFileChooser fileLoader = new JFileChooser();
        fileLoader.setDialogTitle("打开...");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("材质(*.sm)",
                "sm");
        fileLoader.addChoosableFileFilter(filter);
        fileLoader.addChoosableFileFilter(new FileNameExtensionFilter(
                "3D模型文件(*.fu)", "fu"));
        if (lastOpenDir != null) {
            fileLoader.setCurrentDirectory(lastOpenDir);
        }
        int returnVal = fileLoader.showOpenDialog(JOptionPane.getRootFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileLoader.getSelectedFile();
            lastOpenDir = selectedFile.getParentFile();
            final MerchandiseSaveLoader saveLoader = new MerchandiseSaveLoader(selectedFile);
            final Merchandise currentMerchandise = saveLoader.getObject();
            if (currentMerchandise != null) {
                displayer.setCurrentMerchandise(currentMerchandise);
                (propertiesPanel).setPreferreds();
            }
        }

    }

    private void saveFurnitureFile(final File furnitureFile) {
        try {
            final Furniture currentFurniture = (Furniture) displayer.getCurrentMerchandise();
            if (propertiesPanel.getScale() != 1) {
                currentFurniture.getModel().setModelScale(propertiesPanel.getScale());
                propertiesPanel.rest();
            }
            if (propertiesPanel.installationable()) {
                currentFurniture.getModel().calculateInstallCoords();
            }
            final MerchandiseSaveLoader saveLoader = new MerchandiseSaveLoader(furnitureFile);
            saveLoader.setObject(currentFurniture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSurfaceMaterialFile(final File surfaceMaterialFile) {
        try {
            final SurfaceMaterial currentMaterial = (SurfaceMaterial) displayer.getCurrentMerchandise();
            final MerchandiseSaveLoader saveLoader = new MerchandiseSaveLoader(surfaceMaterialFile);
            saveLoader.setObject(currentMaterial);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        final JFileChooser filesaver = new JFileChooser();
        filesaver.setDialogTitle("保存...");
        filesaver.addChoosableFileFilter(new FileNameExtensionFilter(
                "考拉猫模型文件(*.fn;)", LocalStorage.FURNITURE_EXT));
        filesaver.addChoosableFileFilter(new FileNameExtensionFilter(
                "考拉猫模材质文件(*.sm;)", LocalStorage.SURFACE_MATERIAL_EXT));
        if (lastSaveDir != null) {
            filesaver.setCurrentDirectory(lastSaveDir);
        }
        int returnVal = filesaver.showSaveDialog(JOptionPane.getRootFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = filesaver.getSelectedFile();
            lastSaveDir = selectedFile.getParentFile();
            if (displayer.getCurrentMerchandise() instanceof Furniture) {
                if (!selectedFile.getName().endsWith(LocalStorage.FURNITURE_EXT)) {
                    selectedFile = new File(selectedFile.getPath()
                            + LocalStorage.FURNITURE_EXT);
                }
                if (selectedFile.exists()) {
                    int userSelect = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                            "是否覆盖当前文件？",
                            "是否覆盖",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    if (userSelect == JOptionPane.OK_OPTION) {
                        saveFurnitureFile(selectedFile);
                    }
                } else {
                    saveFurnitureFile(selectedFile);
                }
            } else if (displayer.getCurrentMerchandise() instanceof SurfaceMaterial) {
                if (!selectedFile.getName().endsWith(LocalStorage.SURFACE_MATERIAL_EXT)) {
                    selectedFile = new File(selectedFile.getPath()
                            + LocalStorage.SURFACE_MATERIAL_EXT);
                }
                if (selectedFile.exists()) {
                    int userSelect = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                            "是否覆盖当前文件？",
                            "是否覆盖",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    if (userSelect == JOptionPane.OK_OPTION) {
                        saveSurfaceMaterialFile(selectedFile);
                    }
                } else {
                    saveSurfaceMaterialFile(selectedFile);
                }
            }
        }
    }


    private void preViewMerchandise() {
        final Merchandise currentMerchandise = displayer.getCurrentMerchandise();
        final MerchandiseBean merchandiseBean = new MerchandiseBean();
        if (currentMerchandise instanceof Furniture) {
            final Furniture currentFurniture = (Furniture) currentMerchandise;
            merchandiseBean.setType(MerchandiseBean.MerchandiseType.furniture);
            if (propertiesPanel.getScale() != 1) {
                currentFurniture.getModel().setModelScale(propertiesPanel.getScale());
                propertiesPanel.rest();
            }
            if (propertiesPanel.installationable()) {
                currentFurniture.getModel().calculateInstallCoords();
            }
        } else if (currentMerchandise instanceof SurfaceMaterial) {
            merchandiseBean.setType(MerchandiseBean.MerchandiseType.surfaceMaterial);
        }
        currentMerchandise.setUnitName(propertiesPanel.getUintName());
        merchandiseBean.setModelSource(FileUtil.objectToBytes(currentMerchandise));
        merchandiseBean.setName(propertiesPanel.getNameValue());
        previewPanel.setMerchandiseBean(merchandiseBean);
    }
}
