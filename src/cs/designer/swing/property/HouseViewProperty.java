package cs.designer.swing.property;

import cs.designer.swing.tool.HorizontalLayout;
import cs.designer.swing.tool.ViewButtonControls;
import cs.designer.swing.ui.SwingTool;
import cs.designer.view.controller.DisplayControlable;
import cs.designer.view.controller.ModelControler;
import cs.designer.view.controller.ViewControler;
import cs.designer.view.controller.VirtualViewController;
import cs.designer.view.viewer.DisplayView;
import cs.designer.view.viewer.HousePlanView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/18/12
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class HouseViewProperty extends Property implements ActionListener {
    private JRadioButton drawPlanCoomd;
    private JRadioButton obitCommd;
    private JRadioButton vortualCoomd;
    private ViewButtonControls viewButtonControls;
    private HousePlanView view;
    private JButton choseColorBtn;
    private JPanel colorPanel;


    public HouseViewProperty(final JPanel parentPanel) {
        super(parentPanel);
        drawPlanCoomd = new JRadioButton("平面户型");
        obitCommd = new JRadioButton("室外俯瞰");
        vortualCoomd = new JRadioButton("室内浏览");
        choseColorBtn = new JButton("");
        choseColorBtn.setOpaque(false);
        choseColorBtn.setBounds(20, 20, 100, 80);
        final ButtonGroup group = new ButtonGroup();
        setLayout(new FlowLayout(FlowLayout.CENTER));
        group.add(drawPlanCoomd);
        group.add(obitCommd);
        group.add(vortualCoomd);
        add(drawPlanCoomd);
        add(obitCommd);
        add(vortualCoomd);
        drawPlanCoomd.addActionListener(this);
        obitCommd.addActionListener(this);
        vortualCoomd.addActionListener(this);
        colorPanel = new JPanel();
        colorPanel.setBackground(DisplayView.DEFAULT_BACK_COLOR);
        colorPanel.setLayout(new HorizontalLayout());
        colorPanel.setPreferredSize(new Dimension(20, 20));
        colorPanel.add(choseColorBtn);
        choseColorBtn.addActionListener(this);
        add(colorPanel);
        viewButtonControls = new ViewButtonControls();
        add(viewButtonControls);

        viewButtonControls.setBackground(SwingTool.THEME_COLOR);

    }

    @Override
    public void clear() {

    }

    @Override
    public void setPerty(final Object object) {
        if (object instanceof HousePlanView) {
            this.view = (HousePlanView) object;
            if (view.getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
                drawPlanCoomd.setSelected(true);
                colorPanel.setBackground(view.getPlanControler().getBaseFace().getBackColor());
            } else if (view.getViewType() == DisplayView.ViewType.ORBIT_VIEW) {
                obitCommd.setSelected(true);
                colorPanel.setBackground(view.getBackGroundColor());
            }
            if (view.getViewType() == DisplayView.ViewType.VIRTUAL_VIEW) {
                vortualCoomd.setSelected(true);
                colorPanel.setBackground(view.getBackGroundColor());
            }
        }

    }

    @Override
    public void setModifyControler(final DisplayControlable controler) {
        if (controler instanceof ViewControler) {
            viewButtonControls.setControlor((ViewControler) controler);
        } else if (controler instanceof VirtualViewController) {
            viewButtonControls.setControlor((VirtualViewController) controler);
        } else if (controler instanceof ModelControler) {
            viewButtonControls.setControlor((ModelControler) controler);
        }


    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (view != null) {
            if (source == drawPlanCoomd) {
                view.getPlanViewControler().drawPlanView();
            } else if (source == obitCommd) {
                view.getPlanViewControler().orbitView();
            } else if (source == vortualCoomd) {
                view.getPlanViewControler().virtuallView();
            } else if (source == choseColorBtn) {
                Window window = SwingUtilities.getWindowAncestor(view);
                Color color = JColorChooser.showDialog(
                        window, "背景颜色", colorPanel.getBackground());
                if (color != null) {
                    if (view.getViewType() == DisplayView.ViewType.DRAW_PLANVIEW) {
                        view.getPlanControler().getBaseFace().setBackColor(color);
                    } else {
                        view.setBackGroupColor(color);
                    }
                    colorPanel.setBackground(color);
                    colorPanel.updateUI();
                }
            }
        }

    }

}
