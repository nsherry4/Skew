package skew.views.strain;

import java.awt.Color;
import java.util.ArrayList;
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
import skew.core.viewer.modes.views.Summary;
import skew.models.strain.IXRDStrain;
import fava.datatypes.Pair;
import fava.functionable.FList;

public class StrainView extends MapView
{
	
	RasterColorMapPainter painter;
	AbstractPalette palette;
	
	ISkewGrid<IXRDStrain> model;
	
	public StrainView(ISkewGrid<IXRDStrain> model)
	{
		super("Strain");
		
		this.model = model;
		
		painter = new RasterColorMapPainter();
		palette = new ThermalScalePalette(false, true);
		
	}

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return new SpinnerNumberModel(5.0, 0.1, 1000.0, 0.1);
	}

	@Override
	public List<Summary> getSummary(int x, int y)
	{
		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary(getTitle());
		summaries.add(s);
		s.addHeader("XX", "YY", "ZZ", "XY", "XZ", "YZ", "VM");
		
		ISkewPoint<IXRDStrain> point = model.getPoint(x, y);
		if (! point.isValid()) return summaries;
		
		IXRDStrain data = point.getData();
		
		s.addValue("XX", fmt(data.strain()[0]));
		s.addValue("YY", fmt(data.strain()[1]));
		s.addValue("ZZ", fmt(data.strain()[2]));
		s.addValue("XY", fmt(data.strain()[3]));
		s.addValue("XZ", fmt(data.strain()[4]));
		s.addValue("YZ", fmt(data.strain()[5]));
		s.addValue("VM", fmt(data.strain()[6]));
				
		return summaries;
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
		
		String p1, p2, p3, p4, p5;
		
		if (subview.getIndex() < 6)
		{
			p1 = SigDigits.roundFloatTo((float)(-maxValue), 3, true);
			p2 = SigDigits.roundFloatTo((float)(-maxValue * 0.5), 3, true);
			p3 = "0";
			p4 = SigDigits.roundFloatTo((float)(maxValue * 0.5), 3, true);
			p5 = SigDigits.roundFloatTo((float)maxValue, 3, true);
						
		} else {
			//von mises
			p1 = "0";
			p2 = SigDigits.roundFloatTo((float)(maxValue * 0.25), 3, true);
			p3 = SigDigits.roundFloatTo((float)(maxValue * 0.5), 3, true);
			p4 = SigDigits.roundFloatTo((float)(maxValue * 0.75), 3, true);
			p5 = SigDigits.roundFloatTo((float)maxValue, 3, true);			
		}
		
		
		axisMarkings.add(new Pair<Float, String>(0f, p1));
		axisMarkings.add(new Pair<Float, String>(0.25f, p2));
		axisMarkings.add(new Pair<Float, String>(0.5f, p3));
		axisMarkings.add(new Pair<Float, String>(0.75f, p4));
		axisMarkings.add(new Pair<Float, String>(1f, p5));
		
		
		AxisPainter spectrum = new SpectrumCoordsAxisPainter(
				false, null, null, null, null, null,  //coordinate settings
				true, 
				20, 
				256, 
				new FList<AbstractPalette>(palette), 
				false, 
				"Strain", 
				1,
				subview.getIndex() < 6,
				axisMarkings);
		
		
		return new FList<AxisPainter>(spectrum);
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
				double v = subview.select(data.strain());
				c = palette.getFillColour(v, maximum);
				pixelColours.set(point.getIndex(), c);
			} else {
				c = backgroundGray;
			}
		}
		
		painter.setPixels(pixelColours);
	}

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {}

}
