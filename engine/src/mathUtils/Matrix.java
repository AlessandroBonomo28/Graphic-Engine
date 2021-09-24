package mathUtils;




import mathUtils.matrixExceptions.*;


public class Matrix 
{
	private int x,y;
	private double [][] matrix;
	public Matrix(int rows,int cols) throws MatrixException
	{
		if(positiveRowsAndCols(rows, cols))
		{
			x = cols;
			y = rows;
			matrix = new double[rows][cols];
		}
		else throw new UndefinedMatrix();
	}
	public Matrix(int[][] matrix)
	{
		y = rowsOf(matrix);
		x = colsOf(matrix);
		setMatrix(matrix);
	}
	public Matrix(float[][] matrix)
	{
		y = rowsOf(matrix);
		x = colsOf(matrix);
		setMatrix(matrix);
	}
	public Matrix(double[][] matrix)
	{
		y = rowsOf(matrix);
		x = colsOf(matrix);
		setMatrix(matrix);
	}
	public int cols() {
		return x;
	}
	public int rows() {
		return y;
	}
	// GETTERS restituiscono una copia della matrix
	public double[][] getDoubleMatrix() {
		double[][] res = new double[y][x];
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
			{
				this.matrix[i][j] = matrix[i][j];
			}
		return res;
	}
	public float[][] getFloatMatrix() {
		float[][] res = new float[y][x];
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
			{
				this.matrix[i][j] = matrix[i][j];
			}
		return res;
	}
	public int[][] getIntMatrix() {
		int[][] res = new int[y][x];
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
			{
				this.matrix[i][j] = matrix[i][j];
			}
		return res;
	}
	public double get(int i,int j)throws MatrixException
	{
		if(areIndexesInside(i, j))
			return matrix[i][j];
		else throw new IndexOutOfMatrix();
	}
	public Matrix getRow(int index) throws MatrixException
	{
		if(areIndexesInside(index, 0))
		{
			Matrix res = new Matrix(1,cols());
			for(int i=0;i<cols();i++)
				res.set(0, i, get(index,i));
			return res;
		}
		else throw new IndexOutOfMatrix();
	}
	public Matrix getColumn(int index) throws MatrixException
	{
		if(areIndexesInside(0, index))
		{
			Matrix res = new Matrix(rows(),1);
			for(int i=0;i<rows();i++)
				res.set(i,0 , get(i,index));
			return res;
		}
		else throw new IndexOutOfMatrix();
	}
	// SETTERS
	public void setMatrix(double[][] matrix) {
		this.matrix = new double[y][x];
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
			{
				this.matrix[i][j] = matrix[i][j];
			}
	}
	public void setMatrix(float[][] matrix) {
		this.matrix = new double[y][x];
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
			{
				this.matrix[i][j] = matrix[i][j];
			}
	}
	public void setMatrix(int[][] matrix) {
		this.matrix = new double[y][x];
		for(int i=0;i<y;i++)
			for(int j=0;j<x;j++)
			{
				this.matrix[i][j] = matrix[i][j];
			}
	}
	public void set(int i,int j,double val) throws MatrixException
	{
		if(areIndexesInside(i, j))
			matrix[i][j]=val;
		else throw new IndexOutOfMatrix();
	}
	public Matrix copy()
	{
		return new Matrix(matrix);
	}
	public boolean areIndexesInside(int i,int j)
	{
		if(i <y &&  j<x)return true;
		else return false;
	}
	
