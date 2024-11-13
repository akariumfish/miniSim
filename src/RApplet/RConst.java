package RApplet;

import processing.core.PApplet;
import processing.core.PVector;

public interface RConst {
	public final static float PI = (float) Math.PI;
	

	public final static String[] alphabet = { 
			  "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			  "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
			  "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", 
			  "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", 
			  "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
			  }; 
	
	static String copy(String s) { 
		if (s != null) return "" + s/*.substring(0, s.length())*/; else return null; }
	static String str_copy(String s) { 
		if (s != null) return "" + s/*.substring(0, s.length())*/; else return null; }
	
	static public boolean xor(boolean a, boolean b) {
		return (a || b) && !(a && b);
	}
	
	static public boolean testParseFlt(String s) {
		return s.length() > 0 && !PApplet.str(Rapp.parseFlt(s)).equals("NaN");
	}
	static public boolean testParseInt(String s) {
		return s.length() > 0 && !PApplet.str(PApplet.parseInt(s)).equals("NaN");
	}
	
	static public String trimFlt(float f) { return trimFlt(f, 3); }
	static public String trimFlt(float f, int p) {
		int dec_m = 0;
		float abs = PApplet.abs(f);
		if (f != 0) {
			if (abs >= PApplet.pow(10, p-1)) { 
				dec_m = p-1;
				while (abs > PApplet.pow(10, dec_m)) { dec_m++; }
				dec_m -= p-1;
				f /= PApplet.pow(10, dec_m);
			}
			if (abs < PApplet.pow(10, -(p-2))) { 
				dec_m = -(p-2);
				while (abs < PApplet.pow(10, dec_m)) { dec_m--; }
				dec_m -= -(p-2);
				f /= PApplet.pow(10, dec_m);
			}
		}
		String s = PApplet.str(f);
		
		if (f >= 0) {
			if (abs >= PApplet.pow(10, p-1)) s = s.substring(0, PApplet.min(s.length(), p));
			s = s.substring(0, PApplet.min(s.length(), p+1));
		} else {
			if (abs >= PApplet.pow(10, p-1)) s = s.substring(0, PApplet.min(s.length(), p+1));
			s = s.substring(0, PApplet.min(s.length(), p+2));
		}
		if (dec_m != 0) s += "E"+dec_m;
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
	static float distSqPointPoint(float xa, float ya, float xb, float yb) {
	  return (float)(Math.pow((xb-xa), 2) + Math.pow((yb-ya), 2));
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
