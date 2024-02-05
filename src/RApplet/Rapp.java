package RApplet;

import java.util.ArrayList;

//import themidibus.*;

import RBase.InputData;
import RBase.RConst;
import Specialize.Canvas;
import Specialize.Face;
import Specialize.FlocComu;
import Specialize.GrowerComu;
import Specialize.Organism;
import Specialize.Simulation;
import UI.nWidget;
import processing.core.*;
import processing.event.MouseEvent;
import sData.nRunnable;


// Library : The MidiBus 8 Severin Smith http://smallbutdigital.com/projects/themidibus/


/*

todo : see top of Macro_Sheet  MacSh.pde
  double click
  right click on widget

PApplet
  Log
    DEBUG_SAVE
    DEBUG_SCREEN_INFO
    log(string)
    logln()

  void setup()
    Interface
    Simulation(Interface)
    Prints

  void draw()
    Interface.frame  >  frame events, drawing
*/

public class Rapp extends PApplet implements InputData, RConst {
	
//	public static void main(String[] args) {
//		PApplet.main("RApplet.Rapp");
//	}
	
	sInterface interf;
	
	public void settings() {
		fullScreen();
		noSmooth();
//		  smooth();//anti aliasing
	}
	
	public void setup() {
	  //size(1600, 900);//taille de l'ecran
		  surface.setResizable(true);
		
		frameRate(60);
//		rectMode(PApplet.CENTER);
//		ellipseMode(PApplet.RADIUS);
		
//		blendMode(REPLACE);

		  interf = new sInterface(this, 40);
		  
		init();
		
		background(0);//fond noir
		  
	    
	    interf.addEventNextFrame(new nRunnable() { 
	      public void run() { interf.addEventNextFrame(new nRunnable() { 
	        public void run() { interf.setup_load(); } } ); } } );

		  app_grab = new nWidget(interf.screen_gui, "", 28, 0, 0, base_width - 40, 40)
		    .setTrigger()
		    .addEventTrigger(new nRunnable() { public void run() { mx = (int)mouseX; my = (int)mouseY; } } )
		    .addEventPressed(new nRunnable() { public void run() { 
		      sx = (int)((int)mouseX + sx - mx);
		      sy = (int)((int)mouseY + sy - my);
		      surface.setLocation(sx, sy); 
		    } } );
		  app_close = new nWidget(interf.screen_gui, "X", 28, base_width - 40, 0, 40, 40)
		    .setTrigger()
		    .addEventTrigger(new nRunnable() { public void run() { 
		      logln("exit");
		      if (save_log_exit) savelog(); 
		      interf.addEventTwoFrame(new nRunnable() { 
		        public void run() { exit(); } } ); } } );
		  
		  
		  interf.full_screen_run.run();
		  interf.full_screen_run.run();
		  interf.full_screen_run.run();
		  surface.setLocation(100, 5);
	}

	public void init() {//executé au demarage
		  
		  logln("setup");
		  
//		  setup_midi();
		  
		  
		  
		  Simulation simul = (Simulation)interf.addUniqueSheet(new Simulation.SimPrint());
		  Canvas canv = (Canvas) interf.addUniqueSheet(new Canvas.CanvasPrint(simul));
		  interf.addSpecializedSheet(new Face.FacePrint(canv));
		  interf.addSpecializedSheet(new Organism.OrganismPrint(simul));
		  interf.addSpecializedSheet(new GrowerComu.GrowerPrint(simul));
		  interf.addSpecializedSheet(new FlocComu.FlocPrint(simul));
		  //interf.addSpecializedSheet(new BoxPrint(simul));
		  
		  
		  //logln("end models: "+interf.gui_theme.models.size());
		  
		  
		  logln("setup end");
		}

	nWidget app_grab, app_close;
	public float window_head = 40;
	float mx, my;
	int sx, sy;
	boolean fullscreen=true;
	public int base_width=1400; //non fullscreen width
	public int base_height=800; //non fullscreen height
	
