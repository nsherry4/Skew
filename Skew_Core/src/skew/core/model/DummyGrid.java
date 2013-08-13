package skew.core.model;

import java.util.List;

public class DummyGrid implements ISkewGrid<Object>
{

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public ISkewPoint<Object> get(int position)
	{
		return new ISkewPoint<Object>() {
			
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

			@Override
			public boolean isValid() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setValid(boolean validPoint) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Object getData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setData(Object data) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public ISkewPoint<Object> get(int x, int y)
	{
		return new ISkewPoint<Object>() {
			
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

			@Override
			public boolean isValid() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setValid(boolean validPoint) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Object getData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setData(Object data) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll)
	{
		return;
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
	public List<ISkewPoint<Object>> getPoints() {
		// TODO Auto-generated method stub
		return null;
	}


}
