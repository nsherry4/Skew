package skew.packages.misorientation.view;

import skew.core.model.SkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.packages.misorientation.drawing.BoundaryMapPainter;
import skew.packages.misorientation.drawing.SelectedGrainPainter;

public abstract class MisorientationView extends MapView
{
	
	protected BoundaryMapPainter boundaryPainter;
	protected SelectedGrainPainter selectedGrainPainter;

	public MisorientationView()
	{

		super();
		
		boundaryPainter = new BoundaryMapPainter();
		selectedGrainPainter = new SelectedGrainPainter();
	}
	
	protected void setData(SkewGrid data, MapSubView subview)
	{
		boundaryPainter.setData(data);
		selectedGrainPainter.setData(data);
	}
	
}
