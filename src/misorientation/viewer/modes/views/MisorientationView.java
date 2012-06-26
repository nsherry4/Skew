package misorientation.viewer.modes.views;

import java.util.List;

import javax.swing.SpinnerModel;

import fava.functionable.FList;

import scitypes.SigDigits;

import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;
import misorientation.viewer.modes.subviews.MisorientationSubView;

public abstract class MisorientationView
{
	
	public abstract SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView);
	public abstract String getSummaryText(MisAnglePoint point, MisAngleGrid data);
	public abstract boolean hasNumericScale();
	public abstract boolean hasSublist();
	public abstract List<MisorientationSubView> getSubList();

	protected static String formatGrainValue(double value)
	{
		if (value < 0) return "None";
		return "#" + (int)value;
	}
	
	protected static String formatMisorientationValue(double value)
	{
		String valString;
		valString = SigDigits.roundFloatTo((float)value, 3);
		if (value < 0) valString = "Boundary";
		
		return valString;
	}
	
	public static List<MisorientationView> getViews()
	{
		return new FList<MisorientationView>(
				new LocalView(),
				new InterGrainView(),
				new GrainMagnitudeView(),
				new OrientationView(),
				new GrainLabelView()
			);
	}
	
}
