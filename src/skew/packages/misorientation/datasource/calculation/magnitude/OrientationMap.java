package skew.packages.misorientation.datasource.calculation.magnitude;

import java.util.List;

import ca.sciencestudio.process.xrd.util.Orientation;
import scitypes.DirectionVector;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;


public class OrientationMap
{
	public static void calculateOrientation(MisAngleGrid<? extends MisAnglePoint> data)
	{
		for (MisAnglePoint p : data.getBackingList())
		{
			if (p == null || p.orientation == null) continue;
			List<DirectionVector> vectors = Orientation.calculatePolarCoordinates(p.orientation.getInverse());
			p.orientationVectors = vectors;
		}
	}
}
