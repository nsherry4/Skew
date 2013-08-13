package skew.packages.misorientation.view.grain;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterColorMapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.DirectionVector;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.models.Misorientation.MisAngle;
import skew.models.Misorientation.MisAngleGrid;
import skew.packages.misorientation.subview.OrientationSubView;
import ca.sciencestudio.process.xrd.util.Orientation;
import fava.functionable.FList;


public class OrientationView extends MapView
{

	protected RasterColorMapPainter orientationPainter;
	private MisAngleGrid model;
	
	public OrientationView(MisAngleGrid model)
	{
		super();
		this.model = model;
		orientationPainter = new RasterColorMapPainter();
	}
	
	@Override
	public String toString()
	{
		return "Orientation";
	}

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(int x, int y)
	{
		return "";
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
		List<Color> pixelColours = new FList<Color>(model.getWidth() * model.getHeight());
		for (int i = 0; i < model.getWidth() * model.getHeight(); i++){ pixelColours.add(backgroundGray); }
		
		Color c;
		for (ISkewPoint<MisAngle> point : model.getBackingList())
		{
			if (!point.getData().orientation.getHasOMData())
			{
				c = backgroundGray;
			}
			else
			{
				DirectionVector dv = point.getData().orientationVectors.get(subview.getIndex());
				c = Orientation.directionToColor(dv, 1f);
			}
			pixelColours.set(point.getIndex(), c);
		}
		
		orientationPainter.setPixels(pixelColours);
	}

	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException
	{		
		writer.write("index, x, y, distance [001], direction [001], distance [110], direction [110], distance [111], direction [111]\n");
		
		for (ISkewPoint<MisAngle> point : model.getBackingList())
		{
			MisAngle data = point.getData();
			if (data.orientationVectors == null)
			{
				writer.write(point.getIndex() + ", " + point.getX() + ", " + point.getY() + ", " + "-, -, -, -, -, -\n");
			}
			else
			{
				DirectionVector dv1 = data.orientationVectors.get(0);
				DirectionVector dv2 = data.orientationVectors.get(1);
				DirectionVector dv3 = data.orientationVectors.get(2);
				
				writer.write(
						point.getIndex() + ", " + 
						point.getX() + ", " + 
						point.getY() + ", " +
						data.grain + ", " + 
						
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
	
	@Override
	public boolean canWriteData()
	{
		return true;
	}
	
	
}
