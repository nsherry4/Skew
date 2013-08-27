package skew.models.orientation;

import java.util.List;

import scitypes.DirectionVector;

/**
 * OrientationMatrix defines the structure for storing the orientation matrix for one scan point in an area scan. 
 * These matrixes are loaded from index files. It accepts the index file format for both XMAS and FOXMAS
 * @author Jinhui Qin, 2011
 *
 */

public class OrientationMatrix implements IOrientationMatrix
{

	private float					inverse[][], direct[][];
	private List<DirectionVector> 	orientationVectors;

	public OrientationMatrix()
	{
		this.inverse = new float[3][3];
		this.direct = new float[3][3];
	}


	/* (non-Javadoc)
	 * @see skew.packages.xrdindex.model.IOrientationMatrix#printInverseMatrix()
	 */
	@Override
	public void printInverseMatrix()
	{
		System.out.println("[[" + 
				inverse[0][0] +","+ inverse[0][1] +","+ inverse[0][2] + "],[" +
				inverse[1][0] +","+ inverse[1][1] +","+ inverse[1][2] + "],[" + 
				inverse[2][0] +","+ inverse[2][1] +","+ inverse[2][2] + "]]");
	}

	/* (non-Javadoc)
	 * @see skew.packages.xrdindex.model.IOrientationMatrix#getInverse()
	 */
	@Override
	public float[][] getInverse() {
		return inverse;
	}


	/* (non-Javadoc)
	 * @see skew.packages.xrdindex.model.IOrientationMatrix#setInverse(float[][])
	 */
	@Override
	public void setInverse(float[][] inverse) {
		this.inverse = inverse;
	}


	/* (non-Javadoc)
	 * @see skew.packages.xrdindex.model.IOrientationMatrix#getDirect()
	 */
	@Override
	public float[][] getDirect() {
		return direct;
	}


	/* (non-Javadoc)
	 * @see skew.packages.xrdindex.model.IOrientationMatrix#setDirect(float[][])
	 */
	@Override
	public void setDirect(float[][] direct) {
		this.direct = direct;
	}


	@Override
	public void setOrientationVectors(List<DirectionVector> directions) {
		orientationVectors = directions;
	}


	@Override
	public List<DirectionVector> getOrientationVectors() {
		return orientationVectors;
	}
	
	
	

}
