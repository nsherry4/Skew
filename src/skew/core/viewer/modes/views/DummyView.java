package skew.core.viewer.modes.views;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.model.SkewGrid;
import skew.core.model.SkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;

public class DummyView extends MapView
{

	@Override
	public SpinnerModel scaleSpinnerModel(SkewGrid data, MapSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(SkewPoint point, SkewGrid data)
	{
		return "";
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
	public float getMaximumIntensity(SkewGrid data, MapSubView subview)
	{
		return 0;
	}

	@Override
	public List<MapPainter> getPainters(SkewGrid data, MapSubView subview, float maximum)
	{
		return new ArrayList<MapPainter>();
	}

	@Override
	public List<AxisPainter> getAxisPainters(SkewGrid data, MapSubView subview, float maxValue)
	{
		return new ArrayList<AxisPainter>();
	}

	@Override
	public void writeData(SkewGrid data, MapSubView subview, BufferedWriter writer) throws IOException
	{
		
	}
	
	@Override
	public String toString()
	{
		return "View";
	}

}
