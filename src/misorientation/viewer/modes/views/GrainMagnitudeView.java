package misorientation.viewer.modes.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import misorientation.model.Grain;
import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;
import misorientation.viewer.modes.subviews.GrainMagnitudeSubView;
import misorientation.viewer.modes.subviews.MisorientationSubView;

public class GrainMagnitudeView extends MisorientationView
{
	public String toString(){ return "Grain Magnitude"; }



	@Override
	public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
	{
		GrainMagnitudeSubView gms = (GrainMagnitudeSubView)subView;
		float grainVal;
		float maxVal = 0;
		for (Grain g : data.grains)
		{
			grainVal = (float) gms.select(new double[]{g.magMin, g.magMax, g.magAvg});
			maxVal = Math.max(grainVal, maxVal);
		}
		
		maxVal = (int)(maxVal * 10);
		maxVal /= 10f;
		return new SpinnerNumberModel(maxVal, 0.0, 180.0, 0.1);
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
		
		String mag = formatMisorientationValue(g.magMin);
		return "Grain: " + grain + ", Magnitude: " + mag;
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
		return new ArrayList<MisorientationSubView>(Arrays.asList(new GrainMagnitudeSubView[]{
			new GrainMagnitudeSubView(0),
			new GrainMagnitudeSubView(1),
			new GrainMagnitudeSubView(2)
		}));
	}

}
