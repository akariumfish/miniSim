package Macro;

import java.util.ArrayList;

import UI.nLinkedWidget;
import sData.nRunnable;
import sData.sBoo;
import sData.sValueBloc;

public class MBasic extends Macro_Bloc { 
	  Macro_Element elem_com;
	  
	  sBoo param_view;
	  
	  nLinkedWidget param_ctrl;
	  nRunnable pview_run;
	  
	  boolean rebuilding = false;
	  MBasic(Macro_Sheet _sheet, String t, sValueBloc _bloc) { 
	    super(_sheet, t, t, _bloc); 
	    
	    param_view = newBoo("com_param_view", "com_param_view", false);
	    
	    get_param_openner().setRunnable(new nRunnable() { public void run() { 
	        param_view.set(!param_view.get());
	        rebuild();
	      } });
	    init();
	    if (param_view.get()) build_param();
	    else build_normal();
	  }
	  void init() { ; }
	  void build_param() { addEmptyS(0); addEmptyS(1); }
	  void build_normal() { addEmptyS(0); addEmptyS(1); }
	  
	  void rebuild() {
	    if (!rebuilding) {
	      rebuilding = true;
	      
	      boolean was_select = mmain().selected_macro.contains(this);
	      
	      ArrayList<Macro_Abstract> prev_selected = new ArrayList<Macro_Abstract>();
	      for(Macro_Abstract m : mmain().selected_macro) if (m != this) prev_selected.add(m);
	      
	      sValueBloc _bloc = mmain().inter.data.copy_bloc(value_bloc, mmain().inter.data);
	      clear();
	      sValueBloc v_bloc = mmain().inter.data.copy_bloc(_bloc, sheet.value_bloc);
	      Macro_Abstract mv = sheet.addByValueBloc(v_bloc); 
	      
	      mmain().szone_clear_select();
	      for(Macro_Abstract m : prev_selected) m.szone_select();
	      if (was_select) mv.szone_select();
	    }
	  }
	  public MBasic clear() {
	    super.clear(); 
	    return this; }
	  public MBasic toLayerTop() {
	    super.toLayerTop(); 
	    return this; }
	}
