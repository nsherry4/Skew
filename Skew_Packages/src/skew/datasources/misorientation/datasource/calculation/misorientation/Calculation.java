package skew.datasources.misorientation.datasource.calculation.misorientation;

/**
 * This package provides the routines for calculating mis-orientation angles for each scan point in an area scan.
 * The mis-angle for each scan point is set as the average of all mis-angles to its 8  neighbors if the angle value  
 * is less than 'boundary' degrees; it also recorded the angles to the east and to the south neighbors as the reference 
 * for drawing grain boundaries, e.g. if any of these two angles is large than 'boundary' degrees, draw a boundary line at the
 * east/south side of this scan point.     
 * @author Jinhui Qin, 2011
 *
 */


import java.io.File;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;

import plural.executor.ExecutorSet;
import plural.executor.RunnableExecutorSet;
import plural.executor.eachindex.EachIndexExecutor;
import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import plural.executor.map.MapExecutor;
import scitypes.Coord;
import skew.core.datasource.DataSource;
import skew.core.model.IModel;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewDataset;
import skew.datasources.misorientation.datasource.MisorientationDataSource;
import skew.datasources.misorientation.datasource.MisorientationProvider;
import skew.datasources.misorientation.datasource.calculation.magnitude.GrainIdentify;
import skew.datasources.misorientation.datasource.calculation.magnitude.Magnitude;
import skew.datasources.misorientation.datasource.calculation.magnitude.OrientationMap;
import skew.models.grain.Grain;
import skew.models.grain.GrainUtil;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;
import commonenvironment.IOOperations;
import fava.functionable.FList;
import fava.signatures.FnEach;

public class Calculation
{

	private static CubicSymOP	symmetryOperators	= new CubicSymOP();

	
	public static RunnableExecutorSet<ISkewDataset> calculate(List<String> filenames, MapExecutor<String, String> loadFilesExec, DataSource datasource, MisorientationProvider misdata, Coord<Integer> mapSize, double boundary)
	{
		
		//Perform various calculations on loaded data
		final EachIndexExecutor calculateExec = calcLocalMisorientation(misdata.misModel, misdata.omModel, mapSize.x, mapSize.y, boundary);
		final EachIndexExecutor calcGrainExec = calculateGrainMagnitude(misdata.grainModel, misdata.misModel, misdata.omModel);

		
		
		String datasetName = IOOperations.getCommonFileName(filenames);
		final String name = new File(datasetName).getName();
		final String path = new File(filenames.get(0)).getParent();
		
		/*
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
				GrainIdentify.calculate(misdata.misModel, misdata.grainModel, boundary);

				//create grain objects for all grain labels
				Magnitude.setupGrains(misdata.grainModel);

				//calculate the misorientation magnitude of each grain
				calcGrainExec.setWorkUnits(GrainUtil.grainCount(misdata.grainModel));
				calcGrainExec.executeBlocking();

				OrientationMap.calculateOrientation(misdata.misModel, misdata.omModel);
				
				return new SkewDataset(name, path, new FList<IModel>(misdata.misModel, misdata.omModel, misdata.grainModel), datasource);


			}
		};

		execset.addExecutor(loadFilesExec);
		execset.addExecutor(calculateExec);
		execset.addExecutor(calcGrainExec);
		 */
		
		
		RunnableExecutorSet<ISkewDataset> execset = new RunnableExecutorSet<ISkewDataset>("Opening Data Set", () -> 
			new SkewDataset(name, path, new FList<IModel>(misdata.misModel, misdata.omModel, misdata.grainModel), datasource)
		);

		execset.addExecutor(loadFilesExec, () -> loadFilesExec.executeBlocking());
		execset.addExecutor(calculateExec, () -> calculateExec.executeBlocking());
		execset.addExecutor(calcGrainExec, () -> {
			//calculate which grain each pixel belongs to
			GrainIdentify.calculate(misdata.misModel, misdata.grainModel, boundary);
	
			//create grain objects for all grain labels
			Magnitude.setupGrains(misdata.grainModel);
	
			//calculate the misorientation magnitude of each grain
			calcGrainExec.setWorkUnits(GrainUtil.grainCount(misdata.grainModel));
			calcGrainExec.executeBlocking();
	
			OrientationMap.calculateOrientation(misdata.misModel, misdata.omModel);
		});
		
		
		return execset;

	}

	
	
	/**
	 * Calculates the local misorientation from the Orientation Matrix information
	 */
	public static EachIndexExecutor calcLocalMisorientation(final ISkewGrid<MisAngle> misModel, final ISkewGrid<IOrientationMatrix> omModel, final int width, final int height, final double boundary)
	{

		FnEach<Integer> eachIndex = index -> setMisAnglePoints(index, misModel, omModel, width, height, boundary);

		EachIndexExecutor exec = new PluralEachIndexExecutor(width * height, eachIndex);
		exec.setName("Local Misorientation");
		return exec;

	}

