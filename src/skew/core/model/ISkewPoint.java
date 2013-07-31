package skew.core.model;



public interface ISkewPoint<T>
{

	public abstract int getIndex();
	public abstract int getX();
	public abstract int getY();

	public boolean isValid();
	public void setValid(boolean validPoint);
	
	public T getData();
	public void setData(T data);

}