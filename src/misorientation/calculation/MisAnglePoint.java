package misorientation.calculation;
/**
 * MisAnglePoint defines the structure for storing the mis-angle for one scan point in an area scan.
 * The mis-angle for each scan point is set as the average of all mis-angles to its 8  neighbors; 
 * It also records the angles to the east and to the south neighbors as the reference 
 * for drawing grain boundaries   
 * @author Jinhui Qin, 2011
 *
 */
public class MisAnglePoint {
	
	private	double average; //average of eight neighbors (if any<5)
	private double south; //angle with the south neighbor
	private double east;  //angle with the east neighbor
	private int row;  //the row of this point
	private int col;  //the column of this point
	public MisAnglePoint(int row, int col){
		  this.row=row;
		  this.col=col;
	      this.average = -1.;
	      this.south = -1.;
	      this.east = -1.;
	}
	public void setSouth(double south){
	      this.south = south;
	       
	}
	public void setEast(double east){
	      this.east = east;
	       
	}
	public void setAverage(double average){
	      this.average = average;
	       
	}
	public void setRow(int row){
		this.row=row;
		
	}
	public void setCol(int col){
		this.col = col;
	}
	public int getCol(){return col;}
	public int getRow(){return row;}
	public double getAverage(){return this.average;}
	public double getSouth(){return this.south;}
	public double getEast(){return this.east;}

}
