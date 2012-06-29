package skew.packages.misorientation.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import plural.executor.map.MapExecutor;
import scitypes.Coord;
import skew.core.datasource.DataSource;
import skew.core.model.SkewGrid;
import skew.packages.misorientation.datasource.calculation.misorientation.Calculation;
import skew.packages.misorientation.model.MisAngleGrid;
import skew.packages.misorientation.model.MisAnglePoint;

public abstract class MisorientationDataSource extends DataSource
{

	public abstract MapExecutor<String, String> loadPoints(final MisAngleGrid<? extends MisAnglePoint> data, List<String> filenames);
	
	public ExecutorSet<SkewGrid> calculate(List<String> filenames, Coord<Integer> mapsize)
	{
		return Calculation.calculate(filenames, this, mapsize);
	}
	
	public MisAnglePoint createPoint(int index, int x, int y)
	{
		return new MisAnglePoint(index, x, y);
	}
	
}
