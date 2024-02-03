package UI;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import sData.nRunnable;
import sData.sStr;

public class nFilePicker extends nWindowPanel {
	  
	  public nFilePicker addFilter(String f) { 
	    ext_filter.add(f); 
	    mitig_ac = true; 
	    update(); 
	    mitig_ac = false; 
	    return this; 
	  }
	  
	  ArrayList<String> explorer_entry;
	  ArrayList<String> ext_filter;
	  
	  nList explorer_list;
	  nWidget info;
	  
	  sStr val_cible;
	  boolean autoclose = false, mitig_ac = false, filter_db = false;
	  
	  ArrayList<nRunnable> eventsChoose = new ArrayList<nRunnable>();
	  public nFilePicker addEventChoose(nRunnable r) { eventsChoose.add(r);  return this; } 
	  
	  public nFilePicker(nGUI _g, nTaskPanel _task, sStr _sv, boolean _autoclose, String t, boolean _fdb) { 
	    super(_g, _task, t); 
	    val_cible = _sv;
	    autoclose = _autoclose;
	    filter_db = _fdb;
	    explorer_entry = new ArrayList<String>();
	    ext_filter = new ArrayList<String>();
	    
	    explorer_list = getShelf().addList(5, 10, 1).setTextAlign(PApplet.LEFT);
	    
	    if (!_autoclose) {
	      info = getShelf().addSeparator(0.25F)
	        .addDrawer(1.4F)
	          .addLinkedModel("Label-S4", "Selected File :")
	          .setLinkedValue(val_cible).setTextAlignment(PApplet.LEFT, PApplet.TOP);
	    
	      getShelf()
	        .addSeparator(0.25F)
	        .addDrawer(10.25F, 1)
	          .addCtrlModel("Button-S2-P2", "OK")
	          .setRunnable(new nRunnable() { public void run() { 
	            clear(); runEvents(eventsChoose); } }).getShelf();
	    }
	    update_list();
	    
	    explorer_list.addEventChange_Builder(new nRunnable() { 
	      public void run() {
	        String choice = explorer_list.last_choice_text;
	        if (!choice.equals("")) { 
	          val_cible.set(choice); 
	          if (autoclose && !mitig_ac) { clear(); nRunnable.runEvents(eventsChoose); }
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
	    String[] names = null;
	    File file = new File(gui.app.sketchPath());
	    if (file.isDirectory()) { names = file.list(); } // all files in sketch directory
	    if (names != null) {
	      explorer_entry.clear();
	      for (String s : names) {
	        String ext = "";
	        int i = s.length() - 1;
	        while (i > 0 && s.charAt(i) != '.') { ext = s.charAt(i) + ext; i--; }
	        boolean fn = false;
	        for (String st : ext_filter) { fn = fn || st.equals(ext); }
	        if (fn && (!filter_db || !s.equals("database.sdata"))) explorer_entry.add(s);
	      }
	      explorer_list.setEntrys(explorer_entry);
	      selectEntry(val_cible.get());
	    }
	  }
	  public nWindowPanel clear() { 
	    super.clear(); return this; }
	  public nWindowPanel updateHeight() { 
	    super.updateHeight(); return this; }
	  public nWindowPanel updateWidth() { 
	    super.updateWidth(); return this; }
	}
