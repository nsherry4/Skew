package skew.packages.misorientation.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import plural.executor.map.MapExecutor;
import scitypes.Coord;
import skew.core.datasource.impl.DataSource;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewPoint;
import skew.core.model.impl.SkewPoint;
import skew.models.Misorientation.MisAngle;
import skew.models.Misorientation.MisAngleGrid;
import skew.packages.misorientation.datasource.calculation.misorientation.Calculation;

public abstract class MisorientationDataSource extends DataSource
{

	protected MisAngleGrid misModel;
	public abstract MapExecutor<String, String> loadPoints(final MisAngleGrid data, List<String> filenames);
	
	public ExecutorSet<ISkewDataset> calculate(List<String> filenames, Coord<Integer> mapsize)
	{
		return Calculation.calculate(filenames, this, mapsize);
	}
	
	public ISkewPoint<MisAngle> createPoint(int index, int x, int y)
	{
		return new SkewPoint<MisAngle>(x, y, index, new MisAngle());
	}
	
}
