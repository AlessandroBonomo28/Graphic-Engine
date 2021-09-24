package fileReaders3D;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import fileReaders3D.FileReaderExceptions.FileReaderException;
import fileReaders3D.FileReaderExceptions.WrongFileExtension;
import geometry.Mesh;
import geometry.Triangle;
import geometry.GeometryException.MeshException;

public interface FileReader3D {
	public ArrayList<Triangle> readTrianglesFromFile(String fileName)
			throws FileNotFoundException, MeshException, FileReaderException;
	public Mesh readMeshFromFile(String fileName) 
			throws FileNotFoundException, MeshException, FileReaderException;
	public void checkFileExtension(String fileName) throws WrongFileExtension;
}
