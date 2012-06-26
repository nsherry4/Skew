package misorientation.viewer.modes.views;

import java.util.List;

import javax.swing.SpinnerModel;

import misorientation.model.Grain;
import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;
import misorientation.viewer.modes.subviews.MisorientationSubView;

public class GrainLabelView extends MisorientationView
{
	
	public String toString(){ return "Grain Labels"; }

	@Override
	public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
	{
		String grain = formatGrainValue(point.grain);
		String result = "Grain: " + grain;
		
		Grain g;
		try { g = data.grains.get(point.grain); }
		catch (ArrayIndexOutOfBoundsException e) { return result; }
		if (g == null) return result;
		
		result += ", Size: " + g.points.size() + " pixels";
		return result;
		
	}

	@Override
	public boolean hasNumericScale()
	{
		return false;
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
