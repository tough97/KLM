/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.util.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.media.j3d.Material;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.vecmath.Color3f;

/**
 *
 * @author gang-liu
 */
public class MaterialPanel extends JPanel implements DocumentListener,
        ActionListener, ChangeListener {

    private static final String[] COLOR_TARGETS = {"Ambient", "Emmesive",
        "Diffuse", "Specular", "Ambient_Diffuse"};
    public static final int RED_COMPONENT_INDEX = 1;
    public static final int GREEN_COMPONENT_INDEX = 2;
    public static final int BLUE_COMPONENT_INDEX = 3;
    public static final int COLOR_CHOOSER_INDEX = 4;
    public static final int SHININESS_SLIDER_INDEX = 1;
    public static final int COLOR_TARGET_COMBO_INDEX = 1;
    public static final int LIGHT_ENABLED_INDEX = 2;
    public static final int TITLE_PANEL_INDEX = 0;
    public static final int AMBIENT_PANEL_INDEX = 1;
    public static final int DIFFUSE_PANEL_INDEX = 2;
    public static final int EMMESIVE_PANEL_INDEX = 3;
    public static final int SPECULAR_PANEL_INDEX = 4;
    public static final int SHININESS_PANEL_INDEX = 5;
    public static final int COLOR_LIGHT_PANEL_INDEX = 6;
    public static final int HORIZONTAL_GAP = 20;
    public static final int VERTICAL_GAP = 20;
    private Set<Material> materialSet;
    private JPanel[] paramPanels = new JPanel[7];

    public MaterialPanel() {
        super();
        setBounds(0, 0, 450, 300);
        setPreferredSize(new Dimension(450, 300));
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        setLayout(new GridLayout(paramPanels.length, 1, HORIZONTAL_GAP,
                VERTICAL_GAP));
        paramPanels[TITLE_PANEL_INDEX] = new JPanel();
        paramPanels[TITLE_PANEL_INDEX].setLayout(new GridLayout(1, 5,
                HORIZONTAL_GAP, VERTICAL_GAP));
        paramPanels[TITLE_PANEL_INDEX].add(new JLabel("         ", JLabel.LEFT));
        paramPanels[TITLE_PANEL_INDEX].add(new JLabel(" Red ", JLabel.CENTER));
        paramPanels[TITLE_PANEL_INDEX].add(new JLabel("Green", JLabel.CENTER));
        paramPanels[TITLE_PANEL_INDEX].add(new JLabel(" Blue", JLabel.CENTER));
        paramPanels[TITLE_PANEL_INDEX].add(new JLabel("", JLabel.RIGHT));
        add(paramPanels[TITLE_PANEL_INDEX]);
        for (int index = AMBIENT_PANEL_INDEX, titleIndex = 0; index <
                SHININESS_PANEL_INDEX; index++, titleIndex++) {
            paramPanels[index] = new JPanel();
            paramPanels[index].setLayout(new GridLayout(1, 5, HORIZONTAL_GAP,
                    10));
            paramPanels[index].add(new JLabel(COLOR_TARGETS[titleIndex],
                    JLabel.LEFT));
            for (int colorFieldIndex = RED_COMPONENT_INDEX; colorFieldIndex <=
                    BLUE_COMPONENT_INDEX; colorFieldIndex++) {
                final JTextField colorInput = new JTextField();
                colorInput.getDocument().addDocumentListener(this);
                colorInput.setEditable(false);
                colorInput.setHorizontalAlignment(JTextField.CENTER);
                paramPanels[index].add(colorInput);
                paramPanels[index].setMaximumSize(new Dimension(5, 5));
                add(paramPanels[index]);
            }
            final JButton button = new JButton();
            button.addActionListener(this);
            paramPanels[index].add(button);
        }

        paramPanels[SHININESS_PANEL_INDEX] = new JPanel();
        paramPanels[SHININESS_PANEL_INDEX].setLayout(new BorderLayout());
        paramPanels[SHININESS_PANEL_INDEX].add(new JLabel("Shiness    ",
                JLabel.LEFT), BorderLayout.WEST);
        final JSlider shinessSlider = new JSlider(0, 128);
        shinessSlider.addChangeListener(this);
        shinessSlider.setMajorTickSpacing(20);
        shinessSlider.setMinorTickSpacing(1);
        shinessSlider.setPaintTrack(true);
        shinessSlider.setPaintLabels(true);
        shinessSlider.setFont(new Font("Serif", Font.ITALIC, 8));
        shinessSlider.addChangeListener(this);
        paramPanels[SHININESS_PANEL_INDEX].add(shinessSlider,
                BorderLayout.CENTER);
        add(paramPanels[SHININESS_PANEL_INDEX]);

        paramPanels[COLOR_LIGHT_PANEL_INDEX] = new JPanel();
        paramPanels[COLOR_LIGHT_PANEL_INDEX].setLayout(new GridLayout(1, 3,
                HORIZONTAL_GAP, VERTICAL_GAP));
        paramPanels[COLOR_LIGHT_PANEL_INDEX].add(new JLabel("Color Target",
                JLabel.LEFT));
        final JComboBox colorTarget = new JComboBox(COLOR_TARGETS);
        colorTarget.addActionListener(this);
        paramPanels[COLOR_LIGHT_PANEL_INDEX].add(colorTarget,
                COLOR_TARGET_COMBO_INDEX);
        final JCheckBox checkBox = new JCheckBox("Light Enabled");
//        checkBox.addChangeListener(this);
        checkBox.addActionListener(this);
        paramPanels[COLOR_LIGHT_PANEL_INDEX].add(checkBox, LIGHT_ENABLED_INDEX);
        add(paramPanels[COLOR_LIGHT_PANEL_INDEX]);
    }

    public void setMaterials(final Set<Material> materialSet) {
        if (materialSet != null && !materialSet.isEmpty()) {
            final Material[] materialArr = getMaterial(materialSet);
            this.materialSet = materialSet;
            Color3f color = new Color3f();
            materialArr[0].getAmbientColor(color);
            setPanelColor(paramPanels[AMBIENT_PANEL_INDEX], color);
            materialArr[0].getDiffuseColor(color);
            setPanelColor(paramPanels[DIFFUSE_PANEL_INDEX], color);
            materialArr[0].getEmissiveColor(color);
            setPanelColor(paramPanels[EMMESIVE_PANEL_INDEX], color);
            materialArr[0].getSpecularColor(color);
            final JSlider shininessSlider =
                    (JSlider) paramPanels[SHININESS_PANEL_INDEX].getComponent(
                    SHININESS_SLIDER_INDEX);
            shininessSlider.setValue((int) materialArr[0].getShininess());
            final JComboBox targetCombo =
                    (JComboBox) paramPanels[COLOR_LIGHT_PANEL_INDEX].
                    getComponent(
                    COLOR_TARGET_COMBO_INDEX);
            targetCombo.setSelectedIndex(materialArr[0].getColorTarget());
            final JCheckBox colorEnabled =
                    (JCheckBox) paramPanels[COLOR_LIGHT_PANEL_INDEX].
                    getComponent(
                    LIGHT_ENABLED_INDEX);
            colorEnabled.setSelected(materialArr[0].getLightingEnable());
            changeColorContent();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();
        if (source != null && materialSet != null) {
            if (source instanceof JButton) {
                final JButton button = (JButton) e.getSource();
                final JPanel panel = getParent(button);
                final JTextField redField = (JTextField) panel.getComponent(
                        RED_COMPONENT_INDEX);
                final JTextField greenField = (JTextField) panel.getComponent(
                        GREEN_COMPONENT_INDEX);
                final JTextField blueField = (JTextField) panel.getComponent(
                        BLUE_COMPONENT_INDEX);
                final Color color = new Color(
                        Integer.parseInt(redField.getText().trim()),
                        Integer.parseInt(greenField.getText().trim()),
                        Integer.parseInt(blueField.getText().trim()));
                final Color selectedColor = JColorChooser.showDialog(null, null,
                        color);
                if (selectedColor != null) {
                    redField.setText(String.valueOf(selectedColor.getRed()));
                    greenField.setText(String.valueOf(selectedColor.getGreen()));
                    blueField.setText(String.valueOf(selectedColor.getBlue()));
                    button.setBackground(color);
                }
            } else if (source instanceof JComboBox) {
                final JComboBox targetTypes = (JComboBox) source;
                final int colorTarget = targetTypes.getSelectedIndex();
                setAllColorTargets(colorTarget);
            } else if (source instanceof JCheckBox) {
                final boolean lightEnabled = ((JCheckBox) source).isSelected();
                setAllLightEnabled(lightEnabled);
            }
            changeColorContent();
            //Debug
            printAllMaterial();
        }
    }

    //All JTextFields for RGB        
    @Override
    public void insertUpdate(DocumentEvent e) {
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        final Object source = e.getSource();
        if (materialSet != null && source != null) {
            if (source instanceof JSlider) {
                final int shininess = ((JSlider) source).getValue();
                setAllShininess(shininess);
            }
            changeColorContent();
        }
        //Debug
        printAllMaterial();
    }

    private void setPanelColor(final JPanel panel, final Color3f color) {
        final JTextField[] colorFields = getColorFields(panel);
        colorFields[0].setText(String.valueOf((int) (color.getX() * 255)));
        colorFields[1].setText(String.valueOf((int) (color.getY() * 255)));
        colorFields[2].setText(String.valueOf((int) (color.getZ() * 255)));
        panel.getComponent(COLOR_CHOOSER_INDEX).setBackground(new Color(color.
                getX(), color.getY(), color.getZ()));
    }

    private JPanel getParent(final JComponent child) {
        final Container container = child.getParent();
        if (container instanceof JPanel) {
            return (JPanel) container;
        }
        return null;
    }

    private JTextField[] getColorFields(final JPanel panel) {
        if (panel.getComponentCount() == 5) {
            final JTextField[] ret = {
                (JTextField) panel.getComponent(RED_COMPONENT_INDEX),
                (JTextField) panel.getComponent(GREEN_COMPONENT_INDEX),
                (JTextField) panel.getComponent(BLUE_COMPONENT_INDEX)
            };
            return ret;
        } else {
            return null;
        }
    }

    private void changeColorContent() {
        final Color ambient = getColor(paramPanels[AMBIENT_PANEL_INDEX]);
        final Color diffuse = getColor(paramPanels[DIFFUSE_PANEL_INDEX]);
        final Color emmesive = getColor(paramPanels[SPECULAR_PANEL_INDEX]);
        final Color specular = getColor(paramPanels[SPECULAR_PANEL_INDEX]);
        setAllMaterialColors(ambient, diffuse, emmesive, specular);
    }

    private void setAllMaterialColors(final Color ambient, final Color diffuse,
            final Color emmesive, final Color specular) {
        if (materialSet != null && !materialSet.isEmpty()) {
            final Iterator<Material> materialIte = materialSet.iterator();
            while (materialIte.hasNext()) {
                final Material material = materialIte.next();
                material.setAmbientColor(new Color3f(ambient));
                material.setDiffuseColor(new Color3f(diffuse));
                material.setEmissiveColor(new Color3f(emmesive));
                material.setSpecularColor(new Color3f(specular));
            }
        }
    }

    private void setAllShininess(final int shininess) {
        if (materialSet != null && !materialSet.isEmpty()) {
            final Iterator<Material> materialIte = materialSet.iterator();
            while (materialIte.hasNext()) {
                final Material material = materialIte.next();
                material.setShininess(shininess);
            }
        }
    }

    private void setAllColorTargets(final int colorTarget) {
        if (materialSet != null && !materialSet.isEmpty()) {
            final Iterator<Material> materialIte = materialSet.iterator();
            while (materialIte.hasNext()) {
                final Material material = materialIte.next();
                material.setColorTarget(colorTarget);
            }
        }
    }

    private void setAllLightEnabled(final boolean enabled) {
        if (materialSet != null && !materialSet.isEmpty()) {
            final Iterator<Material> materialIte = materialSet.iterator();
            while (materialIte.hasNext()) {
                final Material material = materialIte.next();
                material.setLightingEnable(enabled);
            }
        }
    }

    private Color getColor(final JPanel colorPanel) {
        if (colorPanel.getComponentCount() != 5) {
            return null;
        }
        final int red = Integer.valueOf(((JTextField) colorPanel.getComponent(
                RED_COMPONENT_INDEX)).getText());
        final int green = Integer.valueOf(((JTextField) colorPanel.getComponent(
                GREEN_COMPONENT_INDEX)).getText());
        final int blue = Integer.valueOf(((JTextField) colorPanel.getComponent(
                BLUE_COMPONENT_INDEX)).getText());
        return new Color(red, green, blue);
    }

    private Material[] getMaterial(final Set<Material> materialSet) {
        final Material[] ret = new Material[materialSet.size()];
        final Iterator<Material> mateIte = materialSet.iterator();
        int index = 0;
        while (mateIte.hasNext()) {
            ret[index++] = mateIte.next();
        }
        return ret;
    }

    void printAllMaterial() {
        final Iterator<Material> matIt = materialSet.iterator();
        System.out.println(
                "------------------------------------------------------------");
        while (matIt.hasNext()) {
            System.out.println(matIt.next());
        }
        System.out.println(
                "------------------------------------------------------------");
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setSize(450, 300);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(null);
        final MaterialPanel m = new MaterialPanel();
        final Material mat = new Material();
        mat.setAmbientColor(new Color3f(Color.white));
        System.out.println(mat);
        final Set<Material> ms = new HashSet<Material>();
        ms.add(new Material());
        ms.add(new Material());
        ms.add(new Material());
        m.setMaterials(ms);
        System.out.println(Color.white.getRed());
        f.getContentPane().add(m);
        f.setVisible(true);
    }
}
