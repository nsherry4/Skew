package skew.views.misorientation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.models.grain.Grain;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;


public class InterGrainView extends MisAngleView
{


	public InterGrainView(ISkewGrid<MisAngle> misModel, GrainModel grainModel) {
		super("Intragrain Misorientation", misModel, grainModel);
	}


	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		IntraGrainSubView igv = (IntraGrainSubView)subView;
		return igv.getSpinnerModel(misModel);
	}

	@Override
	public Map<String, String> getSummaryData(int x, int y)
	{
		Map<String, String> values = new LinkedHashMap<>();
		
		MisAngle point = misModel.getPoint(x, y).getData();
		values.put("Grain", formatGrainValue(point.grainIndex));
		values.put("Misorientation", formatMisValue(point.intraGrainMisorientation));
		
		return values;
		
	}
	
	@Override
	public List<String> getSummaryHeaders() {
		return new FList<>("Grain", "Misorientation");
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
			setupPainters(subview);
			setUpdateComplete();
		}
		return new FList<MapPainter>(super.misorientationPainter);
	}
	
	
	private void setupPainters(MapSubView subview)
	{
		Spectrum misorientationData = new Spectrum(misModel.size());
		IntraGrainSubView igsv = (IntraGrainSubView)subview;
		
		boolean relative = igsv.getIndex() == 0;
		
		for (int i = 0; i < misModel.size(); i++)
		{
			MisAngle p = misModel.getData(i);
			if (p == null)	{ misorientationData.set(i, -1.0f); continue; }

			
			if (!p.grainIndex.is()) { misorientationData.set(i, -1.0f); continue; }
			Grain g = grainModel.getGrain(misModel.getPoint(i));
			
			
			float v = p.intraGrainMisorientation.get().floatValue();
			if (relative) v /= g.intraGrainMax;
			misorientationData.set(i, (float)v);
			
		}
		
		misorientationPainter.setData(misorientationData);
	}


	
}
