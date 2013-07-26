package skew.core.viewer.modes.views.impl;

import java.util.ArrayList;
import java.util.List;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.SecondaryView;

public class DummySecondaryView extends SecondaryView 
{

	@Override
	public List<MapPainter> getPainters(MapSubView subview, float maximum) {
		return new ArrayList<MapPainter>();
	}

	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue) {
		return new ArrayList<AxisPainter>();
	}

}