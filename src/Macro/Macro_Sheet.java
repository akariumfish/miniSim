package Macro;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import RApplet.sInterface;
import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import sData.*;
import z_old_specialise.Sheet_Specialize;


/*

sheet extend abstract
shelfpanel of shown bloc


methods for adding blocs inside

has spot for blocs to display when reducted
child bloc au dessus du panel can snap to spot

no sheet co, stick to a free place in the hard back to make a co 

quand une sheet est ouverte sont soft back est trensparent et sont parent est caché
seulement une top sheet ouverte a la fois
cant be grabbed when open

*/


//class SheetPrint extends Sheet_Specialize {
//  SheetPrint() { super("sheet", ""); }
//  public Macro_Sheet get_new(Macro_Sheet s, String n, sValueBloc b) { return new Macro_Sheet(s, n, b); }
//}

public class Macro_Sheet extends Macro_Abstract {
	
	class MSheet_Builder extends MAbstract_Builder {
		MSheet_Builder(Macro_Main m) { 
			super("sheet", "Sheet", "can contain blocs and organise them", "Sheet");
			first_start_show(m); }
		Macro_Sheet build(Macro_Sheet s, sValueBloc b) { return new Macro_Sheet(s, type, b); }
	}

	float elem_space = ref_size*2.5F;
	float minx = -elem_space; 
	float miny = -elem_space;
	float maxx = panel.getLocalX() + panel.getLocalSX() + elem_space;
	float maxy = panel.getLocalY() + panel.getLocalSY() + elem_space;
	void moving() { 
		updateBack(); 
		if (sheet != this) sheet.movingChild(this); }
	void movingChild(Macro_Abstract mov_m) { 
		updateBack(); 
	}
	void testChild(Macro_Abstract m) { 
		if (m.openning.get() == DEPLOY) {
          if (minx > m.grabber.getLocalX() + m.back.getLocalX() - elem_space) 
            minx = m.grabber.getLocalX() + m.back.getLocalX() - elem_space;
          if (miny > m.grabber.getLocalY() + m.back.getLocalY() - elem_space) 
            miny = m.grabber.getLocalY() + m.back.getLocalY() - elem_space;
          if (maxx < m.grabber.getLocalX() + m.back.getLocalX() + m.back.getLocalSX() + elem_space) 
            maxx = m.grabber.getLocalX() + m.back.getLocalX() + m.back.getLocalSX() + elem_space;
          if (maxy < m.grabber.getLocalY() + m.back.getLocalY() + m.back.getLocalSY() + elem_space) 
            maxy = m.grabber.getLocalY() + m.back.getLocalY() + m.back.getLocalSY() + elem_space;
        } else if (m.openning.get() == OPEN) {
          if (minx > m.grabber.getLocalX() + m.panel.getLocalX() - elem_space) 
            minx = m.grabber.getLocalX() + m.panel.getLocalX() - elem_space;
          if (miny > m.grabber.getLocalY() + m.panel.getLocalY() - elem_space) 
            miny = m.grabber.getLocalY() + m.panel.getLocalY() - elem_space;
          if (maxx < m.grabber.getLocalX() + m.panel.getLocalX() + m.panel.getLocalSX() + elem_space) 
            maxx = m.grabber.getLocalX() + m.panel.getLocalX() + m.panel.getLocalSX() + elem_space;
          if (maxy < m.grabber.getLocalY() + m.panel.getLocalY() + m.panel.getLocalSY() + elem_space) 
            maxy = m.grabber.getLocalY() + m.panel.getLocalY() + m.panel.getLocalSY() + elem_space;
        } else if (m.openning.get() == REDUC) {
          if (minx > m.grabber.getLocalX() - elem_space) 
            minx = m.grabber.getLocalX() - elem_space;
          if (miny > m.grabber.getLocalY() - elem_space) 
            miny = m.grabber.getLocalY() - elem_space;
          if (maxx < m.grabber.getLocalX() + m.grabber.getLocalSX() + elem_space) 
            maxx = m.grabber.getLocalX() + m.grabber.getLocalSX() + elem_space;
          if (maxy < m.grabber.getLocalY() + m.grabber.getLocalSY() + elem_space) 
            maxy = m.grabber.getLocalY() + m.grabber.getLocalSY() + elem_space;
        }
  }
  void updateBack() {
    if (openning.get() == DEPLOY) {
    		elem_space = ref_size*2.5F;
        minx = -elem_space; 
        miny = -elem_space;
        maxx = panel.getLocalX() + panel.getLocalSX() + elem_space;
        maxy = panel.getLocalY() + panel.getLocalSY() + elem_space;
    	  	for (Macro_Abstract m : child_macro) testChild( m);
          back.setPosition(minx, miny);
          back.setSize(maxx - minx, maxy - miny);
          if (sheet != this) sheet.updateBack();
    }
    
    mmain().update_select_bound();
  }
  public Macro_Sheet select() {
    if (mmain().selected_sheet != this) { 
      if (sheet != this && openning.get() != DEPLOY) deploy();
      if (!mmain().show_macro.get()) for (Macro_Abstract m : mmain().child_macro) m.hide();
      Macro_Sheet prev_selected = mmain().selected_sheet;
      mmain().selected_sheet.back_front.setOutline(false);
      if (mmain().selected_sheet.openning.get() == DEPLOY)
        mmain().selected_sheet.grabber.setLook(gui.theme.getLook("MC_Grabber_Deployed"));
      else mmain().selected_sheet.grabber.setLook(gui.theme.getLook("MC_Grabber"));
      for (Macro_Abstract m : mmain().selected_macro) m.szone_unselect();
      mmain().selected_macro.clear();
      mmain().selected_sheet = this;
      back_front.setOutline(true);
      grabber.setLook(gui.theme.getLook("MC_Grabber_Selected"));
      prev_selected.cancel_new_spot();
      cancel_new_spot();
      toLayerTop();
    }
    if (mmain().sheet_explorer != null) mmain().sheet_explorer.setBloc(value_bloc);
    return this;
  }
  Macro_Sheet deploy() {
    if (openning.get() != DEPLOY && 
        (!(openning.get() == HIDE) || (openning.get() == HIDE && mmain().canAccess(see_access))) ) {
      if (sheet != this && sheet.openning.get() != DEPLOY) sheet.deploy();
      openning.set(DEPLOY);
      title_fixe = true; 
      grabber.show(); panel.show(); back.show(); back_front.show();
      front.show(); title.show(); reduc.hide(); deployer.show();
      grabber.setLook(gui.theme.getLook("MC_Grabber_Deployed"));
      cancel_new_spot();
      left_spot_add.show(); right_spot_add.show();
      for (Macro_Abstract m : child_macro) { m.show(); 
//	    if (!gui.app.DEBUG_NOTOLAYTOP) 
	    	m.toLayerTop(); 
	  }
      updateBack(); 
      moving(); 
    }
    toLayerTop();
    return this;
  }
  public Macro_Sheet open() {
    if (sheet != this && openning.get() != OPEN && 
        (!(openning.get() == HIDE) || (openning.get() == HIDE && mmain().canAccess(see_access))) ) {
      openning.set(OPEN);
      title_fixe = true; 
      grabber.show(); panel.show(); back.hide(); back_front.hide();
      front.show(); title.show(); reduc.show(); deployer.show();
      reduc.setPosition(-ref_size, ref_size*0.375)
	    .setSize(ref_size*0.4, ref_size*0.75);;

//      reduc.setPosition(-ref_size * 0.75, ref_size*0.5)
//	    .setSize(ref_size*0.4, ref_size*0.75);
      grabber.setLook(gui.theme.getLook("MC_Grabber"));
      cancel_new_spot();
      left_spot_add.show(); right_spot_add.show();
      for (Macro_Abstract m : child_macro) m.hide();
      if (mmain().selected_sheet == this && sheet != this) sheet.select();
      moving(); toLayerTop();
      toLayerTop(); 
    }
    return this;
  }
  Macro_Sheet reduc() {
    if (sheet != this && openning.get() != REDUC && 
        (!(openning.get() == HIDE) || (openning.get() == HIDE && mmain().canAccess(see_access))) ) {
      openning.set(REDUC);
      title_fixe = false; 
      grabber.show(); panel.hide(); back.hide(); back_front.hide();
      front.hide(); 
      title.hide(); 
      reduc.show(); deployer.hide();
      reduc.show().setPosition(ref_size * 0.5, ref_size*0.5)
	    .setSize(ref_size*0.4, ref_size*0.5);
      grabber.setLook(gui.theme.getLook("MC_Grabber"));
      cancel_new_spot();
      left_spot_add.hide(); right_spot_add.hide();
      for (Macro_Abstract m : child_macro) m.hide();
      if (mmain().selected_sheet == this && sheet != this) sheet.select();
      moving(); toLayerTop();
      toLayerTop(); 
    }
    return this;
  }
  Macro_Sheet hide() {
    if (sheet != this && openning.get() != HIDE) {
      openning_pre_hide.set(openning.get());
      openning.set(HIDE);
      title_fixe = false; 
      cancel_new_spot();
      grabber.hide(); panel.hide(); back.hide(); back_front.hide();
      front.hide(); title.hide(); reduc.hide(); deployer.hide();
      left_spot_add.hide(); right_spot_add.hide();
      if (mmain().show_macro.get() && mmain().selected_sheet == this && sheet != this && sheet != mmain()) 
        sheet.select();
      toLayerTop(); 
    }
    for (Macro_Abstract m : child_macro) m.hide();
    return this;
  }
//
//  public Macro_Abstract buildLayer() { 
//	  panel.getDrawable().clear_coDrawer();
//	  buildLayerTo(panel.getDrawable()); 
//  return this; }
//  public Macro_Abstract buildLayerTo(Drawable d) { 
////	  d.clear_coDrawer();
//	  d.add_coDrawer(front.getDrawable()); 
//      if (child_macro != null) 
//  	  	for (Macro_Abstract e : child_macro) {
//  	  		e.buildLayerTo(d);
//  	  	}
//	  d.add_coDrawer(reduc.getDrawable()); 
//      reduc.getDrawable().clear_coDrawer(); 
//      reduc.getDrawable().add_coDrawer(prio_sub.getDrawable()); 
//      reduc.getDrawable().add_coDrawer(prio_add.getDrawable()); 
//      reduc.getDrawable().add_coDrawer(prio_view.getDrawable()); 
//      reduc.getDrawable().add_coDrawer(grabber.getDrawable()); 
//      if (deployer != null) reduc.getDrawable()
//      	.add_coDrawer(deployer.getDrawable()); 
//      reduc.getDrawable().add_coDrawer(title.getDrawable()); 
//      if (back_front != null) reduc.getDrawable()
//      	.add_coDrawer(back_front.getDrawable()); 
//
//  return this; }
//  
  public Macro_Sheet toLayerTop() { 
	  //if (!gui.app.DEBUG_NOTOLAYTOP) super.toLayerTop(); 
	  for (nWidget w : spot_empty_widget) w.toLayerTop(); 
	  if (child_macro != null) {
		  int max_l = 0;
		  for (Macro_Abstract e : child_macro) 
			  if (e.priority.get() > max_l) max_l = e.priority.get();
		  for (int i = 0 ; i <= max_l ; i++)
			  for (Macro_Abstract e : child_macro) 
				  if (e.priority.get() == i) e.toLayerTop(); 
      }
      panel.toLayerTop(); 
      front.toLayerTop(); back_front.toLayerTop(); 
      prio_sub.toLayerTop(); prio_add.toLayerTop(); prio_view.toLayerTop(); 
      title.toLayerTop(); grabber.toLayerTop(); deployer.toLayerTop(); 
      reduc.toLayerTop(); 
      return this;
  }
  
