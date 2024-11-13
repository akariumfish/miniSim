package Macro;

import java.util.ArrayList;

import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;


public class M_Sheet {}

class MZone extends MBasic { 
  static class Builder extends MAbstract_Builder {
    Builder(Macro_Main m) { super("zone", "Zone", "--", "Sheet"); }
    MZone build(Macro_Sheet s, sValueBloc b) { MZone m = new MZone(s, b); return m; }
  }
  sVec corner_pos;
  nLinkedWidget corner_widg;
  nWidget zone_widg;
  Drawable zone_draw;
  nRunnable goto_run, select_run;
  nRunnable move_run, zoom_run;
  nWidget screen_widget;
  nRunnable rebuild_run;
  Macro_Element zone_ref_elem = null;
  sVec zone_size;
  sBoo do_select;
  sBoo view_select, view_goto, view_zonesize;
  
  
  MZone(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "zone", _bloc); }
  void init() {
	  view_select = newBoo(false, "view_select");
	  view_goto = newBoo(true, "view_goto");
	  view_zonesize = newBoo(false, "view_zonesize");
	  do_select = newBoo(false, "do_select");
	  
	  goto_run = new nRunnable() { public void run() {
	      float ns = mmain().inter.cam.cam_scale.get();
	      if (gui.view.size.x / zone_widg.getLocalSX() < gui.view.size.y / zone_widg.getLocalSY())
	        ns *= gui.view.size.x / zone_widg.getLocalSX();
	      else ns *= gui.view.size.y / zone_widg.getLocalSY();
	      ns = Math.min(1.0F, ns);
	      ns = Math.max(0.03F, ns);
	      mmain().inter.cam.cam_scale.set(ns);
	      mmain().inter.cam.cam_pos
	        .set(-(zone_widg.getX() + zone_widg.getLocalSX()/2) * mmain().inter.cam.cam_scale.get(), 
	             -(zone_widg.getY() + zone_widg.getLocalSY()/2) * mmain().inter.cam.cam_scale.get() );
	  }};
	  select_run = new nRunnable() { public void run() {
	  	  Rect zone = new Rect(zone_widg.getX(), zone_widg.getY(), 
	  			zone_widg.getSX(), zone_widg.getSY());
	  	  Rect front_rect = new Rect();
	      for (Macro_Abstract m : sheet.child_macro) {
	    	  	  front_rect.set(m.front.getX(), m.front.getY(), 
	    	  			m.front.getSX(), m.front.getSY());
	    	  	  if (Rect.rectCollide(zone, front_rect)) {
	    	  		  m.switch_select();
	    	  	  }
	      }
	  }};
	  rebuild_run = new nRunnable() { public void run() {
		  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
			  rebuild();
		  }}); }};
	  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		  view_select.addEventChange(rebuild_run);
		  view_goto.addEventChange(rebuild_run);
		  view_zonesize.addEventChange(rebuild_run);
		  
		  zoom_run.run();
	  }});
  }
  void build_param() {
	  addEmptyS(1);
	  addSelectL(0, do_select, view_select, view_goto, view_zonesize, 
			  "autosel", "v sel", "v goto", "v zone").no_mirror();
	  build_normal();
  }
  void build_normal() {
	  if (view_goto.get()) {
		  zone_ref_elem = addInputBang(0, "goto", 
				new nRunnable() { public void run() { goto_run.run(); }}).elem;
		  addTrigS(1, "goto", goto_run); }
	  
	  if (view_select.get()) {
		  zone_ref_elem = addInputBang(0, "select", 
				new nRunnable() { public void run() { select_run.run(); }}).elem;
		  addTrigS(1, "select", select_run); }

	  if (view_zonesize.get() ) {
		  zone_size = newSRowVec("zone_size");
		  zone_ref_elem = addEmptyS(0);
	  }
	  else zone_size = newVec("zone_size");
	  
	  if (zone_ref_elem == null) zone_ref_elem = addEmptyS(0);
	  
	  build_zone();
  }
  void build_zone() {
	  zone_widg = zone_ref_elem.addModel("Field");
	  zone_widg.setPosition(ref_size * 2 / 16, ref_size * 2 / 16)
	  	.setPassif().stackDown().alignLeft();
	  zone_draw = new Drawable(gui.drawing_pile, 0) { public void drawing() {
		  gui.app.noFill(); gui.app.stroke(220);
		  gui.app.rect(	zone_widg.getX(), zone_widg.getY(), 
				  		zone_widg.getSX(), zone_widg.getSY() );
	  } };
	  zone_widg.setDrawable(zone_draw);

	  corner_pos = newVec("corner_pos");
	  if (!loading_from_bloc) corner_pos.set(ref_size*3.0F, ref_size*3.0F);
	  corner_pos.addEventChange(new nRunnable() { public void run() {
		  zone_widg.setSize(corner_pos.get().x + corner_widg.getSX(), 
				  		   corner_pos.get().y); 
		  zone_size.set(corner_pos.get().x + corner_widg.getSX(), 
		  		   corner_pos.get().y + corner_widg.getSY());
          PVector p = new PVector(corner_pos.get().x + corner_widg.getX()
        		  - corner_widg.getLocalX() - corner_widg.getSX() / 2
        		  + screen_widget.getSX(), 
        		  corner_pos.get().y + corner_widg.getY() - corner_widg.getLocalY()
        		  - corner_widg.getSY() / 2 + screen_widget.getSY());
          p = mmain().inter.cam.cam_to_screen(p);
          screen_widget.setPosition(p.x, p.y);
          
          update_selected();
	  }});
	  corner_widg = zone_ref_elem.addLinkedModel("MC_SpeTrig");
	  corner_widg.alignDown().alignLeft();
	  corner_widg.setLinkedValue(corner_pos);
	  
	  zone_size.addEventChange(new nRunnable() { public void run() {
		  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
			  corner_pos.set(zone_size.get().x - corner_widg.getSX(), 
					  zone_size.get().y - corner_widg.getSY());
		  }});
	  }});
	  
	  screen_widget = gui.theme.newWidget(sheet.mmain().screen_gui, "Cursor")
	      .setSize(ref_size/2, ref_size/2);
	  PVector p = new PVector(corner_widg.getX(), 
			  corner_widg.getY());
      p = mmain().inter.cam.cam_to_screen(p);
      screen_widget.setPosition(p.x, p.y).setGrabbable().center();
	  screen_widget.addEventDrag(new nRunnable() {public void run() {
	        PVector p = new PVector(screen_widget.getX(), 
	        		screen_widget.getY());
	        p = mmain().inter.cam.screen_to_cam(p);
	        corner_pos.set(p.x - corner_widg.getX() + corner_widg.getLocalX()
	        				+ corner_widg.getSX() / 2, 
	        		p.y - corner_widg.getY() + corner_widg.getLocalY()
	        				+ corner_widg.getSY() / 2);
	  }});
	
	  move_run = new nRunnable() {public void run() { 
	        if (corner_pos != null) {
	        	  PVector p = new PVector(corner_pos.get().x + corner_widg.getX()
	        	  	- corner_widg.getLocalX() - corner_widg.getSX() / 2
	        	  	+ screen_widget.getSX(), 
	        	  	corner_pos.get().y + corner_widg.getY() - corner_widg.getLocalY()
	        	  	- corner_widg.getSY() / 2 + screen_widget.getSY());
	        	  p = mmain().inter.cam.cam_to_screen(p);
	        	  screen_widget.setPosition(p.x, p.y);
	        }
	        }};
      zoom_run = new nRunnable() {public void run() { 
	      update_view();
	      if (corner_pos != null) {
	        	PVector p = new PVector(corner_pos.get().x + corner_widg.getX()
	        		- corner_widg.getLocalX() - corner_widg.getSX() / 2
	        	  	+ screen_widget.getSX(), 
	        		corner_pos.get().y + corner_widg.getY() - corner_widg.getLocalY()
	        		- corner_widg.getSY() / 2 + screen_widget.getSX());
	        	p = mmain().inter.cam.cam_to_screen(p);
	        	screen_widget.setPosition(p.x, p.y);
	      }
	      }};
	      
	  mmain().inter.cam.addEventMove(move_run);
	  mmain().inter.cam.addEventZoom(zoom_run);
	  
