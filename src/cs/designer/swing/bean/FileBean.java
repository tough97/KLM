package cs.designer.swing.bean;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/5/12
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileBean implements CoBean {
    private File sourceFile;
    private String serverPath;

    public FileBean(final File file) {
        this.sourceFile = file;

    }

    public String createDescription() {
        return sourceFile.getName();
    }

    public String getCode() {
        return null;
    }

    public String getName() {
        return sourceFile.getName();
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
}
