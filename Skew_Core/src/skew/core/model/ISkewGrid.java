package skew.core.model;

import java.util.List;


public interface ISkewGrid<T> extends IModel, Iterable<ISkewPoint<T>>
{

	int size();

	/**
	 * Returns point given another point (possibly from a different model), null if point is out of bounds
	 */
	ISkewPoint<T> getPoint(ISkewPoint<?> point);
	
	/**
	 * Returns point for index, null if point is out of bounds
	 */
	ISkewPoint<T> getPoint(int position);
	
	/**
	 * Returns point for coordinates, null if point is out of bounds
	 */
	ISkewPoint<T> getPoint(int x, int y);
	
	/**
	 * Returns a list of valid all points in the grid
	 */
	List<ISkewPoint<T>> getPoints();
	
	
	
	/**
	 * Returns data for point, null if point does not exist
	 */
	T getData(ISkewPoint<?> point);
	
	/**
	 * Returns data for index, null if point does not exist
	 */
	T getData(int position);
	
	/**
	 * Returns data for coordinates, null if point does not exist
	 */
	T getData(int x, int y);

	
}