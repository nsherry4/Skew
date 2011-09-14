package misorientation.calculation;

 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;
import plural.executor.map.implementations.SimpleMapExecutor;

import commonenvironment.AlphaNumericComparitor;
import fava.signatures.FnEach;
import fava.signatures.FnMap;
 
public class MatrixList {
	private static int startNum=1; //by default
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
