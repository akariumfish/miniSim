package Macro;

import java.util.ArrayList;

import RApplet.Rapp;
import RBase.RConst;
import UI.*;
import processing.core.PConstants;
import processing.core.PVector;
import sData.*;







/*
connexion 
 circle, hard outline, transparent, mode in or out, exist in a sheet, has an unique number
 has a label with no back for a short description and a field acsessible or not for displaying values
 the label and values are aligned, either of them can be on the left or right
 the connexion circle is on the left right top or down side center of
 the rectangle formed by the label and values
 priority button
 2 round button on top of eachother on left top corner of the connect
 1 round widget covering half of each button with the priority layer
 highlight connectable in when creating link
 package info on top of connections
 
 */
public class Macro_Connexion extends nBuilder implements Macro_Interf {
  Macro_Element getElement() { return elem; }

  public Macro_Connexion toLayerTop() { 
    super.toLayerTop(); 
    msg_view.toLayerTop(); 
    lens.toLayerTop(); 
    ref.toLayerTop();
    return this;
  }
  //ArrayList<nWidget> elem_widgets = new ArrayList<nWidget>();
  nWidget customBuild(nWidget w) { 
    //if (elem_widgets != null) elem_widgets.add(w); 
    if ( (!is_sheet_co && sheet.openning.get() != DEPLOY) || 
    (is_sheet_co && (sheet.openning.get() != DEPLOY || elem.spot == null)) )w.hide();
    return w; 
  }
  
