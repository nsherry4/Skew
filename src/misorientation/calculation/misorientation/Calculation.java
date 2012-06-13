package misorientation.calculation.misorientation;

/**
 * This package provides the routines for calculating mis-orientation angles for each scan point in an area scan.
 * The mis-angle for each scan point is set as the average of all mis-angles to its 8  neighbors if the angle value  
 * is less than 5 degree; it also recorded the angles to the east and to the south neighbors as the reference 
 * for drawing grain boundaries, e.g. if any of these two angles is large than 5 degree, draw a boundary line at the
 * east/south side of this scan point.     
 * @author Jinhui Qin, 2011
 *
 */


import java.util.List;

import misorientation.calculation.magnitude.Magnitude;
import misorientation.model.MisAnglePoint;
import misorientation.model.MisAngleGrid;

import fava.signatures.FnEach;

import plural.executor.ExecutorSet;
import plural.executor.eachindex.EachIndexExecutor;
import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import plural.executor.map.MapExecutor;

import scitypes.Coord;

public class Calculation
{

	private static CubicSymOP	symmetryOperators	= new CubicSymOP();


	public static ExecutorSet<MisAngleGrid> calculate(final List<String> filenames, final Coord<Integer> mapSize)
	{

		// create empty data structures
		final MatrixList matrixlist = new MatrixList(mapSize.x, mapSize.y);
		final MisAngleGrid anglelist = new MisAngleGrid(mapSize.x, mapSize.y);


		// executors
		final MapExecutor<String, String> loadFilesExec = matrixlist.loadMatrixList(filenames);
		final EachIndexExecutor calculateExec = calculateAngleList(matrixlist, anglelist, mapSize.x, mapSize.y);
		final EachIndexExecutor calcGrainExec = calculateGrainMagnitude(anglelist);

		// define how the executors will operate
		ExecutorSet<MisAngleGrid> execset = new ExecutorSet<MisAngleGrid>("Opening Data Set") {

			@Override
			protected MisAngleGrid doMaps()
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

				
				return anglelist;

			}
		};

		execset.addExecutor(loadFilesExec);
		execset.addExecutor(calculateExec);
		execset.addExecutor(calcGrainExec);

		return execset;

	}


	private static EachIndexExecutor calculateAngleList(final MatrixList matrixlist, final MisAngleGrid anglelist,
			final int width, final int height)
	{

		FnEach<Integer> eachIndex = new FnEach<Integer>() {

			@Override
			public void f(Integer index)
			{
				setMisAnglePoints(index, matrixlist, anglelist, width, height);
			}
		};

		EachIndexExecutor exec = new PluralEachIndexExecutor(width * height, eachIndex);
		exec.setName("Local Misorientation");
		return exec;

	}

	private static EachIndexExecutor calculateGrainMagnitude(final MisAngleGrid data)
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

	private static void setMisAnglePoints(int i, MatrixList matrixlist, MisAngleGrid anglelist, int width, int height)
	{

		if (!matrixlist.getMatrix(i).matrixOK()) return;


		int n, w, e, s, nw, sw, se, ne, row, col, points = 0;
		double angle_total = 0., angle;

		points = 0;
		angle_total = 0.;
		// deside center point and its 8 eight neighbours' indeces


		// row=anglelist.get(i).row;
		// col=anglelist.get(i).col;
		row = i / anglelist.width;
		col = i - row * anglelist.width;


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

		MisAnglePoint point = anglelist.get(i);
		point.orientation = matrixlist.getMatrix(i);

		if (n >= 0)
		{ // has north neighbor
			angle = 0.;

			if (matrixlist.getMatrix(n).matrixOK())
			{

				angle = calculateAngle(matrixlist.getMatrix(i), matrixlist.getMatrix(n));

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
			if (matrixlist.getMatrix(s).matrixOK())
			{


				angle = calculateAngle(matrixlist.getMatrix(i), matrixlist.getMatrix(s));

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
			if (matrixlist.getMatrix(w).matrixOK())
			{


				angle = calculateAngle(matrixlist.getMatrix(i), matrixlist.getMatrix(w));

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
			if (matrixlist.getMatrix(e).matrixOK())
			{
				// angle =
				// calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(e));


				angle = calculateAngle(matrixlist.getMatrix(i), matrixlist.getMatrix(e));

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
			if (matrixlist.getMatrix(nw).matrixOK())
			{
				angle = calculateAngle(matrixlist.getMatrix(i), matrixlist.getMatrix(nw));

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
			if (matrixlist.getMatrix(ne).matrixOK())
			{
				angle = calculateAngle(matrixlist.getMatrix(i), matrixlist.getMatrix(ne));
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
			if (matrixlist.getMatrix(sw).matrixOK())
			{
				angle = calculateAngle(matrixlist.getMatrix(i), matrixlist.getMatrix(sw));

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
			if (matrixlist.getMatrix(se).matrixOK())
			{
				angle = calculateAngle(matrixlist.getMatrix(i), matrixlist.getMatrix(se));

				if (angle < 5)
				{
					angle_total += angle;
					points++;
				}
			}
		}

		if (points != 0)
		{
			anglelist.get(i).average = angle_total / points;
		}
		else
		{
			anglelist.get(i).average = 0.0;
		}
		// printf("%10d %10d %15.8f %15.8f %15.8f \n",row,
		// col,pointList[i].average,pointList[i].east,pointList[i].south);


	}

	public static double calculateAngle(OrientationMatrix gA, OrientationMatrix gB)
	{

		double[][] delta_g, mis;

		double minAngle = 400., temp;

		// gA = mA.getMatrix();
		delta_g = new double[3][3];
		mis = new double[3][3];

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


}
