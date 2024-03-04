package Macro;

import java.util.ArrayList;

import UI.*;
import processing.core.PApplet;
import sData.*;

public class Macro_Element extends nDrawer implements Macro_Interf {
	
	nCtrlWidget addValuePanel(sValue v) {
		nCtrlWidget w = addCtrlModel("MC_Element_MiniButton")
	  	.setRunnable(new nRunnable(v) { public void run() {
		  sValue cible = (sValue)builder;
		  if (cible != null) {
	        if (cible.type.equals("str")) { 
	          new nTextPanel(bloc.mmain().screen_gui, bloc.mmain().inter.taskpanel, (sStr)cible);
	        } else if (cible.type.equals("flt")) { 
	          new nNumPanel(bloc.mmain().screen_gui, bloc.mmain().inter.taskpanel, (sFlt)cible);
	        } else if (cible.type.equals("int")) { 
	          new nNumPanel(bloc.mmain().screen_gui, bloc.mmain().inter.taskpanel, (sInt)cible);
	        } else if (cible.type.equals("boo")) { 
	          new nBinPanel(bloc.mmain().screen_gui, bloc.mmain().inter.taskpanel, (sBoo)cible);
	        } else if (cible.type.equals("col")) { 
	          new nColorPanel(bloc.mmain().screen_gui, bloc.mmain().inter.taskpanel, (sCol)cible);
	        } else if (cible.type.equals("vec")) { 
	          new nVecPanel(bloc.mmain().screen_gui, bloc.mmain().inter.taskpanel, (sVec)cible);
	        }
	      }
	  	}});
		return w;
	}

	  nCtrlWidget addTrigS(String l, nRunnable r) { 
		    return addCtrlModel("MC_Element_SButton", l).setRunnable(r);
		  }
	  Macro_Element addTrigSelector(int select_nb, String l, nRunnable r) { 
		addCtrlModel("MC_Element_Button_Selector_"+select_nb, l).setRunnable(r);
	    return this;
	  }
	  Macro_Element addTrigSelector(int select_nb, String l, String inf, nRunnable r) { 
		addCtrlModel("MC_Element_Button_Selector_"+select_nb, l)
			.setRunnable(r).setInfo(inf);
	    return this;
	  }
	  Macro_Element addSwitchSelector(int select_nb, String l, String inf, sBoo r) { 
		addLinkedModel("MC_Element_Button_Selector_"+select_nb, l)
			.setLinkedValue(r).setInfo(inf);
	    return this;
	  }
	  Macro_Element addSwitchXLSelector(int select_nb, String l, String inf, sBoo r) { 
		addLinkedModel("MC_Element_XLButton_Selector_"+select_nb, l)
			.setLinkedValue(r).setInfo(inf);
	    return this;
	  }
	  nLinkedWidget addSwitchS(String l, sBoo r) { 
		    return addLinkedModel("MC_Element_SButton", l).setLinkedValue(r);
		  }
	  nLinkedWidget addLinkedMiniSwitch(String l, sBoo r) { 
		    return addLinkedModel("MC_Element_MiniButton", l).setLinkedValue(r);
		  }
	  nCtrlWidget addTrigSwtchS(String sw_txt, sBoo vb, String bp_txt, nRunnable r) { 
	    nCtrlWidget cw = addCtrlModel("MC_Element_SButton", bp_txt).setRunnable(r);
	    addLinkedModel("MC_Element_MiniButton", sw_txt).setLinkedValue(vb);
	    return cw;
	  }
	  
	  Macro_Element mirror(boolean b) { 
		  if (connect != null) connect.mirror(b);
		  if (sheet_connect != null) sheet_connect.mirror(b);
		  int f = 1;
		  if (bloc.shelfs.get(2).drawers.size() > 0) f = 2;
		  if (    bloc.shelfs.get(1).drawers.size() > 0 || 
				  bloc.shelfs.get(2).drawers.size() > 0) {
			  if 		(b && shelf_ind == 0) 	back.setPX(ref_size*2.25*f);
			  else if 	(b && shelf_ind == f) 	back.setPX(-ref_size*2.25*f);
			  else 								back.setPX(0);
		  } else back.setPX(0);
	      return this;
	  }
	  int shelf_ind = 0, row_ind = 0;

