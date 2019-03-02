package cs.designer.swing.ui;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Room;
import com.klm.persist.Merchandise;
import com.klm.util.CSUtilException;
import com.klm.util.MerchandiseStatisticToExcel;
import com.klm.util.RealNumberOperator;
import com.klm.util.impl.MerchandiseInfo;
import cs.designer.view.viewer.HousePlanView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/15/12
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CostDialog extends JDialog implements ActionListener {
    private static final String EXTEBSUIB_NAME = ".xls";
    private static final String[] TITLE = {
            "房间", "商品", "单价", "数量", "单位", "合计"
    };
    private HousePlanView view;
    private JButton exportDateBtn;
    private JTable costTable;
    private DefaultTableModel model;

    public CostDialog(final Window parent, final HousePlanView view, final String title) {
        super(parent, title);
        setModal(true);
        this.view = view;
        setResizable(true);
        setSize(574, 526);
        int locationX = getParent().getLocation().x + (getParent().getWidth()
                - getWidth()) / 2;
        int locationY = getParent().getLocation().y + (getParent().getHeight()
                - getHeight()) / 2;
        setLocation(locationX, locationY);
        init();
        fillDate();


    }

    private void init() {

        model = new DefaultTableModel(null, TITLE) {

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        exportDateBtn = new JButton("导出");
        exportDateBtn.addActionListener(this);
        costTable = new JTable(model);
        JPanel buttomPanel = new JPanel();
        buttomPanel.add(exportDateBtn);
        getContentPane().add(BorderLayout.CENTER, new JScrollPane(costTable));
        getContentPane().add(BorderLayout.SOUTH, buttomPanel);
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(JLabel.CENTER);
        costTable.setDefaultRenderer(Object.class, tcr);

    }

    private void fillDate() {

        final Map<Room, MerchandiseInfo> roomMerchandiseInfoMap;
        try {
            if (view != null) {
                roomMerchandiseInfoMap = view.getCurrentFloor().getFloorMerchandiseInfo();
                for (final Room room : roomMerchandiseInfoMap.keySet()) {
                    if (room != null) {
                        Object[] data = {room.getRoomName()};
                        model.addRow(data);
                        for (Merchandise merchandise : roomMerchandiseInfoMap.get(room).getMerchandiseSet()) {
                            double amount = RealNumberOperator.roundNumber(roomMerchandiseInfoMap.get(room).
                                    getQuantityCounter().get(merchandise), 2);
                            data = new Object[]{"", merchandise.getName(), merchandise.getUnitPrice(), amount,
                                    merchandise.getUnitName(), merchandise.getUnitPrice() * amount};
                            model.addRow(data);

                        }

                    }
                }
                Object[] data = {"其它"};
                model.addRow(data);
                for (Merchandise merchandise : roomMerchandiseInfoMap.get(null).getMerchandiseSet()) {
                    double amount = RealNumberOperator.roundNumber(roomMerchandiseInfoMap.get(null).
                            getQuantityCounter().get(merchandise), 2);
                    data = new Object[]{"", merchandise.getName(), merchandise.getUnitPrice(), amount,
                            merchandise.getUnitName(), merchandise.getUnitPrice() * amount};
                    model.addRow(data);

                }

            }
        } catch (CSHouseException e) {
            e.printStackTrace();
        }


    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == exportDateBtn) {
            try {
                final JFileChooser filesaver = new JFileChooser();
                filesaver.setDialogTitle("保存...");
                filesaver.addChoosableFileFilter(new FileNameExtensionFilter(
                        "Excel文件(*.xls;)", "xls"));
                int returnVal = filesaver.showSaveDialog(JOptionPane.getRootFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = filesaver.getSelectedFile();
                    if (!selectedFile.getName().endsWith(EXTEBSUIB_NAME)) {
                        selectedFile = new File(selectedFile.getPath() + EXTEBSUIB_NAME);
                    }
                    if (!selectedFile.exists()) {
                        MerchandiseStatisticToExcel.toExcel(view.getCurrentHouse(), selectedFile.getPath());
                    } else {
                        int userSelect = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                                "是否覆盖当前文件？",
                                "是否覆盖",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        if (userSelect == JOptionPane.OK_OPTION) {
                            MerchandiseStatisticToExcel.toExcel(view.getCurrentHouse(), selectedFile.getPath());
                        }
                    }
                }

            } catch (CSUtilException e) {
                e.printStackTrace();
            }
            setVisible(false);
        }
    }
}
