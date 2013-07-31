package skew.core.datasource.impl;

import java.util.ArrayList;
import java.util.List;

import scitypes.Coord;
import skew.core.datasource.IDataSource;
import skew.core.model.ISkewPoint;
import skew.core.model.impl.SkewPoint;
import fava.signatures.FnGet;

public abstract class DataSource implements IDataSource {

	public static <S> List<ISkewPoint<S>> getEmptyPoints(Coord<Integer> mapsize, FnGet<S> create)
	{
		List<ISkewPoint<S>> points = new ArrayList<ISkewPoint<S>>();
		for (int i = 0; i < mapsize.x * mapsize.y; i++)
		{
			points.add(new SkewPoint<S>(i % mapsize.x, i / mapsize.x, i, create.f()));
		}
		return points;
	}
	
}
