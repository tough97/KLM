package cs.designer.swing;


import cs.designer.io.local.FileExtensionUtill;
import cs.designer.io.local.LocalFileManage;
import cs.designer.module.Pan;
import cs.designer.swing.ui.*;
import cs.designer.swing.undo.HouseEdit;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ControlResponser implements ActionListener {
    private HousePlanView view;

    public ControlResponser(final HousePlanView view) {
        this.view = view;

    }

    public void actionPerformed(ActionEvent ae) {
        final String command = ae.getActionCommand();
        if (ActionCommandKey.NEW_FILE == command) {
            newFileCommand();
        } else if (ActionCommandKey.CREATE_ROOM == command) {
            view.createRoom();
        } else if (ActionCommandKey.PAN_COMD == command) {
            panCommand();

        } else if (ActionCommandKey.SAVE_AS_FILE == command) {
            saveFile();
        } else if (ActionCommandKey.UPLOAD_FILE == command) {
            uploadHouse();
        } else if (ActionCommandKey.LOAD_FILE == command) {
            openFile();
        } else if (ActionCommandKey.SAVE == command) {
            Window window = SwingUtilities.getWindowAncestor(view);
            JDialog dialog = new SaveHouseDialog(window, this);
            dialog.setVisible(true);
        } else if (ActionCommandKey.IMPORT_PLAN == command) {
            importPlanImage();

        } else if (ActionCommandKey.CHANGE_VIEW == command) {
            view.getPlanViewControler().changeView();
        } else if (ActionCommandKey.REDO == command) {
            HouseEdit.getHouseEditor().redo();
            view.getToolPanl().checkDoEdit();

        } else if (ActionCommandKey.UNDO == command) {
            HouseEdit.getHouseEditor().undo();
            view.getToolPanl().checkDoEdit();
        } else if (ActionCommandKey.DELETE_OBJECT == command) {
            view.setOperateType(DisplayView.OperateType.DELETE_OBJECT);
            if (view.getViewType() == DisplayView.ViewType.ORBIT_VIEW
                    || view.getViewType() == DisplayView.ViewType.VIRTUAL_VIEW) {
                view.deleteSelectObject(null);
                view.setOperateType(DisplayView.OperateType.DEFAULT);
            }
        } else if (ActionCommandKey.ZOOM_IN == command) {
            view.getPlanViewControler().zoomIn(-3);

        } else if (ActionCommandKey.ZOOM_OUT == command) {
            view.getPlanViewControler().zoomIn(3);

        } else if (ActionCommandKey.LOGIN == command) {
            login();

        } else if (ActionCommandKey.COST == command) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    Window window = SwingUtilities.getWindowAncestor(view);
                    CostDialog cost = new CostDialog(window, view, "成本统计");
                    cost.setVisible(true);
                }
            });

        } else if (ActionCommandKey.INSTALLATION == command) {
            view.installFurniture();
        } else if (ActionCommandKey.DRAW_PLAN_VIEW == command) {
            view.getPlanViewControler().drawPlanView();

        } else if (ActionCommandKey.OBIT_VIEW == command) {
            view.getPlanViewControler().orbitView();

        } else if (ActionCommandKey.VIRTUAL_VIEW == command) {
            view.getPlanViewControler().virtuallView();

        }else if (ActionCommandKey.PHOTO == command) {
            photoImage();
        } else if (ActionCommandKey.SHOPPING_CART == command) {
            ShoppingCart.show(SwingUtilities.getWindowAncestor(view));
        }

    }

    private void panCommand() {
        view.getPropertyPanel().setPropertys(Pan.getPan());
        if (view.getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
            view.setOperateType(DisplayView.OperateType.DRAW_PLAN);

        } else if (view.getViewType() == DisplayView.ViewType.ORBIT_VIEW) {
            view.setOperateType(DisplayView.OperateType.ADD_SUBSURFACE);
            Pan.getPan().setPanType(Pan.PanType.RECTAMGLE);
        } else if (view.getViewType() == DisplayView.ViewType.VIRTUAL_VIEW) {
            view.setOperateType(DisplayView.OperateType.ADD_SUBSURFACE);
            Pan.getPan().setPanType(Pan.PanType.RECTAMGLE);

        }

    }

    private void newFileCommand() {
        int userSelect = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                "是否保存当前文件？",
                "是否保存",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (userSelect == JOptionPane.OK_OPTION) {
            saveFile();
        } else if (userSelect == JOptionPane.NO_OPTION) {
            view.clear();
        }
    }

    private void saveFile() {
        final JFileChooser filesaver = new JFileChooser();
        filesaver.setDialogTitle("保存...");
        filesaver.addChoosableFileFilter(new FileNameExtensionFilter(
                "考拉猫户型文件(*.klm;)", "klm"));
        int returnVal = filesaver.showSaveDialog(JOptionPane.getRootFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = filesaver.getSelectedFile();
            if (!selectedFile.getName().endsWith(FileExtensionUtill.
                    HOUSE_EXTEBSUIB_NAME)) {
                selectedFile = new File(selectedFile.getPath() +
                        FileExtensionUtill.HOUSE_EXTEBSUIB_NAME);
            }
            boolean sucess = true;
            if (!selectedFile.exists()) {
                sucess = LocalFileManage.outPutLocalFile(view.getCurrentHouse(), selectedFile);
            } else {
                int userSelect = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                        "是否覆盖当前文件？",
                        "是否覆盖",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (userSelect == JOptionPane.OK_OPTION) {
                    sucess = LocalFileManage.outPutLocalFile(view.getCurrentHouse(), selectedFile);
                }
            }
            String image = "保存成功";
            if (!sucess) {
                image = "保存失败";
            }
            JOptionPane.showMessageDialog(view, image, "",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void photoImage() {
        final JDialog photoDialog = new PhotoDialog(view, "效果图");
        photoDialog.setVisible(true);
    }

    public void openFile() {
        boolean loadFileSuccess = false;
        final JFileChooser fileloader = new JFileChooser();
        fileloader.setDialogTitle("打开...");
        fileloader.addChoosableFileFilter(new FileNameExtensionFilter(
                "考拉猫户型文件(*.klm;)", "klm"));
        int returnVal = fileloader.showOpenDialog(JOptionPane.getRootFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileloader.getSelectedFile();
            LocalFileManage.importLocalFile(selectedFile, view);
        }
    }

    public void importPlanImage() {
        final JFileChooser fileloader = new JFileChooser();
        fileloader.setDialogTitle("打开...");
        int returnVal = fileloader.showOpenDialog(JOptionPane.getRootFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileloader.getSelectedFile();
            if (selectedFile != null) {
                final JDialog importPlan =
                        new ImportPlanImageDialog(view,
                                "导入户型图", selectedFile);
                importPlan.setVisible(true);
            }
        }

    }

    private void uploadHouse() {
        final UploadHouseDialog dialog = new UploadHouseDialog("上传", view);
        if (view.getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
            view.getPlanControler().getBaseFace().displayLine(false);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.setPhoto(view.getCamera3D().photograph(600,600));
            }
        });

        if (view.getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
            view.getPlanControler().getBaseFace().displayLine(true);
        }
        dialog.setVisible(true);
    }

    public void login() {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                final Window window = SwingUtilities.getWindowAncestor(view);
//                final JDialog loginDialog = new LoginDialog(window, "登录");
//                loginDialog.setVisible(true);
//            }
//        });
    }

//    public void loadOwnHose() {
//        Window window = SwingUtilities.getWindowAncestor(view);
//        final OwnHoseDialog openHouseDialog = new OwnHoseDialog(window, "open.....");
//        openHouseDialog.setVisible(true);
//    }


}