  void add_link(String in, String out) {
//    gui.app.mlogln(value_bloc.ref+ " add_link "+in+" "+out);
    String def = in+INFO_TOKEN+out+OBJ_TOKEN;
    links.set(links.get()+def);
    //redo_link();
    gui.app.mlogln(value_bloc.ref+ " add_link "+in+" "+out+ " end");
  }
  void remove_link(String in, String out) {
//    gui.app.mlogln(value_bloc.ref+ " remove_link "+in+" "+out);
    String[] links_list = PApplet.splitTokens(links.get(), OBJ_TOKEN);
    String new_list = "";
    for (String l : links_list) {
      String[] link_l = PApplet.splitTokens(l, INFO_TOKEN);
      String i = link_l[0]; String o = link_l[1];
//      gui.app.mlogln("try "+i+" "+o+" for "+in+" "+out);
      if (!(i.equals(in) && o.equals(out))) new_list += l+OBJ_TOKEN;
    }
    links.set(new_list);
    //redo_link();
    gui.app.mlogln(value_bloc.ref+ " remove_link "+in+" "+out+ " end");
  }
  void clear_link() {
    for (Macro_Connexion co1 : child_connect) co1.clear_link();
        //for (Macro_Connexion co2 : child_connect) if (co1 != co2) co1.disconnect_from(co2);
    //logln("cleared links:"+links.get());
    links.set("");
  }
  void redo_link() {
//    gui.app.mlogln(value_bloc.ref+ " redo_link ");
    String[] links_list = PApplet.splitTokens(links.get(), OBJ_TOKEN);
    for (Macro_Connexion co1 : child_connect) co1.clear_link_array();
    links.set("");
    //clear_link();  
    for (String l : links_list) {
      //logln("link "+l);
      String[] link_l = PApplet.splitTokens(l, INFO_TOKEN);
      if (link_l.length == 2) {
        String i = link_l[0]; String o = link_l[1];
        //logln("in "+i+" out "+o);
        Macro_Connexion in = null, out = null;
        for (Macro_Connexion co : child_connect) {
          if (co.descr.equals(i)) in = co;
          if (co.descr.equals(o)) out = co;
        }
        if (in != null && out != null) {
          //logln("connect");
          in.connect_to(out);
        }
      }
    }
    gui.app.mlogln(value_bloc.ref+ " redo_link end ");
  }
  
  //call by add_spot widget
  void new_spot(String side) {
	  if (!building_spot) {
		  if (side.equals("left")) {
			  left_spot_add.setText("Empty"); 
			  right_spot_add.setBackground().setLook("MC_Add_Spot_Passif");
		  }
		  if (side.equals("right")) {
			  right_spot_add.setText("Empty");
			  left_spot_add.setBackground().setLook("MC_Add_Spot_Passif");
		  }
		  for (Macro_Element m : child_elements) {
			  if (m.sheet_viewable) {
				  m.back.setTrigger().setLook("MC_Element_For_Spot"); /*event in init de l'element*/ 
				  for (nWidget w : m.elem_widgets) if (w != m.back) w.tempPassif(true);
			  } 
			  if (m.spot != null) {
				  m.spot.setTrigger(); /*event in init de l'element*/
			  }
		  }
		  for (nWidget w : spot_empty_widget) w.setTrigger();
		  building_spot = true;
		  new_spot_side = side;
		  mmain().inter.addEventFrame(new_spot_run);
	  } else {
		  add_spot(side, null);
	  }
  }
  ArrayList<nWidget> spot_empty_widget = new ArrayList<nWidget>();
  int spot_empty_nb = 0;
  void selecting_element(Macro_Element elem) { // called by eventTrigger of elem.back
	  if (elem.spot == null) {
		  add_spot(new_spot_side, elem);
		  cancel_new_spot();
	  } else {
		   remove_spot(elem.descr);
	  }
  }
  void cancel_new_spot() {
	for (nWidget w : spot_empty_widget) w.setBackground();
    for (Macro_Element m : child_elements) if (m.sheet_viewable) {
      m.back.setBackground().setLook("MC_Element"); 
	  if (m.spot != null) m.spot.setBackground(); 
    }
    mmain().inter.removeEventFrame(new_spot_run);
    if (openning.get() == DEPLOY && mmain().selected_sheet == this) {
      left_spot_add.setTrigger().setLook("MC_Add_Spot_Actif").setText("+"); 
      right_spot_add.setTrigger().setLook("MC_Add_Spot_Actif").setText("+"); }
    else {
      left_spot_add.setBackground().setLook("MC_Add_Spot_Passif").setText(""); 
      right_spot_add.setBackground().setLook("MC_Add_Spot_Passif").setText(""); }
    for (Macro_Element m : child_elements) {
    		for (nWidget w : m.elem_widgets) w.tempPassif(false);
    }
    building_spot = false; new_spot_side = "";
  }