	private boolean positiveRowsAndCols(int rows,int cols)
	{
		if(rows <=0 || cols <=0)return false;
		else return true;
	}
	public boolean isProductCompatible(Matrix b)
	{
		return isProductCompatible(this,b);
	}
	public static boolean isProductCompatible(Matrix a,Matrix b)
	{
		if(a.x == b.y)return true;
		else return false;
	}
	public boolean hasSameRowsAndCols(Matrix b)
	{
		return hasSameRowsAndCols(this,b);
	}
	public static boolean hasSameRowsAndCols(Matrix a,Matrix b)
	{
		if(a.x == b.x && a.y == b.y)return true;
		else return false;
	}
	public Matrix sum(Matrix b) throws MatrixException
	{
		return sum(this,b);
	}
	public static Matrix sum(Matrix a,Matrix b,Matrix ...other) throws MatrixException
	{
		Matrix res = a.copy();
		if(hasSameRowsAndCols(a, b))
		{
			for(int i=0;i<a.y;i++)
				for(int j=0;j<a.x;j++)
					res.matrix[i][j]+=b.matrix[i][j];
		}
		else throw new NotSameRowsAndCols();
		// se almeno una matrice è di dimensioni diverse lancia eccezione
		for(Matrix m:other)
			if(!hasSameRowsAndCols(a, m))
				throw new NotSameRowsAndCols();
		// somma le matrici a res
		for(Matrix m:other)
			for(int i=0;i<a.y;i++)
				for(int j=0;j<a.x;j++)
					res.matrix[i][j]+=m.matrix[i][j];
		return res;
	}
	public Matrix multiply(double s)
	{
		Matrix res = copy();
		for(int i=0;i<rows();i++)
			for(int j=0;j<cols();j++)
				res.matrix[i][j]*=s;
		return res;
	}
	public Matrix multiply(Matrix b) throws MatrixException
	{
		return multiply(this,b);
	}
	public static Matrix multiply(Matrix a, Matrix b) throws MatrixException
	{
		if(isProductCompatible(a, b))
		{
			int r = b.rows();
			int c = b.cols();
			Matrix res = new Matrix(r,c);
			for(int k=0;k<c;k++)
				for(int i=0;i<a.rows();i++)
					for(int j=0;j<a.cols();j++)
						res.matrix[i][k]+=a.matrix[i][j]*b.matrix[j][k];
			return res;		
		}
		else throw new IncompatibleMatrixProduct();
	}
	public double determinant() throws MatrixException
	{
		return recursiveDeterminant(this);
	}
	private static double recursiveDeterminant(Matrix m) throws MatrixException
	{
		if(m.rows()!=m.cols())throw new DeterminantDoesNotExists();
		if(m.rows()==m.cols() && m.cols()==1)return m.get(0, 0);
		double sviluppoLaplace=0;
		int i =0;
		for(int j=0;j<m.cols();j++)
		{
			double complAlg = complementoAlgebrico(m,i,j);
			sviluppoLaplace += m.get(i, j)*complAlg;
		}	
		return sviluppoLaplace;
	}
	private static double complementoAlgebrico(Matrix m,int i,int j) throws MatrixException
	{
		Matrix minCompl = m.removeRow(i).removeColumn(j);
		return Math.pow(-1, i+j)*recursiveDeterminant(minCompl);
	}
	public Matrix transpose() throws MatrixException
	{
		Matrix res = this.copy();
		for(int i=0;i<rows();i++)
			for(int j=0;j<cols();j++)
				res.set(j,i,matrix[i][j]);
		return res;
	}
	public Matrix inverse() throws MatrixException
	{
		double det = determinant();
		if(det==0)throw new InverseMatrixDoesNotExists(); 
		Matrix inv = this.copy();
		for(int i=0;i<rows();i++)
			for(int j=0;j<cols();j++)
			{
				double complAlg =complementoAlgebrico(this,i,j);
				inv.set(i, j,complAlg);
			}
		inv = inv.transpose();
		return inv.multiply(1/det);
	}
	public Matrix removeColumn(int index)throws MatrixException
	{
		if(!areIndexesInside(0, index))throw new IndexOutOfMatrix();
		Matrix res = new Matrix(rows(),cols()-1);
		int offset =0, k=0;
		for(int i=0;i<rows();i++)
		{
			offset=0;
			for(int j=0;j<cols();j++)
			{
				if(j == index)
				{
					offset=1;
					continue;
				}
				k=j-offset;
				res.set(i, k, matrix[i][j]);
			}
		}
		return res;
	}
	public Matrix removeRow(int index)throws MatrixException
	{
		if(!areIndexesInside(index, 0))throw new IndexOutOfMatrix();
		Matrix res = new Matrix(rows()-1,cols());
		int offset =0, k=0;
		for(int j=0;j<cols();j++)
		{
			offset=0;
			for(int i=0;i<rows();i++)
			{
				if(i == index)
				{
					offset=1;
					continue;
				}
				k=i-offset;
				res.set(k, j, matrix[i][j]);
			}
		}
			
		return res;
	}
	public Matrix redefine(int newRows,int newCols) throws MatrixException
	{
		Matrix a = this;
		Matrix res = zero(newRows,newCols);
		for(int i=0;i<newRows;i++)
			for(int j=0;j<newCols;j++)
			{
				if(i<a.y && j<a.x)
				{
					res.matrix[i][j]=a.matrix[i][j];
				}
			}
		return res;
	}
	public static Matrix identity(int order) throws MatrixException
	{
		Matrix res = zero(order);
		for(int i=0;i<order;i++)
			res.matrix[i][i] = 1;
		return res;
	}
	public static Matrix zero(int rows,int cols) throws MatrixException
	{
		Matrix res = new Matrix(rows,cols);
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				res.matrix[i][j]=0;
		return res;
	}
	public static Matrix zero(int order) throws MatrixException
	{
		return zero(order,order);
	}
	
	public static int rowsOf(double a[][])
	{
		return a.length;
	}
	public static int colsOf(double a[][])
	{
		return a[0].length;
	}
	public static int rowsOf(float a[][])
	{
		return a.length;
	}
	public static int colsOf(float a[][])
	{
		return a[0].length;
	}
	public static int rowsOf(int a[][])
	{
		return a.length;
	}
	public static int colsOf(int a[][])
	{
		return a[0].length;
	}
	@Override
	public String toString() 
	{
		String s = "[";
		for(int i=0;i<y;i++)
		{
			s+="{";
			for(int j=0;j<x;j++)
			{
				s+= matrix[i][j];
				if(j!=x-1)s+=", ";
			}
			if(i!=y-1)s+="}\n";
			else s+="}";
		}
		s+="]";
		return s;	
	}
	@Override
	public boolean equals(Object obj) 
	{
		if(obj.getClass() == Matrix.class)
		{
			Matrix a = (Matrix)obj;
			if(hasSameRowsAndCols(this, a))
			{
				for(int i=0;i<y;i++)
					for(int j=0;j<x;j++)
						if(matrix[i][j]!=a.matrix[i][j])
							return false;
				return true;
			}
			else return false;
		}
		else return false;
	}
}
