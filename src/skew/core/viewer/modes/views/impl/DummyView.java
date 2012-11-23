package skew.core.viewer.modes.views.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;

public class DummyView extends MapView
{

	@Override
	public SpinnerModel scaleSpinnerModel(ISkewGrid data, MapSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(ISkewPoint point, ISkewGrid data)
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
	public float getMaximumIntensity(ISkewGrid data, MapSubView subview)
	{
		return 0;
	}

	@Override
	public List<MapPainter> getPainters(ISkewGrid data, MapSubView subview, float maximum)
	{
		return new ArrayList<MapPainter>();
	}

	@Override
	public List<AxisPainter> getAxisPainters(ISkewGrid data, MapSubView subview, float maxValue)
	{
		return new ArrayList<AxisPainter>();
	}

	@Override
	public void writeData(ISkewGrid data, MapSubView subview, BufferedWriter writer) throws IOException
	{
		
	}
	
	@Override
	public String toString()
	{
		return "View";
	}

	@Override
	public boolean canWriteData()
	{
		return false;
	}

}
