package fileReaders3D;
import java.io.File; 
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import fileReaders3D.FileReaderExceptions.FileReaderException;
import fileReaders3D.FileReaderExceptions.MeshInsideFileIsNotTriangulated;
import fileReaders3D.FileReaderExceptions.TryingToReadEmptyMesh;
import fileReaders3D.FileReaderExceptions.WrongFileExtension;
import geometry.Mesh;
import geometry.Triangle;
import geometry.Vertex;
import geometry.GeometryException.MeshException;
import mathUtils.*;
public class ObjFileReader implements FileReader3D{
	private final String extension = "obj";
	public ObjFileReader() {
		
	}
	public Mesh readMeshFromFile(String fileName) 
			throws FileNotFoundException, MeshException, FileReaderException
	{
		return new Mesh(readTrianglesFromFile(fileName));
	}
	public ArrayList<Triangle> readTrianglesFromFile(String fileName) 
			throws FileNotFoundException, MeshException, FileReaderException
	{
		checkFileExtension(fileName);
		ArrayList<Vertex> vertici = new ArrayList<Vertex>();
		ArrayList<Vector3> normals = new ArrayList<Vector3>();
		ArrayList<Triangle> triangoli = new ArrayList<Triangle>();
		
		File myObj = new File(fileName);
		@SuppressWarnings("resource")
		Scanner myReader = new Scanner(myObj);
		while (myReader.hasNextLine()) {
		  String data = myReader.nextLine();
		    
		  if(data.length()==0)continue;
		  if(data.contains("vn"))
		  {
			  Vector3 normal = readVertexLineAsVector3(data);
			  normals.add(normal);
		  }
		  else if(data.charAt(0)=='v' && !data.contains("vt") && !data.contains("vn"))
		  {
			  Vertex vertex = new Vertex(readVertexLineAsVector3(data));
			  vertici.add(vertex);
		  }
		  else if(data.charAt(0)=='f')
		  {
			  while(data.contains("//"))
				  data =data.replace("//", "/0/");
			  try
			  {
				  int endTriangle1= data.indexOf(' ',2);
				  int endTriangle2= data.indexOf(' ',endTriangle1+1);
				  
				  
			      Vector3 indexesV1 = readVertexIndexes(data.substring(2,endTriangle1));
				  Vector3 indexesV2 = readVertexIndexes(data.substring(endTriangle1+1,endTriangle2));
				  Vector3 indexesV3 = readVertexIndexes(data.substring(endTriangle2+1));
			    	
				  Vertex v1 = buildVertex(indexesV1, normals, vertici);
				  Vertex v2 = buildVertex(indexesV2, normals, vertici);
				  Vertex v3 = buildVertex(indexesV3, normals, vertici);
			    	
				  //triangoli.add(new Triangle(v1,v2,v3));
				  triangoli.add(new Triangle(v1,v3,v2));
			  } catch(NumberFormatException e)
			  {
				  throw new MeshInsideFileIsNotTriangulated();
			  }
			  
		  }
		}
//		System.out.println("tot vertici:"+vertici.size());
//		System.out.println("tot normals:"+normals.size());
		myReader.close();
		if(triangoli.size()==0)
			throw new TryingToReadEmptyMesh();
		return triangoli;
	}
	private Vector3 readVertexLineAsVector3(String vLine)
	{
		int xBegin = vLine.indexOf(' ');
    	int xEnd = vLine.indexOf(' ',xBegin+1);
    	String xStr = vLine.substring(xBegin,xEnd);
    	
    	int yBegin = vLine.indexOf(' ',xEnd-1);
    	int yEnd = vLine.indexOf(' ',yBegin+1);
    	String yStr = vLine.substring(yBegin,yEnd);
    	
    	String zStr = vLine.substring(yEnd);
    	return new Vector3(Double.parseDouble(xStr),
    			Double.parseDouble(yStr),Double.parseDouble(zStr));
	}
	private Vertex buildVertex(Vector3 vertexIndexes,ArrayList<Vector3> normals,ArrayList<Vertex> vertici)
	{
		int vIndex = (int)vertexIndexes.x;
		int vnIndex = (int)vertexIndexes.z;
		
		Vertex vertex = vertici.get(vIndex).copy();
		//vertex.setPosition(vertex.getPosition().multiply(2)); 
		vertex.setNormal(normals.get(vnIndex).copy().normalized());
		
		return vertex;
	}
	private Vector3 readVertexIndexes(String fLine)
	{
		int firstSlash = fLine.indexOf('/');
		int secondSlash =  fLine.indexOf('/', firstSlash+1);
		String v = fLine.substring(0,firstSlash);
		String vt = fLine.substring(firstSlash+1,secondSlash);
		String vn = fLine.substring(secondSlash+1);
		//System.out.println(fLine);
		return new Vector3(Double.parseDouble(v)-1,
    			Double.parseDouble(vt)-1,Double.parseDouble(vn)-1);
	}
	@Override
	public void checkFileExtension(String fileName) throws WrongFileExtension {
		if(!fileName.substring(fileName.lastIndexOf('.')+1).equals(extension))
			throw new WrongFileExtension();
	}
}
