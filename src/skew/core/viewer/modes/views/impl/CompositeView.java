package skew.core.viewer.modes.views.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;

import fava.functionable.FList;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.core.viewer.modes.views.SecondaryView;

public class CompositeView extends MapView 
{

	private MapView primary;
	private List<SecondaryView> secondary;
	
	public CompositeView(MapView primary, SecondaryView secondary) {
		this(primary, new FList<SecondaryView>(secondary));
	}
	
	public CompositeView(MapView primary, List<SecondaryView> secondary) {
		super();
		this.primary = primary;
		this.secondary = secondary;
	}
	
	


	public boolean canWriteData() {
		return primary.canWriteData();
	}


	public void setUpdateRequired() {
		primary.setUpdateRequired();
		for (MapView s : secondary){ s.setUpdateRequired(); }
	}

	public boolean isUpdateRequired() {
		
		boolean required = primary.isUpdateRequired();
		for (MapView s : secondary) { required |= s.isUpdateRequired(); }
		return required;
	}

	public SpinnerModel scaleSpinnerModel(ISkewGrid data, MapSubView subView) {
		return primary.scaleSpinnerModel(data, subView);
	}

	public String getSummaryText(ISkewPoint point, ISkewGrid data) {
		return primary.getSummaryText(point, data);
	}

	public boolean hasSublist() {
		return primary.hasSublist();
	}

	public List<MapSubView> getSubList() {
		return primary.getSubList();
	}

	public float getMaximumIntensity(ISkewGrid data, MapSubView subview) {
		return primary.getMaximumIntensity(data, subview);
	}

	public List<MapPainter> getPainters(ISkewGrid data, MapSubView subview,	float maximum) {
		List<MapPainter> painters = primary.getPainters(data, subview, maximum);
		for (MapView s : secondary) { painters.addAll(s.getPainters(data, null, 0)); }
		return painters;
	}

	public List<AxisPainter> getAxisPainters(ISkewGrid data, MapSubView subview, float maximum) {
		List<AxisPainter> painters = primary.getAxisPainters(data, subview, maximum);
		for (MapView s : secondary) { painters.addAll(s.getAxisPainters(data, null, 0)); }
		return painters;
	}


	public void writeData(ISkewGrid data, MapSubView subview, BufferedWriter writer) throws IOException {
		primary.writeData(data, subview, writer);
	}
	
	public String toString() {
		return primary.toString();
	}
	
	
}
