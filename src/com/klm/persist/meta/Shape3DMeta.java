package com.klm.persist.meta;

import com.sun.j3d.utils.scenegraph.io.NamedObjectException;
import com.sun.j3d.utils.scenegraph.io.SceneGraphStreamReader;
import com.sun.j3d.utils.scenegraph.io.SceneGraphStreamWriter;

import javax.media.j3d.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Shape3DMeta extends Shape3D implements Serializable {
    private static final long serialVersionUID = 100;

    public Shape3DMeta(final Shape3D baseShape3D) {
        super();
        init();
        copyProperties(baseShape3D);

    }

    public Shape3DMeta(final Geometry geometry) {
        super(geometry);
        init();

    }

    public Shape3DMeta(final Geometry geometry,
                       final AppearanceMeta appearanceMeta) {
        this(geometry);
        setAppearance(appearanceMeta);

    }

    private void init() {
        setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
        setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException,
            NamedObjectException {
        SceneGraphStreamWriter writer = new
                SceneGraphStreamWriter(oos) {
                    private static final long serialVersionUID = 100;
                };
        final BranchGroup bg = new BranchGroup();
        bg.addChild(new Shape3D(getGeometry()));
        final HashMap map = new HashMap();
        writer.writeBranchGraph(bg, map);
        oos.writeObject(getAppearance());
    }

    private void readObject(final ObjectInputStream objectInputStream)
            throws ClassNotFoundException, IOException {
        final SceneGraphStreamReader reader = new SceneGraphStreamReader(objectInputStream) {
            private static final long serialVersionUID = 100;
        };
        Shape3D baseShape3D = (Shape3D) (reader.readBranchGraph(new HashMap()).getChild(0));
        final AppearanceMeta shapeAppearance = (AppearanceMeta) objectInputStream.readObject();
        removeAllGeometries();
        addGeometry(baseShape3D.getGeometry());
        setAppearance(shapeAppearance);
        init();
    }

    private void copyProperties(final Shape3D baseShape3D) {
        if (baseShape3D != null) {
            removeAllGeometries();
            addGeometry(baseShape3D.getGeometry());
            setAppearance(baseShape3D.getAppearance());
        }
    }

    public Node cloneNode(boolean b) {
        Appearance appearance = getAppearance();
        if (appearance != null) {
            Appearance clonedAppearance = (Appearance) appearance.cloneNodeComponent(true);
            Texture texture = appearance.getTexture();
            if (texture != null) {
                clonedAppearance.setTexture(texture);
            }
            return new Shape3DMeta(new Shape3D(getGeometry(), clonedAppearance));

        }
        return new Shape3DMeta(new Shape3D(getGeometry()));
    }
}