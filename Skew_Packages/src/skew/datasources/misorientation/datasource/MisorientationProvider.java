package skew.datasources.misorientation.datasource;

import skew.core.model.ISkewGrid;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;
import skew.views.misorientation.ThresholdSecondaryView;
import autodialog.model.Parameter;
import autodialog.view.editors.BooleanEditor;
import autodialog.view.editors.DoubleEditor;

public class MisorientationProvider
{

	public ISkewGrid<GrainPixel> grainModel;
	public ISkewGrid<MisAngle> misModel;
	public ISkewGrid<IOrientationMatrix> omModel;
	
	public Parameter<Double> boundaryParameter = new Parameter<Double>("Grain Boundary Angle", new DoubleEditor(), 5.);
	public Parameter<Boolean> showBoundariesParameter = new Parameter<Boolean>("Show Grain Boundaries", new BooleanEditor(), true);
	
	public ThresholdSecondaryView grainView;
	
	public ThresholdSecondaryView grainView() {
		double angle = boundaryParameter.getValue();
		if (grainView == null) grainView = new ThresholdSecondaryView(misModel, grainModel, angle);
		grainView.setShowGrainBoundary(showBoundariesParameter.getValue());
		return grainView;
	}
	
}
