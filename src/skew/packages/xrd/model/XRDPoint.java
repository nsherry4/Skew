package skew.packages.xrd.model;

import skew.models.Misorientation.MisAnglePoint;
import skew.models.XRDStrain.IXRDStrainPoint;
import skew.models.XRDStrain.XRDStrainPoint;

public class XRDPoint extends MisAnglePoint implements IXRDStrainPoint
{

	public boolean hasStrainData = false;
	public IXRDStrainPoint str;
	public double pixdev = 0;
	
	public XRDPoint(int x, int y, int index)
	{
		super(x, y, index);
		str = new XRDStrainPoint(x, y, index);
	}

	@Override
	public double[] strain() {
		return str.strain();
	}

	@Override
	public double[] stress() {
		return str.stress();
	}

	@Override
	public boolean getHasStrainData() {
		return str.getHasStrainData();
	}

	@Override
	public void setHasStrainData(boolean b) {
		str.setHasStrainData(b);
	}

	

}
