package mathUtils;

import java.util.ArrayList;

public class Vector3
{
	public double x,y,z;
//	public Vector3(float x, float y,float z)
//	{
//		this.x=x;
//		this.y=y;
//		this.z=z;
//	}
	public Vector3(double x, double y,double z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public Vector3()
	{
		x=y=z=0;
	}
	public double distance(Vector3 b) 
	{
		return sub(this,b).length();
	}
	
	public static double distance(Vector3 a, Vector3 b) 
	{
		return sub(a,b).length();
	}
	public static double distanceMagnitude(Vector3 a, Vector3 b) 
	{
		return sub(a,b).lengthMagnitude();
	}
	public double distanceMagnitude(Vector3 b) 
	{
		return sub(this,b).lengthMagnitude();
	}
	public double length() 
	{
		return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z,2));
	}
	public double lengthMagnitude() 
	{
		return Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z,2);
	}
	public Vector3 normalized()
	{
		if(x ==0 && y == 0 && z==0)return this;
		double len = length();
		return new Vector3(x/len,y/len,z/len);
	}
	public static double angleBetween(Vector3 a,Vector3 b)
	{
		return Math.acos(dot(a,b)/(a.length()*b.length()));
	}
	public static Vector3 polarToCartesian(double len,double theta,double phi)
	{
		return new Vector3(len*Math.sin(theta)*Math.cos(phi),len*Math.cos(theta),len*Math.sin(phi)*Math.sin(theta));
	}
	public static Vector3 polarToCartesian(Vector3 polarForm)
	{
		double len = polarForm.x, theta=polarForm.y, phi=polarForm.z;
		return new Vector3(len*Math.sin(theta)*Math.cos(phi),len*Math.cos(theta),len*Math.sin(phi)*Math.sin(theta));
	}
	public Vector3 getPolarForm()
	{
		double len = length();
		if(len ==0)return Vector3.zero();
		return new Vector3(len,Math.acos(y/len),Math.atan2(z,x));
	}
	public Vector3 sum(double x)
	{
		return sum(this,one().multiply(x));
	}
	public Vector3 sum(Vector3 b)
	{
		return sum(this,b);
	}
	public static Vector3 sum(Vector3 a,Vector3 b,Vector3 ...other)
	{
		Vector3 res = new Vector3(a.x+b.x,a.y+b.y,a.z+b.z);
		for(Vector3 v:other)
		{
			res.x+=v.x;
			res.y+=v.y;
			res.z+=v.z;
		}
		return res;
	}
	public Vector3 sub(Vector3 b)
	{
		return sub(this,b);
	}
	public static Vector3 sub(Vector3 a,Vector3 b)
	{
		return sum(a,b.multiply(-1));
	}
	public Vector3 multiply(double s)
	{
		return new Vector3(x*s,y*s,z*s);
	}
	public Vector3 projectoOntoB(Vector3 b)
	{
		return projectAontoB(this, b);
	}
	public static Vector3 projectAontoB(Vector3 a,Vector3 b)
	{
		Vector3 u = b.normalized();
		return u.multiply(dot(a,u));
	}
	public Vector3 cross(Vector3 b)
	{
		return cross(this,b);
	}
	public static Vector3 cross(Vector3 a,Vector3 b)
	{
		double x = a.y * b.z - a.z * b.y; 
		double y = a.z * b.x - a.x * b.z; 
		double z = a.x * b.y - a.y * b.x; 
		return new Vector3(x,y,z);
	}
	public double areaCross(Vector3 b)
	{
		return areaCross(this, b);
	}
	public static double areaCross(Vector3 a,Vector3 b)
	{
		double theta = angleBetween(a, b);
		return a.length()*b.length()*Math.sin(theta);
	}
	public double dot(Vector3 b)
	{
		return dot(this,b);
	}
	public static double dot(Vector3 a,Vector3 b,Vector3 ...other)
	{
		double prodX = a.x*b.x;
		double prodY = a.y*b.y;
		double prodZ = a.z*b.z;
		for(Vector3 v:other)
		{
			prodX*=v.x;
			prodY*=v.y;
			prodZ*=v.z;
		}
		return prodX + prodY + prodZ;
	}
	public static Vector3 avg(Vector3 a,Vector3 b,Vector3 ...other)
	{
		Vector3 tot=Vector3.zero();
		tot = tot.sum(a);
		tot = tot.sum(b);
		float cont=2;
		for(Vector3 v:other)
		{
			tot=tot.sum(v);
			cont+=1;
		}
		return tot.multiply(1/cont);
	}
	public static Vector3 avg(ArrayList<Vector3> other) {
		if(other.size()==0)return Vector3.zero();
		Vector3 tot=Vector3.zero();
		for(Vector3 v:other)
			tot=tot.sum(v);
		return tot.multiply((double)1/other.size());
	}
	public Vector3 round()
	{
		return new Vector3(Math.round(x),Math.round(y),Math.round(z));
	}
	public Vector3 mod(double a)
	{
		return new Vector3(x%a,y%a,z%a);
	}
	public Vector2 toVector2()
	{
		return new Vector2(x,y);
	}
	public static Vector3 zero()
	{
		return new Vector3(0,0,0);
	}
	public static Vector3 one()
	{
		return new Vector3(1,1,1);
	}
	public static Vector3 forward()
	{
		return new Vector3(0,0,1);
	}
	public static Vector3 backward()
	{
		return new Vector3(0,0,-1);
	}
	public static Vector3 right()
	{
		return new Vector3(1,0,0);
	}
	public static Vector3 left()
	{
		return new Vector3(-1,0,0);
	}
	public static Vector3 up()
	{
		return new Vector3(0,1,0);
	}
	public static Vector3 down()
	{
		return new Vector3(0,-1,0);
	}
	public String toString()
	{
		return "("+String.format("%.2f", x)+", "+String.format("%.2f", y)+", "
				+String.format("%.2f", z)+")";
	}
	public boolean equals(Object o)
	{
		if(o.getClass()==Vector2.class)
		{
			Vector2 v = (Vector2)o;
			if(v.x == x && v.y == y)return true;
			else return false;
		}
		else if (o.getClass() ==Vector3.class)
		{
			Vector3 v = (Vector3)o;
			if(v.x == x && v.y == y && v.z == z)return true;
			else return false;
		}
		else return false;
	}
	public Vector3 copy()
	{
		return new Vector3(x,y,z);
	}
}
