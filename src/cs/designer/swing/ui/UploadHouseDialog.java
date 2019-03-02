package cs.designer.swing.ui;

import com.klm.cons.impl.House;
import cs.designer.io.net.*;
import cs.designer.swing.TopPanel;
import cs.designer.swing.bean.*;
import cs.designer.swing.tool.VerticalLayout;
import cs.designer.utils.FileUtil;
import cs.designer.view.viewer.HousePlanView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;


/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/27/12
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadHouseDialog extends JDialog implements ItemListener, ActionListener, LoginTask {
    private JPanel imagePanel;
    private BufferedImage image;
    private JComboBox provinceComboBox;
    private JComboBox cityComboBox;
    private JComboBox compoundComboBox;
    private HousePlanView view;
    private JTextArea description;
    private JButton uploadBtn;

    public UploadHouseDialog(final String title,
                             final HousePlanView view) {
        super(SwingUtilities.getWindowAncestor(view), title);
        this.view = view;
        setModal(true);
        setSize(new Dimension(750, 350));
        int locationX = getParent().getLocation().x + (getParent().getWidth()
                - getWidth()) / 2;
        int locationY = getParent().getLocation().y + (getParent().getHeight()
                - getHeight()) / 2;
        setLocation(locationX, locationY);
        init();
        setResizable(false);

    }

    private void init() {
        provinceComboBox = new JComboBox(LocationBean.PROVINCES_LOCATION.getJurisdicLocations().toArray());
        cityComboBox = new JComboBox();
        compoundComboBox = new JComboBox();
        provinceComboBox.addItemListener(this);
        compoundComboBox.addItemListener(this);
        cityComboBox.addItemListener(this);
        provinceComboBox.setPreferredSize(new Dimension(120, 25));
        cityComboBox.setPreferredSize(new Dimension(120, 25));
        compoundComboBox.setPreferredSize(new Dimension(120, 25));
        description = new JTextArea(10, 20);
        description.setLineWrap(true);
        uploadBtn = new JButton("保 存");
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        uploadBtn.addActionListener(this);
        final JPanel rootPanel = new JPanel();
        final JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new VerticalLayout(10));
        imagePanel = new JPanel() {
            Point pBegin = new Point();
            Point pEnd = new Point();

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                if (image != null) {
                    countImagePoint(image);
                    g2.drawImage(image, pBegin.x, pBegin.y, pEnd.x, pEnd.y,
                            0, 0, image.getWidth(), image.getHeight(), this);
                }
            }

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
        };
        imagePanel.setPreferredSize(new Dimension(200,
                200));
        rootPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        getContentPane().setLayout(new BorderLayout());
        imagePanel.setOpaque(false);
        infoPanel.setOpaque(false);
        final JPanel imagebg = new JPanel();
        imagebg.setLayout(new BorderLayout());
        imagebg.setPreferredSize(new Dimension(150, 150));
        rootPanel.add(BorderLayout.WEST, imagePanel);
        rootPanel.add(BorderLayout.CENTER, infoPanel);
        getContentPane().add(BorderLayout.CENTER, rootPanel);
        infoPanel.add(createLocationPanel());
        infoPanel.add(createDescriptionPanel());
        infoPanel.add(createUploadComdPanel());
    }

    private JPanel createLocationPanel() {
        final JPanel locationPanel = new JPanel();
        locationPanel.setPreferredSize(new Dimension(430, 25));
        locationPanel.setOpaque(false);
        locationPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        locationPanel.add(provinceComboBox);
        locationPanel.add(cityComboBox);
        locationPanel.add(compoundComboBox);
        return locationPanel;
    }

    private JPanel createDescriptionPanel() {
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setOpaque(false);
        descriptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        descriptionPanel.setLayout(new BorderLayout());
        descriptionPanel.add(BorderLayout.NORTH, new JLabel("补充描述:"));
        descriptionPanel.add(BorderLayout.CENTER, new JScrollPane(description,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        description.setBorder(null);
        return descriptionPanel;
    }

    private JPanel createUploadComdPanel() {
        final JPanel uploadComdPanel = new JPanel();
        uploadComdPanel.setOpaque(false);
        uploadComdPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        uploadComdPanel.add(uploadBtn);
        return uploadComdPanel;
    }

    public void setPhoto(final BufferedImage photo) {
        image = photo;
        imagePanel.repaint();
    }

    private void setCity(LocationBean city) {
        if (city.getJurisdicLocations().size() == 0) {
            CompoundNetIO compoundNetIO = new CompoundNetIO(city);
            compoundNetIO.list(null);
        }
        compoundComboBox.removeAllItems();
        for (LocationBean compound : city.getJurisdicLocations()) {
            compoundComboBox.addItem(compound);
            setCompound((CompoundBean) compound);
        }
    }

    private void setCompound(final CompoundBean compound) {
        if (compound.getLayoutHouses().size() == 0) {
            CompoundLayoutNetIO compoundLayoutNetIO = new CompoundLayoutNetIO(compound);
            compoundLayoutNetIO.list(null);
        }

    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getSource() == provinceComboBox) {
                LocationBean province = (LocationBean) provinceComboBox.getSelectedItem();
                cityComboBox.removeAllItems();
                compoundComboBox.removeAllItems();
                for (LocationBean city : province.getJurisdicLocations()) {
                    cityComboBox.addItem(city);
                }
            } else if (e.getSource() == cityComboBox) {
                LocationBean city = (LocationBean) cityComboBox.getSelectedItem();
                if (city != null) {
                    setCity(city);
                }
            } else if (e.getSource() == compoundComboBox) {
                final CompoundBean compound = (CompoundBean) compoundComboBox.getSelectedItem();
                if (compound != null) {
                    setCompound(compound);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadBtn) {
            uploadHouse();
        }

    }


    public void setVisible(boolean visible) {
        if (visible) {
            if (!chechLogin()) {
                final Window window = SwingUtilities.getWindowAncestor(view);
                LoginDialog loginDialog = new LoginDialog(window, this);
                loginDialog.setVisible(true);
                return;
            }
        }
        super.setVisible(visible);
    }

    private boolean chechLogin() {
        return LoginDialog.USER.getCode() != null
                && LoginDialog.USER.getCode().length() != 0;
    }

    private void uploadHouse() {
        final House uploadHouse = view.getCurrentHouse();
        final HouseBean houseBean = view.getHouseBean();
        boolean newHouse = houseBean.getCode() == null;
        houseBean.setOwnerUserCode(LoginDialog.USER.getCode());
        final String compoundCode = compoundComboBox.getSelectedItem() == null ?
                "" : ((CoBean) compoundComboBox.getSelectedItem()).getCode();
        houseBean.setCompoundCode(compoundCode);
        houseBean.setDescription(description.getText());
        houseBean.setSneapView(image);
        houseBean.setHouseSource(FileUtil.objectToBytes(uploadHouse));
        final UserHouseBean userHouseBean = new UserHouseBean();
        userHouseBean.addHouse(houseBean);
        UserHouseNetIO userHouseNetIO = new UserHouseNetIO(userHouseBean);

        WaitingDialog.show(SwingUtilities.getWindowAncestor(view),
                userHouseNetIO, "正在上传......", "上传成功", "上传失败", this);
    }

    public void finishUp(UserBean bean) {
        TopPanel.setUserName(bean.getName());
        setVisible(true);
    }
}


