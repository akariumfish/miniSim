package sData;

import RBase.RConst;

public class sFlt extends sValue implements RConst {
	  boolean limited_min = false, limited_max = false; float min, max;
	  public sFlt set_limit(float mi, float ma) { 
	    limited_min = true; limited_max = true; min = mi; max = ma; return this; }
	  
	  public sFlt set_min(float mi) { limited_min = true; min = mi; return this; }
	  public sFlt set_max(float ma) { limited_max = true; max = ma; return this; }
	  public float getmin() { return min; }
	  public float getmax() { return max; }
	  public float getscale() { return (val - min) / (max - min); }
	  public void setscale(float v) { set(min + v * (max - min)); }
	  
	  public float asFloat() { return val; }
	  
	  public String getString() { return RConst.trimStringFloat(val); }
	  public void clear() { super.clear(); val = def; }
	  float val = 0, def;
	  public float ctrl_factor = 2;
	  public sFlt(sValueBloc b, float v, String n, String s) { super(b, "flt", n, s); val = v; def = val; }
	  public float get() { return val; }
	  public void set(double v) { set((float)v); }
	  public void set(float v) { 
	    if (limited_max && v > max) v = max; if (limited_min && v < min) v = min;
	    if (v != val) { val = v; doChange(); } }
	  public void add(float v) { set(get()+v); }
	  public void add(double v) { add((float)v); }
	  void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
	    svb.newData("val", val);
	  }
	  void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
	    set(svb.getFloat("val"));
	  }
	}
