package skew.core.viewer.modes.views;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.SigDigits;
import skew.core.viewer.modes.subviews.MapSubView;



public abstract class MapView
{
	
	public static final Color backgroundGray = new Color(0.1f, 0.1f, 0.1f);
	
	protected AbstractPalette greyEmpty;
	
	private boolean updateRequired = true;
	
	public MapView()
	{
	
		greyEmpty = new AbstractPalette() {
			
			@Override
			public Color getFillColour(double intensity, double maximum) {
				if (intensity < 0) return backgroundGray;
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
	
	
	public abstract SpinnerModel scaleSpinnerModel(MapSubView subView);
	public abstract String getSummaryText(int x, int y);
	public abstract boolean hasSublist();
	public abstract List<MapSubView> getSubList();

	public abstract float getMaximumIntensity(MapSubView subview);
	public abstract List<MapPainter> getPainters(MapSubView subview, float maximum);
	public abstract List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue);
	
	public abstract void writeData(MapSubView subview, BufferedWriter writer) throws IOException; 
	public abstract boolean canWriteData();
	
	
	
	protected static String formatGrainValue(double value)
	{
		if (value < 0) return "None";
		return "#" + (int)value;
	}
	
	protected static String formatMisorientationValue(double value)
	{
		String valString;
		valString = SigDigits.roundFloatTo((float)value, 3);
		if (value < 0) valString = "Boundary";
		
		return valString;
	}
	
}
