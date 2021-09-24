package game.demos;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JFrame;

import Graphics.DrawableTriangle;
import Graphics.Rasterizer;
import fileReaders3D.ObjFileReader;
import fileReaders3D.PlyFileReader;
import fileReaders3D.FileReaderExceptions.FileReaderException;
import game.Audio;
import game.GameEngine;
import game.Hill;
import geometry.*;
import geometry.GeometryException.MeshException;
import mathUtils.*;
import mathUtils.matrixExceptions.*;

public class MainGame_Tesseract extends GameEngine
{
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = (int) (1920/1.5f);
	private static final int HEIGHT = (int) (1080/1.5f);
	private static Color bgColor = new Color(33, 0, 79);
	
	ObjFileReader objReader;
	PlyFileReader plyReader;
	
	public Transform camera;
	public Transform tesseractTransform;
	ArrayList<Vertex> tesseractVertexesOriginal = new ArrayList<Vertex>();
	ArrayList<Vertex> tesseractVertexes = new ArrayList<Vertex>();
	double[] wVertexes;
	
	// matrici di rotazione
	private Matrix getXYrotationMatrix(double angle)
	{
		double[][] m = {
				{Math.cos(angolo),-Math.sin(angolo),0,0},
				{Math.sin(angolo),Math.cos(angolo),0,0},
				{0,0,1,0},
				{0,0,0,1}
			};
		return new Matrix(m);
	}
	private Matrix getXZrotationMatrix(double angle)
	{
		double[][] m = {
				{Math.cos(angolo),0,-Math.sin(angolo),0},
				{0,1,0,0},
				{Math.sin(angolo),0,Math.cos(angolo),0},
				{0,0,0,1}
			};
		return new Matrix(m);
	}
	private Matrix getXWrotationMatrix(double angle)
	{
		double[][] m = {
				{Math.cos(angolo),0,0,-Math.sin(angolo)},
				{0,1,0,0},
				{0,0,1,0},
				{Math.sin(angolo),0,0,Math.cos(angolo)}
			};
		return new Matrix(m);
	}
	private Matrix getZWXYrotationMatrix(double angle)
	{
		double[][] m = {
				{Math.cos(angolo),-Math.sin(angolo),0,0},
				{Math.sin(angolo),Math.cos(angolo),0,0},
				{0,0,Math.cos(angolo),-Math.sin(angolo)},
				{0,0,Math.sin(angolo),Math.cos(angolo)}
			};
		return new Matrix(m);
	}
	private Matrix getZWrotationMatrix(double angle)
	{
		double[][] m = {
				{1,0,0,0},
				{0,1,0,0},
				{0,0,Math.cos(angolo),-Math.sin(angolo)},
				{0,0,Math.sin(angolo),Math.cos(angolo)}
			};
		return new Matrix(m);
	}
	private Matrix getYWrotationMatrix(double angle)
	{
		double[][] m = {
				{1,0,0,0},
				{0,Math.cos(angolo),0,-Math.sin(angolo)},
				{0,0,1,0},
				{0,Math.sin(angolo),0,Math.cos(angolo)}

			};
		return new Matrix(m);
	}
	private Matrix getYZrotationMatrix(double angle)
	{
		double[][] m = {
				{1,0,0,0},
				{0,Math.cos(angolo),-Math.sin(angolo),0},
				{0,Math.sin(angolo),Math.cos(angolo),0},
				{0,0,0,1}
			};
		return new Matrix(m);
	}
	
