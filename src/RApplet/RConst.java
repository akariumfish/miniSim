package RApplet;

import processing.core.PApplet;
import processing.core.PVector;

public interface RConst {
	public final static float PI = (float) Math.PI;
	
	static String copy(String s) { 
		if (s != null) return "" + s/*.substring(0, s.length())*/; else return null; }
	static String str_copy(String s) { 
		if (s != null) return "" + s/*.substring(0, s.length())*/; else return null; }
	
	static public String trimFlt(float f) { return trimFlt(f, 3); }
	static public String trimFlt(float f, int p) {
		String s;
		  if (f%1.0 == 0.0) s = PApplet.nfc((int)(f)); else s = PApplet.str(f);
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
		        if (f >= PApplet.pow(10, c+1)) m -= 1;
		        if (f >= PApplet.pow(10, c+1) && (c+1)%3 == 0) m -= 1;
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
//		int dec_m = 0;
//		while (f > 10) { f /= 10; dec_m++; }
//		while (f < 1) { f *= 10; dec_m--; }
//		String s = PApplet.str(f);
//		s = s.substring(0, p+1);
//		s += "E"+dec_m;
//		return s;
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
	
//#######################################################################
//##          ROTATING TO ANGLE CIBLE BY SHORTEST DIRECTION            ##
//#######################################################################

  static float mapToCircularValues(float current, float cible, float increment, float start, float stop) {
	  if (start > stop) {float i = start; start = stop; stop = i;}
	  increment = Math.abs(increment);
	  
	  while (cible > stop) {cible -= (stop - start);}
	  while (current > stop) {current -= (stop - start);}
	  while (cible < start) {cible += (stop - start);}
	  while (current < start) {current += (stop - start);}
	  
	  if (cible < current) {
	    if ( (current - cible) <= (stop - current + cible - start) ) {
	      if (increment >= current - cible) {return cible;}
	      else                              {return current - increment;}
	    } else {
	      if (increment >= stop - current + cible - start) {return cible;}
	      else if (current + increment < stop)             {return current + increment;}
	      else                                             {return start + (increment - (stop - current));}
	    }
	  } else if (cible > current) {
	    if ( (cible - current) <= (stop - cible + current - start) ) {
	      if (increment >= cible - current) {return cible;}
	      else                              {return current + increment;}
	    } else { 
	      if (increment >= stop - cible + current - start) {return cible;}
	      else if (current - increment > start)            {return current - increment;}
	      else                                             {return stop - (increment - (current - start));}
	    }
	  }
	  return cible;
	}
}
