package skew.views.misorientation;

import java.util.List;

import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scitypes.SigDigits;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.datasources.misorientation.drawing.EBSDPalette;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import fava.datatypes.Pair;
import fava.functionable.FList;

public abstract class MisAngleView extends MapView
{

	protected AbstractPalette thermal;
	
	protected FList<AbstractPalette> misorientationPalettes;
	protected RasterSpectrumMapPainter misorientationPainter;
	
	
	
	protected AxisPainter spectrum;
	
	protected ISkewGrid<MisAngle> misModel;
	protected ISkewGrid<GrainPixel> grainModel;
	
	public MisAngleView(String title, ISkewGrid<MisAngle> misModel, ISkewGrid<GrainPixel> grainModel)
	{
		super(title);
		
		this.misModel = misModel;
		this.grainModel = grainModel;
		
		thermal = new EBSDPalette();
		misorientationPalettes = new FList<AbstractPalette>(super.negativeValueEmptyPalette, thermal);
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
				null, null, null, null, 
				null, 
				true, 
				20, 256, 
				misorientationPalettes, 
				false, 
				"Misorientation Angle in Degrees", 
				1,
				false,
				axisMarkings);
		
		
		return new FList<AxisPainter>(spectrum);
	}
	
	
	
	
	
}
