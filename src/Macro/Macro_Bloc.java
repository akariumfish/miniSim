package Macro;

import java.util.ArrayList;

import RBase.RConst;
import UI.nCtrlWidget;
import UI.nLinkedWidget;
import UI.nWidget;
import processing.core.PApplet;
import sData.nRunnable;
import sData.sBoo;
import sData.sValueBloc;

/*

 bloc extend abstract
 shelfpanel of element
 methods to add and manipulate element for easy macro building
 
 */
public class Macro_Bloc extends Macro_Abstract {
  Macro_Bloc(Macro_Sheet _sheet, String t, String n, sValueBloc _bloc) {
    super(_sheet, t, n, _bloc);
//    mlogln("build bloc "+t+" "+n+" "+ _bloc);
    addShelf(); 
    addShelf();
  }

  Macro_Element addSelectS(int c, sBoo v1, sBoo v2, String s1, String s2) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    nWidget w1 = m.addLinkedModel("MC_Element_Button_Selector_1", s1).setLinkedValue(v1);
    nWidget w2 = m.addLinkedModel("MC_Element_Button_Selector_2", s2).setLinkedValue(v2);
    w1.addExclude(w2);
    w2.addExclude(w1);
    return m;
  }
  

  Macro_Element addSelectL(int c, sBoo v1, sBoo v2, sBoo v3, sBoo v4, 
                           String s1, String s2, String s3, String s4) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Double", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    nWidget w1 = m.addLinkedModel("MC_Element_Button_Selector_1", s1).setLinkedValue(v1);
    nWidget w2 = m.addLinkedModel("MC_Element_Button_Selector_2", s2).setLinkedValue(v2);
    nWidget w3 = m.addLinkedModel("MC_Element_Button_Selector_3", s3).setLinkedValue(v3);
    nWidget w4 = m.addLinkedModel("MC_Element_Button_Selector_4", s4).setLinkedValue(v4);
    w1.addExclude(w2).addExclude(w3).addExclude(w4);
    w2.addExclude(w1).addExclude(w3).addExclude(w4);
    w3.addExclude(w2).addExclude(w1).addExclude(w4);
    w4.addExclude(w2).addExclude(w3).addExclude(w1);
    return m;
  }
  
  Macro_Element addEmptyS(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    return m;
  }
  nCtrlWidget addTrigS(int c, String l, nRunnable r) { 
	    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
	    addElement(c, m); 
	    return m.addCtrlModel("MC_Element_SButton", l).setRunnable(r);
	  }
  nLinkedWidget addSwitchS(int c, String l, sBoo r) { 
	    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
	    addElement(c, m); 
	    return m.addLinkedModel("MC_Element_SButton", l).setLinkedValue(r);
	  }
  nCtrlWidget addTrigSwtchS(int c, String sw_txt, sBoo vb, String bp_txt, nRunnable r) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    nCtrlWidget cw = m.addCtrlModel("MC_Element_SButton", bp_txt).setRunnable(r);
    m.addLinkedModel("MC_Element_MiniButton", sw_txt).setLinkedValue(vb);
    return cw;
  }
  Macro_Element addEmptyL(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Double", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m;
  }
  Macro_Element addEmptyXL(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Triple", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m;
  }
  Macro_Element addEmptyB(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Big", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m;
  }
  Macro_Element addEmptyXB(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Bigger", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m;
  }
  nWidget addEmpty(int c) { 
    Macro_Element m = new Macro_Element(this, "", "mc_ref", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m.back;
  }

  nWidget addFillR(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Fillright", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m.back;
  }
  nWidget addFillL(int c) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Fillleft", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m.back;
  }

  nWidget addLabelS(int c, String t) { 
    Macro_Element m = new Macro_Element(this, t, "MC_Element_Single", null, NO_CO, NO_CO, true);
    addElement(c, m); 
    return m.back;
  }
  nWidget addLabelL(int c, String t) { 
    Macro_Element m = new Macro_Element(this, t, "MC_Element_Double", null, NO_CO, NO_CO, false);
    addElement(c, m); 
    return m.back;
  }

  Macro_Connexion addInput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, INPUT, INPUT, true);
    if (m.sheet_connect != null) m.sheet_connect.direct_connect(m.connect);
    addElement(c, m); 
    return m.connect;
  }
  Macro_Connexion addOutput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, OUTPUT, OUTPUT, true);
    if (m.sheet_connect != null) m.connect.direct_connect(m.sheet_connect);
    addElement(c, m); 
    return m.connect;
  }

  Macro_Connexion addInput(int c, String t, nRunnable r) { 
    return addInput(c, t).addEventReceive(r);
  }
  Macro_Connexion addOutput(int c, String t, nRunnable r) { 
    return addOutput(c, t).addEventReceive(r);
  }
  
  Macro_Connexion addInputBang(int c, String t, nRunnable r) { 
    return addInput(c, t).setFilterBang().addEventReceiveBang(r); }
  
  Macro_Element addSheetInput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, OUTPUT, INPUT, true);
    if (m.sheet_connect != null) m.sheet_connect.direct_connect(m.connect);
    addElement(c, m); 
    return m;
  }
  Macro_Element addSheetOutput(int c, String t) { 
    Macro_Element m = new Macro_Element(this, "", "MC_Element_Single", t, INPUT, OUTPUT, true);
    if (m.sheet_connect != null) m.connect.direct_connect(m.sheet_connect);
    addElement(c, m); 
    return m;
  }


  Macro_Element addElement(int c, Macro_Element m) {
    if (c >= 0 && c < 3) {
      if (c == 2 && shelfs.size() < 3) addShelf();
      elements.add(m);
      getShelf(c).insertDrawer(m);
      if (c == 0 && getShelf(c).drawers.size() == 1) getShelf(c).getDrawer(0).ref.setPX(-ref_size*0.0);
      if (c == 1 && getShelf(c).drawers.size() == 1) getShelf(c).getDrawer(0).ref.setPX(ref_size*0.5);
      if (c == 2 && getShelf(c).drawers.size() == 1) getShelf(c).getDrawer(0).ref.setPX(ref_size);
      if (openning.get() == OPEN) for (Macro_Element e : elements) e.show();
      toLayerTop();
      return m;
    } else return null;
  }
  
  String resum_link() { 
    String r = "";
    for (Macro_Element m : elements) {
      if (m.connect != null) for (Macro_Connexion co : m.connect.connected_inputs) 
        r += co.descr + INFO_TOKEN + m.connect.descr + OBJ_TOKEN;
      if (m.connect != null) for (Macro_Connexion co : m.connect.connected_outputs) 
        r += m.connect.descr + INFO_TOKEN + co.descr + OBJ_TOKEN;
      //if (m.sheet_connect != null) for (Macro_Connexion co : m.sheet_connect.connected_inputs) 
      //  r += co.descr + INFO_TOKEN + m.sheet_connect.descr + OBJ_TOKEN;
      //if (m.sheet_connect != null) for (Macro_Connexion co : m.sheet_connect.connected_outputs) 
      //  r += m.sheet_connect.descr + INFO_TOKEN + co.descr + OBJ_TOKEN;
    }
    return r; 
  }
  String resum_spot(String spots) { 
    String[] spots_side_list = PApplet.splitTokens(spots, GROUP_TOKEN);
    String left_s = OBJ_TOKEN, right_s = OBJ_TOKEN;
    if (spots_side_list.length == 2) { 
      left_s = RConst.copy(spots_side_list[0]); right_s = RConst.copy(spots_side_list[1]); }
    
    for (Macro_Element m : elements) {
      if (m.spot != null && m.side.equals("left")) 
        left_s += m.descr + OBJ_TOKEN;
      if (m.spot != null && m.side.equals("right")) 
        right_s += m.descr + OBJ_TOKEN;
    }
    return left_s + GROUP_TOKEN + right_s; 
  }
  
  ArrayList<Macro_Element> elements = new ArrayList<Macro_Element>();
  public Macro_Bloc toLayerTop() { 
    super.toLayerTop(); 
    for (Macro_Element e : elements) e.toLayerTop(); 
    for (Macro_Element e : elements) if (e.spot != null) e.toLayerTop(); 
    grabber.toLayerTop(); 
    return this;
  }
  public Macro_Bloc clear() {
    for (Macro_Element e : elements) e.clear();
    super.clear(); return this; }

  Macro_Bloc open() {
    super.open();
    for (Macro_Element m : elements) m.show();
    toLayerTop();
    return this;
  }
  Macro_Bloc reduc() {
    super.reduc();
    for (Macro_Element m : elements) m.reduc();
    toLayerTop();
    return this;
  }
  Macro_Bloc show() {
    super.show();
    for (Macro_Element m : elements) m.show();
    toLayerTop();
    return this;
  }
  Macro_Bloc hide() {
    super.hide(); 
    for (Macro_Element m : elements) m.hide();
    //toLayerTop();
    return this;
  }
}





