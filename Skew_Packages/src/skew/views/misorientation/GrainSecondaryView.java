package skew.views.misorientation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.SecondaryView;
import skew.core.viewer.modes.views.Summary;
import skew.datasources.misorientation.drawing.BoundaryMapPainter;
import skew.datasources.misorientation.drawing.SelectedGrainPainter;
import skew.models.grain.Grain;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;

public class GrainSecondaryView extends SecondaryView
{
	
	protected BoundaryMapPainter boundaryPainter;
	
	protected boolean selectable;
	protected SelectedGrainPainter selectedGrainPainter;

	protected ISkewGrid<MisAngle> misModel;
	protected GrainModel grainModel;
	
	public GrainSecondaryView(ISkewGrid<MisAngle> misModel, GrainModel grainModel)
	{
		this(misModel, grainModel, Color.black, true);
	}
	
	public GrainSecondaryView(ISkewGrid<MisAngle> misModel, GrainModel grainModel, Color boundaryColor, boolean selectable)
	{
		super("Grain", false);
		boundaryPainter = new BoundaryMapPainter(misModel, boundaryColor);
		this.selectable = selectable;
		selectedGrainPainter = new SelectedGrainPainter(grainModel);
		setData(misModel, grainModel);
	}
	
	
	
	
	protected void setData(ISkewGrid<MisAngle> misModel, GrainModel grainModel)
	{
		this.misModel = misModel;
		this.grainModel = grainModel;
		boundaryPainter.setData(misModel);
		if (selectable) selectedGrainPainter.setData(grainModel);
	}


	@Override
	public List<MapPainter> getPainters(MapSubView subview,	float maximum) {
		setData(misModel, grainModel);
		if (!selectable) {
			return new FList<MapPainter>(boundaryPainter);
		} else {
			return new FList<MapPainter>(boundaryPainter, selectedGrainPainter);
		}
	}


	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue) {
		return new ArrayList<AxisPainter>();
	}

	@Override
	public List<Summary> getSummary(int x, int y) {

		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary(getTitle());
		summaries.add(s);
		s.addHeader("Number", "Size");

		MisAngle misData = misModel.getData(x, y);
		if (!misData.grainIndex.is()) return summaries;
		
		s.addValue("Number", ""+misData.grainIndex.get());
		Grain g = grainModel.getGrain(misData);
		s.addValue("Size", g.points.size() + " pixels");
		
		
		return summaries;
	}

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {
		if (!selectable) return;
		selectedGrainPainter.setPointSelected(misModel.getPoint(x, y), deselectAll);
		
	}


	

}
