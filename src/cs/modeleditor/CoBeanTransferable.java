package cs.modeleditor;

import cs.designer.swing.bean.CoBean;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/10/12
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class CoBeanTransferable implements Transferable {
    private CoBean bean;

    public CoBeanTransferable(final CoBean bean) {
        this.bean =bean;
    }

    public DataFlavor[] getTransferDataFlavors() {

        DataFlavor[] flavors = new DataFlavor[1];
        Class type = bean.getClass();
        String mimeType = "application/x-java-jvm-local-objectref;class= " + type.getName();
        try {
            flavors[0] = new DataFlavor(mimeType);
            return flavors;
        } catch (ClassNotFoundException ex) {
            return new DataFlavor[0];
        }
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return "application".equals(flavor.getPrimaryType())
                && "x-java-jvm-local-objectref".equals(flavor.getSubType())
                && flavor.getRepresentationClass().isAssignableFrom(bean.getClass());
    }

    public CoBean getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        return bean;
    }
}