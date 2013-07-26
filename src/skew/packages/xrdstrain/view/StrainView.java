package skew.packages.xrdstrain.view;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterColorMapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.SigDigits;
import skew.core.model.ISkewGrid;
import skew.core.model.impl.SkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.models.XRDStrain.IXRDStrainPoint;
import skew.packages.xrdstrain.subview.StrainSubView;
import fava.datatypes.Pair;
import fava.functionable.FList;

public class StrainView extends MapView
{
	
	RasterColorMapPainter painter;
	AbstractPalette palette;
	
	SkewGrid<IXRDStrainPoint> model;
	
	public StrainView(SkewGrid<IXRDStrainPoint> model)
	{
		super();
		
		this.model = model;
		
		painter = new RasterColorMapPainter();
		palette = new ThermalScalePalette(false, true);
		
	}
	
	@Override
	public String toString()
	{
		return "Strain";
	}
	
	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return new SpinnerNumberModel(5.0, 0.1, 1000.0, 0.1);
	}

	@Override
	public String getSummaryText(int x, int y)
	{
		IXRDStrainPoint point = model.get(x, y);
		if (! point.getHasStrainData()) return "";
		
		return "" + 
				"XX: " + fmt(point.strain()[0]) + ", " +
				"YY: " + fmt(point.strain()[1]) + ", " +
				"ZZ: " + fmt(point.strain()[2]) + ", " +
				"XY: " + fmt(point.strain()[3]) + ", " +
				"XZ: " + fmt(point.strain()[4]) + ", " +
				"YZ: " + fmt(point.strain()[5]) + ", ";
				
	}


	@Override
	public boolean hasSublist()
	{
		return true;
	}

	@Override
	public List<MapSubView> getSubList()
	{
		return new FList<MapSubView>(
				new StrainSubView(0),
				new StrainSubView(1),
				new StrainSubView(2),
				new StrainSubView(3),
				new StrainSubView(4),
				new StrainSubView(5),
				new StrainSubView(6)
			);
	}

	@Override
	public float getMaximumIntensity(MapSubView subview)
	{
		return 1;
	}

	@Override
	public List<MapPainter> getPainters(MapSubView subview, float maximum)
	{		
		if (isUpdateRequired())
		{
			setupPainters(model, subview, maximum);
			setUpdateComplete();
		}
		return new FList<MapPainter>(painter);
	}
		
	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
		List<Pair<Float, String>> axisMarkings = new FList<Pair<Float,String>>();
		
		axisMarkings.add(  new Pair<Float, String>(0f, "" + SigDigits.roundFloatTo((float)(-maxValue), 3))  );
		axisMarkings.add(  new Pair<Float, String>(0.25f, "" + SigDigits.roundFloatTo((float)(-maxValue * 0.5), 3))  );
		axisMarkings.add(  new Pair<Float, String>(0.5f, "" + 0)  );
		axisMarkings.add(  new Pair<Float, String>(0.75f, "" + SigDigits.roundFloatTo((float)(maxValue * 0.5), 3))  );
		axisMarkings.add(  new Pair<Float, String>(1f, "" + SigDigits.roundFloatTo((float)maxValue, 3))  );
		
		
		AxisPainter spectrum = new SpectrumCoordsAxisPainter(
				false, 
				null, 
				null, 
				null, 
				null, 
				null, 
				true, 
				20, 
				256, 
				new FList<AbstractPalette>(palette), 
				false, 
				"Strain", 
				1,
				true,
				axisMarkings);
		
		
		return new FList<AxisPainter>(spectrum);
	}

	
	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException
	{

		writer.write("index, x, y, xx, yy, zz, xy, xz, yz, von mises\n");
		
		for (IXRDStrainPoint point : model.getBackingList())
		{
			writer.write(
					point.getIndex() + ", " + 
					point.getX() + ", " + 
					point.getY() + ", " +
					fmt(point.strain()[0]) + ", " +
					fmt(point.strain()[1]) + ", " + 
					fmt(point.strain()[2]) + ", " + 
					fmt(point.strain()[3]) + ", " + 
					fmt(point.strain()[4]) + ", " + 
					fmt(point.strain()[5]) + ", " +
					fmt(point.strain()[6]) + 
					"\n"
				);
		}
	}
	
	@Override
	public boolean canWriteData()
	{
		return true;
	}
	

	
	private void setupPainters(ISkewGrid skewdata, MapSubView subview, float maximum)
	{
		
		List<Color> pixelColours = new FList<Color>(model.getWidth() * model.getHeight());
		for (int i = 0; i < model.getWidth() * model.getHeight(); i++){ pixelColours.add(Color.black); }
		
		Color c;

		for (IXRDStrainPoint point : model.getBackingList())
		{	
			if (point.getHasStrainData()) 
			{
				double v = subview.select(point.strain());
				c = palette.getFillColour(v, maximum);
				pixelColours.set(point.getIndex(), c);
			} else {
				c = backgroundGray;
			}
		}
		
		painter.setPixels(pixelColours);
	}

}
