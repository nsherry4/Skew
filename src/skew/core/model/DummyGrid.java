package skew.core.model;

public class DummyGrid implements SkewGrid
{

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public SkewPoint get(int position)
	{
		return new SkewPoint() {
			
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
	public SkewPoint get(int x, int y)
	{
		return new SkewPoint() {
			
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
	public boolean setPointSelected(SkewPoint p, boolean deselectAll)
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

}