	void fs_switch() {
	  if (fullscreen) {
	    app_grab.show();
	    app_close.show();
	    surface.setSize(base_width,base_height); 
	    surface.setLocation(100, 5);
	    sx = 100; sy = 5;
	    fullscreen=false;
	    surface.setAlwaysOnTop(false);
	  } else {
	    app_grab.hide();
	    app_close.hide();
	    surface.setSize(displayWidth, displayHeight + (int)(window_head));
	    fullscreen=true;
	    surface.setLocation(0, (int) -(window_head));
	    sx = 0; sy = (int) -(window_head);
	    surface.setAlwaysOnTop(true);
	  }
	}
	
	public void draw() {
	  interf.frame();
	  global_frame_count++;
	  if (global_frame_count < 5) { fill(0); noStroke(); rect(0, 0, width, height); }
	  
	  show();
	}
	
	public void show() {}
	
	public void mouseWheel(MouseEvent event) { 
	  interf.input.mouseWheelEvent(event);
	}  
	public void keyPressed() { 
	  interf.input.keyPressedEvent();
	}  
	public void keyReleased() { 
	  interf.input.keyReleasedEvent();
	}
	public void mousePressed() { 
	  interf.input.mousePressedEvent();
	}
	public void mouseReleased() { 
	  interf.input.mouseReleasedEvent();
	}
	public void mouseDragged() { 
	  //interf.input.mouseDraggedEvent();
	}
	public void mouseMoved() { 
	  //interf.input.mouseMovedEvent();
	}
	
	    //#######################################################################
		//##                         METHODES UTILES                           ##
		//#######################################################################
		
		
		

	public float crandom(float d) { return pow(random((float) 1.0), d); }
		
		// auto indexing
		int used_index = 0;
		int get_free_id() { used_index++; return used_index - 1; }
		
		// gestion des polices de caractére
		ArrayList<myFont> existingFont = new ArrayList<myFont>();
		class myFont { PFont f; int st; }
		public PFont getFont(int st) {
		  st = (int)(st / 2) * 2;
		  if (st > 40) st = 40;
		  if (st < 6) st = 6;
		  for (myFont f : existingFont) if (f.st == st) return f.f;
		  myFont f = new myFont();
		  f.f = createFont("Arial",st); f.st = st;
		  return f.f; }
		//for (String s : PFont.list()) println(s); // liste toute les police de text qui existe
		
		
	    //#######################################################################
		//##                             LOGGING                               ##
		//#######################################################################
		
		
		

		boolean DEBUG_HOVERPILE = false;
		public boolean DEBUG_NOFILL = false;
		
		boolean DEBUG_MACRO = false;
		
		boolean DEBUG = true;
		
		int global_frame_count = 0;
		ArrayList<String> logs = new ArrayList<String>();
		String current_log = "";
		boolean console_log = true, save_log_exit = false, save_log_all = false;
		String logpath = "log.txt";
		String[] loglist;
		void savelog() {
		  if (loglist == null || loglist.length < logs.size()) loglist = new String[logs.size() + 500]; 
		  int i = 0;
		  for (String s : logs) { loglist[i] = RConst.copy(s); i++; }
		  saveStrings(logpath, loglist);
		}
		public void og(String s) {
		  if (console_log && DEBUG && current_log.length() == 0) print(global_frame_count+"::");
		  if (console_log && DEBUG) print(s);
		  if (current_log.length() == 0) current_log += global_frame_count+"::";
		  current_log += s;
		}
		public void logln(String s) {
		  if (console_log && DEBUG && current_log.length() == 0) print(global_frame_count+"::");
		  if (console_log && DEBUG) println(s);
		  if (current_log.length() == 0) current_log += global_frame_count+"::";
		  current_log += s;
		  logs.add(RConst.copy(current_log));
		  current_log = "";
		  if (save_log_all) savelog();
		}
		public void mlog(String s) {
		  if (console_log && DEBUG_MACRO && current_log.length() == 0) print(global_frame_count+":M:");
		  if (console_log && DEBUG_MACRO) print(s);
		  if (current_log.length() == 0 && DEBUG_MACRO) current_log += global_frame_count+"::";
		  if (DEBUG_MACRO) current_log += s;
		}
		public void mlogln(String s) {
		  if (console_log && DEBUG_MACRO && current_log.length() == 0) print(global_frame_count+":M:");
		  if (console_log && DEBUG_MACRO) println(s);
		  if (current_log.length() == 0 && DEBUG_MACRO) current_log += global_frame_count+":M:";
		  if (DEBUG_MACRO) current_log += s;
		  logs.add(RConst.copy(current_log));
		  current_log = "";
		  if (save_log_all) savelog();
		}
		
