package com.klm.persist.impl;

import com.klm.cons.impl.CSHouseException;
import com.klm.cons.impl.House;
import com.klm.cons.impl.Surface3D;
import com.klm.persist.CSPersistException;
import com.klm.persist.Merchandise;
import cs.designer.io.net.MerchandiseSourceNetIO;
import cs.designer.swing.bean.MerchandiseBean;
import org.apache.log4j.*;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 12/13/11
 * Time: 8:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class LocalStorage {

    public static final String SURFACE_MATERIAL_EXT = ".sm";
    public static final String FURNITURE_EXT = ".fn";
    public static final String FILE_NAME_CONNECTOR = "-";
    public static final long MAXIMUM_FILE_SIZE = 10 * 1024 * 1024;
    public static final long MAXIMIN_LOG_FILE_SIZE = 1 * 1024 * 1024;

    public static final String KLM_STORAGE_DIR = "klm";
    public static final String[] SUB_DIRECTORIES = {"sum", "fun", "dl", "tmp"};
    public static final int SURFACE_MATERIAL_DIR = 0;
    public static final int FURNITURE_DIR = 1;
    public static final int DEBUG_LOG_DIR = 2;
    public static final int TEMPRORY_DIR = 3;

    public static final int ENTITY_MERCHANDISE = 1;
    public static final int SURFACE_MERCHANDISE = 2;

    private static LocalStorage localStorage = null;

    public static LocalStorage getLocalStorage() throws CSHouseException {
        if (localStorage == null) {
            localStorage = new LocalStorage();
        }
        return localStorage;
    }

    private File klmLocalStorage;
    private File[] subDirectories = new File[SUB_DIRECTORIES.length];
    private MessageDigest encryptor;
    private Map<String, SurfaceMaterial> surfaceMaterialMap = new HashMap<String, SurfaceMaterial>();

    /*

     */
    private LocalStorage() throws CSHouseException {
        klmLocalStorage = new File(System.getProperty("java.io.tmpdir"), KLM_STORAGE_DIR);
        klmLocalStorage.mkdirs();
        for (int index = 0; index < SUB_DIRECTORIES.length; index++) {
            subDirectories[index] = new File(klmLocalStorage, SUB_DIRECTORIES[index]);
            subDirectories[index].mkdirs();
        }
        configLoger();
        try {
            encryptor = MessageDigest.getInstance("MD5");
            System.out.println(klmLocalStorage.getCanonicalPath());
        } catch (Exception ex) {
            Logger.getLogger(LocalStorage.class).error("Error when initiating local storage : \n", ex);
        }
    }

    /**
     * @param index values in range of SURFACE_MATERIAL_DIR, FURNITURE_DIR, DEBUG_LOG_DIR or TEMPRORY_DIR
     * @return sub file directory of KLM sub-directory
     * @throws CSHouseException
     */
    public File getSubDir(final int index) throws CSPersistException {
        if (index < 0 || index >= SUB_DIRECTORIES.length) {
            throw new CSPersistException(new IllegalArgumentException("Directory indicator is out of range"));
        }
        return subDirectories[index];
    }

    /**
     * This method only provide mean of Merchandise persistence for local cache, it has absolutely nothing to do with
     * uploading which means this merchandise is definitely exist on remote Server
     *
     * @param merchandise
     */
    public File writeMerchandize(final Merchandise merchandise) throws CSPersistException {
        String fExt = null;
        File parentDir = null;

        if (merchandise instanceof SurfaceMaterial) {
            parentDir = getSubDir(SURFACE_MATERIAL_DIR);
            fExt = SURFACE_MATERIAL_EXT;
        } else if (merchandise instanceof Furniture) {
            parentDir = getSubDir(FURNITURE_DIR);
            fExt = FURNITURE_EXT;
        } else {
            throw new CSPersistException(new IllegalArgumentException("Can not categorize merchandise " + merchandise.toString()));
        }

        final File mercgandizeFile = new File(parentDir, encryptID(merchandise.getId()) + fExt);
        if (mercgandizeFile.exists()) {
            return mercgandizeFile;
        }

        if (merchandise == null || !(merchandise instanceof Serializable)) {
            throw new CSPersistException("Can not serilize given Object " + merchandise);
        }
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mercgandizeFile));
            oos.writeObject(merchandise);
            oos.flush();
            oos.close();
        } catch (Exception ex) {
            throw new CSPersistException(ex);
        }
        return mercgandizeFile;
    }

    public Merchandise readMerchandise(final String id) throws CSPersistException {
        final Merchandise ret = readMerchandise(id, SurfaceMaterial.class);
        if (ret != null) {
            return ret;
        } else {
            return readMerchandise(id, Furniture.class);
        }
    }


    public Merchandise readMerchandise(final String id, final Class target) throws CSPersistException {
        String fExt;
        File parentDir;
        if (target.getCanonicalName().equals(SurfaceMaterial.class.getCanonicalName())) {
            parentDir = getSubDir(SURFACE_MATERIAL_DIR);
            fExt = SURFACE_MATERIAL_EXT;
        } else if (target.getCanonicalName().equals(Furniture.class.getCanonicalName())) {
            parentDir = getSubDir(FURNITURE_DIR);
            fExt = FURNITURE_EXT;
        } else {
            throw new CSPersistException("Can not identify merchandise type " + target);
        }
        final String fName = encryptID(id) + fExt;
        try {
            for (final File cachedMerchandises : parentDir.listFiles()) {
                if (cachedMerchandises.getName().equals(fName)) {
                    final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cachedMerchandises));
                    final Merchandise ret = (Merchandise) ois.readObject();
                    ois.close();
                    return ret;
                }
            }
        } catch (Exception ex) {
            throw new CSPersistException(ex);
        }

        //todo :a MerchandiseDownloading Thread should be started here download remote Merchandise, return
        return null;
    }

    public synchronized Merchandise readMerchandise(final String id, final Class target,
                                       final Surface3D parentSurface) throws CSPersistException {
        if (Merchandise.merchandises.containsKey(id)) {
            return Merchandise.merchandises.get(id).clone();
        }
        Merchandise readMerchandise = readMerchandise(id, target);
        if (readMerchandise == null) {
            final MerchandiseBean merchandiseBean = new MerchandiseBean();
            merchandiseBean.setId(id);
            if (target.getCanonicalName().equals(SurfaceMaterial.class.getCanonicalName())) {
                merchandiseBean.setType(MerchandiseBean.MerchandiseType.surfaceMaterial);
            } else if (target.getCanonicalName().equals(Furniture.class.getCanonicalName())) {
                merchandiseBean.setType(MerchandiseBean.MerchandiseType.furniture);
            }
            readMerchandise = merchandiseBean.createObject();
            Thread downloadThread = new Thread(new MerchandiseSourceNetIO(readMerchandise, parentSurface));
            downloadThread.start();
        }else{
           Merchandise.merchandises.put(id,readMerchandise);
        }


        return readMerchandise;
    }

    public long getFileSize() {
        long ret = 0;
        for (final File subDir : subDirectories) {
            for (final File subDirFile : subDir.listFiles()) {
                ret += subDirFile.length();
            }
        }
        return ret;
    }

    public File writeDebugObject(final String fName, final Serializable object) throws CSHouseException {
        try {
            final File targetFile = new File(subDirectories[DEBUG_LOG_DIR], fName);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(targetFile));
            oos.writeObject(object);
            oos.close();
            return targetFile;
        } catch (Exception ex) {
            throw new CSHouseException(ex);
        }
    }

    public Object readDebugObject(final String fName) throws CSHouseException {
        try {
            final ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(new File(subDirectories[DEBUG_LOG_DIR], fName)));
            final Object ret = ois.readObject();
            ois.close();
            return ret;
        } catch (Exception ex) {
            throw new CSHouseException(ex);
        }
    }

    public File getDebugFile(final String fName) {
        final File ret = new File(subDirectories[DEBUG_LOG_DIR], fName);
        return ret.exists() ? ret : null;
    }

    public void logSystemMemoryUsage(final boolean print, final String source) {
        final int mb = 1024 * 1024;
        final StringBuilder message = new StringBuilder(source).append("\nTotal Memory: ").append(String.valueOf(Runtime.getRuntime().totalMemory() * 1.0 / mb)).append("M\n")
                .append("Free Memory: ").append(String.valueOf(Runtime.getRuntime().freeMemory() * 1.0 / mb)).append("M\n").append("Memory used: ")
                .append(String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() * 1.0) / mb));

        Logger.getLogger(LocalStorage.class.getName() + ".logSystemMemoryUsage").debug(message.toString());
        if (print) {
            System.out.println(message.toString());
        }
    }

    //Private Methods---------------------------------------------------------------------------------------------------
    private String encryptID(final String merchandizeID) {
        encryptor.update(merchandizeID.getBytes(), 0, merchandizeID.length());
        final String ret = new BigInteger(1, encryptor.digest()).toString(16);
        encryptor.reset();
        return ret;
    }

    private void configLoger() {
        try {
            final RollingFileAppender fileAppender = new RollingFileAppender();
            fileAppender.setFile(new File(subDirectories[LocalStorage.DEBUG_LOG_DIR], "lg").getCanonicalPath());
            fileAppender.setLayout(new PatternLayout("%-6r [%15.15t] %-5p %30.30c %x - %m%n"));
            fileAppender.setMaximumFileSize(MAXIMIN_LOG_FILE_SIZE);
            fileAppender.activateOptions();

            final Logger root = Logger.getRootLogger();
            root.setLevel(Level.ALL);
            root.addAppender(fileAppender);
        } catch (Exception ex) {
            Logger.getLogger(LocalStorage.class).error("Error when create Logger : ", ex);
        }
    }

    //Testing Methods---------------------------------------------------------------------------------------------------
    public static void main(String[] args) throws CSHouseException {
        final LocalStorage ls = LocalStorage.getLocalStorage();
    }

}
