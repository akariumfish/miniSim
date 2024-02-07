package RBase;

import processing.core.PApplet;
import processing.core.PVector;

public interface RConst {
	public final static float PI = (float) Math.PI;
	
	static String copy(String s) { 
		if (s != null) return "" + s/*.substring(0, s.length())*/; else return null; }
	static String str_copy(String s) { 
		if (s != null) return "" + s/*.substring(0, s.length())*/; else return null; }
	
	static public String trimStringFloat(float f) { return trimStringFloat(f, 3); }
	static public String trimStringFloat(float f, int p) {
	  String s;
	  if (f%1.0 == 0.0) s = PApplet.nfc((int)(f)); else s = String.valueOf(f);
	  String end = "";
	  for (int i = s.length()-1; i > 0 ; i--) {
	    if (s.charAt(i) == 'E') {
	      end = s.substring(i, s.length());
	    }
	  }
	  for (int i = 0; i < s.length() ; i++) {
	    if (s.charAt(i) == '.' && s.length() - i > p) {
	      int m = p;
	      for (int c = 0 ; c < p ; c++) {
	        if (f >= Math.pow(10, c+1)) m -= 1;
	        if (f >= Math.pow(10, c+1) && (c+1)%3 == 0) m -= 1;
	      }
	      //if (f >= 10) m -= 1;
	      //if (f >= 100) m -= 1;
	      //if (f >= 1000) m -= 2;
	      s = s.substring(0, i+m);
	      s = s + end;
	      return s;
	    }
	  }
	  return s;
	}
	
	static float soothedcurve(float rad, float dst) {
	  float val = Math.max(0, rad*rad - dst*dst);
	  return val * val * val;
	}
	
	static float distancePointToLine(float x, float y, float x1, float y1, float x2, float y2) {
	  float r =  (float) (( ((x-x1)*(x2-x1)) + ((y-y1)*(y2-y1)) ) / Math.pow(distancePointToPoint(x1, y1, x2, y2), 2));
	  if (r <= 0) {return distancePointToPoint(x1, y1, x, y);}
	  if (r >= 1) {return distancePointToPoint(x, y, x2, y2);}
	  float px = x1 + (r * (x2-x1));
	  float py = y1 + (r * (y2-y1));
	  return distancePointToPoint(x, y, px, py);
	}
	static float crss(PVector a, PVector b) { return a.x*b.y-a.y*b.x; }
	
	static boolean point_in_trig(PVector A, PVector B, PVector C, PVector P)
	{
	    PVector ab = new PVector(B.x - A.x, B.y - A.y);
	    PVector ac = new PVector(C.x - A.x, C.y - A.y);
	    PVector pa = new PVector(A.x - P.x, A.y - P.y);
	
	    if (ac.y == 0.0) ac.y = (float) 0.00001;
	
	    float w1 = crss(pa, ac) / crss(ac, ab);
	    float w2 = (-1 * pa.y - w1 * ab.y) / ac.y;
	    return w1 >= 0 && w2 >= 0 && (w1 + w2) <= 1;
	}
	
	static float distancePointToPoint(float xa, float ya, float xb, float yb) {
	  return (float) Math.sqrt( Math.pow((xb-xa), 2) + Math.pow((yb-ya), 2) );
	}
}
