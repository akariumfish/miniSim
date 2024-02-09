package Macro;

import UI.nCursor;
import sData.nRunnable;
import sData.sBoo;
import sData.sValueBloc;
import sData.sVec;

public class MCursor extends MBasic { 
	  static class MCursor_Builder extends MAbstract_Builder {
		    MCursor_Builder() { super("cursor", "add a cursor"); show_in_buildtool = true; }
		  MCursor build(Macro_Sheet s, sValueBloc b) { MCursor m = new MCursor(s, b); return m; }
		  }
		  public nCursor cursor;
		    public sVec pval = null;
		    public sVec dval = null;
		    public sBoo show = null;
		  nRunnable sheet_grab_run, pval_run;
		  Macro_Connexion in, out;
		  MCursor(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "cursor", _bloc); }
		  void init() {
		    pval = newVec("pos", "pos");
		      show = newBoo(false, "show", "show"); //!!!!! is hided by default
		      dval = newVec("dir", "dir");
		      cursor = new nCursor(mmain(), value_bloc, true);
		      mmain().cursors_list.add(cursor);
		      mmain().update_cursor_selector_list();
		      sheet.sheet_cursors_list.add(cursor);
		      sheet.cursor_count++;
		      
		      if (!(pval.x() == 0 && pval.y() == 0)) {
		        setPosition(pval.get().x - sheet.grabber.getX(), 
		            pval.get().y - sheet.grabber.getY());
		        moving(); 
		      }
		      
		      cursor.addEventClear(new nRunnable(cursor) { public void run() { 
		          sheet.sheet_cursors_list.remove(((nCursor)builder));
		        mmain().cursors_list.remove(((nCursor)builder)); 
		        sheet.cursor_count--;
		        mmain().update_cursor_selector_list(); }});
		      
		      grab_pos.addEventChange(new nRunnable() { public void run() {
		          if (cursor.pval != null) cursor.pval.set(
		              grab_pos.get().x + sheet.grabber.getX(), 
		              grab_pos.get().y + sheet.grabber.getY());
		      } });
		      pval_run = new nRunnable() { public void run() {
		          if (sheet != mmain())
		            setPosition(cursor.pval.get().x - sheet.grabber.getX(), 
		                  cursor.pval.get().y - sheet.grabber.getY());
		          else setPosition(cursor.pval.get().x, cursor.pval.get().y);
		          moving();
		    }};
		    cursor.pval.addEventChange(pval_run);
		      
		      
		      if (sheet != mmain()) {
		        sheet_grab_run = new nRunnable() { public void run() {
		          setPosition(cursor.pval.get().x - sheet.grabber.getX(), 
		              cursor.pval.get().y - sheet.grabber.getY());
		          moving();
		        } };
		        sheet.grab_pos.addEventChange(sheet_grab_run);
		      }
		  }
		  void build_param() {
			  
		  }
		  void build_normal() {
		    addEmptyS(0);
		    addSwitchS(1, "show", show);
		  }
		  public MCursor clear() {
		    super.clear(); 
		    cursor.clear();
		    if (pval != null) pval.removeEventChange(pval_run);
		    if (sheet != mmain()) sheet.grab_pos.removeEventChange(sheet_grab_run);
		    return this; }
		  public MCursor toLayerTop() {
		    super.toLayerTop(); 
		    return this; }
		}
