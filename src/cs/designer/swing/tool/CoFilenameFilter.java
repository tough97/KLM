package cs.designer.swing.tool;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/5/12
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoFilenameFilter implements FilenameFilter {
    private String[] types;

    public CoFilenameFilter(final String[] types) {
        this.types = types;

    }

    public boolean accept(File parent, String filePath) {
        for (String type : types) {
            String fileName = new File(filePath).getName();
            if (getExtensionName(fileName).endsWith(type)) {
                return true;
            }
        }
        return false;
    }
    private String getExtensionName(final String fileName) {
        final String extensionName = fileName.substring(fileName.lastIndexOf("."));
        return extensionName;
    }

}
