package skew.models.grain;

import fava.datatypes.Maybe;

public class GrainPixel
{
	public Maybe<Integer> grainIndex;
	public Grain grain;
	public Maybe<Double> intraGrainMisorientation;
	
	public GrainPixel() {
		grainIndex = new Maybe<>();
		intraGrainMisorientation = new Maybe<>();
	}
	
}
