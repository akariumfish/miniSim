package Macro;

import java.util.ArrayList;

import sData.sValueBloc;

public abstract class Sheet_Specialize {
	  static int count = 0;
	  static ArrayList<Sheet_Specialize> prints = new ArrayList<Sheet_Specialize>();
	  
	  public Macro_Main mmain;
	  public String name, build_access = "all";
	  public int sheet_count = -1;
	  public boolean unique = false;
	  
	  public Sheet_Specialize(String n) { name = n;  
	    prints.add(this); 
	    count++;
	  }
	  public Sheet_Specialize(String n, String dt) { name = n;  
	    prints.add(this); 
	    count++;
	    default_template = dt;
	  }
	  
	  String default_template = "base_sheet";
	  
	  Macro_Sheet add_new(Macro_Sheet s, sValueBloc b, Macro_Sheet p ) { 
	    if (mmain.canAccess(build_access) && (!unique || (unique && sheet_count == -1))) { 
	      sheet_count++; 
	      Macro_Sheet m = null;
	      if (b == null && p == null) m = get_new(s, name + "_" + sheet_count, (sValueBloc)null);
	      else if (b != null) m = get_new(s, b.base_ref, (sValueBloc)b);
	      else if (p != null) m = get_new(s, p.value_bloc.base_ref, p);
	      if (m != null) {
	        m.sheet_specialize = this; m.specialize.set(name); if (unique) m.unclearable = true;
//	        m.open();
	        if (b == null && default_template.length() > 0 && mmain.saved_template.getBloc(default_template) != null) {
	          Macro_Sheet prev_select = mmain.selected_sheet;
	          m.select();
	          mmain.paste_tmpl(mmain.saved_template.getBloc(default_template));
	          prev_select.select();
//	          m.delayed_open();
	        }
	  		// !!! if build from bloc, need to do init elsewhere
    	  		if (b == null) m.init_end(); 
	      }
	      return m; } 
	    else return null; }
	  public  abstract Macro_Sheet get_new(Macro_Sheet s, String n, sValueBloc b);
	  public  Macro_Sheet get_new(Macro_Sheet s, String n, Macro_Sheet b) { return null; }
	}



	class SheetPrint extends Sheet_Specialize {
	  SheetPrint() { super("sheet", ""); }
	  public Macro_Sheet get_new(Macro_Sheet s, String n, sValueBloc b) { return new Macro_Sheet(s, n, b); }
	}
