package game.demos;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.sound.sampled.Clip;
import javax.swing.JFrame;

import Graphics.DrawableTriangle;
import Graphics.Rasterizer;
import fileReaders3D.ObjFileReader;
import fileReaders3D.PlyFileReader;
import fileReaders3D.FileReaderExceptions.FileReaderException;
import game.GameEngine;
import game.Hill;
import geometry.*;
import geometry.GeometryException.MeshException;
import geometry.TransformException.TransformException;
import mathUtils.*;
import mathUtils.matrixExceptions.*;

public class MainGame_Planet extends GameEngine
{
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1920/2;
	private static final int HEIGHT = 1080/2;
	private static Color bgColor = new Color(33, 0, 79);
	
	ObjFileReader objReader;
	PlyFileReader plyReader;
	
	public Transform camera;
	public Transform terrain;
	public Transform sea;
	public Transform sun;
	
	public ArrayList<Vertex> terrainVertexes;
	public ArrayList<Hill> terrainHills = new ArrayList<Hill>();
	public ArrayList<Vertex> seaVertexes;
	public ArrayList<Hill> seaHills = new ArrayList<Hill>();
	Vector3 sphereCenter = new Vector3();
	
	private int planetIndex=0;
	private int maxPlanetIndex=1;
	private void changePlanetToEdit() {
		planetIndex++;
		if(planetIndex>maxPlanetIndex)planetIndex=0;
	}
	private int aspectIndex=0,maxAspectIndex =15;
	private void changeAspect() {
		aspectIndex++;
		if(aspectIndex>maxAspectIndex)aspectIndex=0;
	}
	public MainGame_Planet(JFrame window,int widthWindow,int heightWindow) throws MatrixException {
		super(window,widthWindow,heightWindow,bgColor);	
		
		objReader = new ObjFileReader();
		plyReader = new PlyFileReader(); 
		
		Rasterizer rasterizer = getRasterizer();
		rasterizer.setUseDirectionalLight(true);
		rasterizer.setLightDirection(new Vector3(1,1,-1));
		rasterizer.setUseRenderDistance(false);
		rasterizer.setRenderDistance(20);
		
		camera = getCamera();
		
		String sunFilePath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\sun.ply";
		String sphereFilePath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\sphere.obj";
		String sphereHighPolyFilePath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\sphereDetail.obj";
	    String backgroundMusicPath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\audio\\vaporwaveBg.wav";
		// Riproduci background music
	    //Audio.getInstance().playMusicLoop(backgroundMusicPath,0.6f);
	    try {
			Vector3 posSun = new Vector3(0,1,35);
	    	sun = new Transform(plyReader.readMeshFromFile(sunFilePath),posSun);
	    	
			sea = new Transform(objReader.readMeshFromFile(sphereHighPolyFilePath),new Vector3(0,0,5)); 
			sea.getMesh().setColor(new Color(0,102,255,255/2));
			terrain = new Transform(objReader.readMeshFromFile(sphereFilePath),new Vector3(0,0,5)); 
			terrain.getMesh().setColor(new Color(82,163,23));
			
			camera.setRotation(new Vector3(0.7853981633974486,0,0));
			camera.transla(new Vector3(0,1.5725271418507085,0),Space.World);
			camera.transla(Vector3.backward().multiply(2.5), Space.Local);
			
			camera.transla(Vector3.down().multiply(0.5f), Space.World);
			
			addToDrawList(sea);
			addToDrawList(terrain);
			//addToDrawList(sun);
			
			try {
				sun.setParent(sea);
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			terrainVertexes = terrain.getMesh().getVertexes();
			seaVertexes = sea.getMesh().getVertexes();
			sphereCenter = sea.getMesh().center().copy();
			ArrayList<Vertex> vAdded = new ArrayList<Vertex>();
			for(Vertex v:seaVertexes)
			{
				if(vAdded.contains(v))continue;
				seaHills.add(new Hill(sea.getMesh().getVertexesConnectedTo(v)));
				vAdded.add(v);
			}
			vAdded = new ArrayList<Vertex>();
			for(Vertex v:terrainVertexes)
			{
				if(vAdded.contains(v))continue;
				terrainHills.add(new Hill(terrain.getMesh().getVertexesConnectedTo(v)));
				vAdded.add(v);
			}	
			
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (MeshException e) {
			
			e.printStackTrace();
		} catch (MatrixException e) {
			
			e.printStackTrace();
		} catch (FileReaderException e) {
			
			e.printStackTrace();
		}
	}
	public static void main(String[] args) 
	{
		try {
			// start game
			new MainGame_Planet(new JFrame(), WIDTH,HEIGHT).start();
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
	
	
	
	
	double zOff = 0;
	double angolo=0;
	double speedTerrain = 0.01f;
	
	
	Planet planetTerrain = new Planet("Terrain",5, 2, 3);
	Planet planetSea = new Planet("Sea",3, 2, 3);
	
	Vector3 lightDir = new Vector3(0.3,0.7,0.2);
	
	// Update viene chiamato prima di draw
	@Override
	protected void update() 
	{
		super.update();
		for(Hill hill:seaHills)
		{
			Vertex v = hill.getStartValue();
			double x = v.getPosition().x;
			double y = v.getPosition().y;
			double z = v.getPosition().z;
			
			double xOff = zOff;
			double yOff = zOff;
			
			double scaleNoise = planetSea.getHillsRate();
			double maxH = planetSea.getHillsHeight();
			
			Vector3 dirEspansione = v.getPosition().sub(sphereCenter).normalized();
			//double h = PerlinNoise.noise(x*scaleNoise+xOff,y*scaleNoise+yOff,z*scaleNoise+zOff)*maxH;
			//double h = PerlinNoise.noise(z*scaleNoise+zOff,0,0)*maxH;//wave
			double h = PerlinNoise.noise(x*scaleNoise+xOff,y*scaleNoise+yOff,z*scaleNoise+0)*maxH;
			for(Vertex i:hill.getVerticiHill())
				i.setPosition(sphereCenter.sum(dirEspansione.multiply(h+planetSea.getRadius())));
		}
		
		for(Hill hill:terrainHills)
		{
			Vertex v = hill.getStartValue();
			double x = v.getPosition().x;
			double y = v.getPosition().y;
			double z = v.getPosition().z;
			
			double xOff = zOff;
			double yOff = zOff;
			
			
			double scaleNoise = planetTerrain.getHillsRate();
			double maxH = planetTerrain.getHillsHeight();
			
			Vector3 dirEspansione = v.getPosition().sub(sphereCenter).normalized();
			//double h = PerlinNoise.noise(x*scaleNoise+xOff,y*scaleNoise+yOff,z*scaleNoise+zOff)*maxH;
			//double h = PerlinNoise.noise(z*scaleNoise+zOff,0,0)*maxH;//wave
			double h = PerlinNoise.noise(x*scaleNoise+0,y*scaleNoise+0,z*scaleNoise+0)*maxH;
			for(Vertex i:hill.getVerticiHill())
				i.setPosition(sphereCenter.sum(dirEspansione.multiply(h+planetTerrain.getRadius())));
		}
		
		// incrementa offset z noise
		if(zOff>=Double.MAX_VALUE)zOff =0;
		else zOff+=speedTerrain/1.5f;
		try {
			//ruota sole
			angolo+=Math.PI/1000;
			if(angolo>=Math.PI*2)angolo=0;
			
			sun.setRotation(new Vector3(0,0,angolo));
			//sea.setRotation(new Vector3(0,angolo*2,0));
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
				getRasterizer().setUseDirectionalLight(true);
				tri.fillTriangle(g, Color.black);
				tri.drawOutlineTriangle(g, Color.black);
			break;
			case 1:
				getRasterizer().setUseDirectionalLight(false);
				tri.fillTriangle(g, Color.black);
				tri.drawOutlineTriangle(g, Color.white);
			break;
				case 2:
				getRasterizer().setUseDirectionalLight(false);
				tri.fillTriangle(g, Color.black);
				tri.drawOutlineTriangle(g, Color.green);
			break;
				case 3:
					getRasterizer().setUseDirectionalLight(false);
					tri.drawOutlineTriangle(g, Color.green);
			break;
				case 4:
					getRasterizer().setUseDirectionalLight(true);
					tri.fillTriangleByMajorColor(g);
					//Color outline = new Color(241, 248, 213);
					//outline = new Color(24, 173, 191);
					//Color c2 = new Color(24, 48, 136);
					tri.drawOutlineTriangle(g, Color.black);
			break;
				case 5:
					getRasterizer().setUseDirectionalLight(true);
					tri.fillTriangleByMajorColor(g);
					//Color outline = new Color(241, 248, 213);
					//outline = new Color(24, 173, 191);
					//Color c2 = new Color(24, 48, 136);
					//tri.drawOutlineTriangle(g, Color.black);
			break;
				case 6:
					getRasterizer().setUseDirectionalLight(true);
					tri.fillTriangle(g, Color.black);
			break;
				case 7:
					getRasterizer().setUseDirectionalLight(true);
					
					tri.fillTriangle(g, Color.red);
					tri.drawOutlineTriangle(g, Color.black);
			break;
				case 8:
					getRasterizer().setUseDirectionalLight(true);
					
					tri.fillTriangle(g, Color.green);
					tri.drawOutlineTriangle(g, Color.black);
			break;
				case 9:
					getRasterizer().setUseDirectionalLight(false);
					
					//tri.fillTriangle(g,new Color(117, 41, 66));
					tri.fillTriangle(g, bgColor);
					tri.drawOutlineTriangle(g,new Color(241, 0, 195));
			break;
				case 10:
					getRasterizer().setUseDirectionalLight(false);
					tri.fillTriangle(g, new Color(241, 0, 195,255/2));
					tri.drawOutlineTriangle(g,Color.black);
			break;
				case 11:
					getRasterizer().setUseDirectionalLight(true);
					
					//tri.fillTriangle(g,new Color(117, 41, 66));
					tri.fillTriangle(g, bgColor);
					//tri.drawOutlineTriangle(g,new Color(241, 0, 195));
			break;
				case 12:
					getRasterizer().setUseDirectionalLight(false);
					
					//tri.fillTriangle(g,new Color(117, 41, 66));
					tri.fillTriangle(g, new Color(231, 178, 118,255/2));
					tri.drawOutlineTriangle(g,Color.black);
					//tri.drawOutlineTriangle(g,new Color(241, 0, 195));
			break;
				case 13:
					getRasterizer().setUseDirectionalLight(true);
					
					tri.fillTriangle(g,new Color(143,45,80));
					//tri.drawOutlineTriangle(g,new Color(18,19,27));
			break;
				case 14:
					getRasterizer().setUseDirectionalLight(true);
					
					tri.fillTriangle(g,new Color(49,166,158,255/4));
					//tri.fillTriangle(g, Color.black);
					tri.drawOutlineTriangle(g,new Color(18,19,27,255/2));
			break;
				case 15:
					getRasterizer().setUseDirectionalLight(true);
					
					tri.fillTriangle(g,new Color(96,65,153));
					//tri.fillTriangle(g, Color.black);
					tri.drawOutlineTriangle(g,new Color(77,68,85,255/2));
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
		
		if(planetIndex == 0) 
		{
			g.drawString("Procedural planet: "+planetSea.getName(),0,20); 
			g.drawString("Max hill heigth: "+String.format("%.2f", planetSea.getHillsHeight())+ " (edit 7-8)",0,40); 
			g.drawString("Radius: "+String.format("%.2f", planetSea.getRadius()) + " (edit 5-6)",0,60); 
			g.drawString("Hill rate: "+String.format("%.2f", planetSea.getHillsRate())+ " (edit M-K)",0,80);  
		}
		else 
		{
			g.drawString("Procedural planet: "+planetTerrain.getName(),0,20); 
			g.drawString("Max hill heigth: "+String.format("%.2f", planetTerrain.getHillsHeight())+ " (edit 7-8)",0,40); 
			g.drawString("Radius: "+String.format("%.2f", planetTerrain.getRadius()) + " (edit 5-6)",0,60); 
			g.drawString("Hill rate: "+String.format("%.2f", planetTerrain.getHillsRate())+ " (edit M-K)",0,80); 
		}
		g.drawString("Speed up/down: "+String.format("%.3f", speedTerrain)+" (edit O-P)",0,100); 
	}
	@Override
	protected void keyTyped(KeyEvent e) {
		try {
			float speed = 20;
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
				System.out.println("terrain info: "+sea);
			}
			else if(e.getKeyChar() == '5')
			{
				if(planetIndex==0)
					planetSea.setRadius(planetSea.getRadius()-0.1f);
				else
					planetTerrain.setRadius(planetTerrain.getRadius()-0.1f);
			}
			else if(e.getKeyChar() == '6')
			{
				
				if(planetIndex==0)
					planetSea.setRadius(planetSea.getRadius()+0.1f);
				else
					planetTerrain.setRadius(planetTerrain.getRadius()+0.1f);
			}
			else if(e.getKeyChar() == '7')
			{
				if(planetIndex==0)
					planetSea.setHillsHeight(planetSea.getHillsHeight()-0.05f);
				else
					planetTerrain.setHillsHeight(planetTerrain.getHillsHeight()-0.05f);
				//maxH -=0.05f;
			}
			else if(e.getKeyChar() == '8')
			{
				if(planetIndex==0)
					planetSea.setHillsHeight(planetSea.getHillsHeight()+0.05f);
				else
					planetTerrain.setHillsHeight(planetTerrain.getHillsHeight()+0.05f);
				//maxH +=0.05f;
			}
			else if(e.getKeyChar() == 'm')
			{
				if(planetIndex==0)
					planetSea.setHillsRate(planetSea.getHillsRate()-0.01f);
				else
					planetTerrain.setHillsRate(planetTerrain.getHillsRate()-0.01f);
			}
			else if(e.getKeyChar() == 'k')
			{
				if(planetIndex==0)
					planetSea.setHillsRate(planetSea.getHillsRate()+0.01f);
				else
					planetTerrain.setHillsRate(planetTerrain.getHillsRate()+0.01f);
			}
			else if(e.getKeyChar() == 'p')
			{
				speedTerrain /=1.5f;
			}
			else if(e.getKeyChar() == 'o')
			{
				speedTerrain *=1.5f;
				if(speedTerrain>=0.05f)speedTerrain=0.05f;
				
			}
			else if(e.getKeyChar() =='h')
			{
				changeAspect();
			}
			else if(e.getKeyChar() =='0')
			{
				changePlanetToEdit();
			}
		} catch (MatrixException e1) {
			
			e1.printStackTrace();
		}
	}
	@Override
	protected void keyReleased(KeyEvent e) {
		
	}
	private double clamp(double val, double min, double max) {
	    return Math.max(min, Math.min(max, val));
	}
	
}
