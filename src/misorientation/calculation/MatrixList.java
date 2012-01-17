package misorientation.calculation;
/**
 * MatrixList defines the structure for storing the orientation matrix for all scan points in an area scan. 
 * These matrixes are loaded from index files. It accepts the index file format for both XMAS and FOXMAS
 * @author Jinhui Qin, 2011
 *
 */
 
import java.util.ArrayList;
import java.util.List;

import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;

import fava.signatures.FnMap;
 
public class MatrixList {
	private static int startNum=1; //by default, assuming all index files are named with a number started from 1
	private ArrayList<OrientationMatrix> values;
	private int width;
	private int height;
	
	
	 
	public MatrixList(int width, int height){
		this.width = width;
		this.height = height;
		this.values = new ArrayList<OrientationMatrix>(width*height);
		for(int i=0; i<width*height;i++){
			values.add(new OrientationMatrix());
		}
	}
	public int getWidth(){
		return this.width;
	}
	public int getHeight(){
		return this.height;
	}
	public OrientationMatrix getMatrix(int position){
		return values.get(position);
	}
	 
	public MapExecutor<String, String> loadMatrixList(List<String> filenames){
		
//		Collections.sort(filenames, new AlphaNumericComparitor());
		
		FnMap<String, String> eachFilename = new FnMap<String, String>(){

			@Override
			public String f(String filename) {
				int index = IndexFileName.getFileNumber(filename)-startNum;
				values.get(index).setOrientationMatrix(filename); 	
				return "";
			}};
			
		MapExecutor<String, String> exec = new PluralMapExecutor<String, String>(filenames, eachFilename);
		exec.setName("Reading Files");
		return exec;
		
	}

}