  nWidget ref, lens, msg_view;
  Drawable ref_draw;
  Macro_Element elem; Macro_Sheet sheet; sObj val_self;
  int type = INPUT;
  String descr; boolean is_sheet_co = false;
  Rapp app;
  Macro_Connexion(Macro_Element _elem, Macro_Sheet _sheet, int _type, String _info, boolean isc) {
    super(_elem.gui, _elem.ref_size); 
    app = gui.app;
    type = _type; elem = _elem; sheet = _sheet; is_sheet_co = isc;
    descr = elem.descr+"_co";
    if      (!is_sheet_co && type == INPUT) descr += "_IN";
    else if (!is_sheet_co && type == OUTPUT) descr += "_OUT";
    else if (is_sheet_co && type == INPUT) descr += "_sheet_IN";
    else if (is_sheet_co && type == OUTPUT) descr += "_sheet_OUT";
    //val_self = ((sObj)(elem.bloc.setting_bloc.getValue(descr))); 
    //if (val_self == null) val_self = elem.bloc.setting_bloc.newObj(descr, this);
    //else val_self.set(this);
    lens = addModel("MC_Connect_Default").setTrigger()
      .setSize(ref_size*14/16, ref_size*14/16)
      .setPosition(-ref_size*5/16, -ref_size*5/16)
      .addEventTrigger(new nRunnable(this) { public void run() {
        if (sheet.mmain().selected_sheet != sheet && !is_sheet_co) sheet.select();
        
        if (buildingLine) {
          buildingLine = false; elem.bloc.mmain().buildingLine = false;
          for (Macro_Connexion i : sheet.child_connect) 
            i.lens.setLook(gui.theme.getLook("MC_Connect_Default")).setTrigger();  
        }
        else if (!elem.bloc.mmain().buildingLine && !buildingLine && sheet.mmain().selected_sheet == sheet) {
          if (type == OUTPUT) {
            buildingLine = true; elem.bloc.mmain().buildingLine = true;
            
            sheet.mmain().szone_clear_select();
            
            for (Macro_Connexion i : sheet.child_connect) 
              if (i.type == INPUT) i.lens.setLook(gui.theme.getLook("MC_Connect_In_Actif")).setTrigger(); 
              else if (i.type == OUTPUT && i != (Macro_Connexion)builder) 
                i.lens.setLook(gui.theme.getLook("MC_Connect_Out_Passif")).setBackground(); 
              else if (i.type == OUTPUT && i == (Macro_Connexion)builder) 
                i.lens.setLook(gui.theme.getLook("MC_Connect_Out_Passif")); 
          }
          else if (type == INPUT) {
            buildingLine = true; elem.bloc.mmain().buildingLine = true;
            
            sheet.mmain().szone_clear_select();
            
            for (Macro_Connexion i : sheet.child_connect) 
              if (i.type == OUTPUT) i.lens.setLook(gui.theme.getLook("MC_Connect_Out_Actif")).setTrigger(); 
              else if (i.type == INPUT && i != (Macro_Connexion)builder) 
                i.lens.setLook(gui.theme.getLook("MC_Connect_In_Passif")).setBackground(); 
              else if (i.type == INPUT && i == (Macro_Connexion)builder) 
                i.lens.setLook(gui.theme.getLook("MC_Connect_In_Passif")); 
          }
        }
      } } )
      .addEventFrame(new nRunnable(this) { public void run() {
        sending = false;
        if (buildingLine) {
          newLine.x = elem.bloc.mmain().gui.mouseVector.x;
          newLine.y = elem.bloc.mmain().gui.mouseVector.y;
          if (elem.bloc.mmain().gui.in.getClick("MouseRight")) { 
            buildingLine = false; elem.bloc.mmain().buildingLine = false;
            for (Macro_Connexion i : sheet.child_connect) 
              i.lens.setLook(gui.theme.getLook("MC_Connect_Default")).setTrigger(); 
          }
          if (elem.bloc.mmain().gui.in.getClick("MouseLeft")) {
            boolean found = false;
            for (Macro_Connexion m : sheet.child_connect) { 
              if (type != m.type && m.lens.isHovered()) {
                connect_to(m);
                buildingLine = false; 
                elem.bloc.mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
                  elem.bloc.mmain().buildingLine = false; }});
                for (Macro_Connexion i : sheet.child_connect) 
                  i.lens.setLook(gui.theme.getLook("MC_Connect_Default")).setTrigger(); 
                found = true;
              }
            }
            if (!found && !lens.isHovered()) {
              buildingLine = false; elem.bloc.mmain().buildingLine = false;
              for (Macro_Connexion i : sheet.child_connect) 
                i.lens.setLook(gui.theme.getLook("MC_Connect_Default")).setTrigger();  
            }
          }
        }
        if (!buildingLine && elem.bloc.mmain().gui.in.getClick("MouseRight")) for (Macro_Connexion m : connected_inputs) {
          if (RConst.distancePointToLine(gui.mouseVector.x, gui.mouseVector.y, 
              getCenter().x, getCenter().y, m.getCenter().x, m.getCenter().y) 
              < 
              3 * ref.look.outlineWeight / gui.scale) {
                
                
                
            disconnect_from(m);
            
            
            
            break;
          }
        }
      } } )
      ;
    ref_draw = new Drawable(gui.drawing_pile, 0) { 
      public void drawing() {
        //logln("draw " + descr + " sheet " + sheet.value_bloc.ref + " op " + sheet.openning.get());
        if (
            isDraw()
            ) {
          if (lens.isClicked) app.fill(ref.look.pressColor);
          else if (lens.isHovered) app.fill(ref.look.hoveredColor);
          else if (sending || hasSend > 0 || hasReceived > 0) app.fill(ref.look.outlineColor);
          else app.fill(ref.look.standbyColor);
          app.noStroke(); app.ellipseMode(PConstants.CENTER);
          app.ellipse(getCenter().x, getCenter().y, ref.getLocalSX(), ref.getLocalSY());
          if (lens.isClicked) app.stroke(ref.look.pressColor);
          else if (lens.isHovered) app.stroke(ref.look.hoveredColor);
          else if (sending || hasSend > 0 || hasReceived > 0) app.stroke(ref.look.outlineColor);
          else app.noStroke();
          app.noFill(); app.strokeWeight(ref.look.outlineWeight/4);
          app.ellipse(getCenter().x, getCenter().y, 
            ref.getLocalSX() + ref.look.outlineWeight * 2, ref.getLocalSY() + ref.look.outlineWeight * 2);
            
          if (buildingLine) {
        	  app.stroke(ref.look.outlineColor);
        	  app.strokeWeight(ref.look.outlineWeight/2);
            PVector l = new PVector(newLine.x - getCenter().x, newLine.y - getCenter().y);
            PVector lm = new PVector(l.x, l.y);
            lm.setMag(getSize()/2);
            app.line(getCenter().x+lm.x, getCenter().y+lm.y, 
                 getCenter().x+l.x-lm.x, getCenter().y+l.y-lm.y);
            app.fill(255, 0);
            app.ellipseMode(PConstants.CENTER);
            app.ellipse(getCenter().x, getCenter().y, 
                    getSize(), getSize() );
            app.ellipse(newLine.x, newLine.y, 
                    getSize(), getSize() );
          }
          for (Macro_Connexion m : connected_inputs) {
            if (gui.scale > 0.03 && 
                m.isDraw()
                ) {
              if (RConst.distancePointToLine(elem.bloc.mmain().gui.mouseVector.x, elem.bloc.mmain().gui.mouseVector.y, 
                  getCenter().x, getCenter().y, m.getCenter().x, m.getCenter().y) < 
                  3 * ref.look.outlineWeight / gui.scale) { 
                if (pack_info != null && hasSend > 0) elem.bloc.mmain().info.showText(pack_info);
                app.fill(ref.look.outlineSelectedColor); app.stroke(ref.look.outlineSelectedColor); } 
              else if (sending || hasSend > 0) { app.fill(ref.look.outlineColor); app.stroke(ref.look.outlineColor); }
              else { app.fill(ref.look.standbyColor); app.stroke(ref.look.standbyColor); }
              app.strokeWeight(ref.look.outlineWeight);
              PVector l = new PVector(m.getCenter().x - getCenter().x, m.getCenter().y - getCenter().y);
              PVector lm = new PVector(l.x, l.y);
              lm.setMag(getSize()/2);
              app.line(getCenter().x+lm.x, getCenter().y+lm.y, 
                   getCenter().x+l.x-lm.x, getCenter().y+l.y-lm.y);
            }
          }
          if (hasSend > 0) hasSend--;
          if (hasReceived > 0) hasReceived--;
        }
      }
    };
    ref = addModel("MC_Connect_Link")
      .setSize(ref_size*4/16, ref_size*4/16)
      .setPosition(-ref_size*6/16, ref_size*6/16)
      .setDrawable(ref_draw);
    if (_info != null) lens.setInfo(_info);
    infoText = RConst.copy(_info);
    ref.setParent(elem.back);
    msg_view = addModel("MC_Connect_View").clearParent();
    msg_view.setParent(ref);
    if (type == OUTPUT) { 
      msg_view.stackLeft();
      elem.back.setTextAlignment(PConstants.LEFT, PConstants.CENTER);
      ref.alignRight().setPX(-ref.getLocalX()); 
    } 
    else {
      msg_view.stackRight();
      elem.back.setTextAlignment(PConstants.RIGHT, PConstants.CENTER);
    }
    lens.setParent(ref);
    sheet.child_connect.add(this);
  }
  
  Macro_Connexion upview() { 
    if (isDraw()) {
      ref.show(); 
      if ((!is_sheet_co && elem.bloc.sheet.openning.get() == DEPLOY && elem.bloc.openning.get() == OPEN ) 
          || (is_sheet_co && elem.spot != null && elem.bloc.sheet.openning.get() == OPEN) ) {
        lens.show(); 
        msg_view.show(); 
      } else {
        lens.hide(); 
        msg_view.hide(); 
      }
    } else {
      ref.hide(); 
      lens.hide(); 
      msg_view.hide(); 
    }
    return this;
  }
  
  boolean isDraw() {
    return sheet.mmain().show_macro.get() && ( (!is_sheet_co && sheet.openning.get() == DEPLOY)
            || (is_sheet_co && elem.spot != null && sheet.openning.get() == DEPLOY)
            );
  }
  
  PVector getCenter(nWidget w) { 
    return new PVector(w.getX()+w.getLocalSX()/2, w.getY()+w.getLocalSY()/2);
  }
  PVector getCenter() {
    PVector p = new PVector();
    if (!is_sheet_co) {
      if (sheet.openning.get() != DEPLOY) {
        return p;
      }
      if (sheet.openning.get() == DEPLOY) {
        if (elem.bloc.openning.get() == HIDE) return p;
        if (elem.bloc.openning.get() == REDUC) return getCenter(elem.bloc.grabber);
        if (elem.bloc.openning.get() == OPEN) return getCenter(ref);
      }
    }
    if (is_sheet_co) {
      if (sheet.openning.get() != DEPLOY) {
        return p;
      }
      if (sheet.openning.get() == DEPLOY) {
        if (elem.bloc.sheet.openning.get() == HIDE) return p;
        if (elem.bloc.sheet.openning.get() == REDUC) return getCenter(elem.bloc.sheet.grabber);
        if (elem.bloc.sheet.openning.get() == OPEN) return getCenter(ref);
        if (elem.bloc.sheet.openning.get() == DEPLOY) {
          if (elem.bloc.openning.get() == HIDE) return p;
          if (elem.bloc.openning.get() == REDUC) return getCenter(elem.bloc.grabber);
          if (elem.bloc.openning.get() == OPEN) return getCenter(ref);
        } 
      }
    }
    
    return new PVector(10, 10);
  }
  
  
  float getSize() { return ref.getLocalSY() * 2; }
  
  String infoText = "";
  
  Macro_Connexion setInfo(String t) { 
    infoText = t; lens.setInfo(infoText+" "+last_def+filter); return this; }
  
  public Macro_Connexion clear() {
    super.clear();
    clear_link();
    ref_draw.clear();
    return this;
  }
  
  Macro_Connexion clear_link() {
    for (int i = connected_inputs.size() - 1 ; i >= 0 ; i--) disconnect_from(connected_inputs.get(i));
    for (int i = connected_outputs.size() - 1 ; i >= 0 ; i--) disconnect_from(connected_outputs.get(i));
    return this;
  }
  
  Macro_Connexion clear_link_array() {
    connected_inputs.clear();
    connected_outputs.clear();
    return this;
  }
  
  boolean connect_to(Macro_Connexion m) {
    if (m != null) {
      if (type == OUTPUT && m.type == INPUT && !connected_inputs.contains(m)) {
        connected_inputs.add(m);
        m.connected_outputs.add(this); 
        sheet.add_link(descr, m.descr);
        elem.bloc.mmain().last_link_sheet = sheet;
        elem.bloc.mmain().last_created_link = descr + INFO_TOKEN + m.descr;
        return true;
      } else if (type == INPUT && m.type == OUTPUT && !connected_outputs.contains(m)) {
        connected_outputs.add(m);
        m.connected_inputs.add(this); 
        sheet.add_link(m.descr, descr);
        elem.bloc.mmain().last_link_sheet = sheet;
        elem.bloc.mmain().last_created_link = m.descr + INFO_TOKEN + descr;
        return true;
      } 
    }
    return false;
  }
  void disconnect_from(Macro_Connexion m) {
    if (m != null) {// && connected_inputs.contains(m)
      connected_inputs.remove(m);
      m.connected_outputs.remove(this); 
      sheet.remove_link(descr, m.descr);
    } 
    if (m != null) {// && connected_outputs.contains(m)
      connected_outputs.remove(m);
      m.connected_inputs.remove(this); 
      sheet.remove_link(m.descr, descr);
    }
  }
  
  boolean buildingLine = false;
  PVector newLine = new PVector();
  
  ArrayList<Macro_Connexion> connected_inputs = new ArrayList<Macro_Connexion>();
  ArrayList<Macro_Connexion> connected_outputs = new ArrayList<Macro_Connexion>();
  
  void end_packet_process() {
    last_packet = null;
  }
  
  boolean sending = false;
  int hasSend = 0, hasReceived = 0;
  
  String last_def = "";
  
  String pack_info = null;
  
  Macro_Connexion send(Macro_Packet p) {
    msg_view.setText(p.getText());
    last_def = RConst.copy(p.def);
    lens.setInfo(infoText+" "+last_def);
    pack_info = RConst.copy(p.def);
    for (String m : p.messages) pack_info = pack_info + " " + m;
    sending = true;
    hasSend = 15;
    //logln(descr+" send");
    packet_to_send.add(p);
    sheet.ask_packet_process(this);
    return this;
  }
  ArrayList<Macro_Packet> packet_to_send = new ArrayList<Macro_Packet>();
  
  boolean process_send() {
    //logln(descr+" process send");
    
    process_resum = ""; 
    boolean flag = packet_to_send.size() == 0;
    if (!flag) process_resum += descr+" send ";
    for (Macro_Packet p : packet_to_send) {
      process_resum = process_resum + p.getText() + " ";
      if (direct_co != null && direct_co.type == OUTPUT) direct_co.send(p);
      if (direct_co != null && direct_co.type == INPUT) direct_co.receive(this, p);
      int prio = 0;
      for (Macro_Connexion m : connected_inputs) 
        if (prio < m.elem.bloc.priority.get() ) prio = m.elem.bloc.priority.get();
      //logln(descr+" max prio "+prio);
      int co_done = 0;
      while (prio >= 0 && co_done < connected_inputs.size()) {
        //logln("try prio "+prio);
        for (Macro_Connexion m : connected_inputs) {
          //logln("try co "+m.elem.descr+" of prio "+m.elem.bloc.priority.get());
          if (prio == m.elem.bloc.priority.get()) { 
            //logln("found"); 
            co_done++; 
            m.receive(this, p); 
          }
        }
        prio--;
      }
      //for (Macro_Connexion m : connected_inputs) m.receive(p);
    }
    packet_to_send.clear();
    return flag;
  }
  
  Macro_Connexion sendBang() { send(Macro_Packet.newPacketBang()); return this; }
  Macro_Connexion sendFloat(float v) { send(Macro_Packet.newPacketFloat(v)); return this; }
  Macro_Connexion sendInt(int v) { send(Macro_Packet.newPacketInt(v)); return this; }
  Macro_Connexion sendBool(boolean v) { send(Macro_Packet.newPacketBool(v)); return this; }
  Macro_Connexion setDefBang() { last_def = "bang"; return this; }
  Macro_Connexion setDefBool() { last_def = "bool"; return this; }
  Macro_Connexion setDefBin() { last_def = "bin"; return this; }
  Macro_Connexion setDefInt() { last_def = "int"; return this; }
  Macro_Connexion setDefFloat() { last_def = "float"; return this; }
  Macro_Connexion setDefNumber() { last_def = "num"; return this; }
  Macro_Connexion setDefVal() { last_def = "val"; return this; }
  Macro_Connexion setDefVec() { last_def = "vec"; return this; }
  
  
  
  Macro_Connexion setLastBang() { 
    last_packet = Macro_Packet.newPacketBang(); msg_view.setText(last_packet.getText()); return this; }
  Macro_Connexion setLastBool(boolean v) { 
    last_packet = Macro_Packet.newPacketBool(v); msg_view.setText(last_packet.getText()); return this; }
  Macro_Connexion setLastFloat(float v) { 
    last_packet = Macro_Packet.newPacketFloat(v); msg_view.setText(last_packet.getText()); return this; }
  

  Macro_Packet last_packet = null;
  
  Macro_Packet getLastPacket() { return last_packet; }
  
  void receive(Macro_Connexion s, Macro_Packet p) {
    if (filter == null || p.def.equals(filter) || 
        (filter.equals("bin") && (p.def.equals("bool") || p.def.equals("bang"))) ||
        (filter.equals("num") && (p.def.equals("float") || p.def.equals("int"))) ||
        (filter.equals("val") && (p.def.equals("float") || p.def.equals("int") || 
                                  p.def.equals("bool"))) ) {
      //logln(descr+"receive");
      packet_received.add(p);
      packet_sender.add(s);
      sheet.ask_packet_process(this);
    }
  }
  ArrayList<Macro_Packet> packet_received = new ArrayList<Macro_Packet>();
  ArrayList<Macro_Connexion> packet_sender = new ArrayList<Macro_Connexion>();
  
  String process_resum = "";
  boolean process_receive() {
    //logln(descr+" process receive");
    
    process_resum = "";
    boolean flag = packet_received.size() == 0;
    if (!flag) process_resum += descr+" receive ";
    
    int prio = 0;
    for (Macro_Connexion m : packet_sender) 
      if (prio < m.elem.bloc.priority.get()) prio = m.elem.bloc.priority.get();
    int c = 0;
    while (prio >= 0 && c < packet_received.size()) {
      for (int i = 0 ; i < packet_received.size() ; i++) 
        if (prio == packet_sender.get(i).elem.bloc.priority.get()) { 
          c++; 
          last_packet = packet_received.get(i);
          process_resum = process_resum + last_packet.getText() + " ";
          for (nRunnable r : eventReceiveRun) r.run();
          
          if (last_packet.isBang()) nRunnable.runEvents(eventReceiveBangRun);
          
          if (direct_co != null && direct_co.type == OUTPUT) direct_co.send(last_packet);
          if (direct_co != null && direct_co.type == INPUT) direct_co.receive(packet_sender.get(i), last_packet);
          msg_view.setText(last_packet.getText());
          hasReceived = 15;
        }
      prio--;
    }
    //for (int i = 0 ; i < packet_received.size() ; i++) {
    //  last_packet = packet_received.get(i);
    //  process_resum = process_resum + last_packet.getText() + " ";
    //  for (Runnable r : eventReceiveRun) r.run();
    //  if (direct_co != null && direct_co.type == OUTPUT) direct_co.send(last_packet);
    //  if (direct_co != null && direct_co.type == INPUT) direct_co.receive(packet_sender.get(i), last_packet);
    //  msg_view.setText(last_packet.getText());
    //  hasReceived = 15;
    //}
    packet_received.clear();
    packet_sender.clear();
    return flag;
  }
  
  ArrayList<nRunnable> eventReceiveRun = new ArrayList<nRunnable>();
  Macro_Connexion addEventReceive(nRunnable r)    { eventReceiveRun.add(r); return this; }
  Macro_Connexion removeEventReceive(nRunnable r) { eventReceiveRun.remove(r); return this; }
  
  ArrayList<nRunnable> eventReceiveBangRun = new ArrayList<nRunnable>();
  Macro_Connexion addEventReceiveBang(nRunnable r)    { eventReceiveBangRun.add(r); return this; }
  
  
  Macro_Connexion direct_co = null;
  void direct_connect(Macro_Connexion o) { direct_co = o; }
  
  String filter = null;
  
  Macro_Connexion setFilter(String f) {
    filter = RConst.copy(f);
    lens.setInfo(infoText+" "+filter);
    return this; }
  Macro_Connexion clearFilter() {
    lens.setInfo(infoText);
    filter = null;
    return this; }
  Macro_Connexion setFilterBang() {
    filter = "bang";
    lens.setInfo(infoText+" "+filter);
    return this; }
  Macro_Connexion setFilterInt() {
    filter = "int";
    lens.setInfo(infoText+" "+filter);
    return this; }
  Macro_Connexion setFilterFloat() {
    filter = "float";
    lens.setInfo(infoText+" "+filter);
    return this; }
  Macro_Connexion setFilterNumber() { //int and float
    filter = "num";
    lens.setInfo(infoText+" "+filter);
    return this; }
  Macro_Connexion setFilterBool() {
    filter = "bool";
    lens.setInfo(infoText+" "+filter);
    return this; }
  Macro_Connexion setFilterBin() {
    filter = "bin";
    lens.setInfo(infoText+" "+filter);
    return this; }
  Macro_Connexion setFilterValue() { //bool int and float
    filter = "val";
    lens.setInfo(infoText+" "+filter);
    return this; }
  Macro_Connexion setFilterVec() { //bool int and float
    filter = "vec";
    lens.setInfo(infoText+" "+filter);
    return this; }
}

/*

 element > drawer
 has a text pour l'info bulle
 is a rectangle without back who can hold different function :
 button trigger / switch > runnable
 label for info or values > element has method to set
 selector : multi switch exclusives or not > runnable
 slide?
 jauge and graph? 
 connexions 4 places possible
 
 */

