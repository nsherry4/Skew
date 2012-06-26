package misorientation.viewer.modes.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;

import misorientation.model.Grain;
import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;
import misorientation.viewer.modes.subviews.IntraGrainSubView;
import misorientation.viewer.modes.subviews.MisorientationSubView;

public class InterGrainView extends MisorientationView
{


	public String toString(){ return "Intragrain Misorientation"; }

	@Override
	public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
	{
		IntraGrainSubView igv = (IntraGrainSubView)subView;
		return igv.getSpinnerModel(data);
	}

	@Override
	public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
	{
	
		String grain = formatGrainValue(point.grain);
		String result = "Grain :" + grain;
		
		Grain g;
		try { g = data.grains.get(point.grain); }
		catch (ArrayIndexOutOfBoundsException e) { return result; }			
		
		if (g == null) return result; 
		
		String mag = formatMisorientationValue(point.intraGrainMisorientation);
		return "Grain: " + grain + ", Misorientation: " + mag + "\u00B0";
		
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
		return new ArrayList<MisorientationSubView>(Arrays.asList(new IntraGrainSubView[]{
				new IntraGrainSubView(0),
				new IntraGrainSubView(1)
			}));
	}
	
}
