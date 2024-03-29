package z_old_specialise;

import java.util.ArrayList;

import Macro.Macro_Main;
import Macro.Macro_Sheet;
import sData.nRunnable;
import sData.sValueBloc;

public abstract class Sheet_Specialize {
	  static int count = 0;
	  public static ArrayList<Sheet_Specialize> prints = new ArrayList<Sheet_Specialize>();
	  
	  public Macro_Main mmain;
	  public String name, build_access = "all";
	  public int sheet_count = -1;
	  public boolean unique = false;
	  public boolean show_in_buildtool = false;
	  public boolean visible = true;
	  
	  
	  public Sheet_Specialize(String n) { name = n;  
	    prints.add(this); 
	    count++;
	  }
	  public Sheet_Specialize(String n, String dt) { name = n;  
	    prints.add(this); 
	    count++;
	    default_template = dt;
	  }
	  
	  String default_template = "";//base_sheet
	  
	  public Macro_Sheet add_new(Macro_Sheet s, sValueBloc b, Macro_Sheet p ) { 
	    if (mmain.canAccess(build_access) && (!unique || (unique && sheet_count == -1))) { 
	      sheet_count++; 
	      Macro_Sheet m = null;
	      if (b == null && p == null) m = get_new(s, name + "_" + sheet_count, (sValueBloc)null);
	      else if (b != null) m = get_new(s, b.base_ref, (sValueBloc)b);
	      else if (p != null) m = get_new(s, p.value_bloc.base_ref, p);
	      if (m != null) {
	        m.sheet_specialize = this; m.specialize.set(name); if (unique) m.unclearable = true;
//	        m.open();
	  		if (b == null) { 
	  			m.init_end(); 
	  			if (unique) {
	  				mmain.inter.addEventTwoFrame(new nRunnable(m) { public void run() { 
	  		  			((Macro_Sheet)builder).open(); 
	  				}});
	  			}
	  		}
//	        if (b == null && default_template.length() > 0 && mmain.saved_template.getBloc(default_template) != null) {
//	          Macro_Sheet prev_select = mmain.selected_sheet;
//	          m.select();
//	          mmain.paste_tmpl(mmain.saved_template.getBloc(default_template));
//	          prev_select.select();
////	          m.delayed_open();
//	        }
//	        if (unique || b != null) { m.open(); }
	  		// !!! if build from bloc, need to do init elsewhere
	      }
	      return m; } 
	    else return null; }
	  public  abstract Macro_Sheet get_new(Macro_Sheet s, String n, sValueBloc b);
	  public  Macro_Sheet get_new(Macro_Sheet s, String n, Macro_Sheet b) { return null; }
	}


