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
import game.Audio;
import game.GameEngine;
import game.Hill;
import geometry.*;
import geometry.GeometryException.MeshException;
import geometry.TransformException.TransformException;
import mathUtils.*;
import mathUtils.matrixExceptions.*;

public class MainGame_VaporWave extends GameEngine
{
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = (int) ((int)1920/1.5f);
	private static final int HEIGHT = (int) ((int)1080/1.5f);
	private static Color bgColor = new Color(33, 0, 79);
	
	ObjFileReader objReader;
	PlyFileReader plyReader;
	
	public Transform camera;
	public Transform terrain;
	public Transform sun;
	
	public ArrayList<Vertex> terrainVertexes;
	public ArrayList<Hill> hills = new ArrayList<Hill>();
	
	private int aspectIndex=0,maxAspectIndex =15;
	private void changeAspect() {
		aspectIndex++;
		if(aspectIndex>maxAspectIndex)aspectIndex=0;
	}
	public MainGame_VaporWave(JFrame window,int widthWindow,int heightWindow) throws MatrixException {
		super(window,widthWindow,heightWindow,bgColor);	
		
		objReader = new ObjFileReader();
		plyReader = new PlyFileReader(); 
		
		Rasterizer rasterizer = getRasterizer();
		rasterizer.setUseDirectionalLight(true);
		rasterizer.setLightDirection(Vector3.one());
		rasterizer.setUseRenderDistance(false);
		rasterizer.setRenderDistance(20);
		
		camera = getCamera();
		// 3d files path
		String sunFilePath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\sun.ply";
		String terrainPath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\terrainVaporwave.ply";
		
	    String backgroundMusicPath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\audio\\vaporwaveBg.wav";
		// Riproduci background music
	    Audio.getInstance().playMusicLoop(backgroundMusicPath,0.6f);
	    try {
			Vector3 posSun = new Vector3(0,1,35);
	    	sun = new Transform(plyReader.readMeshFromFile(sunFilePath),posSun);
	    	
			terrain = new Transform(plyReader.readMeshFromFile(terrainPath),new Vector3(0,0,0)); 
			terrain.transla(Vector3.forward().multiply(15), Space.World);
			
			camera.setRotation(new Vector3(0.7853981633974486,0,0));
			camera.transla(new Vector3(0,1.5725271418507085,0),Space.World);
			camera.transla(Vector3.backward().multiply(2.5), Space.Local);
			
			camera.transla(Vector3.down().multiply(0.5f), Space.World);
			
			addToDrawList(terrain);
			addToDrawList(sun);
			
			try {
				sun.setParent(terrain);
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			terrainVertexes = terrain.getMesh().getVertexes();
			ArrayList<Vertex> vAdded = new ArrayList<Vertex>();
			for(Vertex v:terrainVertexes)
			{
				if(vAdded.contains(v)) // non aggiungere i duplicati
					continue;
				hills.add(new Hill(terrain.getMesh().getVertexesConnectedTo(v)));
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
			new MainGame_VaporWave(new JFrame(), WIDTH,HEIGHT).start();
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
	
	double larghezzaStrada = 5 / 2;
	double scaleNoise = 2;
	double maxH = 3f;
	Vector3 lightDir = new Vector3(0.3,0.7,0.2);
	// Update viene chiamato prima di draw
	@Override
	protected void update() 
	{
		super.update();
		// cambia l'angolazione della luce
		try {
			lightDir = Transformation.ruotaSuAsseXYZ(lightDir, Vector3.zero(),
					new Vector3(angolo/1000,angolo/1000,-angolo/1000)).normalized();
			getRasterizer().setLightDirection(lightDir);
		} catch (MatrixException e1) {
			e1.printStackTrace();
		}
		
		for(Hill hill:hills)
		{
			Vertex v = hill.getVerticiHill().get(0);
			double x = v.getPosition().x;
			double y = v.getPosition().z;
			
			double xOff = 0;
			double yOff = zOff;
			
			double h = PerlinNoise.noise(x*scaleNoise+xOff,y*scaleNoise+yOff, 0)*maxH;
			
			if(v.getPosition().x<larghezzaStrada && v.getPosition().x>-larghezzaStrada)
			{
				//h*=0;
				h*= (larghezzaStrada*larghezzaStrada)/((Math.pow(larghezzaStrada, 5))+1);
			}
			for(Vertex i:hill.getVerticiHill())
				i.setPosition(new Vector3(i.getPosition().x,h,i.getPosition().z));
		}
		// incrementa offset z noise
		if(zOff>=Double.MAX_VALUE)zOff =0;
		else zOff+=speedTerrain/1.5f;
		try {
			//ruota sole
			angolo+=Math.PI/1000;
			if(angolo>=Math.PI*2)angolo=0;
			
			sun.setRotation(new Vector3(0,0,angolo));
			
			// muovi terreno avanti e indietro
			if(terrain.getPosition().z <-10 || terrain.getPosition().z >15)
			{
				speedTerrain*=-1;
				terrain.transla(Vector3.backward().multiply(speedTerrain), Space.World);
			}
			terrain.transla(Vector3.backward().multiply(speedTerrain), Space.World);
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
		g.drawString("Procedural vaporwave hills",0,20); 
		g.drawString("Max hill heigth: "+String.format("%.2f", maxH)+ " (edit 7-8)",0,40); 
		g.drawString("Street width: "+String.format("%.2f", larghezzaStrada) + " (edit 5-6)",0,60); 
		g.drawString("Scale noise: "+String.format("%.2f", scaleNoise)+ " (edit M-K)",0,80); 
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
				System.out.println("terrain info: "+terrain);
			}
			else if(e.getKeyChar() == '5')
			{
				larghezzaStrada -=0.5f;
			}
			else if(e.getKeyChar() == '6')
			{
				larghezzaStrada +=0.5f;
				if(larghezzaStrada<0)larghezzaStrada=0;
			}
			else if(e.getKeyChar() == '7')
			{
				maxH -=0.05f;
			}
			else if(e.getKeyChar() == '8')
			{
				maxH +=0.05f;
			}
			else if(e.getKeyChar() == 'm')
			{
				scaleNoise -=0.01f;
				if(scaleNoise<0)scaleNoise=0;
			}
			else if(e.getKeyChar() == 'k')
			{
				scaleNoise +=0.01f;
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
