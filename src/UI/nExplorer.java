package UI;

import java.util.ArrayList;
import java.util.Map;

import processing.core.PApplet;
import sData.nRunnable;
import sData.sBoo;
import sData.sFlt;
import sData.sInt;
import sData.sStr;
import sData.sValue;
import sData.sValueBloc;

public class nExplorer extends nDrawer {
	  boolean access_child = true;
	  public nExplorer setChildAccess(boolean b) { access_child = b; return this; }
	  ArrayList<String> explorer_entry;
	  ArrayList<sValueBloc> explorer_blocs;
	  ArrayList<sValue> explorer_values;
	  public sValueBloc explored_bloc;
	public sValueBloc selected_bloc;
	public sValueBloc starting_bloc;
	  public sValue selected_value;
	  int selected_bloc_index = 0, selected_value_index = 0;
	  public nList explorer_list;
	  
	  public nExplorer setStrtBloc(sValueBloc sb) { if (sb != explored_bloc) { starting_bloc = sb; explored_bloc = sb; update(); } return this; }
	  public nExplorer setBloc(sValueBloc sb) { if (sb != explored_bloc) { explored_bloc = sb; update(); } return this; }
	  
	  public nExplorer hideValueView() { 
	    myshelf.removeDrawer(info_drawer);
	    info_drawer.drawer_height = 0;
	    myshelf.insertDrawer(info_drawer);
	    //info_drawer.clear(); info_drawer = null;
	    val_info.clear(); val_info = null;
	    shelf.removeDrawer(this);
	    drawer_height = ref_size*7.1F;
	    shelf.insertDrawer(this);
	    return this; 
	  }
	  public nExplorer hideGoBack() { 
	    hidegoback = true;
	    gobackindex = -1;
	    gobackspace = 0;
	    update();
	    return this; 
	  }
	  nExplorer showGoBack() { 
	    hidegoback = false;
	    gobackindex = 0;
	    gobackspace = 1;
	    update();
	    return this; 
	  }
	  boolean hidegoback = false;
	  int gobackindex = 0;
	  int gobackspace = 1; 
	  
	  nShelf myshelf;
	  nWidget bloc_info, val_info;
	  nDrawer info_drawer;
	  
	  public nDrawer setLayer(int l) { super.setLayer(l); myshelf.setLayer(l); return this; }
	  public nDrawer toLayerTop() { 
	    super.toLayerTop(); myshelf.toLayerTop(); 
	    for (nCtrlWidget w : values_button) w.toLayerTop(); 
	    return this; }
	  
	  ArrayList<nRunnable> eventChangeRun = new ArrayList<nRunnable>();
	  public nExplorer addEventChange(nRunnable r)       { eventChangeRun.add(r); return this; }
	  
	  public nExplorer addEventChange_Builder(nRunnable r) { eventChangeRun.add(r); r.builder = this; return this; }
	  
	  public nExplorer addValuesModifier(nTaskPanel _t) {
	    task = _t;
	    hasvalbutton = true;
	    for (int i = 0 ; i < entry_nb ; i++) {
	      nCtrlWidget w = explorer_list.addCtrlModel("Button-SSS1", ""+i);
	      w.setRunnable(new nRunnable(w) { public void run() {
	        int ind = (int)Float.parseFloat(((nCtrlWidget)builder).getText()) + explorer_list.entry_pos;
	        if (ind != gobackindex && ind - explorer_blocs.size() < explorer_values.size()+gobackspace) {
	          sValue clicked_val = explorer_values.get(ind-gobackspace - explorer_blocs.size());
	          if (clicked_val.type.equals("str")) { 
	            new nTextPanel(gui, task, (sStr)clicked_val);
	          } else if (clicked_val.type.equals("flt")) { 
	            new nNumPanel(gui, task, (sFlt)clicked_val);
	          } else if (clicked_val.type.equals("int")) { 
	            new nNumPanel(gui, task, (sInt)clicked_val);
	          } else if (clicked_val.type.equals("boo")) { 
	            new nBoolPanel(gui, task, (sBoo)clicked_val);
	          } else if (clicked_val.type.equals("col")) { 
//	            new nColorPanel(gui, task, (sCol)clicked_val);
	          }
	        } 
	      }});
	      w.addEventVisibilityChange(new nRunnable(w) { public void run() {
	    	  	if (!myshelf.ref.isHided()) {
		        int ind = (int)Float.parseFloat(((nCtrlWidget)builder).getText()) + explorer_list.entry_pos;
		        if (ind != gobackindex && ind > explorer_blocs.size() && 
		            ind - explorer_blocs.size() < explorer_values.size()+gobackspace) {
		          sValue val = explorer_values.get(ind-gobackspace - explorer_blocs.size());
		          if (val.type.equals("str") || val.type.equals("col") || 
		              val.type.equals("int") || val.type.equals("flt") || val.type.equals("boo")) 
		                ((nCtrlWidget)builder).show();
		          else { ((nCtrlWidget)builder).hide(); }
		        } else { ((nCtrlWidget)builder).hide(); }
	    	  	} else { ((nCtrlWidget)builder).hide(); }
	      }});
	      w.setPosition(ref_size * 8.125F, ref_size * (i + 0.25F))
	        .setTextVisibility(false)
	        //.hide()
	        ;
	      values_button.add(w);
	    }
	    explorer_list.addEventScroll(new nRunnable() { public void run() {
	      update_val_bp();
	    }});
	    update_val_bp();
	    return this; 
	  }
	  
