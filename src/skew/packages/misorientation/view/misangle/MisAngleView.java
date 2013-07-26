package skew.packages.misorientation.view.misangle;

import java.util.List;

import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.SigDigits;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;
import skew.packages.misorientation.drawing.EBSDPalette;
import fava.datatypes.Pair;
import fava.functionable.FList;

public abstract class MisAngleView extends MapView
{

	AbstractPalette thermal;
	
	FList<AbstractPalette> misorientationPalettes;
	protected RasterSpectrumMapPainter misorientationPainter;
	
	
	
	protected AxisPainter spectrum;
	MisAngleGrid<? extends MisAnglePoint> misModel;
	
	public MisAngleView(MisAngleGrid<? extends MisAnglePoint> misorientationModel)
	{
		super();
		
		this.misModel = misorientationModel;
		
		thermal = new EBSDPalette();
		
		misorientationPalettes = new FList<AbstractPalette>(super.greyEmpty, thermal);
		misorientationPainter = new RasterSpectrumMapPainter(misorientationPalettes, null);

		
	}
	
	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
	
		List<Pair<Float, String>> axisMarkings = new FList<Pair<Float,String>>();
		
		axisMarkings.add(  new Pair<Float, String>(0.0f, "" + 0)  );
		axisMarkings.add(  new Pair<Float, String>(0.25f, "" + SigDigits.roundFloatTo((float)(maxValue * 0.25), 3))  );
		axisMarkings.add(  new Pair<Float, String>(0.5f, "" + SigDigits.roundFloatTo((float)(maxValue * 0.5), 3))  );
		axisMarkings.add(  new Pair<Float, String>(0.75f, "" + SigDigits.roundFloatTo((float)(maxValue * 0.75), 3))  );
		axisMarkings.add(  new Pair<Float, String>(1f, "" + SigDigits.roundFloatTo((float)maxValue, 3))  );
		
		
		AxisPainter spectrum = new SpectrumCoordsAxisPainter(
				false, 
				null, 
				null, 
				null, 
				null, 
				null, 
				true, 
				20, 
				256, 
				misorientationPalettes, 
				false, 
				"Misorientation Angle in Degrees", 
				1,
				false,
				axisMarkings);
		
		
		return new FList<AxisPainter>(spectrum);
	}
	
		
}
