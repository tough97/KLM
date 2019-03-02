/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klm.exhib;

import java.util.HashSet;
import java.util.Set;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Light;

/**
 *
 * @author gang-liu
 */
public class LightNode extends BranchGroup {

    private Set<Light> lights;

    public LightNode(final Light light) {
        lights = new HashSet<Light>();
        lights.add(light);
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        light.setCapability(Light.ALLOW_COLOR_READ);
        light.setCapability(Light.ALLOW_COLOR_WRITE);
        light.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_READ);
        light.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
        light.setCapability(Light.ALLOW_STATE_READ);
        light.setCapability(Light.ALLOW_STATE_WRITE);
        addChild(light);
    }

    public LightNode(final Set<Light> lights) {
        super();
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        for (final Light light : lights) {
            light.setCapability(Light.ALLOW_COLOR_READ);
            light.setCapability(Light.ALLOW_COLOR_WRITE);
            light.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_READ);
            light.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
            light.setCapability(Light.ALLOW_STATE_READ);
            light.setCapability(Light.ALLOW_STATE_WRITE);
            addChild(light);
        }
        this.lights = lights;
    }

    public LightNode(final Light[] lights) {
        super();
        this.lights = new HashSet<Light>();
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        for (final Light light : lights) {
            light.setCapability(Light.ALLOW_COLOR_READ);
            light.setCapability(Light.ALLOW_COLOR_WRITE);
            light.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_READ);
            light.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
            light.setCapability(Light.ALLOW_STATE_READ);
            light.setCapability(Light.ALLOW_STATE_WRITE);
            addChild(light);
            this.lights.add(light);
        }
    }

    public Set<Light> getAllLights() {
        return lights;
    }

    public int getLightCount() {
        return lights.size();
    }

    public void turnOffLights() {
        for (final Light light : lights) {
            light.setEnable(false);
        }
    }

    public void turnOnLights() {
        for (final Light light : lights) {
            light.setEnable(true);
        }
    }
}
