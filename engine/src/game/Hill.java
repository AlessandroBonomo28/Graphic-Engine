package game;

import java.util.ArrayList;

import geometry.Vertex;

public class Hill {
	private ArrayList<Vertex> verticiHill;
	private Vertex startValue;
	public Hill(ArrayList<Vertex> vertexes) {
		setVerticiHill(vertexes);
		startValue = vertexes.get(0).copy();
	}
	public ArrayList<Vertex> getVerticiHill() {
		return verticiHill;
	}
	public void setVerticiHill(ArrayList<Vertex> verticiHill) {
		this.verticiHill = verticiHill;
	}
	public Vertex getStartValue() {
		return startValue;
	}

}
