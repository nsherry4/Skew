package misorientation.viewer;

import java.awt.Color;
import java.util.List;

import scidraw.drawing.common.Spectrums;
import scidraw.drawing.map.palettes.AbstractPalette;


public class EBSDPalette extends AbstractPalette
{
	
	private final static double[][] ebsd = { 
		{ 0.10, 0.24, 0.45, 0 }, //dark blue
		//{ 0.13, 0.29, 0.53, 0.3f },
		{ 0.16, 0.35, 0.65, 0.12f }, //light blue
		{ 0.35, 0.69, 0.03, 0.12f }, //green
		{ 0.89, 0.73, 0.00, 0.4f }, //yellow
		{ 0.81, 0.36, 0.00, 0.24f }, //orange
		{ 0.64, 0.00, 0.00, 0.12f }  //red
	};
	private List<Color> spectrum;

	public EBSDPalette()
	{
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
