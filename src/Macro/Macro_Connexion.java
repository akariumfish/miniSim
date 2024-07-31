package Macro;

import java.util.ArrayList;

import RApplet.Rapp;
import RApplet.RConst;
import UI.*;
import processing.core.PApplet;
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

//  public Macro_Connexion buildLayer() { 
//	  buildLayerTo(msg_view.getDrawable()); 
//  return this; }
//  public Macro_Connexion buildLayerTo(Drawable d) { 
//	    d.add_coDrawer(msg_view.getDrawable()); 
//		msg_view.getDrawable().clear_coDrawer();
//	    msg_view.getDrawable().add_coDrawer(lens.getDrawable()); 
//	    msg_view.getDrawable().add_coDrawer(ref.getDrawable()); 
//	return this; }
  
  public Macro_Connexion toLayerTop() { 
//	    if (!gui.app.DEBUG_NOTOLAYTOP) 
//	    	super.toLayerTop(); 
	    msg_view.toLayerTop(); 
	    lens.toLayerTop(); 
	    ref.toLayerTop(); 
        
	    return this;
  }
  //ArrayList<nWidget> elem_widgets = new ArrayList<nWidget>();
  protected nWidget customBuild(nWidget w) { 
    //if (elem_widgets != null) elem_widgets.add(w); 
    if ( (!is_sheet_co && sheet.openning.get() != DEPLOY) || 
    (is_sheet_co && (sheet.openning.get() != DEPLOY || elem.spot == null)) )w.hide();
    return w; 
  }

  void draw_point() {
      app.ellipseMode(PConstants.CENTER);
    	  if (type == OUTPUT) {
          if (lens.isClicked) app.fill(ref.look.pressColor);
          else if (lens.isHovered) app.fill(ref.look.hoveredColor);
          else if (hasSend > 0 || hasReceived > 0) app.fill(ref.look.outlineColor);
          else if (linking_connections.size() > 0) app.fill(ref.look.textColor);
          else if (linkable) app.fill(ref.look.pressColor);
          else if (illumined) app.fill(ref.look.outlineSelectedColor);
          else app.fill(ref.look.standbyColor);
          app.noStroke(); 
      	  app.ellipse(getCenter().x, getCenter().y, ref.getLocalSX()/1.2F, 
      			  ref.getLocalSY()/1.2F);
          if (lens.isClicked) app.stroke(ref.look.pressColor);
          else if (lens.isHovered) app.stroke(ref.look.hoveredColor);
          else if (hasSend > 0 || hasReceived > 0) app.stroke(ref.look.outlineColor);
          else if (linking_connections.size() > 0) app.stroke(ref.look.pressColor);
          else if (connected_inputs.size() > 0) app.stroke(ref.look.standbyColor);
          else app.noStroke();
          app.noFill(); 
          if (linking_connections.size() > 0) app.strokeWeight(ref.look.outlineWeight*1.2F);
          else app.strokeWeight(ref.look.outlineWeight/2);
          app.ellipse(getCenter().x, getCenter().y, 
            ref.getLocalSX() + ref.look.outlineWeight * 2, 
            ref.getLocalSY() + ref.look.outlineWeight * 2);
      } else {
          if (lens.isClicked) app.stroke(ref.look.pressColor);
          else if (lens.isHovered) app.stroke(ref.look.hoveredColor);
          else if (hasSend > 0 || hasReceived > 0) app.stroke(ref.look.outlineColor);
          else if (linking_connections.size() > 0) app.stroke(ref.look.pressColor);
          else if (linkable) app.stroke(ref.look.pressColor);
          else if (illumined) app.stroke(ref.look.outlineSelectedColor);
          else app.stroke(ref.look.standbyColor);
          app.noFill(); 
          if (linking_connections.size() > 0) app.strokeWeight(ref.look.outlineWeight*1.2F);
          else app.strokeWeight(ref.look.outlineWeight/1.1F);
          app.ellipse(getCenter().x, getCenter().y, 
        		ref.getLocalSX() + ref.look.outlineWeight * 1.9F, 
        		ref.getLocalSY() + ref.look.outlineWeight * 1.9F);
          if (lens.isClicked) app.stroke(ref.look.pressColor);
          else if (lens.isHovered) app.stroke(ref.look.hoveredColor);
          else if (hasSend > 0 || hasReceived > 0) app.stroke(ref.look.outlineColor);
          else if (linking_connections.size() > 0) app.stroke(ref.look.textColor);
          else if (connected_outputs.size() > 0) app.stroke(ref.look.standbyColor);
          else app.noStroke();
          app.noFill(); 
          app.strokeWeight(ref.look.outlineWeight);
        	  app.ellipse(getCenter().x, getCenter().y, 
        			  ref.getLocalSX()*1.1F, ref.getLocalSY()*1.1F);
    	  }
  }
  void draw_line(Macro_Connexion m) {
	  if (m.isDraw() && (sheet.mmain().show_link.get() || 
				((lens.isHovered || m.lens.isHovered) && sheet.mmain().link_volatil.get()) || 
				elem.bloc.szone_selected || m.elem.bloc.szone_selected 
//		||
//		(elem.spot != null && elem.bloc.sheet.openning.get() == OPEN) ||
//		(m.elem.spot != null && m.elem.bloc.sheet.openning.get() == OPEN)
		) ) {

//		  m.ref.setAlwaysView(true);
//		  m.draw_point();
//		  m.ref.setAlwaysView(false);
		  boolean is_hovered = (sheet.mmain().selected_sheet == m.sheet || 
				  sheet.mmain().selected_sheet == sheet) && 
				  (RConst.distancePointToLine(elem.bloc.mmain().gui.mouseVector.x, 
						  elem.bloc.mmain().gui.mouseVector.y, 
						  getCenter().x, getCenter().y, m.getCenter().x, m.getCenter().y) < 
				  		  3 * ref.look.outlineWeight / gui.scale ||
				  lens.isHovered || m.lens.isHovered)
	            ;
		  if (is_hovered) {
			  app.strokeWeight(ref.look.outlineWeight*2.0F);
			  illumined = true; m.illumined = true; 
		  }
		  else app.strokeWeight(ref.look.outlineWeight);
		  PVector l = new PVector(m.getCenter().x - getCenter().x, 
				  				  m.getCenter().y - getCenter().y);
		  PVector lm = new PVector(l.x, l.y);
		  lm.setMag(ref.getLocalSX() / 2 + ref.look.outlineWeight);
		  if (is_hovered) { 
		    	if (pack_info != null && (hasSend > 0 || hasReceived > 0)) 
		    		  elem.bloc.mmain().info.showText(pack_info);
	    	      app.fill(ref.look.outlineSelectedColor); 
	    	      app.stroke(ref.look.outlineSelectedColor); 
	    	  
	          app.line(getCenter().x+lm.x, getCenter().y+lm.y, 
	              getCenter().x+l.x-lm.x, getCenter().y+l.y-lm.y); 
		    } else if (linking_connections.contains(m)) { 
		    	  if (hasSend > 0 || hasReceived > 0) { 
		    		  app.fill(ref.look.pressColor); app.stroke(ref.look.textColor); }
		    	  else { 
		        	  app.fill(ref.look.textColor); app.stroke(ref.look.pressColor); } 
		
		      app.line(getCenter().x+lm.x, getCenter().y+lm.y, 
		           getCenter().x+l.x-lm.x, getCenter().y+l.y-lm.y);
		    } else if (hasSend > 0 || hasReceived > 0) { 
		  	  if (sheet.mmain().packpross_by_frame.get() >= 1 / 
		  			  sheet.mmain().inter.framerate.median_framerate.get()) {
	    	  	  app.fill(ref.look.outlineColor); app.stroke(ref.look.outlineColor); 
	              app.line(getCenter().x+lm.x, getCenter().y+lm.y, 
		                   getCenter().x+l.x-lm.x, getCenter().y+l.y-lm.y);
		  	} else {
	    	  	  app.fill(ref.look.standbyColor); app.stroke(ref.look.standbyColor); 
	          app.line(getCenter().x+lm.x, getCenter().y+lm.y, 
	               getCenter().x+l.x-lm.x, getCenter().y+l.y-lm.y);
	          
	    	  	  app.fill(ref.look.outlineColor); app.stroke(ref.look.outlineColor); 
	          l.set(l.x - 2*lm.x, l.y - 2*lm.y);
	          l.setMag(sheet.mmain().packpross_pile*l.mag());
	          app.line(getCenter().x+lm.x, getCenter().y+lm.y, 
	                  getCenter().x+l.x, getCenter().y+l.y); 
	        }
		  } else { 
			  app.fill(ref.look.standbyColor); app.stroke(ref.look.standbyColor); 
		      app.line(getCenter().x+lm.x, getCenter().y+lm.y, 
		              getCenter().x+l.x-lm.x, getCenter().y+l.y-lm.y); 
		  }
	  }
  }
  Macro_Connexion mirror(boolean b) { 
	  	lens.setSize(ref_size*14/16, ref_size*14/16)
		  .setPosition(-ref_size*5/16, -ref_size*5/16);
	    ref.setSize(ref_size*4/16, ref_size*4/16)
	      .setPosition(-ref_size*8/16, ref_size*7/16);
	    if ((!b && type == OUTPUT) || (b && type == INPUT)) { 
	      msg_view.stackLeft();
	      elem.back.setTextAlignment(PConstants.LEFT, PConstants.CENTER);
	      ref.alignRight().setPX(-ref.getLocalX()); 
	    } 
	    else {
	      msg_view.stackRight();
	      elem.back.setTextAlignment(PConstants.RIGHT, PConstants.CENTER);
	      ref.alignLeft().setPX(ref.getLocalX()); 
	    }
      return this;
  }
  
  nWidget ref, lens, msg_view;
  Drawable ref_draw;
  Macro_Element elem; Macro_Sheet sheet; //sObj val_self;
  int type = INPUT;
  String descr,base_info; boolean is_sheet_co = false;
  Rapp app;
  boolean illumined = false;
  Macro_Connexion selfthis;
  public void start_building_line() {
      buildingLine = true; elem.bloc.mmain().buildingLine = true;
      elem.bloc.mmain().line_building_co = selfthis;
      ref.setAlwaysView(buildingLine);
      
      sheet.mmain().szone_clear_select();
      
	  if (type == OUTPUT) {
        for (Macro_Connexion i : sheet.child_connect) 
        		if (i == this)
        			i.lens.setLook(gui.theme.getLook("MC_Connect_Out_Passif")).setPassif(); 
        		else if (linkable == i.linkable || link_undefine || i.link_undefine)  {
	            	if (i.type == OUTPUT) 
            			i.lens.setLook(gui.theme.getLook("MC_Connect_Out_Passif")).setPassif(); 
            		else if (i.type == INPUT) 
            			i.lens.setLook(gui.theme.getLook("MC_Connect_In_Actif")).setTrigger(); 
        		} else {
	            	if (i.type == OUTPUT) 
            			i.lens.setLook(gui.theme.getLook("MC_Connect_Out_Passif")).setPassif(); 
            		else if (i.type == INPUT) 
            			i.lens.setLook(gui.theme.getLook("MC_Connect_In_Passif")).setPassif(); 
        		} 
      } else if (type == INPUT) {
        for (Macro_Connexion i : sheet.child_connect) 
    		if (i == this)
    			i.lens.setLook(gui.theme.getLook("MC_Connect_Out_Passif")).setPassif(); 
    		else if (linkable == i.linkable || link_undefine || i.link_undefine)  {
            	if (i.type == INPUT) 
        			i.lens.setLook(gui.theme.getLook("MC_Connect_In_Passif")).setPassif(); 
        		else if (i.type == OUTPUT) 
        			i.lens.setLook(gui.theme.getLook("MC_Connect_Out_Actif")).setTrigger(); 
    		} else {
            	if (i.type == INPUT) 
        			i.lens.setLook(gui.theme.getLook("MC_Connect_In_Passif")).setPassif(); 
        		else if (i.type == OUTPUT) 
        			i.lens.setLook(gui.theme.getLook("MC_Connect_Out_Passif")).setPassif(); 
    		} 
      } 
  }
  public void end_building_line() {
      buildingLine = false; elem.bloc.mmain().buildingLine = false;
      elem.bloc.mmain().line_building_co = null;
      ref.setAlwaysView(buildingLine);
      for (Macro_Connexion i : sheet.child_connect) 
        i.lens.setLook(gui.theme.getLook("MC_Connect_Default")).setTrigger();  
  }
  Macro_Connexion(Macro_Element _elem, Macro_Sheet _sheet, int _type, String _info, boolean isc) {
    super(_elem.gui, _elem.ref_size); 
    selfthis = this;
    app = gui.app;
    type = _type; elem = _elem; sheet = _sheet; is_sheet_co = isc; base_info = _info;
    descr = elem.descr+"_co";
    if      (!is_sheet_co && type == INPUT) descr += "_IN";
    else if (!is_sheet_co && type == OUTPUT) descr += "_OUT";
    else if (is_sheet_co && type == INPUT) descr += "_sheet_IN";
    else if (is_sheet_co && type == OUTPUT) descr += "_sheet_OUT";
    
    lens = addModel("MC_Connect_Default").setTrigger()
      .setSize(ref_size*14/16, ref_size*14/16)
      .setPosition(-ref_size*5/16, -ref_size*5/16)
      .addEventTrigger(new nRunnable(this) { public void run() {
        if (sheet.mmain().selected_sheet != sheet && !is_sheet_co) sheet.select();
        if (!elem.bloc.mmain().buildingLine && !buildingLine && sheet.mmain().selected_sheet == sheet) 
        		start_building_line();
        else if (buildingLine) end_building_line();
      } } )
      .addEventFrame(new nRunnable(this) { public void run() {
        if (buildingLine) {
          newLine.x = elem.bloc.mmain().gui.mouseVector.x;
          newLine.y = elem.bloc.mmain().gui.mouseVector.y;
          if (elem.bloc.mmain().gui.in.getClick("MouseRight")) { 
            buildingLine = false; elem.bloc.mmain().buildingLine = false;
	          elem.bloc.mmain().line_building_co = null;
            ref.setAlwaysView(buildingLine);
            for (Macro_Connexion i : sheet.child_connect) 
              i.lens.setLook(gui.theme.getLook("MC_Connect_Default")).setTrigger(); 
          }
          if (elem.bloc.mmain().gui.in.getClick("MouseLeft")) {
            for (Macro_Connexion m : sheet.child_connect) {
              if (type != m.type && (m.linkable == linkable || link_undefine || m.link_undefine) && 
            		  m.lens.isHovered()) {
                connect_to(m);
                if (m.link_undefine) if (linkable) m.set_link(); else m.set_nolink();
                else if (link_undefine) if (m.linkable) set_link(); else set_nolink();
                buildingLine = false; 
                ref.setAlwaysView(buildingLine);
                elem.bloc.mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
                  elem.bloc.mmain().buildingLine = false;
    	          	  elem.bloc.mmain().line_building_co = null; }});
                for (Macro_Connexion i : sheet.child_connect) 
                  i.lens.setLook(gui.theme.getLook("MC_Connect_Default")).setTrigger(); 
              }
            }
          }
        }
        if (!buildingLine && elem.bloc.mmain().gui.in.getClick("MouseRight")) 
        	for (Macro_Connexion m : connected_inputs) {
          if ( (sheet.mmain().selected_sheet == m.sheet || 
        		  sheet.mmain().selected_sheet == sheet) && 
        		  (RConst.distancePointToLine(
        				  elem.bloc.mmain().gui.mouseVector.x, elem.bloc.mmain().gui.mouseVector.y, 
  					  getCenter().x, getCenter().y, m.getCenter().x, m.getCenter().y)
        				  < 3 * ref.look.outlineWeight / gui.scale ||
  			  lens.isHovered || m.lens.isHovered) && 
              sheet.mmain().link_volatil.get() && 
              (sheet.mmain().show_link.get() || (lens.isHovered || m.lens.isHovered) )
              ) {
                
                
                
            disconnect_from(m);
            
            
            
            break;
          }
        }
        
//        if (!buildingLine) for (Macro_Connexion m : connected_inputs) {
//            if ( (sheet.mmain().selected_sheet == m.sheet || 
//          		  sheet.mmain().selected_sheet == sheet) && 
//          		  (RConst.distancePointToLine(
//  				  elem.bloc.mmain().gui.mouseVector.x, elem.bloc.mmain().gui.mouseVector.y, 
//  				  getCenter().x, getCenter().y, m.getCenter().x, m.getCenter().y)
//  				  < 3 * ref.look.outlineWeight / gui.scale ||
//  				  lens.isHovered || m.lens.isHovered) && 
//          		  (sheet.mmain().show_link.get() || (lens.isHovered || m.lens.isHovered) ) ) {
//            	elem.bloc.light_on();
//            	m.elem.bloc.light_on();
//        } }
    }});
    
    ref_draw = new Drawable(gui.drawing_pile, 0) { public void drawing() {
      //logln("draw " + descr + " sheet " + sheet.value_bloc.ref + " op " + sheet.openning.get());
      if (ref.isViewable() && isDraw() ) {
	      if (gui.scale > 0.03) {
	    		  for (Macro_Connexion m : connected_inputs) {
	        		  draw_line(m);
	        		  m.draw_point();
	        	  }
	        	  for (Macro_Connexion m : connected_outputs) {
	        		  m.draw_line(selfthis);
	        		  m.draw_point();
	        	  }
        		  draw_point();
	      }
//      	  draw_point();
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
      }
      illumined = false; 
      if (hasSend > 0) hasSend--;
      if (hasReceived > 0) hasReceived--;
    } };
    ref = addModel("MC_Connect_Link")
      .setSize(ref_size*4/16, ref_size*4/16)
      .setPosition(-ref_size*8/16, ref_size*7/16)
      .setDrawable(ref_draw)
      .setFineView(true);
    if (_info != null) lens.setInfo(_info);
    infoText = RConst.copy(_info);
    ref.setParent(elem.back).setIdentity("co_ref");
    msg_view = addModel("MC_Connect_View").clearParent();
    msg_view.setParent(ref).setTextLineLength(10);
    msg_view.setIdentityFlag(true).setIdentity("msg_view");

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
  
	void enlight() {
  		ref.setOutlineColor(gui.app.color(255, 0, 255));
  		ref.setStandbyColor(gui.app.color(255, 0, 255));
  		ref.setLabelColor(gui.app.color(255, 0, 255));
  		ref.setClickedColor(gui.app.color(255, 0, 255));
  		elem.bloc.mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
  			ref.setOutlineColor(gui.app.color(200, 100, 100));
  			ref.setStandbyColor(gui.app.color(200));
  			ref.setLabelColor(gui.app.color(20, 180, 240));
      		ref.setClickedColor(gui.app.color(10, 110, 250));
  		}});
	}

  Macro_Connexion hide_msg() { 
	  msg_view
	    .setTextVisibility(false)
	    .setStandbyColor(gui.app.color(0, 0, 0, 1))
	    .setSize(ref_size*0.5, ref_size*0.75);
    return this;
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
    return sheet.mmain().show_macro.get() && ( 
    			(!is_sheet_co && sheet.openning.get() == DEPLOY) || 
            (is_sheet_co && elem.spot != null && sheet.openning.get() == DEPLOY)
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
        if (m.link_undefine && !link_undefine) 
        		if (linkable) m.set_link(); else m.set_nolink();
        if (link_undefine && !m.link_undefine)
    			if (m.linkable) set_link(); else set_nolink();
        if (link_undefine && m.link_undefine)
    			if (linkable) m.set_link(); else m.set_nolink();
        if (linkable && m.linkable) { 
	        	linking_connections.add(m); m.linking_connections.add(this); 
        }
        nRunnable.runEvents(eventLinkRun);
        nRunnable.runEvents(m.eventLinkRun);
        return true;
      } else if (type == INPUT && m.type == OUTPUT && !connected_outputs.contains(m)) {
        connected_outputs.add(m);
        m.connected_inputs.add(this); 
        sheet.add_link(m.descr, descr);
        elem.bloc.mmain().last_link_sheet = sheet;
        elem.bloc.mmain().last_created_link = m.descr + INFO_TOKEN + descr;
        if (m.link_undefine && !link_undefine) 
    			if (linkable) m.set_link(); else m.set_nolink();
        if (link_undefine && !m.link_undefine)
			if (m.linkable) set_link(); else set_nolink();
        if (linkable && m.linkable) { 
	        	linking_connections.add(m); m.linking_connections.add(this); 
        }
        nRunnable.runEvents(eventLinkRun);
        nRunnable.runEvents(m.eventLinkRun);
        return true;
      } 
    }
    return false;
  }
  void disconnect_from(Macro_Connexion m) {
    		connected_inputs.remove(m);
      	m.connected_outputs.remove(this); 
      	sheet.remove_link(descr, m.descr);
    		connected_outputs.remove(m);
		m.connected_inputs.remove(this); 
		sheet.remove_link(m.descr, descr);
	    	linking_connections.remove(m);
	    	m.linking_connections.remove(this);
        if (connected_inputs.size() == 0 && connected_outputs.size() == 0 && 
        		link_undefine) set_nolink();
        if (m.connected_inputs.size() == 0 && m.connected_outputs.size() == 0 && 
        		m.link_undefine) m.set_nolink();
	    	nRunnable.runEvents(eventUnLinkRun);
	    	nRunnable.runEvents(m.eventUnLinkRun);
//        if (m.link_undefine && !link_undefine && linkable) m.set_nolink();
//        if (link_undefine && !m.link_undefine && m.linkable) set_nolink();
  }

  Macro_Connexion set_link() {
	  if (!linkable) {
		linkable = true;      
		for (Macro_Connexion m : direct_cos) m.set_link();
		
		for (Macro_Connexion c : connected_inputs)
			if (c.linkable) {
				if (!linking_connections.contains(c)) linking_connections.add(c);
				if (!c.linking_connections.contains(this)) c.linking_connections.add(this);
//		    		nRunnable.runEvents(eventLinkRun);
//		    		nRunnable.runEvents(c.eventLinkRun);
			} else if (!c.linkable && c.link_undefine) {
				c.set_link();
				if (!linking_connections.contains(c)) linking_connections.add(c);
				if (!c.linking_connections.contains(this)) c.linking_connections.add(this);
			}
		for (Macro_Connexion c : connected_outputs)
			if (c.linkable) {
				if (!linking_connections.contains(c)) linking_connections.add(c);
				if (!c.linking_connections.contains(this)) c.linking_connections.add(this);
			} else if (!c.linkable && c.link_undefine) {
				c.set_link();
				if (!linking_connections.contains(c)) linking_connections.add(c);
				if (!c.linking_connections.contains(this)) c.linking_connections.add(this);
			}
	  }
    return this;
  }
  Macro_Connexion set_nolink() {
	  if (linkable) {
		linkable = false;  
//		ArrayList<Macro_Connexion> temp = new ArrayList<Macro_Connexion>();
		for (Macro_Connexion m : direct_cos) if (m.link_undefine) m.set_nolink();
//		for (Macro_Connexion c : linking_connections) temp.add(c);
//		for (Macro_Connexion c : connected_inputs) temp.add(c);
//		for (Macro_Connexion c : connected_outputs) temp.add(c);
//		for (Macro_Connexion c : temp) linking_connections.remove(c);
		linking_connections.clear();
		if (type == OUTPUT) for (Macro_Connexion c : connected_inputs) 
			if (c.link_undefine) c.set_nolink();
	  }
    return this;
  }

  void direct_connect(Macro_Connexion o) { 
	  if (linkable) o.set_link(); //else o.set_nolink();
	  direct_cos.add(o); 
  }
  void direct_deconnect(Macro_Connexion o) { 
	  if (linkable && o.link_undefine && o.type == OUTPUT) o.set_nolink();
      if (linkable && o.connected_inputs.size() == 0 && o.connected_outputs.size() == 0 && 
      		o.link_undefine) o.set_nolink();
	  direct_cos.remove(o); 
  }
  ArrayList<Macro_Connexion> direct_cos = new ArrayList<Macro_Connexion>();
  
  protected boolean linkable = false;
  public boolean link_undefine = false;
  void set_undefine() { link_undefine = true; }
  ArrayList<Macro_Connexion> linking_connections = new ArrayList<Macro_Connexion>();
  
  ArrayList<nRunnable> eventUnLinkRun = new ArrayList<nRunnable>();
  ArrayList<nRunnable> eventLinkRun = new ArrayList<nRunnable>();
  Macro_Connexion addEventChangeLink(nRunnable r) { 
	  eventUnLinkRun.add(r); eventLinkRun.add(r); return this; }
  Macro_Connexion removeEventChangeLink(nRunnable r) { 
	  eventUnLinkRun.remove(r); eventLinkRun.remove(r); return this; }
  Macro_Connexion addEventLink(nRunnable r) { eventLinkRun.add(r); return this; }
  Macro_Connexion removeEventLink(nRunnable r) { eventLinkRun.remove(r); return this; }
  Macro_Connexion addEventUnLink(nRunnable r) { eventUnLinkRun.add(r); return this; }
  Macro_Connexion removeEventUnLink(nRunnable r) { eventUnLinkRun.remove(r); return this; }
  
  boolean buildingLine = false;
  PVector newLine = new PVector();
  
  ArrayList<Macro_Connexion> connected_inputs = new ArrayList<Macro_Connexion>();
  ArrayList<Macro_Connexion> connected_outputs = new ArrayList<Macro_Connexion>();
  
  void end_packet_process() {
    last_packet = null;
//    sended_since_last_pross = false;
  }
  
