package skew.packages.misorientation.view.misangle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fava.functionable.FList;

import scidraw.drawing.map.painters.MapPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.packages.misorientation.model.MisAngleGrid;
import skew.packages.misorientation.model.MisAnglePoint;


public class LocalView extends MisAngleView
{
	public String toString(){ return "Local Misorientation"; }

	@Override
	public SpinnerModel scaleSpinnerModel(ISkewGrid data, MapSubView subView)
	{
		return new SpinnerNumberModel(2, 0.0, 180.0, 0.1);
	}

	@Override
	public String getSummaryText(ISkewPoint skewpoint, ISkewGrid data)
	{
		
		MisAnglePoint point = (MisAnglePoint)skewpoint;

		String avg = formatMisorientationValue(point.average);
		String east = formatMisorientationValue(point.east);
		String south = formatMisorientationValue(point.south);
		String west = formatMisorientationValue(point.west);
		String north = formatMisorientationValue(point.north);
		
		return ""+
			"Angles - " + 
			"Average: " + avg + 
			", \u2191" + north +
			", \u2192" + east + 
			", \u2193" + south +
			", \u2190" + west;
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
	public float getMaximumIntensity(ISkewGrid data, MapSubView subview)
	{
		return 0;
	}

	@Override
	public List<MapPainter> getPainters(ISkewGrid data, MapSubView subview, float maximum)
	{
		if (isUpdateRequired())
		{
			super.setData(data, subview);
			setupPainters(data, subview);
			setUpdateComplete();
		}
		return new FList<MapPainter>(super.misorientationPainter, super.boundaryPainter, super.selectedGrainPainter);
	}
	
	
	private void setupPainters(ISkewGrid skewdata, MapSubView subview)
	{
		@SuppressWarnings("unchecked")
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		
		Spectrum misorientationData = new Spectrum(data.size());

		for (int i = 0; i < data.size(); i++)
		{
			double v = data.get(i).average;
			misorientationData.set(i, (float)v);
		}
		
		misorientationPainter.setData(misorientationData);
	}

	@Override
	public void writeData(ISkewGrid skewdata, MapSubView subview, BufferedWriter writer) throws IOException
	{
		@SuppressWarnings("unchecked")
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		
		writer.write("index, x, y, grain, average, north, east, south, west \n");
		
		for (MisAnglePoint point : data.getBackingList())
		{
			writer.write(
					point.getIndex() + ", " + 
					point.getX() + ", " + 
					point.getY() + ", " + 
					point.grain + ", " + 
					fmt(point.average) + ", " + 
					fmt(point.north) + ", " + 
					fmt(point.east) + ", " + 
					fmt(point.south) + ", " + 
					fmt(point.west) + ", " + 
					"\n"
				);
		}
	}
	
	@Override
	public boolean canWriteData()
	{
		return true;
	}
}
