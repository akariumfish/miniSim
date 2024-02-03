package RApplet;

import java.util.ArrayList;

import UI.Rect;

import processing.core.PVector;
import sData.nRunnable;
import sData.sBoo;
import sData.sFlt;
import sData.sValueBloc;
import sData.sVec;

public class Camera {
  sInput input;
  Rect cam_view;
  public sVec cam_pos; //position de la camera
  public sFlt cam_scale; //facteur de grossicement
  float ZOOM_FACTOR = 1.1F; //facteur de modification de cam_scale quand on utilise la roulette de la sourie
  boolean GRAB = true, grabbed = false;
  public sBoo grid; //show grid
  public boolean screenshot = false; //enregistre une image de la frame sans les menu si true puis se desactive
  boolean matrixPushed = false; //track if in or out of the cam matrix

  Camera(sInput i, sValueBloc d) { 
	input = i;
    grid = new sBoo(d, true, "show grid", "grid");
    cam_scale = new sFlt(d, 1.0F, "cam scale", "scale");
    cam_scale.addEventChange(new nRunnable() { public void run() {
    	cam_view.pos.set(screen_to_cam(new PVector(0, 0)));
    	cam_view.size.set(screen_to_cam(
    			new PVector(input.app.width, input.app.height)).sub(cam_view.pos)); 
      nRunnable.runEvents(eventsZoom);
      nRunnable.runEvents(eventsMove); }});
    cam_pos = new sVec(d, "cam pos", "pos");
    cam_pos.addEventChange(new nRunnable() { public void run() {
    	cam_view.pos.set(screen_to_cam(new PVector(0, 0)));
    	cam_view.size.set(screen_to_cam(
    			new PVector(input.app.width, input.app.height)).sub(cam_view.pos)); 
      nRunnable.runEvents(eventsZoom);
      nRunnable.runEvents(eventsMove); }});
    cam_view = new Rect(0, 0, input.app.width, input.app.height);
    cam_view.pos.set(screen_to_cam(new PVector(0, 0)));
    cam_view.size.set(screen_to_cam(
    		new PVector(input.app.width, input.app.height)).sub(cam_view.pos));
  }

  ArrayList<nRunnable> eventsZoom = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventsMove = new ArrayList<nRunnable>();
  public Camera addEventZoom(nRunnable r) { 
    eventsZoom.add(r); 
    return this; }
  public Camera addEventMove(nRunnable r) { 
    eventsMove.add(r); 
    return this; }
  public Camera removeEventZoom(nRunnable r) { 
    eventsZoom.remove(r); 
    return this; }
  public Camera removeEventMove(nRunnable r) { 
    eventsMove.remove(r); 
    return this; }
    
  PVector mouse = new PVector();
  PVector pmouse = new PVector(); //prev pos
  PVector mmouse = new PVector(); //mouvement

  void pushCam(float x, float y) {
    cam_pos.add(x*cam_scale.get(), y*cam_scale.get());
  }

  void pushCam() {
	input.app.pushMatrix();
	input.app.translate(input.app.width / 2, (input.app.height) / 2);
	input.app.scale(cam_scale.get());
	input.app.translate((cam_pos.x() / cam_scale.get()), (cam_pos.y() / cam_scale.get()));
    matrixPushed = true;

    if (grid.get() && cam_scale.get() > 0.0008) {
      int spacing = 200;
      if (cam_scale.get() > 2) spacing /= 5;
      if (cam_scale.get() < 0.2) spacing *= 5;
      if (cam_scale.get() < 0.04) spacing *= 5;
      if (cam_scale.get() < 0.008) spacing *= 5;
      input.app.stroke(100);
      input.app.strokeWeight(2.0F / cam_scale.get());
      PVector s = screen_to_cam(new PVector(-spacing * cam_scale.get(), -spacing * cam_scale.get()));
      s.x -= s.x%spacing; 
      s.y -= s.y%spacing;
      PVector m = screen_to_cam( new PVector(input.app.width, input.app.height) );
      for (float x = s.x; x < m.x; x += spacing) {
        if ( ( (x-(x%spacing)) / spacing) % 5 == 0 ) input.app.stroke(100); 
        else input.app.stroke(70);
        if (x == 0) input.app.stroke(150, 0, 0);
        input.app.line(x, s.y, x, m.y);
      }
      for (float y = s.y; y < m.y; y += spacing) {
        if ( ( (y-(y%spacing)) / spacing) % 5 == 0 ) input.app.stroke(100); 
        else input.app.stroke(70);
        if (y == 0) input.app.stroke(150, 0, 0);
        input.app.line(s.x, y, m.x, y);
      }
    }
  }

