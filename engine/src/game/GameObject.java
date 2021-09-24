package game;

import game.demos.MainGame_VaporWave;
import geometry.Transform;

public class GameObject {
	protected Transform transform;
	protected boolean active=true;
	public GameObject(Transform t) {
		transform = t;
	}
	public void onStart()
	{
		
	}
	public void update()
	{
		
	}
	public void onDestroy()
	{
		
	}
	
	public Transform getTransform() {
		return transform;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
		if(active) MainGame_VaporWave.getInstance().addToDrawList(transform);
		else MainGame_VaporWave.getInstance().removeFromDrawList(transform);
	}
	
}
