package misorientation.calculation;

public class IndexFileName {
	public static int getFileNumber(String filename){
		return Integer.parseInt(filename.substring(filename.lastIndexOf("_")+1, filename.lastIndexOf("."))  );

	}
}
