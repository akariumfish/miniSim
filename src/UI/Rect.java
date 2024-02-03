package UI;

import RApplet.Rapp;
import processing.core.PVector;

//utiliser par le hovering
public class Rect {
	public PVector pos = new PVector(0, 0);
	public PVector size = new PVector(0, 0);
	public Rect() {}
	public Rect(float x, float y, float w, float h) {pos.x = x; pos.y = y; size.x = w; size.y = h;}
	public Rect(Rect r) {pos.x = r.pos.x; pos.y = r.pos.y; size.x = r.size.x; size.y = r.size.y;}
	void draw(Rapp a) { 
		  a.rect(pos.x, pos.y, size.x, size.y); 
	}
	public Rect copy(Rect r) { 
	  pos.x = r.pos.x; pos.y = r.pos.y; size.x = r.size.x; size.y = r.size.y; 
	  return this; }

	static boolean rectCollide(Rect rect1, Rect rect2) {
	  return (rect1.pos.x < rect2.pos.x + rect2.size.x &&
	          rect1.pos.x + rect1.size.x > rect2.pos.x &&
	          rect1.pos.y < rect2.pos.y + rect2.size.y &&
	          rect1.pos.y + rect1.size.y > rect2.pos.y   );
	}
	
	public static boolean rectCollide(Rect rect1, Rect rect2, float s) {
	  Rect r1 = new Rect(rect1); r1.pos.x -= s; r1.pos.y -= s; r1.size.x += 2*s; r1.size.y += 2*s;
	  Rect r2 = new Rect(rect2); r2.pos.x -= s; r2.pos.y -= s; r2.size.x += 2*s; r2.size.y += 2*s;
	  return (r1.pos.x < r2.pos.x + r2.size.x &&
	          r1.pos.x + r1.size.x > r2.pos.x &&
	          r1.pos.y < r2.pos.y + r2.size.y &&
	          r1.pos.y + r1.size.y > r2.pos.y   );
	}
	
	static boolean rectCollide(PVector p, Rect rect) {
	  return (p.x >= rect.pos.x && p.x <= rect.pos.x + rect.size.x &&
	          p.y >= rect.pos.y && p.y <= rect.pos.y + rect.size.y );
	}
	
	static boolean rectCollide(PVector p, Rect rect, float s) {
	  Rect rects = new Rect(rect); rects.pos.x -= s; rects.pos.y -= s; rects.size.x += 2*s; rects.size.y += 2*s;
	  return (p.x >= rects.pos.x && p.x <= rects.pos.x + rects.size.x &&
	          p.y >= rects.pos.y && p.y <= rects.pos.y + rects.size.y );
	}
}
