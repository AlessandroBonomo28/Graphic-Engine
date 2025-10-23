package geometry;


import java.awt.Color;

import mathUtils.*;
import mathUtils.matrixExceptions.MatrixException;


public class Vertex {
	private Vector3 position;
	private Vector3 nonRotatedPosition;
	private Vector3 posShiftedByNormal =  new Vector3();
	private Vector3 nonRotatedPosShiftedByNormal = new Vector3();
	private Color color = Color.gray;
	public Vertex(Vector3 position) 
	{
		this.position = position.copy();
		nonRotatedPosition = position.copy();
	}
	public Vertex(Vector3 position, Color c) 
	{
		this.position = position.copy();
		nonRotatedPosition = position.copy();
		color =c;
	}
	public Vertex(Vector3 position,Vector3 normal) 
	{
		this.position = position;
		nonRotatedPosition = position.copy();
		setNormal(normal);
	}
	public Vertex(Vector3 position,Vector3 normal,Color c) 
	{
		this.position = position;
		nonRotatedPosition = position.copy();
		setNormal(normal);
		color = c;
	}
	public void transla(Vector3 dir)
	{
		position = position.sum(dir);
		nonRotatedPosition = nonRotatedPosition.sum(dir);
		
		posShiftedByNormal = posShiftedByNormal.sum(dir);
		nonRotatedPosShiftedByNormal = nonRotatedPosShiftedByNormal.sum(dir);
	}
	public Vector3 getPosition() {
		return position;
	}
	public void setPosition(Vector3 position) {
		this.position = position.copy();
		nonRotatedPosition = position.copy();
	}
	public Vector3 getNormal() {
		return posShiftedByNormal.sub(position).normalized();
	}
	public void setNormal(Vector3 normal) {
		posShiftedByNormal = position.sum(normal);
		nonRotatedPosShiftedByNormal = posShiftedByNormal.copy();
	}
	
	public void ruotaSuAsseXYZ(Vector3 origine, Vector3 angoli) throws MatrixException 
	{
		position = Transformation.ruotaSuAsseXYZ(nonRotatedPosition, origine, angoli);
		posShiftedByNormal = Transformation.ruotaSuAsseXYZ(nonRotatedPosShiftedByNormal, 
				origine, angoli);
	}
	
	public boolean isAboveOrOntoPlane(Vector3 planePoint, Vector3 planeNormal) {
		planeNormal = planeNormal.normalized();
		double d1 = position.dot(planeNormal);
		double d2 = planePoint.dot(planeNormal);
		return (d1-d2) >= 0;
	}
	
	public Vertex copy()
	{
		return new Vertex(position,posShiftedByNormal,color);
	}
	@Override
	public String toString() {
		String s = "[Position: "+position+", Normal: "+getNormal()+", Color: "+color+"]";
		return s;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color c){
		color = c;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass() == Vertex.class)
		{
			Vertex v = (Vertex)obj;
			return v.getPosition().equals(position);
		}
		else return false;
	}
}
