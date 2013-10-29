package skew.models.grain;

import com.google.common.base.Optional;


public class GrainPixel
{
	public Optional<Integer> grainIndex;
	public Grain grain;
	public Optional<Double> intraGrainMisorientation;
	
	public GrainPixel() {
		grainIndex = Optional.absent();
		intraGrainMisorientation = Optional.absent();
	}
	
}
