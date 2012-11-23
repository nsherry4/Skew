package skew.packages.pixeldeviation.view;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;

import fava.datatypes.Pair;
import fava.functionable.FList;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.painter.RasterColorMapWrapper;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.packages.pixeldeviation.model.PixDev;

public class PixelDeviationComparisonView extends MapView
{

	RasterColorMapWrapper painter;
	AbstractPalette palette;
	
	public PixelDeviationComparisonView()
	{
		super();
			
		painter = new RasterColorMapWrapper() {
			
			@Override
			protected Color valueToColor(ISkewPoint point)
			{
				
				return getDevColor(((PixDev)point).getValue());
			}
		};
		
		palette = new AbstractPalette() {
			
			@Override
			public Color getFillColour(double value, double max)
			{
				float val = (float)(value / max);
				return getDevColor(val);
			}
		};
	}
	
	public String toString()
	{
		return "Comparative Pixel Deviation";
	}
	
	@Override
	public SpinnerModel scaleSpinnerModel(ISkewGrid data, MapSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(ISkewPoint point, ISkewGrid data)
	{
		return "Comparative Pixel Deviation";
	}

	@Override
	public boolean hasSublist()
	{
		return false;
	}

	@Override
	public List<MapSubView> getSubList()
	{
		return null;
	}

	@Override
	public float getMaximumIntensity(ISkewGrid data, MapSubView subview)
	{
		return 1;
	}


	@Override
	public List<MapPainter> getPainters(ISkewGrid data, MapSubView subview, float maximum)
	{
		painter.setData(data);
		return new FList<MapPainter>(painter);
	}

	
	@Override
	public List<AxisPainter> getAxisPainters(ISkewGrid data, MapSubView subview, float maxValue)
	{
		List<Pair<Float, String>> axisMarkings = new FList<Pair<Float,String>>();
		
		axisMarkings.add(  new Pair<Float, String>(0f, "-100%"));
		axisMarkings.add(  new Pair<Float, String>(0.25f, "-50%"));
		axisMarkings.add(  new Pair<Float, String>(0.5f, "0/No Data")  );
		axisMarkings.add(  new Pair<Float, String>(0.75f, "50%"));
		axisMarkings.add(  new Pair<Float, String>(1f, "100%"));
		
		
		AxisPainter spectrum = new SpectrumCoordsAxisPainter(
				false, 
				null, 
				null, 
				null, 
				null, 
				null, 
				true, 
				20, 
				256, 
				new FList<AbstractPalette>(palette), 
				false, 
				"Comparative Pixel Deviation", 
				1,
				true,
				axisMarkings);
		
		
		return new FList<AxisPainter>(spectrum);
	}

	@Override
	public void writeData(ISkewGrid data, MapSubView subview, BufferedWriter writer) throws IOException
	{
		return;
	}
	
	@Override
	public boolean canWriteData()
	{
		return false;
	}
	
	
	
	private Color getDevColor(float val)
	{	
		if (val < 0)
		{
			//RED
			val = -val;
			if (val > 1) val = 1;
			float rval = 1f-val;
			return new Color(1f, rval, rval);
		} else {
			//BLUE
			if (val > 1) val = 1;
			float rval = 1f-val;
			return new Color(rval, rval, 1f);
		}
	}



}
