package skew.views.misorientation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.google.common.base.Optional;

import scidraw.drawing.map.painters.MapPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.Summary;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;


public class LocalView extends MisAngleView
{
	protected float invalidValue = -1f;
	
	public LocalView(ISkewGrid<MisAngle> misModel) {
		super("Local Misorientation", misModel, null);
	}


	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return new SpinnerNumberModel(2, 0.0, 180.0, 0.1);
	}


	

	@Override
	public List<Summary> getMapSummary() {
		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary(getTitle() + " Map");
		summaries.add(s);
		s.addHeader("Average");
		
		
		double average = 0d;
		int count = 0;
		for (ISkewPoint<MisAngle> point : misModel) {
			if (!point.isValid()) continue;
			if (!point.getData().average.isPresent()) continue;
			average += point.getData().average.get();
			count++;
		}
		average /= count;
		
		
		s.addValue("Average", formatMisValue(Optional.of(average)));
	
		return summaries;
	}
	
	
	@Override
	public List<Summary> getPointSummary(int x, int y)
	{
		
		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary(getTitle());
		summaries.add(s);
		s.addHeader("8-Way Average", "Above", "Below", "Right", "Left");
		
		
		if (!misModel.getPoint(x, y).isValid()) return summaries;
		
		
		MisAngle point = misModel.getData(x, y);
		s.addValue("8-Way Average", formatMisValue(point.average));
		s.addValue("Above", formatMisValue(point.north));
		s.addValue("Below", formatMisValue(point.south));
		s.addValue("Right", formatMisValue(point.east));
		s.addValue("Left", formatMisValue(point.west));
		
		return summaries;
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
			setupPainters();
			setUpdateComplete();
		}
		return new FList<MapPainter>(super.misorientationPainter);
	}
	
	
	private void setupPainters()
	{
		
		Spectrum misorientationData = new Spectrum(misModel.size());

		for (int i = 0; i < misModel.size(); i++)
		{
			Optional<Double> average = misModel.getData(i).average;
			if (misModel.getPoint(i).isValid() && average.isPresent()) {
				misorientationData.set(i, average.get().floatValue());	
			} else {
				misorientationData.set(i, invalidValue);
			}
			
		}
		
		misorientationPainter.setData(misorientationData);
	}
	

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {}

}
