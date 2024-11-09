package RApplet;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.MouseEvent;
import sData.sValue;

public class sInput {

  //keyboard letters
  public boolean getState(char k) { 
    return getKeyboardButton(k).state;
  }
  public boolean getClick(char k) { 
    return getKeyboardButton(k).trigClick;
  }
  public boolean getUnClick(char k) { 
    return getKeyboardButton(k).trigUClick;
  }

  //mouse n specials
  public boolean getState(String k) { 
    return getButton(k).state;
  }
  public boolean getClick(String k) { 
    return getButton(k).trigClick;
  }
  public boolean getUnClick(String k) { 
    return getButton(k).trigUClick;
  }

  public char getLastKey() { 
    return last_key;
  }

  public sInput(Rapp a) {//PApplet app) {
	app = a;
    //app.registerMethod("pre", this);
    mouseLeft = getButton("MouseLeft");
    mouseRight = getButton("MouseRight");
    mouseCenter = getButton("MouseCenter");
    keyBackspace = getButton("Backspace"); 
    keyEnter = getButton("Enter");
    keyCtrl = getButton("Ctrl");
    keyLeft = getButton("Left"); 
    keyRight = getButton("Right");
    keyUp = getButton("Up"); 
    keyDown = getButton("Down");
    keyAll = getButton("All"); //any key
  }
  
  public Rapp app;

  public PVector mouse = new PVector();
  public PVector pmouse = new PVector(); //prev pos
  public PVector mmouse = new PVector(); //mouvement
  public boolean mouseWheelUp, mouseWheelDown;
  public boolean do_shortcut = true;
  ArrayList<sInput_Button> pressed_keys = new ArrayList<sInput_Button>();
  char last_key = ' ';

  ArrayList<sInput_Button> buttons = new ArrayList<sInput_Button>();
  
  public ArrayList<sValue> shorted_values = new ArrayList<sValue>();
  public sInput_Button mouseLeft;

sInput_Button mouseRight;

sInput_Button mouseCenter;

sInput_Button keyBackspace;

sInput_Button keyEnter;

sInput_Button keyCtrl;

sInput_Button keyLeft;

sInput_Button keyRight;

sInput_Button keyUp;

sInput_Button keyDown;

public sInput_Button keyAll;

  sInput_Button getButton(String r) {
    for (sInput_Button b : buttons) if (b.ref.equals(r)) return b;
    sInput_Button n = new sInput_Button(r); 
    buttons.add(n);
    return n;
  }
  public sInput_Button getKeyboardButton(char k) {
    for (sInput_Button b : buttons) if (b.ref.equals("k") && k == b.key_char) return b;
    sInput_Button n = new sInput_Button("k", k); 
    buttons.add(n);
    return n;
  }

  void frame_str() {
    mouse.x = app.mouseX; 
    mouse.y = app.mouseY; 
    pmouse.x = app.pmouseX; 
    pmouse.y = app.pmouseY;
    mmouse.x = app.mouseX - app.pmouseX; 
    mmouse.y = app.mouseY - app.pmouseY;
  }
  void frame_end() {
    mouseWheelUp = false; 
    mouseWheelDown = false;
    for (sInput_Button b : buttons) {
    		if (do_shortcut && b.trigClick) for (sValue v : shorted_values) 
    			if (v.direct_shortcut != 0 && v.direct_shortcut == b.key_char) {
    				app.logln("dsc_action:"+v.ref+":"+b.key_char); 
    				v.directshortcut_action(); }
    		b.frame();
    }
  }

  void mouseWheelEvent(MouseEvent event) {
    @SuppressWarnings("deprecation")
	float e = event.getAmount();
    if (e>0) { 
      mouseWheelUp =true; 
      mouseWheelDown =false;
    }
    if (e<0) { 
      mouseWheelDown = true; 
      mouseWheelUp=false;
    }
  }  

  void keyPressedEvent() { 
    char k = app.key;
    //if (keyCtrl.state) { 
    //  k = char(keyCode);
    //  String ts = ""; ts = ts + k;
    //  ts = ts.toLowerCase();
    //  k = ts.charAt(0);
    //}
//    if (!(app.key == PConstants.CODED && app.keyCode == PConstants.CONTROL))
//    		app.logln(k+" "+Integer.toBinaryString((int)app.keyCode));
    
    for (sInput_Button b : buttons) 
      if (b.ref.equals("k") && b.key_char == app.key) { b.eventPress(); pressed_keys.add(b); }
    if (app.key == PConstants.CODED) {
      if (app.keyCode == PConstants.LEFT) keyLeft.eventPress();
      if (app.keyCode == PConstants.RIGHT) keyRight.eventPress();
      if (app.keyCode == PConstants.UP) keyUp.eventPress();
      if (app.keyCode == PConstants.DOWN) keyDown.eventPress();
      if (app.keyCode == PConstants.CONTROL) keyCtrl.eventPress();
    } else {
      if (app.key == PConstants.BACKSPACE) keyBackspace.eventPress();
      if (app.key == PConstants.ENTER) keyEnter.eventPress();
      keyAll.eventPress();
      last_key = (char) app.key;
    }
  }

  void keyReleasedEvent() { 
    for (sInput_Button b : buttons) 
      if (b.ref.equals("k") && b.key_char == app.key) { b.eventRelease(); pressed_keys.remove(b); }
    if (app.key == PConstants.CODED) {
      if (app.keyCode == PConstants.LEFT) keyLeft.eventRelease();
      if (app.keyCode == PConstants.RIGHT) keyRight.eventRelease();
      if (app.keyCode == PConstants.UP) keyUp.eventRelease();
      if (app.keyCode == PConstants.DOWN) keyDown.eventRelease();
      if (app.keyCode == PConstants.CONTROL) keyCtrl.eventRelease();
    } else {
      if (app.key == PConstants.BACKSPACE) keyBackspace.eventRelease();
      if (app.key == PConstants.ENTER) keyEnter.eventRelease();
      boolean ks = false;
      for (sInput_Button b : buttons) ks = ks && b.state;
      if (!ks) keyAll.eventRelease();
    }
  }

  void mousePressedEvent()
  {
    if (app.mouseButton==PConstants.LEFT) mouseLeft.eventPress();
    if (app.mouseButton==PConstants.RIGHT) mouseRight.eventPress();
    if (app.mouseButton==PConstants.CENTER) mouseCenter.eventPress();
  }

  void mouseReleasedEvent()
  {
    if (app.mouseButton==PConstants.LEFT) mouseLeft.eventRelease();
    if (app.mouseButton==PConstants.RIGHT) mouseRight.eventRelease();
    if (app.mouseButton==PConstants.CENTER) mouseCenter.eventRelease();
  }
}
