package fileReaders3D;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import fileReaders3D.FileReaderExceptions.FileMustBeAscii;
import fileReaders3D.FileReaderExceptions.FileReaderException;
import fileReaders3D.FileReaderExceptions.MeshInsideFileIsNotTriangulated;
import fileReaders3D.FileReaderExceptions.PlyFileCannotIncludeUV;
import fileReaders3D.FileReaderExceptions.PlyFileMustIncludeNormals;
import fileReaders3D.FileReaderExceptions.TryingToReadEmptyMesh;
import fileReaders3D.FileReaderExceptions.WrongFileExtension;
import geometry.Mesh;
import geometry.Triangle;
import geometry.Vertex;
import geometry.GeometryException.MeshException;
import mathUtils.Vector3;

public class PlyFileReader implements FileReader3D{
	private final String extension = "ply";
	public PlyFileReader() {
		// TODO Auto-generated constructor stub
	}
//	public static void main(String[] args) {
//		try {
//			ArrayList<Triangle> tris = new PlyFileReader().readTrianglesFromFile(
//					"C:\\Users\\Alessandro\\OneDrive\\Desktop\\blenderCubeply.ply");
//			for(Triangle t:tris)
//			{
//				System.out.println(t.getV1());
//				System.out.println(t.getV2());
//				System.out.println(t.getV3());
//			}
//			Mesh m =new PlyFileReader().readMeshFromFile("C:\\Users\\Alessandro\\OneDrive\\Desktop\\blenderCubeply.ply");
//			for(Triangle t:m.getTriangles())
//			{
//				System.out.println(t.getV1());
//				System.out.println(t.getV2());
//				System.out.println(t.getV3());
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MeshException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MeshInsideFileIsNotTriangulated e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	public void checkFileExtension(String fileName) throws WrongFileExtension
	{
		if(!fileName.substring(fileName.lastIndexOf('.')+1).equals(extension))
			throw new WrongFileExtension();
	}
	@Override
	public ArrayList<Triangle> readTrianglesFromFile(String fileName)
			throws FileNotFoundException, MeshException, FileReaderException {
		checkFileExtension(fileName);
		ArrayList<Vertex> vertici = new ArrayList<Vertex>();
		ArrayList<Triangle> triangoli = new ArrayList<Triangle>();
		
		File myObj = new File(fileName);
		@SuppressWarnings("resource")
		Scanner myReader = new Scanner(myObj);
		
		int totVertici =0, totFacce=0;
		int verticiDaLeggere = 0, facceDaLeggere=0;
		while (myReader.hasNextLine()) 
		{
			String data = myReader.nextLine();
			if(data.contains("format binary"))
				throw new FileMustBeAscii();
			if(data.contains("element vertex"))
			{
				totVertici = Integer.parseInt(data.substring(data.indexOf(' ',10)+1));
			}
			if(data.contains("element face"))
			{
				totFacce = Integer.parseInt(data.substring(data.indexOf(' ',10)+1));
			}
			else if(data.contains("end_header"))
			{
				verticiDaLeggere = totVertici;
			}
			else if(verticiDaLeggere>0)
			{
				//System.out.println(data);
				if(countChar(data, ' ')>9)
					throw new PlyFileCannotIncludeUV();
				Vector3 position = readVector3(data);
				int indexEndNormal = indexOfCar(data, ' ', 6);
				Vector3 color = null;
				double alpha =1;
				Vector3 normal; 
				try 
				{
					if(indexEndNormal==-1)
					{
						String s = data.substring(indexOfCar(data, ' ', 3))+" ";
						normal = readVector3(s);
					}
					else 
					{
						normal = readVector3(data.substring(indexOfCar(data, ' ', 3)));
						color =  readVector3(data.substring(indexOfCar(data, ' ', 6)));
						alpha = Double.parseDouble(data.substring(indexOfCar(data, ' ', 9)));
					}
				} catch (IndexOutOfBoundsException e) {
					throw new PlyFileMustIncludeNormals();
				}
				
//				System.out.println("position = "+position);
//				System.out.println("normal = "+normal);
//				System.out.println("color = "+color);
//				System.out.println("alpha = "+alpha);
				Vertex v;
				if(color!=null)
				{
					v = new Vertex(position,normal,
							new Color((int)color.x,(int)color.y,(int)color.z,(int)alpha));
				}
				else v = new Vertex(position,normal);
				vertici.add(v);
				
				verticiDaLeggere--;
				if(verticiDaLeggere<=0)
					facceDaLeggere=totFacce;
			}
			else if(facceDaLeggere>0)
			{
				//System.out.println("face: "+data);
				// se leggi come primo carattere 4 allora c'e' un quad di triangoli
				// e quindi la mesh non e' triangolata
				if(data.charAt(0) == '4')
					throw new MeshInsideFileIsNotTriangulated();
				
				String s = data.substring(2,data.length())+ " ";
				Vector3 indexVertexes = readVector3(s);
				//System.out.println(indexVertexes);
				
				Vertex v1 = vertici.get((int) indexVertexes.x).copy();
				Vertex v2 = vertici.get((int) indexVertexes.y).copy();
				Vertex v3 = vertici.get((int) indexVertexes.z).copy();
				
				triangoli.add(new Triangle(v1,v2,v3));
				facceDaLeggere--;
			}
		}
		myReader.close();
		if(triangoli.size()==0)
			throw new TryingToReadEmptyMesh();
		return triangoli;
	}

	@Override
	public Mesh readMeshFromFile(String fileName)
			throws FileNotFoundException, MeshException, FileReaderException {
		return new Mesh(readTrianglesFromFile(fileName));
	}
	private Vector3 readVector3(String line)
	{
		int i = indexOfCar(line, ' ', 1);
		int j = indexOfCar(line, ' ', 2);
		double x = Double.parseDouble(
				line.substring(0,i));
		double y = Double.parseDouble(line.substring(i,j)); 
		i=j;
		j = indexOfCar(line, ' ', 3);
		double z = Double.parseDouble(line.substring(i,j)); 
		return new Vector3(x,y,z);
	}
	private int indexOfCar(String str,char c,int occorrenza)
	{
		int lastIndex=0;
		for(int i=0;i<occorrenza;i++)
		{
			if(lastIndex == -1)break;
			lastIndex = str.indexOf(c,lastIndex+1);
		}
		return lastIndex;
	}
	private int countChar(String s,char c)
	{
		int cont=0;
		for(int i=0;i<s.length();i++)
			if(s.charAt(i)==c)cont++;
		return cont;
	}
}
