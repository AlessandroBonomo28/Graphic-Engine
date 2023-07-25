package Graphics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import geometry.*;
import mathUtils.*;
import mathUtils.matrixExceptions.MatrixException;

public class Rasterizer 
{
	private int widthWindow;
	private int heightWindow;
	private double FOV=90;
	private Transform camera;
	private Matrix perspectiveMatrix,viewMatrix;
	
	private boolean useDirectionalLight = true;
	private boolean useRenderDistance = true;
	
	private Vector3 lastCameraPos = new Vector3();
	private Vector3 lastCameraRot = new Vector3();
	// deve essere un vettore unitario
	private Vector3 lightDirection = new Vector3(-1,0,0);
	
	private Vector3 nearPlanePoint = new Vector3(0,0,2f);
	private Vector3 nearPlaneNormal = new Vector3(0,0,1);
	
	private double lightIntensityCorrection = 0.15; // tra 0 e 1
	private double maxPixelBrightness = 1; // tra 0 e 1
	private double minPixelBrightness = 0.1; // tra 0 e 1
	
	private double renderDistanceSquared=500*500;
	
	public Rasterizer(int widthWindow, int heightWindow, double FOV, Transform camera) throws MatrixException {
		super();
		this.widthWindow = widthWindow;
		this.heightWindow = heightWindow;
		this.FOV = clamp(FOV,90,180);
		this.camera = camera;
		perspectiveMatrix = calcPerspectiveMatrix();
		// inizializzo ultima posizione a un valore diverso da quello della camera
		lastCameraPos = camera.getPosition().sum(Vector3.forward()); 
	}
	public Rasterizer(int widthWindow, int heightWindow, Transform camera) throws MatrixException {
		super();
		this.widthWindow = widthWindow;
		this.heightWindow = heightWindow;
		this.FOV = 90;
		this.camera = camera;
		perspectiveMatrix = calcPerspectiveMatrix();
		// inizializzo ultima posizione a un valore diverso da quello della camera
		lastCameraPos =camera.getPosition().sum(Vector3.forward()); 
	}
	public boolean isUsingDirectionalLight() {
		return useDirectionalLight;
	}
	public void setUseDirectionalLight(boolean useDirectionalLight) {
		this.useDirectionalLight = useDirectionalLight;
	}
	public Vector3 getLightDirection() {
		return lightDirection;
	}
	public void setLightDirection(Vector3 lightDirection) {
		this.lightDirection = lightDirection.normalized();
	}
	public void changeFOV(double FOV)
	{
		this.FOV = FOV;
		perspectiveMatrix = calcPerspectiveMatrix();
	}
	private Matrix calcPerspectiveMatrix()
	{
		double FOVrad = 1/Math.tan(FOV*0.5f*180/Math.PI);
		double zFar=1000,zNear=0.1f;
		double screenRatio = widthWindow/heightWindow;
		
		double[][] perspectiveMatrix = {
				{screenRatio*FOVrad,0,0,0},
				{0,FOVrad,0,0},
				{0,0,zFar/(zFar-zNear),1},
				{0,0,(-zFar*zNear)/(zFar-zNear),0}
		};
		return new Matrix(perspectiveMatrix);
	}
	private Matrix calcViewSpaceMatrix() throws MatrixException
	{
		if(camera.getRotation().equals(lastCameraRot) &&
				camera.getPosition().equals(lastCameraPos))
			return viewMatrix;
		
		Vector3 right = camera.right();
		Vector3 forward = camera.forward();
		Vector3 up = camera.up();
		double[][] view_matrix = new double[4][4];
		view_matrix[0][0] = right.x; 
		view_matrix[0][1] = right.y; 
		view_matrix[0][2] = right.z; 
		view_matrix[1][0] = up.x; 
		view_matrix[1][1] = up.y; 
		view_matrix[1][2] = up.z; 
		view_matrix[2][0] = forward.x; 
		view_matrix[2][1] = forward.y; 
		view_matrix[2][2] = forward.z; 
		
		view_matrix[3][3] = 1; 
		 
		view_matrix[3][0] = camera.getPosition().x; 
		view_matrix[3][1] = camera.getPosition().y; 
		view_matrix[3][2] = camera.getPosition().z; 
		
		lastCameraPos = camera.getPosition();
		lastCameraRot = camera.getRotation();
		return new Matrix(view_matrix).inverse();
	}
	private Vector3 toViewSpace(Vector3 punto) throws MatrixException
	{
		viewMatrix = calcViewSpaceMatrix();
		return Operations.multiplyMajorRow(punto,viewMatrix);
	}
	private Vector3 toNormalizedDeviceCoord(Vector3 punto) throws MatrixException
	{
		Matrix vectorMajorRow = Operations.makeMajorRowVector(punto);
		Matrix res = Matrix.multiply(vectorMajorRow, perspectiveMatrix);
		double x = res.get(0, 0);
		double y = res.get(0, 1);
		double z = res.get(0, 2);
		double w = res.get(0, 3);
		if(w!= 0)return new Vector3(x/w, y/w, z/w);
		else return new Vector3(x, y, z);
		
	}
	private Vector3 toWindowSpace(Vector3 ndcCoord)
	{
		double z = ndcCoord.z;
		// porta al centro dello schermo le coordinate
		ndcCoord = ndcCoord.sum(Vector3.one()); 
		ndcCoord =  ndcCoord.multiply(0.5f*widthWindow);
		ndcCoord.z = z; // mantieni Z depth
		return ndcCoord;
	}
	private double clamp(double val, double min, double max) {
	    return Math.max(min, Math.min(max, val));
	}
	List<Triangle> clipAgainstPlane(Triangle triToClip, Vector3 planePoint, Vector3 planeNormalTowardsInside) {
		ArrayList<Triangle> clippedTriangles = new ArrayList<Triangle>();
		ArrayList<Vertex> outsideVertexes = new ArrayList<Vertex>();
		ArrayList<Vertex> insideVertexes = new ArrayList<Vertex>();
		
		for(int i=0;i<3;i++) {
			Vertex v;
			if(i==0)v = triToClip.getV1();
			else if(i==1)v = triToClip.getV2();
			else v = triToClip.getV3();
			
			boolean isInside = v.isAboveOrOntoPlane(planePoint, planeNormalTowardsInside);
			if(isInside) {
				insideVertexes.add(v);
			} else {
				outsideVertexes.add(v);
			}
			
		}
		
		int insideCount = insideVertexes.size();
		
		if(insideCount == 3) clippedTriangles.add(triToClip);
		else if(insideCount == 1) { // form new triangle
			Vector3 vo1 = outsideVertexes.get(0).getPosition(),vo2 = outsideVertexes.get(1).getPosition();
			Vector3 vi = insideVertexes.get(0).getPosition(); 
			Vector3 dir1 = Vector3.sub(vo1,vi);
			Vector3 dir2 = Vector3.sub(vo2, vi);
			try {
				Vector3 intercept1 = Operations.interceptPlane(vo1, dir1, planePoint, planeNormalTowardsInside);
				Vector3 intercept2 = Operations.interceptPlane(vo2, dir2, planePoint, planeNormalTowardsInside);
				clippedTriangles.add(new Triangle(vi,intercept1,intercept2));
			} catch(Exception e) {
				clippedTriangles.add(triToClip);
				return clippedTriangles;
			}
		} else if(insideCount == 2){ // form quad (2 new triangles)
			Vector3 vi1 = insideVertexes.get(0).getPosition(),vi2 = insideVertexes.get(1).getPosition();
			Vector3 vo = outsideVertexes.get(0).getPosition(); 
			Vector3 dir1 = Vector3.sub(vo,vi1);
			Vector3 dir2 = Vector3.sub(vo, vi2);
			try {
				Vector3 intercept1 = Operations.interceptPlane(vo, dir1, planePoint, planeNormalTowardsInside);
				Vector3 intercept2 = Operations.interceptPlane(vo, dir2, planePoint, planeNormalTowardsInside);
				
				clippedTriangles.add(new Triangle(vi1,vi2,intercept2));
				clippedTriangles.add(new Triangle(vi1,intercept1,intercept2));
			} catch(Exception e) {
				clippedTriangles.add(triToClip);
				return clippedTriangles;
			}
		}
		
		return clippedTriangles;
	}
	
