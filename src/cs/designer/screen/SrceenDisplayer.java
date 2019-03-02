package cs.designer.screen;

import cs.designer.screen.impi.*;
import cs.designer.swing.TopPanel;
import cs.designer.swing.ui.AuthorizPhoneDialog;
import cs.designer.swing.ui.LoginDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 8/28/12
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SrceenDisplayer extends JPanel {
    public static final String SEARCH_PLANE = "searchPlan";
    public static final String DESIGNER = "designer";
    public static final String PERSONAL_CENTER = "personalCenter";
    public static final String HELP_CENTER = "helpCenter";
    public static final String KNOWLEDGE_CENTER = "knowledgeCenter";
    public static final String INDEX = "index";
    public static final String RENOVATION = "renovation";
    private KLMScreen searchPlanScreen;
    private KLMScreen designerScreen;
    private KLMScreen helpScreen;
    private KLMScreen personalScreen;
    private KLMScreen knowledgelScreen;
    private KLMScreen indexScreen;
    private KLMScreen renovationScreen;
    private String currentScreen;

    public SrceenDisplayer() {
        searchPlanScreen = new SearchPlanScreen(this);
        designerScreen = new DesignerScreen(this);
        helpScreen = new HelpScreen(this);
        personalScreen = new PersonalScreen(this);
        knowledgelScreen = new KnowledgeScreen(this);
        indexScreen = new IndexScreen(this);
        renovationScreen = new RenovationScreen(this);

    }

    public synchronized void displaySrceen(final String screenCommand) {
        if (screenCommand != currentScreen) {
            if (SEARCH_PLANE == screenCommand) {
                searchPlanScreen.display();
                selectScreen(screenCommand);
            } else if (DESIGNER == screenCommand) {
                designerScreen.display();
                selectScreen(screenCommand);
            } else if (HELP_CENTER == screenCommand) {
                helpScreen.display();
                selectScreen(screenCommand);
            } else if (PERSONAL_CENTER == screenCommand) {
                if (LoginDialog.USER.getCode() != null && LoginDialog.USER.getCode().length() != 0) {
                    ((PersonalScreen) personalScreen).setUserId(LoginDialog.USER.getCode());
                    personalScreen.display();
                    selectScreen(screenCommand);
                } else {
                    final Window window = SwingUtilities.getWindowAncestor(this);
                    final LoginDialog loginDialog = new LoginDialog(window, (PersonalScreen) personalScreen);
                    loginDialog.setVisible(true);
                }
            } else if (KNOWLEDGE_CENTER == screenCommand) {
                knowledgelScreen.display();
                selectScreen(screenCommand);
            } else if (INDEX == screenCommand) {
                indexScreen.display();
                selectScreen(screenCommand);
            } else if (RENOVATION == screenCommand) {
//                if (LoginDialog.USER.getCode() != null && LoginDialog.USER.getCode().length() != 0) {
//                    renovationScreen.display();
//                    selectScreen(screenCommand);
//                } else {
//                    final Window window = SwingUtilities.getWindowAncestor(this);
//                    final LoginDialog loginDialog = new LoginDialog(window, (RenovationScreen) renovationScreen);
//                    loginDialog.setVisible(true);
//                }
                renovationScreen.display();
                selectScreen(screenCommand);

            }

        }

    }

    public DesignerScreen getDesignerScreen() {
        return (DesignerScreen) designerScreen;
    }

    private void selectScreen(String screenCommand) {
        this.currentScreen = screenCommand;
        TopPanel.setSelectButton(screenCommand);
    }
}