//	  grabber.addEventVisibilityChange(new nRunnable() { public void run() { update_view(); }});
	  
  }
  void update_selected() {
	  if (do_select.get()) {
		  mmain.szone_clear_select();
		  Rect zone = new Rect(zone_widg.getX(), zone_widg.getY(), 
	  			zone_widg.getSX(), zone_widg.getSY());
	  	  Rect front_rect = new Rect();
	      for (Macro_Abstract m : sheet.child_macro) {
	    	  	  front_rect.set(m.front.getX(), m.front.getY(), 
	    	  			m.front.getSX(), m.front.getSY());
	    	  	  if (Rect.rectCollide(zone, front_rect)) {
	    	  		  m.szone_select();
	    	  	  }
	      }
	  }
  }

  void update_view() {
    if (mmain().inter.cam.cam_scale.get() < 0.5) {// && !grabber.isHided()) {  
        screen_widget.show(); 
        toLayerTop(); 
    } else { 
    		screen_widget.hide();
    		toLayerTop();
    }
  }
  public MZone clear() {
    super.clear(); 
    zone_draw.clear();
    screen_widget.clear();
    return this; }
  public MZone toLayerTop() {
    super.toLayerTop(); 
    return this; }
}

class MCursor extends MBasic { 
  static class MCursor_Builder extends MAbstract_Builder {
    MCursor_Builder(Macro_Main m) { super("cursor", "Cursor", "add a cursor", "Sheet"); 
	  first_start_show(m); }
  MCursor build(Macro_Sheet s, sValueBloc b) { MCursor m = new MCursor(s, b); return m; }
  }
  public nCursor cursor;
    public sVec pval = null;
    public sVec dval = null;
    public sBoo show = null, draw_dir,show_pos,show_dir,show_mov,show_show,show_link;
    public sVec mval;
  nRunnable sheet_grab_run, pval_run, movingchild_run;
  Macro_Connexion in, out;
  Macro_Connexion out_link;
  MCursor(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "cursor", _bloc); }
  void init() {
	  super.init();

	  show_pos = newBoo(true, "show_pos_ctrl");
	  show_dir = newBoo(false, "show_dir_ctrl");
	  show_mov = newBoo(false, "show_mov_ctrl");
	  show_show = newBoo(true, "show_show_ctrl");
	  draw_dir = newBoo(true, "draw_dir");
	  show_link = newBoo(false, "show_link_ctrl");
  }
  
  void init_cursor() {
	  cursor = new nCursor(mmain(), value_bloc, true);
	  mmain().cursors_list.add(cursor);
	  sheet.sheet_cursors_list.add(cursor);
	  sheet.cursor_count++;
	  
	  if (!(pval.x() == 0 && pval.y() == 0)) {
	    setPosition(pval.get().x - sheet.grabber.getX(), 
	        pval.get().y - sheet.grabber.getY());
	    moving(); 
	  }
	  
	  cursor.addEventClear(new nRunnable(cursor) { public void run() { 
		  cursor = null;
	      sheet.sheet_cursors_list.remove(((nCursor)builder));
	    mmain().cursors_list.remove(((nCursor)builder)); 
	    sheet.cursor_count--;
	  }});
	  
	  grab_pos.addEventChange(new nRunnable() { public void run() {
	      if (cursor != null && cursor.pval!= null) cursor.pval.set(
	          grab_pos.get().x + sheet.grabber.getX(), 
	          grab_pos.get().y + sheet.grabber.getY());
	  } });
  	pval_run = new nRunnable() { public void run() {
		if (sheet != mmain())
			setPosition(cursor.pval.get().x - sheet.grabber.getX(), 
	          cursor.pval.get().y - sheet.grabber.getY());
		else setPosition(cursor.pval.get().x, cursor.pval.get().y);
		moving();
		  
		if (out != null && cursor != null) out.send(cursor.pval.asPacket());
	}};
	cursor.pval.addEventChange(pval_run);
	
	movingchild_run = new nRunnable() { public void run() { moving(); } };
	  
	if (sheet != mmain()) {
		sheet_grab_run = new nRunnable() { public void run() {
			PVector tm = new PVector(cursor.pval.get().x, cursor.pval.get().y);
		    setPosition(tm.x - sheet.grabber.getX(), 
		                tm.y - sheet.grabber.getY());
		    moving();
		} };
		sheet.grab_pos.addEventAllChange(sheet_grab_run);
	}
  }

  protected void group_move_custom(float x, float y) {
//	  cursor.pval.set(cursor.pval.get().x + x, 
//			  cursor.pval.get().y + y);
  }
  void build_param() {
	  addEmptyS(0);
	  addEmptyS(1);
	  out_link = addOutput(2, "cursor_link");
	  out_link.set_link();
	  show = newRowBoo(false, "show"); //!!!!! is hided by default
	  pval = newRowVec("pos");
	  dval = newRowVec("dir");
	  mval = newRowVec("mov");
	  addEmptyS(1);
	  in = addInput(0, "move", new nRunnable() { public void run() {
		  if (in.lastPack() != null && in.lastPack().isBang()) // && pval != null && mval != null
			  cursor.pval.add(mval.get());
	  } });
	  addSelectS(0, show_link, show_show,
			  "link", "show").no_mirror();
	  addSelectL(1, draw_dir, show_pos, show_dir, show_mov, 
			  "point", "pos", "dir_val", "move").no_mirror();
	  init_cursor();
	  draw_dir.addEventChange(new nRunnable() { public void run() {
		  if (draw_dir.get()) {
			  cursor.show_dir = true;
			  cursor.update_view();
		  } else {
			  cursor.show_dir = false;
			  cursor.update_view();
		  }
	  }});
  }
  void build_normal() {
	  if (show_link.get()) {
	    addEmptyS(0); addEmptyS(1);
	    out_link = addOutput(2, "cursor_link"); out_link.set_link();
	  }
	  if (show_show.get()) show = newRowBoo(false, "show");
	  else show = newBoo(false, "show"); 
	  
	  if (show_pos.get()) pval = newRowVec("pos");
	  else pval = newVec("pos");
	  if (show_dir.get()) dval = newRowVec("dir");
	  else dval = newVec("dir");
	  if (show_mov.get()) {
		  mval = newRowVec("mov");
		  addEmptyS(1);
		  in = addInput(0, "move", new nRunnable() { public void run() {
			  if (in.lastPack() != null && in.lastPack().isBang()) // && pval != null && mval != null
				  cursor.pval.add(mval.get());
		  } });
	  }
	  else mval = newVec("mov");
	  
	  init_cursor();
	  draw_dir.addEventChange(new nRunnable() { public void run() {
		  if (draw_dir.get()) {
			  cursor.show_dir = true;
			  cursor.update_view();
		  } else {
			  cursor.show_dir = false;
			  cursor.update_view();
		  }
	  }});
  }
  boolean flag_del = false;
  public MCursor clear() {
	  flag_del = true;
	if (sheet != mmain()) sheet.grab_pos.removeEventAllChange(sheet_grab_run);
	if (pval != null) pval.removeEventChange(pval_run);
    super.clear(); 
    if (cursor != null) cursor.clear();
    return this; }
  public MCursor toLayerTop() {
    super.toLayerTop(); 
    return this; }
}


