package misorientation.viewer.modes.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;

import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;
import misorientation.viewer.modes.subviews.OrientationSubView;
import misorientation.viewer.modes.subviews.MisorientationSubView;

public class OrientationView extends MisorientationView
{

	@Override
	public String toString()
	{
		return "Orientation";
	}

	@Override
	public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
	{
		return "";
	}

	@Override
	public boolean hasNumericScale()
	{
		return false;
	}

	@Override
	public boolean hasSublist()
	{
		return true;
	}

	@Override
	public List<MisorientationSubView> getSubList()
	{
		return new ArrayList<MisorientationSubView>(Arrays.asList(new OrientationSubView[]{
				new OrientationSubView(0),
				new OrientationSubView(1),
				new OrientationSubView(2)
			}));
	}
}
