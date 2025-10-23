package game.demos;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.swing.JFrame;

import Graphics.DrawableTriangle;
import Graphics.Rasterizer;
import fileReaders3D.ObjFileReader;
import fileReaders3D.PlyFileReader;
import game.GameEngine;
import geometry.*;
import geometry.GeometryException.MeshException;
import mathUtils.*;
import mathUtils.matrixExceptions.*;

public class MainGame_Plotter extends GameEngine
{
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1920 / 2;
	private static final int HEIGHT = 1080 / 2;
	private static Color bgColor = new Color(33, 0, 79);
	
	ObjFileReader objReader;
	PlyFileReader plyReader;
	
	public Transform camera;
	public Transform shape;
	public Mesh meshShape;
	
	
	double lengthRect = 3;
	double widthRect =  3;
	Vertex lastV3,lastV4;
	Vector3 dirExtrude = Vector3.forward();
	Vector3 upExtrude = Vector3.up();
	double speedExtrude=30;
	
	private int aspectIndex=0,maxAspectIndex =16;
	private void changeAspect() {
		aspectIndex++;
		if(aspectIndex>maxAspectIndex)aspectIndex=0;
	}
	
	
	
	public MainGame_Plotter(JFrame window,int widthWindow,int heightWindow) throws MatrixException {
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
	    	
	    	
	    	Vector3 vec1 = Vector3.cross(dirExtrude, upExtrude).multiply(widthRect);
	    	Vertex v1 = new Vertex(vec1);
	    	Vertex v2 = new Vertex(vec1.multiply(-1));
	    	
	    	double lengthRect=0;
	    	Vector3 vec3 = vec1.sum(dirExtrude.multiply(lengthRect));
	    	Vector3 vec4 = Vector3.cross(upExtrude, dirExtrude).multiply(widthRect)
	    			.sum(dirExtrude.multiply(lengthRect));
	    	
	    	Vertex v3 = new Vertex(vec3);
	    	
	    	Vertex v4 = new Vertex(vec4);
	    	
	    	lastV3 = v3.copy();
	    	lastV4 = v4.copy();
	    	
	    	meshShape = new Mesh(new Triangle(v1,v2,v3),
	    			new Triangle(v2.copy(),lastV3,lastV4));
	    	shape = new Transform(meshShape);
	    	
	    	double offsetInizialeZshape = 5;
			shape.transla(Vector3.forward().multiply(offsetInizialeZshape), Space.World);
			
			camera.setRotation(new Vector3(0.95, -0.88, 0.00));
			camera.transla(new Vector3(81.85, 30.62, -59.33),Space.World);
			
			addToDrawList(shape);
				
			
			
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
			new MainGame_Plotter(new JFrame(), WIDTH,HEIGHT).start();
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
	int indexPrimo=0;
	ArrayList<Integer> primi = (ArrayList<Integer>) numeriPrimiFinoA(50);
	Vector3 lightDir = new Vector3(0.3,0.7,0.2);
	
	ArrayList<Color> colorsOverTime = (ArrayList<Color>) 
			Stream.of(new Color(255,0,0),new Color(255,127,0),
					new Color(255,255,0),new Color(0,255,0),
					new Color(0,0,255),new Color(75,0,130),
					new Color(148,0,211)).collect(Collectors.toList());
	int colorIndex = 0;
	double tInterpColor=0;
	double timeSwitchColor = 1;
	
	
	double distTravelledExtrusion = 0;
	double step=14;
	double stepLenRect=0.05f;
	int i=1;
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
		
		if(distTravelledExtrusion==0)
		{
			Vector2 v = Vector2.polarToCartesian(i, i*Math.PI/180);
			dirExtrude = new Vector3(v.x,0,v.y).normalized();
			//dirExtrude = new Vector3(v.x,0,v.y).normalized().sum(Vector3.up().multiply(0.05f));
			i += step;
			lengthRect += stepLenRect;
			if(lengthRect >= 5)
			{
				lengthRect = 1;
				i=1;
			}
	    	Vertex v1 = lastV3.copy();
	    	Vertex v2 = lastV4.copy();
	    	Vector3 vec1 = v1.getPosition();
	    	Vector3 vec3 = vec1.copy();
	    	Vector3 vec4 = v2.getPosition();
	    	
	    	Vertex v3 = new Vertex(vec3);
	    	
	    	Vertex v4 = new Vertex(vec4);
	    	
	    	lastV3 = v3;
	    	lastV4 = v4;
	    	
	    	Triangle t1 = new Triangle(v1,v2,v3);
	    	Triangle t2 = new Triangle(v2.copy(),lastV3,lastV4);
	    	meshShape.addTriangle(t1);
	    	meshShape.addTriangle(t2);
	    	
	    	lastV3 = v3;
	    	lastV4 = v4;
	    	distTravelledExtrusion+=deltaTime*speedExtrude;
		}
		else if(distTravelledExtrusion<lengthRect)
		{
			lastV3.transla(dirExtrude.multiply(deltaTime*speedExtrude));
			lastV4.transla(dirExtrude.multiply(deltaTime*speedExtrude));
			distTravelledExtrusion+=deltaTime*speedExtrude;
		}
		else 
		{	
			distTravelledExtrusion=0;
		}
		// effetto cambio colore
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
		
		try {
			angolo+=Math.PI/1000;
			if(angolo>=Math.PI*2)angolo=0;
			
			//shape.setRotation(new Vector3(angolo,angolo,angolo));
			//shape.setRotation(new Vector3(angolo,angolo,0));
			shape.setRotation(Vector3.zero());
		} catch (MatrixException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Triangle> meshTriangles = meshShape.getTriangles();
		if(meshTriangles.size()>450)
		{
			meshTriangles.remove(0);
			meshTriangles.remove(0);
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
		g.drawString("3D plotter",0,20); 
		g.drawString("Step i: "+String.format("%.2f", step)+ " (edit 5-6)",0,40); 
		g.drawString("Step length rect: "+String.format("%.2f", stepLenRect)+ " (edit 7-8)",0,60); 
		g.drawString("Speed up/down: "+String.format("%.3f", speedExtrude)+" (edit O-P)",0,80);
	}
	@Override
	protected void keyTyped(KeyEvent e) {
		try {
			float speed = 25*4;
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
			else if(e.getKeyChar() == '5')
			{
				step -=0.05f;
				if(step<0.05f)step= 0.05f;
			}
			else if(e.getKeyChar() == '6')
			{
				step +=0.05f;
			}
			else if(e.getKeyChar() == '7')
			{
				stepLenRect -=0.05f;
				if(stepLenRect<0.05f)stepLenRect= 0.05f;
			}
			else if(e.getKeyChar() == '8')
			{
				stepLenRect +=0.05f;
			}
			else if(e.getKeyChar() == 'p')
			{
				speedExtrude /=1.5f;
			}
			else if(e.getKeyChar() == 'o')
			{
				speedExtrude *=1.5f;
				if(speedExtrude>=40)speedExtrude=40;
				
			}
		} catch (MatrixException e1) {
			
			e1.printStackTrace();
		}
	}
	@Override
	protected void keyReleased(KeyEvent e) {
		
	}
	public List<Integer> numeriPrimiFinoA(int n) {
	    return IntStream.rangeClosed(2, n)
	      .filter(x -> isPrimo(x)).boxed()
	      .collect(Collectors.toList());
	}
	private boolean isPrimo(int numero) {
		return IntStream.rangeClosed(2, (int) (Math.sqrt(numero)))
	      .allMatch(n -> numero % n != 0);
	}
	
}
