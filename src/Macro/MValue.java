package Macro;

import UI.nBoolPanel;
import UI.nColorPanel;
import UI.nCtrlWidget;
import UI.nNumPanel;
import UI.nTextPanel;
import UI.nValuePicker;
import UI.nWatcherWidget;
import sData.nRunnable;
import sData.sBoo;
import sData.sCol;
import sData.sFlt;
import sData.sInt;
import sData.sStr;
import sData.sValue;
import sData.sValueBloc;

public class MValue extends MBasic { 
	  static 
	  class MValue_Builder extends MAbstract_Builder {
	      MValue_Builder() { super("svalue", "send selected value as packet"); show_in_buildtool = true; }
	      MValue build(Macro_Sheet s, sValueBloc b) { MValue m = new MValue(s, b); return m; }
	    }
	  nRunnable get_run;
	  Macro_Connexion in, out;
	  sStr val_cible; 
	  sBoo setup_send;
	  sValue cible;
	  nCtrlWidget picker, pop_pan;
	  nWatcherWidget val_field;
	  MValue(Macro_Sheet _sheet, sValueBloc _bloc) { super(_sheet, "svalue", _bloc); }
	  void init() {
	      val_cible = newStr("cible", "cible", "");
	      setup_send = newBoo("stp_snd", "stp_snd", false);
	      get_cible();
	      get_run = new nRunnable() { public void run() {
	        if (cible != null) out.send(cible.asPacket());
	      }};
	      if (setup_send.get()) get_run.run();
	  }
	  void build_normal() { 
	      addTrigSwtchS(1, "st", setup_send, "get", get_run);
	      in = addInputBang(0, "get", get_run);
	      //addEmptyS(1);
	      out = addOutput(1, "out");
	  }

	  void build_param() { 
	    Macro_Element e = addEmptyS(0);
	    picker = e.addCtrlModel("MC_Element_SButton", "pick").setRunnable(new nRunnable() { public void run() {
	      new nValuePicker(mmain().screen_gui, mmain().inter.taskpanel, val_cible, sheet.value_bloc, true)
	        .addEventChoose(new nRunnable() { public void run() { get_cible(); } } );
	    } });
	    pop_pan = picker.getDrawer().addCtrlModel("MC_Element_MiniButton", "p")
	    .setRunnable(new nRunnable() { public void run() {
	      if (cible != null) {
	        if (cible.type.equals("str")) { 
	          new nTextPanel(mmain().screen_gui, mmain().inter.taskpanel, (sStr)cible);
	        } else if (cible.type.equals("flt")) { 
	          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sFlt)cible);
	        } else if (cible.type.equals("int")) { 
	          new nNumPanel(mmain().screen_gui, mmain().inter.taskpanel, (sInt)cible);
	        } else if (cible.type.equals("boo")) { 
	          new nBoolPanel(mmain().screen_gui, mmain().inter.taskpanel, (sBoo)cible);
	        } else if (cible.type.equals("col")) { 
	          new nColorPanel(mmain().screen_gui, mmain().inter.taskpanel, (sCol)cible);
	        }
	      }
	    } }); 
	    addEmpty(1);
	    val_field = addEmptyL(0).addWatcherModel("MC_Element_Text");
	    addEmpty(1);
	  }
	  void get_cible() {
	    if (val_field != null) val_field.setLook(gui.theme.getLook("MC_Element_Text")).setText("");
	    cible = sheet.value_bloc.getValue(val_cible.get());
	    if (val_field != null && cible != null) {
	      val_field.setLinkedValue(cible).setLook(gui.theme.getLook("MC_Element_Field"));
	    }
	  }
	  public MValue clear() {
	    super.clear(); 
	    return this; }
	  public MValue toLayerTop() {
	    super.toLayerTop(); 
	    return this; }
	}