class MNode extends MBasic { 
  static class Builder extends MAbstract_Builder {
	  Builder(Macro_Main m) { super("node", "Node", "Connections Node", "Sheet"); 
	  first_start_show(m); }
	  MNode build(Macro_Sheet s, sValueBloc b) { MNode m = new MNode(s, b); return m; }
    }
  sInt co_nb; sBoo as_in, as_out, as_label, as_link, is_point;
  Macro_Connexion in_link, out_link; 
  ArrayList<MNode> nodes;
  ArrayList<Macro_Connexion> out_list;
  ArrayList<Macro_Connexion> in_list;
  int in_col = 0; int out_col = 2; int lab_col = 1;
  MNode(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "node", _bloc); }
	public void init() {
		co_nb = newInt(1, "co_nb");
		co_nb.set_limit(1, 8);

		as_in = newBoo(true, "as_in");
		as_out = newBoo(true, "as_out");
		as_label = newBoo(false, "as_label");
		as_link = newBoo(false, "as_link");
		is_point = newBoo(true, "is_point");
		  
		nodes = new ArrayList<MNode>();
		out_list = new ArrayList<Macro_Connexion>();
		in_list = new ArrayList<Macro_Connexion>();
		
		mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
			nRunnable change_run = new nRunnable() { public void run() { 
				  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		  			  if (!rebuilding) rebuild(); }}); }};
		  	co_nb.addEventChange(change_run);
		  	as_in.addEventChange(change_run); 
		  	as_out.addEventChange(change_run); 
		  	as_in.addEventChange(new nRunnable() { public void run() { if (!as_in.get()) as_out.set(true); }}); 
		  	as_out.addEventChange(new nRunnable() { public void run() { if (!as_out.get()) as_in.set(true); }}); 
		  	as_label.addEventChange(change_run); 
		  	as_link.addEventChange(change_run); 
		}});
		if (RConst.xor(as_in.get(), as_out.get())) {
			if (!as_label.get()) out_col = 0;
			else {
				if (as_in.get()) { in_col = 0; out_col = 1; lab_col = 1; addEmptyS(0); }
				if (as_out.get()) { in_col = 0; out_col = 1; lab_col = 0; addEmptyS(1); }
			}
		} else {
			if (!as_label.get() ) out_col = 1;
			else { out_col = 2; lab_col = 1; if (as_link.get()) addEmptyS(1); }
		}
	}	
	public void build_param() {
		if (is_point.get()) {
			build_normal();
			return;
		}
		build_normal();
		addSSelectToInt(in_col, co_nb).no_mirror();
//		addEmptyS(2).no_mirror();
		addSelectL(out_col, as_in, as_out, as_label, as_link, "in", "out", "label", "link")
			.no_mirror();
	}	
	public void build_normal() {
		if (is_point.get()) {
			build_point();
			return;
		}
		if (as_link.get() && (as_out.get() || as_in.get())) {
			nRunnable link_run;
			if (as_out.get()) {
				in_link = addInput(in_col, "link_in");
				in_link.set_link();
			}
			if (as_in.get()) {
				out_link = addOutput(out_col, "link_out");
				link_run = new nRunnable() { public void run() { update_nodelist(); }};
				out_link.set_link();
				out_link.addEventLink(link_run).addEventUnLink(link_run);
			}
		}
		for (int i = 0 ; i < co_nb.get() ; i++) {
			Macro_Connexion o = null;
			if (as_out.get()) {
			  	if (!as_in.get()) o = addSheetInput(out_col, "out"+(i+1)).connect;
			  	else o = addOutput(out_col, "out"+(i+1));
			  	o.set_undefine();
				out_list.add(o);
			}
			if (as_in.get()) {
			  	nObjectTrio pr = new nObjectTrio();
			  	pr.obj2 = o;
				Macro_Connexion c = null;
				if (!as_out.get()) c = addSheetOutput(in_col, "in"+(i+1)).connect;
				else c = addInput(in_col, "in"+(i+1));
			  	c.set_undefine();
				in_list.add(c);
				if (o != null) c.direct_connect(o);
			  	pr.obj1 = c; pr.obj3 = i;
			  	c.addEventReceive(new nRunnable(pr) { public void run() {
			  		update_nodelist();
					nObjectTrio p = (nObjectTrio)builder;
					int id = (int) p.obj3;
					Macro_Connexion in = (Macro_Connexion) p.obj1;
					for (MNode n : nodes) {
						if (n.as_in.get() && id < n.in_list.size())
							n.in_list.get(id).receive(in, in.lastPack());
						else if (!n.as_in.get() && n.as_out.get() && id < n.out_list.size())
							n.out_list.get(id).send(in.lastPack());
							
					}
				}});
			}
			if (as_label.get()) {
				sStr l = newStr("label"+(i+1), "label"+(i+1));
				nLinkedWidget w = addLinkedSField(lab_col, l);
				if (as_in.get() && as_out.get()) 
					w.setPosition(ref_size*-1.625, ref_size*0.125).setSX(ref_size*5.5);
				else if (as_in.get()) 
					w.setPosition(ref_size*-1.375, ref_size*0.125).setSX(ref_size*3.5);
				else if (as_out.get()) 
					w.setPosition(ref_size*0.125, ref_size*0.125).setSX(ref_size*3.5);
			}
		}
	}

	ArrayList<Macro_Bloc> link_blocs = new ArrayList<Macro_Bloc>();
	void test_connect(Macro_Connexion out) {
		if (out != null) for (Macro_Connexion m : out.connected_inputs) {
			if (m.elem.bloc.val_type.get().equals("node")) {
				MNode n = (MNode)m.elem.bloc;
				if (m.base_info.equals("link_in")) {
					nodes.add((MNode)m.elem.bloc);
					if (n.out_link != null) {
						test_connect(n.out_link);
						for (Macro_Connexion c2 : n.out_link.direct_cos) 
							test_connect(c2);
					}
				} 
				else for (int i = 1 ; i < 9 ; i++) if (m.base_info.equals("in"+i)) {
					for (Macro_Connexion c : m.direct_cos) test_connect(c);
					for (MNode n2 : n.nodes) for (Macro_Connexion c : n2.in_list) {
						if (c.base_info.equals("in"+i)) {
							test_connect(c);
							for (Macro_Connexion c2 : c.direct_cos) test_connect(c2);
						}
					}
				}
			}
			else if (m.elem.bloc.val_type.get().equals("gate")) {
				MGate g = (MGate)m.elem.bloc;
				test_connect(g.get_active_out());
			} else if (m.linkable) {
				link_blocs.add(m.elem.bloc);
//				if (!m.elem.bloc.link_blocs.contains(out.elem.bloc)) 
//					m.elem.bloc.link_blocs.add(out.elem.bloc);
			}
		}
	}
	void update_nodelist() {
		nodes.clear();
		link_blocs.clear();
		if (out_link != null) {
			test_connect(out_link);
			for (Macro_Connexion c : out_link.direct_cos) test_connect(c); 
		}
	}

	//as link angle
	boolean angle_constructor = false;
	MNode(Macro_Sheet _sheet, Macro_Connexion building_co, PVector pos) { 
		super(_sheet, "node", null); 
		if (building_co.linkable) {
			as_link.set(true);
			in_list.get(0).set_link();
		}
//		if (!loading_from_bloc) build_point();
		if (building_co.type == OUTPUT) {
			pos.x -= pos.x%GRID_SNAP_FACT;
			pos.y -= pos.y%GRID_SNAP_FACT;
			setPosition(pos.x-ref_size*0.375F, pos.y-ref_size*1.0F);
			building_co.connect_to(in_list.get(0));
			building_co.end_building_line();
			out_list.get(0).start_building_line();
		} else {
			pos.x -= pos.x%GRID_SNAP_FACT;
			pos.y -= pos.y%GRID_SNAP_FACT;
			setPosition(pos.x-ref_size*1.0F, pos.y-ref_size*1.0F);
			building_co.connect_to(out_list.get(0));
			building_co.end_building_line();
			in_list.get(0).start_building_line();
		}
		angle_constructor = true;
	}
  
	void build_point() {
		hideCtrl();
		if (param_open != null) param_open.setPX(-ref_size * 0.75);
		if (mirror_sw != null) mirror_sw.setPX(-ref_size * 1.25);
//		grabber.hide();
	    Macro_Element ei = new Macro_Element(this, "", "MC_Element_Single", "in1", INPUT, OUTPUT, true);
	      elements.add(ei);
		ei.back.setSX(ref_size*0);
	    ei.drawer_width = ref_size*1.25F;
	    if (ei.sheet_connect != null) ei.connect.direct_connect(ei.sheet_connect);
	    ei.connect.msg_view.setSX(0);
	    if (ei.sheet_connect != null) ei.sheet_connect.msg_view.setSX(0);
	    addElement(0, ei); 
		in_list.add(ei.connect);
	    Macro_Element eo = new Macro_Element(this, "", "MC_Element_Single", "out1", OUTPUT, INPUT, true);
	      elements.add(eo);
	    eo.back.setSX(ref_size*0);
	    eo.drawer_width = ref_size*1.25F;
	    if (eo.sheet_connect != null) eo.sheet_connect.direct_connect(eo.connect);
	    eo.connect.msg_view.setSX(0);
	    if (eo.sheet_connect != null) eo.sheet_connect.msg_view.setSX(0);
	    addElement(0, eo); 
		out_list.add(eo.connect);
	    getShelf(0).removeDrawer(eo);
	    eo.ref.setParent(ei.ref);
		eo.ref.setPX(ref_size*0).setPY(ref_size*0);
		ei.ref.setPX(ref_size*0.75); 
		if (as_link.get()) {
			ei.connect.set_link();
			eo.connect.set_link();
		} 
		ei.connect.direct_connect(eo.connect);
//		if (ei.sheet_connect != null && eo.sheet_connect != null) 
//			ei.sheet_connect.direct_connect(eo.sheet_connect);
		
		if (openning.get() == OPEN) for (Macro_Element e : elements) e.show();
		toLayerTop();

		mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
			if (!loading_from_bloc && !angle_constructor) {
				is_point.set(false);
				if (!rebuilding) rebuild();
			} else {
//				if (	out_list.get(0).connected_inputs.size() > 0 && 
//						in_list.get(0).connected_outputs.size() > 0 ) reduc();
//				else {
//					//auto close after co 
//				}
			}
		}});
	}
	public MNode clear() {
		super.clear(); 
		return this; }
}



