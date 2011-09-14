package misorientation.calculation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

 
import java.util.ArrayList;
import java.util.List;

 

import scitypes.Coord;
import scitypes.SigDigits;

public class Calculation {
	 
	 
	public static void calculate(List<String> filenames, Coord<Integer> mapSize, Writer writer ) throws IOException
	{
		
		 MatrixList matrixlist = new MatrixList(mapSize.x, mapSize.y);
		 matrixlist.loadMatrixList(filenames); 
		 MisAnglePointList anglelist = new MisAnglePointList(mapSize.x, mapSize.y);
		 calculateAngleList(matrixlist, anglelist,mapSize.x,mapSize.y);
		 writeAngleList(writer, anglelist);
	     	
	}

	private static void writeAngleList(Writer writer , MisAnglePointList anglelist)throws IOException {
		 
		//BufferedWriter output = new BufferedWriter(writer);
		int row,col;
		float angle,east,south;
		for (int i=0; i<anglelist.getHeight()*anglelist.getWidth();i++){
			try{
				MisAnglePoint point = anglelist.getAnglePoint(i);
				row = point.getRow();
				col = point.getCol();
				angle = (float) point.getAverage();
				east = (float) point.getEast();
				south = (float) point.getSouth();
				
				  ;
				
				writer.write(row+" "+ col +" "+ SigDigits.roundFloatTo( angle, 8)+" "+SigDigits.roundFloatTo(east, 8)+" "
						+SigDigits.roundFloatTo( south, 8)+"\n");
			}catch(IOException e){
				e.printStackTrace();
			}
			
			 
		}
		
		writer.flush();
	}	

