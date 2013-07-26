package skew.packages.misorientation.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import plural.executor.map.MapExecutor;
import scitypes.Coord;
import skew.core.datasource.IDataSource;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;
import skew.packages.misorientation.datasource.calculation.misorientation.Calculation;

public abstract class MisorientationDataSource implements IDataSource
{

	protected MisAngleGrid<? extends MisAnglePoint> misModel;
	public abstract MapExecutor<String, String> loadPoints(final MisAngleGrid<? extends MisAnglePoint> data, List<String> filenames);
	
	public ExecutorSet<ISkewDataset> calculate(List<String> filenames, Coord<Integer> mapsize)
	{
		return Calculation.calculate(filenames, this, mapsize);
	}
	
	public MisAnglePoint createPoint(int index, int x, int y)
	{
		return new MisAnglePoint(x, y, index);
	}
	
}