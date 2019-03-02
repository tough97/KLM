package cs.modeleditor;

import cs.designer.swing.property.PropertyPanel;
import cs.designer.swing.ui.SwingTool;
import cs.designer.view.viewer.HousePlanView;
import cs.designer.view.viewer.MerchandiseEditorView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


public class KLMModelEditor extends JApplet {
    static {
        SwingTool.initUI();
    }

    public KLMModelEditor() {
        final MerchandiseEditorView displayer = new MerchandiseEditorView();
        final MerchandiseCtrlPanel controlPanel = new MerchandiseCtrlPanel(displayer);
        setJMenuBar(createMenu(controlPanel));
        setLayout(new BorderLayout());
        PropertyPanel propertyPanel = new PropertyPanel();
        HousePlanView view = new HousePlanView(propertyPanel);
        view.addDropTargetListener(new PreviewDropTargetListener(view, displayer));
        add(BorderLayout.CENTER, view);
        add(BorderLayout.WEST, controlPanel);
        add(BorderLayout.SOUTH, propertyPanel);
    }

    private JMenuBar createMenu(ActionListener listener) {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu("文件");
        final JMenuItem importMenuItem = new JMenuItem("导入");
        final JMenuItem loadMenuItem = new JMenuItem("打开");
        final JMenuItem preViewMenuItem = new JMenuItem("预览");
        final JMenu helpMenu = new JMenu("帮助");
        final JMenuItem saveAsItem=new JMenuItem("另存为");
        helpMenu.setActionCommand("help");
        menuBar.add(fileMenu);
        fileMenu.add(importMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(preViewMenuItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        preViewMenuItem.addActionListener(listener);
        preViewMenuItem.setActionCommand(MerchandiseCtrlPanel.PRE_VIEW);
        importMenuItem.addActionListener(listener);
        importMenuItem.setActionCommand(MerchandiseCtrlPanel.IMPORT_FILE);
        loadMenuItem.addActionListener(listener);
        loadMenuItem.setActionCommand(MerchandiseCtrlPanel.LOAD_FILE);
        saveAsItem.addActionListener(listener);
        saveAsItem.setActionCommand(MerchandiseCtrlPanel.SAVE_FILE);
        menuBar.add(helpMenu);
        return menuBar;
    }

    public static void main(String[] args) {
        final JFrame mainFrame = new JFrame("考拉猫模型编辑器");
        mainFrame.setSize(mainFrame.getToolkit().getScreenSize());
        mainFrame.setLocation(0, 0);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final KLMModelEditor uploader = new KLMModelEditor();
        mainFrame.getContentPane().add(uploader);
        mainFrame.setVisible(true);
    }

}