//  boolean sended_since_last_pross = false;
  int hasSend = 0, hasReceived = 0;
  
  String last_def = "";
  
  String pack_info = null;
//  private boolean is_duplic_packet(Macro_Packet p) {
//	  boolean result = false;
//	  for (Macro_Packet s : packet_to_send) 
//		  result = result || ( s.getText().equals(p.getText()) && 
//				  			   s.getType().equals(p.getType()) );
//	  return result;
//  }
  Macro_Connexion send(Macro_Packet p) {
//	  if (!sended_since_last_pross || true) { // || !is_duplic_packet(p)
//		    sended_since_last_pross = true;
	    msg_view.setText(p.getText());
	    last_def = RConst.copy(p.def);
	    lens.setInfo(infoText+" "+last_def);
	    pack_info = RConst.copy(p.def);
	    for (String m : p.messages) pack_info = pack_info + " " + m;
	    
	    
	    if (sheet.mmain().loading_delay == 0)
		    hasSend = Math.max(6, PApplet.parseInt( Math.min( 
		    		sheet.mmain().inter.framerate.median_framerate.get(), 
		    		sheet.mmain().inter.framerate.median_framerate.get() / 
		    		sheet.mmain().packpross_by_frame.get()) ));
	    gui.app.plogln(descr+" send");
	    packet_to_send.add(p);
	    sheet.ask_packet_process(this);

      for (Macro_Connexion m : direct_cos) 
	      if (m.type == OUTPUT) m.send_from_dir(p);
	      else if (m.type == INPUT) m.receive(this, p);
	  return this;
  }
  Macro_Connexion send_from_dir(Macro_Packet p) {
//	  if (!sended_since_last_pross || true) { // || !is_duplic_packet(p)
//		    sended_since_last_pross = true;
	    msg_view.setText(p.getText());
	    last_def = RConst.copy(p.def);
	    lens.setInfo(infoText+" "+last_def);
	    pack_info = RConst.copy(p.def);
	    for (String m : p.messages) pack_info = pack_info + " " + m;
	    
	    
	    if (sheet.mmain().loading_delay == 0)
		    hasSend = Math.max(6, PApplet.parseInt( Math.min( 
		    		sheet.mmain().inter.framerate.median_framerate.get(), 
		    		sheet.mmain().inter.framerate.median_framerate.get() / 
		    		sheet.mmain().packpross_by_frame.get()) ));
	    gui.app.plogln(descr+" send");
	    packet_to_send.add(p);
	    sheet.ask_packet_process(this);

	  return this;
  }
  ArrayList<Macro_Packet> packet_to_send = new ArrayList<Macro_Packet>();
  
  boolean process_send() {
	  gui.app.plogln(descr+" process send");
    
    process_resum = ""; 
    boolean flag = packet_to_send.size() == 0;
    if (!flag) process_resum += ">>>"+descr+" send ";
    for (Macro_Packet p : packet_to_send) {
      process_resum = process_resum + p.getText() + " ";
      int prio = 0;
      for (Macro_Connexion m : connected_inputs) 
        if (prio < m.elem.bloc.priority.get() ) prio = m.elem.bloc.priority.get();
//      gui.app.plogln(descr+" max prio "+prio);
      int co_done = 0;
      while (prio >= 0 && co_done <= connected_inputs.size()) {
//    	  gui.app.plogln("try prio "+prio);
        for (Macro_Connexion m : connected_inputs) {
//        	gui.app.plogln("try co "+m.elem.descr+" of prio "+m.elem.bloc.priority.get());
          if (prio == m.elem.bloc.priority.get()) { 
//        	  gui.app.plogln("found"); 
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
  
  Macro_Packet lastPack() { return last_packet; }
  
  void receive(Macro_Connexion s, Macro_Packet p) {
    if (filter == null || p.def.equals(filter) || 
    		(filter_bang_pass && p.def.equals("bang")) || 
        (filter.equals("bin") && (p.def.equals("bool") || p.def.equals("bang"))) ||
        (filter.equals("num") && (p.def.equals("float") || p.def.equals("int"))) ||
        (filter.equals("val") && (p.def.equals("float") || p.def.equals("int") || 
                                  p.def.equals("bool"))) ) {
    	gui.app.plogln(descr+"receive");
      packet_received.add(p);
      packet_sender.add(s);
      sheet.ask_packet_process(this);
    }
  for (Macro_Connexion m : direct_cos) 
    if (m.type == OUTPUT) m.send(p); //m.send(last_packet);
    else 
  	  if (m.type == INPUT) m.receive_from_dir(s, p);

  }
  void receive_from_dir(Macro_Connexion s, Macro_Packet p) {
	    if (filter == null || p.def.equals(filter) || 
	    		(filter_bang_pass && p.def.equals("bang")) || 
	        (filter.equals("bin") && (p.def.equals("bool") || p.def.equals("bang"))) ||
	        (filter.equals("num") && (p.def.equals("float") || p.def.equals("int"))) ||
	        (filter.equals("val") && (p.def.equals("float") || p.def.equals("int") || 
	                                  p.def.equals("bool"))) ) {
	    	  gui.app.plogln(descr+"receive");
	      packet_received.add(p);
	      packet_sender.add(s);
	      sheet.ask_packet_process(this);
	    }
	  }
  ArrayList<Macro_Packet> packet_received = new ArrayList<Macro_Packet>();
  ArrayList<Macro_Connexion> packet_sender = new ArrayList<Macro_Connexion>();
  
  String process_resum = "";
  boolean process_receive() {
	  gui.app.plogln(descr+" process receive");
    
    process_resum = "";
    boolean flag = packet_received.size() == 0;
    if (!flag) process_resum += ">>>"+descr+" receive ";
    
    int prio = 0;
    for (Macro_Connexion m : packet_sender) 
      if (prio < m.elem.bloc.priority.get()) prio = m.elem.bloc.priority.get();
    int co_done = 0;
    while (prio >= 0 && co_done <= packet_received.size()) {
      for (int i = 0 ; i < packet_received.size() ; i++) 
    	  		if (prio == packet_sender.get(i).elem.bloc.priority.get()) { 
          co_done++; 
          last_packet = packet_received.get(i);
          process_resum = process_resum + last_packet.getText() + " ";
          for (nRunnable r : eventReceiveRun) r.run();

          if (last_packet.isBang()) nRunnable.runEvents(eventReceiveBangRun);
          if (last_packet.isBool()) nRunnable.runEvents(eventReceiveBoolRun);
          if (last_packet.isInt()) nRunnable.runEvents(eventReceiveIntRun);
          if (last_packet.isFloat()) nRunnable.runEvents(eventReceiveFloatRun);
          if (last_packet.isStr()) nRunnable.runEvents(eventReceiveStrRun);
          if (last_packet.isVec()) nRunnable.runEvents(eventReceiveVecRun);
          if (last_packet.isCol()) nRunnable.runEvents(eventReceiveColRun);
          
       
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

  ArrayList<nRunnable> eventReceiveBoolRun = new ArrayList<nRunnable>();
  Macro_Connexion addEventReceiveBool(nRunnable r)    { eventReceiveBoolRun.add(r); return this; }

  ArrayList<nRunnable> eventReceiveIntRun = new ArrayList<nRunnable>();
  Macro_Connexion addEventReceiveInt(nRunnable r)    { eventReceiveIntRun.add(r); return this; }

  ArrayList<nRunnable> eventReceiveFloatRun = new ArrayList<nRunnable>();
  Macro_Connexion addEventReceiveFloat(nRunnable r)    { eventReceiveFloatRun.add(r); return this; }

  ArrayList<nRunnable> eventReceiveStrRun = new ArrayList<nRunnable>();
  Macro_Connexion addEventReceiveStr(nRunnable r)    { eventReceiveStrRun.add(r); return this; }

  ArrayList<nRunnable> eventReceiveVecRun = new ArrayList<nRunnable>();
  Macro_Connexion addEventReceiveVec(nRunnable r)    { eventReceiveVecRun.add(r); return this; }

  ArrayList<nRunnable> eventReceiveColRun = new ArrayList<nRunnable>();
  Macro_Connexion addEventReceiveCol(nRunnable r)    { eventReceiveColRun.add(r); return this; }
  
 
  String filter = null;
  boolean filter_bang_pass = false;
  
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
  Macro_Connexion addPassBang() {
	  filter_bang_pass = true;
	    lens.setInfo(infoText+" "+"bang-"+filter);
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
  Macro_Connexion setFilterString() { //int and float
	    filter = "str";
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
  Macro_Connexion setFilterColor() { //bool int and float
	    filter = "col";
	    lens.setInfo(infoText+" "+filter);
	    return this; }
  

	Macro_Connexion addBangGet(sValue v, Macro_Connexion out) {
		addPassBang();
		nObjectPair pout = new nObjectPair();
		pout.obj1 = v; pout.obj2 = out;
		addEventReceiveBang(new nRunnable(pout) { public void run() {
			nObjectPair pair = ((nObjectPair)builder);
	    		((Macro_Connexion)pair.obj2).send(((sValue)pair.obj1).asPacket());
	    }});
		return this;
	}
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

