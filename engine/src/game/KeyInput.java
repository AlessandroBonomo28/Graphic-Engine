package game;

import java.awt.event.KeyAdapter;              
import java.awt.event.KeyEvent;                
public class KeyInput extends KeyAdapter {     
        Drawer game;
    public KeyInput(Drawer game){                
        this.game = game;
    }
    @Override
    public void keyPressed(KeyEvent e){        
        game.keyPressed(e);                    
    }
    @Override
    public void keyReleased(KeyEvent e){       
        game.keyReleased(e);                   
    }
    @Override
    public void keyTyped(KeyEvent e) {
	    game.keyTyped(e);
    }
}