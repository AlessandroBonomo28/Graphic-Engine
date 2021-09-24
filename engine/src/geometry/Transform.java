package geometry;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import geometry.TransformException.ChildIndexOutOfBounds;
import geometry.TransformException.CannotBeChildOfChild;
import geometry.TransformException.CannotBeChildOfSelf;
import geometry.TransformException.CannotBeParentOfParent;
import geometry.TransformException.CannotBeParentOfSelf;
import geometry.TransformException.TransformException;
import mathUtils.*;
import mathUtils.matrixExceptions.MatrixException;

public class Transform 
{
	private Mesh mesh;
	private Vector3 position = new Vector3();
	private Vector3 rotation= new Vector3();
	private Vector3 localRotation= new Vector3();
	private Transform parent;
	private List<Transform> childs = new ArrayList<Transform>();
	public Transform(Mesh mesh, Vector3 position, Vector3 rotation) throws MatrixException {
		super();
		this.mesh = mesh;
		setPosition(position);
		setRotation(rotation);
	}
	public Transform(Vector3 position) throws MatrixException {
		super();
		setPosition(position);
		setRotation(Vector3.zero());
	}
	public Transform() throws MatrixException{
		super();
		setPosition(Vector3.zero());
		setRotation(Vector3.zero());
	}
	public Transform(Mesh mesh) throws MatrixException{
		super();
		this.mesh = mesh;
		position = mesh.center(); // imposto il centro del transform
		setPosition(Vector3.zero()); // muovo transform a zero 
		setRotation(Vector3.zero());
	}
	public Transform(Mesh mesh,Vector3 position) throws MatrixException{
		super();
		this.mesh = mesh;
		setPosition(position);
		setRotation(Vector3.zero());
	}
	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Vector3 getPosition() {
		return position.copy();
	}
	public Matrix getTranslationMatrix() throws MatrixException
	{
		return Transformation.makeTranslationMatrix(position);
	}
	public Matrix getRotationMatrix() throws MatrixException
	{
		return Transformation.makeRotationMatrix(rotation);
	}
	public void transla(Vector3 dir,Space space) throws MatrixException
	{
		if(space == Space.Local)
		{
			translaLocalSpace(dir);
			for(Transform c:childs)
				c.translaWorldSpace(worldToLocal(dir));
		}
		else
		{
			translaWorldSpace(dir);
			for(Transform c:childs)
				c.translaWorldSpace(dir);
		}
			
		
		
	}
	private void translaWorldSpace(Vector3 dir) throws MatrixException
	{
		if(hasMesh())
		{
			mesh.transla(dir);
		}
		position = position.sum(dir);
	}
	private void translaLocalSpace(Vector3 dir) throws MatrixException
	{
		dir = worldToLocal(dir);
		
		if(hasMesh())
		{
			mesh.transla(dir);
		}
		position = position.sum(dir);
	}
	public void setPosition(Vector3 newPosition) throws MatrixException {
		Vector3 oldPosition = this.position;
		transla(Vector3.sub(newPosition, oldPosition),Space.World);
		
	}

	public Vector3 getRotation() {
		return rotation.copy();
	}
	public void setRotation(Vector3 angoli) throws MatrixException {
		localRotation = angoli.copy();
		ruotaSuAsseXYZconOrigine(position, angoli);
	}
	public void ruotaSuWorldAsseXYZ(Vector3 angoli) throws MatrixException
	{
		ruotaSuAsseXYZconOrigine(Vector3.zero(), angoli);
	}
	public void ruotaSuAsseXYZconOrigine(Vector3 origine,Vector3 angoli) throws MatrixException
	{
		// TODO da migliorare
		Vector3 rotazRelativa = angoli.sub(rotation);
		rotation = angoli.copy();
		position = Transformation.ruotaSuAsseXYZ(position, origine, angoli);
		
		if(hasMesh())
			mesh.ruotaSuAsseXYZ(origine, angoli);
		
		for(Transform c:childs)
		{
			c.setPosition(Transformation.ruotaSuAsseXYZ(c.getPosition(), position,
					rotazRelativa));
			Vector3 rotChild = c.getLocalRotation().sum(rotazRelativa);
			c.setRotation(rotChild);
		}
	}
	
