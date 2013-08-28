package skew.views.misorientation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.Spectrum;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.core.viewer.modes.views.Summary;
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
	public List<Summary> getSummary(int x, int y)
	{
		return new ArrayList<>();
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


	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {}
	
}
