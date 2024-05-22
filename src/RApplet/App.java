package RApplet;

import UI.Rect;
import UI.nGUI;
import UI.nTheme;
import UI.nWidget;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class App extends PApplet implements RConst {
	
	public static void main(String[] args) {
		PApplet.main("RApplet.App");
	}
	
	public void settings() {
//		  size(1600, 900);//taille de l'ecran
		fullScreen();
		noSmooth();
//		  smooth();//anti aliasing
	}
	

	  public sInput input;
	  
	  public nTheme gui_theme;
	  public nGUI screen_gui;
	  public float ref_size;
	  public Rect screen_view;
	
	public void setup() {
		surface.setResizable(true);
		
		frameRate(60);
		textSize(18);
		
		background(0);//fond noir
		
//		interf = new sInterface(this, 40);
	    ref_size = 40;
		
//		input = new sInput(this);
//		gui_theme = new nTheme(this, ref_size);
//	    screen_gui = new nGUI(input, gui_theme, ref_size);
//	    screen_view = new Rect(this.screen_0_x, this.screen_0_y, 
//	    		this.screen_width, this.screen_height);
//	    screen_gui.setView(screen_view);
		
//		  app_grab = new nWidget(interf.screen_gui, "", 28, 0, 0.1F, base_width - 40, 40)
//		    .setTrigger()
//		    .addEventTrigger(new nRunnable() { public void run() { mx = mouseX; my = mouseY; } } )
//		    .addEventPressed(new nRunnable() { public void run() { 
//		      sx = (mouseX + sx - mx); sx = sx - sx%1;
//		      sy = (mouseY + sy - my); sy = sy - sy%1;
//		      surface.setLocation((int)sx, (int)sy); 
//		    } } );
//		  app_close = new nWidget(interf.screen_gui, "X", 28, base_width - 40, 0.1F, 40, 40)
//		    .setTrigger()
//		    .addEventTrigger(new nRunnable() { public void run() { 
//		      interf.addEventTwoFrame(new nRunnable() { 
//		        public void run() { exit(); } } ); } } );
//		  
//
//		  interf.full_screen_run.run();
//		  interf.full_screen_run.run();
//		  
//		  if (START_AS_WINDOW) {
//			  interf.full_screen_run.run();
//			  interf.addEventNextFrame(new nRunnable() { public void run() { 
//				  surface.setLocation(100, 50); } } );
//		  }
		fs_switch();
		
	}
	
	nWidget app_grab, app_close;
	public float window_head = 40;
	float mx, my;
	float sx, sy;
	boolean fullscreen=true;
	public int base_width=1400; //non fullscreen width
	public int base_height=800; //non fullscreen height
	public int screen_0_x, screen_0_y, screen_width, screen_height;
	void fs_switch() {
	  if (fullscreen) {
//	    app_grab.show();
//	    app_close.show();
	    surface.setSize(base_width,base_height + (int)window_head); 
	    surface.setLocation(100, 50);
	    sx = 100; sy = 50;
	    fullscreen=false;
	    surface.setAlwaysOnTop(false);
	    screen_0_x = 0; screen_0_y = (int)window_head; 
	    screen_width = base_width; screen_height = base_height;
	  } else {
//	    app_grab.hide();
//	    app_close.hide();
	    surface.setSize(displayWidth, displayHeight + (int)(window_head));
	    fullscreen=true;
	    surface.setLocation(0, (int) -(window_head));
	    sx = 0; sy = (int) -(window_head);
	    surface.setAlwaysOnTop(false);
	    screen_0_x = 0; screen_0_y = (int)window_head; 
	    screen_width = displayWidth; screen_height = displayHeight;
	  }
	}
	
	public void draw() {
//	  interf.frame();
	}
	

	
	public void mouseWheel(MouseEvent event) { 
//	  interf.input.mouseWheelEvent(event);
	}  
	public void keyPressed() { 
//	  interf.input.keyPressedEvent();
	}  
	public void keyReleased() { 
//	  interf.input.keyReleasedEvent();
	}
	public void mousePressed() { 
//	  interf.input.mousePressedEvent();
	}
	public void mouseReleased() { 
//	  interf.input.mouseReleasedEvent();
	}
	public void mouseDragged() { 
	  //interf.input.mouseDraggedEvent();
	}
	public void mouseMoved() { 
	  //interf.input.mouseMovedEvent();
	}
	
}
