package mathUtils;

public class Vector2 
{
	public double x=0, y=0;
	public Vector2()
	{
		x=0;
		y=0;
	}
	public Vector2(float x,float y)
	{
		this.x = x;
		this.y = y;
	}
	public Vector2(double x,double y)
	{
		this.x = x;
		this.y = y;
	}
	public Vector2 normalized()
	{
		if(x ==0 && y == 0)return this;
		double len = length();
		return new Vector2(x/len,y/len);
	}
	public double length()
	{
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	public double angleBetween(Vector2 b)
	{
		return angleAtoB(this, b);
	}
	// ritorna l'angolo tra i vettori senza il segno
	public static double angleBetween(Vector2 a,Vector2 b)
	{
		return Math.acos(dot(a,b)/(a.length()*b.length()));
	}
	public double angleToB(Vector2 b)
	{
		return angleAtoB(this, b);
	}
	// ritorna l'angolo tra i vettori con il segno
	public static double angleAtoB(Vector2 a,Vector2 b)
	{
		return Math.atan2(a.y, a.x)-Math.atan2(b.y, b.x);
	}
	public static Vector2 polarToCartesian(double len,double angle)
	{
		return new Vector2(len*Math.cos(angle),len*Math.sin(angle));
	}
	public static Vector2 polarToCartesian(Vector2 polarForm)
	{
		double len = polarForm.x, angle=polarForm.y;
		return new Vector2(len*Math.cos(angle),len*Math.sin(angle));
	}
	public Vector2 getPolarForm()
	{
		double len = length();
		if( len==0)return Vector2.zero();
		return new Vector2(len,Math.atan2(y,x));
	}
	public Vector2 sum(Vector2 b)
	{
		return sum(this,b);
	}
	public static Vector2 sum(Vector2 a,Vector2 b,Vector2 ...other)
	{
		Vector2 res = new Vector2(a.x+b.x,a.y+b.y);
		for(Vector2 v:other)
		{
			res.x+=v.x;
			res.y+=v.y;
		}
		return res;
	}
	public Vector2 sub(Vector2 b)
	{
		return sub(this,b);
	}
	public static Vector2 sub(Vector2 a,Vector2 b)
	{
		return sum(a,b.multiply(-1));
	}
	public Vector2 multiply(double s)
	{
		return new Vector2(x*s,y*s);
	}
	public Vector2 multiply(float s)
	{
		return new Vector2(x*s,y*s);
	}
	public Vector2 multiply(int s)
	{
		return new Vector2(x*s,y*s);
	}
	public double dot(Vector2 b)
	{
		return dot(this,b);
	}
	public static double dot(Vector2 a,Vector2 b, Vector2 ...other)
	{
		double prodX=a.x*b.x,prodY=a.y*b.y;
		for(Vector2 v:other)
		{
			prodX*=v.x;
			prodY*=v.y;
		}
		return prodX + prodY;
	}
	public Vector2 projectOntoB(Vector2 b)
	{
		return projectAontoB(this,b);
	}
	public static Vector2 projectAontoB(Vector2 a,Vector2 b)
	{
		Vector2 u = b.normalized();
		return u.multiply(dot(a,u));
	}
	public double distance(Vector2 b)
	{
		return distance(this,b);
	}
	public Vector3 toVector3()
	{
		return new Vector3(x,y,0);
	}
	public static double distance(Vector2 a,Vector2 b)
	{
		return sub(a,b).length();
	}
	public static Vector2 avg(Vector2 a,Vector2 b,Vector2 ...other)
	{
		Vector2 tot=zero();
		tot = tot.sum(a);
		tot = tot.sum(b);
		float cont=2;
		for(Vector2 v:other)
		{
			tot=tot.sum(v);
			cont+=1;
		}
		return tot.multiply(1/cont);
	}
	public Vector2 mod(double a)
	{
		return new Vector2(x%a,y%a);
	}
	public static Vector2 up()
	{
		return new Vector2(0,1);
	}
	public static Vector2 down()
	{
		return new Vector2(0,-1);
	}
	public static Vector2 right()
	{
		return new Vector2(1,0);
	}
	public static Vector2 left()
	{
		return new Vector2(-1,0);
	}
	public static Vector2 zero()
	{
		return new Vector2(0,0);
	}
	public static Vector2 one()
	{
		return new Vector2(1,1);
	}
	public String toString()
	{
		return "("+String.format("%.2f", x)+", "+String.format("%.2f", y)+")";
	}
	public boolean equals(Object o)
	{
		if(o.getClass()==Vector2.class)
		{
			Vector2 v = (Vector2)o;
			if(v.x == x && v.y == y)return true;
			else return false;
		}
		else return false;
	}
	public Vector2 copy()
	{
		return new Vector2(x,y);
	}
}
