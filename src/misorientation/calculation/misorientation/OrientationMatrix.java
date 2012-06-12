package misorientation.calculation.misorientation;

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

public class OrientationMatrix
{

	private double		inverse[][], direct[][];
	private int		fileNumber;

	public OrientationMatrix()
	{
		fileNumber = -1;
		this.inverse = new double[3][3];
		this.direct = new double[3][3];
	}



	public void readOrientationMatrix(String fileName)
	{
		if (loadOrientationMatrix(fileName))
		{
			this.fileNumber = IndexFileName.getFileNumber(fileName);
			Calculation.invert3(inverse, direct);
		}
	}

	public void printInverseMatrix()
	{
		System.out.println("filenumber=" + fileNumber + ":" + inverse[0][0] + inverse[0][1] + inverse[0][2] + ","
				+ inverse[1][0] + inverse[1][1] + inverse[1][2] + "'" + inverse[2][0] + inverse[2][1] + inverse[2][2]);
	}

	public int getFileNumber()
	{
		return this.fileNumber;
	}

	public boolean matrixOK()
	{
		return fileNumber != -1;
	}

	public double[][] getInverse()
	{
		return this.inverse;
	}

	public double[][] getDirect()
	{
		return this.direct;
	}

	private boolean loadOrientationMatrix(String inputFile)
	{
		BufferedReader reader = null;
		String line;
		String delims = " ";
		String[] tokens = null;

		int quality = 0;

		try
		{

			reader = new BufferedReader(new FileReader(inputFile));

			// skip first three lines
			reader.readLine();
			reader.readLine();
			reader.readLine();

			line = reader.readLine();
			while (line != null)
			{
				if (line.startsWith("Number of indexed reflections"))
				{
					tokens = line.split(delims);
					break;
				}
				else
				{
					line = reader.readLine();
				}
			}

			if (tokens != null)
			{
				quality = Integer.parseInt(tokens[4]);
			}
			// skip more lines until reaching the line for the matrix
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

				this.inverse[i][0] = Double.parseDouble(tokens[0]);// Float.parseFloat(tokens[0]);
				this.inverse[i][1] = Double.parseDouble(tokens[1]);// Float.parseFloat(tokens[1]);
				this.inverse[i][2] = Double.parseDouble(tokens[2]);// Float.parseFloat(tokens[2]);
			}

			// close the file
			reader.close();

			return true;
		}
		catch (Exception e)
		{
			System.out.println("Error reading " + inputFile);
			return false;
		}
		finally
		{
			// Close resource.
			//
			try
			{
				if (reader != null) { reader.close(); }
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		}
	}


}
