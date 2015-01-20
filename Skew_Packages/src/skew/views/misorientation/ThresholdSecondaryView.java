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
	protected boolean showGrainBoundary;
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
		showGrainBoundary = true;
		setData(misModel, grainModel);
	}
	
	public void setShowGrainBoundary(boolean show) {
		showGrainBoundary = show;
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
		List<MapPainter> painters = new ArrayList<>();
		if (showGrainBoundary) painters.add(boundaryPainter);
		if (selectable && showGrainBoundary) painters.add(selectedGrainPainter);
		return painters;
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
		s.addCanonicalKeys("Number", "Size");

		GrainPixel grainData = grainModel.getData(x, y);
		if (!grainData.grainIndex.isPresent()) return summaries;
		
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