	private int rotMatrixIndex = 6, maxRotMatrixIndex = 6;
	private void changeRotationMatrix() {
		rotMatrixIndex++;
		if(rotMatrixIndex>maxRotMatrixIndex)rotMatrixIndex=0;
	}
	private int aspectIndex=2,maxAspectIndex =16;
	private void changeAspect() {
		aspectIndex++;
		if(aspectIndex>maxAspectIndex)aspectIndex=0;
	}
	private Triangle connect(Vertex v1,Vertex v2)
	{
		return new Triangle(v1,v2,v1);
	}
	private int findVertex(ArrayList<Vertex> vertexes,Vector3 pos)
	{
		int i=0;
		for(Vertex v:vertexes)
		{
			if(pos.equals(v.getPosition()))return i;
			i++;
		}
		return -1;
				
	}
	public MainGame_Tesseract(JFrame window,int widthWindow,int heightWindow) throws MatrixException {
		super(window,widthWindow,heightWindow,bgColor);	
		
		objReader = new ObjFileReader();
		plyReader = new PlyFileReader(); 
		
		Rasterizer rasterizer = getRasterizer();
		rasterizer.setUseDirectionalLight(false);
		rasterizer.setLightDirection(new Vector3(0.5f,0.5f,1));
		rasterizer.setUseRenderDistance(false);
		rasterizer.setRenderDistance(20);
		
		camera = getCamera();
		
		String backgroundMusicPath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\audio\\hyperObject.wav";
		// Riproduci background music
	    Audio.getInstance().playMusicLoop(backgroundMusicPath,0.6f);
	    try {
	    	double scaleCubeInside = 1f;
	    	List<Vertex> cubeVertex = new ArrayList<Vertex>();
	    	cubeVertex.add(new Vertex(new Vector3(-1,-1,1).multiply(scaleCubeInside)));
	    	cubeVertex.add(new Vertex(new Vector3(1,-1,1).multiply(scaleCubeInside)));
	    	cubeVertex.add(new Vertex(new Vector3(1,1,1).multiply(scaleCubeInside)));
	    	
	    	cubeVertex.add(new Vertex(new Vector3(-1,1,1).multiply(scaleCubeInside)));
	    	cubeVertex.add(new Vertex(new Vector3(-1,-1,-1).multiply(scaleCubeInside)));
	    	cubeVertex.add(new Vertex(new Vector3(1,-1,-1).multiply(scaleCubeInside)));
	    	cubeVertex.add(new Vertex(new Vector3(1,1,-1).multiply(scaleCubeInside)));
	    	cubeVertex.add(new Vertex(new Vector3(-1,1,-1).multiply(scaleCubeInside)));
	    	
	    	ArrayList<Triangle> tris = new ArrayList<Triangle>();
	    	tris.add(connect(cubeVertex.get(0),cubeVertex.get(1)));
	    	tris.add(connect(cubeVertex.get(1),cubeVertex.get(2)));
	    	tris.add(connect(cubeVertex.get(2),cubeVertex.get(3)));
	    	tris.add(connect(cubeVertex.get(3),cubeVertex.get(0)));
	    	
	    	tris.add(connect(cubeVertex.get(4),cubeVertex.get(5)));
	    	tris.add(connect(cubeVertex.get(4),cubeVertex.get(7)));
	    	tris.add(connect(cubeVertex.get(7),cubeVertex.get(6)));
	    	tris.add(connect(cubeVertex.get(6),cubeVertex.get(5)));
	    	
	    	tris.add(connect(cubeVertex.get(0),cubeVertex.get(4)));
	    	tris.add(connect(cubeVertex.get(3),cubeVertex.get(7)));
	    	tris.add(connect(cubeVertex.get(2),cubeVertex.get(6)));
	    	tris.add(connect(cubeVertex.get(1),cubeVertex.get(5)));
	    	
	    	Mesh mesh = new Mesh(tris);
	    	
	    	double distTraCubi = scaleCubeInside * 2.5f;
	    	tris = mesh.copy().getTriangles();
	    	for(Triangle t:tris)
	    	{
	    		t.setV1(new Vertex(t.getV1().getPosition().multiply(distTraCubi)));
	    		t.setV2(new Vertex(t.getV2().getPosition().multiply(distTraCubi)));
	    		t.setV3(new Vertex(t.getV3().getPosition().multiply(distTraCubi)));
	    		mesh.addTriangle(t);
	    	}
	    	ArrayList<Vertex> vertexes = mesh.getVertexes();
	    	for(int i=0;i<vertexes.size();i++)
	    	{
	    		int k = findVertex(vertexes,
						vertexes.get(i).getPosition().multiply(distTraCubi));
	    		if(k!=-1)
	    			mesh.addTriangle(connect(vertexes.get(i),vertexes.get(k)));
				
	    	}
	    	tesseractVertexes = mesh.getVertexes();
	    	// rimuovi duplicati
	    	Set<Vertex> set = new HashSet<Vertex>(tesseractVertexes);
	    	tesseractVertexes.clear();
	    	tesseractVertexes.addAll(set);
	    	
	    	int n = tesseractVertexes.size();
	    	wVertexes = new double[n];
	    	for(int i=0;i<n;i++)
	    	{
	    		if(Math.abs(tesseractVertexes.get(i).getPosition().x) >scaleCubeInside)
	    			wVertexes[i] = -1 *distTraCubi;
	    		else 
	    			wVertexes[i] = 1*scaleCubeInside;
	    	}
	    		
	    	
			tesseractTransform = new Transform(mesh,new Vector3(0,0,0));
			
			camera.setPosition(new Vector3(6.99, 3.39, -6.95));
			camera.setRotation(new Vector3(0.79, -0.79, 0.00));
//			camera.transla(new Vector3(0,1.5725271418507085,0),Space.World);
//			camera.transla(Vector3.backward().multiply(2.5), Space.Local);
//			camera.transla(Vector3.down().multiply(0.5f), Space.World);
			
			addToDrawList(tesseractTransform);
		} catch (MeshException e) {
			
			e.printStackTrace();
		} catch (MatrixException e) {
			
			e.printStackTrace();
		}
	}
	public static void main(String[] args) 
	{
		try {
			// start game
			new MainGame_Tesseract(new JFrame(), WIDTH,HEIGHT).start();
		}
		catch (MatrixException e){
			e.printStackTrace();
		}
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	double angolo=Math.PI/100;
	private Matrix getTesseractVertexAs4x1matrix(int i)
	{
		Vector3 vPos = tesseractVertexes.get(i).getPosition();
		double w = wVertexes[i];
		double[][] m = {{vPos.x},
				{vPos.y},
				{vPos.z},
				{w}};
		return new Matrix(m);
	}
	private Matrix getRotationMatrix(double angolo)
	{
		switch(rotMatrixIndex)
		{
			case 0:
				return getZWrotationMatrix(angolo);
			case 1:
				return getXYrotationMatrix(angolo);
			case 2:
				return getXZrotationMatrix(angolo);
			case 3:
				return getXWrotationMatrix(angolo);
			case 4:
				return getYZrotationMatrix(angolo);
			case 5:
				return getZWXYrotationMatrix(angolo);
			case 6:
				return getYWrotationMatrix(angolo);
			default:
				return getZWrotationMatrix(angolo);
		}
		
	}
	// Update viene chiamato prima di draw
	@Override
	protected void update() 
	{
		super.update();
		
		try {
			for(int i=0;i<tesseractVertexes.size();i++)
			{
				int index = i;
				double angle = angolo;
				Matrix m1 = getRotationMatrix(angle);
				Matrix m2 = getTesseractVertexAs4x1matrix(index);
				Matrix ris = Matrix.multiply(m1, m2);
				double x = ris.get(0, 0);
				double y =ris.get(1, 0);
				double z = ris.get(2, 0);
				double w = ris.get(3, 0);
				//Vector3 newPos = matrixToVec3(ris);
				Vector3 newPos = new Vector3(x,y,z);
				//System.out.println(wVertexes[index]+" new w"+w);
				wVertexes[index] = w;
				
				//tesseractTransform.getMesh().getVertexes().get(index).setPosition(newPos);
				tesseractVertexes.get(index).setPosition(newPos);
				//System.out.println(newPos);
			}
		} catch (MatrixException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	protected void drawTriangle(Graphics2D g, DrawableTriangle tri) {
		switch(aspectIndex)
		{
			case 0:
				getRasterizer().setUseDirectionalLight(false);
				tri.drawOutlineTriangle(g, Color.black);
			break;
			case 1:
				getRasterizer().setUseDirectionalLight(false);
				tri.drawOutlineTriangle(g, Color.white);
			break;
				case 2:
				getRasterizer().setUseDirectionalLight(false);
				tri.drawOutlineTriangle(g, Color.green);
			break;
				case 3:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g, Color.red);
			break;
				case 4:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g, Color.blue);
			break;
				case 5:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g, Color.yellow);
					
			break;
				case 6:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g, Color.orange);
			break;
				case 7:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g, Color.PINK);
			break;
				case 8:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g, Color.orange);
			break;
				case 9:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g,new Color(241, 0, 195));
			break;
				case 10:
					getRasterizer().setUseDirectionalLight(false);
					
					tri.drawOutlineTriangle(g,new Color(142, 23, 60));
			break;
				case 11:
					getRasterizer().setUseDirectionalLight(false);
					
					//tri.fillTriangle(g,new Color(117, 41, 66));
					tri.drawOutlineTriangle(g,new Color(28, 3, 21,255/2));
					//tri.drawOutlineTriangle(g,new Color(241, 0, 195));
			break;
				case 12:
					getRasterizer().setUseDirectionalLight(false);
					
					//tri.fillTriangle(g,new Color(117, 41, 66));
					tri.drawOutlineTriangle(g,Color.gray);
					//tri.drawOutlineTriangle(g,new Color(241, 0, 195));
			break;
				case 13:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g,new Color(143,45,80));
			break;
				case 14:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g,new Color(18,19,27,255/2));
			break;
				case 15:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g,new Color(77,68,85,255/2));
			break;
				case 16:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g,Color.cyan);
			break;
			default:
				getRasterizer().setUseDirectionalLight(false);
				tri.fillTriangle(g, Color.black);
				tri.drawOutlineTriangle(g, Color.black);
			break;
		}
		
	}
	@Override
	protected void draw(Graphics2D g) 
	{
		super.draw(g);
		
		g.setColor(Color.white);
		g.drawString("Tesseract (HyperCube)",0,20); 
		g.drawString("Press H to switch aspect",0,40);
		g.drawString("Explore using WASD-RF-1234",0,60);
		g.drawString("Rotation matrix ("+rotMatrixIndex+") press 0 to switch",0,80);
	}
	@Override
	protected void keyTyped(KeyEvent e) {
		try {
			float speed = 10;
			if(e.getKeyChar() == 'w')
			{
				camera.transla(Vector3.forward().multiply(deltaTime*speed), Space.Local);
			}
			else if(e.getKeyChar() == 's')
			{
				camera.transla(Vector3.backward().multiply(deltaTime*speed), Space.Local);
			}
			if(e.getKeyChar() == 'a')
			{
				camera.transla(Vector3.right().multiply(deltaTime*speed), Space.Local);
			}
			else if(e.getKeyChar() == 'd')
			{
				camera.transla(Vector3.left().multiply(deltaTime*speed), Space.Local);
			}
			if(e.getKeyChar() == 'r')
			{
				camera.transla(Vector3.up().multiply(deltaTime*speed), Space.Local);
			}
			else if(e.getKeyChar() == 'f')
			{
				camera.transla(Vector3.down().multiply(deltaTime*speed), Space.Local);
			}
			else if(e.getKeyChar() == '1')
			{
				double newJaw = camera.getRotation().y-Math.PI/100;
				camera.setRotation(new Vector3(camera.getRotation().x,newJaw,camera.getRotation().z));
			}
			else if(e.getKeyChar() == '2')
			{
				double newJaw = camera.getRotation().y+Math.PI/100;
				camera.setRotation(new Vector3(camera.getRotation().x,newJaw,camera.getRotation().z));
			}
			
			else if(e.getKeyChar() == '3')
			{
				double newPitch = camera.getRotation().x-Math.PI/100;
				camera.setRotation(new Vector3(newPitch,camera.getRotation().y,camera.getRotation().z));
			}
			else if(e.getKeyChar() == '4')
			{
				double newPitch = camera.getRotation().x+Math.PI/100;
				camera.setRotation(new Vector3(newPitch,camera.getRotation().y,camera.getRotation().z));
			}
			else if(e.getKeyChar() == 'i')
			{
				System.out.println("Camera info: "+camera);
				System.out.println("HyperCube info: "+tesseractTransform);
			}
			else if(e.getKeyChar() == '0')
			{
				changeRotationMatrix();
			}
			else if(e.getKeyChar() =='h')
			{
				changeAspect();
			}
		} catch (MatrixException e1) {
			
			e1.printStackTrace();
		}
	}
	@Override
	protected void keyReleased(KeyEvent e) {
		
	}
	
}
