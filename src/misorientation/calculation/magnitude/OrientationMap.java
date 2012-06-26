package misorientation.calculation.magnitude;

import java.util.List;

import scitypes.DirectionVector;

import ca.sciencestudio.process.xrd.calculation.Orientation;
import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;

public class OrientationMap
{
	public static void calculateOrientation(MisAngleGrid data)
	{
		for (MisAnglePoint p : data.getBackingList())
		{
			if (p == null || p.orientation == null) continue;
			List<DirectionVector> vectors = Orientation.calculatePolarCoordinates(p.orientation.inverse);
			p.orientationVectors = vectors;
		}
	}
}
