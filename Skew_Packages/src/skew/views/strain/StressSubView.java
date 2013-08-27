package skew.views.strain;

import skew.core.viewer.modes.subviews.MapSubView;

public class StressSubView implements MapSubView
{
	int index;
	
	public StressSubView(int index)
	{
		this.index = index;
	}
	
	@Override
	public String toString()
	{
		if (index == 0) return "XX";
		if (index == 1) return "YY";
		if (index == 2) return "ZZ";
		if (index == 3) return "XY";
		if (index == 4) return "XZ";
		if (index == 5) return "YZ";
		if (index == 6) return "Von Mises";
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
