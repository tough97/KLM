package cs.designer.view.viewer;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewerAvatar;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import java.awt.*;

/**
 * @author rongyang
 */
public class MiniMap extends Canvas3D {

    public final static double DEFAULT_VIEW_HEIGHT = 300;
    public final static Color3f AVATAR_COLOR = new Color3f(Color.RED);
    private Viewer mapViewer;
    private Viewer userViewer;
    private ViewerAvatar viewerAvatar;
    private SimpleUniverse scenceUniverse;
    private Appearance avatarApp;
    private boolean avatarVisible = true;
    private double viewHeight = DEFAULT_VIEW_HEIGHT;

    public MiniMap(Viewer userViewer,
                   SimpleUniverse scenceUniverse) {
        super(SimpleUniverse.getPreferredConfiguration());
        setSize(200, 200);
        this.scenceUniverse = scenceUniverse;
        this.userViewer = userViewer;
        this.viewerAvatar = createViewerAvatar();
        this.userViewer.setAvatar(viewerAvatar);
        createMapView();
    }

    private void createMapView() {
        mapViewer = new Viewer(this);
        ViewingPlatform mapViewPlatform = new ViewingPlatform(1);
        final Transform3D trans = new Transform3D();
        trans.set(new Vector3d(0, viewHeight, 0));
        Transform3D rot = new Transform3D();
        rot.rotX(-Math.PI / 2);
        trans.mul(rot);
        mapViewPlatform.getViewPlatformTransform().setTransform(trans);
        mapViewer.setViewingPlatform(mapViewPlatform);
        mapViewer.getView().setFrontClipDistance(0.1);
        mapViewer.getView().setFieldOfView(0.1);
        mapViewer.getView().setBackClipDistance(500);
        scenceUniverse.getLocale().addBranchGraph(mapViewPlatform);
    }

    private ViewerAvatar createViewerAvatar() {
        ViewerAvatar avatar = new ViewerAvatar();
        TransformGroup tg = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setEuler(new Vector3d(Math.PI / 2.0, Math.PI, 0));
        tg.setTransform(t3d);
        avatarApp = new Appearance();
        avatarApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
        avatarApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(AVATAR_COLOR);
        avatarApp.setColoringAttributes(ca);
        final TransparencyAttributes ta = new TransparencyAttributes();
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        ta.setTransparency(0f);
        ta.setTransparencyMode(TransparencyAttributes.FASTEST);
        avatarApp.setTransparencyAttributes(ta);
        Cone viewHead = new Cone(1F, 3F, Primitive.GENERATE_NORMALS, avatarApp);
        tg.addChild(viewHead);
        avatar.addChild(tg);
        return avatar;
    }

    public void setAvatarVisible(boolean visible) {
        if (visible != this.avatarVisible) {
            if (visible) {
                avatarApp.getTransparencyAttributes().setTransparency(0.45f);
            } else {
                avatarApp.getTransparencyAttributes().setTransparency(1f);
            }
            this.avatarVisible = visible;
        }
    }

    public boolean isAvatarVisible() {
        return avatarVisible;
    }
}
