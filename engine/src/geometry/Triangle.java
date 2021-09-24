package geometry;

import java.awt.Color;
import java.util.ArrayList;

import mathUtils.*;
import mathUtils.matrixExceptions.MatrixException;

public class Triangle {
	protected Vertex v1;
	protected Vertex v2;
	protected Vertex v3;
	protected Vector3 normal;
	public Triangle(Vector3 v1, Vector3 v2, Vector3 v3) {
		super();
		this.v1 = new Vertex(v1);
		this.v2 = new Vertex(v2);
		this.v3 = new Vertex(v3);
		normal = calcNormal();
	}
	public Triangle(Vertex v1, Vertex v2, Vertex v3) {
		super();
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		normal = calcNormal();
	}
	public Triangle(Vector3 v1, Vector3 v2, Vector3 v3, Vector3 normal) {
		super();
		this.v1 = new Vertex(v1);
		this.v2 = new Vertex(v2);
		this.v3 = new Vertex(v3);
		this.normal = normal.copy();
	}
	public Triangle(Vertex v1, Vertex v2, Vertex v3, Vector3 normal) {
		super();
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.normal = normal.copy();
	}
	private Vector3 calcNormal()
	{
		return Vector3.avg(this.v1.getNormal(),this.v2.getNormal(),this.v3.getNormal());
	}
	public void setColor(Color c)
	{
		v1.setColor(c);
		v2.setColor(c);
		v3.setColor(c);
	}
	public Vertex getV1() {
		return v1;
	}
	public void setV1(Vertex v1) {
		this.v1 = v1;
	}
	public Vertex getV2() {
		return v2;
	}
	public void setV2(Vertex v2) {
		this.v2 = v2;
	}
	public Vertex getV3() {
		return v3;
	}
	public void setV3(Vertex v3) {
		this.v3 = v3;
	}
	public ArrayList<Vertex> getVertexes()
	{
		ArrayList<Vertex> vertexes = new ArrayList<Vertex>();
		vertexes.add(v1);
		vertexes.add(v2);
		vertexes.add(v3);
		return vertexes;
	}
	public Vector3 getNormal() {
		return normal;
	}
	public void setNormal(Vector3 normal) {
		this.normal = normal.copy();
	}
	public void transla(Vector3 dir)
	{
		v1.transla(dir);
		v2.transla(dir);
		v3.transla(dir);
	}
	public ArrayList<Vector3> getVertexPositions()
	{
		ArrayList<Vector3> positions = new ArrayList<Vector3>();
		positions.add(v1.getPosition());
		positions.add(v2.getPosition());
		positions.add(v3.getPosition());
		return positions;
	}
	// ritorna i triangoli risultato dell estrusione, al primo posto dell'array c'e' il triangolo estruso
	// gli eulerAngles passati per parametro fanno ruotare il triangolo estruso
	// specificare un centro di rotazione (default = centroide del triangolo che si vuole estrudere)
	public ArrayList<Triangle> getTrianglesResultingFromExtrusion(Vector3 dirExtrusion,Vector3 eulerAngles,Vector3 centerRot) throws MatrixException
	{
		ArrayList<Triangle> result = new ArrayList<Triangle>();
		Vector3 v1 = this.v1.getPosition().copy();
		Vector3 v2 = this.v2.getPosition().copy();
		Vector3 v3 = this.v3.getPosition().copy();
		
		Vector3 v4 = Transformation.ruotaSuAsseXYZ(v1, centerRot, eulerAngles).sum(dirExtrusion);
		Vector3 v5 = Transformation.ruotaSuAsseXYZ(v2, centerRot, eulerAngles).sum(dirExtrusion);
		Vector3 v6 = Transformation.ruotaSuAsseXYZ(v3, centerRot, eulerAngles).sum(dirExtrusion);
		
		// triangolo estruso
		//Vector3 normal = Vector3.cross(v4, v5);
//		Vector3 normal = Vector3.cross(v4.sub(v5), v4.sub(v6));
		Vector3 normal = calcNormal(v4,v5,v6);
		
		Triangle t = new Triangle(v4,v5,v6);
		t.setNormal(normal);
		result.add(t);
		
		// rettangolo 1
//		normal = Vector3.cross(v1, v2);
//		normal = Vector3.cross(v1.sub(v2), v1.sub(v5));
		normal = calcNormal(v1,v2,v5);
		t = new Triangle(v1,v2,v5);
		t.setNormal(normal);
		result.add(t);
		
		t = new Triangle(v1,v4,v5);
		t.setNormal(normal);
		result.add(t);
		
		// rettangolo 2
//		normal = Vector3.cross(v2, v3);
//		normal = Vector3.cross(v2.sub(v3), v2.sub(v6));
		normal = calcNormal(v2,v3,v6);
		t = new Triangle(v2,v3,v6);
		t.setNormal(normal);
		result.add(t);
		
		t= new Triangle(v2,v5,v6);
		t.setNormal(normal);
		result.add(t);
		
		// rettangolo 3
		
//		normal = Vector3.cross(v1, v3);
//		normal = Vector3.cross(v1.sub(v3), v1.sub(v4));
		normal = calcNormal(v1,v3,v4);
		t = new Triangle(v1,v3,v4);
		t.setNormal(normal);
		result.add(t);
		
		t = new Triangle(v4,v3,v6);
		t.setNormal(normal);
		result.add(t);
		return result;
	}
	private Vector3 calcNormal(Vector3 p1, Vector3 p2, Vector3 p3)
	{
		Vector3 v = p2.sub(p1).normalized();
		Vector3 w = p3.sub(p1).normalized();
		return new Vector3(v.y * w.z - v.z*w.y, 
							v.z * w.x - v.x*w.z,
							v.x *w.y - v.y*w.x).normalized();
	}
	public ArrayList<Triangle> getTrianglesResultingFromExtrusion(Vector3 dirExtrusion,Vector3 eulerAngles) throws MatrixException
	{
		return getTrianglesResultingFromExtrusion(dirExtrusion, eulerAngles,centroid());
	}
	public ArrayList<Triangle> getTrianglesResultingFromExtrusion(Vector3 dirExtrusion) throws MatrixException
	{
		return getTrianglesResultingFromExtrusion(dirExtrusion, Vector3.zero(),centroid());
	}
	public Vector3 centroid()
	{
		double x = (v1.getPosition().x+v2.getPosition().x+v3.getPosition().x)/3;
		double y = (v1.getPosition().y+v2.getPosition().y+v3.getPosition().y)/3;
		double z = (v1.getPosition().z+v2.getPosition().z+v3.getPosition().z)/3;
		return new Vector3(x,y,z);
	}
	
	public void ruotaSuAsseXYZ(Vector3 origine, Vector3 angoli) throws MatrixException 
	{
		v1.ruotaSuAsseXYZ(origine, angoli);
		v2.ruotaSuAsseXYZ(origine, angoli);
		v3.ruotaSuAsseXYZ(origine, angoli);
		normal = calcNormal();
	}
	public boolean contains(Vertex v)
	{
		if(v.equals(v1) || v.equals(v2) || v.equals(v3))
			return true;
		else return false;
	}
	public Triangle copy()
	{
		return new Triangle(v1.copy(),v2.copy(),v3.copy());
	}
	@Override
	public String toString() {
		String s = "[v1: "+v1+"\n"+"v2: "+v2+"\n"+"v3: "+v3+"]";
		return s;
	}
}
