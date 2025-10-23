package game.demos;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import mathUtils.*;
import mathUtils.matrixExceptions.*;

public class MainGame_Blob extends GameEngine
{
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1920/2;
	private static final int HEIGHT = 1080/2;
	private static Color bgColor = new Color(33, 0, 79);
	
	ObjFileReader objReader;
	PlyFileReader plyReader;
	
	public Transform camera;
	public Transform shape;
	
	public ArrayList<Vertex> terrainVertexes;
	public ArrayList<Hill> hills = new ArrayList<Hill>();
	Vector3 sphereCenter = new Vector3();
	
	private int noiseTypeIndex=0,maxNoiseTypeIndex =5;
	private void changeNoise() {
		noiseTypeIndex++;
		if(noiseTypeIndex>maxNoiseTypeIndex)noiseTypeIndex=0;
	}
	
	private int aspectIndex=0,maxAspectIndex =16;
	private void changeAspect() {
		aspectIndex++;
		if(aspectIndex>maxAspectIndex)aspectIndex=0;
	}
	public MainGame_Blob(JFrame window,int widthWindow,int heightWindow) throws MatrixException {
		super(window,widthWindow,heightWindow,bgColor);	
		
		objReader = new ObjFileReader();
		plyReader = new PlyFileReader(); 
		
		Rasterizer rasterizer = getRasterizer();
		rasterizer.setUseDirectionalLight(true);
		rasterizer.setLightDirection(new Vector3(0.5f,0.5f,1));
		rasterizer.setUseRenderDistance(false);
		rasterizer.setRenderDistance(20);
		
		camera = getCamera();
		
		String shapeFilePath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\heart2.obj";
		
	    String backgroundMusicPath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\audio\\vaporwaveBg.wav";
		// Riproduci background music
	    //Audio.getInstance().playMusicLoop(backgroundMusicPath,0.6f);
	    try {
	    	
			shape = new Transform(objReader.readMeshFromFile(shapeFilePath),new Vector3(0,0,0)); 
			shape.transla(Vector3.forward().multiply(5), Space.World);
			
			camera.setRotation(new Vector3(0.7853981633974486,0,0));
			camera.transla(new Vector3(0,1.5725271418507085,0),Space.World);
			camera.transla(Vector3.backward().multiply(2.5), Space.Local);
			camera.transla(Vector3.down().multiply(0.5f), Space.World);
			
			addToDrawList(shape);
			
			terrainVertexes = shape.getMesh().getVertexes();
			sphereCenter = shape.getMesh().center().copy();
			ArrayList<Vertex> vAdded = new ArrayList<Vertex>();
			for(Vertex v:terrainVertexes)
			{
				if(vAdded.contains(v))continue;
				hills.add(new Hill(shape.getMesh().getVertexesConnectedTo(v)));
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
			new MainGame_Blob(new JFrame(), WIDTH,HEIGHT).start();
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
	
	double scaleNoise = 2;
	double maxH = 3f;
	Vector3 lightDir = new Vector3(0.3,0.7,0.2);
	
	ArrayList<Color> colorsOverTime = (ArrayList<Color>) 
			Stream.of(new Color(255,0,0),new Color(255,127,0),
					new Color(255,255,0),new Color(0,255,0),
					new Color(0,0,255),new Color(75,0,130),
					new Color(148,0,211)).collect(Collectors.toList());
	int colorIndex = 0;
	double tInterpColor=0;
	double timeSwitchColor = 1;
	
	// Update viene chiamato prima di draw
	@Override
	protected void update() 
	{
		super.update();
		// cambia l'angolazione della luce
//		try {
//			lightDir = Transformation.ruotaSuAsseXYZ(lightDir, Vector3.zero(),
//					new Vector3(angolo/1000,angolo/1000,-angolo/1000)).normalized();
//			getRasterizer().setLightDirection(lightDir);
//		} catch (MatrixException e1) {
//			e1.printStackTrace();
//		}
		if((aspectIndex == 4 || aspectIndex == 5 ||
				aspectIndex == 16)&& colorsOverTime.size()>1) // se è impostato l'aspetto che mostra il colore della mesh
		{
			Color c = colorsOverTime.get(colorIndex);
			Vector3 c1 = new Vector3(c.getRed(),c.getGreen(),c.getBlue());
			
			if(colorIndex == colorsOverTime.size()-1) c = colorsOverTime.get(0);
			else c = colorsOverTime.get(colorIndex+1);
			
			Vector3 c2 = new Vector3(c.getRed(),c.getGreen(),c.getBlue());
			
			Vector3 interp = Interpolation.interpolaVector3(tInterpColor, c1,c2);
			shape.getMesh().setColor(new Color((int)interp.x,(int)interp.y,(int)interp.z));
			tInterpColor += deltaTime * (1/timeSwitchColor);
			
			if(tInterpColor>=1)
			{
				tInterpColor=0;
				colorIndex++;
				if(colorIndex>=colorsOverTime.size())colorIndex=0;
			}
		}
		
		for(Hill hill:hills)
		{
			Vertex v = hill.getStartValue();
			double x = v.getPosition().x;
			double y = v.getPosition().y;
			double z = v.getPosition().z;
			
			double xOff = zOff;
			double yOff = zOff;
			
			double scaleIncrement = 0;
			double h;
			switch(noiseTypeIndex)
			{
				case 0:
					h=-scaleIncrement; // no noise
					break;
				case 1:
					h = PerlinNoise.noise(x*scaleNoise+xOff,0,0)*maxH;// 1D noise x
					break;
				case 2:
					h = PerlinNoise.noise(0,y*scaleNoise+xOff,0)*maxH;// 1D noise y
					break;
				case 3:
					h = PerlinNoise.noise(0,0,z*scaleNoise+zOff)*maxH;// 1D noise z
					break;
				case 4:
					h = PerlinNoise.noise(x*scaleNoise+xOff,y*scaleNoise+yOff,z*scaleNoise+zOff)*maxH;
					break;
				case 5:
					h = PerlinNoise.noise(x*scaleNoise+xOff,0,z*scaleNoise+zOff)*maxH;
					break;
				case 6:
					h = PerlinNoise.noise(x*scaleNoise+xOff,y*scaleNoise+yOff,0)*maxH;
					break;
				default:
					h=1;
					System.out.println("Noise index inatteso!");
					break;
			}
			Vector3 dirEspansione = v.getPosition().sub(sphereCenter).normalized();
//			for(Vertex i:hill.getVerticiHill())
//				i.setPosition(sphereCenter.sum(dirEspansione.multiply(h+raggio)));
			for(Vertex i:hill.getVerticiHill())
				i.setPosition(v.getPosition().sum(dirEspansione.multiply(h+scaleIncrement)));
		}
		// incrementa offset z noise
		if(zOff>=Double.MAX_VALUE)zOff =0;
		else zOff+=speedTerrain/1.5f;
		try {
			angolo+=Math.PI/1000;
			if(angolo>=Math.PI*2)angolo=0;
			
			shape.setRotation(new Vector3(0,angolo*2,0));
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
					
					tri.drawOutlineTriangle(g, Color.black);
			break;
				case 5:
					getRasterizer().setUseDirectionalLight(true);
					tri.fillTriangleByMajorColor(g);
					
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
				case 16:
					getRasterizer().setUseDirectionalLight(false);
					tri.fillTriangleByMajorColor(g);
					tri.drawOutlineTriangle(g,Color.black);
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
		g.drawString("Procedural blob",0,20); 
		if(noiseTypeIndex==0) g.drawString("Noise type: no noise (edit 0)",0,40); 
		else g.drawString("Noise type: "+(noiseTypeIndex+1)+ " (edit 0)",0,40); 
		g.drawString("Max hill heigth: "+String.format("%.2f", maxH)+ " (edit 7-8)",0,60); 
		g.drawString("Scale noise: "+String.format("%.2f", scaleNoise)+ " (edit M-K)",0,80); 
		g.drawString("Speed up/down: "+String.format("%.3f", speedTerrain)+" (edit O-P)",0,100); 
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
				System.out.println("terrain info: "+shape);
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
			else if(e.getKeyChar() =='0')
			{
				changeNoise();
			}
		} catch (MatrixException e1) {
			
			e1.printStackTrace();
		}
	}
	@Override
	protected void keyReleased(KeyEvent e) {
		
	}
	
}
