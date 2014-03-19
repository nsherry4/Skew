package skew.views.xrdmeta;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fava.datatypes.Pair;
import fava.functionable.FList;
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
import skew.models.xrdmeta.XRDMeta;

public class IndexQualityView extends MapView
{
	
	private RasterColorMapPainter painter;
	private AbstractPalette palette;
	
	private ISkewGrid<XRDMeta> model;

	public IndexQualityView(ISkewGrid<XRDMeta> model) {
		super("Index Quality");
		this.model = model;
		
		painter = new RasterColorMapPainter();
		palette = new ThermalScalePalette(false, true);
		
	}

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView) {
		return new SpinnerNumberModel(5.0, 0.1, 1000.0, 0.1);
	}

	@Override
	public boolean hasSublist() {
		return false;
	}

	@Override
	public List<MapSubView> getSubList() {
		return null;
	}

	@Override
	public List<Summary> getPointSummary(int x, int y) {
		
		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary(getTitle());
		summaries.add(s);
		s.addHeader("Quality");
		
		ISkewPoint<XRDMeta> point = model.getPoint(x, y);
		if (! point.isValid()) return summaries;
		
		XRDMeta data = point.getData();
		
		s.addValue("Quality", fmt(data.indexQuality));
		
		return summaries;
	}

	@Override
	public List<Summary> getMapSummary() {
		return new ArrayList<>();
	}

	@Override
	public float getMaximumIntensity(MapSubView subview) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public List<MapPainter> getPainters(MapSubView subview, float maximum) {
		if (isUpdateRequired())
		{
			setupPainters(subview, maximum);
			setUpdateComplete();
		}
		return new FList<MapPainter>(painter);
	}

	
	
	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
		List<Pair<Float, String>> axisMarkings = new FList<Pair<Float,String>>();
		
		String p1, p2, p3, p4, p5;
		p1 = "0";
		p2 = SigDigits.roundFloatTo((float)(maxValue * 0.25), 3, true);
		p3 = SigDigits.roundFloatTo((float)(maxValue * 0.5), 3, true);
		p4 = SigDigits.roundFloatTo((float)(maxValue * 0.75), 3, true);
		p5 = SigDigits.roundFloatTo((float)maxValue, 3, true);
		
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
				"Index Quality", 
				1,
				false,
				axisMarkings);
		
		
		return new FList<AxisPainter>(spectrum);
	}

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {}

	
	
	
	
	
	
	private void setupPainters(MapSubView subview, float maximum)
	{
		
		List<Color> pixelColours = new FList<Color>(model.getWidth() * model.getHeight());
		for (int i = 0; i < model.getWidth() * model.getHeight(); i++){ pixelColours.add(Color.black); }
		
		Color c;

		for (ISkewPoint<XRDMeta> point : model.getPoints())
		{	
			XRDMeta data = point.getData();
			if (point.isValid()) 
			{
				double v = data.indexQuality;
				c = palette.getFillColour(v, maximum);
				pixelColours.set(point.getIndex(), c);
			} else {
				c = backgroundGray;
			}
		}
		
		painter.setPixels(pixelColours);
	}
	
}
