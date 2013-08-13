package skew.models.OrientationMatrix;

public interface IOrientationMatrix 
{

	public abstract void printInverseMatrix();

	public abstract boolean matrixOK();

	public abstract float[][] getInverse();
	public abstract void setInverse(float[][] inverse);

	public abstract float[][] getDirect();
	public abstract void setDirect(float[][] direct);

	public abstract int getMatrixIndex();
	public abstract void setMatrixIndex(int index);
	
	public abstract boolean getHasOMData();
	public abstract void setHasOMData(boolean b);

}