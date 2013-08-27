package skew.views.misorientation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.SecondaryView;
import skew.datasources.misorientation.drawing.BoundaryMapPainter;
import skew.datasources.misorientation.drawing.SelectedGrainPainter;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;

public class GrainSecondaryView extends SecondaryView
{
	
	protected BoundaryMapPainter boundaryPainter;
	protected SelectedGrainPainter selectedGrainPainter;

	protected ISkewGrid<MisAngle> misModel;
	protected GrainModel grainModel;
	
	public GrainSecondaryView(ISkewGrid<MisAngle> misModel, GrainModel grainModel)
	{
		this(misModel, grainModel, Color.black);
	}
	
	public GrainSecondaryView(ISkewGrid<MisAngle> misModel, GrainModel grainModel, Color boundaryColor)
	{
		super("Grain Boundaries");
		boundaryPainter = new BoundaryMapPainter(misModel, boundaryColor);
		selectedGrainPainter = new SelectedGrainPainter(grainModel);
		setData(misModel, grainModel);
	}
	
	
	
	
	protected void setData(ISkewGrid<MisAngle> misModel, GrainModel grainModel)
	{
		this.misModel = misModel;
		this.grainModel = grainModel;
		boundaryPainter.setData(misModel);
		selectedGrainPainter.setData(grainModel);
	}


	@Override
	public List<MapPainter> getPainters(MapSubView subview,	float maximum) {
		setData(misModel, grainModel);
		return new FList<MapPainter>(boundaryPainter, selectedGrainPainter);
	}


	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue) {
		return new ArrayList<AxisPainter>();
	}

	@Override
	public Map<String, String> getSummaryData(int x, int y) {
		Map<String, String> values = new LinkedHashMap<>();

		values.put("Grain", ""+misModel.getData(x, y).grainIndex);
		
		return values;
	}

	@Override
	public List<String> getSummaryHeaders() {
		return new FList<>("Grain");
	}
	
}
