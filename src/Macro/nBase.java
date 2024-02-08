package Macro;

import RApplet.Rapp;
import processing.core.PVector;

class nFace {
  //float standard_aire = 10;
  PVector p1,p2,p3;
  //void norma() {
  //  float a = standard_aire; //trig aire
  //  p1.mult(standard_aire/a);
  //  p2.mult(standard_aire/a);
  //  p3.mult(standard_aire/a);
  //}
}

abstract class nShape {
  PVector pos = new PVector(0, 0);
  PVector dir = new PVector(10, 0); //heading : rot , mag : scale
  boolean do_fill = true, do_stroke = true;
  int col_fill, col_line;
  float line_w = 0.01F;
  nShape(Rapp app) {
	  col_fill = app.color(20, 130, 40); col_line = app.color(10, 190, 40);
  }
  void draw(Rapp app, boolean is_line) {
	  app.pushMatrix();
	  app.translate(pos.x, pos.y);
	  app.rotate(dir.heading());
	  app.scale(dir.mag());
	if (!is_line) {
//	    app.noStroke();
//	    app.noFill(); 
	    if (do_fill) app.fill(col_fill); else app.noFill(); 
	    if (do_stroke) app.stroke(col_line); else app.noStroke(); app.strokeWeight(line_w);
	    draw_fill_call(app);
//	    draw_stroke_call(app);
	} else {
		app.noFill(); 
	    if (do_stroke) app.stroke(col_line); else app.noStroke(); app.strokeWeight(line_w);
	    draw_line_call(app);
	}
    
    app.popMatrix();
  }
  abstract void draw_fill_call(Rapp a);
  abstract void draw_stroke_call(Rapp a);
  abstract void draw_line_call(Rapp a);
}

public class nBase extends nShape {
  nFace face;    
  nBase(Rapp app) {
	  super(app);
    face = new nFace();
    face.p1 = new PVector(1, 0);
    face.p2 = new PVector(0, 0.0F);
    face.p3 = new PVector(0, -1.0F);
    //face.norma();

    m = new PVector(0, 0);
    l1 = new PVector(0, 0);
    l2 = new PVector(0, 0);
    l3 = new PVector(0, 0);
  }
  float nrad() {
    return Math.max(Math.max(face.p1.mag(), face.p2.mag()), face.p3.mag()); }
  float rad() {
    return dir.mag() * Math.max(Math.max(face.p1.mag(), face.p2.mag()), face.p3.mag()); }
  PVector p1() { 
    PVector p = new PVector(face.p1.x, face.p1.y);
    p.rotate(dir.heading()); p.setMag(face.p1.mag()*dir.mag()); p.add(pos);
    return p; }
  PVector p2() { 
    PVector p = new PVector(face.p2.x, face.p2.y);
    p.rotate(dir.heading()); p.setMag(face.p2.mag()*dir.mag()); p.add(pos);
    return p; }
  PVector p3() { 
    PVector p = new PVector(face.p3.x, face.p3.y);
    p.rotate(dir.heading()); p.setMag(face.p3.mag()*dir.mag()); p.add(pos);
    return p; }
  void draw_fill_call(Rapp app) {
	    app.triangle(face.p1.x, face.p1.y, face.p2.x, face.p2.y, face.p3.x, face.p3.y);
	  }
  void draw_line_call(Rapp app) {
	    app.line(face.p1.x, face.p1.y, face.p2.x, face.p2.y);
	  }
  PVector m,l1,l2,l3;
	void draw_stroke_call(Rapp app) {
		m.set(face.p1.x, face.p1.y);
		m.add(face.p2.x, face.p2.y);
		m.add(face.p3.x, face.p3.y);
		m.set(m.x/3.0F, m.y/3.0F);
//		l1.set(face.p1.x - m.x, face.p1.y - m.y)
//				.setMag(line_w/2).add(face.p1.x, face.p1.y);
//		l2.set(face.p2.x - m.x, face.p2.y - m.y)
//				.setMag(line_w/2).add(face.p2.x, face.p2.y);
//		l3.set(face.p3.x - m.x, face.p3.y - m.y)
//				.setMag(line_w/2).add(face.p3.x, face.p3.y);
		l1.set(face.p1.x - m.x, face.p1.y - m.y).mult(1.0F + line_w/nrad()).add(m);
		l2.set(face.p2.x - m.x, face.p2.y - m.y).mult(1.0F + line_w/nrad()).add(m);
		l3.set(face.p3.x - m.x, face.p3.y - m.y).mult(1.0F + line_w/nrad()).add(m);
		app.triangle(l1.x, l1.y, l2.x, l2.y, l3.x, l3.y);
	}
}