	private static void calculateAngleList(MatrixList matrixlist,
			MisAnglePointList anglelist,int width,int height) {
		 
		 int n,w,e,s,nw,sw,se,ne,row,col, points=0 ;
		 double angle_total=0., angle;
		 
		 
		 for(int i=0;i<width*height;i++){
			  
			 
			 if(matrixlist.getMatrix(i).matrixOK()){
				  
				 points=0;
				 angle_total=0.;
				 //deside center point and its 8 eight neighbours' indeces
				 
				 
				 row=anglelist.getAnglePoint(i).getRow();
				 col=anglelist.getAnglePoint(i).getCol(); 
				
				 n=(row-1)*width+col;
				 
				 s=(row+1)*width+col;
				 w=i-1;
				 e=i+1;  
				 nw=n-1;
				 ne=n+1;
				 sw=s-1;
				 se=s+1;
				 
				 if(row==0) {n=-1;nw=-1;ne=-1;} 
				 if(row==height-1) {s=-1;sw=-1;se=-1;}
				 if(col==0) {w=-1;sw=-1;nw=-1;}
				 if(col==width-1) {e=-1;ne=-1;se=-1;} 
				  
				 if(n>=0) {
					 angle=0.;
					 
					 if(matrixlist.getMatrix(n).matrixOK()){
						 
							  
						 
							 angle =  calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(n));
							 
					     
						 if(angle <5.){
							 angle_total+=angle;
							 points++;
						 }
						  
					 }
				 }
				 if(s >=0){
					 angle=0.;
					 if(matrixlist.getMatrix(s).matrixOK()){
						 
						 
							 angle =  calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(s));
							 
						 if(angle <5.){
							 angle_total+=angle;
							 points++;
						 }
						 anglelist.getAnglePoint(i).setSouth(angle);
						  
					 } 
				 }
				 if(w >=0){
					 angle=0.;
					 if(matrixlist.getMatrix(w).matrixOK()){
						 
						 
							 angle =  calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(w));
							 
						 if(angle <5.){
							 angle_total+=angle;
							 points++;
						 }  
					 } 
				 }
				 if(e >=0){
					 angle=0.;
					 if(matrixlist.getMatrix(e).matrixOK()){
						 //angle =  calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(e));
						  
						 
							 angle =  calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(e));
							 
						 if(angle <5.){
							 angle_total+=angle;
							 points++;
						 }
						 anglelist.getAnglePoint(i).setEast(angle);
						  
					 } 
				 } 
				 if(nw >=0){
					 angle=0.;
					 if(matrixlist.getMatrix(nw).matrixOK()){
						 angle =  calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(nw));
					 
						 if(angle <5.){
							 angle_total+=angle;
							 points++;
						 }  
					 } 
				 } 
				 if(ne >=0){
					 angle=0.;
					 if(matrixlist.getMatrix(ne).matrixOK()){
						 angle =  calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(ne)); 
						 if(angle <5.){
							 angle_total+=angle;
							 points++;
						 }  
					 } 
				 } 
				 if(sw >=0){
					 angle=0.;
					 if(matrixlist.getMatrix(sw).matrixOK()){
						 angle =  calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(sw));
					 
						 if(angle <5.){
							 angle_total+=angle;
							 points++;
						 }  
					 } 
				 } 
				 if(se >=0){
					 angle=0.;
					 if(matrixlist.getMatrix(se).matrixOK()){
						 angle =  calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(se));
					 
						 if(angle <5){
							 angle_total+=angle;
							 points++;
						 }  
					 } 
				 } 
						 
				 if(points!=0){
					 //System.out.println(i+ " angle="+angle_total/points);
					 anglelist.getAnglePoint(i).setAverage(angle_total/points);
					
				 } 
				 else{
					 anglelist.getAnglePoint(i).setAverage(0.0);
				 }
				 //printf("%10d %10d %15.8f %15.8f %15.8f \n",row, col,pointList[i].average,pointList[i].east,pointList[i].south);
			 }
		 }
		
	}

	private static double calculatAngle(OrientationMatrix gA,
			OrientationMatrix gB) {
		 
		double[][] i_gA,delta_g,op,mis;
		
		double minAngle=400.,angle,trace,temp;  
		
		//gA = mA.getMatrix();
		i_gA = new double[3][3];
		delta_g = new double[3][3];
		mis = new double[3][3];
		op = new double[3][3];
		
		invert3(gA.getMatrix(),i_gA); 
		prodmat(gB.getMatrix(),i_gA,delta_g);
	 
		CubicSymOP operators= new CubicSymOP(); 
	        
		 
	 
		for(int i=0;i<operators.getNumOP();i++){
			copyOP(op,operators.getOP(i)); 
		    prodmat(delta_g,op,mis);
			 
			trace = mis[0][0]+mis[1][1]+mis[2][2];
			temp= (trace-1.0)/2.0;
			if(temp>1.0) temp = 1.0;
			if (temp <-1.0) temp = -1.0;
			angle =  Math.acos(temp)/Math.PI*180.;  
			 
			if(minAngle >angle)
			   minAngle = angle;
	   }    
	
	   return minAngle; 
	}
	 
	public static void printMat(double[][] op){
		System.out.println(op[0][0]+","+op[0][1]+","+op[0][2]+","
				+op[1][0]+","+op[1][1]+","+op[1][2]+","
				+op[2][0]+","+op[2][1]+","+op[2][2]);
	}
	private static void copyOP(double[][] op, int[][] op2) {
		 
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				op[i][j]=(double)op2[i][j];
			}
				
		}
		
	}

	 

	private static void prodmat(double[][] a, double[][] b,
			double[][] ab) {
		 
		 int i,j,k; 
	     int n=3;

	       for(i=0; i<n; i++)
	          for(j=0;j<n;j++)
	              ab[i][j]=0.;
	 

	        for(i=0; i<n; i++)
	          for(j=0;j<n;j++)
	             for(k=0;k<n;k++)
	               ab[i][j]+=a[i][k]*b[k][j];
	}

	private static boolean invert3(double[][] mat, double[][] imat) {
		 
		 int i, j;
		  
		 double det;
		 boolean flag;

		  flag=true;
		   
		  
		   
		 
		  imat[0][0]=mat[1][1]*mat[2][2]-mat[1][2]*mat[2][1];
			 
		  imat[0][1]=-mat[0][1]*mat[2][2]+mat[0][2]*mat[2][1];
			 
		  imat[0][2]=mat[0][1]*mat[1][2]-mat[0][2]*mat[1][1];
			 
		  imat[1][0]=-mat[1][0]*mat[2][2]+mat[2][0]*mat[1][2];
			 
		  imat[1][1]=mat[0][0]*mat[2][2]-mat[2][0]*mat[0][2];
			 
		  imat[1][2]=-mat[0][0]*mat[1][2]+mat[0][2]*mat[1][0];
			 
		  imat[2][0]=mat[1][0]*mat[2][1]-mat[1][1]*mat[2][0];
		 
		  imat[2][1]=-mat[0][0]*mat[2][1]+mat[0][1]*mat[2][0];
			 
		  imat[2][2]=mat[0][0]*mat[1][1]-mat[1][0]*mat[0][1];
			 
		  det=mat[0][0]*mat[1][2]*mat[2][1]+mat[0][1]*mat[1][0]*mat[2][2];
			 
		  det=det+mat[0][2]*mat[1][1]*mat[2][0]-mat[0][2]*mat[1][0]*mat[2][1];
			 
		  det=det-mat[0][1]*mat[1][2]*mat[2][0]-mat[0][0]*mat[1][1]*mat[2][2];
			 
		  det=(-1.)*det;
			 
		  if(det!=0){
		      for(i=0; i<3; i++)
		        for(j=0;j<3; j++)
		           imat[i][j]=imat[i][j]/det;
		  }
		 
		   else
		     flag = false;
		   

		   return flag;
		
	}
	
	public static void main(String[] args) throws Exception { 
		
        if (args.length != 4) {
        	System.out.println("Usage: [indexFileDirName][outputFileName][mapWidth][mapHeight]");
            System.exit(1);
        }

        
        List<String> fileNames = new ArrayList<String>();
        File pathName = new File(args[0]);
        String[] dirContents;
        if(pathName.isDirectory()){
        	dirContents = pathName.list();
        	//System.out.println("dir "+dirContents.length);;
        	for(int i=0; i<dirContents.length;i++){
        		//if((new File(dirContents[i])).isFile())
        			fileNames.add(pathName.getAbsolutePath() + "/" + dirContents[i]);	
        	}
        }
        
       // System.out.println(fileNames.size());
        
        if(fileNames.size()>0){
        	Writer output = null;
        	Coord<Integer> mapSize = new Coord<Integer>(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        	
        	output = new BufferedWriter(new FileWriter(new File(args[1])));
        	calculate(fileNames,mapSize,output);   
        	output.close();
        }
    } 
	
}
