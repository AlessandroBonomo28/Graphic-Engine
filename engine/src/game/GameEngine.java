package game;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;

import geometry.Transform;
import mathUtils.Vector3;
import mathUtils.matrixExceptions.MatrixException;

public class GameEngine extends GraphicEngine {
	private static final long serialVersionUID = 1L;

	private static GameEngine instance;
	private ArrayList<GameObject> instantiatedGameObjects = new ArrayList<GameObject>();
	private ArrayList<GameObject> toDestroy = new ArrayList<GameObject>();
	private boolean destroyListClear = true;
	
	public GameEngine(JFrame window, int widthWindow, int heightWindow, double FOV, Color bgColor)
			throws MatrixException {
		super(window, widthWindow, heightWindow, FOV, bgColor);
		instance = this;
		// TODO Auto-generated constructor stub
	}

	public GameEngine(JFrame window, int widthWindow, int heightWindow, Color bgColor) throws MatrixException {
		super(window, widthWindow, heightWindow, bgColor);
		instance = this;
		// TODO Auto-generated constructor stub
	}

	public GameEngine(JFrame window, int widthWindow, int heightWindow) throws MatrixException {
		super(window, widthWindow, heightWindow);
		instance = this;
		// TODO Auto-generated constructor stub
	}
	
	public static GameEngine getInstance() {
		return instance;
	}
	public void instantiate(GameObject go, Vector3 position, Vector3 rotation) throws MatrixException
	{
		instantiatedGameObjects.add(go);
		Transform t = go.getTransform();
		t.setPosition(position);
		t.setRotation(rotation);
		if(go.isActive())
			addToDrawList(t);
	}
	public void destroy(GameObject go)
	{
		go.onDestroy();
		toDestroy.add(go);
		destroyListClear =false;
	}
	
	private void destroyGameObjectsInDestroyList()
	{
		for(GameObject go :toDestroy)
		{
			int index = instantiatedGameObjects.indexOf(go);
			if(index!=-1)
				instantiatedGameObjects.remove(index);
			Transform t = go.getTransform();
			removeFromDrawList(t);
		}
		
	}
	private void clearDestroyList()
	{
		toDestroy = new ArrayList<GameObject>();
		destroyListClear = true;
	}
	private void destroyRoutine()
	{
		if(!destroyListClear)
		{
			destroyGameObjectsInDestroyList();
			clearDestroyList();
		}
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		@SuppressWarnings("unchecked")
		ArrayList<GameObject> tmp = (ArrayList<GameObject>) instantiatedGameObjects.clone();
		for(GameObject go:tmp)
			go.onStart();
		
		
		destroyRoutine();
	}
	@Override
	protected void update() {
		// TODO Auto-generated method stub
		super.update();
		@SuppressWarnings("unchecked")
		ArrayList<GameObject> tmp = (ArrayList<GameObject>) instantiatedGameObjects.clone();
		for(GameObject go:tmp)
			go.update();
		
		destroyRoutine();
	}
}
