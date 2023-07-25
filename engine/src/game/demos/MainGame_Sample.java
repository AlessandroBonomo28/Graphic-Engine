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
import game.Audio;
import game.GameEngine;
import game.Hill;
import geometry.*;
import geometry.GeometryException.MeshException;
import mathUtils.*;
import mathUtils.matrixExceptions.*;

public class MainGame_Sample extends GameEngine
{
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1920/2;
	private static final int HEIGHT = 1080/2;
	private static Color bgColor = Color.black;//new Color(33, 0, 79);
	
	ObjFileReader objReader;
	PlyFileReader plyReader;
	
	public Transform camera;
	public Transform shape;
	
	
	
	private int aspectIndex=0,maxAspectIndex =16;
	private void changeAspect() {
		aspectIndex++;
		if(aspectIndex>maxAspectIndex)aspectIndex=0;
	}
	public MainGame_Sample(JFrame window,int widthWindow,int heightWindow) throws MatrixException {
		super(window,widthWindow,heightWindow,bgColor);	
		
		objReader = new ObjFileReader();
		plyReader = new PlyFileReader(); 
		
		Rasterizer rasterizer = getRasterizer();
		rasterizer.setUseDirectionalLight(true);
		rasterizer.setLightDirection(new Vector3(0.5f,0.5f,1));
		rasterizer.setUseRenderDistance(false);
		rasterizer.setRenderDistance(20);
		
		camera = getCamera();
		
		String shapeFilePath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\heart.obj";
		
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
			
			getRasterizer().setLightDirection(new Vector3(0,1,0).normalized());
				
			
			
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
			new MainGame_Sample(new JFrame(), WIDTH,HEIGHT).start();
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
	
	
	double angolo=0;
	
	
	// Update viene chiamato prima di draw
	@Override
	protected void update() 
	{
		super.update();
		
		try {
			shape.setRotation(new Vector3(0 ,angolo,0));
		} catch (MatrixException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		angolo += Math.PI / 200;
		
		

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
		g.drawString("Simple engine demo",0,20); 
		g.drawString("Position: "+camera.getPosition(),0,40); 
		g.drawString("Look euler angles (X,Y,Z): "+camera.getRotation().multiply(180/Math.PI)+" °",0,60); 
		g.drawString("Move (WASDRF), Look (1234): ",0,80); 
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
