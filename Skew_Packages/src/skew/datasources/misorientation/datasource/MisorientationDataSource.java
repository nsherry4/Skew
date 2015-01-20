package skew.datasources.misorientation.datasource;

import java.util.List;

import fava.functionable.FList;
import autodialog.model.Parameter;
import autodialog.view.editors.BooleanEditor;
import autodialog.view.editors.DoubleEditor;
import plural.executor.map.MapExecutor;
import scitypes.Coord;
import skew.core.datasource.DataSource;
import skew.core.datasource.ExecutorDataSource;
import skew.core.model.ISkewGrid;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;
import skew.models.orientation.OrientationMatrix;
import skew.views.misorientation.ThresholdSecondaryView;

public abstract class MisorientationDataSource implements ExecutorDataSource
{

	public MisorientationProvider misdata = new MisorientationProvider();
	
	/**
	 * Loads the OrientationMatrix model from files 
	 * @param filenames
	 * @return
	 */

	
	public List<Parameter<?>> getLoadParameters(){
		return new FList<Parameter<?>>(misdata.boundaryParameter);
	}
	
	@Override
	public String getLoadParametersInformation() {
		return null;
	}

	@Override
	public List<Parameter<?>> getRuntimeParameters() {
		return new FList<Parameter<?>>(misdata.showBoundariesParameter);
	}
	


	@Override
	public void recalculate() {
		misdata.grainView.setShowGrainBoundary(misdata.showBoundariesParameter.getValue());
	}
	
	public void createModels(Coord<Integer> mapSize)
	{
		misdata.misModel = DataSource.createGrid(mapSize, MisAngle::new);
		misdata.grainModel = DataSource.createGrid(mapSize, GrainPixel::new);
		misdata.omModel = DataSource.createGrid(mapSize, OrientationMatrix::new);
	}

}
