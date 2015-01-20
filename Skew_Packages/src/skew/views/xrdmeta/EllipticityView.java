package skew.views.xrdmeta;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fava.functionable.FList;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.DirectionVector;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.RasterColorMapView;
import skew.core.viewer.modes.views.Summary;

public class EllipticityView extends RasterColorMapView<DirectionVector>
{
	
	

	public EllipticityView(ISkewGrid<DirectionVector> model) {
		super("Spot Ellipticity", model);	
	}

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView) {
		return new SpinnerNumberModel(5.0, 0.1, 1000.0, 0.1);
	}

	@Override
	public boolean hasSublist() {
		return false;
	}

	@Override
	public List<MapSubView> getSubList() {
		return null;
	}

	@Override
	public List<Summary> getPointSummary(int x, int y) {
		
		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary(getTitle(), "Magnitude", "Angle");
		summaries.add(s);
		
		ISkewPoint<DirectionVector> point = model.getPoint(x, y);
		if (! point.isValid()) return summaries;
		
		DirectionVector ellip = point.getData();
		
		s.addValue("Magnitude", fmt(ellip.getDistance()) + " Pixels");
		s.addValue("Angle", fmt(ellip.getDirection()) + " Degrees");
		
		return summaries;
	}

	@Override
	public List<Summary> getMapSummary() {
		return new ArrayList<>();
	}

	@Override
	public float getMaximumIntensity(MapSubView subview) {
		// TODO Auto-generated method stub
		return 1;
	}
	
	
	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
		return new FList<AxisPainter>();
	}

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {}



	@Override
	protected Color colorForPoint(ISkewPoint<DirectionVector> point, MapSubView subview, float maximum) {
		DirectionVector ellip = point.getData();
		float saturation = ellip.getDistance() / maximum;
		if (saturation > 1.0f) { saturation = 1.0f; } 
		return new Color(Color.HSBtoRGB(ellip.getDirection() / 180.0f, 0.9f, saturation*0.8f + 0.2f));
		//return new Color(Color.HSBtoRGB(0.597f, 0.7f, saturation*0.8f + 0.2f));
	}
	
}
