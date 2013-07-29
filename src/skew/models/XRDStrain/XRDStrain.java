package skew.models.XRDStrain;


public class XRDStrain implements IXRDStrain {


	private double[] strainData = new double[7];
	private double[] stressData = new double[7];
	
	@Override
	public double[] strain() {
		return strainData;
	}
	@Override
	public double[] stress() {
		return stressData;
	}

	
}
