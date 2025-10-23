package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
public class Audio {
	private static Audio instance;
	private Clip activeMusicClip;
	public ArrayList<Clip> activeSoundClips = new ArrayList<Clip>();
	private int maxActiveSoundClips=5;
	public Audio() {
		//TODO 
	}
	public static Audio getInstance()
	{
		if(instance==null)instance = new Audio();
		return instance;
	}
	public Clip playMusic(String filePath,float volume)
	{
		Clip c=null;
		try {
			if(activeMusicClip!=null)activeMusicClip.stop();
			File musicFile = new File(filePath);
			if(musicFile.exists())
			{
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.start();
				c=clip;
				setClipVolume(clip,volume);
				activeMusicClip = clip;
			}
			else throw new FileNotFoundException();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	public Clip playMusicLoop(String filePath,float volume)
	{
		Clip c=null;
		try {
			if(activeMusicClip!=null)activeMusicClip.stop();
			File musicFile = new File(filePath);
			if(musicFile.exists())
			{
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.start();
				c=clip;
				setClipVolume(clip,volume);
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				activeMusicClip = clip;
				
			}
			else throw new FileNotFoundException();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	public Clip playSound(String filePath)
	{
		Clip c=null;
		try {
			
			File musicFile = new File(filePath);
			if(musicFile.exists())
			{
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.start();
				c=clip;
				addToActiveSounds(clip);
				new java.util.Timer().schedule( 
				        new java.util.TimerTask() {
				            @Override
				            public void run() {
				                // your code here
				            	removeAndStopSoundClip(clip);
				            }
				        }, 
				        (long) (clip.getMicrosecondLength() *0.001)
				);
				
			}
			else throw new FileNotFoundException();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	public Clip playSoundLoop(String filePath)
	{
		Clip c = null;
		try {
			
			File musicFile = new File(filePath);
			if(musicFile.exists())
			{
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				clip.start();
				c=clip;
				addToActiveSounds(clip);
				
				
			}
			else throw new FileNotFoundException();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	public void playExistingClip(Clip c)
	{
		int index = activeSoundClips.indexOf(c);
		if(index!=-1)
		{
			activeSoundClips.get(index).start();
		}
	}
	public void playExistingClipLoop(Clip c)
	{
		int index = activeSoundClips.indexOf(c);
		if(index!=-1)
		{
			Clip clip = activeSoundClips.get(index);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
		}
	}
	public void stopExistingClip(Clip c)
	{
		int index = activeSoundClips.indexOf(c);
		if(index!=-1)
		{
			activeSoundClips.get(index).stop();
		}
	}
	private void addToActiveSounds(Clip clip)
	{
		if(activeSoundClips.indexOf(clip)==-1)
		{
			if((activeSoundClips.size()+1)>maxActiveSoundClips)
				removeAndStopSoundClip(activeSoundClips.get(0));
			activeSoundClips.add(clip);
		}
			
	}
	private void removeAndStopSoundClip(Clip clip)
	{
		int index = activeSoundClips.indexOf(clip);
		if(index!=-1)
		{
			activeSoundClips.get(index).stop();
			activeSoundClips.remove(index);
		}
	}
	private double clamp(double val, double min, double max) {
	    return Math.max(min, Math.min(max, val));
	}
	private void setClipVolume(Clip c,float vol)
	{
		if(c==null)
		{
			System.out.println("null clip");
			return;
		}
		vol = (float) clamp(vol,0,1);
		FloatControl gainControl = 
			    (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
		float range = gainControl.getMaximum() - gainControl.getMinimum();
		float gain = (range * vol) + gainControl.getMinimum();
		gainControl.setValue(gain);
		
	}
}
