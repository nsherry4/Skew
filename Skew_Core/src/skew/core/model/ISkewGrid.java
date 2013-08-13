package skew.core.model;

import java.util.List;


public interface ISkewGrid<T>
{

	public abstract int size();

	public abstract ISkewPoint<T> get(int position);
	public abstract ISkewPoint<T> get(int x, int y);
	public abstract List<ISkewPoint<T>> getPoints();
	
	public abstract void setPointSelected(int x, int y, boolean deselectAll);

	public abstract int getWidth();
	public abstract int getHeight();
	
}