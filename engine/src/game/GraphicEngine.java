package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JFrame;

import Graphics.DrawableTriangle;
import Graphics.Rasterizer;
import geometry.Transform;
import geometry.Triangle;
import mathUtils.matrixExceptions.MatrixException;

public class GraphicEngine extends Drawer{
	private static final long serialVersionUID = 1L;
	private Rasterizer rasterizer;
	private ArrayList<Transform> transformToDraw = new ArrayList<Transform>();
	private Transform camera;
	
	public GraphicEngine(JFrame window,int widthWindow, int heightWindow,double FOV, Color bgColor) throws MatrixException
	{
		super(window,widthWindow,heightWindow,bgColor);
		camera = new Transform();
		rasterizer = new Rasterizer(widthWindow,heightWindow,FOV,camera);
	}
	public GraphicEngine(JFrame window,int widthWindow, int heightWindow, Color bgColor) throws MatrixException
	{
		super(window,widthWindow,heightWindow,bgColor);
		camera = new Transform();
		rasterizer = new Rasterizer(widthWindow,heightWindow,camera);
	}
	public GraphicEngine(JFrame window,int widthWindow, int heightWindow) throws MatrixException
	{
		super(window,widthWindow,heightWindow,Color.black);
		camera = new Transform();
		rasterizer = new Rasterizer(widthWindow,heightWindow,camera);
	}
	
	public Rasterizer getRasterizer() {
		return rasterizer;
	}
	public void addToDrawList(Transform t)
	{
		transformToDraw.add(t);
	}
	public void removeFromDrawList(Transform t)
	{
		int index = transformToDraw.indexOf(t);
		if(index!=-1)
			transformToDraw.remove(index);
	}
	public void clearDrawList()
	{
		transformToDraw = new ArrayList<Transform>();
	}
	@Override
	protected void draw(Graphics2D g) {
		super.draw(g);
		try 
		{
			ArrayList<DrawableTriangle> trianglesToDraw = new ArrayList<DrawableTriangle>();
			@SuppressWarnings("unchecked")
			ArrayList<Transform> transformToDrawTmp = (ArrayList<Transform>) transformToDraw.clone();
			for(Transform transform:transformToDrawTmp)
			{
				for(DrawableTriangle tri:rasterizer.rasterize(transform))
					trianglesToDraw.add(tri);	
			}
			// ordina per Z depth buffer
			Collections.sort(trianglesToDraw, new Comparator<Triangle>() {
				@Override
				public int compare(Triangle a, Triangle b) {
					double aZ = a.centroid().z;
					double bZ = b.centroid().z;
					if(aZ == bZ)return 0;
					if(aZ>bZ)return -1;
					else return 1;
				}
			});
			for(DrawableTriangle tri:trianglesToDraw)
			{
				drawTriangle(g,tri);
			}
			
		} catch (MatrixException e) {
			e.printStackTrace();
		}
	}
	protected void drawTriangle(Graphics2D g,DrawableTriangle tri)
	{
		tri.fillTriangleByMajorColor(g);
		//tri.fillTriangle(g, Color.gray);
		//tri.drawOutlineTriangle(g, Color.black);
	}

	public Transform getCamera() {
		return camera;
	}
	
}