class MSheetBloc extends MBasic { 
	static class Builder extends MAbstract_Builder {
		Builder(Macro_Main m) { 
		super("sheetbloc", "SheetBloc", "sheet tools", "Sheet"); 
		first_start_show(m); }
	MSheetBloc build(Macro_Sheet s, sValueBloc b) { MSheetBloc m = new MSheetBloc(s, b); return m; }
}
	Macro_Element menu_elem;
	Macro_Connexion in_menu;
	nLinkedWidget stp_view; sBoo menu_open, menu_reduc; sVec menu_pos; sInt menu_tab;
	nRunnable grab_run, reduc_run, close_run, tab_run;
	  nRunnable goto_run;
	  Macro_Connexion out_sheet;//, out_obj;
	  sBoo show1, show2, show3, show4;
	MSheetBloc(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "sheetbloc", _bloc); 
//		unclearable = true; 
		if (sheet == mmain() && mmain().main_sheetbloc != null) { clear(); return; }
		if (sheet == mmain()) mmain().main_sheetbloc = this;
	}
	void init() {
	    menu_open = newBoo("stp_snd", "stp_snd", false);
	    menu_reduc = newBoo("menu_reduc", "menu_reduc", false);
	    menu_tab = newInt("menu_tab", "menu_tab", 0);
	    menu_pos = newVec("menu_pos", "menu_pos");
	    show1 = newBoo("show1", "show1", false);
	    show2 = newBoo("show2", "show2", false);
	    show3 = newBoo("show3", "show3", false);
	    show4 = newBoo("show4", "show4", false);

	    goto_run = new nRunnable() { public void run() {
	      float ns = mmain().inter.cam.cam_scale.get();
	      if (gui.view.size.x / sheet.back.getLocalSX() < gui.view.size.y / sheet.back.getLocalSY())
	        ns *= gui.view.size.x / sheet.back.getLocalSX();
	      else ns *= gui.view.size.y / sheet.back.getLocalSY();
	      ns = Math.min(1.0F, ns);
	      ns = Math.max(0.03F, ns);
	      mmain().inter.cam.cam_scale.set(ns);
	      mmain().inter.cam.cam_pos
	        .set(-(sheet.back.getX() + sheet.back.getLocalSX()/2) * mmain().inter.cam.cam_scale.get(), 
	             -(sheet.back.getY() + sheet.back.getLocalSY()/2) * mmain().inter.cam.cam_scale.get() );
	    }};
	    grab_run = new nRunnable() { public void run() { if (sheet.sheet_front != null) 
	        menu_pos.set(sheet.sheet_front.grabber.getLocalX(), 
	        				sheet.sheet_front.grabber.getLocalY()); } };
	    reduc_run = new nRunnable() { public void run() {
	    		if (sheet.sheet_front != null) menu_reduc.set(sheet.sheet_front.collapsed); } };
	    tab_run = new nRunnable() { public void run() {
	    		if (sheet.sheet_front != null) menu_tab.set(sheet.sheet_front.current_tab_id); } };
	    close_run = new nRunnable() { public void run() { 
	    		menu_open.set(false); } };
	}
	void init_end() {
		super.init_end();
		if (sheet != mmain() && !loading_from_bloc && menu_elem != null) { //
			sheet.add_spot("left", menu_elem);
			sheet.add_spot("right", out_sheet.elem);
		}

	    if (menu_open.get()) mmain().inter.addEventTwoFrame(new nRunnable() { public void run() {
	    		menu(); } });
	}
	void build_param() {
		menu_elem = addEmptyS(1);
		menu_elem.addTrigS("menu", new nRunnable() { public void run() {
		    		menu(); } })
		    .setInfo("open sheet general menu");
		menu_elem.addLinkedMiniSwitch("st", menu_open);
	    addInputBang(0, "open menu", new nRunnable() { public void run() { 
	    		mmain().inter.addEventTwoFrame(new nRunnable() { public void run() {
	    		menu(); } }); } });
	    out_sheet = addOutput(2, "sheet link");
	    out_sheet.set_link();
//		menu_elem.addTrigS("get obj", new nRunnable() { public void run() {
//			out_obj.send(Macro_Packet.newPacketObject(sheet)); } })
//		.setInfo("get object");
//	    out_obj = addOutput(2, "object");

	    addEmptyS(2); 
	    nRunnable show_curs_run = new nRunnable() { public void run() {
			for (nCursor c : sheet.sheet_cursors_list) c.show.set(true); } };
		nRunnable hide_curs_run = new nRunnable() { public void run() {
			for (nCursor c : sheet.sheet_cursors_list) c.show.set(false); } };
	    		
	    addTrigS(0, "show", "show all sheet cursor", show_curs_run);
	    addTrigS(1, "hide", "hide all sheet cursor", hide_curs_run);
	    addInputBang(0, "show", show_curs_run);
	    addInputBang(1, "hide", hide_curs_run);
    		
	    addEmptyS(1); addEmptyS(2); 
	    Macro_Element m = 
	    addEmptyXL(0);
	    m.addSwitchXLSelector(1, "view", "show view access in normal view", show1);
	    if (sheet != mmain()) m.addSwitchXLSelector(2, "open", "show openning control in normal view", show2);
	    m.addSwitchXLSelector(3, "self", "show self object access in normal view", show3);
	    if (sheet != mmain()) m.addSwitchXLSelector(4, "prio", "show sheet priority control in normal view", show4);
	    
	    addTrigS(1, "view", goto_run).setInfo("view full sheet");
	    addInputBang(0, "view", new nRunnable() { public void run() { goto_run.run(); }});
	    addEmptyS(2);
	    
	    if (sheet != mmain()) {
	    addInputBang(0, "Deploy Sheet", 
	    		new nRunnable() { public void run() { sheet.deploy(); }});
	    addInputBang(1, "Open Sheet", 
	    		new nRunnable() { public void run() { sheet.open(); }});
	    addInputBang(2, "Reduc Sheet", 
	    		new nRunnable() { public void run() { sheet.reduc(); }});
	    }
	    newRowProtectedValue(sheet.val_self);
	    if (sheet != mmain()) newRowValue_Pan(sheet.priority);

//	    newRowValue(sheet.grab_pos);
	}
	void build_normal() {
	    if (show2.get() || show3.get() || show4.get()) addEmptyS(2);
	    
	    nRunnable menu_run = new nRunnable() { public void run() {
	    	menu(); grab_run.run(); tab_run.run(); reduc_run.run(); } };
	    menu_elem = addInput(0, "menu").setFilterBang()
	    		.addEventReceiveBang(menu_run).elem;
	    menu_elem.addTrigS("menu", menu_run)
		    .setInfo("open sheet general menu");

	    out_sheet = addOutput(1, "sheet link");
	    out_sheet.set_link();
//		menu_elem.addTrigS("get obj", new nRunnable() { public void run() {
//			out_obj.send(Macro_Packet.newPacketObject(sheet)); } })
//		.setInfo("get object");
//	    out_obj = addOutput(2, "object");

	    if (show2.get() || show3.get() || show4.get()) addEmptyS(2); 
	    nRunnable show_curs_run = new nRunnable() { public void run() {
    			for (nCursor c : sheet.sheet_cursors_list) c.show.set(true); } };
		nRunnable hide_curs_run = new nRunnable() { public void run() {
			for (nCursor c : sheet.sheet_cursors_list) c.show.set(false); } };

//	    addTrigS(0, "show", "show all sheet cursor", show_curs_run);
//	    addTrigS(1, "hide", "hide all sheet cursor", hide_curs_run);
	    
	    if (show1.get()) {
		    addTrigS(1, "view", goto_run).setInfo("view full sheet");
		    addInputBang(0, "view", new nRunnable() { public void run() { goto_run.run(); }});
		    if (show2.get() || show3.get() || show4.get())
		    		addEmptyS(2);
	    }
	    if (show2.get() && sheet != mmain()) {
		    addInputBang(0, "Dep", 
		    		new nRunnable() { public void run() { sheet.deploy(); }});
		    addInputBang(1, "Opn", 
		    		new nRunnable() { public void run() { sheet.open(); }});
		    addInputBang(2, "Red", 
		    		new nRunnable() { public void run() { sheet.reduc(); }});
	    }
	    if (show3.get()) {
		  newRowProtectedValue(sheet.val_sheet);
	    }
	    if (show4.get() && sheet != mmain()) {
	    		newRowValue_Pan(sheet.priority);
	    }
	}
	void menu() {
	    menu_open.set(true);
	    sheet.build_sheet_menu();
	    if (sheet.sheet_front != null) { 
		    if (menu_pos.get().x == 0 && menu_pos.get().y == 0) grab_run.run();
		    if (menu_open.get()) {
				if (!(menu_pos.get().x == 0 && menu_pos.get().y == 0)) 
					sheet.sheet_front.grabber.setPosition(menu_pos.get());
				if (menu_reduc.get()) sheet.sheet_front.collapse(); else sheet.sheet_front.popUp();
				sheet.sheet_front.setTab(menu_tab.get());
		    }
//			setup_send.set(true);
			sheet.sheet_front.grabber.addEventDrag(grab_run); 
			sheet.sheet_front.addEventTab(tab_run)
			.addEventCollapse(reduc_run).addEventClose(close_run);  
	    }
		grab_run.run(); tab_run.run(); reduc_run.run(); 
	}
	public MSheetBloc clear() {
		if (mmain().main_sheetbloc != this) {
		    if (sheet.sheet_front != null) sheet.sheet_front.grabber.removeEventDrag(grab_run);
		    if (sheet.sheet_front != null) sheet.sheet_front.removeEventCollapse(reduc_run); 
		    if (sheet.sheet_front != null) sheet.sheet_front.removeEventClose(close_run); 
		    if (sheet.sheet_front != null) sheet.sheet_front.clear();
		    if (mmain().main_sheetbloc == this) mmain().main_sheetbloc = null; 
		}
		super.clear(); 
		return this; }
	public MSheetBloc toLayerTop() {
		super.toLayerTop(); 
		return this; }
}








