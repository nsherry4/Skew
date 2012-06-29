package skew.core.model;


public interface SkewGrid
{

	public abstract int size();

	public abstract SkewPoint get(int position);

	public abstract SkewPoint get(int x, int y);

	public abstract boolean setPointSelected(SkewPoint p, boolean deselectAll);

	public abstract int getWidth();

	public abstract int getHeight();

}