package skew.packages.misorientation.view.misangle;

import java.util.List;

import fava.datatypes.Pair;
import fava.functionable.FList;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.SigDigits;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.packages.misorientation.drawing.EBSDPalette;
import skew.packages.misorientation.view.MisorientationView;

public abstract class MisAngleView extends MisorientationView
{

	AbstractPalette thermal;
	
	FList<AbstractPalette> misorientationPalettes;
	protected RasterSpectrumMapPainter misorientationPainter;
	
	
	
	protected AxisPainter spectrum;
	
	public MisAngleView()
	{
		super();
		
		thermal = new EBSDPalette();
		
		misorientationPalettes = new FList<AbstractPalette>(super.greyEmpty, thermal);
		misorientationPainter = new RasterSpectrumMapPainter(misorientationPalettes, null);

		
	}
	
	@Override
	public List<AxisPainter> getAxisPainters(ISkewGrid data, MapSubView subview, float maxValue)
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
