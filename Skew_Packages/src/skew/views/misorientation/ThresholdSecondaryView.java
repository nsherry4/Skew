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
import skew.datasources.misorientation.drawing.SelectedGrainPainter;
import skew.datasources.misorientation.drawing.ThresholdBoundaryMapPainter;
import skew.models.grain.Grain;
import skew.models.grain.GrainUtil;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;

public class ThresholdSecondaryView extends SecondaryView
{
	
	protected ThresholdBoundaryMapPainter boundaryPainter;
	
	protected boolean selectable;
	protected SelectedGrainPainter selectedGrainPainter;

	protected ISkewGrid<GrainPixel> grainModel;
	protected ISkewGrid<MisAngle> misModel; 
	
	public ThresholdSecondaryView(ISkewGrid<MisAngle> misModel, ISkewGrid<GrainPixel> grainModel, double boundary)
	{
		this(misModel, grainModel, Color.black, true, boundary);
	}
	
	public ThresholdSecondaryView(ISkewGrid<MisAngle> misModel, ISkewGrid<GrainPixel> grainModel, Color boundaryColor, boolean selectable, double boundary)
	{
		super("Grain", false);
		boundaryPainter = new ThresholdBoundaryMapPainter(misModel, boundaryColor, boundary);
		this.selectable = selectable;
		selectedGrainPainter = new SelectedGrainPainter(grainModel);
		setData(misModel, grainModel);
	}
	
	
	
	
	protected void setData(ISkewGrid<MisAngle> misModel, ISkewGrid<GrainPixel> grainModel)
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
	public List<Summary> getMapSummary() {
		return new ArrayList<>();
	}
	
	@Override
	public List<Summary> getPointSummary(int x, int y) {

		List<Summary> summaries = new ArrayList<>();
		Summary s = new Summary(getTitle());
		summaries.add(s);
		s.addHeader("Number", "Size");

		GrainPixel grainData = grainModel.getData(x, y);
		if (!grainData.grainIndex.is()) return summaries;
		
		s.addValue("Number", ""+grainData.grainIndex.get());
		Grain g = grainData.grain;
		s.addValue("Size", GrainUtil.getGrainPoints(grainModel, g).size() + " pixels");
		
		
		return summaries;
	}

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {
		if (!selectable) return;
		selectedGrainPainter.setPointSelected(grainModel.getPoint(x, y), deselectAll);
		
	}


	

}
