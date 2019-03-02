package com.klm.persist.meta;


import com.klm.persist.CSPersistException;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.scenegraph.io.NamedObjectException;
import com.sun.j3d.utils.scenegraph.io.SceneGraphStreamReader;
import com.sun.j3d.utils.scenegraph.io.SceneGraphStreamWriter;

import javax.media.j3d.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class AppearanceMeta extends Appearance implements Serializable {
    private static final long serialVersionUID = 100;


    public AppearanceMeta() {
        super();
        init();
    }

    public AppearanceMeta(Appearance basAppearance) {
        super();
        init();
    }

    private void init() {
        setCapability(Appearance.ALLOW_TEXGEN_READ);
        setCapability(Appearance.ALLOW_TEXGEN_WRITE);
        setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        setMaterial(new Material());

    }

    private void writeObject(final ObjectOutputStream oos) throws IOException,
            NamedObjectException {
        SceneGraphStreamWriter writer = new
                SceneGraphStreamWriter(oos);
        final BranchGroup bg = new BranchGroup();
        Appearance tempAppearance = new Appearance();
        setAttributesProperties(this, tempAppearance);
        final Shape3D shape = new Shape3D(null, tempAppearance);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        bg.addChild(shape);
        writer.writeBranchGraph(bg, new HashMap());
        oos.writeObject(new MaterialMeta(getMaterial()));
        oos.writeObject(getTexture2DImage());
    }

    private void readObject(final ObjectInputStream objectInputStream)
            throws ClassNotFoundException, IOException {
        final SceneGraphStreamReader reader = new SceneGraphStreamReader(objectInputStream);
        final Appearance tempAppearance = ((Shape3D) (reader.readBranchGraph(new HashMap()).getChild(0))).getAppearance();
        setAttributesProperties(tempAppearance, this);
        final MaterialMeta materialMeta = (MaterialMeta) objectInputStream.readObject();
        final BufferedImageMeta textureImage = (BufferedImageMeta) objectInputStream.readObject();
        setMaterial(materialMeta);
        setTextureImage(textureImage);

    }

    private void setAttributesProperties(final Appearance baseAppearance,
                                         final Appearance copyAppearance) {
        if (baseAppearance != null) {
            copyAppearance.setColoringAttributes(baseAppearance.getColoringAttributes());
            copyAppearance.setLineAttributes(baseAppearance.getLineAttributes());
            copyAppearance.setPointAttributes(baseAppearance.getPointAttributes());
            copyAppearance.setPolygonAttributes(baseAppearance.getPolygonAttributes());
            copyAppearance.setRenderingAttributes(baseAppearance.getRenderingAttributes());
            copyAppearance.setTexCoordGeneration(baseAppearance.getTexCoordGeneration());
            copyAppearance.setTextureAttributes(baseAppearance.getTextureAttributes());
            copyAppearance.setTextureUnitState(baseAppearance.getTextureUnitState());
            copyAppearance.setTransparencyAttributes(baseAppearance.getTransparencyAttributes());

        }
    }

    public void setTextureUnitStateImage(final BufferedImage textureImage) {
        try {
            if (textureImage != null) {
                final BufferedImageMeta bufferedImageMeta = new BufferedImageMeta(textureImage);
                setTextureImage(bufferedImageMeta);
            }
        } catch (CSPersistException ex) {
            ex.printStackTrace();
        }
    }

    public void setTextureUnitStateImage(final BufferedImageMeta textureImage) {
        if (textureImage != null) {
            TextureLoader textureLoader = new TextureLoader(textureImage.getImage());
            Texture texture = textureLoader.getTexture();
            setTexture(texture);
        }
    }

    public void setTextureImage(final BufferedImage textureImage) {
        try {
            if (textureImage != null) {
                final BufferedImageMeta bufferedImageMeta = new BufferedImageMeta(textureImage);
                setTextureImage(bufferedImageMeta);
            }
        } catch (CSPersistException ex) {
            ex.printStackTrace();
        }
    }

    public void setTextureImage(final BufferedImageMeta textureImage) {
        if (textureImage != null) {
            TextureLoader textureLoader = new TextureLoader(textureImage.getImage());
            Texture texture = textureLoader.getTexture();
            ImageComponent[] images = texture.getImages();
            for (ImageComponent image : images) {
                image.setCapability(ImageComponent.ALLOW_IMAGE_READ);
            }
            setTexture(texture);
        }
    }


    public BufferedImageMeta getTexture2DImage() {
        if (getTexture() != null) {
            final ImageComponent2D imageComponent2D = (ImageComponent2D) getTexture().getImage(0);
            try {
                return new BufferedImageMeta(imageComponent2D.getImage());
            } catch (CSPersistException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public NodeComponent cloneNodeComponent(boolean forceDuplicate) {
        AppearanceMeta appearanceMeta = new AppearanceMeta();
        appearanceMeta.duplicateNodeComponent(this, forceDuplicate);
        appearanceMeta.setMaterial(getMaterial());
        appearanceMeta.setTexture(getTexture());
        return appearanceMeta;
    }
}