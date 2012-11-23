package skew.core.model.impl;

import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;

public class DummyGrid implements ISkewGrid
{

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public ISkewPoint get(int position)
	{
		return new ISkewPoint() {
			
			@Override
			public int getY()
			{
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getX()
			{
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getIndex()
			{
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

	@Override
	public ISkewPoint get(int x, int y)
	{
		return new ISkewPoint() {
			
			@Override
			public int getY()
			{
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getX()
			{
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getIndex()
			{
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

	@Override
	public boolean setPointSelected(ISkewPoint p, boolean deselectAll)
	{
		return false;
	}

	@Override
	public int getWidth()
	{
		return 0;
	}

	@Override
	public int getHeight()
	{
		return 0;
	}

	@Override
	public String datasetName()
	{
		return "Dataset";
	}

}
