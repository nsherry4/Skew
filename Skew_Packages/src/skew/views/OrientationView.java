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
	public List<Summary> getSummary(int x, int y)
	{
		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary("Orientation");
		summaries.add(s);
		s.addHeader("[001] Distance", "[001] Direction", "[110] Distance", "[110] Direction", "[111] Distance", "[111] Direction");

		
		ISkewPoint<IOrientationMatrix> omPoint = omModel.getPoint(x, y);
		IOrientationMatrix omData = omPoint.getData();
		
		if (!omPoint.isValid()) return summaries;
		

		
		DirectionVector dv1 = omData.getOrientationVectors().get(0);
		DirectionVector dv2 = omData.getOrientationVectors().get(1);
		DirectionVector dv3 = omData.getOrientationVectors().get(2);
		
		s.addValue("[001] Distance",  fmt(dv1.getDistance()));
		s.addValue("[001] Direction", fmt(dv1.getDirection()));
		s.addValue("[110] Distance",  fmt(+dv2.getDistance()));
		s.addValue("[110] Direction", fmt(dv2.getDirection()));
		s.addValue("[111] Distance",  fmt(dv3.getDistance()));
		s.addValue("[111] Direction", fmt(dv3.getDirection()));
		
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
