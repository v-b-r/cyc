package www.coral.innovations.cyc.Storage;

public abstract class DrawableClickListener {
    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public abstract void onClick(DrawablePosition target);
}
