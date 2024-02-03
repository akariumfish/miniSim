package Macro;
import UI.Rect;
import UI.nCtrlWidget;
import UI.nGUI;
import UI.nLinkedWidget;
import UI.nShelfPanel;
import UI.nWatcherWidget;
import UI.nWidget;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import RApplet.sInterface;
import RBase.RConst;
import sData.*;

//
///*
//abstract extend shelfpanel
// can be selected and group dragged copy/pasted > template or deleted
// 
// */
public class Macro_Abstract extends nShelfPanel implements Macro_Interf {
  
  Macro_Abstract deploy() { open(); return this; }
  Macro_Abstract open() {
    if (openning.get() != OPEN) {
      openning.set(OPEN);
      grabber.show(); grab_front.show(); panel.show(); back.hide(); 
      front.show(); title.show(); reduc.show(); 
      reduc.setPosition(-ref_size, ref_size*0.25);
      moving();
    }
    toLayerTop();
    return this;
  }
  Macro_Abstract reduc() {
    if (openning.get() != REDUC) {
      openning.set(REDUC);
      grabber.show(); grab_front.show(); panel.hide(); back.hide(); 
      front.hide(); title.hide(); reduc.show(); 
      reduc.show().setPosition(ref_size * 0.75, ref_size*0.75);
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
    return this;
  }
  Macro_Abstract changeOpenning() {
    if (openning.get() == OPEN) { reduc(); }
    else if (openning.get() == REDUC) { open(); }
    else if (openning.get() == DEPLOY) { open(); }
    return this; }
  
  void szone_select() {
    if (mmain().selected_sheet != sheet) sheet.select();
    mmain().selected_macro.add(this);
    szone_selected = true;
    mmain().update_select_bound();
    if (openning.get() == REDUC) grab_front.setOutline(true);
    else front.setOutline(true);
    toLayerTop();
  }
  void szone_unselect() {//carefull! still need to remove from mmain.selected_macro and run mmain.update_select_bound
    szone_selected = false;
    front.setOutline(false);
    grab_front.setOutline(false);
  }
  
  void moving() { 
    sheet.movingChild(this); 
  }
  void group_move(float x, float y) { 
    grabber.setPY(grabber.getLocalY() + y); grabber.setPX(grabber.getLocalX() + x); }
  Macro_Abstract setPosition(float x, float y) { 
    grab_pos.doEvent(false);
    grabber.setPosition(x, y); grab_pos.set(x, y);
    grab_pos.doEvent(true);
    return this; }
  Macro_Abstract setParent(Macro_Sheet s) { grabber.clearParent(); grabber.setParent(s.grabber); return this; }
  public Macro_Abstract toLayerTop() { 
    super.toLayerTop(); panel.toLayerTop(); title.toLayerTop(); grabber.toLayerTop(); 
    reduc.toLayerTop(); prio_sub.toLayerTop(); prio_add.toLayerTop(); prio_view.toLayerTop(); 
    front.toLayerTop(); grab_front.toLayerTop(); return this; }

  public Macro_Main mmain() { if (sheet == this) return (Macro_Main)this; return sheet.mmain(); }
  
  public nGUI gui;
  Macro_Sheet sheet;    int sheet_depth = 0;
  boolean szone_selected = false, title_fixe = false, unclearable = false, pos_given = false;
  public float ref_size = 40;
  sVec grab_pos; sStr val_type;
  public  sStr val_descr;
sStr val_title;
  sInt priority, openning, openning_pre_hide; sObj val_self;
  float prev_x, prev_y; //for group dragging
  nLinkedWidget grabber, title; nCtrlWidget prio_sub, prio_add; nWatcherWidget prio_view;
  nWidget reduc, front, grab_front, back;
  public sValueBloc value_bloc = null;
  public sValueBloc setting_bloc;
  nRunnable szone_st, szone_en;
  
Macro_Abstract(Macro_Sheet _sheet, String ty, String n, sValueBloc _bloc) {
    super(_sheet.gui, _sheet.ref_size, 0.25F);
    gui = _sheet.gui; ref_size = _sheet.ref_size; sheet = _sheet; 
    sheet_depth = sheet.sheet_depth + 1;
    
    if (_bloc == null) {
      String n_suff = "";
      if (n == null) n_suff = ty;
      else n_suff = n;
      String n_ref = "0_" + n_suff;
      if (sheet == mmain() && mmain().child_sheet != null && mmain().child_sheet.size() == 0 ) {
        
      } else {
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
      }
      value_bloc = sheet.value_bloc.newBloc(n_ref);
      
    } else value_bloc = _bloc;
    
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
    else pos_given = true;
    
    if (!pos_given && sheet != this && !(sheet.child_macro.size() == 0) ) {
      PVector sc_pos = new PVector((float)(mmain().screen_gui.view.pos.x + mmain().screen_gui.view.size.x * 1.0 / 2.0), 
    		  					   (float)(mmain().screen_gui.view.pos.y + mmain().screen_gui.view.size.y / 3.0) );
      sc_pos = mmain().inter.cam.screen_to_cam(sc_pos);
      grab_pos.set(sc_pos.x - sheet.grabber.getX(), sc_pos.y - sheet.grabber.getY());
      grab_pos.setx(grab_pos.x() - grab_pos.x()%(ref_size * 0.5));
      grab_pos.sety(grab_pos.y() - grab_pos.y()%(ref_size * 0.5));
    }
    
    if (openning == null) openning = setting_bloc.newInt("open", "op", OPEN);
    if (openning_pre_hide == null) openning_pre_hide = setting_bloc.newInt("pre_open", "pop", OPEN);
    if (val_self == null) val_self = setting_bloc.newObj("self", this);
    else val_self.set(this);
    if (priority == null) priority = setting_bloc.newInt("priority", "prio", 0);
    build_ui();
  }
  Macro_Abstract(sInterface _int) { // FOR MACRO_MAIN ONLY
    super(_int.cam_gui, _int.ref_size, 0.125F);
    
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
      
    grabber.clearParent().addEventDrag(new nRunnable(this) { public void run() { 
      grabber.setPY(grabber.getLocalY() - grabber.getLocalY()%(ref_size * 0.5));
      grabber.setPX(grabber.getLocalX() - grabber.getLocalX()%(ref_size * 0.5));
      
      if (mmain().selected_macro.contains(((Macro_Abstract)builder)))
        for (Macro_Abstract m : mmain().selected_macro) if (m != ((Macro_Abstract)builder))
          m.group_move(grabber.getLocalX() - prev_x, grabber.getLocalY() - prev_y);
      
      prev_x = grabber.getLocalX(); prev_y = grabber.getLocalY();
      moving(); 
    } });
    grabber.addEventGrab(new nRunnable() { public void run() { 
      prev_x = grabber.getLocalX(); prev_y = grabber.getLocalY(); toLayerTop(); } });
    
    panel.copy(gui.theme.getModel("MC_Panel"));
    panel.setParent(grabber).setPassif();
    panel.setPosition(-grabber.getLocalSX()/4, grabber.getLocalSY()/2 + ref_size * 1 / 8)
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
    
    title = addLinkedModel("MC_Title").setLinkedValue(val_title);
    title.addEventFieldChange(new nRunnable() { public void run() { title.setOutline(true); } });
    title.clearParent().setParent(panel);
    title.alignDown().stackLeft();
    grabber.addEventMouseEnter(new nRunnable() { public void run() { 
      if (openning.get() == REDUC) title.show(); } });
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
    
    mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
      if (openning.get() == REDUC) { openning.set(OPEN); reduc(); }
      else if (openning.get() == OPEN) { openning.set(REDUC); open(); }
      else if (openning.get() == HIDE) { openning.set(openning_pre_hide.get()); hide(); }
      else if (openning.get() == DEPLOY) { openning.set(OPEN); deploy(); }
      if (!pos_given) find_place(); 
      if (openning.get() != HIDE && !mmain().is_paste_loading) { 
        mmain().szone_clear_select();
        szone_select();
      }
      if (!mmain().show_macro.get()) hide();
      
      if (mmain().sheet_explorer != null) mmain().sheet_explorer.update(); 
      runEvents(eventsSetupLoad); 
      toLayerTop(); 
    } } );
  }
  void find_place() {
    
    grabber.setPY(grabber.getLocalY() - grabber.getLocalY()%(ref_size * 0.5));
    grabber.setPX(grabber.getLocalX() - grabber.getLocalX()%(ref_size * 0.5));
    
    int adding_v = 0;
    boolean found = false;
    while (!found) {
      if (adding_v > 0) setPosition(grabber.getLocalX() - ref_size * 3, grabber.getLocalY());
      adding_v++; 
      if (adding_v == 6) { 
        adding_v = 0; setPosition(grabber.getLocalX() + ref_size * 15, grabber.getLocalY() + ref_size * 3); }
      boolean col = false;
      float phf = 0.5F;
      for (Macro_Abstract c : sheet.child_macro) 
        if (c != this && c.openning.get() == DEPLOY 
            && Rect.rectCollide(panel.getRect(), c.back.getRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == REDUC 
                 && Rect.rectCollide(panel.getRect(), c.grabber.getRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == OPEN 
                 && Rect.rectCollide(panel.getRect(), c.panel.getRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == HIDE && c.openning_pre_hide.get() == DEPLOY
                 && Rect.rectCollide(panel.getPhantomRect(), c.back.getPhantomRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == HIDE && c.openning_pre_hide.get() == REDUC
                 && Rect.rectCollide(panel.getPhantomRect(), c.grabber.getPhantomRect(), ref_size * phf)) col = true;
        else if (c != this && c.openning.get() == HIDE && c.openning_pre_hide.get() == OPEN
                 && Rect.rectCollide(panel.getPhantomRect(), c.panel.getPhantomRect(), ref_size * phf)) col = true;
      if (sheet != mmain() && openning.get() == HIDE 
          && Rect.rectCollide(panel.getPhantomRect(), sheet.panel.getPhantomRect(), ref_size * phf*2)) col = true;
      if (sheet != mmain() && openning.get() != HIDE 
          && Rect.rectCollide(panel.getRect(), sheet.panel.getRect(), ref_size * phf*2)) col = true;
      if (!col) found = true;
    }
    sheet.updateBack();
  }
  public Macro_Abstract clear() {
    if (!unclearable) {
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
  
  
  public sBoo newBoo(boolean d, String r, String s) { return newBoo(r, s, d); }
  public sBoo newBoo(boolean d, String r) { return newBoo(r, r, d); }
  public sBoo newBoo(String r, boolean d) { return newBoo(r, r, d); }
  public sInt newInt(int d, String r, String s) { return newInt(r, s, d); }
  public sFlt newFlt(float d, String r, String s) { return newFlt(r, s, d); }
  public sRun newRun(nRunnable d, String r, String s) { return newRun(r, s, d); }
  
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


















    