  void add_spot(String side, Macro_Element elem) {
	  if (elem == null) {
		  gui.app.mlogln(value_bloc.ref+ " add_empty_spot "+side);
		  String[] spots_side_list = PApplet.splitTokens(spots.get(), GROUP_TOKEN);
	      String left_s = OBJ_TOKEN, right_s = OBJ_TOKEN;
	      if (spots_side_list.length == 2) { 
	        left_s = RConst.copy(spots_side_list[0]); right_s = RConst.copy(spots_side_list[1]); }

		  nWidget spot = null;
		  String ttl = BLOC_TOKEN + "empty" + BLOC_TOKEN + 
				  spot_empty_nb + OBJ_TOKEN;
	      if (side.equals("left")) {
	        left_s += ttl;
	        getShelf(0).removeDrawer(left_spot_drawer);
	        nDrawer d = getShelf(0).addDrawer(2.25, 1.125);
	        spot = d.addModel("MC_Panel_Spot_Back")
//	        		.setTextVisibility(false)
	        		.setText(ttl)
	        		.setTrigger();
	    	    spot.addEventTrigger(new nRunnable(spot) {public void run() {
	        			remove_empty_spot("left", (nWidget)builder); }});
	        getShelf(0).insertDrawer(left_spot_drawer);
	      } else if (side.equals("right")) {
	        right_s += ttl;
	        getShelf(1).removeDrawer(right_spot_drawer);
	        nDrawer d = getShelf(1).addDrawer(2.25, 1.125);
	        spot = d.addModel("MC_Panel_Spot_Back")
//	        		.setTextVisibility(false)
	        		.setText(ttl)
	        		.setTrigger();
	        spot.addEventTrigger(new nRunnable(spot) {public void run() {
	        			remove_empty_spot("right", (nWidget)builder); }});
	        getShelf(1).insertDrawer(right_spot_drawer);
	      }

	      String new_str = "";
	      new_str += left_s+GROUP_TOKEN+right_s;
	      spots.set(new_str);
	      if (spot != null) {
	    	  	spot_empty_widget.add(spot);
	    	  	spot_empty_nb++;
	        if (openning.get() != REDUC && openning.get() != HIDE) spot.show();
	        else spot.hide();
	      }

	      moving();
	      toLayerTop();
	  } else {
	    gui.app.mlogln(value_bloc.ref+ " add_spot "+side+" "+elem.descr);
	    if (elem.spot == null && sheet != this) {
	      String[] spots_side_list = PApplet.splitTokens(spots.get(), GROUP_TOKEN);
	      String left_s = OBJ_TOKEN, right_s = OBJ_TOKEN;
	      if (spots_side_list.length == 2) { 
	        left_s = RConst.copy(spots_side_list[0]); right_s = RConst.copy(spots_side_list[1]); }
	      
	      nWidget spot = null;
	      if (side.equals("left")) {
	        left_s += elem.descr + OBJ_TOKEN;
	        getShelf(0).removeDrawer(left_spot_drawer);
	        nDrawer d = getShelf(0).addDrawer(2.25, 1.125);
	        spot = d.addModel("MC_Panel_Spot_Back");
	        getShelf(0).insertDrawer(left_spot_drawer);
	      } else if (side.equals("right")) {
	        right_s += elem.descr + OBJ_TOKEN;
	        getShelf(1).removeDrawer(right_spot_drawer);
	        nDrawer d = getShelf(1).addDrawer(2.25, 1.125);
	        spot = d.addModel("MC_Panel_Spot_Back");
	        getShelf(1).insertDrawer(right_spot_drawer);
	      }
	      
	      elem.set_spot(spot,side);
	      String new_str = "";
	      new_str += left_s+GROUP_TOKEN+right_s;
	      spots.set(new_str);
	      if (spot != null) {
	        if (openning.get() != REDUC && openning.get() != HIDE) spot.show();
	        else spot.hide();
	        spot.setTrigger();
	      }
	      
	      moving();
	      toLayerTop();
	    }
	  }
      gui.app.mlogln(value_bloc.ref+ " add_spot end ");  
  }
  void remove_empty_spot(String side, nWidget spot) {
    gui.app.logln(value_bloc.ref+ " remove_empty_spot "+side);
    
//	    spot_empty_widget.remove(spot);
    
//	    nShelf sh = null;
//	    if (side.equals("left")) sh = getShelf(0);
//	    if (side.equals("right")) sh = getShelf(1);
//	    if (sh != null) sh.removeDrawer(spot.getDrawer()); 
//	    spot.getDrawer().clear();
//	    spot.clear();
    
    
    String[] spots_side_list = PApplet.splitTokens(spots.get(), GROUP_TOKEN);
    String left_s = OBJ_TOKEN, right_s = OBJ_TOKEN;
    if (spots_side_list.length == 2) { 
      left_s = RConst.copy(spots_side_list[0]); right_s = RConst.copy(spots_side_list[1]); }

    String[] list = PApplet.splitTokens(left_s, OBJ_TOKEN);
    left_s = OBJ_TOKEN;
    for (String s : list) if (!s.equals(spot.getText())) left_s += s + OBJ_TOKEN;
    list = PApplet.splitTokens(right_s, OBJ_TOKEN);
    right_s = OBJ_TOKEN;
    for (String s : list) if (!s.equals(spot.getText())) right_s += s + OBJ_TOKEN;
    
    String new_str =  left_s+GROUP_TOKEN+right_s;
    spots.set(new_str);
    
    redo_spot();
    gui.app.mlogln(value_bloc.ref+ " remove_spot end ");
  }
  void remove_spot(String ref) {
    gui.app.mlogln(value_bloc.ref+ " remove_spot "+ref);
    String[] spots_side_list = PApplet.splitTokens(spots.get(), GROUP_TOKEN);
    String left_s = OBJ_TOKEN, right_s = OBJ_TOKEN;
    if (spots_side_list.length == 2) { 
      left_s = RConst.copy(spots_side_list[0]); right_s = RConst.copy(spots_side_list[1]); }
    
    String[] list = PApplet.splitTokens(left_s, OBJ_TOKEN);
    left_s = OBJ_TOKEN;
    for (String s : list) if (!s.equals(ref)) left_s += s + OBJ_TOKEN;
    
    list = PApplet.splitTokens(right_s, OBJ_TOKEN);
    right_s = OBJ_TOKEN;
    for (String s : list) if (!s.equals(ref)) right_s += s + OBJ_TOKEN;
    
    String new_str =  left_s+GROUP_TOKEN+right_s;
    spots.set(new_str);
    
    redo_spot();
    gui.app.mlogln(value_bloc.ref+ " remove_spot end ");
  }
  void clear_spot() { //clear using and clear spot drawers
    spots.set(OBJ_TOKEN+GROUP_TOKEN+OBJ_TOKEN);
    //logln("clear_child_elem_spot");
    for (Macro_Element t : child_elements) t.clear_spot();
    
    //logln("clear_left");
    ArrayList<nDrawer> a = new ArrayList<nDrawer>();
    getShelf(0).removeDrawer(left_spot_drawer);
    for (nDrawer d : getShelf(0).drawers) a.add(d);
    for (nDrawer d : a) { getShelf(0).removeDrawer(d); d.clear(); }
    getShelf(0).insertDrawer(left_spot_drawer);
    
    //logln("clear_right");
    a.clear();
    getShelf(1).removeDrawer(right_spot_drawer);
    for (nDrawer d : getShelf(1).drawers) a.add(d);
    for (nDrawer d : a) { getShelf(1).removeDrawer(d); d.clear(); }
    getShelf(1).insertDrawer(right_spot_drawer);
    
    cancel_new_spot();
  }
  void redo_spot() {
    gui.app.mlogln(value_bloc.ref+ " redo_spot ");
    String[] spots_side_list = PApplet.splitTokens(spots.get(), GROUP_TOKEN);
    String left_s = OBJ_TOKEN, right_s = OBJ_TOKEN;
    if (spots_side_list.length == 2) { 
      left_s = RConst.copy(spots_side_list[0]); right_s = RConst.copy(spots_side_list[1]); }
    
    //logln("clear_spot");
    clear_spot();
    
    //logln("addl_spot");
    String[] list = PApplet.splitTokens(left_s, OBJ_TOKEN);
    for (String elem_ref : list) {
      Macro_Element e = null;
      for (Macro_Element t : child_elements) if (t.descr.equals(elem_ref)) { e = t; break; }
//      String[] l = PApplet.splitTokens(elem_ref, OBJ_TOKEN);
      if (e != null || elem_ref.equals(BLOC_TOKEN + "empty" + BLOC_TOKEN + "empty")) 
    	  	add_spot("left", e);
      
    }
    
    list = PApplet.splitTokens(right_s, OBJ_TOKEN);
    for (String elem_ref : list) {
      Macro_Element e = null;
      for (Macro_Element t : child_elements) if (t.descr.equals(elem_ref)) { e = t; break; }
      if (e != null || elem_ref.equals(BLOC_TOKEN + "empty" + BLOC_TOKEN + "empty")) 
    	  	add_spot("right", e);
    }
    gui.app.mlogln(value_bloc.ref+ " redo_spot end ");
  }
  //when a spot is used the ref of the element and the nb and side of the spot are saved into the string
  //when the sheet is open click on a spot to reassign it, 
  //  right click to cancel, left click on empty to clear assignment
  //two add spot button > add_spot(side)
   
   
  /*macro turn:
    no tick anywhere > simulation gives tick
    no frame loop, works only by reacting to gui or input event (for keyboard create a keypress/release event)
      only time bloc have frame loop, delay and pulse need them, get it throug gui
      
    when at a frame an out whant to send :
      all out who want to send do it, input save msg
      if an out have multiple exit packet are send in input priority order
      once no out whant to send all input process msg in function of the corresponding output priority 
      order and mark their out for sending eventually
      once all in have processed their msg we start again if there is an out who want to send
      careful! loop can occur, 1 turn delays will fix them
      
    when in a connexion recursive loop count the depth to detect loop and break them 
      show a popup and desactivate everything somehow
  */
  void ask_packet_process(Macro_Connexion co) {
//	  gui.app.plogln("Sheet " + value_bloc.ref+" ask_packet_process from "+co.descr);
    if (co.type == OUTPUT) out_to_process.add(co);
    if (co.type == INPUT) in_to_process.add(co);
    mmain().pross_in_waiting++;
    mmain().ask_packet_process(this);
  }

