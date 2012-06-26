package misorientation.viewer.modes.views;

import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;
import misorientation.viewer.modes.subviews.MisorientationSubView;

public class LocalView extends MisorientationView
{
	public String toString(){ return "Local Misorientation"; }

	@Override
	public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
	{
		return new SpinnerNumberModel(2, 0.0, 180.0, 0.1);
	}

	@Override
	public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
	{

		String avg = formatMisorientationValue(point.average);
		String east = formatMisorientationValue(point.east);
		String south = formatMisorientationValue(point.south);
		String west = formatMisorientationValue(point.west);
		String north = formatMisorientationValue(point.north);
		
		return ""+
			"Angles - " + 
			"Average: " + avg + 
			", \u2191" + north +
			", \u2192" + east + 
			", \u2193" + south +
			", \u2190" + west;
	}

	@Override
	public boolean hasNumericScale()
	{
		return true;
	}

	@Override
	public boolean hasSublist()
	{
		return false;
	}

	@Override
	public List<MisorientationSubView> getSubList()
	{
		return null;
	}
}
