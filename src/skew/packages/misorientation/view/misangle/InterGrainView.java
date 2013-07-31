package skew.packages.misorientation.view.misangle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.models.Grain.Grain;
import skew.models.Misorientation.MisAngle;
import skew.models.Misorientation.MisAngleGrid;
import skew.packages.misorientation.subview.IntraGrainSubView;
import fava.functionable.FList;


public class InterGrainView extends MisAngleView
{


	public InterGrainView(MisAngleGrid misorientationModel) {
		super(misorientationModel);
	}

	public String toString(){ return "Intragrain Misorientation"; }

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		IntraGrainSubView igv = (IntraGrainSubView)subView;
		return igv.getSpinnerModel(misModel);
	}

	@Override
	public String getSummaryText(int x, int y)
	{
		
		MisAngle point = misModel.get(x, y).getData();
	
		String grain = formatGrainValue(point.grain);
		String result = "Grain :" + grain;
		
		Grain g;
		try { g = misModel.grains.get(point.grain); }
		catch (ArrayIndexOutOfBoundsException e) { return result; }			
		
		if (g == null) return result; 
		
		String mag = formatMisorientationValue(point.intraGrainMisorientation);
		return "Grain: " + grain + ", Misorientation: " + mag + "\u00B0";
		
	}

	@Override
	public boolean hasSublist()
	{
		return true;
	}

	@Override
	public List<MapSubView> getSubList()
	{
		return new ArrayList<MapSubView>(Arrays.asList(new IntraGrainSubView[]{
				new IntraGrainSubView(0),
				new IntraGrainSubView(1)
			}));
	}

	@Override
	public float getMaximumIntensity(MapSubView subview)
	{
		IntraGrainSubView igsv = (IntraGrainSubView)subview;
		
		if (igsv.getIndex() == 0) return 1;
		
		return 0;
	}

	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
		IntraGrainSubView igsv = (IntraGrainSubView)subview;
		boolean relative = igsv.getIndex() == 0;
		
		if (!relative) return super.getAxisPainters(subview, maxValue);
		return new FList<AxisPainter>();
	}
	
	@Override
	public List<MapPainter> getPainters(MapSubView subview, float maximum)
	{

		if (isUpdateRequired())
		{
			setupPainters(misModel, subview);
			setUpdateComplete();
		}
		return new FList<MapPainter>(super.misorientationPainter);
	}
	
	
	private void setupPainters(MisAngleGrid data, MapSubView subview)
	{
		Spectrum misorientationData = new Spectrum(data.size());
		IntraGrainSubView igsv = (IntraGrainSubView)subview;
		
		boolean relative = igsv.getIndex() == 0;
		
		for (int i = 0; i < data.size(); i++)
		{
			MisAngle p = data.get(i).getData();
			if (p == null)	{ misorientationData.set(i, -1.0f); continue; }

			Grain g = data.getGrainAtPoint(data.get(i));
			if (g == null)	{ misorientationData.set(i, -1.0f); continue; }
			
			float v = (float) p.intraGrainMisorientation;
			if (relative) v /= g.intraGrainMax;
			misorientationData.set(i, (float)v);
			
		}
		
		misorientationPainter.setData(misorientationData);
	}

	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException
	{
		
		writer.write("index, x, y, grain, value, percent\n");
		
		for (ISkewPoint<MisAngle> point : misModel.getBackingList())
		{
			
			Grain g = misModel.getGrainAtPoint(point);
			String relative = "";
			if (g != null) {
				relative = fmt(point.getData().intraGrainMisorientation / g.intraGrainMax);
			} else {
				relative = fmt(-1f);
			}
			writer.write(
					point.getIndex() + ", " + 
					point.getX() + ", " + 
					point.getY() + ", " +
					point.getData().grain + ", " + 
					fmt(point.getData().intraGrainMisorientation) + ", " + 
					relative +  
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
