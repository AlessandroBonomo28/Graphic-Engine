package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;


public class Drawer extends Canvas implements Runnable 
{
	private static final long serialVersionUID = 1L;
	

    // window title
    private static final String TITLE         = "BONOMO Graphic Engine";

    /**
     * Number of logical/physical updates per real second
     */
    private static final int    UPDATE_RATE   = 60;

    /**
     * Number of rendering buffers
     */
    private static final int    BUFFERS_COUNT = 3;

    /**
     * Value of a second in NanoSeconds DO NOT CHANGE!
     */
    private static final long   NANOS_IN_SEC  = 1000000000L;

    /**
     * Update interval in double precision NanoSeconds DO NOT CHANGE!
     */
    private static final double UPDATE_SCALE  = (double) NANOS_IN_SEC / UPDATE_RATE;
    
    
    // resolution
    private int WIDTH;
	private int HEIGHT;
    private Color bgColor = Color.black;
    
    private JFrame window;
    protected BufferedImage img;
    private Thread gameThread;
    private boolean running;
    
	private KeyInput keyInput;
	
	protected double deltaTime=0;
    
    ////////////////

    public Drawer(JFrame window,int WIDTH,int HEIGHT,Color bgColor) 
    {
    	
        this.window = window;
        this.running = false;
        
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.bgColor = bgColor;
        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // properly ends the game by calling stop when window is closed
        this.window.addWindowListener((WindowListener) new WindowAdapter() 
        {
            public void windowClosing(WindowEvent e) 
            {
                stop();
                super.windowClosing(e);
                System.exit(0);
            }
        });
        
        keyInput = new KeyInput(this);
        addKeyListener(keyInput);
        
        this.window.getContentPane().add(this);
        this.window.setResizable(false);
        this.window.pack();
        this.window.setLocationRelativeTo(null);
        this.window.setVisible(true);
    }
    // starts the game
    public synchronized void start() 
    {
        if (running)
            return;

        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        onStart();
    }

    // ends the game
    public synchronized void stop() {
        if (!running)
            return;

        running = false;
        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
                System.out.println("Game thread stopped");
            } catch (InterruptedException e) {
                System.out.println("Failed sopping game thread, retry in 1 second");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    protected void onStart()
    {
    	
    }
    
    protected void update() 
    {
    	
    }
    protected void draw(Graphics2D g)
    {
    	
    }
    private void render() 
    {
    	
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(BUFFERS_COUNT);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) bs.getDrawGraphics().create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        clear(g2d);
        // render here
        draw(g2d);
        //g2d.drawImage(img, 0,0,this);
        
        //////////////
        
        g2d.dispose();

        bs.show();
    }

    private void clear(Graphics2D g2d) 
    {
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
    }

    // game loop thread
    public void run() 
    {
        long startTime = System.currentTimeMillis();
        long tick = 1000;

        int upd = 0;
        int fps = 0;

        double updDelta = 0;

        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            updDelta += (now - lastTime) / UPDATE_SCALE;
            deltaTime = (now-lastTime) * 1e-9; // converto in secondi
            lastTime = now;

            while (updDelta > 1) {
                update();
                upd++;
                updDelta--;
            }
            render();
            fps++;
            // aggiorna il titolo ogni tot
            if (System.currentTimeMillis() - startTime > tick) {
                window.setTitle(TITLE + " || Upd: " + upd + " | Fps: " + fps);
                upd = 0;
                fps = 0;
                tick += 1000;
            }

            try {
                Thread.sleep(5); // always a good idea to let is breath a bit
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
	protected void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	protected void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	protected void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public int getWidth() {
		return WIDTH;
	}
	public int getHeight() {
		return HEIGHT;
	}
}