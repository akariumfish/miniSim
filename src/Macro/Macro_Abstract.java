package Macro;
import UI.Drawable;
import UI.Rect;
import UI.nCtrlWidget;
import UI.nGUI;
import UI.nLinkedWidget;
import UI.nShelfPanel;
import UI.nWatcherWidget;
import UI.nWidget;
import processing.core.PVector;

import java.util.ArrayList;

import RApplet.RConst;
import RApplet.sInterface;
import sData.*;

//
///*
//abstract extend shelfpanel
// can be selected and group dragged copy/pasted > template or deleted
// 
// */

abstract class MAbstract_Builder implements Macro_Interf {
	boolean visible = true;
	boolean show_in_buildtool = false;
	public void first_start_show(Macro_Main mmain) {
		show_in_buildtool = true;
	    	mmain.shown_builder.set(mmain.shown_builder.get() + OBJ_TOKEN + type);
	}
	String type, descr = ""; 
	String title, category = "";
	MAbstract_Builder(String t) { type = t; title = t; }
	MAbstract_Builder(String t, String d) { type = t; title = t; descr = d; }
	MAbstract_Builder(String t, String tl, String d) { 
		type = t; title = tl; descr = d; }
	MAbstract_Builder(String t, String tl, String d, String c) { 
		type = t; title = tl; descr = d; category = c; }
	Macro_Abstract build(Macro_Sheet s, sValueBloc b) { return null; }
}

public class Macro_Abstract extends nShelfPanel implements Macro_Interf {

	  void moving() { 
		  if (sheet != this) sheet.movingChild(this); 
	  }
	  void group_move(float x, float y) { 
	    grabber.setPY(grabber.getLocalY() + y); grabber.setPX(grabber.getLocalX() + x);
	    group_move_custom(x, y); }
	  protected void group_move_custom(float x, float y) {}
	  Macro_Abstract setPosition(double x, double y) { return setPosition((float)x, (float)y); }
	  Macro_Abstract setPosition(float x, float y) { 
//	    grab_pos.doEvent(false);
	    grabber.setPosition(x, y); grab_pos.set(x, y);
//	    grab_pos.doEvent(true);
	    return this; }
	  Macro_Abstract setParent(Macro_Sheet s) { grabber.clearParent(); grabber.setParent(s.grabber); return this; }
	  public Macro_Abstract toLayerTop() { 
		  if (!gui.app.DEBUG_NOTOLAYTOP) super.toLayerTop(); 
		  panel.toLayerTop();  
		  front.toLayerTop(); grab_front.toLayerTop(); grabber.toLayerTop(); 
		  if (!hide_ctrl) {
			  title.toLayerTop(); reduc.toLayerTop(); 
			  prio_sub.toLayerTop(); prio_add.toLayerTop(); prio_view.toLayerTop(); }
		  
	    return this; }
//	  public Macro_Abstract buildLayer() { 
//		  gui.app.logln(value_bloc.ref+ " build layer");
//		  panel.getDrawable().clear_coDrawer();
//		  buildLayerTo(panel.getDrawable()); 
//	  return this; }
//	  public Macro_Abstract buildLayerTo(Drawable d) { 
//		  gui.app.logln(value_bloc.ref+ " build layer TO");
//		    d.add_coDrawer(title.getDrawable()); 
//		    d.add_coDrawer(grabber.getDrawable()); 
//		    d.add_coDrawer(reduc.getDrawable()); 
//		    d.add_coDrawer(prio_sub.getDrawable()); 
//		    d.add_coDrawer(prio_add.getDrawable()); 
//		    d.add_coDrawer(prio_view.getDrawable()); 
//		    d.add_coDrawer(front.getDrawable()); 
//		    d.add_coDrawer(grab_front.getDrawable()); 
//	  return this; }
	  public Macro_Main mmain() { if (sheet == this) return (Macro_Main)this; return sheet.mmain(); }

