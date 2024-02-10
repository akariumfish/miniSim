package Macro;

import UI.*;
import processing.core.PConstants;
import sData.*;










class MPoint extends Macro_Bloc { 
  static class MPoint_Builder extends MAbstract_Builder {
	  MPoint_Builder() { super("point", "connection node"); show_in_buildtool = true; }
      MPoint build(Macro_Sheet s, sValueBloc b) { MPoint m = new MPoint(s, b); return m; }
    }
  Macro_Connexion in, out; 
  MPoint(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "point", "connection node", _bloc); 
    in = addInputBang(0, "in", new nRunnable() {public void run() { 
    		if (in.lastPack() != null) out.send(in.lastPack()); }});
    out = addOutput(0, "out");
  }
  public MPoint clear() {
    super.clear(); 
    return this; }
  public MPoint toLayerTop() {
    super.toLayerTop(); 
    return this; }
}









class MMcomp extends Macro_Bloc { 
  static class MMcomp_Builder extends MAbstract_Builder {
	  MMcomp_Builder() { super("minicomp", "minicomp"); show_in_buildtool = true; }
	  MMcomp build(Macro_Sheet s, sValueBloc b) { MMcomp m = new MMcomp(s, b); return m; }
    }
  sFlt val1, val2;
  Macro_Connexion v1_in, v2_in, out;
  MMcomp(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "minicomp", "minicomp", _bloc); 
    val1 = newFlt("val1", "val1", 0);
    val2 = newFlt("val2", "val2", 0);
    out = addOutput(1, "out");
    v1_in = addInputFloat(0, "v1", new nRunnable() {public void run() { 
    		val1.set(v1_in.lastPack().asFloat()); }});
    v2_in = addInputFloat(0, "v2", new nRunnable() {public void run() { 
    		val2.set(v2_in.lastPack().asFloat()); }});
    addInputBang(1, "try v1 = v2", new nRunnable() {public void run() { 
		out.send(Macro_Packet.newPacketBool(val1.get() == val2.get())); }});
    addInputBang(1, "try v1 > v2", new nRunnable() {public void run() { 
		out.send(Macro_Packet.newPacketBool(val1.get() > val2.get())); }});
    addInputBang(0, "try v1 < v2", new nRunnable() {public void run() { 
		out.send(Macro_Packet.newPacketBool(val1.get() < val2.get())); }});
  }
  public MMcomp clear() {
    super.clear(); 
    return this; }
  public MMcomp toLayerTop() {
    super.toLayerTop(); 
    return this; }
}


class MCount extends Macro_Bloc { 
  static class MCount_Builder extends MAbstract_Builder {
	  MCount_Builder() { super("count", "count"); show_in_buildtool = true; }
      MCount build(Macro_Sheet s, sValueBloc b) { MCount m = new MCount(s, b); return m; }
    }
  sInt count_val;
  Macro_Connexion add_in, sub_in, rst_in, out;
  MCount(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "count", "count", _bloc); 
    count_val = newInt("count_val", "count_val", 0);
    out = addOutput(1, "out");
    add_in = addInputBang(0, "add", new nRunnable() {public void run() { 
    		count_val.add(1); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }});
    sub_in = addInputBang(0, "sub", new nRunnable() {public void run() { 
    		count_val.add(-1); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }});
    rst_in = addInputBang(0, "rst", new nRunnable() {public void run() { 
    		count_val.set(0); out.send(Macro_Packet.newPacketFloat((float)count_val.get())); }});
  }
  public MCount clear() {
    super.clear(); 
    return this; }
  public MCount toLayerTop() {
    super.toLayerTop(); 
    return this; }
}







class MComment extends MBasic { 
  Macro_Connexion in;
  
  Macro_Element elem_param;
  
  sStr val_com;
  sFlt w_f, h_f;
  
  nLinkedWidget com_field;
  nCtrlWidget w_add, w_sub, h_add, h_sub;
  
