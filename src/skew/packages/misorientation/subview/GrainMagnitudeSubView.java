package skew.packages.misorientation.subview;

import skew.core.viewer.modes.subviews.MapSubView;

public class GrainMagnitudeSubView implements MapSubView
{

	public int index;
	
	public GrainMagnitudeSubView(int index)
	{
		this.index = index;
	}
	
	public String toString()
	{
		if (index == 0) return "Min";
		if (index == 1) return "Max";
		if (index == 2) return "Average";
		return "";
	}

	@Override
	public int getIndex()
	{
		return index;
	}

	@Override
	public double select(double[] options)
	{
		return options[index];
	}

	
	
}
