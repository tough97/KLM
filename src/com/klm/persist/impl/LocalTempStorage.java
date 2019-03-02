package com.klm.persist.impl;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 11/15/11
 * Time: 5:55 PM
 * To change this template use File | Settings | File Templates.
 */

import com.klm.persist.CSPersistException;
import com.klm.persist.Merchandise;

import java.io.*;

/**
 * @author gang-liu
 */
public class LocalTempStorage {

    public static final String HOUSE_PLANE_EXT = "HPF";

    public static final String KLM_TEMP_DIR = "klm_old";
    public static final String KLM_HOUSE_DIR = "hp";
    public static final String KLM_FURNITURE_EXT_DIR = "fn";
    public static final String KLM_SURFACE_MATERIAL_DIR = "sm";
    public static final String KLM_DEBUG_DIR = "dg";

    public static final String MERCHANDIZE_NAME_SEPARATOR="-";
    public static final String SURFACE_MATERIAL_EXT = "msm";
    public static final String FURNITURE_EXT = "mfn";
    public static final String NULL_MERCHANDIZE_INDI = "@nm";

    public static final long DEFAULT_MAX_SIZE = 30 * 1024 * 1024;

    private String systemTmpDir;
    private File klmTempDir;
    private File housePlaneDir;
    private File furnitureDir;
    private File surfaceMatrialDir;
    private File debugDir;
    private static LocalTempStorage klmTmpStorage;

    private LocalTempStorage() {
        systemTmpDir = System.getProperty("java.io.tmpdir");
        klmTempDir = new File(systemTmpDir, KLM_TEMP_DIR);
        klmTempDir.mkdirs();
        housePlaneDir = new File(klmTempDir, KLM_HOUSE_DIR);
        housePlaneDir.mkdirs();
        furnitureDir = new File(klmTempDir, KLM_FURNITURE_EXT_DIR);
        furnitureDir.mkdirs();
        surfaceMatrialDir = new File(klmTempDir, KLM_SURFACE_MATERIAL_DIR);
        surfaceMatrialDir.mkdirs();
        debugDir = new File(klmTempDir, KLM_DEBUG_DIR);
        debugDir.mkdirs();
    }

    public static LocalTempStorage getLocalTempStorage() {
        if (klmTmpStorage == null) {
            klmTmpStorage = new LocalTempStorage();
        }
        return klmTmpStorage;
    }

    public File getKLMDir() {
        return klmTempDir;
    }

    public File getHouseDir() {
        return housePlaneDir;
    }

    public File getFurnitureDir() {
        return furnitureDir;
    }

    public File getSurfaceMaterialDir() {
        return surfaceMatrialDir;
    }

    public long getSize() {
        return getFileSize(klmTempDir);
    }

    public void writeSurfaceMaterialToStorage(final SurfaceMaterial sm) throws CSPersistException {
        final File targetFile = new File(getSurfaceMaterialDir(), sm.getFileName());
        writeObjectToFile(targetFile, sm);
    }

    public SurfaceMaterial readSurfaceMaterialFromStorage(final String merchandiseID) throws CSPersistException {
        final File targetFile = lookupSurfaceMaterial(merchandiseID);
        if (targetFile == null) {
            throw new CSPersistException("Can not find material informationa about item " + merchandiseID);
        }
        final SurfaceMaterial ret = (SurfaceMaterial) readObjectFromFile(targetFile, SurfaceMaterial.class);
        return ret;
    }

    public File lookupSurfaceMaterial(final String surfaceMaterialID) {
        for (final File existingFile : surfaceMatrialDir.listFiles()) {
            final String[] fileNameSections = existingFile.getName().split(SurfaceMaterial.FILE_NAME_CONNECTOR);
            if (fileNameSections[0].equals(surfaceMaterialID)) {
                return existingFile;
            }
        }
        return null;
    }

    public static long getFileSize(final File file) {
        long ret = 0;
        if (file.isDirectory()) {
            for (final File subFile : file.listFiles()) {
                ret += getFileSize(subFile);
            }
        } else {
            ret = file.length();
        }
        return ret;
    }

    public File getDebugFir(final String fName) {
        final File ret = new File(debugDir, fName);
        if (ret.exists() && ret.isFile()) {
            return ret;
        } else {
            return null;
        }
    }

    public synchronized void writeMerchandise(final Merchandise merchandise) throws CSPersistException {
        final String merchandiseID = merchandise.getId();
        File parentDir = null;
        if (merchandise instanceof Furniture) {
            parentDir = getFurnitureDir();
        } else if (merchandise instanceof SurfaceMaterial) {
            parentDir = getSurfaceMaterialDir();
        }

        if (parentDir == null) {
            throw new CSPersistException("Can not identify target merchandise destiny");
        }
        writeObjectToFile(new File(parentDir, merchandise.getFileName()), merchandise);
    }

    public synchronized Merchandise readMerchandise(final File file, final Class clz) throws CSPersistException {
        final String className = clz.getCanonicalName();
        if (!className.equals(Furniture.class.getCanonicalName()) &&
                !className.equals(SurfaceMaterial.class.getCanonicalName())) {
            throw new CSPersistException("Can not identify file type of " + className);
        }
        if (!file.exists()) {
            throw new CSPersistException(new FileNotFoundException(file.getName()));
        }
        try {
            final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            final Merchandise ret = (Merchandise)ois.readObject();
            if (ret.getClass().getCanonicalName().equals(clz.getCanonicalName())) {
                return ret;
            } else {
                throw new CSPersistException("Class type mis-match" +
                        ret.getClass().getCanonicalName() + " : " + clz.getCanonicalName());
            }
        } catch (Exception ex) {
            System.out.println("Problem when I am trying to read " + file.getName());
            throw new CSPersistException(ex);
        }
    }

    public synchronized void writeObjectToFile(final File file, final Object targetObj) throws CSPersistException {
        if (file.exists()) {
            file.delete();
        }
        if (targetObj == null || !(targetObj instanceof Serializable)) {
            throw new CSPersistException("Can not serilize given Object " + targetObj);
        }
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(targetObj);
            oos.flush();
            oos.close();
        } catch (Exception ex) {
            throw new CSPersistException(ex);
        }

    }

    public synchronized Object readObjectFromFile(final File file, final Class clz) throws CSPersistException {
        if (!file.exists()) {
            throw new CSPersistException(new FileNotFoundException(file.getName()));
        }
        try {
            final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            final Object ret = ois.readObject();
            if (ret.getClass().getCanonicalName().equals(clz.getCanonicalName())) {
                return ret;
            } else {
                throw new CSPersistException("Class type mis-match" +
                        ret.getClass().getCanonicalName() + " : " + clz.getCanonicalName());
            }
        } catch (Exception ex) {
            System.out.println("Problem when I am trying to read " + file.getName());
            throw new CSPersistException(ex);
        }
    }

}