	public Vector3 worldToLocal(Vector3 puntoGlob) throws MatrixException
	{
		Matrix rotation = getRotationMatrix();
		return Operations.multiplyMajorColumn(rotation, puntoGlob);
	}
	public Vector3 localToWorld(Vector3 puntoLoc) throws MatrixException
	{
		Matrix rotation = getRotationMatrix().inverse();
		Vector3 v= Operations.multiplyMajorColumn(rotation, puntoLoc);
		return Operations.multiplyMajorColumn(getTranslationMatrix(),v);
	}
	public Transform getRootParent()
	{
		if(parent==null)return this;
		else return parent.getRootParent();
	}
	
	public Transform getParent() {
		return parent;
	}

	public void setParent(Transform newParent) throws TransformException{
		if(hasParent())
			if(parent.equals(newParent))
				return;
		
		if(newParent.equals(this)) throw new CannotBeChildOfSelf();
		if(getAllChildsDesc().contains(newParent)) throw new CannotBeChildOfChild();
		
		if(hasParent())
			removeChild(this);
		
		parent = newParent;
		newParent.addChild(this);
	}
	public void addChild(Transform newChild) throws TransformException
	{
		// controlla che non ci sia già
		if(hasChild(newChild))return;
		// controlla che non sia figlio di se stesso
		if(newChild.equals(this)) throw new CannotBeParentOfSelf();
		// controlla che non sia uno dei padri
		if(getAllParentsDesc().contains(newChild)) throw new CannotBeParentOfParent();
		childs.add(newChild);
		newChild.setParent(this);
	}
	public List<Transform> getAllChildsDesc()
	{
		return getAllChildsDescRecursive(new ArrayList<Transform>());
	}
	private List<Transform> getAllChildsDescRecursive(ArrayList<Transform> childsGathered)
	{
		if(!hasChilds())return new ArrayList<Transform>();
		for(Transform t:childs)
			childsGathered.add(t);
		
		for(Transform t:childs)
		{
			List<Transform> copyList = new ArrayList<Transform>();
			
			List<Transform> subChilds =t.getAllChildsDescRecursive(childsGathered);
			for(Transform i:subChilds)
				copyList.add(i);
			
			for(Transform i:copyList)
				if(!childsGathered.contains(i))
					childsGathered.add(i);
		} 
		return childsGathered;
		
	}
	public List<Transform> getAllParentsDesc()
	{
		List<Transform> res = getAllParentsAscRecursive(new ArrayList<Transform>());
		Collections.reverse(res);
		return res;
	}
	private List<Transform> getAllParentsAscRecursive(ArrayList<Transform> parentsGathered)
	{
		if(!hasParent()) return parentsGathered;
		parentsGathered.add(parent);
		return parent.getAllParentsAscRecursive(parentsGathered);
	}
	public List<Transform> getChilds() {
		return childs;
	}
	public Transform getChild(int index) throws TransformException{
		if(index>0 && index<childs.size())
			return childs.get(index);
		else throw new ChildIndexOutOfBounds();
	}
	private int indexOfChild(Transform t)
	{
		for(int i=0;i<childs.size();i++)
			if(childs.get(i).equals(t))
				return i;
		
		return -1;
	}
	private void removeChild(Transform t)
	{
		int i = indexOfChild(t);
		if(i!=-1)
			childs.remove(i);
	}
	public boolean hasParent()
	{
		if(parent == null)return false;
		else return true;
	}
	public boolean hasChilds()
	{
		if(childs == null)return false;
		else 
			if(childs.size()==0)return false;
			else return true;
	}
	public boolean hasParent(Transform t)
	{
		if(parent.equals(t))return true;
		return false;
	}
	public boolean hasChild(Transform t)
	{
		for(Transform c:childs)
			if(c.equals(t))return true;
		return false;
	}
	public boolean hasMesh()
	{
		if(mesh==null)return false;
		else return true;
	}
	public Vector3 forward() throws MatrixException
	{
		return worldToLocal(Vector3.forward());
	}
	public Vector3 backward() throws MatrixException
	{
		return forward().multiply(-1);
	}
	public Vector3 up() throws MatrixException
	{
		return worldToLocal(Vector3.up());
	}
	public Vector3 right() throws MatrixException
	{
		return worldToLocal(Vector3.right());
	}
	public Vector3 left() throws MatrixException
	{
		return right().multiply(-1);
	}
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass()==Transform.class)
		{
			Transform t = (Transform)obj;
			if(t.hashCode() == hashCode())return true;
			else return false;
		}
		else return false;
	}
	@Override
	public String toString() {
		String res = "[Position: "+position+",\n Local rotation: "+rotation+
					 ",\n ID: "+hashCode()+"]";
//		String res = "[ID: "+hashCode()+"]";
		return res;
	}
	public Vector3 getLocalRotation() {
		return localRotation.copy();
	}
}