	  void switch_select() {
		  if (szone_selected ) {
			  szone_unselect();

			  mmain().selected_macro.remove(this);
			  mmain().update_select_bound();
			  
		  } else szone_select();
	  }
	  void szone_select() {
		  if (!szone_selected) {
		    if (mmain().selected_sheet != sheet) sheet.select();
		    mmain().selected_macro.add(this);
		    szone_selected = true;
		    mmain().update_select_bound();
		    if (openning.get() == REDUC) grab_front.setOutline(true);
		    else front.setOutline(true);
		    title_fixe = true;
		    if (!hide_ctrl) title.show();
		    toLayerTop();
		  }
	  }
	  void szone_unselect() {//carefull! still need to remove from mmain.selected_macro and run mmain.update_select_bound
	    szone_selected = false;
	    front.setOutline(false);
	    grab_front.setOutline(false);
	    if (openning.get() == REDUC || openning.get() == HIDE) {
	    		title_fixe = false;
	    		if (!grabber.isHovered()) title.hide();
	    }
	  }
  Macro_Abstract deploy() { open(); return this; } //
  Macro_Abstract open() {
    if (openning.get() != OPEN) {
      openning.set(OPEN);
      grabber.show(); grab_front.show(); panel.show(); back.hide(); 
      front.show(); 
      if (!hide_ctrl) { title.show(); reduc.show(); 
      	prio_sub.show(); prio_add.show(); prio_view.show(); }
      else { 
    	  	title.hide(); prio_sub.hide(); prio_add.hide(); prio_view.hide(); 
    	  	reduc.show(); //grabber.hide(); 
    	  }
      reduc.setPosition(-ref_size * 0.75, ref_size*0.5)
	    .setSize(ref_size*0.4, ref_size*0.75);
      moving();
    }
    toLayerTop();
    return this;
  }
  Macro_Abstract reduc() {
    if (openning.get() != REDUC) {
      openning.set(REDUC);
      grabber.show(); grab_front.show(); panel.hide(); back.hide(); 
      front.hide(); reduc.show();
      if (!hide_ctrl) { 
	      if (!title_fixe) title.hide(); else title.show(); 
      } else { 
    	  	title.hide(); 
    	  }
      prio_sub.hide(); prio_add.hide(); prio_view.hide(); 
      reduc.setPosition(ref_size * 0.5, ref_size*0.5)
	    .setSize(ref_size*0.4, ref_size*0.5);
      moving();
    }
    return this;
  }
  Macro_Abstract show() {
    if (openning.get() == HIDE) { 
      if (openning_pre_hide.get() == REDUC) reduc();
      else if (openning_pre_hide.get() == OPEN) open();
      else if (openning_pre_hide.get() == DEPLOY) deploy();
      //else reduc();
    }
    return this;
  }
  Macro_Abstract hide() {
    if (openning.get() != HIDE) {
      openning_pre_hide.set(openning.get());
      openning.set(HIDE);
    }
    grabber.hide(); grab_front.hide(); panel.hide(); back.hide(); 
    front.hide(); title.hide(); reduc.hide(); 
    prio_sub.hide(); prio_add.hide(); prio_view.hide(); 
    return this;
  }
  Macro_Abstract changeOpenning() {
	    if (openning.get() == OPEN) { reduc(); }
	    else if (openning.get() == REDUC) { open(); }
	    else if (openning.get() == DEPLOY) { open(); }
	    return this; }
  Macro_Abstract hideCtrl() {
	  hide_ctrl = true;
	  title.hide(); prio_sub.hide(); prio_add.hide(); prio_view.hide(); 
	  //grabber.hide();//reduc.hide(); 
    return this; }
  

  
  public nGUI gui;
  Macro_Sheet sheet;    int sheet_depth = 0;
  boolean szone_selected = false, title_fixe = false;
public boolean unclearable = false, hide_ctrl = false;
boolean loading_from_bloc = false;
  public float ref_size = 40;
  sVec grab_pos; public sStr val_type;
  public  sStr val_descr;
sStr val_title;
  public sInt priority;
sInt openning;
sInt openning_pre_hide; sObj val_self;
  float prev_x, prev_y; //for group dragging
  public nLinkedWidget grabber;
nLinkedWidget title; nCtrlWidget prio_sub, prio_add; nWatcherWidget prio_view;
  nWidget reduc, front, grab_front, back;
  public sValueBloc value_bloc = null;
  public sValueBloc setting_bloc;
  nRunnable szone_st, szone_en;
  String short_title = "";
  
