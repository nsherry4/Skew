package skew.views;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterColorMapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.DirectionVector;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.core.viewer.modes.views.Summary;
import skew.models.orientation.IOrientationMatrix;
import skew.views.misorientation.OrientationSubView;
import ca.sciencestudio.process.xrd.util.Orientation;
import fava.functionable.FList;


public class OrientationView extends MapView
{

	protected RasterColorMapPainter orientationPainter;
	private ISkewGrid<IOrientationMatrix> omModel;
	
	public OrientationView(ISkewGrid<IOrientationMatrix> omModel)
	{
		super("Orientation");
		this.omModel = omModel;
		orientationPainter = new RasterColorMapPainter();
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
		s.addHeader(
				"Direction [001]", 
				"Tilt [001]",
				"Direction [110]", 
				"Tilt [110]",
				"Direction [111]",
				"Tilt [111]"
			);

		
		ISkewPoint<IOrientationMatrix> omPoint = omModel.getPoint(x, y);
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
	public List<MapPainter> getPainters(MapSubView subview, float maximum)
	{		
		if (isUpdateRequired())
		{
			setupPainters(subview);
			setUpdateComplete();
		}
		return new FList<MapPainter>(orientationPainter);
	}

	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
		return new FList<AxisPainter>();
	}
	
	private void setupPainters(MapSubView subview)
	{
		List<Color> pixelColours = new FList<Color>(omModel.getWidth() * omModel.getHeight());
		for (int i = 0; i < omModel.getWidth() * omModel.getHeight(); i++){ pixelColours.add(backgroundGray); }
		
		Color c;
		for (ISkewPoint<IOrientationMatrix> omPoint : omModel.getPoints())
		{
			if (!omPoint.isValid())
			{
				c = backgroundGray;
			}
			else
			{
				DirectionVector dv = omPoint.getData().getOrientationVectors().get(subview.getIndex());
				c = Orientation.directionToColor(dv, 1f);
			}
			pixelColours.set(omPoint.getIndex(), c);
		}
		
		orientationPainter.setPixels(pixelColours);
	}


	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {}
	
}
