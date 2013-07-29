package skew.core.model.impl;

public class BasicSkewPoint<T> extends SkewPoint
{

	private T value;
		
	public BasicSkewPoint(int x, int y, int index, T value)
	{
		super(x, y, index);
		this.value = value;
	}

	public T getData()
	{
		return value;
	}

	public void setData(T value)
	{
		this.value = value;
	}

	
	
	
}
