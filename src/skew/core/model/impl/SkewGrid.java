package skew.core.model.impl;

import java.util.List;

import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;

public class SkewGrid<T extends ISkewPoint> implements ISkewGrid
{

	protected List<T>               values;
	
	protected int                   width;
	protected int                   height;
		
	public SkewGrid(int width, int height, List<T> points)
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
	public T get(int position)
	{
		if (position < 0) return null;
		if (position >= values.size()) return null;
		return values.get(position);
	}

	@Override
	public T get(int x, int y)
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


	public List<T> getBackingList()
	{
		return values;
	}




}
