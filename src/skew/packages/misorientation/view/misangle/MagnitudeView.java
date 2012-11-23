package skew.packages.misorientation.view.misangle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fava.functionable.FList;

import scidraw.drawing.map.painters.MapPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.packages.misorientation.model.Grain;
import skew.packages.misorientation.model.MisAngleGrid;
import skew.packages.misorientation.model.MisAnglePoint;
import skew.packages.misorientation.subview.GrainMagnitudeSubView;


public class MagnitudeView extends MisAngleView
{
	public String toString(){ return "Grain Magnitude"; }



	@Override
	public SpinnerModel scaleSpinnerModel(ISkewGrid skewdata, MapSubView subView)
	{
		@SuppressWarnings("unchecked")
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		GrainMagnitudeSubView gms = (GrainMagnitudeSubView)subView;
		
		float grainVal;
		float maxVal = 0;
		for (Grain g : data.grains)
		{
			grainVal = (float) gms.select(new double[]{g.magMin, g.magMax, g.magAvg});
			maxVal = Math.max(grainVal, maxVal);
		}
		
		maxVal = (int)(maxVal * 10);
		maxVal /= 10f;
		return new SpinnerNumberModel(maxVal, 0.0, 180.0, 0.1);
	}

	@Override
	public String getSummaryText(ISkewPoint skewpoint, ISkewGrid skewdata)
	{
	
		@SuppressWarnings("unchecked")
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		MisAnglePoint point = (MisAnglePoint)skewpoint;
		
		String grain = formatGrainValue(point.grain);
		String result = "Grain :" + grain;
		
		Grain g;
		try { g = data.grains.get(point.grain); }
		catch (ArrayIndexOutOfBoundsException e) { return result; }			
		
		if (g == null) return result; 
		
		String mag = formatMisorientationValue(g.magMin);
		return "Grain: " + grain + ", Magnitude: " + mag;
	}



	@Override
	public boolean hasSublist()
	{
		return true;
	}

	@Override
	public List<MapSubView> getSubList()
	{
		return new ArrayList<MapSubView>(Arrays.asList(new GrainMagnitudeSubView[]{
			new GrainMagnitudeSubView(0),
			new GrainMagnitudeSubView(1),
			new GrainMagnitudeSubView(2)
		}));
	}



	@Override
	public float getMaximumIntensity(ISkewGrid data, MapSubView subview)
	{
		return 0;
	}



	@Override
	public List<MapPainter> getPainters(ISkewGrid skewdata, MapSubView subview, float maximum)
	{
		@SuppressWarnings("unchecked")
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		
		if (isUpdateRequired())
		{
			super.setData(data, subview);
			setupPainters(data, subview);
			setUpdateComplete();
		}
		return new FList<MapPainter>(super.misorientationPainter, super.boundaryPainter, super.selectedGrainPainter);
	}
	
	
	private void setupPainters(MisAngleGrid<MisAnglePoint> data, MapSubView subview)
	{
		Spectrum misorientationData = new Spectrum(data.size());

		GrainMagnitudeSubView mag = (GrainMagnitudeSubView)subview;
		
		for (int i = 0; i < data.size(); i++)
		{
			int grain = data.get(i).grain;
			double v;
			if (grain == -1)
			{
				v = -1;
			} else {
				Grain g = data.grains.get(grain);
				v = mag.select(new double[]{g.magMin, g.magMax, g.magAvg});
			}
			misorientationData.set(i, (float)v);
		}
	
		
		misorientationPainter.setData(misorientationData);
	}



	@Override
	public void writeData(ISkewGrid skewdata, MapSubView subview, BufferedWriter writer) throws IOException
	{
		@SuppressWarnings("unchecked")
		MisAngleGrid<MisAnglePoint> data = (MisAngleGrid<MisAnglePoint>)skewdata;
		
		writer.write("grain, min, avg, max\n");
		
		for (Grain g : data.grains)
		{
			writer.write(g.index + ", " + fmt(g.magMin) + ", " + fmt(g.magAvg) + ", " + fmt(g.magMax) + "\n");
		}
	}



	@Override
	public boolean canWriteData()
	{
		return true;
	}

}
