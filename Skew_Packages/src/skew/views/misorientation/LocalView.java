package skew.views.misorientation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import scidraw.drawing.map.painters.MapPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.models.misorientation.MisAngle;
import fava.datatypes.Maybe;
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
	public Map<String, String> getSummaryData(int x, int y)
	{
		
		Map<String, String> values = new LinkedHashMap<>();
		
		if (!misModel.getPoint(x, y).isValid()) {
			values.put("Point", "Invalid Point");
			return values;
		}
		
		MisAngle point = misModel.getData(x, y);
		
		values.put("8-Way Average", formatMisValue(point.average));
		values.put("Above", formatMisValue(point.north));
		values.put("Below", formatMisValue(point.south));
		values.put("Right", formatMisValue(point.east));
		values.put("Left", formatMisValue(point.west));
		
		return values;
	}
	
	@Override
	public List<String> getSummaryHeaders() {
		return new FList<>("8-Way Average", "Above", "Below", "Right", "Left");
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
			Maybe<Double> average = misModel.getData(i).average;
			if (misModel.getPoint(i).isValid() && average.is()) {
				misorientationData.set(i, average.get().floatValue());	
			} else {
				misorientationData.set(i, invalidValue);
			}
			
		}
		
		misorientationPainter.setData(misorientationData);
	}

}
