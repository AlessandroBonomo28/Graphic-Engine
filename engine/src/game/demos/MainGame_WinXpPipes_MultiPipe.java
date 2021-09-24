package game.demos;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.JFrame;

import Graphics.DrawableTriangle;
import Graphics.Rasterizer;
import fileReaders3D.ObjFileReader;
import fileReaders3D.PlyFileReader;
import fileReaders3D.FileReaderExceptions.FileReaderException;
import game.Audio;
import game.GameEngine;
import game.GameObject;
import geometry.*;
import geometry.GeometryException.MeshException;
import mathUtils.*;
import mathUtils.matrixExceptions.*;

public class MainGame_WinXpPipes_MultiPipe extends GameEngine
{
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1920/2;
	private static final int HEIGHT = 1080/2;
	private static Color bgColor = Color.black;//new Color(33, 0, 79);
	
	ObjFileReader objReader;
	PlyFileReader plyReader;
	
	public Transform camera;
	public Transform pipeTransform;
	public Mesh currentPipeMesh;
	ArrayList<Triangle> pipeFaceToExtrude = new ArrayList<Triangle>();
	
	Vector3 topRightBound = Vector3.one().multiply(15);
	Vector3 bottomLeftBound = topRightBound.multiply(-1);
	List<Vector3> visitedPositions = new ArrayList<Vector3>();
	int maxPositions = (int) Math.pow(topRightBound.x, 3);
	
	Vector3 dirTravel = Vector3.forward();
	double distToTravel = 1;
	double distCovered = 0;
	int minPathLen = 5;
	int maxPathLen = 10;
	
	double turnScale = 1;
	int turnStep = 2;
	double movUnit = 0.8f;
	Random rand = new Random(System.currentTimeMillis()); 
	
	int pipeCount = 0;
	int maxPipes = 5;
	
	double timerReset = 0;
	double tempoReset = 5; // sec
	
	int destinationCount = 0;
	int maxDestinations = 35;
	// se true il pipe respawna ogni volta che scade il timer,
	// altrimenti respawna quando destinationCount raggiunge maxDestinations
	boolean usingResetTimer = false;
 	
	boolean isPaused = false;
	
