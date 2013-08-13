package skew.core.model;

import java.util.ArrayList;
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
	public ISkewPoint<T> get(int position)
	{
		if (position < 0) return null;
		if (position >= values.size()) return null;
		return values.get(position);
	}

	@Override
	public ISkewPoint<T> get(int x, int y)
	{
		return get(width * y + x);
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
	
	@Override
	public void setPointSelected(int x, int y, boolean deselectAll)
	{
		return;
	}

	
	public List<ISkewPoint<T>> getPoints()
	{
		return new ArrayList<ISkewPoint<T>>(values);
	}




}
