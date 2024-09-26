package sData;

public class sBoo extends sValue {
	  public void directshortcut_action() { run_events_allset(); val = !val; doChange(); }
	  public boolean asBoo() { return val; }
	  public String getString() { return String.valueOf(val); }
	  public void clear() { super.clear(); val = def; }
	  boolean val = false, def;
	  public sBoo(sValueBloc b, boolean v, String n, String s) { super(b, "boo", n, s); val = v; def = val; }
	  public sBoo(sValueBloc b, boolean v, String n, String s, char ct) { super(b, "boo", n, s); 
	  	val = v; def = val; set_directshortcut(ct); }
	  public boolean get() { return val; }
	  public void set(boolean v) { run_events_allset(); if (v != val) { val = v; doChange(); } }
	  void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
	    svb.newData("val", val);
	  }
	  void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
	    set(svb.getBoolean("val"));
	  }
	  public void swtch() { val = !val; doChange(); }
	}
