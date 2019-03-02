package cs.designer.swing.ui;

import cs.designer.swing.bean.CoBean;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/23/12
 * Time: 9:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class BeanLabel extends CoLable {
    private CoBean bean;

    public BeanLabel(final Color fontColor) {
        super(fontColor);
        setFont(new Font("", Font.PLAIN, 13));
    }

    public BeanLabel() {
        super();
    }

    public BeanLabel(final CoBean coBean) {
        super(coBean.getName());
        this.bean = coBean;
        setFont(new Font("", Font.PLAIN, 13));
    }

    public BeanLabel(final CoBean coBean, final Color fontColor) {
        super(coBean.getName(), fontColor);
        this.bean = coBean;
    }

    public CoBean getBean() {
        return bean;
    }

    public void setCoBean(final CoBean coBean) {
        this.bean = coBean;
        setText(coBean.getName());
    }
}