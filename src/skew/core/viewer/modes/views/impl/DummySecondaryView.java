package skew.core.viewer.modes.views.impl;

import java.util.ArrayList;
import java.util.List;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.SecondaryView;

public class DummySecondaryView extends SecondaryView 
{

	@Override
	public List<MapPainter> getPainters(ISkewGrid data, MapSubView subview, float maximum) {
		return new ArrayList<MapPainter>();
	}

	@Override
	public List<AxisPainter> getAxisPainters(ISkewGrid data, MapSubView subview, float maxValue) {
		return new ArrayList<AxisPainter>();
	}

}
