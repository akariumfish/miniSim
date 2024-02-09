package Macro;

import RApplet.RConst;
import UI.*;
import processing.core.PVector;
import sData.*;

public class MValue extends MBasic { 
  static 
  class MValue_Builder extends MAbstract_Builder {
      MValue_Builder() { super("svalue", "send selected value as packet"); show_in_buildtool = true; }
      MValue build(Macro_Sheet s, sValueBloc b) { MValue m = new MValue(s, b); return m; }
    }
  sBoo setup_send;
  sValue cible;
  sValueBloc bloc_cible;
  sStr val_cible, val_bloc, val_depth; 
  
  sStr var_type;
  sBoo vint; sBoo vflt; sBoo vboo; 
  sBoo vstr; 

  sBoo bval; sInt ival; sFlt fval; sStr sval; sVec vval; sRun rval; sCol cval; sObj oval;
  
  nRunnable set_type_run;
  
  nRunnable get_run;
  Macro_Connexion in, out;
  nCtrlWidget picker, block_picker, pop_pan;
  nWatcherWidget val_field, search_bloc_field, ref_field;
  nLinkedWidget val_lnk_field;
  MValue(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "svalue", _bloc); }

  void init() {
      val_cible = newStr("cible", "cible", "");
      val_bloc = newStr("bloc", "bloc", "");
      val_depth = newStr("depth", "depth", "/");
      var_type = newStr("type", "type", "");
      setup_send = newBoo("stp_snd", "stp_snd", false);
      vint = newBoo("vint", "vint", false);
      vflt = newBoo("vflt", "vflt", false);
      vboo = newBoo("vboo", "vboo", false);
      vstr = newBoo("vstr", "vstr", false);

      bval = newBoo("bval", "bval", false);
      ival = newInt("ival", "ival", 0);
      fval = newFlt("fval", "fval", 0);
      sval = newStr("sval", "sval", "");

      get_run = new nRunnable() { public void run() {
        if (cible != null) out.send(cible.asPacket());
      }};
      set_type_run = new nRunnable() { public void run() {
          if (vint.get() && !var_type.get().equals("int")) {
	        	  var_type.set("int");
	        	  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	        		  rebuild(); } } );
          } else if (vflt.get() && !var_type.get().equals("flt")) {
	        	  var_type.set("flt");
	        	  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	        		  rebuild(); } } );
	      } else if (vboo.get() && !var_type.get().equals("boo")) {
	        	  var_type.set("boo");
	        	  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	        		  rebuild(); } } );
	      } else if (vstr.get() && !var_type.get().equals("str")) {
	        	  var_type.set("str");
	        	  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
	        		  rebuild(); } } );
	      } else get_cible();
        }};
  }
  void build_param() { 
	  addSelectL(0, vint, vflt, vboo, vstr, 
              "int", "flt", "boo", "str");
		nDrawer d = addEmptyS(2);
	  	d.addCtrlModel("MC_Element_SButton", "Send")
	  			.setRunnable(new nRunnable() { public void run() {
	  				get_run.run(); } });
	  	d.addLinkedModel("MC_Element_MiniButton", "st")
			.setLinkedValue(setup_send);

	    addEmpty(1);
	    init_co_row();

//		in = addInput(0, "in", new nRunnable() { public void run() {
//			if (in.lastPack().isBang() && cible != null) {
//				out.send(cible.asPacket());
//			} else if (cible != null) {
//				if (cible.isRun() && in.lastPack().isBang()) 
//					((sRun)cible).run();
//				if (cible.isBool() && in.lastPack().isBool()) 
//					((sBoo)cible).set(in.lastPack().asBool());
//				if (cible.isInt() && in.lastPack().isInt()) 
//					((sInt)cible).set(in.lastPack().asInt());
//				if (cible.isFloat() && in.lastPack().isFloat()) 
//					((sFlt)cible).set(in.lastPack().asFloat());
//				if (cible.isStr() && in.lastPack().isStr()) 
//					((sStr)cible).set(in.lastPack().asStr());
//				if (cible.isCol() && in.lastPack().isCol()) 
//					((sCol)cible).set(in.lastPack().asCol());
//				if (cible.isVec() && in.lastPack().isVec()) 
//					((sVec)cible).set(in.lastPack().asVec());
//				if (cible.isObj() && in.lastPack().isObj()) 
//					((sObj)cible).set(in.lastPack().asObj());
//			}
//		}});
	    
	    addEmpty(1);
	    addEmpty(2);
	    val_lnk_field = addEmptyXL(0).addLinkedModel("MC_Element_LText");
	    
	    vint.addEventChange(set_type_run);
	    vflt.addEventChange(set_type_run);
	    vboo.addEventChange(set_type_run);
	    vstr.addEventChange(set_type_run);
	    
	  set_type_run.run();
      if (setup_send.get()) get_run.run();
      
      get_cible();
  }

  
  void get_cible() {
	  if (!param_view.get()) {
	    if (val_field != null) val_field.setLook(gui.theme.getLook("MC_Element_Text")).setText("");
	    bloc_cible = value_bloc;
	    if (val_depth.get().equals("/")) { 
	    		bloc_cible = sheet.value_bloc.getBloc(val_bloc.get());
	    } else if (val_depth.get().equals("..") && sheet != mmain()) { 
			bloc_cible = sheet.sheet.value_bloc.getBloc(val_bloc.get());
	    } 
	    
	    if (bloc_cible != null) bloc_cible = bloc_cible.getBloc(val_bloc.get());
	    
		if (bloc_cible != null) {
		    cible = bloc_cible.getValue(val_cible.get());
		    if (val_field != null && cible != null) {
		      val_field.setLinkedValue(cible).setLook(gui.theme.getLook("MC_Element_Field"));
			    setValue(cible);
		    }
		}
	    
	  } else {
		  if (val_field != null) val_field.setLook(gui.theme.getLook("MC_Element_Text")).setText("");
		  
		  if (var_type.get().equals("int")) {
			  cible = ival;
			  val_lnk_field.setFloatPrecision(9);
          } else if (var_type.get().equals("flt")) {
			  cible = fval;
			  val_lnk_field.setFloatPrecision(9);
	      } else if (var_type.get().equals("boo")) {
			  cible = bval;
	      } else if (var_type.get().equals("str")) {
			  cible = sval;
	      } 
	    if (val_lnk_field != null && cible != null) {
	    		val_lnk_field.setLinkedValue(cible)
	    			.setLook(gui.theme.getLook("MC_Element_Field"));

	    	    setValue(cible);
	    }
		  
	  }
  }

  void setValue(sValue v) {
	  if (cible != null && val_run != null) cible.removeEventChange(val_run);
	  if (cible != null && val_run != null) cible.removeEventAllChange(val_run);
    if (in_run != null) in.removeEventReceive(in_run);
    in_run = null;
    cible = null;
    val_run = null;
    if (v != null) {
	    val_cible.set(v.ref);
	    cible = v; 
	    if (cible.type.equals("flt")) setValue((sFlt)cible);
	    if (cible.type.equals("int")) setValue((sInt)cible);
	    if (cible.type.equals("boo")) setValue((sBoo)cible);
	    if (cible.type.equals("str")) setValue((sStr)cible);
	    if (cible.type.equals("run")) setValue((sRun)cible);
	    if (cible.type.equals("vec")) setValue((sVec)cible);
	    if (cible.type.equals("col")) setValue((sCol)cible);
	    if (cible.type.equals("obj")) setValue((sObj)cible);
    }
  }
  void build_normal() { 
		nDrawer d = addEmptyS(0);
	  	block_picker = d.addCtrlModel("MC_Element_SButton", "pick")
	  			.setRunnable(new nRunnable() { public void run() {
	  				
	  				if (val_depth.get().equals("/")) { 
  			    		bloc_cible = sheet.value_bloc;
	  			    } else if (val_depth.get().equals("..") && sheet != mmain()) { 
	  					bloc_cible = sheet.sheet.value_bloc;
	  			    }
  			    
	  			    if (bloc_cible != null && bloc_cible.getBloc(val_bloc.get()) != null) 
	  			    		bloc_cible = bloc_cible.getBloc(val_bloc.get());
	  			    
	  				if (bloc_cible != null) {
				        	if (bloc_cible.parent == sheet.value_bloc) val_depth.set("/");
				        	else if (bloc_cible.parent == sheet.sheet.value_bloc) val_depth.set("..");
				        	
				        	if (bloc_cible.parent == sheet.value_bloc || 
					        		bloc_cible.parent == value_bloc || 
					        		bloc_cible.parent == sheet.sheet.value_bloc) 
						      new nBlockPicker(mmain().screen_gui, mmain().inter.taskpanel, val_bloc, bloc_cible, true)
						        .addEventChoose(new nRunnable() { public void run() { 
						        	  mmain().inter.addEventNextFrame(
						        			  	new nRunnable() { public void run() {
						        		  rebuild(); } } ); 	} } );
	  				}
	  			} });
	  	d.addCtrlModel("MC_Element_MiniButton", "/")
			.setRunnable(new nRunnable() { public void run() {
				if (!val_depth.get().equals("/")) {
					val_depth.set("/");
		        	  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		        		  rebuild(); } } );
				} else if (sheet != mmain() && !val_depth.get().equals("..")) {
					val_depth.set("..");
		        	  mmain().inter.addEventNextFrame(new nRunnable() { public void run() {
		        		  rebuild(); } } );
				}
			} });
	    addEmpty(2);
	    search_bloc_field = addEmptyL(1).addWatcherModel("MC_Element_Field")
	    		.setLinkedValue(val_bloc);
	    search_bloc_field.getDrawer().addWatcherModel("MC_Element_SText")
			.setLinkedValue(val_depth).setPX(-ref_size*0.625F).setSX(ref_size*1.0F);
	    picker = addEmptyS(0).addCtrlModel("MC_Element_SButton", "pick")
	    		.setRunnable(new nRunnable() { public void run() {
	    			if (bloc_cible != null ) {
		      new nValuePicker(mmain().screen_gui, mmain().inter.taskpanel, val_cible, bloc_cible, true)
		        .addEventChoose(new nRunnable() { public void run() { get_cible(); } } );
	    			}
	    } });
	    addEmpty(2);
	    ref_field = addEmptyL(1).addWatcherModel("MC_Element_Field")
	    		.setLinkedValue(val_cible);
			    
	    init_co_row();

	    addEmpty(1);
	    addEmpty(2);
    		val_field = addEmptyXL(0).addWatcherModel("MC_Element_LText");

      get_cible();
      if (setup_send.get()) get_run.run();
    }
  
  	  void init_co_row() {
  		pop_pan = addEmptyS(1).addCtrlModel("MC_Element_SButton", "Set")
		    .setRunnable(new nRunnable() { public void run() {
		      if (cible != null) {
		        if (cible.type.equals("str")) { 
		          new nTextPanel(mmain().screen_gui, mmain().inter.taskpanel, (sStr)cible);
		        } else if (cible.type.equals("flt")) { 
		          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sFlt)cible);
		        } else if (cible.type.equals("int")) { 
		          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sInt)cible);
		        } else if (cible.type.equals("boo")) { 
		          new nBinPanel(mmain().screen_gui, mmain().inter.taskpanel, (sBoo)cible);
		        } else if (cible.type.equals("col")) { 
		          new nColorPanel(mmain().screen_gui, mmain().inter.taskpanel, (sCol)cible);
		        }
		      }
		    } }); 
			out = addOutput(2, "out");
		    in = addInput(0, "in");
  	  }
  	  
  	  nRunnable in_run, val_run;
  	  boolean btmp; int itmp; float ftmp; String stmp = ""; 
  	PVector vtmp = new PVector(); int ctmp = 0;

	  void setValue(sRun v) {
	    rval = v;
	    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketBang()); }};
	    in_run = new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isBang()) { 
	        rval.doEvent(false); 
	        rval.run(); 
	        rval.doEvent(true); 
	      }
	    } };
	    v.addEventAllChange(val_run);
	    in.addEventReceive(in_run);
	  }
	  void setValue(sFlt v) {
	    fval = v;
	    out.send(Macro_Packet.newPacketFloat(v.get()));
	    ftmp = v.get();
	    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketFloat(fval.get())); }};
	    in_run = new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isFloat() && ftmp != in.lastPack().asFloat()) { 
	        ftmp = in.lastPack().asFloat();
	        fval.set(in.lastPack().asFloat()); }
	    } };
	    v.addEventChange(val_run);
	    in.addEventReceive(in_run);
	    mmain().inter.addEventNextFrame(val_run); 
	  }
	  void setValue(sInt v) {
	    ival = v;
	    out.send(Macro_Packet.newPacketInt(v.get()));
	    itmp = v.get();
	    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketInt(ival.get())); }};
	    in_run = new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isInt() && itmp != in.lastPack().asInt()) { 
	        itmp = in.lastPack().asInt();
	        ival.set(in.lastPack().asInt()); }
	    } };
	    v.addEventChange(val_run);
	    in.addEventReceive(in_run);
	    mmain().inter.addEventNextFrame(val_run); 
	  }
	  void setValue(sBoo v) {
	    bval = v;
	    out.send(Macro_Packet.newPacketBool(v.get()));
	    btmp = v.get();
	    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketBool(bval.get())); }};
	    in_run = new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isBool() && !(btmp == in.lastPack().asBool())) { 
	        btmp = in.lastPack().asBool();
	        bval.set(in.lastPack().asBool()); }
	      if (in.lastPack() != null && in.lastPack().isBang()) { 
	        btmp = !btmp;
	        bval.set(!bval.get()); }
	    } };
	    v.addEventChange(val_run);
	    in.addEventReceive(in_run);
	    mmain().inter.addEventNextFrame(val_run); 
	  }
	  void setValue(sStr v) {
	    sval = v;
	    out.send(Macro_Packet.newPacketStr(v.get()));
	    stmp = RConst.copy(v.get());
	    val_run = new nRunnable() { public void run() { out.send(Macro_Packet.newPacketStr(sval.get())); }};
	    in_run = new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isStr() && !stmp.equals(in.lastPack().asStr())) { 
	        stmp = RConst.copy(in.lastPack().asStr());
	        sval.set(in.lastPack().asStr()); }
	    } };
	    v.addEventChange(val_run);
	    in.addEventReceive(in_run);
	    mmain().inter.addEventNextFrame(val_run); 
	  }

	  void setValue(sVec v) {
	    vval = v;
	    out.send(Macro_Packet.newPacketVec(v.get()));
	    vtmp.set(v.get());
	    val_run = new nRunnable() { public void run() { 
	      mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
	      out.send(Macro_Packet.newPacketVec(vval.get())); }}); 
	      }};
	    in_run = new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isVec() && !in.lastPack().equalsVec(vtmp)) { 
	        vtmp.set(in.lastPack().asVec());
	        vval.set(in.lastPack().asVec()); 
	      }
	    } };
	    v.addEventChange(val_run);
	    in.addEventReceive(in_run);
	    mmain().inter.addEventNextFrame(val_run); 
	  }
	  void setValue(sCol v) {
	    cval = v;
	    out.send(Macro_Packet.newPacketCol(v.get()));
	    ctmp = v.get();
	    val_run = new nRunnable() { public void run() { 
	      mmain().inter.addEventNextFrame(new nRunnable() { public void run() { 
	      out.send(Macro_Packet.newPacketCol(cval.get()));}}); 
	      }};
	    in_run = new nRunnable() { public void run() { 
	      if (in.lastPack() != null && in.lastPack().isCol() && !in.lastPack().equalsCol(ctmp)) { 
	        ctmp = in.lastPack().asCol();
	        cval.set(in.lastPack().asCol()); 
	      }
	    } };
	    v.addEventChange(val_run);
	    in.addEventReceive(in_run);
	    mmain().inter.addEventNextFrame(val_run); 
	  }
	  public MValue clear() {
		    if (val_run != null && cible != null) cible.removeEventChange(val_run);
		    if (val_run != null && cible != null) cible.removeEventAllChange(val_run);
	    super.clear(); 
	    return this; }
	  public MValue toLayerTop() {
	    super.toLayerTop(); 
	    return this; }
	}
