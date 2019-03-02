/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.cons.impl;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.universe.SimpleUniverse;
import org.dom4j.Element;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * @author gang-liu
 */
public abstract class CSBranchGroup extends BranchGroup implements Serializable {

    protected String name;
    protected static final String OUTPUT_ENCODING = "UTF-8";
    protected static final String INPUT_ENCODING = "UTF-8";

    private static final long serialVersionUID = 100;
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("name", String.class)
    };

    public CSBranchGroup() {
        initiate();
    }

    protected void initiate() {
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_PARENT_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ENABLE_COLLISION_REPORTING);
        setCapability(BranchGroup.ALLOW_PICKABLE_READ);
        setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
    }

    public Node getFirstParentOf(final Class parentKlz) {
        Node parent = getParent();
        while (parent != null) {
            if (parentKlz.getCanonicalName().equals(parent.getClass().getCanonicalName())) {
                return parent;
            } else {
                parent = parent.getParent();
            }
        }
        return parent;
    }


    public Canvas3D getCanvas3DAttached() {
        final Locale locale = getLocale();
        if (locale != null) {
            final VirtualUniverse vu = locale.getVirtualUniverse();
            return vu instanceof SimpleUniverse ? ((SimpleUniverse) vu).getCanvas() : null;
        } else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    protected Element writeToElement(final Field field, final Element parentElement) throws IllegalAccessException, IOException {
        field.setAccessible(true);
        final Object fieldValue = field.get(this);
        final String fieldName = field.getName();
        if (fieldValue instanceof Serializable) {
            final Element subElement = parentElement.addElement(fieldName);
            subElement.setText(new String(readFieldValue((Serializable) fieldValue)));
        } else {
        }
        field.setAccessible(false);
        return null;
    }

    protected static String encode(final String message) {
        return new String(encrypt(message));
    }

    protected static String decoding(final String message) {
        return decrypt(message.getBytes());
    }

    protected static byte[] encrypt(String message) {
        try {
            final MessageDigest md = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = md.digest("HG58YZ3CR9".getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            for (int j = 0, k = 16; j < 8; ) {
                keyBytes[k++] = keyBytes[j++];
            }

            final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            final byte[] plainTextBytes = message.getBytes("utf-8");
            final byte[] cipherText = cipher.doFinal(plainTextBytes);
            final String encodedCipherText = new sun.misc.BASE64Encoder().encode(cipherText);

            return cipherText;
        } catch (java.security.InvalidAlgorithmParameterException e) {
            System.out.println("Invalid Algorithm");
        } catch (javax.crypto.NoSuchPaddingException e) {
            System.out.println("No Such Padding");
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("No Such Algorithm");
        } catch (java.security.InvalidKeyException e) {
            System.out.println("Invalid Key");
        } catch (BadPaddingException e) {
            System.out.println("Invalid Key");
        } catch (IllegalBlockSizeException e) {
            System.out.println("Invalid Key");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Invalid Key");
        }

        return null;
    }

    protected static String decrypt(byte[] message) {
        try {
            final MessageDigest md = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = md.digest("HG58YZ3CR9".getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            for (int j = 0, k = 16; j < 8; ) {
                keyBytes[k++] = keyBytes[j++];
            }

            final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, key, iv);

            //final byte[] encData = new sun.misc.BASE64Decoder().decodeBuffer(message);
            final byte[] plainText = decipher.doFinal(message);

            return plainText.toString();
        } catch (java.security.InvalidAlgorithmParameterException e) {
            System.out.println("Invalid Algorithm");
        } catch (javax.crypto.NoSuchPaddingException e) {
            System.out.println("No Such Padding");
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("No Such Algorithm");
        } catch (java.security.InvalidKeyException e) {
            System.out.println("Invalid Key");
        } catch (BadPaddingException e) {
            System.out.println("Invalid Key");
        } catch (IllegalBlockSizeException e) {
            System.out.println("Invalid Key");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Invalid Key");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected static byte[] readFieldValue(final Serializable value) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(value);
        return bos.toByteArray();
    }

    protected static String readFieldValueToString(final Serializable value) throws IOException{
        return new String(readFieldValue(value));
    }

}
