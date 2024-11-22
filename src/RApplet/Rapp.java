package RApplet;

import java.util.ArrayList;

import com.hamoid.VideoExport;

//import themidibus.*;

import UI.nWidget;
import processing.core.*;
import processing.event.MouseEvent;
import ddf.minim.*;
import ddf.minim.analysis.FFT;
import sData.nRunnable;


// Library : The MidiBus 8 Severin Smith http://smallbutdigital.com/projects/themidibus/


/*

todo : see top of Macro_Sheet

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

public class Rapp extends PApplet implements RConst {
	
//	public static void main(String[] args) {
//		PApplet.main("RApplet.Rapp");
//	}
	
	boolean START_AS_WINDOW = true;
	public boolean USE_SHADERS = true;
	
	sInterface interf;
	
	public void settings() {
//		  if (USE_SHADERS) size(1600, 900, P2D);
//		  else size(1600, 900);
		  if (USE_SHADERS) fullScreen(P2D);
		  else fullScreen();
//		fullScreen(P2D);
//		noSmooth();
		  smooth(2);//anti aliasing
	}
	
	public void setup() {
		
		  surface.setResizable(true);
		
//		frameRate(60); // done later
		textSize(18);
//		
		background(0);//fond noir
		
//		rectMode(PApplet.CENTER);
//		ellipseMode(PApplet.RADIUS);
//		blendMode(REPLACE);
		

		  logln("setup");
		
		  interf = new sInterface(this, 40);

		  
//		  setup_midi();
		  
		  

//		  Simulation simul = (Simulation)interf.addUniqueSheet(new Simulation.SimPrint());
//		  Canvas canv = (Canvas) interf.addUniqueSheet(new Canvas.CanvasPrint(simul));
//		  interf.addSpecializedSheet(new Face.FacePrint(canv));
//		  interf.addSpecializedSheet(new Organism.OrganismPrint(simul));
//		  interf.addSpecializedSheet(new GrowerComu.GrowerPrint(simul));
//		  interf.addSpecializedSheet(new FlocComu.FlocPrint(simul));
//		  interf.addSpecializedSheet(new FlocComu.FlocPrint(simul, canv));
//		  interf.addSpecializedSheet(new SolidComu.SolidPrint(simul, canv));
//		  interf.addSpecializedSheet(new BoxComu.BoxPrint(simul));
//		  interf.addSpecializedSheet(new Grid.GridPrint(simul));
		  
		  
		  //logln("end models: "+interf.gui_theme.models.size());
		  
		  
	    
	    
	    
		  app_grab = new nWidget(interf.screen_gui, "", 28, 0, 0.1F, base_width - 40, 40)
		    .setTrigger()
		    .addEventTrigger(new nRunnable() { public void run() { mx = mouseX; my = mouseY; } } )
		    .addEventPressed(new nRunnable() { public void run() { 
		      sx = (mouseX + sx - mx); sx = sx - sx%1;
		      sy = (mouseY + sy - my); sy = sy - sy%1;
		      surface.setLocation((int)sx, (int)sy); 
		    } } );
		  app_close = new nWidget(interf.screen_gui, "X", 28, base_width - 40, 0.1F, 40, 40)
		    .setTrigger()
	        .setStandbyColor(color(175, 20, 20))
	        .setHoveredColor(color(200, 100, 100))
	        .setOutlineColor(color(125, 10, 10))
		    .addEventTrigger(new nRunnable() { public void run() { 
		      logln("exit");
		      if (save_log_exit) savelog(); 
		      interf.addEventTwoFrame(new nRunnable() { 
		        public void run() { exit(); } } ); } } );
		  
		  media_setup();
		  
		  logln("setup end");
	}
	
	public boolean BLACKOUT = false;
	
	int setup_tmp = 2;
	public void draw() {

	  global_frame_count++;

	  if (global_frame_count > setup_tmp+1) {
		  interf.frame();
	  }
	  if (global_frame_count == setup_tmp) {
		  if (!START_AS_WINDOW) fullscreen = false;
		  interf.full_screen_run.run();
	  }
	  else if (global_frame_count == setup_tmp+2) {
		  if (START_AS_WINDOW) surface.setLocation(100, 50); 
		  frameRate(60); 
	  	  interf.setup_load();
	  }
	  
	  if (global_frame_count < setup_tmp+2) { 
		  fill(0); noStroke(); rect(0, 0, width, height); 
		  fill(255); 
		  text("Starting ...", width/2, height/2);
	  }
	  if (BLACKOUT) { 
		  fill(0); noStroke(); rect(0, window_head, width, height-window_head); 
	      fill(255); 
		  text("Loading ...", width/2, height/2);
	  }
	  if (do_one_save) { do_one_save = false; savelog(); }
		logln_this_frame = false;
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
	    app_grab.show();
	    app_close.show();
	    surface.setSize(base_width,base_height + (int)window_head); 
	    surface.setLocation(100, 50);
	    sx = 100; sy = 50;
	    fullscreen=false;
	    surface.setAlwaysOnTop(false);
	    screen_0_x = 0; screen_0_y = (int)window_head; 
	    screen_width = base_width; screen_height = base_height;
	  } else {
	    app_grab.hide();
	    app_close.hide();
	    surface.setSize(displayWidth, displayHeight + (int)(window_head));
	    fullscreen=true;
	    surface.setLocation(0, (int) -(window_head));
	    sx = 0; sy = (int) -(window_head);
	    surface.setAlwaysOnTop(false);
	    screen_0_x = 0; screen_0_y = (int)window_head; 
	    screen_width = displayWidth; screen_height = displayHeight;
	  }
	}
	
	

	public void breakpnt() { 
  		logln("breakpoint"); 
		float a = 1,b = 2; a = b; b = a;
	}
	
	public void mouseWheel(MouseEvent event) { 
	  interf.input.mouseWheelEvent(event);
	}  
	public void keyPressed() { 
		if (!fullscreen) { //only stop esc in window mode
			switch(key) {
				case ESC: key = 0; break; } }
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
	
	
	

	public VideoExport videoExport;
	public Minim minim;
	public AudioPlayer sound_player;
	public FFT         fft;
	public String sound_file = "son\\test.mp3";

	void media_setup() {
		minim = new Minim(this);
		sound_player = minim.loadFile(sound_file);
		sound_player.loop();
		sound_player.pause();
		fft = new FFT( sound_player.bufferSize(), sound_player.sampleRate() );
		videoExport = new VideoExport(this);
		videoExport.setFfmpegPath(sketchPath() + "\\" + "ffmpeg.exe");
	}
	
	    //#######################################################################
		//##                         METHODES UTILES                           ##
		//#######################################################################


	public static float parseFlt(String s) {
		for (int i = s.length() - 1 ; i >= 0 ; i--) 
			if (s.charAt(i) == 'E') {
				float fct = PApplet.parseFloat(s.substring(i+1, s.length()));
				return PApplet.parseFloat(s.substring(0, i)) * PApplet.pow(10, fct);
			}
		return PApplet.parseFloat(s);
	}
	
	public float squared_mag(PVector p) { return p.x * p.x + p.y * p.y; }

	public float crandom(float d) { return pow(random((float) 1.0), d); }
		
		// auto indexing
//		int used_index = 0;
//		int get_free_id() { used_index++; return used_index - 1; }
		
		// gestion des polices de caractére
//		ArrayList<myFont> existingFont = new ArrayList<myFont>();
//		PFont existingFont;
//		int text_size;
//		class myFont { PFont f; int st; }
		public void textFont(PFont p) {
//			textSize(text_size);
		}
		public PFont getFont(int st) {
//			if (st < 0) st *= -1;
//		  if (st > 40) st = 40;
//		  if (st < 6) st = 6;
//		  if (existingFont == null) { 
//			  text_size = st;
//			  existingFont = createFont("Arial",st,false); //(name, size, smooth)
//			  super.textFont(existingFont);
//		  } else if (st != text_size) {
//			  text_size = st;
//		  }
//		  return existingFont;
		
//		  for (myFont f : existingFont) if (f.st == st) return f.f;
//		  myFont f = new myFont();
//		  f.f = createFont("Arial",st); f.st = st;
//		  return f.f; 
			  return null; 
		}
		//for (String s : PFont.list()) println(s); // liste toute les police de text qui existe
		
		public void drawcross(float x, float y, float s) {
			line(x-s, y, x+s, y);
			line(x, y-s, x, y+s);
		}
		
	    //#######################################################################
		//##                             LOGGING                               ##
		//#######################################################################
		
		
		
//		graphic debugs
		public boolean DEBUG_HOVERPILE = false;
		public boolean DEBUG_NOFILL = false;
		public boolean DEBUG_NOTOLAYTOP = false;
		
		boolean DEBUG = true, save_log_exit = false, save_log_all = false;
		String logpath = "sim_log.txt";
		
		public int global_frame_count = 0;
		ArrayList<String> logs = new ArrayList<String>();
		String current_log = "";
		String[] loglist;
		int log_line_count = 0;
		boolean logln_this_frame = false;
		

		String fMrk(String m) {
			if (!logln_this_frame) { logln_this_frame = true; return ":-"+m+":"; }
			else return ": "+m+":";
		}
		
		void savelog() {
		  if (loglist == null || loglist.length < logs.size()) loglist = new String[logs.size() + 10000]; 
		  for (int i = 0 ; i < log_line_count ; i++) loglist[i] = RConst.copy(logs.get(i));
		  saveStrings(logpath, loglist);
		}
		boolean do_one_save = false;
		void savelog_all() {
//			do_one_save = true;
			savelog();
		}
		
		public void og(String s) {
			String mark = global_frame_count+fMrk(" ");
		  if (DEBUG && current_log.length() == 0) print(mark);
		  if (DEBUG) print(s);
		  if (current_log.length() == 0) current_log += mark;
		  current_log += s;
		}
		public void logln(String s) {
			String mark = global_frame_count+fMrk(" ");
		  if (DEBUG && current_log.length() == 0) print(mark);
		  if (DEBUG) println(s);
		  if (current_log.length() == 0) current_log += mark;
		  current_log += s;
		  log_line_count++;
		  logs.add(RConst.copy(current_log));
		  current_log = "";
		  if (save_log_all) savelog_all();
		}
		
		boolean DEBUG_MACRO = false;
		public void mlog(String s) {
			String mark = global_frame_count+fMrk("M");
		  if (DEBUG && DEBUG_MACRO && current_log.length() == 0) print(mark);
		  if (DEBUG && DEBUG_MACRO) print(s);
		  if (current_log.length() == 0 && DEBUG_MACRO) current_log += mark;
		  if (DEBUG_MACRO) current_log += s;
		}
		public void mlogln(String s) {
			String mark = global_frame_count+fMrk("M");
		  if (DEBUG && DEBUG_MACRO && current_log.length() == 0) print(mark);
		  if (DEBUG && DEBUG_MACRO) println(s);
		  if (current_log.length() == 0 && DEBUG_MACRO) current_log += mark;
		  if (DEBUG_MACRO) { 
			  current_log += s;
			  log_line_count++;
			  logs.add(RConst.copy(current_log));
			  current_log = "";
			  if (save_log_all) savelog_all();
		  }
		}

		boolean DEBUG_CTRL = true;
		public void clog(String s) {
			if (interf.input.keyCtrl.state) {
				String mark = global_frame_count+fMrk("M");
				if (DEBUG && DEBUG_CTRL && current_log.length() == 0) print(mark);
				if (DEBUG && DEBUG_CTRL) print(s);
				if (current_log.length() == 0 && DEBUG_CTRL) current_log += mark;
				if (DEBUG_CTRL) current_log += s;
			}
		}
		public void clogln(String s) {
			if (interf.input.keyCtrl.state) {
				String mark = global_frame_count+fMrk("C");
				if (DEBUG && DEBUG_CTRL && current_log.length() == 0) print(mark);
				if (DEBUG && DEBUG_CTRL) println(s);
				if (current_log.length() == 0 && DEBUG_CTRL) current_log += mark;
				if (DEBUG_CTRL) { 
					current_log += s;
					log_line_count++;
					logs.add(RConst.copy(current_log));
					current_log = "";
					if (save_log_all) savelog_all();
				}
			}
		}
		

		boolean DEBUG_PACKET = false;
		public void plog(String s) {
			String mark = global_frame_count+fMrk("P");
		  if (DEBUG && DEBUG_PACKET && current_log.length() == 0) print(mark);
		  if (DEBUG && DEBUG_PACKET) print(s);
		  if (current_log.length() == 0 && DEBUG_PACKET) current_log += mark;
		  if (DEBUG_PACKET) current_log += s;
		}
		public void plogln(String s) {
			String mark = global_frame_count+fMrk("P");
		  if (DEBUG && DEBUG_PACKET && current_log.length() == 0) print(mark);
		  if (DEBUG && DEBUG_PACKET) println(s);
		  if (current_log.length() == 0 && DEBUG_PACKET) current_log += mark;
		  if (DEBUG_PACKET) { 
			  current_log += s;
			  log_line_count++;
			  logs.add(RConst.copy(current_log));
			  current_log = "";
			  if (save_log_all) savelog_all();
		  }
		}
		
		
		
		boolean DEBUG_SAVE_FULL = false;
		public void slog(String s) {
//		  if (DEBUG && DEBUG_SAVE_FULL) print(s);
//		  if (DEBUG_SAVE_FULL) current_log += s;
		}
		public void slogln(String s) {
//		  if (DEBUG && DEBUG_SAVE_FULL) println(s+":S:"+global_frame_count);
//		  if (DEBUG_SAVE_FULL) current_log += s+":S:"+global_frame_count;
//		  logs.add(RConst.copy(current_log));
//		  current_log = "";
//		  if (save_log_all) savelog_all();
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
