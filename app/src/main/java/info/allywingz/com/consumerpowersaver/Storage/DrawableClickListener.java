package info.allywingz.com.consumerpowersaver.Storage;

public abstract class DrawableClickListener {
    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public abstract void onClick(DrawablePosition target);
}