  LinkedBlockingQueue<Macro_Connexion> in_to_process = new LinkedBlockingQueue<Macro_Connexion>();
  LinkedBlockingQueue<Macro_Connexion> out_to_process = new LinkedBlockingQueue<Macro_Connexion>();
  LinkedBlockingQueue<Macro_Connexion> co_processed = new LinkedBlockingQueue<Macro_Connexion>();
  boolean DEBUG_PACKETS = true;
  int process_packets(int max_turn) {
	  int turn_count = 0;
    if (!mmain().do_packet.get()) {
      in_to_process.clear();
      out_to_process.clear();
    } else {
    	gui.app.plogln("");
    	gui.app.plogln(value_bloc.ref+" packet process start ");
      String procc_resum = value_bloc.ref + " process resum ";
      
      boolean done = false; 
      while (!done && (turn_count < max_turn || mmain().loading_delay > 0)) {
        done = true;
        gui.app.plogln("turn " + turn_count);
        gui.app.plogln("   start:in" + in_to_process.size() + "-out" + out_to_process.size() + " OUTs process send");
        while(out_to_process.size() > 0) {
          Macro_Connexion m = out_to_process.remove();
          done = m.process_send() && done;
          procc_resum += m.process_resum;
          mmain().pross_in_waiting--;
          co_processed.add(m);
        }
        gui.app.plogln("   mid:in" + in_to_process.size() + "-out" + out_to_process.size() + " INs process receive");
        while(in_to_process.size() > 0) {
          Macro_Connexion m = in_to_process.remove();
          done = m.process_receive() && done;
          procc_resum += m.process_resum;
          mmain().pross_in_waiting--;
          co_processed.add(m);
        }
        gui.app.plogln("   end:in" + in_to_process.size() + "-out" + out_to_process.size() + "");

        done = out_to_process.size() == 0 && in_to_process.size() == 0 && done;
        
        turn_count++;
      }
      if (max_turn == turn_count)  { 
//    	  	gui.app.plogln("-----------MAX TURN-----------");
    	  	
      }
      gui.app.plogln("");
      gui.app.plogln(procc_resum);
      gui.app.plogln("");
      
      for (Macro_Connexion m : co_processed) m.end_packet_process();
      co_processed.clear();
      
    }
    return turn_count;
  }
  
  
   
  /*
  access system :
    sheet can only be deployed if you have access to them, a low access score can even hide a sheet to you
    introduce the "user" consept (just a keyword for now)
    each sheet have a str with keywords for complete and restricted access
      complete mean can deploy restricted mean can see it
  */
  String see_access = "all", deploy_access = "all";
  Macro_Sheet setSeeAccess(String a) {
    see_access = a;
    if (!mmain().canAccess(a) && openning.get() != HIDE) hide();
    return this;
  }
  Macro_Sheet setDeployAccess(String a) {
    deploy_access = a;
    if (!mmain().canAccess(a) && openning.get() == DEPLOY) open();
    return this;
  }
  
  sStr links;
  sStr spots;
  sInt val_max_bloc;
  nWidget right_spot_add, left_spot_add;
  boolean building_spot = false;
  String new_spot_side = "";
  nRunnable new_spot_run;
  nDrawer right_spot_drawer, left_spot_drawer;
  
  ArrayList<Macro_Connexion> child_connect = new ArrayList<Macro_Connexion>(0);
  ArrayList<Macro_Element> child_elements = new ArrayList<Macro_Element>(0);
  ArrayList<Macro_Abstract> child_macro = new ArrayList<Macro_Abstract>(0);
  ArrayList<Macro_Sheet> child_sheet;
  
  nWidget back_front, deployer;
  nRunnable szone_run;
  
  public sStr specialize;
  public Sheet_Specialize sheet_specialize = null;
  
  public sObj val_sheet;
  
  
 
  
//void delayed_open() {
//  mmain().inter.addEventTwoFrame(new nRunnable(this) { public void run() { open(); } });
//}
    
public  Macro_Sheet(Macro_Sheet p, String n, sValueBloc _bloc) { 
    super(p, "sheet", n, _bloc); init(); 
    
    gui.app.mlogln("build sheet "+n+" from "+ _bloc);
    
  }
  public Macro_Sheet(sInterface _int) {
    super(_int);
    gui.app.mlogln("build main sheet ");
    child_sheet = new ArrayList<Macro_Sheet>(0);
    new_preset_name = setting_bloc.newStr("preset_name", "preset", "preset");
    specialize = setting_bloc.newStr("specialize", "specialize", "");
    links = setting_bloc.newStr("links", "links", "");
    spots = setting_bloc.newStr("spots", "spots", OBJ_TOKEN+GROUP_TOKEN+OBJ_TOKEN);
    val_sheet = setting_bloc.newObj("sheet_obj", "sheet_obj");
    val_sheet.set(this);
    val_max_bloc = setting_bloc.newInt(50, "val_max_bloc", "val_max_bloc");
    addShelf(); addShelf();
    left_spot_add = addModel("mc_ref");
    right_spot_add = addModel("mc_ref");
    back_front = addModel("mc_ref");
    deployer = addModel("mc_ref"); 
  }
  void init() {
    child_sheet = new ArrayList<Macro_Sheet>(0);
    sheet.child_sheet.add(this);
    
    links = ((sStr)(setting_bloc.getValue("links"))); 
    if (links == null) links = setting_bloc.newStr("links", "links", "");
    
    spots = ((sStr)(setting_bloc.getValue("spots"))); 
    if (spots == null) spots = setting_bloc.newStr("spots", "spots", OBJ_TOKEN+GROUP_TOKEN+OBJ_TOKEN);
    
    new_preset_name = ((sStr)(setting_bloc.getValue("preset_name"))); 
    if (new_preset_name == null) new_preset_name = setting_bloc.newStr("preset_name", "preset", "new");
    specialize = ((sStr)(setting_bloc.getValue("specialize"))); 
    if (specialize == null) specialize = setting_bloc.newStr("specialize", "specialize", "sheet");

    val_sheet = ((sObj)(setting_bloc.getValue("sheet_obj"))); 
    if (val_sheet == null) val_sheet = setting_bloc.newObj("sheet_obj", "sheet_obj");
    val_sheet.set(this);

    val_max_bloc = ((sInt)(setting_bloc.getValue("val_max_bloc"))); 
    if (val_max_bloc == null) val_max_bloc = setting_bloc.newInt(50, "val_max_bloc", "val_max_bloc");
    
    back_front = addModel("MC_Front_Sheet")
      .clearParent().setPassif();
    back_front.setParent(back);
    back.addEventShapeChange(new nRunnable() { public void run() {
      back_front.setSize(back.getLocalSX(), back.getLocalSY()); } } );
    
    deployer = addModel("MC_Deploy").clearParent();
    deployer.setParent(panel);
    deployer.alignDown().stackRight().addEventTrigger(new nRunnable() { public void run() { 
      if (openning.get() == DEPLOY) open(); else { deploy(); select(); } } });
    
//    title.setPX(ref_size*3.25);

    left_spot_drawer = addShelf().addDrawer(2.25, 1.125);
    left_spot_add = left_spot_drawer.addModel("MC_Add_Spot_Passif")
      .addEventTrigger(new nRunnable() { public void run() { 
        new_spot("left"); 
      } });
    right_spot_drawer = addShelf().addDrawer(2.25, 1.125);
    right_spot_add = right_spot_drawer.addModel("MC_Add_Spot_Passif")
      .addEventTrigger(new nRunnable() { public void run() { 
        new_spot("right");
      } });
    
    new_spot_run = new nRunnable() { public void run() { 
        if (mmain().inter.input.getClick("MouseRight")) cancel_new_spot(); } };
    
    szone_run = new nRunnable(this) { public void run() { 
      if (openning.get() != REDUC && mmain().search_sheet.sheet_depth < sheet_depth && 
          mmain().szone.isUnder(back_front)) { 
        mmain().search_sheet = ((Macro_Sheet)builder);
      }
    } };
    
    mmain().szone.addEventStartSelect(szone_run);
    
    updateBack();
    
  }
	public void init_end() {
	  if (openning.get() == REDUC) { openning.set(OPEN); reduc(); }
      else if (openning.get() == OPEN) { openning.set(REDUC); open(); }
      else if (openning.get() == HIDE) { openning.set(openning_pre_hide.get()); hide(); }
      else if (openning.get() == DEPLOY) { openning.set(OPEN); deploy(); }
	  if (!loading_from_bloc) { 
		deploy();
		if (!specialize.get().equals("sheet"))
		mmain().inter.addEventNextFrame(new nRunnable(this) { public void run() { 
			MSheetBloc m = new MSheetBloc(((Macro_Sheet)builder), null);
			m.init_end();
			m.grabber.setPosition(ref_size*8, 0);
			updateBack();
		}});
		sheet.select();
		find_place(back);
		if (sheetCursor != null) sheetCursor.pval.set(grabber.getLocalX(), grabber.getLocalY());
		select();
	  }
      if (!mmain().show_macro.get()) hide();
      if (mmain().sheet_explorer != null) mmain().sheet_explorer.update(); 
      nRunnable.runEvents(eventsSetupLoad); 
//      mmain().buildLayer();
//      toLayerTop(); 
	}
  
  public Macro_Sheet clear() {
    //an unclearable sheet still need to clear child macro
    empty();
    if (!unclearable) {
      super.clear();
	  for (nWidget w : spot_empty_widget) w.clear(); 
	  spot_empty_widget.clear();
      if (sheetCursor != null) sheetCursor.clear();
	  if (sheetCursor_pval != null) sheetCursor_pval.removeEventChange(pval_run);
	  if (sheet != mmain()) sheet.grab_pos.removeEventChange(sheet_grab_run);
      sheet.child_sheet.remove(this);
      value_bloc.clear();
      if (mmain() != this) mmain().szone.removeEventStartSelect(szone_run);
      if (preset_explorer != null) mmain().presets_explorers.remove(preset_explorer);
//      sheet_specialize.sheet_count--;
    }
    return this;
  }
  Macro_Sheet empty() {
    clear_spot();
    for (int i = child_macro.size() - 1 ; i >= 0 ; i--) child_macro.get(i).clear();
    if (!unclearable && mmain() != this) child_sheet.clear();
    updateBack();
    return this;
  }
  
