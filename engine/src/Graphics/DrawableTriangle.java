package Graphics;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;

import geometry.Triangle;
import geometry.Vertex;
import mathUtils.Vector2;
import mathUtils.Vector3;

public class DrawableTriangle extends Triangle {
	private double brightness = 1;
	private boolean enableBrightness = true;
	public DrawableTriangle(Triangle t)
	{
		super(t.getV1(),t.getV2(),t.getV3());
	}
	public DrawableTriangle(Triangle t,double brightness)
	{
		super(t.getV1(),t.getV2(),t.getV3());
		setBrightness(brightness);
	}
	public DrawableTriangle(Triangle t,double brightness, boolean enableBrightness)
	{
		super(t.getV1(),t.getV2(),t.getV3());
		setBrightness(brightness);
		this.setEnableBrightness(enableBrightness);
	}
	public DrawableTriangle(Vector3 v1, Vector3 v2, Vector3 v3) {
		super(v1, v2, v3);
		// TODO Auto-generated constructor stub
	}

	public DrawableTriangle(Vertex v1, Vertex v2, Vertex v3) {
		super(v1, v2, v3);
		// TODO Auto-generated constructor stub
	}

	public DrawableTriangle(Vector3 v1, Vector3 v2, Vector3 v3, Vector3 normal) {
		super(v1, v2, v3, normal);
		// TODO Auto-generated constructor stub
	}

	public DrawableTriangle(Vertex v1, Vertex v2, Vertex v3, Vector3 normal) {
		super(v1, v2, v3, normal);
		// TODO Auto-generated constructor stub
	}
	
	public double getLightIntensity() {
		return brightness;
	}
	private double clamp(double val, double min, double max) {
	    return Math.max(min, Math.min(max, val));
	}
	public void setBrightness(double lightIntensity) {
		this.brightness = clamp(lightIntensity,0,1);
	}
	private Color setColorBrightness(Color c,float brightness)
	{
		if(!enableBrightness)return c;
		float[] hsv = new float[3];
		Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),hsv);
		hsv[2] = brightness;
		return new Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
	}
	public void drawOutlineTriangle(Graphics2D g,Color c)
	{
		g.setColor(c);
		Vector2 vp1 = v1.getPosition().toVector2();
		Vector2 vp2 = v2.getPosition().toVector2();
		Vector2 vp3 = v3.getPosition().toVector2();
		
		g.drawLine((int)vp1.x, (int)vp1.y,(int) vp2.x, (int)vp2.y);
		g.drawLine((int)vp2.x, (int)vp2.y,(int) vp3.x, (int)vp3.y);
		g.drawLine((int)vp3.x, (int)vp3.y,(int) vp1.x, (int)vp1.y);
	}
	public void fillTriangle(Graphics2D g,Color c)
	{
		c = setColorBrightness(c, (float)brightness);
		g.setColor(c);
		Polygon triPoly = getTrianglePoly();
		g.fillPolygon(triPoly);
	}
	public void fillTriangleByMajorColor(Graphics2D g)
	{
		if(hasAllVertexOfSameColor())
			fillTriangle(g, v1.getColor());
		else
		{
			int v1ColorOccur = getPositiveColorComparisons(v1.getColor())-1;
			int v2ColorOccur = getPositiveColorComparisons(v2.getColor())-1;
			int v3ColorOccur = getPositiveColorComparisons(v3.getColor())-1;
			int maxColorOccur = Math.max(v3ColorOccur,Math.max(v1ColorOccur, v2ColorOccur));
			
			if(maxColorOccur==0)fillTriangleVertexColor(g);
			else if(maxColorOccur == v1ColorOccur)fillTriangle(g, v1.getColor());
			else if(maxColorOccur == v2ColorOccur)fillTriangle(g, v2.getColor());
			else fillTriangle(g, v3.getColor());
		}
	}
	private int getPositiveColorComparisons(Color c)
	{
		int cont =0;
		if(c.equals(v1.getColor()))cont++;
		if(c.equals(v2.getColor()))cont++;
		if(c.equals(v3.getColor()))cont++;
		return cont;
	}
	private void fillTriangleVertexColor(Graphics2D g)
	{
		// TODO da migliorare
		if(hasAllVertexOfSameColor())
			fillTriangle(g, v1.getColor());
		
		Polygon triPoly = getTrianglePoly();
		Color vertex1Col = setColorBrightness(v1.getColor(),(float)brightness);
		Color vertex2Col = setColorBrightness(v2.getColor(),(float)brightness);
		Color vertex3Col = setColorBrightness(v3.getColor(),(float)brightness);
		
		Color transp = new Color (0,0,0,0);
		Vector2 vp1 = v1.getPosition().toVector2();
		Vector2 vp2 = v2.getPosition().toVector2();
		Vector2 vp3 = v3.getPosition().toVector2();
		Vector2 center;
		center = Vector2.avg(vp2, vp3);
		GradientPaint v1Grad = new GradientPaint(
				 (int)center.x, (int)center.y, transp,
				 (int) vp1.x, (int)vp1.y, vertex1Col);
		
		center = Vector2.avg(vp1, vp3);
        GradientPaint v2Grad = new GradientPaint(
        		(int)center.x, (int)center.y, transp,
        		(int) vp2.x, (int)vp2.y, vertex2Col);

        center = Vector2.avg(vp1, vp2);
        GradientPaint v3Grad = new GradientPaint(
        		(int)center.x, (int)center.y, transp,
        		(int) vp3.x, (int)vp3.y, vertex3Col);
        
        g.setPaint(v1Grad);
        g.fillPolygon(triPoly);
        
        g.setPaint(v2Grad);
        g.fillPolygon(triPoly);
        
        g.setPaint(v3Grad);
        g.fillPolygon(triPoly);
	}
	public Polygon getTrianglePoly()
	{
		int[] x = {(int)v1.getPosition().x,(int)v2.getPosition().x,(int)v3.getPosition().x};
		int[] y = {(int)v1.getPosition().y,(int)v2.getPosition().y,(int)v3.getPosition().y};
		return new Polygon(x, y, 3);
	}
	public boolean isEnableBrightness() {
		return enableBrightness;
	}
	public void setEnableBrightness(boolean enableBrightness) {
		this.enableBrightness = enableBrightness;
	}
	public boolean hasAllVertexOfSameColor()
	{
		if(v1.getColor().equals(v2.getColor()) && v2.getColor().equals(v3.getColor()))
			return true;
		else return false;
	}

}
