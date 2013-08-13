package skew.packages.xrd.strain;

public class XRDStrainUtil {

	public static double vonMises(double[] a)
	{
		return (Math.sqrt(
						Math.pow(a[0] - a[3], 2) + 
						Math.pow(a[0] - a[5], 2) + 
						Math.pow(a[3] - a[5], 2) + 
						6 * (
								Math.pow(a[1], 2) + 
								Math.pow(a[2], 2) + 
								Math.pow(a[4], 2)
							)
						) 
						/ 
						Math.sqrt(2)
				);
	}
	
}
