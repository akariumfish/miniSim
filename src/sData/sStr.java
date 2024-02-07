package sData;

import RApplet.RConst;

public class sStr extends sValue {
	  boolean limited; int max;
	  sStr set_limit(int ma) { limited = true; max = ma; return this; }
	  sStr clear_limit() { limited = false; return this; }
	  public String getString() { return RConst.copy(val); }
	  public void clear() { super.clear(); val = RConst.copy(def); }
	  String val = null, def;
	  public sStr(sValueBloc b, String v, String n, String s) { 
		  	super(b, "str", n, s); val = RConst.copy(v); def = RConst.copy(val); }
	  public String get() { return RConst.copy(val); }
	  public void set(String v) { 
	    if (!v.equals(val)) { 
	      if (limited && v.length() > max) val = v.substring(0, max); else val = RConst.copy(v); 
	      
	      //filter line return
	      for (int i = val.length() - 1 ; i >= 0  ; i--)
	        if (val.charAt(i) == '\n' || val.charAt(i) == '\r') {
	          val = val.substring(0, i);
	          if (i+1 < val.length()) val += val.substring(i + 1, val.length());
	      }
	      
	      doChange(); 
	    } 
	  }
	  void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
	    svb.newData("val", val);
	  }
	  void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
	    set(svb.getData("val"));
	  }
	}
