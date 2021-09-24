package geometry;




import mathUtils.Matrix;
import mathUtils.Operations;
import mathUtils.Vector3;
import mathUtils.matrixExceptions.MatrixException;

public class Transformation {

	private Transformation() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public static Matrix getPerspectiveMatrix(double FOV,int width,int heigth)
	{
		double FOVrad = 1/Math.tan(FOV*0.5f*180/Math.PI);
		double zFar=1000,zNear=0.1f;
		double screenRatio = width/heigth;
		
		double[][] perspectiveMatrix = {
				{screenRatio*FOVrad,0,0,0},
				{0,FOVrad,0,0},
				{0,0,zFar/(zFar-zNear),1},
				{0,0,(-zFar*zNear)/(zFar-zNear),0}
		};
		return new Matrix(perspectiveMatrix);
	}
	
	
	public static Matrix rotation2D(double angolo) throws MatrixException
	{
		return ZaxisRotation(angolo).redefine(2, 2);
	}
	public static Matrix ZaxisRotation(double angolo)
	{
		double[][] m = {
				{Math.cos(angolo),-Math.sin(angolo),0,0},
				{Math.sin(angolo),Math.cos(angolo),0,0},
				{0,0,1,0},
				{0,0,0,1}
		};
		return  new Matrix(m);
	}
	public static Matrix YaxisRotation(double angolo)
	{
		double[][] m = {
				{Math.cos(angolo),0,Math.sin(angolo),0},
				{0,1,0,0},
				{-Math.sin(angolo),0,Math.cos(angolo),0},
				{0,0,0,1}
		};
		return new Matrix(m);
	}
	public static Matrix XaxisRotation(double angolo)
	{
		double[][] m = {
				{1,0,0,0},
				{0,Math.cos(angolo),-Math.sin(angolo),0},
				{0,Math.sin(angolo),Math.cos(angolo),0},
				{0,0,0,1}
		};
		return new Matrix(m);
	}

	
	
	public static Matrix makeTranslationMatrix(Vector3 translation)
	{
		double[][] m = {
				{1,0,0,translation.x},
				{0,1,0,translation.y},
				{0,0,1,translation.z},
				{0,0,0,1}
		};
		return new Matrix(m);
	}
	public static Matrix makeRotationMatrix(Vector3 angles) throws MatrixException
	{
		Matrix rotation = ZaxisRotation(angles.z);
		rotation = Matrix.multiply(XaxisRotation(angles.x),rotation);
		rotation = Matrix.multiply(YaxisRotation(angles.y),rotation);
		return rotation;
	}
	
	
	public static Vector3 ruotaSuAsseXYZ(Vector3 punto,Vector3 origine,Vector3 angoli) throws MatrixException
	{
		Matrix t0 = Transformation.makeTranslationMatrix(origine.multiply(-1));
		Matrix t1 = Transformation.makeTranslationMatrix(origine);
		Vector3 v = Operations.multiplyMajorColumn(t0, punto);
		v= Operations.multiplyMajorColumn(Transformation.makeRotationMatrix(angoli), v);
		return Operations.multiplyMajorColumn(t1, v);
	}
	
}
