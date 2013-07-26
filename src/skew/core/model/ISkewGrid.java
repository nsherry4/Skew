package skew.core.model;


public interface ISkewGrid
{

	public abstract int size();

	public abstract ISkewPoint get(int position);

	public abstract ISkewPoint get(int x, int y);

	public abstract boolean setPointSelected(int x, int y, boolean deselectAll);

	public abstract int getWidth();

	public abstract int getHeight();
	
}