package misorientation.viewer.modes.subviews;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import misorientation.model.MisAngleGrid;

public class IntraGrainSubView implements MisorientationSubView
{
	private int index;

	public IntraGrainSubView(int index)
	{
		this.index = index;
	}
	
	public String toString()
	{
		if (index == 0) return "Relative to Grain";
		if (index == 1) return "Absolute";
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
	
	public SpinnerModel getSpinnerModel(MisAngleGrid data)
	{
		if (index == 0) return null;
		if (index == 1) return new SpinnerNumberModel(2.0, 0.1, 180.0, 0.1);
		
		return null;
	}
	
}
