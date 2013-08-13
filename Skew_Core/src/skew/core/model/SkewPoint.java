package skew.core.model;


public final class SkewPoint<T> implements ISkewPoint<T>
{

	protected int x, y, index;
	private boolean validPoint;
	private T data;
	

	public SkewPoint(int x, int y, int index, T data)
	{
		this.x = x;
		this.y = y;
		this.index = index;
		this.data = data;
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
	
	@Override
	public boolean isValid() {
		return validPoint;
	}

	@Override
	public void setValid(boolean validPoint) {
		this.validPoint = validPoint;
	}
	
	@Override
	public T getData() {
		return data;
	}

	@Override
	public void setData(T data) {
		this.data = data;
	}

	
	
}
