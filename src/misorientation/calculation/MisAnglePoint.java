package misorientation.calculation;

public class MisAnglePoint {
	
	private	double average; //average of eight neighbours (<5)
	private double south; //angle with the south neighbours
	private double east;  //angle with the east neighbours
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
