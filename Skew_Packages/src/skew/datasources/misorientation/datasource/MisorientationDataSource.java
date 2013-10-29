package skew.datasources.misorientation.datasource;

import java.util.List;

import fava.functionable.FList;
import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
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
	
	private Parameter<Integer> boundaryParameter = new Parameter<Integer>("Grain Boundary Angle", new IntegerEditor(), 5);
	
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
	
	
	public List<Parameter<?>> getLoadParameters(){
		return new FList<Parameter<?>>(boundaryParameter);
	}
	
	@Override
	public String getLoadParametersInformation() {
		return null;
	}

	@Override
	public List<Parameter<?>> getRuntimeParameters() {
		return new FList<>();
	}
	

	@Override
	public void recalculate() {}
	
	/**
	 * Loads the dataset from files, and calculating any other requried data
	 */
	public ExecutorSet<ISkewDataset> loadDataset(List<String> filenames, Coord<Integer> mapsize)
	{
		//return Calculation.calculate(filenames, this, mapsize, boundaryParameter.getValue());
		return Calculation.calculate(filenames, this, mapsize);
	}
		
}
