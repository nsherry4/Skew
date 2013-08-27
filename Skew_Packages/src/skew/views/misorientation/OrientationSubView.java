package skew.views.misorientation;

import skew.core.viewer.modes.subviews.MapSubView;

public class OrientationSubView implements MapSubView
{
	public int index;
	
	public OrientationSubView(int index)
	{
		this.index = index;
	}
	
	public String toString()
	{
		if (index == 0) return "[001]";
		if (index == 1) return "[110]";
		if (index == 2) return "[111]";
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
