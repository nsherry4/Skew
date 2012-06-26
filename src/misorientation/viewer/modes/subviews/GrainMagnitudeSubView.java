package misorientation.viewer.modes.subviews;

public class GrainMagnitudeSubView implements MisorientationSubView
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
