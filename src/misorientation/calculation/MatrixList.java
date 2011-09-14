package misorientation.calculation;

 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import commonenvironment.AlphaNumericComparitor;
 
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
	 
	public void loadMatrixList(List<String> filenames){
		Collections.sort(filenames, new AlphaNumericComparitor());
		Iterator<String> fp=filenames.iterator();
		while(fp.hasNext()){ 
				String filename = (String)fp.next();  
				//System.out.println(filename);
				//System.out.println(IndexFileName.getFileNumber(filename));
				(values.get(IndexFileName.getFileNumber(filename)-startNum)).setOrientationMatrix(filename); 
				 
		}
	}

}
