package skew.packages.misorientation.view.grain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;

import fava.functionable.FList;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.Spectrum;
import skew.core.model.SkewGrid;
import skew.core.model.SkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.packages.misorientation.drawing.GrainPalette;
import skew.packages.misorientation.model.Grain;
import skew.packages.misorientation.model.MisAngleGrid;
import skew.packages.misorientation.model.MisAnglePoint;
import skew.packages.misorientation.view.MisorientationView;


public class GrainLabelView extends MisorientationView
{
	
	MapPainter grainPainter;
	
	AbstractPalette grainpalette = new GrainPalette();
	
	
	
	public GrainLabelView()
	{
		List<AbstractPalette> grainPalettes = new FList<AbstractPalette>(super.greyEmpty, grainpalette);
		grainPainter = new RasterSpectrumMapPainter(grainPalettes, null);
	}
	
	public String toString(){ return "Grain Labels"; }

	@Override
	public SpinnerModel scaleSpinnerModel(SkewGrid data, MapSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(SkewPoint skewpoint, SkewGrid skewdata)
	{
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		MisAnglePoint point = (MisAnglePoint)skewpoint;
		
		String grain = formatGrainValue(point.grain);
		String result = "Grain: " + grain;
		
		Grain g;
		try { g = data.grains.get(point.grain); }
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
	public float getMaximumIntensity(SkewGrid data, MapSubView subview)
	{
		return 0;
	}

	@Override
	public List<MapPainter> getPainters(SkewGrid skewdata, MapSubView subview, float maximum)
	{
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		
		if (isUpdateRequired())
		{
			super.setData(data, subview);
			setupPainters(data, subview);
			setUpdateComplete();
		}
		return new FList<MapPainter>(grainPainter, super.boundaryPainter, super.selectedGrainPainter);
	}

	@Override
	public List<AxisPainter> getAxisPainters(SkewGrid data, MapSubView subview, float maxValue)
	{
		return new FList<AxisPainter>();
	}

	
	private void setupPainters(MisAngleGrid<MisAnglePoint> data, MapSubView subview)
	{
		
		Spectrum misorientationData = new Spectrum(data.size());
		
		for (int i = 0; i < data.size(); i++)
		{
			int grainIndex = data.get(i).grain;
			if (grainIndex < 0) { misorientationData.set(i, -1f); continue; }
			Grain g = data.grains.get(grainIndex);
			if (g == null) { misorientationData.set(i, -1f); continue; }
			else { misorientationData.set(i, g.colourIndex); }
		}
		
		grainPainter.setData(misorientationData);
	}

	@Override
	public void writeData(SkewGrid skewdata, MapSubView subview, BufferedWriter writer) throws IOException
	{
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		
		writer.write("index, x, y, grain, size (px)\n");
		
		for (MisAnglePoint point : data.getBackingList())
		{
			Grain g = data.getGrainAtPoint(point);
			String grainsize = fmt(-1f);
			if (g != null) grainsize = g.points.size() + "";
			writer.write(
					point.getIndex() + ", " + 
					point.getX() + ", " + 
					point.getY() + ", " +
					point.grain + ", " + 
					grainsize + 
				"\n");
		}
	}
	
}
