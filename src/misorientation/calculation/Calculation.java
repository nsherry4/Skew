package misorientation.calculation;

import java.io.Writer;
import java.util.Collections;
import java.util.List;

import commonenvironment.AlphaNumericComparitor;
import commonenvironment.IOOperations;

import scitypes.Coord;

public class Calculation {

	public static void calculate(List<String> filenames, Coord<Integer> mapSize, Writer writer)
	{
		
		Collections.sort(filenames, new AlphaNumericComparitor());
		String commonPrefix = IOOperations.getCommonFileName(filenames);
		
		
	}
	
}