  String resum_link() { 
    String r = "";
    for (Macro_Element m : child_elements) {
      if (m.sheet_connect != null) for (Macro_Connexion co : m.sheet_connect.connected_inputs) 
        r += co.descr + INFO_TOKEN + m.sheet_connect.descr + OBJ_TOKEN;
      if (m.sheet_connect != null) for (Macro_Connexion co : m.sheet_connect.connected_outputs) 
        r += m.sheet_connect.descr + INFO_TOKEN + co.descr + OBJ_TOKEN;
    }
    return r; 
  }
  
  nFrontTab custom_tab;
  
  Macro_Sheet addEventsBuildMenu(nRunnable nRunnable) { eventsBuildMenu.add(nRunnable); return this; }
  ArrayList<nRunnable> eventsBuildMenu = new ArrayList<nRunnable>();
  

  public ArrayList<nCursor> sheet_cursors_list = new ArrayList<nCursor>();
  
  public ArrayList<Macro_Bloc> sheet_unique_bloc = new ArrayList<Macro_Bloc>();
  
//  protected MMenu sheet_menu_bloc = null;
  
  int cursor_count = 0;
  nCursor newCursor(String r, boolean b) {
	    cursor_count++;
	    nCursor c = new nCursor(this, r, r, b);
	    mmain().cursors_list.add(c);
//	    mmain().update_cursor_selector_list();
	    sheet_cursors_list.add(c);
	    c.addEventClear(new nRunnable(c) { public void run() { 
	        sheet_cursors_list.remove(((nCursor)builder));
	      mmain().cursors_list.remove(((nCursor)builder)); 
//	      mmain().update_cursor_selector_list(); 
	    }});
	    return c;
	  }

