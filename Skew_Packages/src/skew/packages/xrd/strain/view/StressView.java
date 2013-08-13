package skew.packages.xrd.strain.view;

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
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.models.XRDStrain.IXRDStrain;
import skew.packages.xrd.strain.subview.StressSubView;
import fava.datatypes.Pair;
import fava.functionable.FList;

public class StressView extends MapView
{
	
	RasterColorMapPainter painter;
	AbstractPalette palette;
	
	ISkewGrid<IXRDStrain> model;
	
	public StressView(ISkewGrid<IXRDStrain> model)
	{
		super();
		
		this.model = model;
		
		painter = new RasterColorMapPainter();
		palette = new ThermalScalePalette(false, true);
		
	}
	
	@Override
	public String toString()
	{
		return "Stress";
	}
	
	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return new SpinnerNumberModel(5.0, 0.1, 1000.0, 0.1);
	}

	@Override
	public String getSummaryText(int x, int y)
	{
		
		ISkewPoint<IXRDStrain> point = model.get(x, y);
		if (! point.isValid()) return "";
		
		IXRDStrain data = point.getData();
		return "" + 
				"XX: " + fmt(data.stress()[0]) + ", " +
				"YY: " + fmt(data.stress()[1]) + ", " +
				"ZZ: " + fmt(data.stress()[2]) + ", " +
				"XY: " + fmt(data.stress()[3]) + ", " +
				"XZ: " + fmt(data.stress()[4]) + ", " +
				"YZ: " + fmt(data.stress()[5]) + ", " +
				"VM: " + fmt(data.strain()[6]);
				
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
				new StressSubView(0),
				new StressSubView(1),
				new StressSubView(2),
				new StressSubView(3),
				new StressSubView(4),
				new StressSubView(5),
				new StressSubView(6)
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
		
		String p1, p2, p3, p4, p5;
		
		if (subview.getIndex() < 6)
		{
			p1 = SigDigits.roundFloatTo((float)(-maxValue), 3);
			p2 = SigDigits.roundFloatTo((float)(-maxValue * 0.5), 3);
			p3 = "0";
			p4 = SigDigits.roundFloatTo((float)(maxValue * 0.5), 3);
			p5 = SigDigits.roundFloatTo((float)maxValue, 3);
		} else {
			//von mises
			p1 = "0";
			p2 = SigDigits.roundFloatTo((float)(maxValue * 0.25), 3);
			p3 = SigDigits.roundFloatTo((float)(maxValue * 0.5), 3);
			p4 = SigDigits.roundFloatTo((float)(maxValue * 0.75), 3);
			p5 = SigDigits.roundFloatTo((float)maxValue, 3);
		}
		
		
		axisMarkings.add(new Pair<Float, String>(0f, p1));
		axisMarkings.add(new Pair<Float, String>(0.25f, p2));
		axisMarkings.add(new Pair<Float, String>(0.5f, p3));
		axisMarkings.add(new Pair<Float, String>(0.75f, p4));
		axisMarkings.add(new Pair<Float, String>(1f, p5));
		
		
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
				false,
				axisMarkings);
		
		
		return new FList<AxisPainter>(spectrum);
	}

	
	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException
	{	
		writer.write("index, x, y, xx, yy, zz, xy, xz, yz, von mises\n");
		
		for (ISkewPoint<IXRDStrain> point : model.getPoints())
		{
			IXRDStrain data = point.getData();
			writer.write(
					point.getIndex() + ", " + 
					point.getX() + ", " + 
					point.getY() + ", " +
					fmt(data.stress()[0]) + ", " +
					fmt(data.stress()[1]) + ", " + 
					fmt(data.stress()[2]) + ", " + 
					fmt(data.stress()[3]) + ", " + 
					fmt(data.stress()[4]) + ", " + 
					fmt(data.stress()[5]) + ", " +
					fmt(data.stress()[6]) + 
					"\n"
				);
		}
	}
	
	@Override
	public boolean canWriteData()
	{
		return true;
	}

	
	private void setupPainters(ISkewGrid<IXRDStrain> skewdata, MapSubView subview, float maximum)
	{

		List<Color> pixelColours = new FList<Color>(model.getWidth() * model.getHeight());
		for (int i = 0; i < model.getWidth() * model.getHeight(); i++){ pixelColours.add(Color.black); }
		
		Color c;
		for (ISkewPoint<IXRDStrain> point : model.getPoints())
		{
			IXRDStrain data = point.getData();
			
			if (point.isValid()) 
			{
				double v = subview.select(data.stress());
				c = palette.getFillColour(v, maximum);
				pixelColours.set(point.getIndex(), c);
			} else {
				c = backgroundGray;
			}
		}
		
		painter.setPixels(pixelColours);
	}

}
