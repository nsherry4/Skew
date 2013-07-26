package skew.packages.misorientation.datasource.calculation.misorientation;

/**
 * This package provides the routines for calculating mis-orientation angles for each scan point in an area scan.
 * The mis-angle for each scan point is set as the average of all mis-angles to its 8  neighbors if the angle value  
 * is less than 5 degree; it also recorded the angles to the east and to the south neighbors as the reference 
 * for drawing grain boundaries, e.g. if any of these two angles is large than 5 degree, draw a boundary line at the
 * east/south side of this scan point.     
 * @author Jinhui Qin, 2011
 *
 */


import java.io.File;
import java.util.List;

import commonenvironment.IOOperations;
import fava.functionable.FList;
import fava.signatures.FnEach;
import plural.executor.ExecutorSet;
import plural.executor.eachindex.EachIndexExecutor;
import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import plural.executor.map.MapExecutor;
import scitypes.Coord;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.core.model.impl.SkewDataset;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;
import skew.models.OrientationMatrix.IOrientationMatrix;
import skew.packages.misorientation.datasource.MisorientationDataSource;
import skew.packages.misorientation.datasource.calculation.magnitude.Magnitude;
import skew.packages.misorientation.datasource.calculation.magnitude.OrientationMap;

public class Calculation
{

	private static CubicSymOP	symmetryOperators	= new CubicSymOP();


	
	public static ExecutorSet<ISkewDataset> calculate(final List<String> filenames, final MisorientationDataSource ds, final Coord<Integer> mapSize)
	{

		FList<MisAnglePoint> values = new FList<MisAnglePoint>(mapSize.x * mapSize.y);
		for (int i = 0; i < mapSize.x * mapSize.y; i++)
		{
			values.add(ds.createPoint(i, i % mapSize.x, i / mapSize.x));
			
		}
		
		String datasetName = IOOperations.getCommonFileName(filenames);
		final String name = new File(datasetName).getName();
		final String path = new File(filenames.get(0)).getParent();
		
		final MisAngleGrid<MisAnglePoint> anglelist = new MisAngleGrid<MisAnglePoint>(mapSize.x, mapSize.y, values);


		// executors		
		//final MapExecutor<String, String> loadFilesExec = EBSD.loadMatrixListEBSD(matrixlist.values, filenames.get(0));
		//final MapExecutor<String, String> loadFilesExec = S_IND.loadMatrixList(matrixlist.values, S_IND.startNum, filenames);
		
		final MapExecutor<String, String> loadFilesExec = ds.loadPoints(anglelist, filenames);
		
		
		final EachIndexExecutor calculateExec = calculateAngleList(anglelist, mapSize.x, mapSize.y);
		final EachIndexExecutor calcGrainExec = calculateGrainMagnitude(anglelist);

		
		
		
		// define how the executors will operate
		ExecutorSet<ISkewDataset> execset = new ExecutorSet<ISkewDataset>("Opening Data Set") {

			@Override
			protected ISkewDataset execute()
			{

				//load files from disk
				loadFilesExec.executeBlocking();

				//calculate local misorientation
				calculateExec.executeBlocking();

				//calculate which grain each pixel belongs to
				anglelist.calculateGrains();

				//create grain objects for all grain labels
				Magnitude.setupGrains(anglelist);

				//calculate the misorientation magnitude of each grain
				calcGrainExec.setWorkUnits(anglelist.grains.size());
				calcGrainExec.executeBlocking();

				OrientationMap.calculateOrientation(anglelist);
								
				return new SkewDataset(name, path, anglelist, ds);


			}
		};

		execset.addExecutor(loadFilesExec);
		execset.addExecutor(calculateExec);
		execset.addExecutor(calcGrainExec);

		
		
		return execset;

	}


	public static EachIndexExecutor calculateAngleList(final MisAngleGrid<? extends MisAnglePoint> anglelist,
			final int width, final int height)
	{

		FnEach<Integer> eachIndex = new FnEach<Integer>() {

			@Override
			public void f(Integer index)
			{
				setMisAnglePoints(index, anglelist, width, height);
			}
		};

		EachIndexExecutor exec = new PluralEachIndexExecutor(width * height, eachIndex);
		exec.setName("Local Misorientation");
		return exec;

	}

	public static EachIndexExecutor calculateGrainMagnitude(final MisAngleGrid<? extends MisAnglePoint> data)
	{
		FnEach<Integer> eachIndex = new FnEach<Integer>() {

			@Override
			public void f(Integer index)
			{
				Magnitude.calcMagnitude(data, data.grains.get(index));
			}
		};

		EachIndexExecutor exec = new PluralEachIndexExecutor(0, eachIndex);
		exec.setName("Grain Misorientation");
		return exec;
	}

