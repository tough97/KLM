package cs.designer.swing.ui;

import cs.designer.io.local.HouseSaveLoader;
import cs.designer.io.net.ClientNetIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/12/12
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class WaitingDialog extends JDialog implements ActionListener {
    private static final String DEFAULT_STATUS = "加载中......";
    private static final String DEFAULT_ERRO = "错误";
    private JProgressBar progressBar;
    //    private JLabel lbStatus;
    private JButton cancelbtn;
    private Window parent;
    private Thread thread;
    private String statusInfo;
    private String resultInfo;
    private String erroInfo;
    private JDialog closedDialog;

    public static void show(final Window parent, final ClientNetIO thread) {
        new WaitingDialog(parent, thread, DEFAULT_STATUS, null, null);
    }

    public static void show(final Window parent, final Thread thread,
                            final String statusInfo, final String resultInfo,
                            final String erroInfo) {

        new WaitingDialog(parent, thread, statusInfo, resultInfo, erroInfo);
    }

    public static void show(final Window parent, final Thread thread,
                            final String statusInfo, final String resultInfo,
                            final String erroInfo, JDialog closedDialog) {

        new WaitingDialog(parent, thread, statusInfo, resultInfo, erroInfo, closedDialog);
    }


    private WaitingDialog(final Window parent, final Thread thread, final String
            statusInfo, final String resultInfo, final String erroInfo) {
        super(parent);
        this.parent = parent;
        this.thread = thread;
        this.statusInfo = statusInfo;
        this.resultInfo = resultInfo;
        this.erroInfo = erroInfo;
        initUI();
        startThread();
        setModal(true);
        setVisible(true);
    }

    private WaitingDialog(final Window parent, final Thread thread, final String
            statusInfo, final String resultInfo, final String erroInfo,
                          final JDialog colosedDialog) {
        super(parent);
        this.parent = parent;
        this.thread = thread;
        this.statusInfo = statusInfo;
        this.resultInfo = resultInfo;
        this.erroInfo = erroInfo;
        this.closedDialog = colosedDialog;
        initUI();
        startThread();
        setModal(true);
        setVisible(true);
    }


    private void initUI() {
        final JPanel rootpPanel = new JPanel(null);
        progressBar = new JProgressBar();
        cancelbtn = new JButton("取消");
        progressBar.setValue(0);
        progressBar.setString(statusInfo);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        cancelbtn.addActionListener(this);

        rootpPanel.add(progressBar);
        rootpPanel.add(cancelbtn);

        getContentPane().add(rootpPanel);
        setUndecorated(true);
        setResizable(true);
        setSize(390, 100);
        int locationX = parent.getLocation().x + (parent.getWidth()
                - getWidth()) / 2;
        int locationY = parent.getLocation().y + (parent.getHeight()
                - getHeight()) / 2;

        setLocation(locationX, locationY);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        rootpPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                layout(rootpPanel.getWidth(), rootpPanel.getHeight());
            }
        });
    }

    private void startThread() {
        new Thread() {
            public void run() {
                try {
                    thread.start();
                    thread.join();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parent, erroInfo, "",
                            JOptionPane.INFORMATION_MESSAGE);
                } finally {
                    dispose();
                    if (thread instanceof ClientNetIO) {
                        ClientNetIO.StatusCode statusCode = ((ClientNetIO) thread).getStatusCode();
                        switch (statusCode) {
                            case normal:
                                JOptionPane.showMessageDialog(parent, resultInfo, "",
                                        JOptionPane.INFORMATION_MESSAGE);
                                if (closedDialog != null) {
                                    closedDialog.setVisible(false);
                                }
                                break;
                            case notFind:
                                JOptionPane.showMessageDialog(parent, erroInfo, "",
                                        JOptionPane.INFORMATION_MESSAGE);
                                break;
                            case netError:
                                JOptionPane.showMessageDialog(parent, erroInfo, "",
                                        JOptionPane.INFORMATION_MESSAGE);
                                break;
                            case versionError:
                                JOptionPane.showMessageDialog(parent, erroInfo, "",
                                        JOptionPane.INFORMATION_MESSAGE);
                            default:
                                JOptionPane.showMessageDialog(parent, erroInfo, "",
                                        JOptionPane.INFORMATION_MESSAGE);
                                break;

                        }


                    } else if (thread instanceof HouseSaveLoader) {
                        HouseSaveLoader saveLoader = (HouseSaveLoader) thread;
                        if (saveLoader.isSucessLoad()) {
                            JOptionPane.showMessageDialog(parent, resultInfo, "",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(parent, erroInfo, "",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }

                    }


                }
            }
        }.start();
    }

    private void layout(int width, int height) {
        progressBar.setBounds(20, 20, 350, 15);
        cancelbtn.setBounds(width - 85, height - 31, 75, 21);
    }

    @SuppressWarnings("deprecation")
    public void actionPerformed(ActionEvent e) {
        resultInfo = "";
        thread.stop();

    }
}