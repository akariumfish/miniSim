package UI;

import RApplet.RConst;
import RApplet.Rapp;
import processing.core.PApplet;
import sData.nRunnable;
import sData.sVec;

public class nVecPanel extends nWindowPanel {
  sVec val;
  
  nWidget field1_widget, field2_widget;
  nRunnable field1_run, val_run, field2_run;
  
  public nVecPanel(nGUI _g, nTaskPanel _task, sVec _cv) { 
    super(_g, _task, "vec "+_cv.bloc.ref + " " + _cv.ref); 
    val = _cv; if (val == null) clear();

    field1_run = new nRunnable() { public void run() { 
      String s = field1_widget.getText();
      if (s.length() > 0 && !PApplet.str(Rapp.parseFlt(s)).equals("NaN")) 
        val.setx(Rapp.parseFlt(s)); 
    } };
    field2_run = new nRunnable() { public void run() { 
        String s = field2_widget.getText();
        if (s.length() > 0 && !PApplet.str(Rapp.parseFlt(s)).equals("NaN")) 
          val.sety(Rapp.parseFlt(s)); 
      } };
    
    getShelf().addDrawer(10.25F, 1).getShelf()
      .addDrawer(10.25F, 1).getShelf()
      .addSeparator(0.5)
      .addDrawer(10.25F, 1).addCtrlModel("Button-S2-P3", "OK")
        .setRunnable(new nRunnable() { public void run() { clear(); } }).getDrawer();
    
    field1_widget = getDrawer(0,0).addModel("Field-S4");
    field1_widget.setText(RConst.trimFlt(val.x(), 10)).setField(true)
      .addEventFieldChange(field1_run);
    field2_widget = getDrawer(0,1).addModel("Field-S4");
    field2_widget.setText(RConst.trimFlt(val.y(), 10)).setField(true)
      .addEventFieldChange(field2_run);
    val_run = new nRunnable() { public void run() { 
		  field1_widget.changeText(RConst.trimFlt(val.x(), 10));
		  field2_widget.changeText(RConst.trimFlt(val.y(), 10));
    } };
    
    val.addEventChange(val_run);
    
  }
  public nWindowPanel clear() { 
    val.removeEventChange(val_run);
    super.clear(); return this; }
  public nWindowPanel updateHeight() { 
    super.updateHeight(); return this; }
  public nWindowPanel updateWidth() { 
    super.updateWidth(); return this; }
}