	private static void setMisAnglePoints(int i, MisAngleGrid<? extends MisAnglePoint> values, int width, int height)
	{
		if (!values.get(i).orientation.matrixOK()) return;

		int n, w, e, s, nw, sw, se, ne, row, col, points = 0;
		double angle_total = 0., angle;

		points = 0;
		angle_total = 0.;
		// deside center point and its 8 eight neighbours' indeces


		// row=anglelist.get(i).row;
		// col=anglelist.get(i).col;
		row = i / values.getWidth();
		col = i - row * values.getWidth();


		n = (row - 1) * width + col;

		s = (row + 1) * width + col;
		w = i - 1;
		e = i + 1;
		nw = n - 1;
		ne = n + 1;
		sw = s - 1;
		se = s + 1;

		if (row == 0)
		{
			n = -1;
			nw = -1;
			ne = -1;
		}
		if (row == height - 1)
		{
			s = -1;
			sw = -1;
			se = -1;
		}
		if (col == 0)
		{
			w = -1;
			sw = -1;
			nw = -1;
		}
		if (col == width - 1)
		{
			e = -1;
			ne = -1;
			se = -1;
		}

		MisAnglePoint point = values.get(i);
		point.orientation = values.get(i).orientation;

		if (n >= 0)
		{ // has north neighbor
			angle = 0.;

			if (values.get(n).orientation.matrixOK())
			{

				angle = calculateAngle(values.get(i).orientation, values.get(n).orientation);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
				point.north = angle;

			}
		}
		if (s >= 0)
		{ // has south neighbor
			angle = 0.;
			if (values.get(s).orientation.matrixOK())
			{


				angle = calculateAngle(values.get(i).orientation, values.get(s).orientation);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
				point.south = angle;

			}
		}
		if (w >= 0)
		{ // has west neighbor
			angle = 0.;
			if (values.get(w).orientation.matrixOK())
			{


				angle = calculateAngle(values.get(i).orientation, values.get(w).orientation);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
				point.west = angle;
			}
		}
		if (e >= 0)
		{ // has east neighbor
			angle = 0.;
			if (values.get(e).orientation.matrixOK())
			{
				// angle =
				// calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(e));


				angle = calculateAngle(values.get(i).orientation, values.get(e).orientation);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
				point.east = angle;

			}
		}
		if (nw >= 0)
		{ // has north-west neighbor
			angle = 0.;
			if (values.get(nw).orientation.matrixOK())
			{
				angle = calculateAngle(values.get(i).orientation, values.get(nw).orientation);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
			}
		}
		if (ne >= 0)
		{// has north-east neighbor
			angle = 0.;
			if (values.get(ne).orientation.matrixOK())
			{
				angle = calculateAngle(values.get(i).orientation, values.get(ne).orientation);
				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
			}
		}
		if (sw >= 0)
		{// has south-west neighbor
			angle = 0.;
			if (values.get(sw).orientation.matrixOK())
			{
				angle = calculateAngle(values.get(i).orientation, values.get(sw).orientation);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
			}
		}
		if (se >= 0)
		{// has south-east neighbor
			angle = 0.;
			if (values.get(se).orientation.matrixOK())
			{
				angle = calculateAngle(values.get(i).orientation, values.get(se).orientation);

				if (angle < 5)
				{
					angle_total += angle;
					points++;
				}
			}
		}

		if (points != 0)
		{
			values.get(i).average = angle_total / points;
		}
		else
		{
			values.get(i).average = 0.0;
		}
		// printf("%10d %10d %15.8f %15.8f %15.8f \n",row,
		// col,pointList[i].average,pointList[i].east,pointList[i].south);


	}

	public static double calculateAngle(IOrientationMatrix gA, IOrientationMatrix gB)
	{

		float[][] delta_g, mis;

		double minAngle = 400., temp;

		// gA = mA.getMatrix();
		delta_g = new float[3][3];
		mis = new float[3][3];

		prodmat(gB.getInverse(), gA.getDirect(), delta_g);

		minAngle = -1;
		for (int i = 0; i < symmetryOperators.getNumOP(); i++)
		{
			prodmatDiag(delta_g, symmetryOperators.getOP(i), mis);

			temp = (mis[0][0] + mis[1][1] + mis[2][2] - 1.0) * 0.5;

			// do this backwards to save the arccos and division calls
			if (temp > minAngle) minAngle = temp;
			// angle = Math.acos(temp)/Math.PI*180.;
			// if (angle < minAngle) minAngle = angle;
		}
		if (minAngle > 1.0) minAngle = 1.0;
		if (minAngle < -1.0) minAngle = -1.0;
		return Math.acos(minAngle) / Math.PI * 180.;
	}

	public static void printMat(double[][] op)
	{
		System.out.println(op[0][0] + "," + op[0][1] + "," + op[0][2] + "," + op[1][0] + "," + op[1][1] + ","
				+ op[1][2] + "," + op[2][0] + "," + op[2][1] + "," + op[2][2]);
	}



