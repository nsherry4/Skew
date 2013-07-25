package skew.models.XRDStrain;

import skew.core.model.ISkewPoint;

public interface IXRDStrainPoint extends ISkewPoint
{
	public abstract double[] strain();
	public abstract double[] stress();
	
	public boolean getHasStrainData();
	public void setHasStrainData(boolean b);
	
}