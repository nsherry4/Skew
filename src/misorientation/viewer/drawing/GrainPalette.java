package misorientation.viewer.drawing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import scidraw.drawing.map.palettes.AbstractPalette;

public class GrainPalette extends AbstractPalette
{

	//private static int slices = 4;
	//private static int steps = 4;
	private List<Color> colours = new ArrayList<Color>();
	
	public GrainPalette()
	{
		
		colours.add(new Color(204, 0,   0  ));  //red
		colours.add(new Color(117, 80,  123));  //purple
		colours.add(new Color(52,  101, 164));  //blue
		colours.add(new Color(115, 210, 22 ));  //green
		colours.add(new Color(193, 125, 17 ));  //brown
		colours.add(new Color(245, 121, 0  ));  //orange
		colours.add(new Color(237, 212, 0  ));  //yellow
		
		
		colours.add(new Color(161, 41,  109));  //red-purple
		colours.add(new Color(79,  67,  143));  //purple-blue
		colours.add(new Color(37,  187, 114));  //blue-green
		colours.add(new Color(177, 201, 19 ));  //green-brown
		colours.add(new Color(219, 122, 9  ));  //brown-orange
		colours.add(new Color(231, 165, 9  ));  //orange-yellow
		
		
	}
	

	
	@Override
	public Color getFillColour(double colourIndex, double dmax)
	{	
		int index = ((int)colourIndex) % colours.size();
		return colours.get(index);
	}

}
