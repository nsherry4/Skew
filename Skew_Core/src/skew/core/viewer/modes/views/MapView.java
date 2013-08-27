package skew.core.viewer.modes.views;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.SigDigits;
import skew.core.viewer.modes.subviews.MapSubView;
import fava.datatypes.Maybe;



public abstract class MapView
{
	
	public static final Color backgroundGray = new Color(0.1f, 0.1f, 0.1f);
	protected AbstractPalette negativeValueEmptyPalette, nanEmptyPalette;
	private boolean updateRequired = true;
	private String title = "";
	
	public MapView(String title)
	{
	
		this.title = title;
		negativeValueEmptyPalette = new AbstractPalette() {
			
			@Override
			public Color getFillColour(double intensity, double maximum) {
				if (intensity < 0) return backgroundGray;
				return null;
			}
		};
		
		
		nanEmptyPalette = new AbstractPalette() {
			
			@Override
			public Color getFillColour(double intensity, double maximum) {
				if (Double.isNaN(intensity)) return backgroundGray;
				return null;
			}
		};
				
	}
	
	
	public void setUpdateRequired()
	{
		updateRequired = true;
	}
	
	public boolean isUpdateRequired()
	{
		return updateRequired;
	}
	
	protected void setUpdateComplete()
	{
		updateRequired = false;
	}
	
	protected String fmt(float f)
	{
		return SigDigits.roundFloatTo(f, 5);
	}

	protected String fmt(double d)
	{
		return fmt((float)d);
	}
	
	protected String fmt(Maybe<Double> d)
	{
		return d.is() ? fmt(d.get()) : "-"; 
	}
	
	
	public abstract SpinnerModel scaleSpinnerModel(MapSubView subView);
	public abstract boolean hasSublist();
	public abstract List<MapSubView> getSubList();

	public abstract Map<String, String> getSummaryData(int x, int y);
	public abstract List<String> getSummaryHeaders();
	
	public abstract float getMaximumIntensity(MapSubView subview);
	public abstract List<MapPainter> getPainters(MapSubView subview, float maximum);
	public abstract List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue);
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String toString() {
		return title;
	}

	
	
	
	protected static String formatGrainValue(Maybe<Integer> value)
	{
		if (!value.is()) return "None";
		return "#" + value.get();
	}
	
	
	protected static String formatMisValue(Maybe<Double> value)
	{
		if (value.is()) {
			return SigDigits.roundFloatTo(value.get().floatValue(), 3) + "\u00B0";
		} else {
			return "Boundary";
		}
		
	}
	

	
}