	public List<DrawableTriangle> rasterize(Transform t) throws MatrixException
	{
		Mesh mesh = t.getMesh();
		ArrayList<DrawableTriangle> rasterizedTriangles = new ArrayList<DrawableTriangle>(); 
		for(Triangle tri:mesh.getTriangles())
		{
			double lightIntensity = 1;
			
			Vector3 v1 = tri.getV1().getPosition();
			Vector3 v2 = tri.getV2().getPosition();
			Vector3 v3 = tri.getV3().getPosition();
			
			Color colV1 = tri.getV1().getColor();
			Color colV2 = tri.getV2().getColor();
			Color colV3 = tri.getV3().getColor();
			
			Vector3 lookAtTri = Vector3.sub(camera.getPosition(),tri.centroid());
			if(-lookAtTri.dot(tri.getNormal())>0)
				continue;
			
			if(useRenderDistance)
				if(Vector3.distanceMagnitude(camera.getPosition(),Vector3.avg(v1, v2, v3))
						> renderDistanceSquared)
						continue;
			
			if(useDirectionalLight)
			{
				lightIntensity = Vector3.dot(lightDirection, tri.getNormal());
				lightIntensity += lightIntensityCorrection;
				lightIntensity = clamp(lightIntensity, minPixelBrightness, maxPixelBrightness);
			}
			
			v1 = toViewSpace(v1);
			v2 = toViewSpace(v2);
			v3 = toViewSpace(v3);
			
			Triangle triToClip = new Triangle(v1,v2,v3);
			List<Triangle> clippedTriangles = clipAgainstPlane(triToClip, nearPlanePoint, nearPlaneNormal);
					
			for(Triangle clippedTriangle : clippedTriangles) {
				v1 = clippedTriangle.getV1().getPosition();
				v2 = clippedTriangle.getV2().getPosition();
				v3 = clippedTriangle.getV3().getPosition();
				
				v1 = toNormalizedDeviceCoord(v1);
				v2 = toNormalizedDeviceCoord(v2);
				v3 = toNormalizedDeviceCoord(v3);
				
				v1 = toWindowSpace(v1);
				v2 = toWindowSpace(v2);
				v3 = toWindowSpace(v3);
				
				Triangle rasterizedTriangle = new Triangle(
						new Vertex(v1,colV1),
						new Vertex(v2,colV2), 
						new Vertex(v3,colV3)
				);
				
				rasterizedTriangles.add(new DrawableTriangle(
						rasterizedTriangle,lightIntensity,useDirectionalLight)
				);
			}
		}
		
		return rasterizedTriangles;
	}
	public void setRenderDistance(double renderDistance) {
		this.renderDistanceSquared = renderDistance*renderDistance;
	}
	public int getWidthWindow() {
		return widthWindow;
	}
	public int getHeightWindow() {
		return heightWindow;
	}
	public boolean isUsingRenderDistance() {
		return useRenderDistance;
	}
	public void setUseRenderDistance(boolean useRenderDistance) {
		this.useRenderDistance = useRenderDistance;
	}
	public double getLightIntensityCorrection() {
		return lightIntensityCorrection;
	}
	public void setLightIntensityCorrection(double lightIntensityCorrection) {
		this.lightIntensityCorrection = clamp(lightIntensityCorrection,0,1);
	}
	public double getMaxPixelBrightness() {
		return maxPixelBrightness;
	}
	public void setMaxPixelBrightness(double maxPixelBrightness) {
		this.maxPixelBrightness = clamp(maxPixelBrightness,0,1);
	}
	public double getMinPixelBrightness() {
		return minPixelBrightness;
	}
	public void setMinPixelBrightness(double minPixelBrightness) {
		this.minPixelBrightness = clamp(minPixelBrightness,0,1);
	}
	
}
