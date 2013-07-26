package skew.packages.misorientation.view.misangle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import scidraw.drawing.map.painters.MapPainter;
import scitypes.Spectrum;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;
import fava.functionable.FList;


public class LocalView extends MisAngleView
{
	public LocalView(MisAngleGrid<? extends MisAnglePoint> misorientationModel) {
		super(misorientationModel);
	}

	public String toString(){ return "Local Misorientation"; }

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return new SpinnerNumberModel(2, 0.0, 180.0, 0.1);
	}

	@Override
	public String getSummaryText(int x, int y)
	{
		
		MisAnglePoint point = misModel.get(x, y);

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
		return new FList<MapPainter>(super.misorientationPainter);
	}
	
	
	private void setupPainters(MapSubView subview)
	{
		
		Spectrum misorientationData = new Spectrum(misModel.size());

		for (int i = 0; i < misModel.size(); i++)
		{
			double v = misModel.get(i).average;
			misorientationData.set(i, (float)v);
		}
		
		misorientationPainter.setData(misorientationData);
	}

	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException
	{
	
		writer.write("index, x, y, grain, average, north, east, south, west \n");
		
		for (MisAnglePoint point : misModel.getBackingList())
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
