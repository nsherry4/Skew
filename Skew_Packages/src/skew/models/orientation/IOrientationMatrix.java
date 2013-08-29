package skew.models.orientation;

import java.util.List;

import scitypes.DirectionVector;

public interface IOrientationMatrix 
{

	void printInverseMatrix();

	float[][] getInverse();
	void setInverse(float[][] inverse);

	float[][] getDirect();
	void setDirect(float[][] direct);
	
	void setOrientationVectors(List<DirectionVector> directions);
	List<DirectionVector> getOrientationVectors();

	
}