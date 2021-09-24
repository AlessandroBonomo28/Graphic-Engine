package mathUtils;

import mathUtils.matrixExceptions.IncompatibleMatrixProduct;
import mathUtils.matrixExceptions.MatrixException;

public class Operations {

	private Operations() {
		
	}
	public static Vector3 extractMajorRowVector(Matrix a) throws MatrixException
	{
		Vector3 v = new Vector3(a.get(0, 0),a.get(0, 1),a.get(0, 2));
		return v;
	}
	public static Vector3 extractMajorColumnVector(Matrix a) throws MatrixException
	{
		Vector3 v = new Vector3(a.get(0, 0),a.get(1, 0),a.get(2, 0));
		return  v;
	}
	
	
	public static Matrix makeMajorColumnVector(Vector3 a)
	{
		double[][] m = new double[4][1];
		m[0][0] = a.x;
		m[1][0] = a.y;
		m[2][0] = a.z;
		m[3][0] = 1;
		return new Matrix(m);
	}
	public static Matrix makeMajorRowVector(Vector3 a)
	{
		double[][] m = new double[1][4];
		m[0][0] = a.x;
		m[0][1] = a.y;
		m[0][2] = a.z;
		m[0][3] = 1;
		return new Matrix(m);
	}
	
	public static Vector3 multiplyMajorRow(Vector3 v,Matrix a) throws MatrixException
	{
		if(a.cols()<3 || a.rows()<3)throw new IncompatibleMatrixProduct();
		if(a.cols()<4 || a.rows()<4)
		{
			a = a.redefine(4, 4);
			a.set(3, 3, 1);
		}
		Matrix b = makeMajorRowVector(v);
		return extractMajorRowVector(Matrix.multiply(b,a));
		
	}
	public static Vector3 multiplyMajorColumn(Matrix a,Vector3 v) throws MatrixException
	{
		if(a.cols()<3 || a.rows()<3)throw new IncompatibleMatrixProduct();
		if(a.cols()<4 || a.rows()<4)
		{
			a = a.redefine(4, 4);
			a.set(3, 3, 1);
		}
		Matrix b = makeMajorColumnVector(v);
		return extractMajorColumnVector(Matrix.multiply(a, b));
	}
	
}
