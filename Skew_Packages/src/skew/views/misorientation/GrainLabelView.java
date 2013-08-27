package skew.views.misorientation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.datasources.misorientation.drawing.GrainPalette;
import skew.models.grain.Grain;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;


public class GrainLabelView extends MapView
{
	
	RasterSpectrumMapPainter grainPainter;
	
	AbstractPalette grainpalette = new GrainPalette();
	private ISkewGrid<MisAngle> misModel;
	private GrainModel grainModel;
	
	
	public GrainLabelView(ISkewGrid<MisAngle> model, GrainModel grainModel)
	{
		super("Grain Labels");
		this.misModel = model;
		this.grainModel = grainModel;
		
		List<AbstractPalette> grainPalettes = new FList<AbstractPalette>(super.negativeValueEmptyPalette, grainpalette);
		grainPainter = new RasterSpectrumMapPainter(grainPalettes, null);
	}
	

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return null;
	}

	@Override
	public Map<String, String> getSummaryData(int x, int y)
	{
		Map<String, String> values = new LinkedHashMap<>();
		
		MisAngle point = misModel.getData(x, y);
		values.put("Grain", formatGrainValue(point.grainIndex));
		
		//if this point is not part of a grain, return without g
		if (!point.grainIndex.is()) return values;
		
		Grain g = grainModel.grains.get(point.grainIndex.get());
		values.put("Size", g.points.size() + " pixels");
		return values;
		
	}
	
	@Override
	public List<String> getSummaryHeaders() {
		return new FList<>("Grain", "Size");
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
		return new FList<MapPainter>(grainPainter);
	}

	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
		return new FList<AxisPainter>();
	}

	
	private void setupPainters(MapSubView subview)
	{
		
		Spectrum misorientationData = new Spectrum(misModel.size());
		
		for (int i = 0; i < misModel.size(); i++)
		{
			MisAngle misAngle = misModel.getData(i);
			if (!misAngle.grainIndex.is()) {
				misorientationData.set(i, -1f); 
				continue;
			}
			Grain g = grainModel.getGrain(misAngle);
			misorientationData.set(i, g.colourIndex);
		}
		
		grainPainter.setData(misorientationData);
	}


	
}
