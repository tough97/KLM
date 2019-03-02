package cs.designer.view.controller;

import cs.designer.swing.tool.Generatorable;
import cs.designer.view.viewer.DisplayView;

import javax.media.j3d.TransformGroup;

/**
 * @author rongyang
 */
public interface DisplayControlable {

    public final static float MODEL_TRANSLATE_UINT = 0.5f;//TranslateUint
    public final static float ROOM_TRANSLATE_UINT = 20;
    public final static double ROTATE_ANGLE_UINT = Math.PI / 24;
    public final static double MODEL_MAX_DISTANCE = 10f;
    public final static double MODEL_MIN_DISIANCE = 0.5f;
    public final static double ROOM_MAX_DISTANCE = 1500f;
    public final static double ROOM_MIN_DISIANCE = 100f;

    public void registerController(TransformGroup contrGroup, boolean addListenerable);

    public DisplayView getView();

    public Generatorable getGenerator();


    public void reset();
}
