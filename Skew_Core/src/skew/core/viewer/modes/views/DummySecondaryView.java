package skew.core.viewer.modes.views;

import java.util.ArrayList;
import java.util.List;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.viewer.modes.subviews.MapSubView;

public class DummySecondaryView extends SecondaryView 
{

	public DummySecondaryView() {
		super("", true);
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
	public List<Summary> getSummary(int x, int y) {
		return new ArrayList<>();
	}

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {}

}
