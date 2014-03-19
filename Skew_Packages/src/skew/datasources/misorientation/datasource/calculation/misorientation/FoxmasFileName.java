package skew.datasources.misorientation.datasource.calculation.misorientation;
/**
 * FoxmasFileName provides the routine for get the file number from a given dat/ind/str filename, assuming the
 * filename is in a format as: **_number.*
 * @author Jinhui Qin, 2011
 *
 */
public class FoxmasFileName {
	public static int getFileNumber(String filename){
		return Integer.parseInt(filename.substring(filename.lastIndexOf("_")+1, filename.lastIndexOf("."))  );
	}
}
