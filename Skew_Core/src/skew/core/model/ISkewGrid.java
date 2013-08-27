package skew.core.model;

import java.util.List;


public interface ISkewGrid<T>
{

	public abstract int size();

	public abstract ISkewPoint<T> getPoint(int position);
	public abstract ISkewPoint<T> getPoint(int x, int y);
	public abstract List<ISkewPoint<T>> getPoints();
	
	public abstract T getData(int position);
	public abstract T getData(int x, int y);
	
	public abstract void setPointSelected(int x, int y, boolean deselectAll);

	public abstract int getWidth();
	public abstract int getHeight();
	
}