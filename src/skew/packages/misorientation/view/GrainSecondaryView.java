package skew.packages.misorientation.view;

import java.util.ArrayList;
import java.util.List;

import fava.functionable.FList;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.painters.axis.AxisPainter;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.SecondaryView;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;
import skew.packages.misorientation.drawing.BoundaryMapPainter;
import skew.packages.misorientation.drawing.SelectedGrainPainter;

public class GrainSecondaryView extends SecondaryView
{
	
	protected BoundaryMapPainter boundaryPainter;
	protected SelectedGrainPainter selectedGrainPainter;

	protected MisAngleGrid<? extends MisAnglePoint> model;
	
	public GrainSecondaryView(MisAngleGrid<? extends MisAnglePoint> model)
	{
		super();
		this.model = model;
		boundaryPainter = new BoundaryMapPainter();
		selectedGrainPainter = new SelectedGrainPainter();
	}
	
	
	protected void setData(ISkewGrid data)
	{
		boundaryPainter.setData(data);
		selectedGrainPainter.setData(data);
	}


	@Override
	public List<MapPainter> getPainters(MapSubView subview,	float maximum) {
		setData(model);
		return new FList<MapPainter>(boundaryPainter, selectedGrainPainter);
	}


	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue) {
		return new ArrayList<AxisPainter>();
	}
	
}
