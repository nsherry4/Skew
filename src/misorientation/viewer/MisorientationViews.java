package misorientation.viewer;

import javax.swing.SpinnerNumberModel;

import scitypes.SigDigits;

import misorientation.model.Grain;
import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;

public enum MisorientationViews
{
	MISORIENTATION {
		
		public String toString(){ return "Misorientation"; }

		@Override
		public double defaultScale(MisAngleGrid data)
		{
			return 2;
		}

		@Override
		public SpinnerNumberModel scaleSpinnerModel(MisAngleGrid data)
		{
			return new SpinnerNumberModel(defaultScale(data), 0.0, 180.0, 0.1);
		}

		@Override
		public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
		{

			String avg = formatMisorientationValue(point.average);
			String east = formatMisorientationValue(point.east);
			String south = formatMisorientationValue(point.south);
			String west = formatMisorientationValue(point.west);
			String north = formatMisorientationValue(point.north);
			
			return ""+
				"Angles - " + 
				"Average: " + avg + 
				", \u2191" + north +
				", \u2192" + east + 
				", \u2193" + south +
				", \u2190" + west;
		}
		
	}, GRAINLABELS {
		
		public String toString(){ return "Grain Labels"; }

		@Override
		public double defaultScale(MisAngleGrid data)
		{
			if (data == null) return 1000;
			return data.grainCount;
		}

		@Override
		public SpinnerNumberModel scaleSpinnerModel(MisAngleGrid data)
		{
			return new SpinnerNumberModel(defaultScale(data), 0.0, data.grainCount, 50);
		}

		@Override
		public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
		{
			String grain = formatGrainValue(point.grain);
			String result = "Grain: " + grain;
			
			Grain g;
			try { g = data.grains.get(point.grain); }
			catch (ArrayIndexOutOfBoundsException e) { return result; }
			if (g == null) return result;
			
			result += ", Size: " + g.points.size() + " pixels";
			return result;
			
		}
		
	}, MAGNITUDE {
		
		public String toString(){ return "Magnitude"; }

		@Override
		public double defaultScale(MisAngleGrid data)
		{
			if (data == null) return 10;
			int max = 0;
			for (Grain g : data.grains)
			{
				max = (int) Math.max(max, g.magnitude);
			}
			return max + 1;
		}

		@Override
		public SpinnerNumberModel scaleSpinnerModel(MisAngleGrid data)
		{
			return new SpinnerNumberModel(defaultScale(data), 0.0, 180.0, 0.1);
		}

		@Override
		public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
		{
		
			String grain = formatGrainValue(point.grain);
			String result = "Grain :" + grain;
			
			Grain g;
			try { g = data.grains.get(point.grain); }
			catch (ArrayIndexOutOfBoundsException e) { return result; }			
			
			if (g == null) return result; 
			
			String mag = formatMisorientationValue(g.magnitude);
			return "Grain: " + grain + ", Magnitude: " + mag;
		}
		
	};
	
	private static String formatGrainValue(double value)
	{
		if (value < 0) return "None";
		return "#" + (int)value;
	}
	
	private static String formatMisorientationValue(double value)
	{
		String valString;
		valString = SigDigits.roundFloatTo((float)value, 3);
		if (value < 0) valString = "Boundary";
		
		return valString;
	}
	
	public abstract double defaultScale(MisAngleGrid data);
	public abstract SpinnerNumberModel scaleSpinnerModel(MisAngleGrid data);
	public abstract String getSummaryText(MisAnglePoint point, MisAngleGrid data);
}