	  nWidget back = null, spot = null;
	  Macro_Connexion connect = null, sheet_connect = null;
	  Macro_Bloc bloc;
	  boolean sheet_viewable = false, was_viewable = false;
	  String descr;
	  sObj val_self;
	  nRunnable spot_select_run;
	  Macro_Element(Macro_Bloc _bloc, String _ref, String _model, String _info, int co_side, int sco_side, boolean sheet_view) {
	    super(_bloc.getShelf(), _bloc.ref_size*2.25F, _bloc.ref_size*1.125F);
	    bloc = _bloc; sheet_viewable = sheet_view; was_viewable = sheet_view; 
	    back = addModel(_model).setText(_ref).setPassif(); 
	    spot_select_run = new nRunnable(this) { public void run() { 
	          bloc.sheet.selecting_element((Macro_Element)builder); } };
	    back.addEventTrigger(spot_select_run);
	    
	    descr = BLOC_TOKEN+bloc.value_bloc.ref+BLOC_TOKEN+"_elem_"+bloc.elements.size();
	    
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
	    spot = _spot; 
	    spot.setLook("MC_Element_At_Spot").setPassif(); 
	    if (sd.equals("right")) spot.setTextAlignment(PApplet.LEFT, PApplet.TOP);
	    else spot.setTextAlignment(PApplet.BOTTOM, PApplet.BOTTOM);//BOTTOM = alignRight
	    back.setLook("MC_Element_At_Spot").setPassif(); 
	    spot.setText(bloc.value_bloc.base_ref);
	    spot.addEventTrigger(spot_select_run);
	    sheet_viewable = false; 
	  }
	  void clear_spot() { 
	    if (spot != null) spot.setText("");//.clear();
	    back.clearParent(); back.setParent(ref); 
//	    if (spot != null) bloc.sheet.remove_spot(descr);
	    spot = null; back.setLook("MC_Element").setPassif(); 
	    sheet_viewable = was_viewable; //if (sheet_connect != null) sheet_connect.hide(); 
	  }
	    
	  public Macro_Element clear() { 
	    if (connect != null) bloc.sheet.child_connect.remove(connect);
	    if (sheet_connect != null) bloc.sheet.sheet.child_connect.remove(sheet_connect);
	    if (connect != null) connect.clear(); 
	    if (sheet_connect != null) sheet_connect.clear(); 
	    clear_spot();
	    bloc.sheet.child_elements.remove(this);
	    back.clear();
	    super.clear(); 
	    return this;
	  }
	  
	  Macro_Element show() {
	    
	    if (bloc.mmain().show_macro.get()) {
		    if (bloc.sheet.openning.get() == DEPLOY && 
			    		bloc.openning.get() == OPEN) {
			    back.clearParent(); back.setParent(ref); 
//			    back.setPX(-ref_size*0.5);
		    		back.show();
		    		for (nWidget w : elem_widgets) w.show();
		    		if (spot != null) { 
		    			if (bloc.sheet.openning.get() != OPEN) spot.show();
		    			else spot.hide(); 
		    			spot.toLayerTop(); 
		    		} else back.toLayerTop(); 
		    } else if (bloc.sheet.openning.get() == OPEN && spot != null) {
			    back.clearParent(); back.setParent(spot);
//			    back.setPX(0);

		    		back.show();
		    		for (nWidget w : elem_widgets) w.show();
		    		if (spot != null) { 
		    			if (bloc.sheet.openning.get() != OPEN) spot.show();
		    			else spot.hide(); }
		    } else {
			      back.hide(); 
			      for (nWidget w : elem_widgets) w.hide();
			      if (spot != null) spot.hide();
			} 
	    } else {
	      back.hide(); 
	      for (nWidget w : elem_widgets) w.hide();
	      if (spot != null) spot.hide();
	    } 
	    toLayerTop();
//	    
	    if (sheet_connect != null) { sheet_connect.upview(); sheet_connect.toLayerTop(); }
	    if (connect != null)  { connect.upview(); connect.toLayerTop(); }
	    
	    return this;
	  }
	  Macro_Element reduc() {
	    
	    back.hide(); 
	    for (nWidget w : elem_widgets) w.hide();
	    if (spot != null) spot.hide();
	    
	    if (sheet_connect != null) { sheet_connect.upview(); sheet_connect.toLayerTop(); }
	    if (connect != null)  { connect.upview(); connect.toLayerTop(); }
	      
	    return this;
	  }
	  
