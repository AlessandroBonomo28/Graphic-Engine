# JAVA Graphic Engine (Not very optimized)
I made this engine to understand how 3D graphics works. I started from a simple drawline function and progressively increased the complexity.
# The basic principle of plotting a triangle in 3D space
- Draw a line
```
Graphics2D g;
g.drawLine(x1, y1, x2, y2);
```
- Draw the outline of a triangle
```
[Inside Triangle class]
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
```
![1](https://user-images.githubusercontent.com/75626033/217095591-3cff1bb9-8f7d-4633-89fb-5aef558b6a2a.png)


![2](https://user-images.githubusercontent.com/75626033/217095607-30f498fc-d50a-4dee-a352-cc26b5c6d795.png)

![3](https://user-images.githubusercontent.com/75626033/217095618-ebf3b609-cc7a-474b-aacc-29dac7d3b83d.png)

- Visualization of a frustum

![viewing frustum](https://user-images.githubusercontent.com/75626033/217095508-80a96407-1ea6-4027-b686-8a27700aa156.JPG)
