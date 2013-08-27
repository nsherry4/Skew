package skew.core.viewer.modes.views;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.viewer.modes.subviews.MapSubView;
import fava.functionable.FList;

public class DummyView extends MapView
{

	public DummyView() {
		super("");
		// TODO Auto-generated constructor stub
	}


	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return null;
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
	public String toString()
	{
		return "View";
	}


	@Override
	public Map<String, String> getSummaryData(int x, int y) {
		return new LinkedHashMap<>();
	}


	@Override
	public List<String> getSummaryHeaders() {
		return new FList<>();
	}

}