		boolean DEBUG_SAVE_FULL = false;
		public void slog(String s) {
		  if (console_log && DEBUG_SAVE_FULL) print(s);
		  if (DEBUG_SAVE_FULL) current_log += s;
		}
		public void slogln(String s) {
		  if (console_log && DEBUG_SAVE_FULL) println(s+":S:"+global_frame_count);
		  if (DEBUG_SAVE_FULL) current_log += s+":S:"+global_frame_count;
		  logs.add(RConst.copy(current_log));
		  current_log = "";
		  if (save_log_all) savelog();
		}
		
		

		boolean DEBUG_SVALUE = false;
		void vlog(String s) {
		//  if (DEBUG_SVALUE) print(s);
		}
		public void vlogln(String s) {
		//  if (DEBUG_SVALUE) println(s);
		}
		

		boolean DEBUG_DATA = false;
		public void dlog(String s) {
		//  if (DEBUG_DATA) print(s);
		}
		public void dlogln(String s) {
		//  if (DEBUG_DATA) println(s);
		}


		//#######################################################################
		//##                             MIDIBus                               ##
		//#######################################################################
		
//		MidiBus midiBus;
	//	
//		void setup_midi() {
//		  //MidiBus.list(); // List all available Midi devices on STDOUT. This will show each device's index and name.
	//	
//		  // Either you can
//		  //                   Parent In Out
//		  //                     |    |  |
//		  //midiBus = new MidiBus(this, 0, 1); // Create a new MidiBus using the device index to select the Midi input and output devices respectively.
	//	
//		  // or you can ...
//		  //                   Parent         In                   Out
//		  //                     |            |                     |
//		  //midiBus = new MidiBus(this, "IncomingDeviceName", "OutgoingDeviceName"); // Create a new MidiBus using the device names to select the Midi input and output devices respectively.
	//	
//		  // or for testing you could ...
//		  //                   Parent  In        Out
//		  //                     |     |          |
//		  //midiBus = new MidiBus(this, -1, "Java Sound Synthesizer"); // Create a new MidiBus with no input device and the default Java Sound Synthesizer as the output device.
//		  
//		}
//		void noteOn(int channel, int pitch, int velocity) {
//		  // Receive a noteOn
//		  //println();
//		  //println("Note On:");
//		  //println("--------");
//		  //println("Channel:"+channel);
//		  //println("Pitch:"+pitch);
//		  //println("Velocity:"+velocity);
//		  interf.macro_main.noteOn(channel, pitch, velocity);
//		}
	//	
//		void noteOff(int channel, int pitch, int velocity) {
//		  // Receive a noteOff
//		  //println();
//		  //println("Note Off:");
//		  //println("--------");
//		  //println("Channel:"+channel);
//		  //println("Pitch:"+pitch);
//		  //println("Velocity:"+velocity);
//		  interf.macro_main.noteOff(channel, pitch, velocity);
//		}
	//	
//		void controllerChange(int channel, int number, int value) {
//		  // Receive a controllerChange
//		  //println();
//		  //println("Controller Change:");
//		  //println("--------");
//		  //println("Channel:"+channel);
//		  //println("Number:"+number);
//		  //println("Value:"+value);
//		  interf.macro_main.controllerChange(channel, number, value);
//		}
}
