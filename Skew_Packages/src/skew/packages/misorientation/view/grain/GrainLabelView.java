package skew.packages.misorientation.view.grain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.models.Grain.Grain;
import skew.models.Misorientation.MisAngle;
import skew.models.Misorientation.MisAngleGrid;
import skew.packages.misorientation.drawing.GrainPalette;
import fava.functionable.FList;


public class GrainLabelView extends MapView
{
	
	RasterSpectrumMapPainter grainPainter;
	
	AbstractPalette grainpalette = new GrainPalette();
	private MisAngleGrid model;
	
	
	public GrainLabelView(MisAngleGrid model)
	{
		this.model = model;
		List<AbstractPalette> grainPalettes = new FList<AbstractPalette>(super.greyEmpty, grainpalette);
		grainPainter = new RasterSpectrumMapPainter(grainPalettes, null);
	}
	
	public String toString(){ return "Grain Labels"; }

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(int x, int y)
	{
		
		MisAngle point = model.get(x, y).getData();
		
		String grain = formatGrainValue(point.grain);
		String result = "Grain: " + grain;
		
		Grain g;
		try { g = model.grains.get(point.grain); }
		catch (ArrayIndexOutOfBoundsException e) { return result; }
		if (g == null) return result;
		
		result += ", Size: " + g.points.size() + " pixels";
		return result;
		
	}

	@Override
	public boolean hasSublist()
	{
		return false;
	}

	@Override
	public List<MapSubView> getSubList()
	{
		return null;
	}

	@Override
	public float getMaximumIntensity(MapSubView subview)
	{
		return 0;
	}

	@Override
	public List<MapPainter> getPainters(MapSubView subview, float maximum)
	{
		
		if (isUpdateRequired())
		{
			setupPainters(subview);
			setUpdateComplete();
		}
		return new FList<MapPainter>(grainPainter);
	}

	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
		return new FList<AxisPainter>();
	}

	
	private void setupPainters(MapSubView subview)
	{
		
		Spectrum misorientationData = new Spectrum(model.size());
		
		for (int i = 0; i < model.size(); i++)
		{
			int grainIndex = model.get(i).getData().grain;
			if (grainIndex < 0) { misorientationData.set(i, -1f); continue; }
			Grain g = model.grains.get(grainIndex);
			if (g == null) { misorientationData.set(i, -1f); continue; }
			else { misorientationData.set(i, g.colourIndex); }
		}
		
		grainPainter.setData(misorientationData);
	}

	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException
	{	
		writer.write("index, x, y, grain, size (px)\n");
		
		for (ISkewPoint<MisAngle> point : model.getBackingList())
		{
			Grain g = model.getGrainAtPoint(point);
			String grainsize = fmt(-1f);
			if (g != null) grainsize = g.points.size() + "";
			writer.write(
					point.getIndex() + ", " + 
					point.getX() + ", " + 
					point.getY() + ", " +
					point.getData().grain + ", " + 
					grainsize + 
				"\n");
		}
	}
	
	@Override
	public boolean canWriteData()
	{
		return true;
	}
	
}