	private int aspectIndex=5,maxAspectIndex =16;
	private void changeAspect() {
		aspectIndex++;
		if(aspectIndex>maxAspectIndex)aspectIndex=0;
	}
	private Color pickRandomColor()
	{
		return pipeColors[randomBetween(0, pipeColors.length)];
//		return new Color(randomBetween( 1, 255),
//						 randomBetween( 1, 255),
//						 randomBetween( 1, 255));
	}
	int nPipeColors = 20;
	Color[] pipeColors = generatePalette(nPipeColors);
	public Color[] generatePalette(int n)
	{
	    Color[] cols = new Color[n];
	    for(int i = 0; i < n; i++)
	    {
	        cols[i] = Color.getHSBColor((float) i / (float) n, 0.85f, 1.0f);
	    }
	    return cols;
	}
	private void resetPipe()
	{
		if(pipeCount>=maxPipes)
		{
			clearDrawList();
			visitedPositions = new ArrayList<Vector3>();
			pipeCount =0;
		}
		distCovered=0;
		distToTravel = 0;
		dirTravel = Vector3.forward();
	}
	boolean isSpawning = false;
	private void spawnPipe() throws FileNotFoundException, MeshException, MatrixException, FileReaderException
	{
		pipeCount++;
		Vector3 pipePosition = randomFreePositionInsideCube();
		pipeTransform = new Transform(objReader.readMeshFromFile(pipeFaceToExtrudeFilePath),pipePosition); 
		//pipeTransform.transla(Vector3.forward().multiply(5), Space.World);
		
		currentPipeMesh = pipeTransform.getMesh();
		
		
		currentPipeColor = pickRandomColor();
		currentPipeMesh.setColor(currentPipeColor);
		
		pipeFaceToExtrude = currentPipeMesh.getTriangles();
		
		savePositionIntoMap(getCurrentPos());
		addToDrawList(pipeTransform);
		isSpawning = true;
	}
	private Vector3 randomFreePositionInsideCube()
	{
		int tryCount = 0;
		int maxTry = 500;
		Vector3 pos;
		int offset = 1;
		do 
		{
			int x = randomBetween((int)bottomLeftBound.x + offset, (int)topRightBound.x -offset);
			int y = randomBetween((int)bottomLeftBound.y + offset, (int)topRightBound.y -offset);
			int z = randomBetween((int)bottomLeftBound.z + offset, (int)topRightBound.z -offset);
			pos = new Vector3(x,y,z);
		}
		while(isPositionVisited(pos) && tryCount < maxTry);
		if(tryCount == maxTry)
			System.out.println("Troppi tentativi per generare una posizione di spawn libera.");
		return pos;
	}
	Color currentPipeColor;
	String sphereFilePath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\sphereLowPoly.obj";
	String pipeFaceToExtrudeFilePath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\3dFiles\\exagon.obj";
	String backgroundMusicPath = "C:\\Users\\Alessandro\\OneDrive\\Desktop\\progetti\\gameEngine\\audio\\theJourney.wav";
	public MainGame_WinXpPipes_MultiPipe(JFrame window,int widthWindow,int heightWindow) throws MatrixException {
		super(window,widthWindow,heightWindow,bgColor);	
		
		objReader = new ObjFileReader();
		plyReader = new PlyFileReader(); 
		
		Rasterizer rasterizer = getRasterizer();
		rasterizer.setUseDirectionalLight(true);
		rasterizer.setLightDirection(new Vector3(0.1,0.4,0.4).normalized());
		rasterizer.setMinPixelBrightness(0.4);
		rasterizer.setUseRenderDistance(false);
		rasterizer.setRenderDistance(20);
		
		camera = getCamera();
		
		// Riproduci background music in loop
	    Audio.getInstance().playMusicLoop(backgroundMusicPath,0.6f);
	    try {
	    	
			camera.setPosition(new Vector3(25.12, 17.63, 44.67));
			camera.setRotation(new Vector3(0.79, -2.58, 0.00));
			camera.transla(Vector3.forward().multiply(10), Space.Local);
//			camera.transla(Vector3.backward().multiply(2.5), Space.Local);
//			camera.transla(Vector3.down().multiply(0.5f), Space.World);
			spawnPipe();
			
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
	
	private int randomBetween(int min,int max)
	{
		int low = min;
		int high = max;
		return rand.nextInt(high-low) + low;
	}
	private Vector2 pickRandomDir(Random r)
	{
		Vector2 dir;
		int result = randomBetween( 1, 4);
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
		// aggiungi triangoli estrusi alla mesh e set color
		for(Triangle t:triExtruded)
		{
			t.setColor(currentPipeColor);
			mesh.addTriangle(t);
		}
			
		return extrudedFace;
	}
	private ArrayList<Triangle> turnDir(ArrayList<Triangle> faceToExtrude,Mesh mesh,
			Vector3 currentDir, int step,double turnScale,Vector2 dirTurn) throws MatrixException
	{
		step = Math.max(1,step);
		turnScale = Math.max(0, turnScale);
		
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
			if(step==1)t=0;
			
			savePositionIntoMap(avgTrianglesCenters(faceToExtrude).round());
			
			faceToExtrude = extrudeFace(faceToExtrude, mesh,
					dir.sum(oppDir.multiply(t)), angle);

			
			savePositionIntoMap(avgTrianglesCenters(faceToExtrude).round());
		}
		return faceToExtrude;
	}
	public static void main(String[] args) 
	{
		try {
			// start game
			new MainGame_WinXpPipes_MultiPipe(new JFrame(), WIDTH,HEIGHT).start();
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
	
	
	
	
	
	
	
	
	private Vector3 avgTrianglesCenters(ArrayList<Triangle> l)
	{
		ArrayList<Vector3> vertexPos = new ArrayList<Vector3>();
		for(Triangle t:l)
			vertexPos.add(t.centroid());
		return Vector3.avg(vertexPos);
	}
	private Vector3 getCurrentPos()
	{
		return avgTrianglesCenters(pipeFaceToExtrude);
	}
	private void savePositionIntoMap(Vector3 pos)
	{
		pos = pos.round();
		if(visitedPositions.size()>=maxPositions)
		{
			System.out.println("Lista posizioni visitate piena.");
			return;
		}
		if(!visitedPositions.contains(pos))
			visitedPositions.add(pos);
		
	}
	
	private boolean isPositionVisited(Vector3 pos)
	{
		return visitedPositions.contains(pos.round());
	}
	private boolean isWayFree(Vector3 pos,Vector3 dir,int lengthPath)
	{
		lengthPath = Math.max(1, lengthPath);
		boolean res=true;
		dir = dir.normalized();
		for(int i=0;i<=lengthPath;i++)
		{
			if(isPositionVisited(pos.sum(dir.multiply(i+1))))
			{
				//System.out.println("not free");
				res = false;
				break;
			}
			//else System.out.println("free "+pos.sum(dir.multiply(i+1)));
		}
		return res;
	}
	private boolean isPositionInsideBounds(Vector3 pos)
	{
		
		if(pos.x<topRightBound.x && pos.y<topRightBound.y && pos.y<topRightBound.y &&
				pos.x>bottomLeftBound.x && pos.y>bottomLeftBound.y && pos.z>bottomLeftBound.z)
			return true;
		else return false;
	}
	// Update viene chiamato prima di draw
	@Override
	protected void update() 
	{
		super.update();
		
		if(isPaused)
			return;
		
		if(usingResetTimer)
			timerReset+=deltaTime;
		
		if(timerReset>= tempoReset && usingResetTimer)
		{
			destinationCount = 0;
			timerReset = 0;
			try {
				resetPipe();
				spawnPipe();
				System.out.println("Respawn pipe at position "+getCurrentPos());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MeshException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MatrixException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(destinationCount>= maxDestinations && !usingResetTimer)
		{
			destinationCount = 0;
			try {
				resetPipe();
				spawnPipe();
				System.out.println("Respawn pipe at position "+getCurrentPos());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MeshException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MatrixException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		ArrayList<Vertex> vertexes = new ArrayList<Vertex>();
		for(Triangle t:pipeFaceToExtrude)
		{
			vertexes.addAll(currentPipeMesh.getVertexesConnectedTo(t.getV1()));
			vertexes.addAll(currentPipeMesh.getVertexesConnectedTo(t.getV2()));
			vertexes.addAll(currentPipeMesh.getVertexesConnectedTo(t.getV3()));
			
		}
		Set<Vertex> set = new HashSet<>(vertexes);
		for(Vertex v:set)
			v.transla(dirTravel.multiply(movUnit));
			
		distCovered +=movUnit;
		
		Vector3 currentPipePosition = getCurrentPos();
		savePositionIntoMap(currentPipePosition);
		
		try {
			if(distCovered>=distToTravel) // ha raggiunto la destinazione impostata
			{
				destinationCount++;
				distCovered=0;
				int maxTry = 500;
				int tryCount =0;
				
				int res = randomBetween(0, 2);
				if(res == 0 && !isSpawning && destinationCount!=maxDestinations) // svolta con step
				{
					
					Vector2 dirTurn;
					Vector3 newDirTravel;
					do
					{
						distToTravel = randomBetween(minPathLen, maxPathLen);
						dirTurn = pickRandomDir(rand);
						newDirTravel = 
								Transformation.ruotaSuAsseXYZ(dirTravel, Vector3.zero(), dirTurnToEuler(dirTurn));
						tryCount++;
					}while(newDirTravel.round().equals(dirTravel.round())
							|| !isWayFree(currentPipePosition, newDirTravel, (int) distToTravel) 
							&& tryCount<maxTry);
					
					if(tryCount == maxTry)
						System.out.println("Troppi tentativi per trovare una strada libera.");
					
					pipeFaceToExtrude = turnDir(pipeFaceToExtrude,currentPipeMesh,dirTravel, turnStep, turnScale,dirTurn);
					
					dirTravel = newDirTravel;
					
					pipeFaceToExtrude = extrudeFace(pipeFaceToExtrude, currentPipeMesh, dirTravel.multiply(movUnit+0.001f), Vector3.zero());
				}
				else // svolta 90 gradi, istanziando una sfera
				{
					if(isSpawning)isSpawning = false;
					
					Vector2 dirTurn;
					Vector3 newDirTravel;
					do
					{
						distToTravel = randomBetween(minPathLen, maxPathLen);
						dirTurn = pickRandomDir(rand);
						newDirTravel = 
								Transformation.ruotaSuAsseXYZ(dirTravel, Vector3.zero(), dirTurnToEuler(dirTurn));
						tryCount++;
					}while(newDirTravel.round().equals(dirTravel.round())
							|| !isWayFree(currentPipePosition, newDirTravel, (int) distToTravel)
							&& tryCount<maxTry);
					
					if(tryCount == maxTry)
						System.out.println("Troppi tentativi per trovare una strada libera.");
					
					pipeFaceToExtrude = turnDir(pipeFaceToExtrude,currentPipeMesh,dirTravel, 1, 0,dirTurn);
					
					dirTravel = newDirTravel;
					
					pipeFaceToExtrude = extrudeFace(pipeFaceToExtrude, currentPipeMesh, dirTravel.multiply(movUnit+0.001f), Vector3.zero());
					
					Transform t = null;
					try {
						Mesh m =objReader.readMeshFromFile(sphereFilePath);
						m.setColor(currentPipeColor);
						t = new Transform(m);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MeshException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FileReaderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// istanzia sfera
					instantiate(new GameObject(t),currentPipePosition,Vector3.zero());
					//faceToExtrude = extrudeFace(faceToExtrude, shape.getMesh(), dirTravel, Vector3.zero());
					
				}
			}
		} catch (MatrixException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
//					getRasterizer().setUseDirectionalLight(true);
//					
//					tri.fillTriangle(g, Color.green);
//					tri.drawOutlineTriangle(g, Color.black);
					getRasterizer().setUseDirectionalLight(false);
					tri.fillTriangleByMajorColor(g);
					
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
		g.drawString("Windows XP pipes (Re-do)",0,20); 
		g.drawString("Press P to pause-resume",0,40);
		g.drawString("Press H to switch aspect",0,60);
		g.drawString("Explore using WASD-RF-1234",0,80);
		g.drawString("PipeCount: "+pipeCount,0,100);
	}
	@Override
	protected void keyTyped(KeyEvent e) {
		try {
			float speed = 10;
			if(e.getKeyChar() == 'w' || e.getKeyChar() == 'W')
			{
				camera.transla(Vector3.forward().multiply(deltaTime*speed), Space.Local);
			}
			else if(e.getKeyChar() == 's' || e.getKeyChar() == 'S')
			{
				camera.transla(Vector3.backward().multiply(deltaTime*speed), Space.Local);
			}
			if(e.getKeyChar() == 'a' || e.getKeyChar() == 'A')
			{
				camera.transla(Vector3.right().multiply(deltaTime*speed), Space.Local);
			}
			else if(e.getKeyChar() == 'd' || e.getKeyChar() == 'D')
			{
				camera.transla(Vector3.left().multiply(deltaTime*speed), Space.Local);
			}
			if(e.getKeyChar() == 'r' || e.getKeyChar() == 'R')
			{
				camera.transla(Vector3.up().multiply(deltaTime*speed), Space.Local);
			}
			else if(e.getKeyChar() == 'f' || e.getKeyChar() == 'F')
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
			else if(e.getKeyChar() == 'i' || e.getKeyChar() == 'I')
			{
				System.out.println("Camera info: "+camera);
				System.out.println("Pipe info: "+pipeTransform);
				//System.out.println("Next reset in "+(tempoReset - timerReset)+" sec");
			}
			else if(e.getKeyChar() =='h' || e.getKeyChar() == 'H')
			{
				changeAspect();
			}
			else if(e.getKeyChar() == 'p' || e.getKeyChar() == 'P')
			{
				isPaused = !isPaused;
			}
		} catch (MatrixException e1) {
			
			e1.printStackTrace();
		}
	}
	@Override
	protected void keyReleased(KeyEvent e) {
		
	}
	
}
