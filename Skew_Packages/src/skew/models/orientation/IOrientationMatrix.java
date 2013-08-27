package skew.models.orientation;

import java.util.List;

import scitypes.DirectionVector;

public interface IOrientationMatrix 
{

	public abstract void printInverseMatrix();

	public abstract float[][] getInverse();
	public abstract void setInverse(float[][] inverse);

	public abstract float[][] getDirect();
	public abstract void setDirect(float[][] direct);
	
	public void setOrientationVectors(List<DirectionVector> directions);
	public List<DirectionVector> getOrientationVectors();

	
}