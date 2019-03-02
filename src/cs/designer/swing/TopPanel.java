package cs.designer.swing;

import cs.designer.main.KLMDesktop;
import cs.designer.screen.CoBorwser;
import cs.designer.screen.SrceenDisplayer;
import cs.designer.swing.bean.UserBean;
import cs.designer.swing.icons.IconManager;
import cs.designer.swing.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 8/28/12
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TopPanel extends JPanel implements ActionListener, LoginTask {
    public static JButton[] commandBtns;
    private static CoLable loginCommand;
    private static CoLable registerCommand;
    private static CoLable userName;
    private static CoLable opinionCommand;

    /**
     * Modified date : 2013 May 28
     * Modified by : Gang Liu
     * Modifiy reason : Pop up link needed to provide house measurement
     * @param listener
     */
    private static CoLable houseMeasureCommand;

    public TopPanel(final ActionListener listener) {
        setPreferredSize(new Dimension(0, 70));
        commandBtns = new JButton[7];
        setBackground(new Color(85, 85, 85));
        init(listener);
    }

    private void init(final ActionListener listener) {
        setLayout(new BorderLayout());
        add(BorderLayout.WEST, createToolBarPanel(listener));
        add(BorderLayout.CENTER, createUserInfoPanel());

    }


    private JPanel createUserInfoPanel() {
        final JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        final Color foregroundColor = new Color(207, 166, 19);
        loginCommand = new CoLable("登录", foregroundColor);
        registerCommand = new CoLable("注册", foregroundColor);
        opinionCommand = new CoLable("提意见", foregroundColor);
        userName = new CoLable();
        userName.setFontColor(foregroundColor);
        //

        loginCommand.setFont(new Font("", Font.BOLD, 12));
        registerCommand.setFont(new Font("", Font.BOLD, 12));
        opinionCommand.setFont(new Font("", Font.BOLD, 12));
        userName.setFont(new Font("", Font.BOLD, 12));
        //
        loginCommand.addActionListener(this);
        registerCommand.addActionListener(this);
        opinionCommand.addActionListener(this);
        userName.addActionListener(this);
        userInfoPanel.add(userName);
        userInfoPanel.add(loginCommand);
        userInfoPanel.add(registerCommand);
        userInfoPanel.add(opinionCommand);

        /**
         * Modified date : 2013 May 28
         * Modified by : Gang Liu
         * Modifiy reason : Pop up link needed to provide house measurement
         * @param listener
         */
        houseMeasureCommand = new CoLable("免费量房", foregroundColor);
        houseMeasureCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)){
                    try {
                        desktop.browse(new java.net.URL("http://www.kaolamao.com/house_measure.jsp").toURI());
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        userInfoPanel.add(houseMeasureCommand);

        return userInfoPanel;
    }

    private JPanel createToolBarPanel(ActionListener listener) {
        final JPanel toolBarPanel = new JPanel();
        toolBarPanel.setOpaque(false);
        toolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 10));
        final JButton seachPlanBtn = new CoButton(IconManager.getSelectedIcons("seachplan"));
        final JButton designerBtn = new CoButton(IconManager.getSelectedIcons("designer"));
        final JButton knowledgeBtn = new CoButton(IconManager.getSelectedIcons("knowledge"));
        final JButton personalBtn = new CoButton(IconManager.getSelectedIcons("personal"));
        final JButton helpBtn = new CoButton(IconManager.getSelectedIcons("help"));
        final JButton renovationBtn = new CoButton(IconManager.getSelectedIcons("renovation"));
        final JButton homeBtn = new CoButton(IconManager.getSelectedIcons("home"));
        seachPlanBtn.addActionListener(listener);
        seachPlanBtn.setActionCommand(SrceenDisplayer.SEARCH_PLANE);
        designerBtn.addActionListener(listener);
        designerBtn.setActionCommand(SrceenDisplayer.DESIGNER);
        knowledgeBtn.addActionListener(listener);
        knowledgeBtn.setActionCommand(SrceenDisplayer.KNOWLEDGE_CENTER);
        personalBtn.addActionListener(listener);
        personalBtn.setActionCommand(SrceenDisplayer.PERSONAL_CENTER);
        helpBtn.addActionListener(listener);
        helpBtn.setActionCommand(SrceenDisplayer.HELP_CENTER);
        homeBtn.addActionListener(listener);
        homeBtn.setActionCommand(SrceenDisplayer.INDEX);
        renovationBtn.addActionListener(listener);
        renovationBtn.setActionCommand(SrceenDisplayer.RENOVATION);
        toolBarPanel.add(homeBtn);
        toolBarPanel.add(seachPlanBtn);
        toolBarPanel.add(designerBtn);
        toolBarPanel.add(personalBtn);
        toolBarPanel.add(renovationBtn);
        toolBarPanel.add(knowledgeBtn);
        toolBarPanel.add(helpBtn);
        commandBtns[0] = seachPlanBtn;
        commandBtns[1] = designerBtn;
        commandBtns[2] = personalBtn;
        commandBtns[3] = renovationBtn;
        commandBtns[4] = knowledgeBtn;
        commandBtns[5] = helpBtn;
        commandBtns[6] = homeBtn;
        return toolBarPanel;
    }


    public static void setSelectButton(final String command) {
        for (int index = 0; index < commandBtns.length; index++) {
            commandBtns[index].setSelected(commandBtns[index].
                    getActionCommand().endsWith(command));
        }

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginCommand) {
            if (loginCommand.getText().endsWith("登录")) {
                final Window window = SwingUtilities.getWindowAncestor(this);
                final LoginDialog loginDialog = new LoginDialog(window, this);
                loginDialog.setVisible(true);
            } else if (loginCommand.getText().endsWith("退出")) {
                loginCommand.setText("登录");
                userName.setText("");
                LoginDialog.USER.reset();
            }
        } else if (e.getSource() == registerCommand) {
            final Window window = SwingUtilities.getWindowAncestor(this);
            final JDialog registerDialog = new RegisterDialog(window, this);
            registerDialog.setVisible(true);

        } else if (e.getSource() == userName) {

        } else if (e.getSource() == opinionCommand) {
            final Window window = SwingUtilities.getWindowAncestor(this);
            CommentDialog.show(window);
        }
    }

    public void finishUp(UserBean bean) {
        setUserName(bean.getName());
    }

    public static void setUserName(final String name) {
        userName.setText(name);
        loginCommand.setText("退出");
    }
}