	  public void update_val_bp() {
	    if (hasvalbutton) {
	      for (int i = 0 ; i < entry_nb ; i++) {
	        int ind = i + explorer_list.entry_pos;
	        if (ind != gobackindex && ind > explorer_blocs.size() && 
	            ind - explorer_blocs.size() < explorer_values.size()+gobackspace) {
	          sValue val = explorer_values.get(ind-gobackspace - explorer_blocs.size());
	          if (val.type.equals("str") || val.type.equals("col") || 
	              val.type.equals("int") || val.type.equals("flt") || val.type.equals("boo")) values_button.get(i).show();
	          else values_button.get(i).hide();
	        } else values_button.get(i).hide();
	      }
	    }
	  }
	  
	  nTaskPanel task;
	  ArrayList<nCtrlWidget> values_button;
	  boolean hasvalbutton = false;
	  int entry_nb = 5;
	  
	  public nExplorer(nShelf s) {
	    super(s, s.ref_size*10, s.ref_size*9);
	    explorer_entry = new ArrayList<String>();
	    explorer_blocs = new ArrayList<sValueBloc>();
	    explorer_values = new ArrayList<sValue>();
	    values_button = new ArrayList<nCtrlWidget>();
	    myshelf = new nShelf(shelf.shelfPanel, shelf.space_factor);
	    myshelf.addSeparator(0.25F);
	    myshelf.ref.setParent(ref);
	    explorer_list = myshelf.addList(entry_nb, 10, 1).setTextAlign(PApplet.LEFT)
	      .addEventChange_Builder(new nRunnable() { 
	      public void run() {
	        int ind = ((nList)builder).last_choice_index;
	        if (ind == gobackindex && explored_bloc != null && explored_bloc != starting_bloc) {
	          explored_bloc = explored_bloc.parent;
	          selected_bloc = null;
	          selected_value = null;
	          update_list();
	          runEvents(eventChangeRun);
	          
	        } else if (ind != gobackindex && ind < explorer_blocs.size()+gobackspace) {
	          if (selected_bloc == explorer_blocs.get(ind-gobackspace) && access_child) {
	            explored_bloc = selected_bloc;
	            selected_bloc = null;
	            selected_value = null;
	            update_list();
	            runEvents(eventChangeRun);
	          } else {
	            selected_bloc = explorer_blocs.get(ind-gobackspace);
	            selected_value = null;
	            update_info();
	            runEvents(eventChangeRun);
	          }
	        } else if (ind != gobackindex && ind - explorer_blocs.size() < explorer_values.size()+gobackspace) {
	          selected_bloc = null;
	          selected_value = explorer_values.get(ind-gobackspace - explorer_blocs.size());
	          
	          update_info();
	          runEvents(eventChangeRun);
	        } 
	      } } )
	      ;
	      
	    bloc_info = myshelf.addSeparator(0.25F)
	      .addDrawer(1.4F)
	        .addModel("Label-S4", "Selected Bloc :").setTextAlignment(PApplet.LEFT, PApplet.TOP);
	    
	    info_drawer = myshelf.addDrawer(1.9F);
	      
	    val_info = info_drawer
	        .addModel("Label-S4", "Selected Value :").setTextAlignment(PApplet.LEFT, PApplet.TOP)
	        	.setPY(ref_size * 0.4F);
	    
	    update_list();
	    
	  }
	  public void selectEntry(String r) {
	    int i = 0;
	    for (Map.Entry<String, sValueBloc> me : explored_bloc.blocs.entrySet()) {
	      if (me.getKey().equals(r)) break;
	      i++; }
	    if (i < explorer_list.listwidgets.size()) explorer_list.listwidgets.get(i).setOn();
	  }
	  void update_info() {
	    update_val_bp();
	    if (selected_bloc != null) 
	      bloc_info.setText("Selected Bloc :\n " + selected_bloc.type + " " + selected_bloc.ref);
	    else bloc_info.setText("Selected Bloc : ");
	    if (selected_value != null && val_info != null) 
	      val_info.setText("Selected Value : " + selected_value.type + " " + selected_value.ref
	                      +"\n = " + selected_value.getString() );
	    else if (val_info != null) val_info.setText("Selected Value : " );
	  }
	  