  public nCursor sheetCursor;
  public sVec sheetCursor_pval = null;
  public sVec sheetCursor_dval = null;
  public sBoo sheetCursor_show = null;
	nRunnable sheet_grab_run, pval_run;
  nCursor newSheetCursor(String r, boolean b) {
		sheetCursor_pval = newVec("pos", "pos");
	    sheetCursor_show = newBoo(false, "show", "show"); //!!!!! is hided by default
	    sheetCursor_dval = newVec("dir", "dir");
	    sheetCursor = new nCursor(mmain(), value_bloc, true);
	    mmain().cursors_list.add(sheetCursor);
//	    mmain().update_cursor_selector_list();
	    sheet.sheet_cursors_list.add(sheetCursor);
	    sheet.cursor_count++;
	    
	    setPosition(sheetCursor_pval.get().x - sheet.grabber.getX(), 
					sheetCursor_pval.get().y - sheet.grabber.getY());
	    moving();	
	    
	    sheetCursor.addEventClear(new nRunnable(sheetCursor) { public void run() { 
	        sheet.sheet_cursors_list.remove(((nCursor)builder));
	      mmain().cursors_list.remove(((nCursor)builder)); 
	      sheet.cursor_count--;
//	      mmain().update_cursor_selector_list(); 
	      }});
	    
	    grabber.addEventDrag(new nRunnable() { public void run() {
	    	if (sheetCursor.pval != null) sheetCursor.pval.set(grabber.getX(), grabber.getY());
	    } });
	    pval_run = new nRunnable() { public void run() {
	    	if (sheet != mmain())
	    	setPosition(sheetCursor.pval.get().x - sheet.grabber.getX(), 
	    			sheetCursor.pval.get().y - sheet.grabber.getY());
	    	else if (sheetCursor.pval != null) setPosition(sheetCursor.pval.get().x, sheetCursor.pval.get().y);
	    	moving();
		}};
		sheetCursor.pval.addEventChange(pval_run);
	    
	    
	    if (sheet != mmain()) {
	    	sheet_grab_run = new nRunnable() { public void run() {
		    	setPosition(sheetCursor.pval.get().x - sheet.grabber.getX(), 
		    			sheetCursor.pval.get().y - sheet.grabber.getY());
		    	moving();
		    } };
	    	sheet.grab_pos.addEventChange(sheet_grab_run);
	    }
	    return sheetCursor;
	  }
  public nCursor menuCursor(String r, boolean b) {
	    cursor_count++;
	    nCursor c = newCursor(r, b);
	    addEventsBuildMenu(new nRunnable(c) { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawer(10, 1)
	        .addLinkedModel("Auto_Button-S2-P3", "show").setLinkedValue(((nCursor)builder).show).getDrawer()
	        .addModel("Label_Small_Text-S1-P1", "Cursor: " + ((nCursor)builder).ref)
	          .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
	        .getShelf()
	        .addSeparator(0.125);
	    } });
	    return c;
	  }
  public nCursor menuSheetCursor(String r, boolean b) {
	    cursor_count++;
	    nCursor c = newSheetCursor(r, b);
	    addEventsBuildMenu(new nRunnable(c) { public void run() { 
	      if (custom_tab != null) custom_tab.getShelf()
	        .addDrawer(10, 1)
	        .addLinkedModel("Auto_Button-S2-P3", "show")
	        		.setLinkedValue(((nCursor)builder).show).getDrawer()
	        .addModel("Label_Small_Text-S1-P1", "Cursor: " + ((nCursor)builder).ref)
	          .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
	        .getShelf()
	        .addSeparator(0.125);
	    } });
	    return c;
	  }
  sInt menuIntSlide(int v, int _min, int _max, String r) {
    sInt f = newInt(v, r, r);
    f.set_limit(_min, _max);
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf().addDrawer(10, 1)
      .addModel("Label_Small_Text-S1-P1", ((sInt)builder).ref)
        .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
      .addWatcherModel("Auto_Watch_Label-S1-P3")
        .setLinkedValue(((sInt)builder))
        .setTextAlignment(PConstants.CENTER, PConstants.CENTER).getDrawer()
      .addWidget(new nSlide(custom_tab.gui, ref_size * 6, ref_size * 0.75F)
        .setValue( ( ((sInt)builder).get() - ((sInt)builder).getmin() ) / 
                   ( ((sInt)builder).getmax() - ((sInt)builder).getmin() ) )
        .addEventSlide(new nRunnable(((sInt)builder)) { public void run(float c) { 
          ((sInt)builder).set( (int)( ((sInt)builder).getmin() + 
                                    c * (((sInt)builder).getmax() - ((sInt)builder).getmin()) ) ); 
        } } )
        .setPosition(4*ref_size, ref_size * 2 / 16) ).getShelf()
      .addSeparator(0.125);
    } });
    return f;
  }
  protected sFlt menuFltSlide(float v, float _min, float _max, String r) {
    sFlt f = newFlt(v, r, r);
    f.set_limit(_min, _max);
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf().addDrawer(10, 1)
      .addModel("Label_Small_Text-S1-P1", ((sFlt)builder).ref)
        .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
      .addWatcherModel("Auto_Watch_Label-S1-P3")
        .setLinkedValue(((sFlt)builder))
        .setSX(ref_size*2.0)
        .setPX(ref_size*2.875)
        .setTextAlignment(PConstants.CENTER, PConstants.CENTER).getDrawer()
      .addWidget(new nSlide(custom_tab.gui, ref_size * 5, ref_size * 0.75F)
        .setValue( ( ((sFlt)builder).get() - ((sFlt)builder).getmin() ) / 
                   ( ((sFlt)builder).getmax() - ((sFlt)builder).getmin() ) )
        .addEventSlide(new nRunnable(((sFlt)builder)) { public void run(float c) { 
          ((sFlt)builder).set( ((sFlt)builder).getmin() + 
                               c * (((sFlt)builder).getmax() - ((sFlt)builder).getmin()) ); 
        } } )
        .setPosition(5*ref_size, ref_size * 2 / 16) ).getShelf()
      .addSeparator(0.125);
    } });
    return f;
  }
  public sCol menuColor(int v, String r) {
    sCol f = newCol(r, r, v);
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf()
        .addDrawer(10, 1)
        .addCtrlModel("Auto_Button-S2-P3", "choose").setRunnable(new nRunnable(builder) { public void run() { 
          new nColorPanel(custom_tab.gui, mmain().inter.taskpanel, ((sCol)builder));
        } } ).getDrawer()
        .addWatcherModel("Auto_Watch_Label-S6/1", "Color picker: " + ((sCol)builder).ref)
          .setLinkedValue(((sCol)builder))
          .setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
        .getShelf()
        .addSeparator(0.125);
    } });
    return f;
  }
  protected sInt menuIntWatch(int v, String r) {
    sInt f = newInt(v, r, r);
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf()
      .addDrawerWatch(((sInt)builder), 10, 1)
      .addSeparator(0.125);
    } });
    return f;
  }
  sFlt menuFltIncr(float v, float _f, String r) {
    sFlt f = newFlt(v, r, r);
    f.ctrl_factor = _f;
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf()
      .addDrawerIncrValue(((sFlt)builder), ((sFlt)builder).ctrl_factor, 10, 1)
      .addSeparator(0.125);
    } });
    return f;
  }
  public sFlt menuFltFact(float v, float _f, String r) {
    sFlt f = newFlt(v, r, r);
    f.ctrl_factor = _f;
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf()
      .addDrawerFactValue(((sFlt)builder), ((sFlt)builder).ctrl_factor, 10, 1)
      .addSeparator(0.125);
    } });
    return f;
  }
  public sInt menuIntIncr(int v, float _f, String r) {
    sInt f = newInt(v, r, r);
    f.ctrl_factor = _f;
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf()
      .addDrawerIncrValue(((sInt)builder), ((sInt)builder).ctrl_factor, 10, 1)
      .addSeparator(0.125);
    } });
    return f;
  }
  sInt menuIntFact(int v, float _f, String r) {
    sInt f = newInt(v, r, r);
    f.ctrl_factor = _f;
    addEventsBuildMenu(new nRunnable(f) { public void run() { 
      if (custom_tab != null) custom_tab.getShelf()
      .addDrawerFactValue(((sInt)builder), ((sInt)builder).ctrl_factor, 10, 1)
      .addSeparator(0.125);
    } });
    return f;
  }
  
  nFrontPanel sheet_front;  
  nExplorer preset_explorer;//sheet_viewer, 
  sStr new_preset_name;
  nWidget match_flag;
  
  
  public void build_custom_menu(nFrontPanel sheet_front) {}
  
  public void build_sheet_menu() {
    if (sheet_front == null) {
      sheet_front = new nFrontPanel(mmain().screen_gui, mmain().inter.taskpanel, val_title.get());
      
//      sheet_front.addTab("View").getShelf()
//        .addSeparator(0.125)
//        .addDrawer(10.25, 1).addModel("Label-S3", "sheet view :").setTextAlignment(PConstants.LEFT, PConstants.CENTER).getShelf()
//        .addSeparator()
//        ;
//      sheet_viewer = sheet_front.getTab(0).getShelf(0)
//        .addSeparator()
//        .addExplorer()
//          .setChildAccess(false)
//          .setStrtBloc(value_bloc)
//          .addValuesModifier(mmain().inter.taskpanel)
//          .addEventChange(new nRunnable() { public void run() { 
//              if (sheet_viewer.explored_bloc != value_bloc) {
//                sheet_viewer.setStrtBloc(value_bloc);
//              }
//          } } )
//          ;
      match_flag = sheet_front.addTab("Preset").getShelf()
        .addSeparator(0.125)
        .addDrawer(10.25, 1).addModel("Label-S3", "Presets :").setTextAlignment(PConstants.LEFT, PConstants.CENTER).getDrawer()
        .addCtrlModel("Button-S2-P3", "Delete").setRunnable(new nRunnable() { public void run() { 
          preset_explorer.selected_bloc.clear(); 
          for (nExplorer e : mmain().presets_explorers) e.update(); } } )
          .setInfo("delete selected preset").getDrawer()
        .addModel("Label_DownLight_Back-S2-P2", "")
          .setInfo("selected preset compatibility with this sheet");
        
      match_flag.getShelf()
        .addSeparator()
        ;
      preset_explorer = sheet_front.getTab(0).getShelf(0)
        .addSeparator()
        .addExplorer()
          .setStrtBloc(mmain().saved_preset)
          //.hideGoBack()
          .addValuesModifier(mmain().inter.taskpanel)
          .addEventChange(new nRunnable() { public void run() { 
            if (!(preset_explorer.explored_bloc == mmain().saved_preset ||
                preset_explorer.explored_bloc.parent == mmain().saved_preset)) 
              preset_explorer.setStrtBloc(mmain().saved_preset);
            if (preset_explorer.selected_bloc != null && 
                (mmain().inter.data.values_found(value_bloc, preset_explorer.selected_bloc) || 
                		mmain().inter.data.values_found(preset_explorer.selected_bloc, value_bloc)) ) 
              match_flag.setLook(gui.theme.getLook("Label_HightLight_Back")).setText("matching");
            else match_flag.setLook(gui.theme.getLook("Label_DownLight_Back")).setText("");
          } } )
          ;
      mmain().presets_explorers.add(preset_explorer);
      
      preset_explorer.getShelf()
        .addSeparator(0.25)
        .addDrawer(1)
          .addCtrlModel("Button-S2-P1", "Save").setRunnable(new nRunnable() { public void run() { 
            save_preset(); } } ).setInfo("Save sheet values as preset").getDrawer()
          .addLinkedModel("Field-S2-P2").setLinkedValue(new_preset_name).getDrawer()
          .addCtrlModel("Button-S2-P3", "Load").setRunnable(new nRunnable() { public void run() { 
            load_preset(); } } ).setInfo("load corresponding preset values into sheet values").getDrawer()
          .getShelf()
        .addSeparator(0.25)
        ;
      //sheet_front.setPosition(
      //  screen_gui.view.pos.x + screen_gui.view.size.x - sheet_front.grabber.getLocalSX() - ref_size * 3, 
      //  screen_gui.view.pos.y + ref_size * 2 );
      
      
      if (eventsBuildMenu.size() > 0) {
	      custom_tab = sheet_front.addTab("Control");
	      custom_tab.getShelf()
	        .addDrawer(10.25, 0.75)
	        .addModel("Label-S10/0.75", "-  Control  -").setFont((int)(ref_size/1.5)).getShelf()
	        .addSeparator(0.125)
	        ;
	      nRunnable.runEvents(eventsBuildMenu);
      }
      build_custom_menu(sheet_front);
      
      sheet_front.addEventClose(new nRunnable(this) { public void run() { 
        if (preset_explorer != null) mmain().presets_explorers.remove(preset_explorer);
        sheet_front = null; }});
    } else {
      sheet_front.popUp();
    }
  }
  
  void save_preset() {
    Save_Bloc b = new Save_Bloc("");
    value_bloc.preset_value_to_save_bloc(b);
    mmain().saved_preset.newBloc(b, new_preset_name.get());
    for (nExplorer e : mmain().presets_explorers) { 
      e.update();
      e.selectEntry(new_preset_name.get());
    }
  }
  void load_preset() {
    if (preset_explorer.selected_bloc != null) {
    	mmain().inter.data.transfer_bloc_values(preset_explorer.selected_bloc, value_bloc);
    }
  }
  
  
  boolean canSetupFrom(sValueBloc bloc) {
    return (super.canSetupFrom(bloc) && bloc.getBloc("settings").getValue("specialize") != null && 
            ((sStr)bloc.getBloc("settings").getValue("specialize")).get().equals(specialize.get()) ) ||
    		(super.canSetupFrom(bloc) && bloc.getBloc("settings").getValue("specialize") == null);
  }
  
  void setupFromBloc(sValueBloc bloc) {
    gui.app.mlogln(value_bloc.ref+" setupFromBloc "+bloc);
    if (!canSetupFrom(bloc) && unclearable) gui.app.mlogln("unclearable "+value_bloc.ref+" cant setup from");
    if (canSetupFrom(bloc)) {
      
      empty();
      
      //to store name change
      new_ref = "";
      //point to container of all blocs being added to filter name duplication
      blocs_to_add = bloc;
      
      mmain().inter.data.transfer_bloc_values(bloc, value_bloc);
      mmain().inter.data.transfer_bloc_values(bloc.getBloc("settings"), setting_bloc);
      grabber.setPosition(((sVec)bloc.getBloc("settings").getValue("position")).get());
      grabber.setPY(grabber.getLocalY() - grabber.getLocalY()%(ref_size * 0.5));
      grabber.setPX(grabber.getLocalX() - grabber.getLocalX()%(ref_size * 0.5));
      
      bloc.runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc bloc) { 
        if (!(bloc.ref.equals("settings"))) {
          //search if existing bloc correspond >> unclearable >> setupFromBloc
          boolean found = false;
          if (value_bloc.getBloc(bloc.ref) != null) {
            for (Macro_Abstract m : child_macro) if (m.value_bloc.ref.equals(bloc.ref)) { 
              found = true; 
              m.setupFromBloc(bloc); 
            }
          }
          if (!found) { //sinon
            addCopyofBloc(bloc, false);
          }
        }
      }});
      
      gui.app.mlogln("setup "+value_bloc.ref+" \n    nref "+new_ref + " \n searching for link/spot");
      
      // take apart saving string to apply name change then create corresponding link or spot
      if (bloc.getBloc("settings").getValue("links") != null) {
        gui.app.mlogln("setup sheet link found");
        String link_s = ((sStr)bloc.getBloc("settings").getValue("links")).get();
        rename_build_link(link_s, new_ref);
      }
      //redo_spot();
      if (bloc.getBloc("settings").getValue("spots") != null) {
        gui.app.mlogln("setup sheet spot found");
        String spot_s = ((sStr)bloc.getBloc("settings").getValue("spots")).get();
        rename_build_spot(spot_s, new_ref);
      }
      redo_spot();
      redo_link();
      
      nRunnable.runEvents(eventsSetupLoad);
      
      //mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
        if (openning.get() == REDUC) { openning.set(OPEN); reduc(); }
        else if (openning.get() == OPEN) { openning.set(REDUC); open(); }
        else if (openning.get() == HIDE) { openning.set(openning_pre_hide.get()); hide(); }
        else if (openning.get() == DEPLOY) { openning.set(OPEN); deploy(); }
        if (!mmain().show_macro.get()) hide();
      //} } );
      
      mmain().szone_clear_select();
    }
    gui.app.mlogln(value_bloc.ref+" setupFromBloc end");
  }
  
  void rename_build_link(String link_s, String n_ref) {
    gui.app.mlogln(value_bloc.ref+" rename_build_link");
    //  change bloc name in bloc links
    //String new_links = "";
    String[] change_list = PApplet.splitTokens(n_ref, OBJ_TOKEN);
    String[] nlink_list = PApplet.splitTokens(link_s, OBJ_TOKEN);
    //String newlink = "";
    for (String l : nlink_list) {
      //logln("link "+l);
      String[] linkpart = PApplet.splitTokens(l, INFO_TOKEN);
      String newco = "";
      for (String k : linkpart) {
        String[] copart = PApplet.splitTokens(k, BLOC_TOKEN);
        for (int i = 0 ; i < change_list.length ; i += 1) {
          String[] changepart = PApplet.splitTokens(change_list[i], INFO_TOKEN);
          //logln("test change "+changepart[0] + " > "+changepart[1]);
          if (copart.length > 0 && copart[0].equals(changepart[0])) {
            copart[0] = changepart[1];
          }
        }
        if (copart.length > 1) {
          newco = newco + BLOC_TOKEN + copart[0] + BLOC_TOKEN + copart[1] + INFO_TOKEN;
        }
      }
      String[] newcopart = PApplet.splitTokens(newco, INFO_TOKEN);
      if (newcopart.length > 1) {
        //logln("newco "+newcopart[0]+" "+newcopart[1]);
        add_link(newcopart[1], newcopart[0]);
        //redo_link();
      }
    }
    gui.app.mlogln(value_bloc.ref+" rename_build_link end");
  }
  void rename_build_spot(String spots_s, String n_ref) {
    gui.app.mlogln(value_bloc.ref+" rename_build_spot");
    String[] change_list = PApplet.splitTokens(n_ref, OBJ_TOKEN);
    //String new_str = "";
    String[] spots_side_list = PApplet.splitTokens(spots_s, GROUP_TOKEN);
    int side = 0;
    for (String spot_side : spots_side_list) {
      gui.app.mlogln("doing side "+side);
      int elc = 0;
      String[] elem_list = PApplet.splitTokens(spot_side, OBJ_TOKEN);
      for (String elm : elem_list) {
        gui.app.mlogln(elc+" got spot elem "+elm);
        elc++;
        String[] elem_descr_l = PApplet.splitTokens(elm, BLOC_TOKEN);
        for (String change : change_list) {
          String[] changepart = PApplet.splitTokens(change, INFO_TOKEN);
          if (elem_descr_l.length == 2 && elem_descr_l[0].equals(changepart[0])) {
            elem_descr_l[0] = changepart[1];
          }
        }
        if (elem_descr_l[0].equals("empty"))
        		spot_empty_nb = Math.max(PApplet.parseInt(elem_descr_l[1]), spot_empty_nb);
        String new_elem = BLOC_TOKEN+elem_descr_l[0]+BLOC_TOKEN+elem_descr_l[1];
        gui.app.mlogln("renamed "+new_elem);
        Macro_Element e = null;
        for (Macro_Element t : child_elements) if (t.descr.equals(new_elem)) { e = t; break; }
        if (e != null) {
	        if (side == 0) add_spot("left", e);
	        if (side == 1) add_spot("right", e);
        }
      }
      
      side++;
    }
    gui.app.mlogln(value_bloc.ref+" rename_build_spot end");
  }
  boolean is_addgroup_top_bloc = false;
  void addCopyofBlocContent(sValueBloc bloc, boolean addgroup_top_bloc) {
    gui.app.mlogln(value_bloc.ref+" addCopyofBlocContent ");
    
    is_addgroup_top_bloc = addgroup_top_bloc;
    //to store name change
    new_ref = "";
    //point to container of all blocs being added to filter name duplication
    blocs_to_add = bloc;
    
    bloc.runBlocIterator(new nIterator<sValueBloc>() { public void run(sValueBloc b) { 
      addCopyofBloc(b, is_addgroup_top_bloc);
    }});
    
    gui.app.mlogln(value_bloc.ref+" \n    nref "+new_ref + " \n searching for link/spot");
    
    // take apart saving string to apply name change then create corresponding link or spot
    
    // template group link saving
    if (bloc.getValue("links") != null) {
      gui.app.mlogln(value_bloc.ref+"found link value - rename/build");
      String link_s = ((sStr)bloc.getValue("links")).get();
      rename_build_link(link_s, new_ref);
    }
    if (bloc.getValue("spots") != null) {
      gui.app.mlogln(value_bloc.ref+"found spot value - rename/build");
      String spot_s = ((sStr)bloc.getValue("spots")).get();
      rename_build_spot(spot_s, new_ref);
    }
    //// sheet links
    //if (bloc.getBloc("settings") != null && bloc.getBloc("settings").getValue("links") != null) {
    //  logln("sheet link found");
    //  String link_s = ((sStr)bloc.getBloc("settings").getValue("links")).get();
    //  rename_build_link(link_s, new_ref);
    //}
    //// sheet spot
    ////redo_spot();
    //if (bloc.getBloc("settings") != null && bloc.getBloc("settings").getValue("spots") != null) {
    //  logln("sheet spot found");
    //  String spot_s = ((sStr)bloc.getBloc("settings").getValue("spots")).get();
    //  rename_build_spot(spot_s, new_ref);
    //}
    //logln(value_bloc.ref+"redo link/spot");
    redo_spot();
    redo_link();
    gui.app.mlogln(value_bloc.ref+" addCopyofBlocContent end");
  }
  
  String new_ref = "";
  sValueBloc blocs_to_add;
  void addCopyofBloc(sValueBloc bloc, boolean from_top_bloc) {
    if (!bloc.ref.equals("settings")) {
      
      //add bloc
      String n_ref = RConst.copy(bloc.ref);
      if (bloc.getBloc("settings") != null && bloc.getBloc("settings").getValue("title") != null)
        n_ref = ((sStr)(bloc.getBloc("settings").getValue("title"))).get();
//      String b_rf = RConst.copy(n_ref);
      String suff_rf = RConst.copy(n_ref);
      while (suff_rf.length() > 0 && suff_rf.charAt(0) != '_') 
        suff_rf = suff_rf.substring(1, suff_rf.length());
      int cn = -1;
      boolean is_in_other_sheet = true;
      gui.app.mlogln("add bloc " + bloc.ref + " try his name " + n_ref + " found suffix " + suff_rf);
      while (value_bloc.getBloc(n_ref) != null || 
             (blocs_to_add.getBloc(n_ref) != null && blocs_to_add.getBloc(n_ref) != bloc) || 
             (sheet != this && sheet.value_bloc.getBloc(n_ref) != null) || 
             is_in_other_sheet) {
        if (cn >= 0) {
          n_ref = cn + suff_rf;
        }
        cn++;
        
        gui.app.mlogln(" try new name " + n_ref);
        is_in_other_sheet = false;
        
        for (Map.Entry<String, sValueBloc> me : blocs_to_add.blocs.entrySet()) { 
          sValueBloc svb = (sValueBloc)me.getValue();
          if (!svb.ref.equals(bloc.ref)) 
            is_in_other_sheet = svb.getBloc(n_ref) != null || is_in_other_sheet;
        }
        
        if (sheet != this) for (Macro_Sheet m : sheet.child_sheet) if (m != this)
          is_in_other_sheet = m.value_bloc.getBloc(n_ref) != null || is_in_other_sheet;
        for (Macro_Sheet m : child_sheet) if (m != this)
          is_in_other_sheet = m.value_bloc.getBloc(n_ref) != null || is_in_other_sheet;
      }
      gui.app.mlogln(n_ref + " found his name");
      
      gui.app.mlogln(value_bloc.ref+" addCopyofBloc " + bloc.ref+"    as "+n_ref);
      
      sValueBloc nbloc = mmain().inter.data.copy_bloc(bloc, value_bloc, n_ref);
      
      sValueBloc nbloc_child = mmain().inter.getTempBloc();
      //get nbloc child
      for (Map.Entry<String, sValueBloc> me : nbloc.blocs.entrySet()) {
        sValueBloc vb = ((sValueBloc)me.getValue());
        if (!vb.base_ref.equals("settings")) mmain().inter.data.copy_bloc(vb, nbloc_child);
      }
      
      //empty nbloc
      sValueBloc sett_temp = mmain().inter.getTempBloc();
      sValueBloc sbloc = mmain().inter.data.copy_bloc(bloc.getBloc("settings"), sett_temp, "settings");
      for (Map.Entry<String, sValueBloc> b : nbloc.blocs.entrySet()) { 
        sValueBloc s = (sValueBloc)b.getValue(); s.clean();
      } 
      nbloc.blocs.clear();
      mmain().inter.data.copy_bloc(sbloc, nbloc, "settings");
      
      //copy link and spot in nbloc_child 
      if (sbloc != null && sbloc.getValue("links") != null) {
      //if (bloc.getBloc("settings") != null && bloc.getBloc("settings").getValue("links") != null) {
//        sValue v = 
        		mmain().inter.data.copy_value(sbloc.getValue("links"), nbloc_child);
        //logln("copy link val has "+v.ref);
      }
      if (sbloc != null && sbloc.getValue("spots") != null) {
      //if (bloc.getBloc("settings") != null && bloc.getBloc("settings").getValue("spots") != null) {
//        sValue v = 
        		mmain().inter.data.copy_value(sbloc.getValue("spots"), nbloc_child);
        //logln("copy spot val has "+v.ref);
      }
      sett_temp.clear();
      
      //if (nbloc.getBloc("settings") != null) logln(value_bloc.ref+" new bloc " + nbloc.ref + " has setting"); 
      //else logln(value_bloc.ref+" new bloc " + nbloc.ref + " DONT has setting"); 
      
      //add macro
      Macro_Abstract a = addByValueBloc(nbloc);
      if (a != null) { 
        new_ref = new_ref + OBJ_TOKEN + bloc.ref+INFO_TOKEN+a.value_bloc.ref;
        if (sheet != this) sheet.new_ref = sheet.new_ref + OBJ_TOKEN + bloc.ref+INFO_TOKEN+a.value_bloc.ref;
      }
      
      //add copied child to new macro
      if (a != null && a.val_type.get().equals("sheet")) {
        ((Macro_Sheet)a).addCopyofBlocContent(nbloc_child, false);
        for (Macro_Abstract m : ((Macro_Sheet)a).child_macro) m.szone_unselect();
        ((Macro_Sheet)a).init_end();
      }
      
      //no new macro = invalid bloc
      if (a == null) nbloc.clear();
       
      nbloc_child.clear();
      
      if (from_top_bloc && a != null && !mmain().is_setup_loading)  { a.szone_select(); }
      gui.app.mlogln(value_bloc.ref+" addCopyofBloc end");
    } 
  }
  //b need to be child of value_bloc and have setting/type + spe if sheet , everything else can be created
  Macro_Abstract addByValueBloc(sValueBloc b) { 
    if (b != null && b.parent == value_bloc && b.getBloc("settings") != null && 
        b.getBloc("settings").getValue("type") != null) {
      
      String typ = ((sStr)b.getBloc("settings").getValue("type")).get();
      
      if (!typ.equals("sheet"))   
    	  	return addByType(typ, b);
      
      else if (b.getBloc("settings").getValue("specialize") != null) {
        
        String spe = ((sStr)b.getBloc("settings").getValue("specialize")).get();
        
        if (spe.equals("sheet"))   
    	  		return addByType("sheet", b);
        
        for (Sheet_Specialize t : Sheet_Specialize.prints) if (!t.unique && t.name.equals(spe))
          return t.add_new(this, b, null);
      }
    }
    return null; 
  }
  
  Macro_Abstract addByType(String t) { 
	  cancel_new_spot();
	  if (child_macro.size() < val_max_bloc.get()) return addByType(t, null); 
	  return null;
  }
  Macro_Abstract addByType(String t, sValueBloc b) { 
	  Macro_Abstract nm = null;
    for (MAbstract_Builder m : mmain().bloc_builders)
      if (t.equals(m.type)) { 
    	  	nm = m.build(this, b);
    	    if (nm != null) nm.init_end();
    	    return nm;
      }
    return null;
//    if (t.equals("numCtrl")) nm = addNumCtrl(b);
//    else if (t.equals("vecCtrl")) nm = addVecCtrl(b);
//    else if (t.equals("preset")) nm = addPrst(b);
//    else if (t.equals("midi")) nm = addMidi(b);
//    else if (t.equals("toolNC")) nm = addToolNCtrl(b);
//    else if (t.equals("crossVec")) nm = addCrossVec(b);
//    if (nm != null) nm.init_end();
//    return nm;
  }
  
