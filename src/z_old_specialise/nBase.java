package z_old_specialise;

import RApplet.Rapp;
import processing.core.PVector;





public class nBase extends nShape {
  public nFace face;    
  public nBase(Rapp app) {
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
  public float nrad() {
    return Math.max(Math.max(face.p1.mag(), face.p2.mag()), face.p3.mag()); }
  public float rad() {
    return dir.mag() * Math.max(Math.max(face.p1.mag(), face.p2.mag()), face.p3.mag()); }
  public PVector p1() { 
    PVector p = new PVector(face.p1.x, face.p1.y);
    p.rotate(dir.heading()); p.setMag(face.p1.mag()*dir.mag()); p.add(pos);
    return p; }
  public PVector p2() { 
    PVector p = new PVector(face.p2.x, face.p2.y);
    p.rotate(dir.heading()); p.setMag(face.p2.mag()*dir.mag()); p.add(pos);
    return p; }
  public PVector p3() { 
    PVector p = new PVector(face.p3.x, face.p3.y);
    p.rotate(dir.heading()); p.setMag(face.p3.mag()*dir.mag()); p.add(pos);
    return p; }
  public void draw_fill_call(Rapp app) {
	    app.triangle(face.p1.x, face.p1.y, face.p2.x, face.p2.y, face.p3.x, face.p3.y);
	  }
  public void draw_line_call(Rapp app) {
	    app.line(face.p1.x, face.p1.y, face.p2.x, face.p2.y);
	  }
  public PVector m,l1,l2,l3;
  public void draw_stroke_call(Rapp app) {
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
