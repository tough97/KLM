package cs.designer.module;


public final class Pan {
    private float thickness;
    public final static float MAX_THICKNESS = 0.8f;
    public final static float MINI_THICKNESS = 0.2f;

    public enum PanType {
        LINE, RECTAMGLE, ELLIPSE, POLYGON
    }

    private PanType panType;
    private boolean magnetismEnabled;
    private static Pan pan = new Pan();
    private int polygonSideNmb = 5;

    private Pan() {
        magnetismEnabled = true;
        panType = PanType.LINE;
        thickness = TempWall.WALL_THICKNESS;
    }

    public static Pan getPan() {
        if (pan == null) {
            pan = new Pan();
        }
        return pan;
    }

    public PanType getPanType() {
        return panType;
    }

    public void setPanType(PanType panType) {
        this.panType = panType;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        if (thickness > MAX_THICKNESS) {
            thickness = MAX_THICKNESS;
        } else if (thickness < MINI_THICKNESS) {
            thickness = MINI_THICKNESS;
        }
        this.thickness = thickness;
    }

    public boolean isMagnetismEnabled() {
        return magnetismEnabled;
    }

    public void setMagnetismEnabled(boolean magnetismEnabled) {
        this.magnetismEnabled = magnetismEnabled;
    }

    public void reset() {
        pan = null;
    }

    public void setPolygonSideNmb(int polygonSideNmb) {
        this.polygonSideNmb = polygonSideNmb;
    }

    public int getPolygonSideNmb() {
        return polygonSideNmb;
    }
}
