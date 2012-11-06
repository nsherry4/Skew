package skew.packages.xrd.model;

import skew.packages.misorientation.model.MisAnglePoint;

public class XRDPoint extends MisAnglePoint
{

	public boolean hasStrainData = false;
	public double[] strain = new double[7];
	public double[] stress = new double[7];
	
	public XRDPoint(int index, int x, int y)
	{
		super(index, x, y);
	}

}