  void up_short_title() {
	  short_title = val_title.get();
	  for (int i = 0 ; i < short_title.length() ; i ++) {
		  if (short_title.charAt(i) == '_') {
			  String n = short_title.substring(0, i);
			  if (RConst.testParseInt(n))
				  short_title = short_title.substring(i+1, short_title.length());
			  break;
		  }
	  }
  }
  
Macro_Abstract(Macro_Sheet _sheet, String ty, String n, sValueBloc _bloc) {
    super(_sheet.gui, _sheet.ref_size, 0.0F);
    gui = _sheet.gui; ref_size = _sheet.ref_size; sheet = _sheet; 
    sheet_depth = sheet.sheet_depth + 1;
    
    if (_bloc == null) {
      String n_suff = "";
      if (n == null || n.equals("")) n_suff = ty;
      else n_suff = n;
      String n_ref = "0_" + n_suff;
//      if (sheet == mmain() && mmain().child_sheet != null && mmain().child_sheet.size() == 0 ) {
//        
//      } else {
        int cn = -1;
        n_ref = cn + "_" + n_suff;
        //mlogln(n_suff + " search new name");
        boolean is_in_other_sheet = true;
        while (is_in_other_sheet ||
               (sheet.sheet.value_bloc.getBloc(n_ref) != null) || // && sheet != mmain()
               sheet.value_bloc.getBloc(n_ref) != null) {
          cn++;
          n_ref = cn + "_" + n_suff;
          //mlogln("try name " + n_ref);
          
          is_in_other_sheet = false;
          if (sheet.child_sheet != null) for (Macro_Sheet m : sheet.child_sheet)
            is_in_other_sheet = m.value_bloc.getBloc(n_ref) != null || is_in_other_sheet;
          
          /*if (sheet != mmain())*/ 
          if (sheet.sheet.child_sheet != null) for (Macro_Sheet m : sheet.sheet.child_sheet) {
            is_in_other_sheet = m.value_bloc.getBloc(n_ref) != null || is_in_other_sheet;
            for (Macro_Sheet mc : m.child_sheet)
              is_in_other_sheet = mc.value_bloc.getBloc(n_ref) != null || is_in_other_sheet;
          }
          
        }
        //mlogln(n_ref + " found his name");
//      }
      value_bloc = sheet.value_bloc.newBloc(n_ref);
      
    } else {
	    	loading_from_bloc = true;
	    	value_bloc = _bloc;
    }
//    mlogln("build abstract "+ty+" "+n+" "+ _bloc+"    as "+value_bloc.ref);
    
    setting_bloc = value_bloc.getBloc("settings");
    if (setting_bloc == null) setting_bloc = value_bloc.newBloc("settings");
    
    val_type = ((sStr)(setting_bloc.getValue("type"))); 
    val_descr = ((sStr)(setting_bloc.getValue("description"))); 
    val_title = ((sStr)(setting_bloc.getValue("title"))); 
    grab_pos = ((sVec)(setting_bloc.getValue("position"))); 
    openning = ((sInt)(setting_bloc.getValue("open"))); 
    openning_pre_hide = ((sInt)(setting_bloc.getValue("pre_open"))); 
    val_self = ((sObj)(setting_bloc.getValue("self"))); 
    priority = ((sInt)(setting_bloc.getValue("priority"))); 
    
    if (val_type == null) val_type = setting_bloc.newStr("type", "type", ty);
    if (val_descr == null) val_descr = setting_bloc.newStr("description", "descr", "macro");
    //if (val_title == null) val_title = setting_bloc.newStr("title", "ttl", n);
    if (val_title == null) val_title = setting_bloc.newStr("title", "ttl", value_bloc.ref);
    else val_title.set(value_bloc.ref);
    if (grab_pos == null) grab_pos = setting_bloc.newVec("position", "pos");
//    else pos_given = true;
    
//    if (!pos_given && sheet != this && !(sheet.child_macro.size() == 0) ) {
//      PVector sc_pos = new PVector((float)(mmain().screen_gui.view.pos.x + mmain().screen_gui.view.size.x * 1.0 / 2.0), 
//    		  					   (float)(mmain().screen_gui.view.pos.y + mmain().screen_gui.view.size.y / 3.0) );
//      sc_pos = mmain().inter.cam.screen_to_cam(sc_pos);
//      grab_pos.set(sc_pos.x - sheet.grabber.getX(), sc_pos.y - sheet.grabber.getY());
//      grab_pos.setx(grab_pos.x() - grab_pos.x()%(ref_size * GRID_SNAP_FACT));
//      grab_pos.sety(grab_pos.y() - grab_pos.y()%(ref_size * GRID_SNAP_FACT));
//    }
    
    if (openning == null) openning = setting_bloc.newInt("open", "op", OPEN);
    if (openning_pre_hide == null) openning_pre_hide = setting_bloc.newInt("pre_open", "pop", OPEN);
    if (val_self == null) val_self = setting_bloc.newObj("self", this);
    else val_self.set(this);
    if (priority == null) priority = setting_bloc.newInt("priority", "prio", 0);
    priority.set_limit(0, 9);
    up_short_title();
    build_ui();
  }
  Macro_Abstract(sInterface _int) { // FOR MACRO_MAIN ONLY
    super(_int.cam_gui, _int.ref_size, 0.0F);
    
//    mlogln("build main abstract ");
    
    gui = _int.cam_gui; 
    ref_size = _int.ref_size; 
    sheet = (Macro_Main)this;
    Macro_Main.myTheme_MACRO(gui.theme, ref_size); 
    panel.copy(gui.theme.getModel("mc_ref"));
    grabber = addLinkedModel("mc_ref");
    grabber.clearParent();
    reduc = addModel("mc_ref");
    panel.hide(); 
    grabber.setSize(0, 0).setPassif().setOutline(false);
    front = addModel("mc_ref");
    title = addLinkedModel("mc_ref");
    back = addModel("mc_ref");
    grab_front = addModel("mc_ref");
    prio_view = addWatcherModel("mc_ref");
    prio_sub = addCtrlModel("mc_ref");
    prio_add = addCtrlModel("mc_ref");
    
    value_bloc = _int.interface_bloc.newBloc("Main_Sheet");
    setting_bloc = value_bloc.newBloc("settings");
    val_type = setting_bloc.newStr("type", "type", "main");
    val_descr = setting_bloc.newStr("description", "descr", "macro main");
    val_title = setting_bloc.newStr("title", "ttl", "macro main");
    grab_pos = setting_bloc.newVec("position", "pos");
    openning = setting_bloc.newInt("open", "op", DEPLOY);
    openning_pre_hide = setting_bloc.newInt("pre_open", "pop", DEPLOY);
    val_self = setting_bloc.newObj("self", this);
    priority = setting_bloc.newInt("priority", "prio", 0);
    priority.set_limit(0, 9);
    //_int.addEventNextFrame(new Runnable() { public void run() { 
    //  openning.set(OPEN); 
    //  deploy();
    //  //if (!mmain().show_macro.get()) hide();
    //  toLayerTop(); 
    //} } );
  }
  void build_ui() {
    grabber = addLinkedModel("MC_Grabber")
      .setLinkedValue(grab_pos);

	grabber.totalhide(); 
      
    grabber.clearParent().addEventDrag(new nRunnable(this) { public void run() { 
      grabber.setPY(grabber.getLocalY() - grabber.getLocalY()%(ref_size * GRID_SNAP_FACT));
      grabber.setPX(grabber.getLocalX() - grabber.getLocalX()%(ref_size * GRID_SNAP_FACT));
      
      if (mmain().selected_macro.contains(((Macro_Abstract)builder)))
        for (Macro_Abstract m : mmain().selected_macro) if (m != ((Macro_Abstract)builder))
          m.group_move(grabber.getLocalX() - prev_x, grabber.getLocalY() - prev_y);
      
      prev_x = grabber.getLocalX(); prev_y = grabber.getLocalY();
      moving(); 
    } });
    grabber.addEventGrab(new nRunnable() { public void run() { 
      prev_x = grabber.getLocalX(); prev_y = grabber.getLocalY(); toLayerTop(); } })
    .addEventTriggerRight(new nRunnable() { public void run() { 
    		if (mmain().selected_sheet == sheet) switch_select(); } });
    panel.copy(gui.theme.getModel("MC_Panel"));
    panel.setParent(grabber).setPassif();
    panel
    		.setPosition(ref_size * 0.06250, ref_size * 0.50)//grabber.getLocalSY()/2)//-grabber.getLocalSX()/4
      .addEventShapeChange(new nRunnable() { public void run() {
        front.setSize(panel.getLocalSX(), panel.getLocalSY()); } } )
      .addEventVisibilityChange(new nRunnable() { public void run() {
      if (panel.isHided()) front.setSize(0, 0);
      else front.setSize(panel.getLocalSX(), panel.getLocalSY()); } } );
    
    back = addModel("MC_Sheet_Soft_Back");
    back.clearParent().setPassif();
    back.setParent(grabber).hide();
    
    reduc = addModel("MC_Reduc").clearParent();
    reduc.setParent(panel);
    reduc.alignDown().stackRight().addEventTrigger(new nRunnable() { public void run() { changeOpenning(); } });
    
    
    prio_sub = addCtrlModel("MC_Prio_Sub")
      .setRunnable(new nRunnable() { public void run() { if (priority.get() > 0) priority.add(-1); } });
    prio_sub.setParent(panel);
    prio_sub.stackUp().alignRight();
    prio_add = addCtrlModel("MC_Prio_Add")
      .setRunnable(new nRunnable() { public void run() { if (priority.get() < 9) priority.add(1); } });
    prio_add.setParent(panel);
    prio_add.stackUp().alignRight();
    
    prio_view = addWatcherModel("MC_Prio_View")
      .setLinkedValue(priority);
    prio_view.setParent(panel);
    prio_view.stackUp().alignRight().setInfo("priority");
    priority.addEventChange(new nRunnable() { public void run() { sheet.toLayerTop(); }});

    title = addLinkedModel("MC_Title").setLinkedValue(val_title);
    title.addEventFieldChange(new nRunnable() { public void run() { 
        up_short_title(); title.setOutline(true); } });
    title.clearParent().setParent(panel);
    title.alignDown().stackRight();
    title.setSX(ref_size*(0.5F + 0.3F*val_title.get().length()) )
    		.setPosition(-ref_size*1.75 - title.getLocalSX(), ref_size*0.5);
    nRunnable title_run = new nRunnable() { public void run() { 
        title.setSX(ref_size*(0.5F + 0.3F*val_title.get().length()) );
        if (openning.get() == REDUC)
      	  	title.setPosition(ref_size*0.25 - title.getLocalSX(), ref_size*0.25); 
        else title.setPosition(-ref_size*1.75 - title.getLocalSX(), ref_size*0.5); 
//        for (Macro_Element m : sheet.child_elements) {
//			m.bloc.up_short_title();
//			if (m.spot != null) m.spot.setText(m.bloc.value_bloc.base_ref);
//		}
    } };
//    val_title.addEventChange(new nRunnable() { public void run() { 
//		
//    } });
    val_title.addEventChange(title_run);
    openning.addEventChange(title_run);
    
    grabber.addEventMouseEnter(new nRunnable() { public void run() { 
      if (openning.get() == REDUC && !hide_ctrl) title.show(); } });
    grabber.addEventMouseLeave(new nRunnable() { public void run() { 
      if (openning.get() == REDUC && !title_fixe) title.hide(); } });
    
    front = addModel("MC_Front")
      .setParent(panel).setPassif()
      .addEventFrame(new nRunnable() { public void run() { 
        if (openning.get() != REDUC && mmain().szone.isSelecting() && mmain().selected_sheet == sheet ) {
          if (mmain().szone.isUnder(front)) front.setOutline(true);
          else front.setOutline(false); } } } )
      ;
    grab_front = addModel("MC_Front")
      .setParent(grabber).setPassif()
      .setSize(grabber.getLocalSX(), grabber.getLocalSY())
      .addEventFrame(new nRunnable() { public void run() { 
        if (openning.get() == REDUC && mmain().szone.isSelecting() && mmain().selected_sheet == sheet ) {
          if (mmain().szone.isUnder(grab_front)) grab_front.setOutline(true);
          else grab_front.setOutline(false); } } } )
      ;
    szone_st = new nRunnable() { public void run() { 
      szone_unselect(); } } ;
    szone_en = new nRunnable(this) { public void run() { 
      if (mmain().selected_sheet == sheet && 
          ((openning.get() != REDUC && mmain().szone.isUnder(front) ) || 
           (openning.get() == REDUC && mmain().szone.isUnder(grab_front) )) )  {
        szone_select(); } } } ;
    if (mmain() != this) {
      mmain().szone.addEventStartSelect(szone_st);
      mmain().szone.addEventEndSelect(szone_en);
    }
    
    setParent(sheet); 
    sheet.child_macro.add(this); 
    

//    this.buildLayer();
    
  }
  void init_end() {
	  if (openning.get() == REDUC) { openning.set(OPEN); reduc(); }
      else if (openning.get() == OPEN) { openning.set(REDUC); open(); }
      else if (openning.get() == HIDE) { openning.set(openning_pre_hide.get()); hide(); }
      else if (openning.get() == DEPLOY) { openning.set(OPEN); deploy(); }
      if (!loading_from_bloc) find_place(panel); 
      if (!is_cleared && openning.get() != HIDE && !mmain().is_paste_loading) { 
        mmain().szone_clear_select();
        szone_select();
      }
      if (!mmain().show_macro.get()) hide();
      
      if (mmain().sheet_explorer != null) mmain().sheet_explorer.update(); 
      nRunnable.runEvents(eventsSetupLoad); 
      toLayerTop(); 
  }
  void find_place(nWidget collide_cible) {
	  
	PVector sc_pos = new PVector(mmain().screen_gui.view.pos.x + mmain().screen_gui.view.size.x * 1.0F / 2.0F, 
								mmain().screen_gui.view.pos.y + mmain().screen_gui.view.size.y / 2.0F);
	sc_pos = mmain().inter.cam.screen_to_cam(sc_pos);
	grabber.setPosition(sc_pos.x - sheet.grabber.getX(), sc_pos.y - sheet.grabber.getY());
	grabber.setPY(grabber.getLocalY() - grabber.getLocalY()%(ref_size * GRID_SNAP_FACT));
	grabber.setPX(grabber.getLocalX() - grabber.getLocalX()%(ref_size * GRID_SNAP_FACT));

	int adding_dir_x = 0;
	int adding_dir_y = 1;
	int adding_side_l = 1;
	int adding_side_cnt = 0;
    int adding_count = 0;
    float phf = 0.75F;
//    float move_fct = 3 * GRID_SNAP_FACT;
    float move_fct = Math.max(collide_cible.getPhantomRect().size.x, collide_cible.getPhantomRect().size.y);
    move_fct /= ref_size;
    move_fct = move_fct - move_fct%(GRID_SNAP_FACT*3) + 6*GRID_SNAP_FACT;
    
    boolean found = false;
    while (!found) {
      boolean col = false;
      for (Macro_Abstract c : sheet.child_macro) 
        if (c != this && c.openning.get() == DEPLOY 
            && Rect.rectCollide(collide_cible.getRect(), c.back.getRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == REDUC 
                 && Rect.rectCollide(collide_cible.getRect(), c.grabber.getRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == OPEN 
                 && Rect.rectCollide(collide_cible.getRect(), c.panel.getRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == HIDE && c.openning_pre_hide.get() == DEPLOY
                 && Rect.rectCollide(collide_cible.getPhantomRect(), c.back.getPhantomRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == HIDE && c.openning_pre_hide.get() == REDUC
                 && Rect.rectCollide(collide_cible.getPhantomRect(), c.grabber.getPhantomRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == HIDE && c.openning_pre_hide.get() == OPEN
                 && Rect.rectCollide(collide_cible.getPhantomRect(), c.panel.getPhantomRect(), ref_size * phf)) col = true;
      if (sheet != mmain() && openning.get() == HIDE 
          && Rect.rectCollide(collide_cible.getPhantomRect(), sheet.panel.getPhantomRect(), ref_size * phf*2)) col = true;
      if (sheet != mmain() && openning.get() != HIDE 
          && Rect.rectCollide(collide_cible.getRect(), sheet.panel.getRect(), ref_size * phf*2)) col = true;
      if (!col) found = true;
      else {
    	  	setPosition(grabber.getLocalX() + ref_size * adding_dir_x * move_fct, 
    	  				grabber.getLocalY() + ref_size * adding_dir_y * move_fct);
    	  	adding_count++;
    	  	if (adding_count >= adding_side_l) {
    	  		adding_count = 0;
    	  		if (adding_dir_x == 0) { adding_dir_x = adding_dir_y * -1; adding_dir_y = 0; }
    	  		else { adding_dir_y = adding_dir_x * -1; adding_dir_x = 0; }
    	  		adding_side_cnt++;
    	  		if (adding_side_cnt >= 2) {
    	  			adding_side_cnt = 0;
    	  			adding_side_l++;
    	  			adding_dir_x *= -1;
    	  			adding_dir_y *= -1;
    	  		}
    	  	}
      }
    }
    sheet.updateBack();
  }
  protected boolean is_cleared = false;
  public Macro_Abstract clear() {
    if (!unclearable) {
    	  is_cleared = true;
    	  szone_unselect();
//    	mmain().selected_macro.remove(this);
      super.clear();
      
      val_type.clear(); val_descr.clear(); val_title.clear(); grab_pos.clear();
      openning.clear(); openning_pre_hide.clear(); val_self.clear();
      priority.clear();
      value_bloc.clear(); 
      sheet.child_macro.remove(this);
      sheet.redo_link();
      sheet.redo_spot();
      sheet.updateBack();
      if (mmain() != this) {
        mmain().szone.removeEventStartSelect(szone_st);
        mmain().szone.removeEventEndSelect(szone_en);
      }
    }
    return this;
  }
  
  String resum_link() { return ""; }
  String resum_spot(String spots) { return spots; }
  
  

  public sFlt newFlt(float d, String r, String s, float mi, float ma) {
	    sFlt v = ((sFlt)(value_bloc.getValue(r))); 
	    if (v == null) v = value_bloc.newFlt(r, s, d).set_limit(mi,ma);
	    return v; }
  public sInt newInt(int d, String r, String s, int mi, int ma) {
	    sInt v = ((sInt)(value_bloc.getValue(r))); 
	    if (v == null) v = value_bloc.newInt(r, s, d).set_limit(mi,ma);
	    return v; }
  
  public sBoo newBoo(boolean d, String r, String s) { return newBoo(r, s, d); }
  public sBoo newBoo(boolean d, String r) { return newBoo(r, r, d); }
  public sBoo newBoo(String r, boolean d) { return newBoo(r, r, d); }
  public sInt newInt(int d, String r, String s) { return newInt(r, s, d); }
  public sInt newInt(int d, String r) { return newInt(r, r, d); }
  public sFlt newFlt(float d, String r, String s) { return newFlt(r, s, d); }
  public sFlt newFlt(float d, String r) { return newFlt(r, r, d); }
  public sCol newCol(int d, String r, String s) { return newCol(r, s, d); }
  public sCol newCol(int d, String r) { return newCol(r, r, d); }
  public sCol newCol(String r) { return newCol(r, r, 0); }
  public sVec newVec(String r) { return newVec(r, r); }
  public sStr newStr(String r, String d) { return newStr(r, r, d); }
  public sStr newStr(String r) { return newStr(r, r, ""); }
  public sRun newRun(nRunnable d, String r, String s) { return newRun(r, s, d); }
  public sRun newRun(nRunnable d, String r) { return newRun(r, r, d); }
  public sRun newRun(String r, nRunnable d) { return newRun(r, r, d); }
  public sRun newRun(String r) { return newRun(r, r, null); }
  public sObj newObj(String r) { return newObj(r, r); }

  public sBoo newBoo(String r, String s, boolean d, char ct) {
    sBoo v = ((sBoo)(value_bloc.getValue(r))); 
    if (v == null) v = value_bloc.newBoo(r, s, d, ct);
    return v; }
  public sBoo newBoo(String r, String s, boolean d) {
	    sBoo v = ((sBoo)(value_bloc.getValue(r))); 
	    if (v == null) v = value_bloc.newBoo(r, s, d);
	    return v; }
  public sInt newInt(String r, String s, int d) {
    sInt v = ((sInt)(value_bloc.getValue(r))); 
    if (v == null) v = value_bloc.newInt(r, s, d);
    return v; }
  public sFlt newFlt(String r, String s, float d) {
	    sFlt v = ((sFlt)(value_bloc.getValue(r))); 
	    if (v == null) v = value_bloc.newFlt(r, s, d);
	    return v; }
  public sFlt newFlt(String r, String s, double d) {
	    return newFlt(r, s, (float)d); }
  public sStr newStr(String r, String s, String d) {
    sStr v = ((sStr)(value_bloc.getValue(r))); 
    if (v == null) v = value_bloc.newStr(r, s, d);
    return v; }
  public sVec newVec(String r, String s) {
    sVec v = ((sVec)(value_bloc.getValue(r))); 
    if (v == null) v = value_bloc.newVec(r, s);
    return v; }
  public sCol newCol(String r, String s, int d) {
    sCol v = ((sCol)(value_bloc.getValue(r))); 
    if (v == null) v = value_bloc.newCol(r, s, d);
    return v; }
  public sRun newRun(String r, String s, nRunnable d) {
	    sRun v = ((sRun)(value_bloc.getValue(r))); 
	    if (v == null) v = value_bloc.newRun(r, s, d);
	    else v.set(d);
	    return v; }
  public sRun newRun(String r, String s, char ct, nRunnable d) {
	    sRun v = ((sRun)(value_bloc.getValue(r))); 
	    if (v == null) v = value_bloc.newRun(r, s, ct, d);
	    else v.set(d);
	    return v; }
  public sObj newObj(String r, String s) {
    sObj v = ((sObj)(value_bloc.getValue(r))); 
    if (v == null) v = value_bloc.newObj(r, s);
    return v; }
    
  
  public Macro_Abstract addEventSetupLoad(nRunnable r) { eventsSetupLoad.add(r); return this; }
  ArrayList<nRunnable> eventsSetupLoad = new ArrayList<nRunnable>();
  
  boolean canSetupFrom(sValueBloc bloc) {
    boolean b = (bloc != null && bloc.getBloc("settings") != null && 
    //        values_found(setting_bloc, bloc.getBloc("settings")) && 
    //        values_found(value_bloc, bloc) && 
            ((sStr)bloc.getBloc("settings").getValue("type")).get().equals(val_type.get()));
    //if (b) log("t"); else log("f");
    return b;
    //return true;
  }
  
  void setupFromBloc(sValueBloc bloc) {
    if (canSetupFrom(bloc)) {
      
      mmain().inter.data.transfer_bloc_values(bloc, value_bloc);
      mmain().inter.data.transfer_bloc_values(bloc.getBloc("settings"), setting_bloc);
      nRunnable.runEvents(eventsSetupLoad);
    }
  }
}


















    
