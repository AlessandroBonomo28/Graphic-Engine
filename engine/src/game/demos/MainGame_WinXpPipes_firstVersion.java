package game.demos;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JFrame;

import Graphics.DrawableTriangle;
import Graphics.Rasterizer;
import fileReaders3D.ObjFileReader;
import fileReaders3D.PlyFileReader;
import fileReaders3D.FileReaderExceptions.FileReaderException;
import game.GameEngine;
import geometry.*;
import geometry.GeometryException.MeshException;
import mathUtils.*;
import mathUtils.matrixExceptions.*;

public class MainGame_WinXpPipes_firstVersion extends GameEngine
{
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1920/2;
	private static final int HEIGHT = 1080/2;
	private static Color bgColor = new Color(33, 0, 79);
	
	ObjFileReader objReader;
	PlyFileReader plyReader;
	
	public Transform camera;
	public Transform shape;
	
	ArrayList<Triangle> faceToExtrude = new ArrayList<Triangle>();
	Vector3 topRightBound = new Vector3(10,10,10);
	Vector3 bottomLeftBound = topRightBound.multiply(-1);
	
	private int aspectIndex=0,maxAspectIndex =16;
	private void changeAspect() {
		aspectIndex++;
		if(aspectIndex>maxAspectIndex)aspectIndex=0;
	}
	public MainGame_WinXpPipes_firstVersion(JFrame window,int widthWindow,int heightWindow) throws MatrixException {
		super(window,widthWindow,heightWindow,bgColor);	
		
		objReader = new ObjFileReader();
		plyReader = new PlyFileReader(); 
		
		Rasterizer rasterizer = getRasterizer();
		rasterizer.setUseDirectionalLight(true);
		rasterizer.setLightDirection(new Vector3(0.5f,0.5f,1));
		rasterizer.setUseRenderDistance(false);
		rasterizer.setRenderDistance(20);
		
		camera = getCamera();
		
		String shapeFilePath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\exagon.obj";
		
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
			
			Mesh mesh = shape.getMesh();
			Random rand = new Random();
			rand.setSeed(System.currentTimeMillis());
			Color meshColor = new Color(randomBetween(rand, 1, 255),
										randomBetween(rand, 1, 255),
										randomBetween(rand, 1, 255));
			
//			int size=mesh.getTriangles().size()-1;
//			for(int i=0;i<size;i++)mesh.getTriangles().remove(0);
			
			double turnScale = 2;
			int turnStep = 2;
			Vector3 dir = new Vector3(0,0,3);
			Vector3 euler = new Vector3(0,0,0);
			// estrudi tutti i triangoli della mesh
			faceToExtrude = extrudeFace(mesh.getTriangles(), mesh, dir, euler);
			Vector2 dirTurn = pickRandomDir();
			
			if(!dirTurn.equals(Vector2.zero()))
				faceToExtrude = turnDir(faceToExtrude,mesh,dir, turnStep, turnScale,dirTurn);
			
			dir = Transformation.ruotaSuAsseXYZ(dir, Vector3.zero(), dirTurnToEuler(dirTurn));
			
			faceToExtrude = extrudeFace(faceToExtrude, mesh, dir, euler);
			
			
//			dir = new Vector3(0,1,3);
//			euler = new Vector3(-Math.PI/2,0,0);
//			faceToExtrude = extrudeFace(faceToExtrude, mesh, dir, euler);
//			dir = new Vector3(0,2,0);
//			euler = Vector3.zero();
//			faceToExtrude = extrudeFace(faceToExtrude, mesh, dir, euler);
			
			addToDrawList(shape);
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
	private int randomBetween(Random r,int min,int max)
	{
		int low = min;
		int high = max;
		return r.nextInt(high-low) + low;
	}
	private Vector2 pickRandomDir()
	{
		Vector2 dir;
		Random r = new Random();
		r.setSeed(System.currentTimeMillis());
		int result = randomBetween(r, 1, 5);
		switch(result)
		{
			case 1: // turn up
				dir= Vector2.up();
				break;
			case 2: // turn down
				dir= Vector2.down();
				break;
			case 3: // turn right
				dir= Vector2.right();
				break;
			case 4: // turn left
				dir = Vector2.left();
				break;
			case 5: // turn left
				dir = Vector2.zero();
				break;
			default:
				dir = Vector2.left();
				break;
		}
		return dir;
	}
	private Vector3 dirTurnToEuler(Vector2 dirTurn)
	{
		double jawTurn,pitchTurn;
		if(dirTurn.equals(Vector2.up())) // up
		{
			jawTurn =0;
			pitchTurn = Math.PI/2;
		}
		else if(dirTurn.equals(Vector2.down())) // down
		{
			jawTurn =0;
			pitchTurn = -Math.PI/2;
		}
		else if(dirTurn.equals(Vector2.right())) // right
		{
			jawTurn =Math.PI/2;
			pitchTurn = 0;
		}
		else // left
		{
			jawTurn =-Math.PI/2;
			pitchTurn = 0;
		}
		return new Vector2(pitchTurn,jawTurn).toVector3();
	}
	// estrude la faccia 'toExtrude', aggiunge il risultato dell'estrusione all mesh e ritorna la faccia estrusa
	private ArrayList<Triangle> extrudeFace(ArrayList<Triangle> toExtrude,Mesh mesh, Vector3 dir,Vector3 eulerAngles) 
			throws MatrixException
	{
		ArrayList<Triangle> extrudedFace = new ArrayList<Triangle>();
		ArrayList<Triangle> triExtruded = new ArrayList<Triangle>();
		ArrayList<Vector3> vertexPos = new ArrayList<Vector3>();
		for(Triangle t:toExtrude)
			vertexPos.add(t.centroid());
		Vector3 center = Vector3.avg(vertexPos);
		int i= 0;
		for(Triangle t:toExtrude)
		{
			ArrayList<Triangle> tri = 
					t.getTrianglesResultingFromExtrusion(dir,eulerAngles,center);
			extrudedFace.add(tri.get(0));
			// rimuovi triangoli non visibili
			if(i%2==0)
			{
				tri.remove(1);
				tri.remove(1);
				tri.remove(1);
				tri.remove(1);
			}
			else 
			{
				tri.remove(1);
				tri.remove(1);
				tri.remove(3);
				tri.remove(3);
			}
			triExtruded.addAll(tri);
			i++;
		}
		// aggiungi triangoli estrusi alla mesh
		for(Triangle t:triExtruded)
			mesh.addTriangle(t);
		return extrudedFace;
	}
	private ArrayList<Triangle> turnDir(ArrayList<Triangle> faceToExtrude,Mesh mesh,
			Vector3 currentDir, int step,double turnScale,Vector2 dirTurn) throws MatrixException
	{
		step = Math.max(1,step);
		turnScale = Math.max(1, turnScale);
		
		Vector3 euler = dirTurnToEuler(dirTurn);
		double jawTurn =euler.y,pitchTurn=euler.x;
		
		Vector3 dir =Transformation.ruotaSuAsseXYZ(currentDir.normalized(),
				Vector3.zero(), new Vector3(pitchTurn,jawTurn,0));
		Vector3 oppDir = dir.multiply(-0.5);
		
		oppDir = currentDir.normalized().sum(oppDir).multiply(turnScale/(float)step);
		dir = currentDir.normalized().sum(dir).multiply(turnScale/(float)step);
		
		
		
		Vector3 angle = new Vector3(pitchTurn,jawTurn,0).multiply(1/((float)step));
		for(int i=0;i<step;i++)
		{
			float t = ((float)(step-1)-i)/(float)(step-1);
			System.out.println(t);
			//float t = 1 -Math.abs(i - (float)(step-1)/2)/(float)(step-1);
			faceToExtrude = extrudeFace(faceToExtrude, mesh,
					dir.sum(oppDir.multiply(t)), angle);
			//System.out.println(oppDir.multiply(t));
//			 faceToExtrude = extrudeFace(faceToExtrude, mesh,
//					dirInc, angleInc);
			
			
		}
		return faceToExtrude;
	}
	public static void main(String[] args) 
	{
		try {
			// start game
			new MainGame_WinXpPipes_firstVersion(new JFrame(), WIDTH,HEIGHT).start();
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
			
			shape.setRotation(new Vector3(0,Math.PI*3/4,0));
			//shape.setRotation(new Vector3(0,angolo*2,0));
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
		g.drawString("Windows xp pipes (Re-do)",0,20); 
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
				System.out.println("shape info: "+shape);
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
