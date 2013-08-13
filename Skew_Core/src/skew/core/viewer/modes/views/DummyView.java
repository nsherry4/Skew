package skew.core.viewer.modes.views;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.viewer.modes.subviews.MapSubView;

public class DummyView extends MapView
{

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
		return 0;
	}

	@Override
	public List<MapPainter> getPainters(MapSubView subview, float maximum)
	{
		return new ArrayList<MapPainter>();
	}

	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
		return new ArrayList<AxisPainter>();
	}

	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException
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
