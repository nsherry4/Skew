package skew.views.misorientation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import scidraw.drawing.map.painters.MapPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.models.grain.Grain;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;
import fava.datatypes.Maybe;
import fava.functionable.FList;


public class MagnitudeView extends MisAngleView
{
	public MagnitudeView(ISkewGrid<MisAngle> misModel, GrainModel grainModel) {
		super("Grain Magnitude", misModel, grainModel);
	}


	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		GrainMagnitudeSubView gms = (GrainMagnitudeSubView)subView;
		
		float grainVal;
		float maxVal = 0;
		for (Grain g : grainModel.grains)
		{
			grainVal = (float) gms.select(new double[]{g.magMin, g.magMax, g.magAvg});
			maxVal = Math.max(grainVal, maxVal);
		}
		
		maxVal = (int)(maxVal * 10);
		maxVal /= 10f;
		return new SpinnerNumberModel(maxVal, 0.0, 180.0, 0.1);
	}

	@Override
	public Map<String, String> getSummaryData(int x, int y)
	{
		Map<String, String> values = new LinkedHashMap<>();
		
		MisAngle point = misModel.getPoint(x, y).getData();
		
		values.put("Grain", formatGrainValue(point.grainIndex));
		
		if (!point.grainIndex.is()) return values;
		Grain g = grainModel.getGrain(point);
		
		values.put("Magnitude (Min)", formatMisValue(new Maybe<>(g.magMin)));
		values.put("Magnitude (Avg)", formatMisValue(new Maybe<>(g.magAvg)));
		values.put("Magnitude (Max)", formatMisValue(new Maybe<>(g.magMax)));
		return values;
	}


	@Override
	public List<String> getSummaryHeaders() {
		return new FList<>("Grain", "Magnitude (Min)", "Magnitude, (Avg)", "Magnitude (Max)");
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

		GrainMagnitudeSubView mag = (GrainMagnitudeSubView)subview;
		
		for (int i = 0; i < misModel.size(); i++)
		{
			MisAngle misData = misModel.getData(i);
			double v;
			if (!misData.grainIndex.is())
			{
				v = -1;
			} else {
				Grain g = grainModel.getGrain(misData);
				v = mag.select(new double[]{g.magMin, g.magMax, g.magAvg});
			}
			misorientationData.set(i, (float)v);
		}
	
		
		misorientationPainter.setData(misorientationData);
	}



}