  MComment(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "com", _bloc); }
  void init() {
    val_com = newStr("val_com", "val_com", "");
      w_f = newFlt("w_f", "w_f", 3.875F);
      h_f = newFlt("h_f", "h_f", 0.875F);
  }
  void build_param() {

      in = addInput(0, "in").addEventReceive(new nRunnable() { public void run() { 
          if (in.lastPack() != null) {
            if (in.lastPack().isBang()) gui.app.logln(val_com.get());
            else gui.app.logln(val_com.get() + " : " + in.lastPack().getText());
          }
      } });
      
      elem_param = addEmptyL(1);
      w_sub = elem_param.addCtrlModel("MC_Element_Button_Selector_1", "W-")
        .setRunnable(new nRunnable() { public void run() { 
          if (w_f.get() > 3.875) {
        w_f.set(3.875);
        rebuild();
        return;
      }
            if (w_f.get() >   1.375) {
              w_f.set(1.375);
              rebuild();
            return;
              }
        } });
      w_add = elem_param.addCtrlModel("MC_Element_Button_Selector_2", "W+")
        .setRunnable(new nRunnable() { public void run() { 
          if (w_f.get() < 3.875) {
              w_f.set(3.875);
              rebuild();
              return;
          } 
          if (w_f.get() < 5.75) {
            w_f.set(5.75);
            rebuild();
          return;
          }
        } });
      h_sub = elem_param.addCtrlModel("MC_Element_Button_Selector_3", "H-")
        .setRunnable(new nRunnable() { public void run() { 
        if (h_f.get() > 1.125) {
          h_f.add(-1.125);
          rebuild();
        }
      } });
      h_add = elem_param.addCtrlModel("MC_Element_Button_Selector_4", "H+")
        .setRunnable(new nRunnable() { public void run() { 
        if (h_f.get() < 5) {
          h_f.add(1.125);
          rebuild();
        }
      } });
      w_sub.setFont((int)(ref_size/2.8)); w_add.setFont((int)(ref_size/2.8));
      h_sub.setFont((int)(ref_size/2.8)); h_add.setFont((int)(ref_size/2.8));
  }
  void build_normal() {
    addEmptyS(1);
      if (h_f.get() > 1) addEmptyS(1);
      if (h_f.get() > 2) addEmptyS(1);
      if (h_f.get() > 4) addEmptyS(1);
      if (h_f.get() > 5) addEmptyS(1);
    elem_com = addEmptyS(0);
      com_field = elem_com.addLinkedModel("MC_Element_Comment_Field").setLinkedValue(val_com);
      com_field.setSize(ref_size * w_f.get(), ref_size * h_f.get());
  }
  public MComment clear() {
    super.clear(); 
    return this; }
  public MComment toLayerTop() {
    super.toLayerTop(); 
    return this; }
}







class MSheetObj extends Macro_Bloc { 
  static class MSheetObj_Builder extends MAbstract_Builder {
      MSheetObj_Builder() { super("sheet obj", "send his sheet as packet"); show_in_buildtool = true; }
      MSheetObj build(Macro_Sheet s, sValueBloc b) { MSheetObj m = new MSheetObj(s, b); return m; }
    }
  nRunnable get_run;
  Macro_Connexion in, out;
  sBoo setup_send;
  MSheetObj(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "sheet obj", "sheet obj", _bloc); 
    setup_send = newBoo("stp_snd", "stp_snd", true);
    get_run = new nRunnable() { public void run() {
      out.send(Macro_Packet.newPacketSheet(sheet));
    }};
    in = addInputBang(0, "get", get_run);
//    addEmptyS(0);
    out = addOutput(1, "out");
    addTrigSwtchS(0, "st", setup_send, "get", get_run);
    if (setup_send.get()) get_run.run();
  }
  public MSheetObj clear() {
    super.clear(); 
    return this; }
  public MSheetObj toLayerTop() {
    super.toLayerTop(); 
    return this; }
}



class MSheetView extends Macro_Bloc { 
  static class MSheetView_Builder extends MAbstract_Builder {
      MSheetView_Builder() { super("sheet view", "set the camera on his sheet"); show_in_buildtool = true; }
      MSheetView build(Macro_Sheet s, sValueBloc b) { MSheetView m = new MSheetView(s, b); return m; }
    }
  nRunnable goto_run;
  Macro_Connexion in;
  MSheetView(Macro_Sheet _sheet, sValueBloc _bloc) { 
    super(_sheet, "sheet view", "sheet view", _bloc); 
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
    addTrigS(1, "goto", goto_run);
    in = addInput(0, "goto", new nRunnable() { public void run() { 
      if (in.lastPack().isBang()) goto_run.run(); }});
  }
  public MSheetView clear() {
    super.clear(); 
    return this; }
  public MSheetView toLayerTop() {
    super.toLayerTop(); 
    return this; }
}
