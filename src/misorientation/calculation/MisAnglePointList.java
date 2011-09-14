package misorientation.calculation;

import java.util.ArrayList;

public class MisAnglePointList {
	
	private int width;
	private int height;
	private ArrayList<MisAnglePoint> values;
	
	
	public MisAnglePointList(int width, int height){
		this.width = width;
		this.height = height;
		this.values = new ArrayList<MisAnglePoint>(width*height);
		for(int i=0; i<width*height;i++){ 
			values.add(new MisAnglePoint(i/width, i%width));
		}
		
	}
	
	public int getWidth(){return this.width;}
	public int getHeight(){return this.height;}
	public ArrayList<MisAnglePoint> getAnglePointList(){return this.values;}
	public MisAnglePoint getAnglePoint(int position){
		return values.get(position);
	}	
		
		
	 
}