//class MPack extends MBasic { 
//static class Builder extends MAbstract_Builder {
//	  Builder() { super("pack", "Pack", "Packing links", "Sheet"); }
//	  MPack build(Macro_Sheet s, sValueBloc b) { MPack m = new MPack(s, b); return m; }
//  }
//sInt co_nb;
//Macro_Connexion link; 
//ArrayList<MPack> packs;
//ArrayList<Macro_Connexion> connects;
//
//MPack(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "pack", _bloc); }
//	public void init() {
//		co_nb = newInt(2, "co_nb");
//		addSSelectToInt(0, co_nb);
//		co_nb.set_limit(2, 8);
//		packs = new ArrayList<MPack>();
//		connects = new ArrayList<Macro_Connexion>();
//	}	
//	public void build_param() {
//		link = addInput(0, "link");
//		nRunnable link_run = new nRunnable() { public void run() {packs.clear();
//		for (Macro_Connexion m : link.connected_inputs) 
//			if (m.elem.bloc.val_type.get().equals("pack")) 
//				packs.add((MPack)m.elem.bloc);
//		if (link.direct_co != null)
//			for (Macro_Connexion m : link.direct_co.connected_inputs) 
//				if (m.elem.bloc.val_type.get().equals("pack")) 
//					packs.add((MPack)m.elem.bloc);
//		}};
//		link.set_link().addEventLink(link_run).addEventUnLink(link_run);
//		for (int i = 0 ; i < co_nb.get() ; i++) {
//			Macro_Connexion c = addOutput(0, "out"+(i+1));
//			connects.add(c);
//		}
//		finish();
//	}	
//	public void build_normal() {
//		link = addOutput(0, "link");
//		nRunnable link_run = new nRunnable() { public void run() {packs.clear();
//		for (Macro_Connexion m : link.connected_inputs) 
//			if (m.elem.bloc.val_type.get().equals("pack")) 
//				packs.add((MPack)m.elem.bloc);
//		if (link.direct_co != null)
//			for (Macro_Connexion m : link.direct_co.connected_inputs) 
//				if (m.elem.bloc.val_type.get().equals("pack")) 
//					packs.add((MPack)m.elem.bloc);
//		}};
//		link.set_link().addEventLink(link_run).addEventUnLink(link_run);
//		for (int i = 0 ; i < co_nb.get() ; i++) {
//			Macro_Connexion c = addInput(0, "in"+(i+1));
//		  	nObjectPair pr = new nObjectPair();
//		  	pr.obj1 = c; pr.obj2 = i;
//		  	c.addEventReceive(new nRunnable(pr) { public void run() {
//
//				packs.clear();
//				for (Macro_Connexion m : link.connected_inputs) 
//					if (m.elem.bloc.val_type.get().equals("pack")) 
//						packs.add((MPack)m.elem.bloc);
//				if (link.direct_co != null)
//					for (Macro_Connexion m : link.direct_co.connected_inputs) 
//						if (m.elem.bloc.val_type.get().equals("pack")) 
//							packs.add((MPack)m.elem.bloc);
//				for (MPack p : packs) 
//					p.connects.get((int) ((nObjectPair)builder).obj2).send(
//						((Macro_Connexion) ((nObjectPair)builder).obj1).lastPack());
//			}});
//			connects.add(c);
//		}
//		finish();
//	}
//	public void finish() {
//		mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
//		co_nb.addEventChange(new nRunnable() { public void run() { 
//			  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
//	  			  if (!rebuilding) rebuild(); }}); }}); }});
//	}
//public MPack clear() {
//  super.clear(); 
//  return this; }
//public MPack toLayerTop() {
//  super.toLayerTop(); 
//  return this; }
//}
//
//
//
//class MMPoints extends MBasic { 
//static class Builder extends MAbstract_Builder {
//	  Builder() { super("points", "MultiPoint", "", "Sheet"); }
//	  MMPoints build(Macro_Sheet s, sValueBloc b) { MMPoints m = new MMPoints(s, b); return m; }
//  }
//sInt co_nb;
//ArrayList<Macro_Connexion> out_list;
//ArrayList<Macro_Connexion> in_list;
//
//MMPoints(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "points", _bloc); }
//	public void init() {
//		co_nb = newInt(2, "co_nb");
//		co_nb.set_limit(2, 8);
//		out_list = new ArrayList<Macro_Connexion>();
//		in_list = new ArrayList<Macro_Connexion>();
//	}	
//	public void build_param() {
//		addSSelectToInt(0, co_nb);
//		addEmptyS(1);
//		addEmptyS(2);
//		build_normal();
//	}	
//	public void build_normal() {
//		finish();
//	}
//	public void finish() {
//		for (int i = 0 ; i < co_nb.get() ; i++) {
//			Macro_Connexion c = addInput(0, "in"+(i+1));
//			in_list.add(c);
//		  	Macro_Connexion o = addOutput(2, "out"+(i+1));
//			out_list.add(o);
//		  	nObjectPair pr = new nObjectPair();
//		  	pr.obj1 = c; pr.obj2 = o;
//		  	c.addEventReceive(new nRunnable(pr) { public void run() {
//		  		nObjectPair p = (nObjectPair)builder;
//		  		if (((Macro_Connexion)p.obj1).lastPack() != null) 
//		  			((Macro_Connexion)p.obj2)
//		  			.send(((Macro_Connexion)p.obj1).lastPack());
//			}});
//			sStr l = newStr("label"+(i+1), "label"+(i+1));
//			addLinkedSField(1, l).setPX(ref_size*-1.8).setSX(ref_size*5.5);
//		}
//		
//		mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
//		co_nb.addEventChange(new nRunnable() { public void run() { 
//			  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
//	  			  if (!rebuilding) rebuild(); }}); }}); }});
//	}
//public MMPoints clear() {
//  super.clear(); 
//  return this; }
//public MMPoints toLayerTop() {
//  super.toLayerTop(); 
//  return this; }
//}
//
//
//
//class MPoint extends MBasic { 
//	static class MPoint_Builder extends MAbstract_Builder {
//		MPoint_Builder() { super("point", "Point", "connection node", "Sheet"); }
//    	MPoint build(Macro_Sheet s, sValueBloc b) { MPoint m = new MPoint(s, b); return m; }
//  }
//	Macro_Connexion in, out; 
//	sStr val_label;
//	MPoint(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "point", _bloc); }
//	public void init() {
//		val_label = newStr("val_label", "val_label");
//	}	
//	public void build_param() {
//		in = addInput(0, "in", new nRunnable() {public void run() { 
//  			if (in.lastPack() != null) out.send(in.lastPack()); }});
//		out = addOutput(2, "out");
//		addLinkedSField(1, val_label).setPX(ref_size*-1.8).setSX(ref_size*5.5);
//	}	
//	public void build_normal() {
//		in = addInput(0, "in", new nRunnable() {public void run() { 
//  			if (in.lastPack() != null) out.send(in.lastPack()); }});
//		out = addOutput(0, "out");
//	}	
//public MPoint clear() {
//  super.clear(); 
//  return this; }
//public MPoint toLayerTop() {
//  super.toLayerTop(); 
//  return this; }
//}
//
//
//
//
//class MSheetCo extends MBasic {
//	static class MSheetCo_Builder extends MAbstract_Builder {
//		MSheetCo_Builder() { 
//			super("co", "Sheet Co", "connexions with sheet parent", "Sheet"); }
//		MSheetCo build(Macro_Sheet s, sValueBloc b) { MSheetCo m = new MSheetCo(s, b); return m; }
//	}
//Macro_Element elem;
//nLinkedWidget view;
//sStr val_view, links_save;
//
//MSheetCo(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "co", _bloc); }
//public void init() {
//  val_view = newStr("val", "val", "");
//}
//public void build_param() {
//  view = addEmptyS(0).addLinkedModel("MC_Element_SField");
//  view.setLinkedValue(val_view);
//  val_view.addEventChange(new nRunnable() { public void run() { 
//      if (sheet != mmain()) elem.sheet_connect.setInfo(val_view.get());
//      elem.connect.setInfo(val_view.get());
//  } });
//  elem = addSheetOutput(1, "out");
//  if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
//  elem.connect.setInfo(val_view.get());
//}
//
//public void build_normal() {
//  view = addEmptyS(1).addLinkedModel("MC_Element_SField");
//  view.addEventFieldChange(new nRunnable() { public void run() { 
//    elem.sheet_connect.setInfo(val_view.get());
//  } });
//  view.setLinkedValue(val_view);
//  elem = addSheetInput(0, "in");
//  if (elem.sheet_connect != null) elem.sheet_connect.setInfo(val_view.get());
//}
//
//public MSheetCo clear() {
//  super.clear(); return this; }
//}

