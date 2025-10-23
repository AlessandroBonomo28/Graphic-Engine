package geometry;

import java.awt.Color;
import java.util.ArrayList;

import geometry.GeometryException.InsufficientTriangles;
import geometry.GeometryException.MeshException;
import mathUtils.Vector3;
import mathUtils.matrixExceptions.MatrixException;

public class Mesh 
{
	private ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	
	public Mesh(ArrayList<Triangle> triangles) throws MeshException 
	{
		this.triangles=triangles;
		if(!hasMinumumNumberOfTriangles())throw new InsufficientTriangles();
		
	}
	public Mesh(ArrayList<Triangle> triangles,Color c) throws MeshException 
	{
		this.triangles=triangles;
		if(!hasMinumumNumberOfTriangles())throw new InsufficientTriangles();
		setColor(c);
		
	}
	public Mesh(Triangle t1,Triangle ...triangles) throws MeshException 
	{
		this.triangles.add(t1);
		for(Triangle t:triangles)
			this.triangles.add(t);
		if(!hasMinumumNumberOfTriangles())throw new InsufficientTriangles();
		
	}
	public void setColor(Color c)
	{
		for(Triangle t:getTriangles())
			t.setColor(c);
	}
	private boolean hasMinumumNumberOfTriangles()
	{
		if(triangles.size()>=1)return true;
		else return false;
	}
	public ArrayList<Triangle> getTrianglesConnectedTo(Vertex v)
	{
		ArrayList<Triangle> result = new ArrayList<Triangle>();
		for(Triangle t:getTriangles())
		{
			if(t.contains(v))
				result.add(t);
		}
		return result;
	}
	public ArrayList<Vertex> getVertexesConnectedTo(Vertex v)
	{
		ArrayList<Vertex> result = new ArrayList<Vertex>();
		ArrayList<Triangle> trianglesConnected = getTrianglesConnectedTo(v);
		for(Triangle t:trianglesConnected)
		{
			if(t.getV1().equals(v))result.add(t.getV1());
			if(t.getV2().equals(v))result.add(t.getV2());
			if(t.getV3().equals(v))result.add(t.getV3());
		}
		return result;
	}
	public ArrayList<Vertex> getVertexes()
	{
		ArrayList<Vertex> vertexes = new ArrayList<Vertex>();
		for(Triangle t:getTriangles())
			vertexes.addAll(t.getVertexes());
		return vertexes;
	}
	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}
	public void setTriangles(ArrayList<Triangle> triangles) {
		this.triangles = triangles;
	}
	public ArrayList<Vector3> getTriangleCenters()
	{
		ArrayList<Vector3> positions = new ArrayList<Vector3>();
		for(Triangle t:triangles)
		{
			positions.add(t.centroid());
		}
		return positions;
	}
	public Vector3 center()
	{
		float cont=0;
		Vector3 sum=Vector3.zero();
		for(Vector3 v:getTriangleCenters())
		{
			sum=sum.sum(v);
			cont++;
		}
		if(cont!=0)
			return sum.multiply(1/cont);
		else return Vector3.zero();
	}
	public void addTriangle(Triangle t)
	{
		if(t!=null)
			triangles.add(t);
	}
	public void transla(Vector3 dir)
	{
		for(Triangle t:triangles)
		{
			t.transla(dir);
		}
	}
	
	public void ruotaSuAsseXYZ(Vector3 origine, Vector3 angoli) throws MatrixException 
	{
		for(Triangle t:triangles)
		{
			t.ruotaSuAsseXYZ(origine, angoli);
		}
	}
	public Mesh copy() throws MeshException
	{
		ArrayList<Triangle> triCopy = new ArrayList<Triangle>();
		for(Triangle t:getTriangles())
			triCopy.add(t.copy());
		return new Mesh(triCopy);
	}
}