  void popCam() {
    input.app.popMatrix();
    matrixPushed = false;
    if (screenshot) { 
//      saveFrame("image/shot-########.png");
    }
    screenshot = false;

    PVector tm = screen_to_cam(input.mouse);
    PVector tpm = screen_to_cam(input.pmouse);
    PVector tmm = screen_to_cam(input.mmouse);
    mouse.x = tm.x; 
    mouse.y = tm.y;
    pmouse.x = tpm.x; 
    pmouse.y = tpm.y;
    mmouse.x = tmm.x; 
    mmouse.y = tmm.y;

    //permet le cliquer glisser le l'ecran
    if (input.getClick("MouseLeft") && GRAB) grabbed = true; 
    if (!input.getState("MouseLeft") && grabbed) grabbed = false; 
    if (input.getState("MouseLeft") && grabbed) { 
      cam_pos.add((mouse.x - pmouse.x)*cam_scale.get(), (mouse.y - pmouse.y)*cam_scale.get());
      cam_view.pos.set(screen_to_cam(new PVector(0, 0)));
      cam_view.size.set(screen_to_cam(
    		  new PVector(input.app.width, input.app.height)).sub(cam_view.pos));
      nRunnable.runEvents(eventsMove);
    }

    //permet le zoom
    if (input.mouseWheelUp && GRAB) { 
      cam_scale.set(cam_scale.get()*1/ZOOM_FACTOR); 
      cam_pos.mult(1/ZOOM_FACTOR); 
      cam_view.pos.set(screen_to_cam(new PVector(0, 0)));
      cam_view.size.set(screen_to_cam(
    		  new PVector(input.app.width, input.app.height)).sub(cam_view.pos));
      nRunnable.runEvents(eventsMove);
      nRunnable.runEvents(eventsZoom);
    }
    if (input.mouseWheelDown && GRAB) {
      cam_scale.set(cam_scale.get()*ZOOM_FACTOR); 
      cam_pos.mult(ZOOM_FACTOR); 
      cam_view.pos.set(screen_to_cam(new PVector(0, 0)));
      cam_view.size.set(screen_to_cam(
    		  new PVector(input.app.width, input.app.height)).sub(cam_view.pos));
      nRunnable.runEvents(eventsMove);
      nRunnable.runEvents(eventsZoom);
    }
  }

  public PVector cam_to_screen(PVector p) {
    PVector r = new PVector();
    if (matrixPushed) {
      r.x = input.app.screenX(p.x, p.y); 
      r.y = input.app.screenY(p.x, p.y);
    } else {
    	input.app.pushMatrix();
    	input.app.translate(input.app.width / 2, input.app.height / 2);
      input.app.scale(cam_scale.get());
      input.app.translate((cam_pos.x() / cam_scale.get()), (cam_pos.y() / cam_scale.get()));

      r.x = input.app.screenX(p.x, p.y); 
      r.y = input.app.screenY(p.x, p.y);

      input.app.popMatrix();
    }
    return r;
  }

  public PVector screen_to_cam(PVector p) {
    PVector r = new PVector();
    if (matrixPushed) {
    	input.app.pushMatrix();
    	input.app.translate(-(cam_pos.x() / cam_scale.get()), -(cam_pos.y() / cam_scale.get()));
    	input.app.scale(1/cam_scale.get());
    	input.app.translate(-input.app.width / 2, -input.app.height / 2);

      input.app.translate(-(cam_pos.x() / cam_scale.get()), -(cam_pos.y() / cam_scale.get()));
      input.app.scale(1/cam_scale.get());
      input.app.translate(-input.app.width / 2, -input.app.height / 2);

      r.x = input.app.screenX(p.x, p.y); 
      r.y = input.app.screenY(p.x, p.y);
      input.app.popMatrix();
    } else {
    	input.app.pushMatrix();
      input.app.translate(-(cam_pos.x() / cam_scale.get()), -(cam_pos.y() / cam_scale.get()));
      input.app.scale(1/cam_scale.get());
      input.app.translate(-input.app.width / 2, -input.app.height / 2);
      r.x = input.app.screenX(p.x, p.y); 
      r.y = input.app.screenY(p.x, p.y);
      input.app.popMatrix();
    }
    return r;
  }
}
