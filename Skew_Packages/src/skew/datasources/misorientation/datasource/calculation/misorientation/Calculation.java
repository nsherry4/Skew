package skew.datasources.misorientation.datasource.calculation.misorientation;

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

import plural.executor.ExecutorSet;
import plural.executor.eachindex.EachIndexExecutor;
import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import plural.executor.map.MapExecutor;
import scitypes.Coord;
import skew.core.datasource.DataSource;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewDataset;
import skew.core.model.SkewGrid;
import skew.datasources.misorientation.datasource.MisorientationDataSource;
import skew.datasources.misorientation.datasource.calculation.magnitude.GrainIdentify;
import skew.datasources.misorientation.datasource.calculation.magnitude.Magnitude;
import skew.datasources.misorientation.datasource.calculation.magnitude.OrientationMap;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;
import skew.models.orientation.OrientationMatrix;

import commonenvironment.IOOperations;

import fava.functionable.FList;
import fava.signatures.FnEach;
import fava.signatures.FnGet;

public class Calculation
{

	private static CubicSymOP	symmetryOperators	= new CubicSymOP();


	
	public static ExecutorSet<ISkewDataset> calculate(final List<String> filenames, final MisorientationDataSource ds, final Coord<Integer> mapSize)
	{

		//Create MisAngleGrid
		List<ISkewPoint<MisAngle>> misList = DataSource.getEmptyPoints(mapSize, new FnGet<MisAngle>(){
			@Override public MisAngle f() { return new MisAngle(); }});

		final ISkewGrid<MisAngle> misModel = new SkewGrid<>(mapSize.x, mapSize.y, misList);
		final GrainModel grainModel = new GrainModel();
		
		//Create OrientationMatrix Grid
		final List<ISkewPoint<IOrientationMatrix>> omList = DataSource.getEmptyPoints(mapSize, new FnGet<IOrientationMatrix>() {
			@Override public IOrientationMatrix f() { return new OrientationMatrix(); }});
		
		final ISkewGrid<IOrientationMatrix> omModel = new SkewGrid<>(mapSize.x, mapSize.y, omList);

				
		//give the datasource the models
		ds.setModels(grainModel, misModel, omModel);

		
		//Load data from files
		final MapExecutor<String, String> loadFilesExec = ds.loadPoints(filenames);
		
		//Perform various calculations on loaded data
		final EachIndexExecutor calculateExec = calcLocalMisorientation(misModel, omModel, mapSize.x, mapSize.y);
		final EachIndexExecutor calcGrainExec = calculateGrainMagnitude(grainModel, misModel, omModel);

		
		
		String datasetName = IOOperations.getCommonFileName(filenames);
		final String name = new File(datasetName).getName();
		final String path = new File(filenames.get(0)).getParent();
		
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
				GrainIdentify.calculate(misModel, grainModel);

				//create grain objects for all grain labels
				Magnitude.setupGrains(grainModel, misModel);

				//calculate the misorientation magnitude of each grain
				calcGrainExec.setWorkUnits(grainModel.grains.size());
				calcGrainExec.executeBlocking();

				OrientationMap.calculateOrientation(misModel, omModel);
								
				return new SkewDataset(name, path, new FList<ISkewGrid<?>>(misModel, omModel), ds);


			}
		};

		execset.addExecutor(loadFilesExec);
		execset.addExecutor(calculateExec);
		execset.addExecutor(calcGrainExec);

		
		
		return execset;

	}

	
	
	/**
	 * Calculates the local misorientation from the Orientation Matrix information
	 */
	public static EachIndexExecutor calcLocalMisorientation(final ISkewGrid<MisAngle> misModel, final ISkewGrid<IOrientationMatrix> omModel, final int width, final int height)
	{

		FnEach<Integer> eachIndex = new FnEach<Integer>() {

			@Override
			public void f(Integer index)
			{
				setMisAnglePoints(index, misModel, omModel, width, height);
			}
		};

		EachIndexExecutor exec = new PluralEachIndexExecutor(width * height, eachIndex);
		exec.setName("Local Misorientation");
		return exec;

	}

	public static EachIndexExecutor calculateGrainMagnitude(final GrainModel grainModel, final ISkewGrid<MisAngle> misModel, final ISkewGrid<IOrientationMatrix> omModel)
	{
		FnEach<Integer> eachIndex = new FnEach<Integer>() {

			@Override
			public void f(Integer index)
			{
				Magnitude.calcMagnitude(misModel, omModel, grainModel.grains.get(index));
			}
		};

		EachIndexExecutor exec = new PluralEachIndexExecutor(0, eachIndex);
		exec.setName("Grain Misorientation");
		return exec;
	}

	private static void setMisAnglePoints(int i, ISkewGrid<MisAngle> misGrid, ISkewGrid<IOrientationMatrix> omGrid, int width, int height)
	{
		ISkewPoint<MisAngle> misPoint = misGrid.getPoint(i);
		ISkewPoint<IOrientationMatrix> omPoint = omGrid.getPoint(i);
		
		MisAngle misData = misGrid.getData(i);
		IOrientationMatrix omData = omPoint.getData();
		
		if (!omPoint.isValid()) return;
	
		int n, w, e, s, nw, sw, se, ne, row, col, points = 0;
		double angle_total = 0., angle;

		points = 0;
		angle_total = 0.;
		// deside center point and its 8 eight neighbours' indices


		// row=anglelist.get(i).row;
		// col=anglelist.get(i).col;
		row = i / omGrid.getWidth();
		col = i - row * omGrid.getWidth();


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

		
		//point.orientation = values.get(i).orientation;
		
		ISkewPoint<IOrientationMatrix> otherOMPoint;
		IOrientationMatrix otherOMData;
		if (n >= 0)
		{ // has north neighbor
			otherOMPoint = omGrid.getPoint(n);
			otherOMData = otherOMPoint.getData();
			angle = 0.;

			if (otherOMPoint.isValid())
			{

				angle = calculateAngle(omData, otherOMData);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
				misData.north.set(angle);

			}
			else { misData.north.set(); }
		}
		if (s >= 0)
		{ // has south neighbor
			otherOMPoint = omGrid.getPoint(s);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{


				angle = calculateAngle(omData, otherOMData);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
				misData.south.set(angle);
				

			}
			else { misData.south.set(); }
		}
		if (w >= 0)
		{ // has west neighbor
			otherOMPoint = omGrid.getPoint(w);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{


				angle = calculateAngle(omData, otherOMData);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
				misData.west.set(angle);
			}
			else { misData.west.set(); }
		}
		if (e >= 0)
		{ // has east neighbor
			otherOMPoint = omGrid.getPoint(e);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{
				// angle =
				// calculatAngle(matrixlist.getMatrix(i),matrixlist.getMatrix(e));


				angle = calculateAngle(omData, otherOMData);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
				misData.east.set(angle);

			}
			else { misData.east.set(); }
		}
		if (nw >= 0)
		{ // has north-west neighbor
			otherOMPoint = omGrid.getPoint(nw);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{
				angle = calculateAngle(omData, otherOMData);

				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
			}
		}
		if (ne >= 0)
		{// has north-east neighbor
			otherOMPoint = omGrid.getPoint(ne);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{
				angle = calculateAngle(omData, otherOMData);
				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
			}
		}
		if (sw >= 0)
		{// has south-west neighbor
			otherOMPoint = omGrid.getPoint(sw);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{
				angle = calculateAngle(omData, otherOMData);
				if (angle < 5.)
				{
					angle_total += angle;
					points++;
				}
			}
		}
		if (se >= 0)
		{// has south-east neighbor
			otherOMPoint = omGrid.getPoint(se);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{
				angle = calculateAngle(omData, otherOMData);
				if (angle < 5)
				{
					angle_total += angle;
					points++;
				}
			}
		}

		if (points != 0)
		{
			misData.average.set(angle_total / points);
			misPoint.setValid(true);
		}
		else
		{
			misData.average.set();
			misPoint.setValid(false);
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
