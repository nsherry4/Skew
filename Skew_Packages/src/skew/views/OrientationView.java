package skew.views;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterColorMapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.DirectionVector;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
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
	public Map<String, String> getSummaryData(int x, int y)
	{
		Map<String, String> values = new LinkedHashMap<>();
		ISkewPoint<IOrientationMatrix> omPoint = omModel.getPoint(x, y);
		IOrientationMatrix omData = omPoint.getData();
		
		if (!omPoint.isValid()) return values;
		
		DirectionVector dv1 = omData.getOrientationVectors().get(0);
		DirectionVector dv2 = omData.getOrientationVectors().get(1);
		DirectionVector dv3 = omData.getOrientationVectors().get(2);
		
		values.put("[001] Distance", ""+dv1.getDistance());
		values.put("[001] Direction", ""+dv1.getDirection());
		values.put("[110] Distance", ""+dv2.getDistance());
		values.put("[110] Direction", ""+dv2.getDirection());
		values.put("[111] Distance", ""+dv3.getDistance());
		values.put("[111] Direction", ""+dv3.getDirection());
		
		return values;
		
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

	/*
	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException
	{		
		writer.write("index, x, y, distance [001], direction [001], distance [110], direction [110], distance [111], direction [111]\n");
		
		for (ISkewPoint<IOrientationMatrix> omPoint : omModel.getPoints())
		{		
			IOrientationMatrix omData = omPoint.getData();
			if (omData.getOrientationVectors() == null)
			{
				writer.write(omPoint.getIndex() + ", " + omPoint.getX() + ", " + omPoint.getY() + ", " + "-, -, -, -, -, -\n");
			}
			else
			{
				DirectionVector dv1 = omData.getOrientationVectors().get(0);
				DirectionVector dv2 = omData.getOrientationVectors().get(1);
				DirectionVector dv3 = omData.getOrientationVectors().get(2);
				
				writer.write(
						omPoint.getIndex() + ", " + 
						omPoint.getX() + ", " + 
						omPoint.getY() + ", " +
						(misModel == null ? "" : misModel.getData(omPoint.getIndex()).grain) + ", " + 
						
						fmt(dv1.getDistance()) + ", " + 
						fmt(dv1.getDirection()) + ", " +
												
						fmt(dv2.getDistance()) + ", " + 
						fmt(dv2.getDirection()) + ", " +
						
						fmt(dv3.getDistance()) + ", " + 
						fmt(dv3.getDirection()) +
						
						"\n"
					);
			}
		}
	}
	*/
	

	@Override
	public List<String> getSummaryHeaders() {
		return new FList<>("[001] Distance", "[001] Direction", "[110] Distance", "[110] Direction", "[111] Distance", "[111] Direction");
	}
	
	
}
