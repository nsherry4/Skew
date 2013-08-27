package skew.datasources.misorientation.datasource.calculation.magnitude;

import java.util.List;

import scitypes.DirectionVector;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;
import ca.sciencestudio.process.xrd.util.Orientation;


public class OrientationMap
{
	public static void calculateOrientation(ISkewGrid<MisAngle> misGrid, ISkewGrid<IOrientationMatrix> omGrid)
	{
		for (ISkewPoint<MisAngle> misPoint : misGrid.getPoints())
		{
			if (misPoint == null) continue;
			
			ISkewPoint<IOrientationMatrix> omPoint = omGrid.getPoint(misPoint.getX(), misPoint.getY());
			if (omPoint == null) continue;
			
			IOrientationMatrix omData = omPoint.getData();
			MisAngle misData = misPoint.getData();
			if (omData == null || misData == null) continue;
			
			List<DirectionVector> vectors = Orientation.calculatePolarCoordinates(omData.getInverse());
			omData.setOrientationVectors(vectors);
		}
	}
}
