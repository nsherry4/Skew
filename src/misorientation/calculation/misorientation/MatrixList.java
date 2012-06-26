package misorientation.calculation.misorientation;
/**
 * MatrixList defines the structure for storing the orientation matrix for all scan points in an area scan. 
 * These matrixes are loaded from index files. It accepts the index file format for both XMAS and FOXMAS
 * @author Jinhui Qin, 2011
 *
 */
 

import java.util.ArrayList;


public class MatrixList {
	
	public ArrayList<OrientationMatrix> values;
	public int width;
	public int height;
	
	
	 
	public MatrixList(int width, int height){
		this.width = width;
		this.height = height;
		this.values = new ArrayList<OrientationMatrix>(width*height);
		for(int i=0; i<width*height;i++){
			values.add(new OrientationMatrix());
		}
	}

	 

	

}


