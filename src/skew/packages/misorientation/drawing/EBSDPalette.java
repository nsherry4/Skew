package skew.packages.misorientation.drawing;

import java.awt.Color;
import java.util.List;

import scidraw.drawing.common.Spectrums;
import scidraw.drawing.map.palettes.AbstractPalette;


public class EBSDPalette extends AbstractPalette
{
	
	private final static int[] ebsd = { 
		26,  61,  115, 0, //dark blue
		41,  89,  166, 31, //light blue
		89,  176, 8,   31, //green
		227, 186, 0,   101, //yellow
		207, 92,  0,   61, //orange
		163, 0,   0,   31  //red
	};
	private List<Color> spectrum;

	public EBSDPalette()
	{
		super();
		this.spectrum = Spectrums.generateSpectrum(1000, ebsd, 1, 1);
	}
	

	@Override
	public Color getFillColour(double intensity, double maximum)
	{
		double percentage;
		percentage = intensity / maximum;
		
		int index = (int)(spectrum.size() * percentage);
		if (index >= spectrum.size()) index = spectrum.size() - 1;
		if (index < 0) index = 0;
				
		return spectrum.get(index);
	}

}
