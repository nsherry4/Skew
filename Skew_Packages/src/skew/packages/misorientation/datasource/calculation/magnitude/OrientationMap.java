package skew.packages.misorientation.datasource.calculation.magnitude;

import java.util.List;

import scitypes.DirectionVector;
import skew.core.model.ISkewPoint;
import skew.models.Misorientation.MisAngle;
import skew.models.Misorientation.MisAngleGrid;
import ca.sciencestudio.process.xrd.util.Orientation;


public class OrientationMap
{
	public static void calculateOrientation(MisAngleGrid grid)
	{
		for (ISkewPoint<MisAngle> p : grid.getPoints())
		{
			if (p == null) continue;
			MisAngle data = p.getData();
			if (data.orientation == null) continue;
			
			List<DirectionVector> vectors = Orientation.calculatePolarCoordinates(data.orientation.getInverse());
			data.orientationVectors = vectors;
		}
	}
}
