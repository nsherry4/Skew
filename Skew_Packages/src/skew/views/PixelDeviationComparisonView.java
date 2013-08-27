package skew.views;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.painter.RasterColorMapWrapper;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import fava.datatypes.Pair;
import fava.functionable.FList;

public class PixelDeviationComparisonView extends MapView
{

	RasterColorMapWrapper<Float> painter;
	AbstractPalette palette;
	ISkewGrid<Float> model;
	
	public PixelDeviationComparisonView(ISkewGrid<Float> model)
	{
		super("Comparative Pixel Deviation");
		
		this.model = model;
		
		painter = new RasterColorMapWrapper<Float>() {
			
			@Override
			protected Color valueToColor(ISkewPoint<Float> point)
			{
				
				return getDevColor(point.getData());
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

	
	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return null;
	}

	@Override
	public Map<String, String> getSummaryData(int x, int y)
	{
		return new HashMap<>();
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
	public float getMaximumIntensity(MapSubView subview)
	{
		return 1;
	}


	@Override
	public List<MapPainter> getPainters(MapSubView subview, float maximum)
	{
		painter.setData(model);
		return new FList<MapPainter>(painter);
	}

	
	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
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

	@Override
	public List<String> getSummaryHeaders() {
		return new ArrayList<>();
	}



}
