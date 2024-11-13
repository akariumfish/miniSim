package sData;

import RApplet.RConst;

public class sFlt extends sValue implements RConst {
	public void set_resolution(int r) { resolution = r; apply_rez(); }
	int resolution = 0;
	  public float asFlt() { return val; }
	float min, max;
	  public sFlt set_limit(float mi, float ma) { 
		  if (!limited_min || !limited_max) doChange(); 
	    limited_min = true; limited_max = true; 
	    min = mi; max = ma; return this; }
	  public sFlt set_min(float mi) { 
		  if (!limited_min) doChange(); 
		  limited_min = true; 
		  min = mi; return this; }
	  public sFlt set_max(float ma) { 
		  if (!limited_max) doChange(); 
		  limited_max = true; 
		  max = ma; return this; }
	  public float getmin() { return min; }
	  public float getmax() { return max; }
	  public float getscale() { return (val - min) / (max - min); }
	  public void setscale(float v) { set(min + v * (max - min)); }
	  
	  public float asFloat() { return val; }
	  
	  public String getString() { return RConst.trimFlt(val); }
	  public void clear() { super.clear(); val = def; }
	  float val = 0, def;
	  public float ctrl_factor = 2;
	  public sFlt(sValueBloc b, float v, String n, String s) { super(b, "flt", n, s); val = v; def = val; }
	  public float get() { return val; }
	  public void set(double v) { set((float)v); }
	  public void set(float v) { 
		  run_events_allset(); 
	    if (limited_max && v > max) v = max; if (limited_min && v < min) v = min;
	    if (v != val) { 
	    		if (log) bloc.data.app.logln(ref+"issetto"+v); 
	    		val = v; 
	    		apply_rez();
	    		doChange(); 
	    	}
	  }
	  public void apply_rez() {
  		if (resolution != 0 && 
  				val > -Math.pow(10, resolution - 1) && 
  				val < Math.pow(10, resolution - 1)) val = 0;
	  }
	  public void add(float v) { set(get()+v); }
	  public void add(double v) { add((float)v); }
	  public void mult(float v) { set(get()*v); }
	  public void mult(double v) { mult((float)v); }
	  public void div(float v) { set(get()/v); }
	  public void div(double v) { div((float)v); }
	  
	  
	  void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
	    svb.newData("val", val);
	    svb.newData("min", min);
	    svb.newData("max", max);
	    svb.newData("lmin", limited_min);
	    svb.newData("lmax", limited_max);
	  }
	  void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
	    set(svb.getFloat("val"));
	    min = svb.getFloat("min");
	    max = svb.getFloat("max");
	    limited_min = svb.getBoolean("lmin");
	    limited_max = svb.getBoolean("lmax");
	  }
	}