	public static void prodmat(float[][] a, float[][] b, float[][] ab)
	{

		int i, j, k;
		int n = 3;

		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				ab[i][j] = 0.f;


		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				for (k = 0; k < n; k++)
					ab[i][j] += a[i][k] * b[k][j];
	}
	
	public static void prodmat(double[][] a, double[][] b, double[][] ab)
	{

		int i, j, k;
		int n = 3;

		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				ab[i][j] = 0.;


		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				for (k = 0; k < n; k++)
					ab[i][j] += a[i][k] * b[k][j];
	}
	
	
	// calculate only the diagonal where x=y, since that is what misorientation uses
	public static void prodmatDiag(float[][] a, float[][] b, float[][] ab)
	{

		int i, k;
		int n = 3;

		ab[0][0] = 0;
		ab[1][1] = 0;
		ab[2][2] = 0;

		for (i = 0; i < n; i++)
		{
			for (k = 0; k < n; k++)
			{
				ab[i][i] += a[i][k] * b[k][i];
			}
		}
	}
	
	
	// calculate only the diagonal where x=y, since that is what misorientation uses
	public static void prodmatDiag(double[][] a, double[][] b, double[][] ab)
	{

		int i, k;
		int n = 3;

		ab[0][0] = 0;
		ab[1][1] = 0;
		ab[2][2] = 0;

		for (i = 0; i < n; i++)
		{
			for (k = 0; k < n; k++)
			{
				ab[i][i] += a[i][k] * b[k][i];
			}
		}
	}

	public static boolean invert3(double[][] mat, double[][] imat)
	{

		int i, j;

		double det;
		boolean flag;

		flag = true;




		imat[0][0] = mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1];

		imat[0][1] = -mat[0][1] * mat[2][2] + mat[0][2] * mat[2][1];

		imat[0][2] = mat[0][1] * mat[1][2] - mat[0][2] * mat[1][1];

		imat[1][0] = -mat[1][0] * mat[2][2] + mat[2][0] * mat[1][2];

		imat[1][1] = mat[0][0] * mat[2][2] - mat[2][0] * mat[0][2];

		imat[1][2] = -mat[0][0] * mat[1][2] + mat[0][2] * mat[1][0];

		imat[2][0] = mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0];

		imat[2][1] = -mat[0][0] * mat[2][1] + mat[0][1] * mat[2][0];

		imat[2][2] = mat[0][0] * mat[1][1] - mat[1][0] * mat[0][1];

		det = mat[0][0] * mat[1][2] * mat[2][1] + mat[0][1] * mat[1][0] * mat[2][2];

		det = det + mat[0][2] * mat[1][1] * mat[2][0] - mat[0][2] * mat[1][0] * mat[2][1];

		det = det - mat[0][1] * mat[1][2] * mat[2][0] - mat[0][0] * mat[1][1] * mat[2][2];

		det = (-1.) * det;

		if (det != 0)
		{
			for (i = 0; i < 3; i++)
				for (j = 0; j < 3; j++)
					imat[i][j] = imat[i][j] / det;
		}

		else flag = false;


		return flag;

	}

	

	public static boolean invert3(float[][] mat, float[][] imat)
	{

		int i, j;

		float det;
		boolean flag;

		flag = true;




		imat[0][0] = mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1];

		imat[0][1] = -mat[0][1] * mat[2][2] + mat[0][2] * mat[2][1];

		imat[0][2] = mat[0][1] * mat[1][2] - mat[0][2] * mat[1][1];

		imat[1][0] = -mat[1][0] * mat[2][2] + mat[2][0] * mat[1][2];

		imat[1][1] = mat[0][0] * mat[2][2] - mat[2][0] * mat[0][2];

		imat[1][2] = -mat[0][0] * mat[1][2] + mat[0][2] * mat[1][0];

		imat[2][0] = mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0];

		imat[2][1] = -mat[0][0] * mat[2][1] + mat[0][1] * mat[2][0];

		imat[2][2] = mat[0][0] * mat[1][1] - mat[1][0] * mat[0][1];

		det = mat[0][0] * mat[1][2] * mat[2][1] + mat[0][1] * mat[1][0] * mat[2][2];

		det = det + mat[0][2] * mat[1][1] * mat[2][0] - mat[0][2] * mat[1][0] * mat[2][1];

		det = det - mat[0][1] * mat[1][2] * mat[2][0] - mat[0][0] * mat[1][1] * mat[2][2];

		det = (-1.f) * det;

		if (det != 0)
		{
			for (i = 0; i < 3; i++)
				for (j = 0; j < 3; j++)
					imat[i][j] = imat[i][j] / det;
		}

		else flag = false;


		return flag;

	}

}
