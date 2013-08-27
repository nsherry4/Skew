package skew.views.subtraction;

import java.util.List;

import fava.datatypes.Pair;
import fava.functionable.FList;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.SigDigits;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.models.misorientation.MisAngle;
import skew.views.misorientation.LocalView;

public class LocalSubtractionView extends LocalView
{

	public LocalSubtractionView(ISkewGrid<MisAngle> misModel) {
		super(misModel);
		
		super.thermal = new ThermalScalePalette(false, true);
		super.misorientationPalettes = new FList<AbstractPalette>(super.nanEmptyPalette, super.thermal);
		super.misorientationPainter = new RasterSpectrumMapPainter(super.misorientationPalettes, null);
		
		super.invalidValue = Float.NaN;
		
	}

	@Override
	public List<AxisPainter> getAxisPainters(MapSubView subview, float maxValue)
	{
	
		List<Pair<Float, String>> axisMarkings = new FList<Pair<Float,String>>();
		
		
		axisMarkings.add(  new Pair<Float, String>(0.0f, "" + SigDigits.roundFloatTo((float)(maxValue * -1.0), 3))  );
		axisMarkings.add(  new Pair<Float, String>(0.25f, "" + SigDigits.roundFloatTo((float)(maxValue * -0.5), 3))  );
		
		axisMarkings.add(  new Pair<Float, String>(0.5f, "" + 0)  );
		
		axisMarkings.add(  new Pair<Float, String>(0.75f, "" + SigDigits.roundFloatTo((float)(maxValue * 0.5), 3))  );
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
				true,
				axisMarkings);
		
		
		return new FList<AxisPainter>(spectrum);
	}
	
	
}
