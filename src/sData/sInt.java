package sData;

public class sInt extends sValue {
	  boolean limited_min = false, limited_max = false; int min, max;
	  public sInt set_limit(int mi, int ma) { limited_min = true; limited_max = true; min = mi; max = ma; return this; }
	  
	  public sInt set_min(float mi) { limited_min = true; min = (int)(mi); return this; }
	  public sInt set_max(float ma) { limited_max = true; max = (int)(ma); return this; }
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
	  public void set(int v) { 
	    if (limited_max && v > max) v = max; if (limited_min && v < min) v = min;
	    if (v != val) { val = v; doChange(); } }
	  public void add(int v) { set(get()+v); }
	  void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
	    svb.newData("val", val);
	  }
	  void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
	    set(svb.getInt("val"));
	  }
	}
