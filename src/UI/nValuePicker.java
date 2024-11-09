package UI;

import java.util.ArrayList;
import java.util.Map;

import RApplet.RConst;
import processing.core.PApplet;
import sData.nRunnable;
import sData.sStr;
import sData.sValue;
import sData.sValueBloc;

public class nValuePicker extends nWindowPanel {
	  
	  ArrayList<String> explorer_entry;
	  
	  nList explorer_list;
	  nWidget info;
	  
	  sStr val_cible;
	  sValueBloc search_bloc;
	  boolean autoclose = false, mitig_ac = false;
	  
	  ArrayList<nRunnable> eventsChoose = new ArrayList<nRunnable>();
	  public nValuePicker addEventChoose(nRunnable r) { eventsChoose.add(r);  return this; } 
	  
	  public nValuePicker(nGUI _g, nTaskPanel _task, sStr _sv, sValueBloc _sb, boolean _autoclose) { 
	    super(_g, _task, "select value"); 
	    val_cible = _sv;
	    search_bloc = _sb;
	    autoclose = _autoclose;
	    explorer_entry = new ArrayList<String>();
	    
	    explorer_list = getShelf().addList(5, 10, 1).setTextAlign(PApplet.LEFT);
	    
	    if (!_autoclose) {
	      info = getShelf().addSeparator(0.25F)
	        .addDrawer(1.4F)
	          .addLinkedModel("Label-S4", "Selected Value :")
	          .setLinkedValue(val_cible).setTextAlignment(PApplet.LEFT, PApplet.TOP);
	    
	      getShelf()
	        .addSeparator(0.25F)
	        .addDrawer(10.25F, 1)
	          .addCtrlModel("Button-S2-P2", "OK")
	          .setRunnable(new nRunnable() { public void run() { 
	            nRunnable.runEvents(eventsChoose); clear(); } }).getShelf();
	    }
	    update_list();
	    
	    explorer_list.addEventChange_Builder(new nRunnable() { 
	      public void run() {
	        String choice = explorer_list.last_choice_text;
	        if (!choice.equals("")) { 
	          val_cible.set(choice); 
	          if (autoclose && !mitig_ac) { nRunnable.runEvents(eventsChoose); clear(); }
	        }
	      } } );
	  } 
	  void selectEntry(String r) {
	    int i = 0;
	    for (String me : explorer_entry) {
	      if (me.equals(r)) break;
	      i++; }
	    if (i < explorer_list.listwidgets.size()) explorer_list.listwidgets.get(i).setOn();
	  }
	  
	  void update() {
	    update_list();
	  }
	  void update_list() {
		  ArrayList<String> names = new ArrayList<String>();
		  for (Map.Entry<String,sValue> b : search_bloc.values.entrySet()) { 
			  sValue sv = (sValue)b.getValue(); 
			  String n = new String();
			  n += sv.ref;
			  names.add(n);
		  }

	      for (String a : RConst.alphabet) 
	    	  	for (String s : names) 
	    	  	  if (s.charAt(0) == a.charAt(0)) explorer_entry.add(s);
	      
	    explorer_list.setEntrys(explorer_entry);
	  }
	  public nWindowPanel clear() { 
	    super.clear(); return this; }
	  public nWindowPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nWindowPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}