	  Macro_Element hide() {
	    if (bloc.mmain().show_macro.get()) {
		    if (bloc.sheet.openning.get() == DEPLOY && 
			    		bloc.openning.get() == OPEN) {
			    back.clearParent(); back.setParent(ref); 
//			    back.setPX(-ref_size*0.5);
		    		back.show();
		    		for (nWidget w : elem_widgets) w.show();
		    		if (spot != null) spot.show();
		    		if (spot != null) spot.toLayerTop();
		    		else back.toLayerTop(); 
		    } else if (bloc.sheet.openning.get() == OPEN && spot != null) {
			    back.clearParent(); back.setParent(spot);
//			    back.setPX(0);

		    		back.show();
		    		for (nWidget w : elem_widgets) w.show();
		    		
	    			spot.show(); 
		    		spot.toLayerTop();
		    } else {
			      back.hide(); 
			      for (nWidget w : elem_widgets) w.hide();
			      if (spot != null) spot.hide();
			} 
	    } else {
	      back.hide(); 
	      for (nWidget w : elem_widgets) w.hide();
	      if (spot != null) spot.hide();
	    } 
	    
	    if (sheet_connect != null) { sheet_connect.upview(); sheet_connect.toLayerTop(); }
	    if (connect != null)  { connect.upview(); connect.toLayerTop(); }
	    
	    return this;
	  }

//	  public Macro_Element buildLayer() { 
//		  back.getDrawable().clear_coDrawer();
//		  buildLayerTo(back.getDrawable()); 
//	  return this; }
//	  public Macro_Element buildLayerTo(Drawable d) { 
//
//		  for (nWidget w : elem_widgets) 
//			  if (w != null && w != back && back != null) 
//  			d.add_coDrawer(w.getDrawable()); 
//	    if (sheet_connect != null) sheet_connect.buildLayerTo(d); 
//	    if (connect != null) connect.buildLayerTo(d); 
//		return this; }
	  public Macro_Element toLayerTop() { 
	    if (!gui.app.DEBUG_NOTOLAYTOP) super.toLayerTop(); 
    		for (nWidget w : elem_widgets) w.toLayerTop();
//	    		back.toLayerTop();  // better
    	    if (sheet_connect != null) sheet_connect.toLayerTop(); 
    	    if (connect != null) connect.toLayerTop(); 
	    return this;
	  }
	  public Macro_Element widget_toLayTop() { 
		  if (spot != null) spot.toLayerTop();
		  back.toLayerTop();
	    		for (nWidget w : elem_widgets) if (w != back) w.toLayerTop();
	    	    if (sheet_connect != null) sheet_connect.toLayerTop(); 
	    	    if (connect != null) connect.toLayerTop(); 
	    return this;
	  }
	  ArrayList<nWidget> elem_widgets = new ArrayList<nWidget>();
	  protected nWidget customBuild(nWidget w) { 
		  super.customBuild(w);
	    if (elem_widgets != null) { 
	    		elem_widgets.add(w); 
	    }
	    if (bloc != null && bloc.sheet.openning.get() != DEPLOY) w.hide();
	    if (w != back) w.setParent(back);
	    return w.setDrawer(this); 
	  }
	  
	}
