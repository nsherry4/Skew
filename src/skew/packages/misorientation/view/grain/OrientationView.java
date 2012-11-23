package skew.packages.misorientation.view.grain;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;

import ca.sciencestudio.process.xrd.util.Orientation;

import fava.functionable.FList;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterColorMapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.DirectionVector;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.packages.misorientation.model.MisAngleGrid;
import skew.packages.misorientation.model.MisAnglePoint;
import skew.packages.misorientation.subview.OrientationSubView;
import skew.packages.misorientation.view.MisorientationView;


public class OrientationView extends MisorientationView
{

	protected RasterColorMapPainter orientationPainter;
	
	public OrientationView()
	{
		orientationPainter = new RasterColorMapPainter();
	}
	
	@Override
	public String toString()
	{
		return "Orientation";
	}

	@Override
	public SpinnerModel scaleSpinnerModel(ISkewGrid data, MapSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(ISkewPoint skewpoint, ISkewGrid data)
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
	public float getMaximumIntensity(ISkewGrid data, MapSubView subview)
	{
		return 0;
	}

	
	@Override
	public List<MapPainter> getPainters(ISkewGrid skewdata, MapSubView subview, float maximum)
	{
		@SuppressWarnings("unchecked")
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		
		if (isUpdateRequired())
		{
			super.setData(data, subview);
			setupPainters(data, subview);
			setUpdateComplete();
		}
		return new FList<MapPainter>(orientationPainter, super.boundaryPainter, super.selectedGrainPainter);
	}

	@Override
	public List<AxisPainter> getAxisPainters(ISkewGrid data, MapSubView subview, float maxValue)
	{
		return new FList<AxisPainter>();
	}
	
	private void setupPainters(MisAngleGrid<MisAnglePoint> data, MapSubView subview)
	{
		List<Color> pixelColours = new FList<Color>(data.getWidth() * data.getHeight());
		for (int i = 0; i < data.getWidth() * data.getHeight(); i++){ pixelColours.add(backgroundGray); }
		
		Color c;
		for (MisAnglePoint point : data.getBackingList())
		{
			if (!point.hasOMData)
			{
				c = backgroundGray;
			}
			else
			{
				DirectionVector dv = point.orientationVectors.get(subview.getIndex());
				c = Orientation.directionToColor(dv, 1f);
			}
			pixelColours.set(point.getIndex(), c);
		}
		
		orientationPainter.setPixels(pixelColours);
	}

	@Override
	public void writeData(ISkewGrid skewdata, MapSubView subview, BufferedWriter writer) throws IOException
	{
		@SuppressWarnings("unchecked")
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		
		writer.write("index, x, y, distance [001], direction [001], distance [110], direction [110], distance [111], direction [111]\n");
		
		for (MisAnglePoint point : data.getBackingList())
		{
			if (point.orientationVectors == null)
			{
				writer.write(point.getIndex() + ", " + point.getX() + ", " + point.getY() + ", " + "-, -, -, -, -, -\n");
			}
			else
			{
				DirectionVector dv1 = point.orientationVectors.get(0);
				DirectionVector dv2 = point.orientationVectors.get(1);
				DirectionVector dv3 = point.orientationVectors.get(2);
				
				writer.write(
						point.getIndex() + ", " + 
						point.getX() + ", " + 
						point.getY() + ", " +
						point.grain + ", " + 
						
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