//  MData addData(sValueBloc b) { MData m = null;
////    if (sheet_viewer != null && sheet_viewer.selected_value != null) {
////      m = new MData(this, b, sheet_viewer.selected_value);
////      sheet_viewer.update(); }
////    else 
//    	if (mmain().sheet_explorer != null && mmain().sheet_explorer.explored_bloc == value_bloc &&
//             mmain().sheet_explorer.selected_value != null) {
//      m = new MData(this, b, mmain().sheet_explorer.selected_value);
//      mmain().sheet_explorer.update(); }
//    else m = new MData(this, b, null); return m; }
//  MSheetIn addSheetIn(sValueBloc b) { MSheetIn m = new MSheetIn(this, b); return m; }
//  MSheetOut addSheetOut(sValueBloc b) { MSheetOut m = new MSheetOut(this, b); return m; }
//  MKeyboard addKey(sValueBloc b) { MKeyboard m = new MKeyboard(this, b); return m; }
//  MSwitch addSwitch(sValueBloc b) { MSwitch m = new MSwitch(this, b); return m; }
//  MTrig addTrig(sValueBloc b) { MTrig m = new MTrig(this, b); return m; }
//  MBigSwitch addBigSwitch(sValueBloc b) { MBigSwitch m = new MBigSwitch(this, b); return m; }
//  MBigTrig addBigTrig(sValueBloc b) { MBigTrig m = new MBigTrig(this, b); return m; }
//  MGate addGate(sValueBloc b) { MGate m = new MGate(this, b); return m; }
//  MNot addNot(sValueBloc b) { MNot m = new MNot(this, b); return m; }
//  MBin addBin(sValueBloc b) { MBin m = new MBin(this, b); return m; }
//  MBool addBool(sValueBloc b) { MBool m = new MBool(this, b); return m; }
//  MVar addVar(sValueBloc b) { MVar m = new MVar(this, b); return m; }
//  MPulse addPulse(sValueBloc b) { MPulse m = new MPulse(this, b); return m; }
//  MCalc addCalc(sValueBloc b) { MCalc m = new MCalc(this, b); return m; }
//  MComp addComp(sValueBloc b) { MComp m = new MComp(this, b); return m; }
//  MChan addChan(sValueBloc b) { MChan m = new MChan(this, b); return m; }
//  MVecXY addVecXY(sValueBloc b) { MVecXY m = new MVecXY(this, b); return m; }
//  MVecMD addVecMD(sValueBloc b) { MVecMD m = new MVecMD(this, b); return m; }
//  MFrame addFrame(sValueBloc b) { MFrame m = new MFrame(this, b); return m; }
//  MNumCtrl addNumCtrl(sValueBloc b) { MNumCtrl m = null;
//    if (sheet_viewer != null && sheet_viewer.selected_value != null) {
//      m = new MNumCtrl(this, b, sheet_viewer.selected_value);
//      sheet_viewer.update(); }
//    else 
//    	if (mmain().sheet_explorer != null && mmain().sheet_explorer.explored_bloc == value_bloc &&
//             mmain().sheet_explorer.selected_value != null) {
//      m = new MNumCtrl(this, b, mmain().sheet_explorer.selected_value);
//      mmain().sheet_explorer.update(); }
//    else m = new MNumCtrl(this, b, null); return m; }
//  MVecCtrl addVecCtrl(sValueBloc b) { MVecCtrl m = null;
//    if (sheet_viewer != null && sheet_viewer.selected_value != null) {
//      m = new MVecCtrl(this, b, sheet_viewer.selected_value);
//      sheet_viewer.update(); }
//    else 
//    	if (mmain().sheet_explorer != null && mmain().sheet_explorer.explored_bloc == value_bloc &&
//             mmain().sheet_explorer.selected_value != null) {
//      m = new MVecCtrl(this, b, mmain().sheet_explorer.selected_value);
//      mmain().sheet_explorer.update(); }
//    else m = new MVecCtrl(this, b, null); return m; }
//  MRandom addRng(sValueBloc b) { MRandom m = new MRandom(this, b); return m; }
//  MMouse addMouse(sValueBloc b) { MMouse m = new MMouse(this, b); return m; }
//  MComment addComment(sValueBloc b) { MComment m = new MComment(this, b); return m; }
//  MPreset addPrst(sValueBloc b) { MPreset m = new MPreset(this, b); return m; }
//  MMIDI addMidi(sValueBloc b) { MMIDI m = new MMIDI(this, b); return m; }
//  MMenu addMenu(sValueBloc b) { MMenu m = new MMenu(this, b); return m; }
//  MTool addTool(sValueBloc b) { MTool m = new MTool(this, b); return m; }
//  MToolBin addToolBin(sValueBloc b) { MToolBin m = new MToolBin(this, b); return m; }
//  MToolTri addToolTri(sValueBloc b) { MToolTri m = new MToolTri(this, b); return m; }
//  MToolNCtrl addToolNCtrl(sValueBloc b) { MToolNCtrl m = null;
//    if (sheet_viewer != null && sheet_viewer.selected_value != null) { 
//      m = new MToolNCtrl(this, b, sheet_viewer.selected_value);
//      sheet_viewer.update(); }
//    else 
//    	if (mmain().sheet_explorer != null && mmain().sheet_explorer.explored_bloc == value_bloc &&
//             mmain().sheet_explorer.selected_value != null) { 
//      m = new MToolNCtrl(this, b, mmain().sheet_explorer.selected_value);
//      mmain().sheet_explorer.update(); }
//    else m = new MToolNCtrl(this, b, null); return m; }
//  MPanel addPanel(sValueBloc b) { MPanel m = new MPanel(this, b); return m; }
//  MPanBin addPanBin(sValueBloc b) { MPanBin m = new MPanBin(this, b); return m; }
//  MPanSld addPanSld(sValueBloc b) { MPanSld m = new MPanSld(this, b); return m; }
//  MPanGrph addPanGrph(sValueBloc b) { MPanGrph m = new MPanGrph(this, b); return m; }
  //MPanCstm addPanCstm(sValueBloc b) { MPanCstm m = new MPanCstm(this, b); return m; }
//  MRamp addRamp(sValueBloc b) { MRamp m = new MRamp(this, b); return m; }
//  MCrossVec addCrossVec(sValueBloc b) { MCrossVec m = new MCrossVec(this, b); return m; }
//  MColRGB addColRGB(sValueBloc b) { MColRGB m = new MColRGB(this, b); return m; }
  
}






















      
