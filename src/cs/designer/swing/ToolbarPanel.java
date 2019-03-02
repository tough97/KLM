package cs.designer.swing;


import cs.designer.swing.icons.IconManager;
import cs.designer.swing.tool.HorizontalLayout;
import cs.designer.swing.ui.CoButton;
import cs.designer.swing.ui.SwingTool;
import cs.designer.swing.undo.HouseEdit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ToolbarPanel extends JPanel {
    public final static int TOOLS_DESC = 20;
    private static JButton redoBtn;
    private static JButton undoBtn;


    public ToolbarPanel(final ActionListener listener) {
        super();
        init(listener);
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        setBackground(SwingTool.THEME_COLOR);
    }

    private void init(final ActionListener actionListener) {
        setPreferredSize(new Dimension(0, 42));
//        setLayout(new FlowLayout(FlowLayout.LEFT, TOOLS_DESC, 0));
        setLayout(new HorizontalLayout(5));
        final ButtonGroup group = new ButtonGroup();
        final JPanel fileToolPanel = new JPanel();
        fileToolPanel.setOpaque(false);
        add(fileToolPanel);
        createFileTools(actionListener, group, fileToolPanel);
        final JPanel editPanel = new JPanel();
        editPanel.setOpaque(false);
        add(editPanel);
        creatEditTools(actionListener, group, editPanel);
        final JPanel viewPanel = new JPanel();
        viewPanel.setOpaque(false);
        add(viewPanel);
        createViewTools(actionListener, group, viewPanel);

    }

    private void createFileTools(final ActionListener actionListener,
                                 final ButtonGroup group, final JComponent parent) {
        final JButton saveFileBtn = new CoButton(IconManager.getSelectedIcons("save"));
        saveFileBtn.setActionCommand(ActionCommandKey.SAVE);
        saveFileBtn.addActionListener(actionListener);
        saveFileBtn.setToolTipText("保存户型");
        final JButton loadFileBtn = new CoButton(IconManager.getSelectedIcons("open"));
        loadFileBtn.setActionCommand(ActionCommandKey.LOAD_FILE);
        loadFileBtn.addActionListener(actionListener);
        loadFileBtn.setToolTipText("打开户型");
        final JButton newFile = new CoButton(IconManager.getSelectedIcons("new"));
        newFile.setActionCommand(ActionCommandKey.NEW_FILE);
        newFile.addActionListener(actionListener);
        newFile.setToolTipText("新建文件");
        final JButton importPlan = new CoButton(IconManager.getSelectedIcons("input"));
        importPlan.setToolTipText("导入户型图");
        importPlan.setActionCommand(ActionCommandKey.IMPORT_PLAN);
        importPlan.addActionListener(actionListener);
        //
        final JButton outPutPlan = new CoButton(IconManager.getSelectedIcons("output"));
        outPutPlan.setToolTipText("导出效果图");
        outPutPlan.setActionCommand(ActionCommandKey.PHOTO);
        outPutPlan.addActionListener(actionListener);
        //
        parent.add(newFile);
        parent.add(loadFileBtn);
        parent.add(importPlan);
        parent.add(outPutPlan);
        parent.add(saveFileBtn);

        final JLabel splitLine = new JLabel(new ImageIcon(IconManager.getIconUrl("sepeline.png")));
        splitLine.setOpaque(false);
        parent.add(splitLine);
        group.add(saveFileBtn);
        group.add(newFile);
        group.add(loadFileBtn);
        group.add(saveFileBtn);
        group.add(outPutPlan);

    }

    private void creatEditTools(final ActionListener actionListener,
                                final ButtonGroup group, final JComponent parent) {
        undoBtn = new CoButton(IconManager.getSelectedIcons("undo"));
        undoBtn.setActionCommand(ActionCommandKey.UNDO);
        undoBtn.addActionListener(actionListener);
        undoBtn.setToolTipText("撤销");
        redoBtn = new CoButton(IconManager.getSelectedIcons("redo"));
        redoBtn.setActionCommand(ActionCommandKey.REDO);
        redoBtn.addActionListener(actionListener);
        redoBtn.setToolTipText("恢复");

        //
        final JButton pan = new CoButton(IconManager.getSelectedIcons("draw"));
        pan.setActionCommand(ActionCommandKey.PAN_COMD);
        pan.addActionListener(actionListener);
        pan.setToolTipText("画笔");
        final JButton creatRoom = new CoButton(IconManager.getSelectedIcons("build"));
        creatRoom.setActionCommand(ActionCommandKey.CREATE_ROOM);
        creatRoom.addActionListener(actionListener);
        creatRoom.setToolTipText("建房");
        final JButton deleteBtn = new CoButton(IconManager.getSelectedIcons("delete"));
        deleteBtn.setActionCommand(ActionCommandKey.DELETE_OBJECT);
        deleteBtn.addActionListener(actionListener);
        deleteBtn.setToolTipText("删除");
        parent.add(undoBtn);
        parent.add(redoBtn);
        parent.add(pan);
        parent.add(creatRoom);
        parent.add(deleteBtn);
        final JLabel splitLine = new JLabel(new ImageIcon(IconManager.getIconUrl("sepeline.png")));
        splitLine.setOpaque(false);
        parent.add(splitLine);
        group.add(pan);
        group.add(creatRoom);
        group.add(deleteBtn);
        group.add(undoBtn);
        group.add(redoBtn);
        checkDoEdit();
    }

    private void createViewTools(final ActionListener actionListener,
                                 final ButtonGroup group, final JComponent parent) {
        final JButton changeViewBtn = new CoButton(IconManager.getSelectedIcons("view"));
        changeViewBtn.setActionCommand(ActionCommandKey.CHANGE_VIEW);
        changeViewBtn.addActionListener(actionListener);
        changeViewBtn.setPreferredSize(new Dimension(changeViewBtn.getIcon().getIconWidth(),
                changeViewBtn.getIcon().getIconHeight()));
        changeViewBtn.setToolTipText("切换视图");
        parent.add(changeViewBtn);
        final JButton costCalculate = new CoButton(IconManager.getSelectedIcons("budget"));
        costCalculate.setToolTipText("成本统计");
        costCalculate.setActionCommand(ActionCommandKey.COST);
        costCalculate.addActionListener(actionListener);
        final JButton shoppingCartBtn = new CoButton(IconManager.getSelectedIcons("shopcar"));
        shoppingCartBtn.addActionListener(actionListener);
        shoppingCartBtn.setActionCommand(ActionCommandKey.SHOPPING_CART);
        parent.add(costCalculate);
        parent.add(shoppingCartBtn);
    }

    public static void checkDoEdit() {
        redoBtn.setEnabled(HouseEdit.getHouseEditor().canRedo());
        undoBtn.setEnabled(HouseEdit.getHouseEditor().canUndo());
    }

}
