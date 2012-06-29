package skew.packages.misorientation.datasource.calculation.misorientation;

/**
 * OrientationMatrix defines the structure for storing the orientation matrix for one scan point in an area scan. 
 * These matrixes are loaded from index files. It accepts the index file format for both XMAS and FOXMAS
 * @author Jinhui Qin, 2011
 *
 */

public class OrientationMatrix
{

	public float		inverse[][], direct[][];
	public int			index;

	public OrientationMatrix()
	{
		index = -1;
		this.inverse = new float[3][3];
		this.direct = new float[3][3];
	}


	public void printInverseMatrix()
	{
		System.out.println("index: " + index + " [[" + 
				inverse[0][0] +","+ inverse[0][1] +","+ inverse[0][2] + "],[" +
				inverse[1][0] +","+ inverse[1][1] +","+ inverse[1][2] + "],[" + 
				inverse[2][0] +","+ inverse[2][1] +","+ inverse[2][2] + "]]");
	}

	public int getIndex()
	{
		return this.index;
	}

	public boolean matrixOK()
	{
		return index != -1;
	}

}
