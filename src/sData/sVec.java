package sData;

import RApplet.RConst;
import processing.core.PVector;

public class sVec extends sValue implements RConst {
	  public PVector asVec() { return new PVector(val.x, val.y); }
	  public String getString() { 
		  return RConst.trimStringFloat(val.x) + "," + RConst.trimStringFloat(val.y); }
	  public void clear() { super.clear(); val.x = def.x; val.y = def.y; }
	  private PVector val = new PVector(), def = new PVector();
	  public sVec(sValueBloc b, String n, String s) { super(b, "vec", n, s); }
	  public float x() { return val.x; }
	  public float y() { return val.y; }
	  public PVector get() { return new PVector(val.x, val.y); }
	  public sVec setx(double v) { return setx((float)v); }
	  public sVec sety(double v) { return sety((float)v); }
	  public sVec setx(float v) { if (v != val.x) { val.x = v; doChange(); } return this; }
	  public sVec sety(float v) { if (v != val.y) { val.y = v; doChange(); } return this; }
	  public sVec set(double _x, double _y) { return set((float)_x, (float)_y); }
	  public sVec set(float _x, float _y) { 
	    if (_x != val.x || _y != val.y) {
	      val.x = _x; 
	      val.y = _y; 
	      doChange(); 
	    } 
	    return this;
	  }
	  public sVec set(PVector v) { set(v.x, v.y); return this; }
	  public sVec addx(float _x) { setx(val.x+_x); return this; }
	  public sVec addy(float _y) { sety(val.y+_y); return this; }
	  public sVec add(float _x, float _y) { set(val.x+_x, val.y+_y); return this; }
	  public  sVec add(PVector v) { add(v.x, v.y); return this; }
	  public  sVec add(sVec v) { add(v.x(), v.y()); return this; }
	  public  sVec mult(float m) { set(val.x*m, val.y*m); return this; }
	  void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
	    svb.newData("x", val.x);
	    svb.newData("y", val.y); }
	  void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
	    set(svb.getFloat("x"), svb.getFloat("y")); }
	}
