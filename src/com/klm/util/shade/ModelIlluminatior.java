/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util.shade;

import com.klm.exhib.ModelDisplayer;
import com.klm.util.CSImageUtil;
import com.klm.util.CSUtilException;
//import com.klm.util.impl.OBJModelImportor;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickResult;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.media.j3d.Appearance;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureUnitState;
import javax.media.j3d.Transform3D;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.vecmath.Vector3d;

/**
 *
 * @author gang-liu
 */
public final class ModelIlluminatior extends ModelDisplayer implements
        ActionListener {

    private static final int IMAGE_DIMENSION = 125;
    private static final int SELECTION_TEX = -1;
    private static final int SHADING_TEX = -2;
    private static final int SHADOWING_TEX = -3;
    private static final BufferedImage SELECTION_IMG = CSImageUtil.
            createBufferedImage(IMAGE_DIMENSION, IMAGE_DIMENSION, Color.red,
            120);
    private static final long CLICK_INTERVALS = 500;
    private Long lastLeftClick = null;
    private Map<Material, Set<Shape3D>> shapeMapping;
    private Set<Shape3D> selectedShapes = new HashSet<Shape3D>();

    public ModelIlluminatior() throws AWTException {
        super();
        menuItems[EDIT_SURFACE_TEXTURE_INDEX].addActionListener(this);
        menuItems[EDIT_SURFACE_TEXTURE_INDEX].setVisible(true);
    }

    public void setDisselection() {
        if (!selectedShapes.isEmpty()) {
            for (final Shape3D shape : selectedShapes) {
                final TextureUnitState[] tuss = shape.getAppearance().
                        getTextureUnitState();
                tuss[tuss.length + SELECTION_TEX].setTexture(null);
            }
            selectedShapes.clear();
        }
    }

    public void setSelections(final Set<Shape3D> selectedShapes) {
        if (selectedShapes != null && !selectedShapes.isEmpty()) {
            setDisselection();
            this.selectedShapes.addAll(selectedShapes);
            for (final Shape3D shape : this.selectedShapes) {
                final TextureUnitState[] tuss = shape.getAppearance().
                        getTextureUnitState();
                final TextureLoader tl = new TextureLoader(SELECTION_IMG);
                tuss[tuss.length + SELECTION_TEX].setTexture(tl.getTexture());
            }
        }
    }

    public Set<Shape3D> getShapeWithSameMaterial(final Shape3D sample) {
        for (final Material material : shapeMapping.keySet()) {
            for (final Shape3D shape : shapeMapping.get(material)) {
                if (shape == sample) {
                    return shapeMapping.get(material);
                }
            }
        }
        return null;
    }
    
    private void displayAllMaterial(){
        for(final Material m : shapeMapping.keySet()){
            System.out.println(m.toString()+" has "+shapeMapping.get(m).size()+" Shape3D(s) "+shapeMapping.get(m).toArray()[0].toString());
        }
    }
    
    private void grandCapabilitiesOfAppearance(final Appearance app) {
        app.setCapability(Appearance.ALLOW_MATERIAL_READ);
        app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        app.getMaterial().setCapability(Material.ALLOW_COMPONENT_READ);
        app.getMaterial().setCapability(Material.ALLOW_COMPONENT_WRITE);
        app.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_READ);
        app.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_WRITE);
    }

    private void extendAppearanceTSU(final Appearance app) {
        app.setTextureUnitState(expendTSU(app.getTextureUnitState()));
    }

    private TextureUnitState[] expendTSU(final TextureUnitState[] origin) {
        TextureUnitState[] ret;
        if (origin == null) {
            ret = new TextureUnitState[2];
        } else {
            ret = new TextureUnitState[origin.length + 2];
            System.arraycopy(origin, 0, ret, 0, origin.length);
        }
        TextureAttributes ta = new TextureAttributes();
        ta.setTextureMode(TextureAttributes.DECAL);
        ret[ret.length + SELECTION_TEX] = new TextureUnitState(null, ta, null);
        ret[ret.length + SELECTION_TEX].setCapability(
                TextureUnitState.ALLOW_STATE_READ);
        ret[ret.length + SELECTION_TEX].setCapability(
                TextureUnitState.ALLOW_STATE_WRITE);
        ta = new TextureAttributes();
        ta.setTextureMode(TextureAttributes.DECAL);
//        ta.setTextureMode(TextureAttributes.MODULATE);
        final TexCoordGeneration tcg =
                new TexCoordGeneration(TexCoordGeneration.SPHERE_MAP,
                TexCoordGeneration.TEXTURE_COORDINATE_2);
        ret[ret.length + SHADING_TEX] = new TextureUnitState(null, ta, tcg);
        ret[ret.length + SHADING_TEX].setCapability(
                TextureUnitState.ALLOW_STATE_READ);
        ret[ret.length + SHADING_TEX].setCapability(
                TextureUnitState.ALLOW_STATE_WRITE);
        return ret;
    }

    private Map<Material, Set<Shape3D>> parseNodeMaterial(final Node node) {
        final Map<Material, Set<Shape3D>> ret =
                new HashMap<Material, Set<Shape3D>>();
        if (node instanceof Group) {
            final Group group = (Group) node;
            for (int index = 0; index < group.numChildren(); index++) {
                final Map<Material, Set<Shape3D>> subMap =
                        parseNodeMaterial(group.getChild(index));
                for (final Material material : subMap.keySet()) {
                    final Set<Shape3D> retSet = ret.get(material);
                    if (retSet != null) {
                        ret.get(material).addAll(subMap.get(material));
                    } else {
                        ret.put(material, subMap.get(material));
                    }
                }
            }
        } else if (node instanceof Shape3D) {
            final Shape3D shape3D = (Shape3D) node;
            grandCapabilitiesOfAppearance(shape3D.getAppearance());
            extendAppearanceTSU(shape3D.getAppearance());
            final Set<Shape3D> set = new HashSet<Shape3D>();
            set.add(shape3D);
            final Material m = ((Shape3D) node).getAppearance().getMaterial();
            ret.put(m, set);
        }
        return ret;
    }

    @Override
    public void setModel(final Node node) {
        shapeMapping = parseNodeMaterial(node);
        //Debug
//        displayAllMaterial();
        super.setModel(node, true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
            case (MouseEvent.BUTTON1):
                final PickResult result = pickCanvas.pickClosest();
                if (result != null) {
                    final Node node = result.getNode(PickResult.SHAPE3D);
                    if (node != null && node instanceof Shape3D) {
                        Set<Shape3D> selectedShapes;
                        //if this is a single click
                        if ((lastLeftClick == null) ||
                                (System.currentTimeMillis() - lastLeftClick.
                                longValue() > CLICK_INTERVALS)) {
                            selectedShapes = new HashSet<Shape3D>();
                            selectedShapes.add((Shape3D) node);
                            lastLeftClick = new Long(System.currentTimeMillis());
                        } else {
                            lastLeftClick = null;
                            selectedShapes = getShapeWithSameMaterial(
                                    (Shape3D) node);
                            if (selectedShapes == null) {
                                selectedShapes = new HashSet<Shape3D>();
                            }
                        }
                        setSelections(selectedShapes);
                    }
                } else {
                    //Nothing is selected
                    setDisselection();
                }
                break;
            case (MouseEvent.BUTTON2):
                resetTransforms();
                break;
            case (MouseEvent.BUTTON3):
                rightBMenu.show(e.getComponent(), e.getX(), e.getY());
                break;
        }
    }

    private Texture createTexture(final BufferedImage bi) {
        final TextureLoader loader = new TextureLoader(bi);
        if (loader == null) {
            return null;
        } else {
            return loader.getTexture();
        }
    }

    public static BufferedImage getImage() {
        return SELECTION_IMG;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        final Object source = e.getSource();
        if (source != null && source instanceof JMenuItem) {
            final JMenuItem item = (JMenuItem) source;
            final String command = item.getActionCommand();
            if (command.equals(super.EDIT_SURFACE_TEXTURE) && !selectedShapes.
                    isEmpty()) {
                final JFileChooser fileChooser = new JFileChooser(System.
                        getProperty("user.home"));
                final FileFilter ff = new FileNameExtensionFilter("Etx", "jpg",
                        "jpeg", "png", "bmp");
                fileChooser.setFileFilter(ff);
                if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(
                        null)) {
                    final File imageFile = fileChooser.getSelectedFile();
                    try {
                        final TextureLoader loader =
                                new TextureLoader(imageFile.toURI().toURL(), new String("RGB"),
                                TextureLoader.BY_REFERENCE,
                                null);
                        if (loader != null) {
                            final Texture tex = loader.getTexture();
                            for (final Shape3D shape : selectedShapes) {
                                final TextureUnitState[] tus = shape.
                                        getAppearance().getTextureUnitState();
                                final TextureAttributes tas = tus[tus.length +SHADING_TEX].getTextureAttributes();
                                final Transform3D trans = new Transform3D();
                                trans.setTranslation(new Vector3d(0.0, 0.1, 0.0));
                                tus[tus.length + SHADING_TEX].setTexture(tex);
                            }
                            setDisselection();
                        }
                    } catch (Exception ex0) {
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, CSUtilException,
            AWTException {
        final JFrame f = new JFrame();
        f.setSize(400, 400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final ModelIlluminatior mil = new ModelIlluminatior();
        final String fName = "/home/gang-liu/Desktop/model/lanbojini.obj";
//        mil.setModel(new OBJModelImportor().importFromFile(args[0]));
//        mil.setModel(new OBJModelImportor().importFromFile(fName));
        mil.addLights(ModelDisplayer.createExhibitionLights(Color.WHITE));
        f.setVisible(true);
        f.getContentPane().add(mil);
    }
}
