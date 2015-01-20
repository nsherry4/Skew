package skew.views;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.DirectionVector;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.RasterColorMapView;
import skew.core.viewer.modes.views.Summary;
import skew.models.orientation.IOrientationMatrix;
import skew.views.misorientation.OrientationSubView;
import ca.sciencestudio.process.xrd.util.Orientation;
import fava.functionable.FList;


public class OrientationView extends RasterColorMapView<IOrientationMatrix>
{

	
	public OrientationView(ISkewGrid<IOrientationMatrix> model)
	{
		super("Orientation", model);
	}
	

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return null;
	}


	@Override
	public List<Summary> getMapSummary() {
		return new ArrayList<>();
	}
	
	
	@Override
	public List<Summary> getPointSummary(int x, int y)
	{
		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary("Orientation");
		summaries.add(s);
		s.addCanonicalKeys(
				"Direction [001]", 
				"Tilt [001]",
				"Direction [110]", 
				"Tilt [110]",
				"Direction [111]",
				"Tilt [111]"
			);

		
		ISkewPoint<IOrientationMatrix> omPoint = model.getPoint(x, y);
		IOrientationMatrix omData = omPoint.getData();
		
		if (!omPoint.isValid()) return summaries;
		

		
		DirectionVector dv1 = omData.getOrientationVectors().get(0);
		DirectionVector dv2 = omData.getOrientationVectors().get(1);
		DirectionVector dv3 = omData.getOrientationVectors().get(2);

		s.addValue("Direction [001]", formatOMDirectionValue(dv1.getDirection() * 360));
		s.addValue("Tilt [001]",  formatOMTiltValue(dv1.getDistance() * 100));
		
		s.addValue("Direction [110]", formatOMDirectionValue(dv2.getDirection() * 360));
		s.addValue("Tilt [110]",  formatOMTiltValue(dv2.getDistance() * 100));
		
		s.addValue("Direction [111]", formatOMDirectionValue(dv3.getDirection() * 360));
		s.addValue("Tilt [111]",  formatOMTiltValue(dv3.getDistance() * 100));
		
		return summaries;
		
	}

	@Override
	public boolean hasSublist()
	{
		return true;
	}

	@Override
	public List<MapSubView> getSubList()
	{
		return new ArrayList<MapSubView>(Arrays.asList(new OrientationSubView[]{
				new OrientationSubView(0),
				new OrientationSubView(1),
				new OrientationSubView(2)
			}));
	}

	@Override
	public float getMaximumIntensity(MapSubView subview)
	{
		return 0;
	}

	
	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
		return new FList<AxisPainter>();
	}
	
	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {}


	@Override
	protected Color colorForPoint(ISkewPoint<IOrientationMatrix> point, MapSubView subview, float maximum) {
		DirectionVector dv = point.getData().getOrientationVectors().get(subview.getIndex());
		return Orientation.directionToColor(dv, 1f);
	}
	
}
