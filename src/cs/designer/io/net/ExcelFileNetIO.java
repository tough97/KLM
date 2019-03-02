package cs.designer.io.net;

import cs.designer.io.local.FileExtensionUtill;
import cs.designer.io.local.LocalFileManage;
import cs.designer.swing.bean.FileBean;
import cs.designer.view.viewer.HousePlanView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 10/26/12
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExcelFileNetIO extends ClientNetIO {
    public static final String SERVER_URL_PATTERNS = "/file";

    private FileBean fileBean;

    public ExcelFileNetIO(final FileBean fileBean) {
        this.fileBean = fileBean;
    }

    public void run() {
        if (!fileBean.getSourceFile().exists()) {
            final HttpPost post = new HttpPost(SERVER_HOST + SERVER_URL_PATTERNS);
            final MultipartEntity reqEntity = new MultipartEntity();
            try {
                reqEntity.addPart("pvkfr",
                        new StringBody("0"));
                reqEntity.addPart("pvkfp",
                        new StringBody(fileBean.getServerPath()));
                post.setEntity(reqEntity);
                final HttpResponse response = requestToServer(post);
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream in = entity.getContent();
                    if (in != null) {
                        final OutputStream os = new FileOutputStream(fileBean.getSourceFile());
                        int bytesRead = 0;
                        byte[] buffer = new byte[1024 * 2];
                        while ((bytesRead = in.read(buffer, 0, 1024 * 2)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        os.close();
                        in.close();
                    }

                }

            } catch (final UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileBean.getSourceFile().exists()) {
            saveFile();
        }

    }


    private void saveFile() {
        final JFileChooser filesaver = new JFileChooser();
        filesaver.setDialogTitle("保存...");
        filesaver.addChoosableFileFilter(new FileNameExtensionFilter(
                "Excel文件(*.xls;)", "xls"));
        int returnVal = filesaver.showSaveDialog(JOptionPane.getRootFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = filesaver.getSelectedFile();
            if (!selectedFile.getName().endsWith(FileExtensionUtill.
                    EXCEL_EXTENSION_NAME)) {
                selectedFile = new File(selectedFile.getPath() +
                        FileExtensionUtill.EXCEL_EXTENSION_NAME);
            }
            boolean sucess = true;
            if (!selectedFile.exists()) {
                copyFile(fileBean.getSourceFile(), selectedFile);
            } else {
                int userSelect = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                        "是否覆盖当前文件？",
                        "是否覆盖",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (userSelect == JOptionPane.OK_OPTION) {
                     copyFile(fileBean.getSourceFile(), selectedFile);
                }
            }
            String image = "保存成功";
            if (!sucess) {
                image = "保存失败";
            }
            JOptionPane.showMessageDialog(null, image, "",
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void copyFile(final File tempFile, final File sourceFile) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (tempFile.exists()) {
                InputStream inStream = new FileInputStream(tempFile);
                FileOutputStream fs = new FileOutputStream(sourceFile);
                byte[] buffer = new byte[1024 * 1024 * 20];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
