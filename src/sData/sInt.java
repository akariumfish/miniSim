package sData;

public class sInt extends sValue {
	  public int asInt() { return val; }
	  int min, max;
	  public sInt set_limit(int mi, int ma) { 
		  if (!limited_min || !limited_max || min != mi || max != ma) doChange(); 
	    limited_min = true; limited_max = true; 
	    min = mi; max = ma; return this; }
	  public sInt set_min(int mi) { 
		  if (!limited_min || min != mi) doChange(); 
		  limited_min = true; 
		  min = mi; return this; }
	  public sInt set_max(int ma) { 
		  if (!limited_max || max != ma) doChange(); 
		  limited_max = true; 
		  max = ma; return this; }
	  public float getmin() { return min; }
	  public float getmax() { return max; }
	  public float getscale() { return (float)(val - min) / (float)(max - min); }
	  public void setscale(float v) { set(min + (int)( v * (max - min) )); }
	  
	  public float asFloat() { return (float)(val); }
	  
	  public String getString() { return String.valueOf(val); }
	  public void clear() { super.clear(); val = def; }
	  int val = 0, def;
	  public float ctrl_factor = 2;
	  public sInt(sValueBloc b, int v, String n, String s) { super(b, "int", n, s); val = v; def = val; }
	  public int get() { return val; }
	  public void set(double v) { set((int)v); }
	  public void set(float v) { set((int)v); }
	  public void set(int v) { 
		    if (limited_max && v > max) v = max; if (limited_min && v < min) v = min;
		    if (v != val) { val = v; doChange(); } }
	  void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
	    svb.newData("val", val);
	    svb.newData("min", min);
	    svb.newData("max", max);
	    svb.newData("lmin", limited_min);
	    svb.newData("lmax", limited_max);
	  }
	  void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
	    set(svb.getInt("val"));
	    min = svb.getInt("min");
	    max = svb.getInt("max");
	    limited_min = svb.getBoolean("lmin");
	    limited_max = svb.getBoolean("lmax");
	  }
	  public void add(int v) { set(get()+v); }
	  public void mult(int v) { set(get()*v); }
	  public void div(int v) { set(get()/v); }
	  public void div(float v) { set((int)(get()/v)); }
	  public void div(double v) { set((int)(get()/v)); }
	}
