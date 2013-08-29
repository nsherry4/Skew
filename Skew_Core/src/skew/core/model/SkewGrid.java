package skew.core.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SkewGrid<T> implements ISkewGrid<T>
{

	protected List<ISkewPoint<T>>   values;
	
	protected int                   width;
	protected int                   height;
	
	
	public SkewGrid(int width, int height, List<ISkewPoint<T>> points)
	{
		this.width = width;
		this.height = height;
		
		this.values = points;
	}
	
	
	@Override
	public int size()
	{
		return values.size();
	}

	@Override
	public ISkewPoint<T> getPoint(int position)
	{
		if (position < 0) return null;
		if (position >= values.size()) return null;
		return values.get(position);
	}

	@Override
	public ISkewPoint<T> getPoint(int x, int y)
	{
		return getPoint(width * y + x);
	}
	
	@Override
	public ISkewPoint<T> getPoint(ISkewPoint<?> point) {
		return getPoint(point.getIndex());
	}
	
	
	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	
	public List<ISkewPoint<T>> getPoints()
	{
		return new ArrayList<ISkewPoint<T>>(values);
	}


	@Override
	public T getData(int position) {
		return getPoint(position).getData();
	}


	@Override
	public T getData(int x, int y) {
		return getPoint(x, y).getData();
	}

	@Override
	public T getData(ISkewPoint<?> point) {
		return getData(point.getIndex());
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static <T> ISkewPoint<T> goNorth(ISkewGrid<T> grid, ISkewPoint<T> p)
	{
		return grid.getPoint(p.getX()-1, p.getY());
	}
	
	public static <T> ISkewPoint<T> goNorthEast(ISkewGrid<T> grid, ISkewPoint<T> p)
	{
		return grid.getPoint(p.getX()-1, p.getY()+1);
	}
	
	public static <T> ISkewPoint<T> goNorthWest(ISkewGrid<T> grid, ISkewPoint<T> p)
	{
		return grid.getPoint(p.getX()-1, p.getY()-1);
	}
	
	public static <T> ISkewPoint<T> goEast(ISkewGrid<T> grid, ISkewPoint<T> p)
	{
		return grid.getPoint(p.getX(), p.getY()+1);
	}
	
	public static <T> ISkewPoint<T> goSouth(ISkewGrid<T> grid, ISkewPoint<T> p)
	{
		return grid.getPoint(p.getX()+1, p.getY());
	}
	
	public static <T> ISkewPoint<T> goSouthEast(ISkewGrid<T> grid, ISkewPoint<T> p)
	{
		return grid.getPoint(p.getX()+1, p.getY()+1);
	}
	
	public static <T> ISkewPoint<T> goSouthWest(ISkewGrid<T> grid, ISkewPoint<T> p)
	{
		return grid.getPoint(p.getX()+1, p.getY()-1);
	}
	
	public static <T> ISkewPoint<T> goWest(ISkewGrid<T> grid, ISkewPoint<T> p)
	{
		return grid.getPoint(p.getX(), p.getY()-1);
	}


	@Override
	public Iterator<ISkewPoint<T>> iterator() {
		return getPoints().iterator();
	}








}
