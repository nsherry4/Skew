package skew.models.XRDStrain;

import skew.core.model.impl.SkewPoint;


public class XRDStrainPoint extends SkewPoint implements IXRDStrainPoint {


	private double[] strainData = new double[7];
	private double[] stressData = new double[7];
	private boolean hasData = false;
	
	public XRDStrainPoint(int x, int y, int index) {
		super(x, y, index);
	}
	
	@Override
	public double[] strain() {
		return strainData;
	}
	@Override
	public double[] stress() {
		return stressData;
	}

	@Override
	public boolean getHasStrainData() {
		return hasData;
	}

	@Override
	public void setHasStrainData(boolean b) {
		hasData = b;
	}
	
}
