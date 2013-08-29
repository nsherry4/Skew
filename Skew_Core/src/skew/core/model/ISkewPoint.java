package skew.core.model;



public interface ISkewPoint<T>
{

	int getIndex();
	int getX();
	int getY();

	boolean isValid();
	void setValid(boolean validPoint);
	
	T getData();
	void setData(T data);

}