	public static EachIndexExecutor calculateGrainMagnitude(final ISkewGrid<GrainPixel> grainModel, final ISkewGrid<MisAngle> misModel, final ISkewGrid<IOrientationMatrix> omModel)
	{
		FnEach<Integer> eachIndex = new FnEach<Integer>() {

			Multimap<Grain, ISkewPoint<GrainPixel>> grainPoints;
			
			@Override
			public void f(Integer index)
			{
				if (grainPoints == null) grainPoints = GrainUtil.getGrainPointMap(grainModel);
				Grain grain = GrainUtil.getGrains(grainModel).get(index);
				if (grain == null) return;
				Magnitude.calcMagnitude(omModel, grainPoints.get(grain), grain);
			}
		};

		EachIndexExecutor exec = new PluralEachIndexExecutor(0, eachIndex);
		exec.setName("Grain Misorientation");
		return exec;
	}

	private static void setMisAnglePoints(int i, ISkewGrid<MisAngle> misGrid, ISkewGrid<IOrientationMatrix> omGrid, int width, int height, double boundary)
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
		// decide center point and its 8 eight neighbours' indices


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

				if (angle < boundary)
				{
					angle_total += angle;
					points++;
				}
				misData.north = Optional.of(angle);

			}
			else { misData.north = Optional.absent(); }
		}
		if (s >= 0)
		{ // has south neighbor
			otherOMPoint = omGrid.getPoint(s);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{


				angle = calculateAngle(omData, otherOMData);

				if (angle < boundary)
				{
					angle_total += angle;
					points++;
				}
				misData.south = Optional.of(angle);
				

			}
			else { misData.south = Optional.absent(); }
		}
		if (w >= 0)
		{ // has west neighbor
			otherOMPoint = omGrid.getPoint(w);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{


				angle = calculateAngle(omData, otherOMData);

				if (angle < boundary)
				{
					angle_total += angle;
					points++;
				}
				misData.west = Optional.of(angle);
			}
			else { misData.west = Optional.absent(); }
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

				if (angle < boundary)
				{
					angle_total += angle;
					points++;
				}
				misData.east = Optional.of(angle);

			}
			else { misData.east = Optional.absent(); }
		}
		if (nw >= 0)
		{ // has north-west neighbor
			otherOMPoint = omGrid.getPoint(nw);
			otherOMData = otherOMPoint.getData();
			angle = 0.;
			if (otherOMPoint.isValid())
			{
				angle = calculateAngle(omData, otherOMData);

				if (angle < boundary)
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
				if (angle < boundary)
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
				if (angle < boundary)
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
				if (angle < boundary)
				{
					angle_total += angle;
					points++;
				}
			}
		}

		if (points != 0)
		{
			misData.average = Optional.of(angle_total / points);
			misPoint.setValid(true);
		}
		else
		{
			misData.average = Optional.absent();
			misPoint.setValid(false);
		}
		// printf("%10d %10d %15.8f %15.8f %15.8f \n",row,
		// col,pointList[i].average,pointList[i].east,pointList[i].south);


	}

	public static double calculateAngle(IOrientationMatrix gA, IOrientationMatrix gB)
	{

		float[][] delta_g, mis;
		

		float minAngle = 400f, temp;

		delta_g = new float[3][3];
		mis = new float[3][3];

		
		prodmat(gB.getInverse(), gA.getDirect(), delta_g);

		minAngle = -1;
		for (int i = 0; i < symmetryOperators.getNumOP(); i++)
		{
			prodmatDiag(delta_g, symmetryOperators.getOP(i), mis);

			temp = (mis[0][0] + mis[1][1] + mis[2][2] - 1f) * 0.5f;

			// do this backwards to save the arccos and division calls
			if (temp > minAngle) minAngle = temp;
		}
		if (minAngle > 1f) minAngle = 1f;
		if (minAngle < -1f) minAngle = -1f;
		return Math.toDegrees(Math.acos(minAngle));

	}

	public static void printMat(double[][] op)
	{
		System.out.println(op[0][0] + "," + op[0][1] + "," + op[0][2] + "," + op[1][0] + "," + op[1][1] + ","
				+ op[1][2] + "," + op[2][0] + "," + op[2][1] + "," + op[2][2]);
	}



	public static void prodmat(float[][] a, float[][] b, float[][] ab)
	{

		int i, j, k;

		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				ab[i][j] = 0.f;


		for (i = 0; i < 3; i++)
			for (j = 0; j < 3; j++)
				for (k = 0; k < 3; k++)
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

		ab[0][0] = 0;
		ab[1][1] = 0;
		ab[2][2] = 0;

		for (int i = 0; i < 3; i++)
		{
			for (int k = 0; k < 3; k++)
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
