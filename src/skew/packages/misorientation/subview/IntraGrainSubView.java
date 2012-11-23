package skew.packages.misorientation.subview;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;


public class IntraGrainSubView implements MapSubView
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
	
	public SpinnerModel getSpinnerModel(ISkewGrid data)
	{
		if (index == 0) return null;
		if (index == 1) return new SpinnerNumberModel(2.0, 0.1, 180.0, 0.1);
		
		return null;
	}
	
}
