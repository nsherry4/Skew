package skew.packages.misorientation.datasource.calculation.misorientation;
/**
 * IndexFileName provides the routine for get the file number from a given index filename, assuming the
 * filename is in a format as: **_number.*
 * @author Jinhui Qin, 2011
 *
 */
public class IndexFileName {
	public static int getFileNumber(String filename){
		return Integer.parseInt(filename.substring(filename.lastIndexOf("_")+1, filename.lastIndexOf("."))  );
	}
}
