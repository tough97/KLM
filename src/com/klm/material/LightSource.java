package com.klm.material;

import javax.media.j3d.Light;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 4/30/12
 * Time: 8:55 AM
 * To change this template use File | Settings | File Templates.
 */
public interface LightSource {

    public boolean isOn();

    public void turnOn();

    public void turnOff();

    public void setColor(final Color color);
    
    public Color getColor();

    public Light getLight();

    public int getLightType();

    public Vector3f getDirection();
    
    public void setDirection(final Vector3f direction);

    public void setAttenuation(final Point3f attenuation);

    public Point3f getAttenuation();

    public void detach();

}
