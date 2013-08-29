package skew.core.model;

import java.util.List;


public interface ISkewGrid<T> extends IModel, Iterable<ISkewPoint<T>>
{

	int size();

	ISkewPoint<T> getPoint(ISkewPoint<?> point);
	ISkewPoint<T> getPoint(int position);
	ISkewPoint<T> getPoint(int x, int y);
	List<ISkewPoint<T>> getPoints();
	
	T getData(ISkewPoint<?> point);
	T getData(int position);
	T getData(int x, int y);

	
}