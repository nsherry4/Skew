package skew.core.viewer.modes.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.viewer.modes.subviews.MapSubView;
import fava.functionable.FList;

public class CompositeView extends MapView 
{

	private MapView primary;
	private List<MapView> secondary;
	
	public CompositeView(MapView primary, MapView secondary) {
		this(primary, new FList<MapView>(secondary));
	}
	
	public CompositeView(MapView primary, MapView... secondary) {
		this(primary, Arrays.asList(secondary));
	}
	
	public CompositeView(MapView primary, List<MapView> secondary) {
		super(primary.getTitle());
		this.primary = primary;
		this.secondary = secondary;
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
	public String toString() {
		return primary.toString();
	}


	@Override
	public void setTitle(String title) {
		primary.setTitle(title);
	}

	@Override
	public String getTitle() {
		return primary.getTitle();
	}

	@Override
	public List<Summary> getSummary(int x, int y) {
		List<Summary> summaries = new ArrayList<>();
		summaries.addAll(primary.getSummary(x, y));
		for (MapView v : secondary) {
			summaries.addAll(v.getSummary(x, y));
		}
		return summaries;
	}
	

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {
		primary.setPointSelected(x, y, deselectAll);
		for (MapView s : secondary) s.setPointSelected(x, y, deselectAll);
	}
	
}
