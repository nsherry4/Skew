package skew.datasources.misorientation.datasource;

import java.util.List;

import fava.functionable.FList;
import fava.signatures.FnGet;
import autodialog.model.Parameter;
import autodialog.view.editors.BooleanEditor;
import autodialog.view.editors.DoubleEditor;
import plural.executor.map.MapExecutor;
import scitypes.Coord;
import skew.core.datasource.DataSource;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;
import skew.models.orientation.OrientationMatrix;
import skew.views.misorientation.ThresholdSecondaryView;

public abstract class MisorientationDataSource extends DataSource
{

	public ISkewGrid<GrainPixel> grainModel;
	public ISkewGrid<MisAngle> misModel;
	public ISkewGrid<IOrientationMatrix> omModel;
	
	protected Parameter<Double> boundaryParameter = new Parameter<Double>("Grain Boundary Angle", new DoubleEditor(), 5.);
	protected Parameter<Boolean> showBoundariesParameter = new Parameter<Boolean>("Show Grain Boundaries", new BooleanEditor(), true);
	
	private ThresholdSecondaryView grainView;
	
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
		return new FList<Parameter<?>>(showBoundariesParameter);
	}
	
	
	protected ThresholdSecondaryView grainView() {
		double angle = boundaryParameter.getValue();
		if (grainView == null) grainView = new ThresholdSecondaryView(misModel, grainModel, angle);
		grainView.setShowGrainBoundary(showBoundariesParameter.getValue());
		return grainView;
	}
	

	@Override
	public void recalculate() {
		grainView.setShowGrainBoundary(showBoundariesParameter.getValue());
	}
	
	public void createModels(Coord<Integer> mapSize)
	{
		//Create MisAngleGrid
		misModel = DataSource.getEmptyGrid(mapSize, new FnGet<MisAngle>(){
			@Override public MisAngle f() { return new MisAngle(); }});
		
		
		//Create Grain Grid
		grainModel = DataSource.getEmptyGrid(mapSize, new FnGet<GrainPixel>(){
			@Override public GrainPixel f() { return new GrainPixel(); }});
		
		
		//Create OrientationMatrix Grid
		omModel = DataSource.getEmptyGrid(mapSize, new FnGet<IOrientationMatrix>() {
			@Override public IOrientationMatrix f() { return new OrientationMatrix(); }});
	}

}
