# JAVA Graphic Engine (Not very optimized)
I made this engine to understand how 3D graphics works. I started from a simple drawline function and progressively increased the complexity.
- Draw a line
```
Graphics2D g;
g.drawLine(x1, y1, x2, y2);
```
- Draw the a triangle
```
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
```
