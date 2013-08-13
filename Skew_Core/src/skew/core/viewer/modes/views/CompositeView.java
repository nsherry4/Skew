package skew.core.viewer.modes.views;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.viewer.modes.subviews.MapSubView;
import fava.functionable.FList;

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
	
	

	@Override
	public boolean canWriteData() {
		return primary.canWriteData();
	}

	@Override
	public void setUpdateRequired() {
		primary.setUpdateRequired();
		for (MapView s : secondary){ s.setUpdateRequired(); }
	}

	@Override
	public boolean isUpdateRequired() {
		
		boolean required = primary.isUpdateRequired();
		for (MapView s : secondary) { required |= s.isUpdateRequired(); }
		return required;
	}

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView) {
		return primary.scaleSpinnerModel(subView);
	}

	@Override
	public String getSummaryText(int x, int y) {
		return primary.getSummaryText(x, y);
	}

	@Override
	public boolean hasSublist() {
		return primary.hasSublist();
	}

	@Override
	public List<MapSubView> getSubList() {
		return primary.getSubList();
	}

	@Override
	public float getMaximumIntensity(MapSubView subview) {
		return primary.getMaximumIntensity(subview);
	}

	@Override
	public List<MapPainter> getPainters(MapSubView subview,	float maximum) {
		List<MapPainter> painters = primary.getPainters(subview, maximum);
		for (MapView s : secondary) { painters.addAll(s.getPainters(null, 0)); }
		return painters;
	}

	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maximum) {
		List<AxisPainter> painters = primary.getAxisPainters(subview, maximum);
		for (MapView s : secondary) { painters.addAll(s.getAxisPainters(null, 0)); }
		return painters;
	}


	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException {
		primary.writeData(subview, writer);
	}

	@Override
	public String toString() {
		return primary.toString();
	}
	
	
}
