package cs.designer.io.local;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.Floor;
import com.klm.cons.impl.House;
import com.klm.cons.impl.Room;
import com.klm.persist.CSPersistException;
import com.klm.persist.Merchandise;
import com.klm.persist.impl.LocalStorage;
import com.klm.util.impl.MerchandiseInfo;
import cs.designer.swing.ui.WaitingDialog;
import cs.designer.view.viewer.HousePlanView;

import javax.swing.*;
import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/24/12
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalFileManage {
    public static boolean outPutLocalFile(final House house,
                                          final File localFile) {
        boolean sucess = true;
        final Set<File> localFiles = new HashSet<File>();
        for (final Floor floor : house.getFloors()) {
            try {
                final Map<Room, MerchandiseInfo> roomMerchandiseInfoMap = floor.getFloorMerchandiseInfo();
                for (final Room room : roomMerchandiseInfoMap.keySet()) {
                    for (final Merchandise merchandise : roomMerchandiseInfoMap.get(room).getMerchandiseSet()) {
                        try {
                            final File merchandiseFile = LocalStorage.getLocalStorage().writeMerchandize(merchandise);
                            localFiles.add(merchandiseFile);
                        } catch (CSPersistException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (CSHouseException e) {
                e.printStackTrace();
                sucess = false;
            }
        }
        try {
            final File houseTempFile = new File(LocalStorage.getLocalStorage().getSubDir(LocalStorage.TEMPRORY_DIR),
                    house.hashCode() + FileExtensionUtill.HOUSE_EXTEBSUIB_NAME);
            final HouseSaveLoader saveLoader = new HouseSaveLoader(houseTempFile);
            saveLoader.setObject(house);
            localFiles.add(houseTempFile);
            //
            createZipFile(localFile, localFiles);
        } catch (Exception e) {
            e.printStackTrace();
            sucess = false;
        }  finally {
            return sucess;
        }
    }

    public static void importLocalFile(File localFile, final HousePlanView view) {
        final long start = System.currentTimeMillis();
        House loadHouse = null;
        if (localFile != null) {
            File houseFile = dispositionLocalFile(localFile);
            if (houseFile != null) {
                final HouseSaveLoader saveLoader = new HouseSaveLoader(houseFile, view);
                WaitingDialog.show(SwingUtilities.getWindowAncestor(view),
                        saveLoader, "考拉猫正在努力为您加载......", "加载成功", "加载失败");

            }
        }

    }

    public static void createZipFile(File rootFile, Set<File> childFile) {
        ZipOutputStream zipOut = null;
        try {
            zipOut = new ZipOutputStream(new FileOutputStream(rootFile));
            for (File tempFile : childFile) {
                if (tempFile.isFile()) {
                    InputStream tempIn = null;
                    try {
                        zipOut.putNextEntry(new ZipEntry(tempFile.getName()));
                        tempIn = new FileInputStream(tempFile);
                        byte[] buffer = new byte[1024 * 1024 * 20];
                        int size;
                        while ((size = tempIn.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, size);
                        }
                        zipOut.closeEntry();
                    } finally {
                        if (tempIn != null) {
                            tempIn.close();
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipOut != null) {
                try {
                    zipOut.finish();
                    zipOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static File dispositionLocalFile(File localFile) {
        ZipInputStream zipIn = null;
        File houseFile = null;
        try {
            zipIn = new ZipInputStream(new FileInputStream(localFile));
            for (ZipEntry entry; (entry = zipIn.getNextEntry()) != null;) {
                String entryName = entry.getName();
                File merchandiseFile = null;
                if (entryName.endsWith(LocalStorage.SURFACE_MATERIAL_EXT)) {
                    merchandiseFile = new File(LocalStorage.getLocalStorage().
                            getSubDir(LocalStorage.SURFACE_MATERIAL_DIR), entryName);
                } else if (entryName.endsWith(LocalStorage.FURNITURE_EXT)) {
                    merchandiseFile = new File(LocalStorage.getLocalStorage().
                            getSubDir(LocalStorage.FURNITURE_DIR), entryName);
                } else if (entryName.endsWith(FileExtensionUtill.HOUSE_EXTEBSUIB_NAME)) {
                    merchandiseFile = new File(LocalStorage.getLocalStorage().
                            getSubDir(LocalStorage.TEMPRORY_DIR), entryName);
                    houseFile = merchandiseFile;
                }
                if (!merchandiseFile.exists()) {
                    FileOutputStream outPut = new FileOutputStream(merchandiseFile);
                    byte[] buffer = new byte[1024 * 1024 * 20];
                    int readed = zipIn.read(buffer);
                    while (readed > 0) {
                        outPut.write(buffer, 0, readed);
                        readed = zipIn.read(buffer);
                    }
                    outPut.flush();
                    outPut.close();
                }
            }
            zipIn.closeEntry();

        } catch (IOException e) {
        } catch (CSPersistException e) {
            e.printStackTrace();
        } catch (CSHouseException e) {
            e.printStackTrace();
        } finally {
            if (zipIn != null) {
                try {
                    zipIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return houseFile;
    }

}
