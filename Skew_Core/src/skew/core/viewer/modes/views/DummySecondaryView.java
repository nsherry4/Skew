package skew.core.viewer.modes.views;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.viewer.modes.subviews.MapSubView;
import fava.functionable.FList;

public class DummySecondaryView extends SecondaryView 
{

	public DummySecondaryView() {
		super("");
	}

	@Override
	public List<MapPainter> getPainters(MapSubView subview, float maximum) {
		return new ArrayList<MapPainter>();
	}

	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue) {
		return new ArrayList<AxisPainter>();
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
