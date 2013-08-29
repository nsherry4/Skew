package skew.datasources.misorientation.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import plural.executor.map.MapExecutor;
import scitypes.Coord;
import skew.core.datasource.DataSource;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.datasources.misorientation.datasource.calculation.misorientation.Calculation;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;

public abstract class MisorientationDataSource extends DataSource
{

	protected ISkewGrid<GrainPixel> grainModel;
	protected ISkewGrid<MisAngle> misModel;
	protected ISkewGrid<IOrientationMatrix> omModel;
	
	public void setModels(ISkewGrid<GrainPixel> grainModel, ISkewGrid<MisAngle> misModel, ISkewGrid<IOrientationMatrix> omGrid)
	{
		this.grainModel = grainModel;
		this.omModel = omGrid;
		this.misModel = misModel;
	}
	
	/**
	 * Loads the OrientationMatrix model from files 
	 * @param filenames
	 * @return
	 */
	public abstract MapExecutor<String, String> loadPoints(List<String> filenames);
	
	
	/**
	 * Loads the dataset from files, and calculating any other requried data
	 */
	public ExecutorSet<ISkewDataset> loadDataset(List<String> filenames, Coord<Integer> mapsize)
	{
		return Calculation.calculate(filenames, this, mapsize);
	}
		
}
