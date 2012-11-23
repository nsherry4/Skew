package skew.core.model.impl;

import skew.core.model.ISkewPoint;

public class SkewPoint implements ISkewPoint
{

	protected int x, y, index;
	
	public SkewPoint(int x, int y, int index)
	{
		this.x = x;
		this.y = y;
		this.index = index;
	}

	
	@Override
	public int getIndex()
	{
		return index;
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getY()
	{
		return y;
	}
	
}
