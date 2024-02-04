package Macro;

import java.util.ArrayList;

import UI.nDrawer;
import UI.nWidget;
import sData.nRunnable;
import sData.sObj;

public class Macro_Element extends nDrawer implements Macro_Interf {
	  Macro_Bloc getBloc() { return bloc; }

	  nWidget back = null, spot = null;
	  Macro_Connexion connect = null, sheet_connect = null;
	  Macro_Bloc bloc;
	  boolean sheet_viewable = false, was_viewable = false;
	  String descr;
	  sObj val_self;
	  Macro_Element(Macro_Bloc _bloc, String _ref, String _model, String _info, int co_side, int sco_side, boolean sheet_view) {
	    super(_bloc.getShelf(), _bloc.ref_size*1.375F, _bloc.ref_size);
	    bloc = _bloc; sheet_viewable = sheet_view; was_viewable = sheet_view; 
	    back = addModel(_model).setText(_ref).setPassif(); 
	    
	    //elem_widgets.remove(back);
	    
	    descr = BLOC_TOKEN+bloc.value_bloc.ref+BLOC_TOKEN+"_elem_"+bloc.elements.size();
	    //val_self = ((sObj)(bloc.setting_bloc.getValue(descr+"_self"))); 
	    //if (val_self == null) val_self = bloc.setting_bloc.newObj(descr+"_self", this);
	    //else val_self.set(this);
	    
	    back.addEventTrigger(new nRunnable(this) { public void run() { 
	          bloc.sheet.selecting_element((Macro_Element)builder); } });
	    
	    if (sheet_view) bloc.sheet.child_elements.add(this);
	    if (back != null && sco_side != NO_CO && bloc.sheet != bloc.mmain()) 
	      sheet_connect = new Macro_Connexion(this, bloc.sheet.sheet, sco_side, _info, true); //_info
	    if (back != null && co_side != NO_CO) 
	      connect = new Macro_Connexion(this, bloc.sheet, co_side, _info, false); //_info
	    //if (sheet_connect != null) sheet_connect.hide(); 
	    //if (connect != null) connect.hide(); 
	  }
	  String side = "";
	  void set_spot(nWidget _spot, String sd) { 
	    side = sd;
	    spot = _spot; spot.setLook("MC_Element_At_Spot").setPassif(); back.setLook("MC_Element_At_Spot").setPassif(); 
	    spot.setText(bloc.value_bloc.base_ref);
	    sheet_viewable = false; 
	  }
	  void clear_spot() { 
	    if (spot != null) spot.setText("");
	    spot = null; back.setLook("MC_Element").setPassif(); 
	    sheet_viewable = was_viewable; //if (sheet_connect != null) sheet_connect.hide(); 
	  }
	    
	  public Macro_Element clear() { 
	    if (connect != null) bloc.sheet.child_connect.remove(connect);
	    if (sheet_connect != null) bloc.sheet.sheet.child_connect.remove(sheet_connect);
	    if (connect != null) connect.clear(); 
	    if (sheet_connect != null) sheet_connect.clear(); 
	    clear_spot();
	    if (spot != null) bloc.sheet.remove_spot(descr);
	    bloc.sheet.child_elements.remove(this);
	    super.clear(); 
	    return this;
	  }
	  
	  Macro_Element show() {
	    
	    back.clearParent(); back.setParent(ref); 
	    back.setPX(-ref_size*0.5);
	    if (bloc.openning.get() == OPEN && bloc.mmain().show_macro.get()) {
	      back.show(); 
	      for (nWidget w : elem_widgets) w.show();
	    } else { 
	      back.hide(); 
	      for (nWidget w : elem_widgets) w.hide();
	    } 
	    toLayerTop();
	    
	    if (sheet_connect != null) { sheet_connect.upview(); sheet_connect.toLayerTop(); }
	    if (connect != null)  { connect.upview(); connect.toLayerTop(); }
	    
	    return this;
	  }
	  Macro_Element reduc() {
	    
	    back.hide(); 
	    for (nWidget w : elem_widgets) w.hide();
	    
	    if (sheet_connect != null) { sheet_connect.upview(); sheet_connect.toLayerTop(); }
	    if (connect != null)  { connect.upview(); connect.toLayerTop(); }
	      
	    return this;
	  }
	  
	  Macro_Element hide() {
	    
	    if (bloc.sheet.openning.get() == OPEN && spot != null && bloc.mmain().show_macro.get()) {
	      
	      back.clearParent(); back.setParent(spot);
	      //if () 
	      back.show(); 
	      back.setPX(0);
	      for (nWidget w : elem_widgets) w.show();
	      toLayerTop();
	    } else { 
	      back.hide(); 
	      for (nWidget w : elem_widgets) w.hide();
	    } 
	    
	    if (sheet_connect != null) { sheet_connect.upview(); sheet_connect.toLayerTop(); }
	    if (connect != null)  { connect.upview(); connect.toLayerTop(); }
	    
	    return this;
	  }
	  
	  public Macro_Element toLayerTop() { 
	    super.toLayerTop(); 
	    for (nWidget w : elem_widgets) w.toLayerTop();
	    if (sheet_connect != null) sheet_connect.toLayerTop(); 
	    if (connect != null) connect.toLayerTop(); 
	    return this;
	  }
	  ArrayList<nWidget> elem_widgets = new ArrayList<nWidget>();
	  protected nWidget customBuild(nWidget w) { 
	    if (elem_widgets != null) elem_widgets.add(w); 
	    if (bloc != null && bloc.sheet.openning.get() != DEPLOY) w.hide();
	    if (w != back) w.setParent(back);
	    return w.setDrawer(this); 
	  }
	  
	}