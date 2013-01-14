package applets;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import belette.IBelette;
import bots.*;
import utils.*;
import surface.*;

/**
 * Very simple applet to handle fundamentals of A.I. in games.
 * 
 * This is the main applet, it handles a "battle field" with objects on it,
 * Every frames the whole field is paint again, there is a (simple) GUI.
 * 
 * How it works? After initialization of Surface, Bots, ... the 
 * applet enters (in run()) an infinite loop that... just sleep to wait for next frame,
 * and then call updateBots(), then updateBelettes() then repaint().
 * The first and second calls update positions and animations of bots and bullets,
 * the last one simple repaint all the field from scratch.
 * 
 * You may want to extend this applet using openGL like JOGL in order to enter the third dimension
 * A very simple entry for this would be for instance http://jsolutions.se/DukeBeanEm
 * 
 * @author L. Simon, Univ. Paris Sud, 2008
 *
 */

public class BattleField extends Applet
    implements Runnable, MouseListener, MouseMotionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Surface surface; // The surface that contains the objects...

	public static ArrayList<IBot> redTeam;
	public static  ArrayList<IBot> blueTeam;
	public static ArrayList<IBelette> bullets;
	public static final int nbTeam = 2;
	public static final int BLUE = 0;
	public static final int RED = 1;
	public static ArrayList<IBot>[] teams = new ArrayList[nbTeam];
	
	// Those constants are hard constants... Why? I don't know.
	static final public float MAXX = 10000F; // Size of the battlefield, in float (not pixels)
	static final public float MAXY = 7500F;
	
	static final public int PREF_VIEWER_XSIZE = 800; // size in pixels (in x, the y is automatically deduced)

	// Viewer variables
    float viewer_scale; // Ratio from size of surface to size of viewer
    int viewer_xsize;
    int viewer_ysize;

    // Canvas for double buffering
    Image buffer_canvasimage;
    Graphics buffer_canvas; // Where to draw (off-screen buffer)
    Graphics viewer_canvas; // What the user actually see (on-screen buffer)
    

    /**
     * Thread that sleeps and update the screen.
     */
    private Thread update;

    
    // Very simple constructor
    public BattleField()
    {
        viewer_scale = MAXX/PREF_VIEWER_XSIZE;
    }
    
 	public void init()
    {
        super.init();
        
        viewer_xsize = PREF_VIEWER_XSIZE; // size in pixels
        viewer_ysize = (int)(MAXY/viewer_scale); // The y axe is automatically computed
        
        resize(viewer_xsize, viewer_ysize);
        buffer_canvasimage = createImage(viewer_xsize, viewer_ysize);
        buffer_canvas = buffer_canvasimage.getGraphics();
        viewer_canvas = this.getGraphics();
        
        addMouseListener(this);
        addMouseMotionListener(this);

        initSurface();
        initBots();
        initBelettes();
    }


    /**
     * Called ones to init the surface. This is where
     * all objects attached to the surface should be loaded.
     * Dynamic objects like bots and bullet are handled elsewhere.
     */
    public void initSurface() {
        surface = new Surface(viewer_xsize,viewer_ysize,viewer_scale); 
        int nbPolyline = 6;
        Random r = new Random();
        for(int i = 0 ; i < nbPolyline; i++){
        	PolylineObject ob = new PolylineObject(new Vector2d(r.nextFloat() * surface.wxsize, r.nextFloat() * surface.wysize), surface);
        	float aireMax = viewer_xsize * viewer_ysize / 10;
        	float aireMin = viewer_xsize * viewer_ysize / 30;
        	
        	float aire =  r.nextFloat() * (aireMax - aireMin) + aireMin;
        			
        	float width = r.nextFloat() * aire / 100;
        	float height = aire / width;
        	Vector2d northOuest =  ob.globalCoordPoints.get(0);
        	Vector2d northEast =  new Vector2d(northOuest.x + width, northOuest.y);
        	Vector2d SouthOuest =  new Vector2d(northOuest.x, northOuest.y + height);
        	Vector2d southEast =  new Vector2d(northOuest.x + width, northOuest.y + height);
        	

    		ob.addNode(northEast);
    		ob.addNode(southEast);
    		ob.addNode(SouthOuest);
    		
        	
    		ob.fixObject();
        	
        	
//        	for(int j = 0 ; j < nbPoints ; j++){
//        		Vector2d previous = ob.globalCoordPoints.get(j);
//        		float xValue = -1.0f;
//        		while(xValue < 0){
//        			float signex =(r.nextBoolean())? -1.0f : 1.0f;
//        			xValue = r.nextFloat() * 70 * signex + previous.x;
//        		}
//        		float yValue = -1.0f;
//        		while(yValue < 0){
//        			float signey =(r.nextBoolean())? -1.0f : 1.0f;
//        			yValue = r.nextFloat() * 70 * signey + previous.y;
//        		}
//        		Vector2d next = new Vector2d(xValue, yValue);
//        		ob.fixObject();
//        		ob.addNode(next);
//        	}
    		
        	surface.getObjects().add(ob);
        }
//        PolylineObject ob1 = new PolylineObject(new Vector2d(100F,200F),surface);
//		ob1.addNode(new Vector2d(100F,250F));
//		ob1.addNode(new Vector2d(200F,250F));
//		ob1.addNode(new Vector2d(200F,200F));
//		ob1.fixObject();
//		surface.getObjects().add(ob1);
//
//		PolylineObject ob2 = new PolylineObject(new Vector2d(300F,500F),surface);
//		ob2.addNode(new Vector2d(300F,800F));
//		ob2.addNode(new Vector2d(320F,800F));
//		ob2.addNode(new Vector2d(320F,500F));
//		ob2.fixObject();
//		surface.getObjects().add(ob2);
//	
//		PolylineObject ob3 = new PolylineObject(new Vector2d(310F,400F),surface);
//		ob3.addNode(new Vector2d(310F,700F));
//		ob3.addNode(new Vector2d(330F,700F));
//		ob3.addNode(new Vector2d(330F,400F));
//		ob3.fixObject();
//		objects.add(ob3);
    }
    
    
    /**
     * Called ones to init all your bots.
     */
    public void initBots() {
    	blueTeam = new ArrayList<IBot>();
    	redTeam = new ArrayList<IBot>();
    	teams[RED] = redTeam;
    	teams[BLUE] = blueTeam;
    	Soldier s1= new Soldier(new Vector2d(300F,500F), 10F, surface, BLUE);
    	Soldier s2= new Soldier(new Vector2d(100F,100F), 10F, surface, RED);
    	blueTeam.add(s1);
    	redTeam.add(s2);
    }
    
    /**
     * Called ones to init all your belettes structures.
     */
    public void initBelettes() {
    	// TODO
    	bullets=new ArrayList<IBelette>();
    }
    
    public boolean handleEvent(Event event)
    {
        boolean returnValue = false;
        return returnValue;
    }

    public void start()
    {
        if(update == null)
        {
            update = new Thread(this);
            update.start();
        }
    }

    public void stop()
    {
        update = null;
    }

    /* 
     * This is the main loop of the game. Sleeping, then updating positions then redrawing
     * If you want a constant framerate, you should measure how much you'll have to sleep
     * depending on the time eated by updates functions.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        do
        {
        	updateBelettes();
            updateBots();
            repaint();
            try
            {
                Thread.sleep(33);
            }
            catch(InterruptedException _ex) { }
        } while(true);
    }

    // Use very simple double buffering technique...
    /**
     * This is a very simple double buffering technique.
     * Drawing are done offscreen, in the buffer_canvasimage canvas.
     * Ones all drawings are done, we copy the whole canvas to 
     * the actual viewed canvas, viewer_canvas.
     * Thus the player will only see a very fast update of its window.
     * No flickering.
     * 
     */
    private void showbuffer()
    {
        viewer_canvas.drawImage(buffer_canvasimage, 0, 0, this);
    }

    /* 
     * Called by repaint, to paint all the offscreen surface.
     * We erase everything, then redraw each components.
     * 
     * @see java.awt.Container#paint(java.awt.Graphics)
     */
    public void paint(Graphics g)
    {
    	float scale = 1.0F;
    	// 1. We erase everything
        buffer_canvas.setColor(Color.lightGray); // Background color
        buffer_canvas.fillRect(0, 0, viewer_xsize, viewer_ysize);
        
        // 2. We draw the surface (and its objects)
        surface.draw(buffer_canvas);
        buffer_canvas.setColor(Color.black);
        buffer_canvas.drawRect(0, 0, viewer_xsize - 1, viewer_ysize - 1);
        
        // 3. TODO: Draw the bots in their position/direction
        for(IBot i: blueTeam){
        	i.draw(buffer_canvas, scale);
        }
        for(IBot i: redTeam){
        	i.draw(buffer_canvas, scale);
        }
        // 4. TODO: Draw the bullets / Special Effects.
        for(IBelette b : bullets){
        	b.draw(buffer_canvas);
        }

        
        // Draws the line for the demo.
        // TODO: you should delete this...
        /*
        if ( (pointA.x > -1) && (pointB.x > -1) ) {
			gui_string = "Il va falloir modifier tout cela pour en faire un jeu... [";
        	if (surface.cansee(pointA, pointB)) {
        		buffer_canvas.setColor(Color.green);
        		gui_string += "A voit B";
        	} else {
        		buffer_canvas.setColor(Color.red);
        		gui_string += "A ne voit pas B";
        	}
        	gui_string +="]";
            buffer_canvas.drawLine((int)pointA.x, (int)pointA.y, (int)pointB.x, (int)pointB.y);
        }
        */
        drawHUD();
        showbuffer();
    }

    
    /**
     * string printed in the simple hud. For debugging...
     */
    String gui_string = "";
    /**
     * Very simple GUI.. Just print the infos string on the bottom of the screen, in a rectangle.
     */
    private void drawHUD() {
    	buffer_canvas.setColor(Color.red);
    	buffer_canvas.drawRect(20,viewer_ysize-23,viewer_xsize-41,20);
    	buffer_canvas.drawChars(gui_string.toCharArray(), 0, Math.min(80,gui_string.length()), 22, viewer_ysize-7);
    }
    
    
    /**
     * Must update bullets positions and handles damages to bots...
     */
    public void updateBelettes() {
    	// TODO: nothing here yet
    	for(IBelette b : bullets){
    		b.computeNextFrame();
    	}
    }
    
    /**
     * Must update bots position / decisions / health
     * This is where your AI will be called.
     * 
     */
    public void updateBots()
    {
    	// TODO: You have to update all your bots here.
    	for(IBot i : blueTeam){
//    		i.setDestination(s_dest);
    		i.AI();
    		i.updatePosition();
    	}
    	for(IBot i : redTeam){
//    		i.setDestination(s_dest);
    		i.AI();
    		i.updatePosition();
    	}
   }

  
    // Simply repaint the battle field... Called every frame...
    public void update(Graphics g)
    {
        paint(g);
    }

    
    
    public static void main(String args[])
    {
        Frame f = new Frame();
        BattleField app = new BattleField();
        app.init();
        app.start();
        f.add("Center", app);
    }


    // Two point2D to memorize mouse gestures (pointA first click, pointB second click)
    private Vector2d pointA = new Vector2d(-1,-1);
    private Vector2d pointB = new Vector2d(-1,-1);
    private Vector2d s_dest =  null;
    // Those methods have to be there... Even if they are empty.
	public void mouseClicked(MouseEvent e) {
		s_dest = new Vector2d(e.getX(),e.getY());
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}

	
	/* Here we memorize the mouse position to draw lines where points can see eachother.
	 * TODO: you must handle mouse events in your game.
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		pointA.x = e.getX();
		pointA.y = e.getY();		
	}

	/* TODO: use this method your own way.
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		if (pointA.x > -1) { // pointA has been defined
			pointB.x = e.getX();
			pointB.y = e.getY();
		}
	}

}
