package skew.views.misorientation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.google.common.base.Optional;

import scidraw.drawing.map.painters.MapPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.Summary;
import skew.models.grain.Grain;
import skew.models.grain.GrainUtil;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;


public class MagnitudeView extends MisAngleView
{
	public MagnitudeView(ISkewGrid<MisAngle> misModel, ISkewGrid<GrainPixel> grainModel) {
		super("Grain Magnitude", misModel, grainModel);
	}


	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		GrainMagnitudeSubView gms = (GrainMagnitudeSubView)subView;
		
		float grainVal;
		float maxVal = 0;
		for (Grain g : GrainUtil.getGrains(grainModel))
		{
			grainVal = (float) gms.select(new double[]{g.magMin, g.magMax, g.magAvg});
			maxVal = Math.max(grainVal, maxVal);
		}
		
		maxVal = (int)(maxVal * 10);
		maxVal /= 10f;
		return new SpinnerNumberModel(maxVal, 0.0, 180.0, 0.1);
	}


	@Override
	public List<Summary> getMapSummary() {
		return new ArrayList<>();
	}
	
	
	@Override
	public List<Summary> getPointSummary(int x, int y)
	{
		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary(getTitle());
		summaries.add(s);
		s.addCanonicalKeys("Magnitude (Min)", "Magnitude, (Avg)", "Magnitude (Max)");
		
	
		GrainPixel grainData = grainModel.getData(x, y);
			
		if (!grainData.grainIndex.isPresent()) return summaries;
		Grain g = grainData.grain;
		
		s.addValue("Magnitude (Min)", formatMisValue(Optional.of(g.magMin)));
		s.addValue("Magnitude (Avg)", formatMisValue(Optional.of(g.magAvg)));
		s.addValue("Magnitude (Max)", formatMisValue(Optional.of(g.magMax)));
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
			GrainPixel grainData = grainModel.getData(i);
			double v;
			if (!grainData.grainIndex.isPresent())
			{
				v = -1;
			} else {
				Grain g = grainData.grain;
				v = mag.select(new double[]{g.magMin, g.magMax, g.magAvg});
			}
			misorientationData.set(i, (float)v);
		}
	
		
		misorientationPainter.setData(misorientationData);
	}



	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {}
	
}
