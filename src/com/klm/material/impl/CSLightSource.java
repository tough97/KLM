package com.klm.material.impl;

import com.klm.cons.impl.CSTransformGroup;
import com.klm.material.LightSource;
import org.apache.poi.poifs.property.Parent;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 4/30/12
 * Time: 8:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class CSLightSource extends CSTransformGroup implements LightSource{

    public static final int AMBIENT_LIGHT = 0;
    public static final int DIRECTIONAL_LIGHT = 1;
    public static final int POINT_LIGHT = 2;
    public static final int SPOT_LIGHT = 3;

    private Light light;

    public CSLightSource(final int lightType){
        createLight(lightType);
    };

    public void createLight(final int lightType){
        removeAllChildren();

        final BoundingSphere bs = new BoundingSphere(new Point3d(0, 0, 0), 20.0);
        switch (lightType){
            case AMBIENT_LIGHT:
                light = new AmbientLight();
                break;
            case DIRECTIONAL_LIGHT:
                light = new DirectionalLight();
                light.setCapability(DirectionalLight.ALLOW_DIRECTION_READ);
                light.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);
                break;
            case POINT_LIGHT:
                light = new PointLight();
                light.setCapability(PointLight.ALLOW_ATTENUATION_READ);
                light.setCapability(PointLight.ALLOW_ATTENUATION_WRITE);
                ((PointLight)light).setAttenuation(1.0f, 0.045f, 0.0075f);
                break;
            case SPOT_LIGHT:
                light = new SpotLight();
                light.setCapability(SpotLight.ALLOW_DIRECTION_READ);
                light.setCapability(SpotLight.ALLOW_DIRECTION_WRITE);
                light.setCapability(SpotLight.ALLOW_ATTENUATION_READ);
                light.setCapability(SpotLight.ALLOW_ATTENUATION_WRITE);
                ((SpotLight)light).setAttenuation(1.0f, 0.045f, 0.0075f);
                break;
        }
        light.setInfluencingBounds(bs);
        light.setCapability(Light.ALLOW_COLOR_READ);
        light.setCapability(Light.ALLOW_COLOR_WRITE);

        addChild(light);
    }

    @Override
    public boolean isOn() {
        return light.getEnable();
    }

    @Override
    public void turnOn() {
        light.setEnable(true);
    }

    @Override
    public void turnOff() {
        light.setEnable(false);
    }

    @Override
    public void setColor(Color color) {
        light.setColor(new Color3f(color));
    }

    @Override
    public Color getColor(){
        final Color3f color = new Color3f();
        light.getColor(color);
        return new Color(color.getX(), color.getY(), color.getZ());
    }

    @Override
    public Light getLight(){
        return light;
    }

    public int getLightType(){
        if(light == null){
            return -1;
        } else if(light instanceof AmbientLight){
            return AMBIENT_LIGHT;
        } else if(light instanceof SpotLight){
            return SPOT_LIGHT;
        } else if(light instanceof DirectionalLight){
            return DIRECTIONAL_LIGHT;
        } else if(light instanceof PointLight){
            return POINT_LIGHT;
        }
        return -1;
    }

    @Override
    public Vector3f getDirection() {
        final Vector3f ret = new Vector3f();
        if(light instanceof DirectionalLight){
            ((DirectionalLight) light).getDirection(ret);
        } else if(light instanceof SpotLight){
            ((SpotLight) light).getDirection(ret);
        }
        return ret;
    }

    @Override
    public void setDirection(Vector3f direction) {
        if(light instanceof DirectionalLight){
            ((DirectionalLight) light).setDirection(direction);
        } else if(light instanceof SpotLight){
            ((SpotLight) light).setDirection(direction);
        }
    }

    @Override
    public void setAttenuation(Point3f attenuation) {
        if(light instanceof PointLight){
            ((PointLight) light).setAttenuation(attenuation);
        }
    }

    @Override
    public Point3f getAttenuation() {
        final Point3f ret = new Point3f();
        if(light instanceof PointLight){
            ((PointLight) light).getAttenuation(ret);
        }
        return ret;
    }

    @Override
    public void detach() {
        final Group parent = (Group)getParent();
        parent.removeChild(this);
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {        
        oos.writeInt(getLightType());

        final double[] matrixData = new double[16];
        getTransform().get(matrixData);
        oos.writeObject(matrixData);

        final Color3f color = new Color3f();
        light.getColor(color);
        oos.writeObject(color);

        oos.flush();
    }

    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        createLight(ois.readInt());

        final double[] matrixData = (double[]) ois.readObject();
        final Transform3D trans = new Transform3D();
        trans.set(matrixData);
        setTransform(trans);

        final Color3f color = (Color3f)ois.readObject();
        light.setColor(color);

        System.gc();
    }

}