	  public void update() {
	    selected_bloc = null;
	    selected_value = null;
	    update_list();
	  }
	  
	  final char[] alphabMin = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 
	                            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	  final char[] alphabMag = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
	                            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	  final char[] alphNum = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	  
	  ArrayList<sValue> val_tmp = new ArrayList<sValue>();
	  ArrayList<sValueBloc> blc_tmp = new ArrayList<sValueBloc>();
	  void update_list() {
	    explorer_entry.clear();
	    explorer_blocs.clear();
	    explorer_values.clear();
	    if (explored_bloc != null) {
	      //println(); println(explored_bloc.getHierarchy(false));
	      if (!hidegoback) {
	        if (explored_bloc != starting_bloc) explorer_entry.add("..");
	        else explorer_entry.add("");
	      }
	               
	      blc_tmp.clear();
	      for (int i = 0; i < 10 ; i++) {
	        for (Map.Entry<String, sValueBloc> me : explored_bloc.blocs.entrySet()) {
	          sValueBloc tv = (sValueBloc)me.getValue();
	          if (tv.base_ref.charAt(0) == alphNum[i]) blc_tmp.add(tv); 
	        }
	      }
	      for (int i = 0; i < 26 ; i++) {
	        for (Map.Entry<String, sValueBloc> me : explored_bloc.blocs.entrySet()) {
	          sValueBloc tv = (sValueBloc)me.getValue();
	          if (tv.base_ref.charAt(0) == alphabMag[i]) blc_tmp.add(tv); 
	        }
	      }
	      for (int i = 0; i < 26 ; i++) {
	        for (Map.Entry<String, sValueBloc> me : explored_bloc.blocs.entrySet()) {
	          sValueBloc tv = (sValueBloc)me.getValue();
	          if (tv.base_ref.charAt(0) == alphabMin[i]) blc_tmp.add(tv); 
	        }
	      }
	      for (Map.Entry<String, sValueBloc> me : explored_bloc.blocs.entrySet()) {
	        sValueBloc tv = (sValueBloc)me.getValue();
	        if (!blc_tmp.contains(tv)) blc_tmp.add(tv); 
	      }
	      for (sValueBloc cvb : blc_tmp) {
	        explorer_blocs.add(cvb); 
	        explorer_entry.add(cvb.ref + " " + cvb.use);
	      }
	      blc_tmp.clear();
	               
	      val_tmp.clear();
	      for (int i = 0; i < 10 ; i++) {
	        for (Map.Entry<String, sValue> me : explored_bloc.values.entrySet()) {
	          sValue tv = (sValue)me.getValue();
	          if (tv.ref.charAt(0) == alphNum[i]) val_tmp.add(tv); 
	        }
	      }
	      for (int i = 0; i < 26 ; i++) {
	        for (Map.Entry<String, sValue> me : explored_bloc.values.entrySet()) {
	          sValue tv = (sValue)me.getValue();
	          if (tv.ref.charAt(0) == alphabMag[i]) val_tmp.add(tv); 
	        }
	      }
	      for (int i = 0; i < 26 ; i++) {
	        for (Map.Entry<String, sValue> me : explored_bloc.values.entrySet()) {
	          sValue tv = (sValue)me.getValue();
	          if (tv.ref.charAt(0) == alphabMin[i]) val_tmp.add(tv); 
	        }
	      }
	      for (Map.Entry<String, sValue> me : explored_bloc.values.entrySet()) {
	        sValue tv = (sValue)me.getValue();
	        if (!val_tmp.contains(tv)) val_tmp.add(tv); 
	      }
	      for (sValue v : val_tmp) {
	        explorer_values.add(v); 
	        explorer_entry.add("   - "+v.ref);
	      }
	      val_tmp.clear();
	    }
	    explorer_list.setEntrys(explorer_entry);
	    update_info();
	  }
	}














