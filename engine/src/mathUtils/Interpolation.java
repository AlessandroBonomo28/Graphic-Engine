package mathUtils;

import java.util.ArrayList;


public class Interpolation 
{
	public static double fattoriale(float x)
	{
		if(x == 0)return 1;
		else return x * fattoriale(x-1);
	}
	public static double coeffBinomiale(float n,float k)
	{
		if(n==k && n>=0)return 1;
		if(k==0 && n>=0)return 1;
		return fattoriale(n)/(fattoriale(k)*fattoriale(n-k));
	}
	public static Vector2 interpola(ArrayList<Vector2> dots,double t)
	{
		double sumX =0;
		double sumY = 0;
		int n = dots.size();
		for(int k=0;k<n;k++)
		{
			double w = bWeight(k,n-1,t);
			sumX += w*dots.get(k).x;
			sumY += w*dots.get(k).y;
		}
		return new Vector2((int)sumX,(int)sumY);
	}
	public static Vector3 interpolaVector3(ArrayList<Vector3> dots,double t)
	{
		double sumX =0;
		double sumY = 0;
		double sumZ = 0;
		int n = dots.size();
		for(int k=0;k<n;k++)
		{
			double w = bWeight(k,n-1,t);
			sumX += w*dots.get(k).x;
			sumY += w*dots.get(k).y;
			sumZ += w*dots.get(k).z;
		}
		return new Vector3((int)sumX,(int)sumY,(int)sumZ);
	}
	public static Vector3 interpolaVector3(double t,Vector3 ...dots)
	{
		ArrayList<Vector3> l = new ArrayList<Vector3>();
		for(Vector3 v:dots)
			l.add(v);
		return interpolaVector3(l, t);
	}
	public static Vector2 interpola(double t,Vector2 ...dots)
	{
		ArrayList<Vector2> l = new ArrayList<Vector2>();
		for(Vector2 v:dots)
			l.add(v);
		return interpola(l, t);
	}
	public static double interpola(double[] array,double t)
	{
		double sum=0;
		int n = array.length;
		for(int k=0;k<n;k++)
		{
			double w = bWeight(k,n-1,t);
			sum+= w*array[k];
		}
		return sum;
	}
	public static double interpola(double t,double ...array)
	{
		double sum=0;
		int n = array.length;
		for(int k=0;k<n;k++)
		{
			double w = bWeight(k,n-1,t);
			sum+= w*array[k];
		}
		return sum;
	}
	public static double interpola(double a,double b,double t)
	{
		return t*a + (1-t)*b;
	}
	private static double bWeight(float k,float n,double t)
	{
		double r = coeffBinomiale(n, k)*Math.pow((1-t), n-k)*Math.pow(t, k);
		return r;
	}
	
	
	
}
