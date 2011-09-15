package misorientation.calculation;
/**
 * OrientationMatrix defines the structure for storing the orientation matrix for one scan point in an area scan. 
 * These matrixes are loaded from index files. It accepts the index file format for both XMAS and FOXMAS
 * @author Jinhui Qin, 2011
 *
 */
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class OrientationMatrix {
	private double matrix[][];
	private int fileNumber;
	public OrientationMatrix(){
		fileNumber = -1;
		this.matrix = new double[3][3];
	}
	 
	 
	
	public void setOrientationMatrix(String fileName)
	{
	
		if(loadOrientationMatrix(fileName)) this.fileNumber = IndexFileName.getFileNumber(fileName); 

	}
	public void printMatrix(){
		System.out.println("filenumber="+fileNumber+":"+matrix[0][0]+matrix[0][1]+matrix[0][2]+","
					+matrix[1][0]+matrix[1][1]+matrix[1][2]+"'"
							+matrix[2][0]+matrix[2][1]+matrix[2][2]);	
	}
	public int getFileNumber(){
		return this.fileNumber;
	}
	
	public boolean matrixOK(){
		return fileNumber != -1;
	}
	public double[][] getMatrix(){return this.matrix;}
	private boolean loadOrientationMatrix(String inputFile)
	{
		BufferedReader reader = null;
		String line;
		String delims = " ";
		String[] tokens = null;

		int quality=0;
		//DirectionVector vector = new DirectionVector(0.0f, 0.0f);

		try
		{
				
			reader = new BufferedReader(new FileReader(inputFile));
			
			//skip first three lines  
			reader.readLine();
			reader.readLine();
			reader.readLine();
			
			line = reader.readLine();
		//	System.out.println("line1:" + line);
			while (line != null){
				if (line.startsWith("Number of indexed reflections")){
					tokens = line.split(delims);
					break;
				}else{
					line = reader.readLine();
				}
			}
			
			if (tokens != null) {quality = Integer.parseInt(tokens[4]);}
			//System.out.println("inputFile:" + inputFile + "  " + quality[0]);
			//skip more  lines until reaching the line for the matrix   
			for (int i = 0; i < quality + 7; i++)
			{
				line = reader.readLine();
			 
			}
	 
			   
			// load the matrix
			for (int i = 0; i < 3; i++)
			{
				line = reader.readLine();
				tokens = line.split(delims);
				
				ArrayList<String> list = new ArrayList<String>(Arrays.asList(tokens));
				list.removeAll(Arrays.asList(""));
				tokens = list.toArray(tokens);

				this.matrix[i][0] = Double.parseDouble(tokens[0]);//Float.parseFloat(tokens[0]);
				this.matrix[i][1] = Double.parseDouble(tokens[1]);//Float.parseFloat(tokens[1]);
				this.matrix[i][2] = Double.parseDouble(tokens[2]);//Float.parseFloat(tokens[2]);
			} 
	
			// close the file
			reader.close();
			
			return true;
		}
		
		catch(Exception e)
		{
			System.out.println(inputFile);
			e.printStackTrace(); 
			return false;
		
		} finally {
		    // Close resource.
		   //
		    try { 
		    	//System.out.println("close it");
		    	if (reader != null){ 
		    		reader.close();
		    	}

		    } catch (IOException e) {
		    	e.printStackTrace();
		    	return false;
		    }
		}
	}
	 

}
