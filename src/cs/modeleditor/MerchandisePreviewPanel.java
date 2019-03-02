package cs.modeleditor;

import cs.designer.swing.bean.MerchandiseBean;

import javax.swing.*;
import java.awt.*;

/**
* Created by IntelliJ IDEA.
* User: rongyang
* Date: 4/2/12
* Time: 2:43 PM
* To change this template use File | Settings | File Templates.
*/
public class MerchandisePreviewPanel extends JPanel {
    private MerchandiseBean merchandiseBean;

    public MerchandisePreviewPanel() {
        super();
        setLayout(new FlowLayout(FlowLayout.LEADING, 20, 20));

    }

    public MerchandisePreviewPanel(final MerchandiseBean merchandiseBean) {
        this();
        setMerchandiseBean(merchandiseBean);
    }

    protected void setMerchandiseBean(MerchandiseBean merchandiseBean) {
        removeAll();
        this.merchandiseBean = merchandiseBean;
        final CoCheckBox coCheckBox = new CoCheckBox(merchandiseBean);
        add(coCheckBox);
        updateUI();
    }

    public MerchandiseBean getMerchandiseBean() {
        return merchandiseBean;
    }
}