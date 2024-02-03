package UI;

import java.util.ArrayList;

import RApplet.Rapp;
import RBase.RConst;
import processing.core.PApplet;
import sData.*;

public class nConstructor {
  nTheme theme; 
  Rapp app;
  float ref_size = 30;
  
  nConstructor(nTheme _g, float s) {
    theme = _g; ref_size = s; app = _g.app;
    theme.addModel("ref", new nWidget(_g.app)
      //.setPassif()
      .setLabelColor(app.color(200, 200, 200))
      .setFont((int)(ref_size/1.6))
      );
    theme.addModel("Hard_Back", theme.newWidget("ref")
      .setStandbyColor(app.color(50))
      .setOutlineColor(app.color(90))
      .setOutlineWeight(ref_size / 16)
      .setOutline(true)
      );
    theme.addModel("Soft_Back", theme.newWidget("ref")
      .setStandbyColor(app.color(60, 100))
      .setOutlineColor(app.color(80))
      .setOutlineWeight(ref_size / 8)
      .setOutline(true)
      );
    theme.addModel("Label", theme.newWidget("ref")
      .setStandbyColor(app.color(255, 0))
      );
    theme.addModel("Label_Small_Text", theme.newWidget("Label")
      .setFont((int)(ref_size/2.1))
      );
    theme.addModel("Label_Back", theme.newWidget("ref")
      .setStandbyColor(app.color(55))
      );
    theme.addModel("Label_HightLight_Back", theme.newWidget("ref")
      .setStandbyColor(app.color(210, 190, 30))
      .setLabelColor(app.color(90, 80, 50))
      .setFont((int)(ref_size/2.1))
      );
    theme.addModel("Label_DownLight_Back", theme.newWidget("ref")
      .setStandbyColor(app.color(70, 10, 10))
      .setFont((int)(ref_size/2.1))
      );
    theme.addModel("Button", theme.newWidget("ref")
      .setStandbyColor(app.color(80))
      .setHoveredColor(app.color(110))
      .setClickedColor(app.color(130))
      );
    theme.addModel("Button_Small_Text", theme.newWidget("Button")
      .setFont((int)(ref_size/2.2))
      );
    theme.addModel("Menu_Button", theme.newWidget("Button")
      .setStandbyColor(app.color(80, 90, 80))
      .setHoveredColor(app.color(110, 120, 110))
      .setClickedColor(app.color(140, 150, 140))
      );
    theme.addModel("Head_Button", theme.newWidget("Button")
      .setStandbyColor(app.color(80, 90, 80))
      .setHoveredColor(app.color(110, 120, 110))
      .setClickedColor(app.color(120, 130, 120))
      );
    theme.addModel("Auto_Button", theme.newWidget("Button")
      .setFont((int)(ref_size/1.9))
      .setStandbyColor(app.color(20, 100, 15))
      .setHoveredColor(app.color(120, 180, 120))
      .setClickedColor(app.color(30, 150, 25))
      );
    theme.addModel("Auto_Ctrl_Button", theme.newWidget("Auto_Button")
      .setFont((int)(ref_size/2.2))
      );
    theme.addModel("Auto_Watch_Label", theme.newWidget("ref")
      .setStandbyColor(app.color(5, 55, 10))
      .setFont((int)(ref_size/2.2))
      );
    theme.addModel("Button_Check", theme.newWidget("ref")
      .setStandbyColor(app.color(20))
      .setOutlineColor(app.color(255, 120))
      .setOutlineWeight(ref_size / 8)
      .setOutline(true)
      );
    theme.addModel("Field", theme.newWidget("ref")
      .setStandbyColor(app.color(20))
      .setOutlineColor(app.color(255, 120))
      .setOutlineSelectedColor(app.color(255, 120))
      .setOutlineWeight(ref_size / 10)
      );
    theme.addModel("Cursor", theme.newWidget("ref")
      .setStandbyColor(app.color(255, 0))
      .setHoveredColor(app.color(255, 120))
      .setClickedColor(app.color(255, 60))
      .setOutlineColor(app.color(120))
      .setOutlineWeight(ref_size / 10)
      .setOutline(true)
      .setOutlineConstant(true)
      );
    theme.addModel("Pointer", theme.newWidget("ref")
      .setStandbyColor(app.color(120))
      .setHoveredColor(app.color(70))
      .setClickedColor(app.color(220))
      .setOutlineColor(app.color(70))
      .setOutlineWeight(ref_size / 10)
      .setOutline(true)
      .setOutlineConstant(true)
      );
    theme.addModel("List_Entry", theme.newWidget("ref")
      .setStandbyColor(app.color(10, 80, 90))
      .setHoveredColor(app.color(20, 90, 130))
      .setClickedColor(app.color(25, 100, 170))
      .setOutlineWeight(ref_size / 40)
      .setOutline(true)
      .setOutlineColor(app.color(40, 40, 140))
      );
    theme.addModel("List_Entry_Selected", theme.newWidget("ref")
      .setStandbyColor(app.color(10, 100, 130))
      .setHoveredColor(app.color(20, 110, 150))
      .setClickedColor(app.color(30, 115, 175))
      .setOutlineWeight(ref_size / 10)
      .setOutline(true)
      .setOutlineColor(app.color(100, 170, 210))
      );
      
    make_outline("Button");
    make_outline("Menu_Button");
    make_outline("Head_Button");
    make_outline("Auto_Ctrl_Button");
    make_outline("Label");
    make_outline("Label_Small_Text");
    make_outline("Label_Back");
    make_outline("Auto_Watch_Label");
    make_outline("Auto_Button");
    make_outline("Label_HightLight_Back");
    make_outline("Button_Small_Text");
    make_outline("Label_DownLight_Back");
    make("Label_DownLight_Back");
    make("Button_Small_Text");
    make("Label_HightLight_Back");
    make("Auto_Button");
    make("Label");
    make("Label_Small_Text");
    make("Button");
    make("Menu_Button");
    make("Head_Button");
    make("Auto_Ctrl_Button");
    make("Label_Back");
    make("Auto_Watch_Label");
    make("Button_Check");
    make("Field");
    make("Cursor");
  }
  void make_outline(String base) {
    theme.addModel(base+"_Outline", theme.newWidget(base)
      .setOutlineColor(app.color(90))
      .setOutlineWeight(ref_size / 8)
      .setOutline(true)
      );
    theme.addModel(base+"_Highlight_Outline", theme.newWidget(base)
      .setOutlineColor(app.color(190, 150, 30))
      .setOutlineWeight(ref_size / 6)
      .setOutline(true)
      );
    theme.addModel(base+"_Downlight_Outline", theme.newWidget(base)
      .setOutlineColor(app.color(100, 100, 100))
      .setOutlineWeight(ref_size / 10)
      .setOutline(true)
      );
    theme.addModel(base+"_Small_Outline", theme.newWidget(base+"_Outline")
      .setOutlineWeight(ref_size / 12)
      );
      
    make(base+"_Outline");
    make(base+"_Highlight_Outline");
    make(base+"_Downlight_Outline");
    make(base+"_Small_Outline");
  }
  void do_sizes(String base, String post, float w, float h) {
    theme.addModel(base+post, theme.newWidget(base).setSize(w, h));}
  void do_places(String base, String post, float x, float y, float w, float h) {
    theme.addModel(base+post, theme.newWidget(base).setSize(w, h).setPosition(x, y));}
  
  float[] sizes_val = { 0.5F, 0.8F, 1F, 1.25F, 1.5F, 2F, 2.5F, 4F, 8F };
  
  void make(String base) {
    
    do_sizes(base, "-S2/1", ref_size*2, ref_size);
    do_sizes(base, "-S2/0.75", ref_size*2, ref_size*0.75F);
    do_sizes(base, "-S2.5/0.75", ref_size*2.5F, ref_size*0.75F);
    do_sizes(base, "-S3/0.75", ref_size*3, ref_size*0.75F);
    do_sizes(base, "-S4/0.75", ref_size*4, ref_size*0.75F);
    do_sizes(base, "-S6/1", ref_size*6, ref_size*1);
    do_sizes(base, "-S10/0.75", ref_size*10, ref_size*0.75F);
    
    do_sizes(base, "-SS1", ref_size*0.75F, ref_size*0.75F);
    do_sizes(base, "-SSS1", ref_size*0.5F, ref_size*0.5F);
    do_sizes(base, "-SS2", ref_size*2.5F, ref_size*0.75F);
    do_sizes(base, "-SS3", ref_size*4, ref_size*0.75F);
    do_sizes(base, "-SS4", ref_size*10, ref_size*0.75F);
    do_sizes(base, "-S1", ref_size, ref_size);
    do_sizes(base, "-S2", ref_size*2.5F, ref_size);
    do_sizes(base, "-S3", ref_size*4, ref_size);
    do_sizes(base, "-S4", ref_size*10, ref_size);
    
    do_places(base, "-S3-P1", ref_size*0.5F, 0, ref_size*4, ref_size);
    do_places(base, "-S3-P2", ref_size*5.5F, 0, ref_size*4, ref_size);
    
    do_places(base, "-S2-P1", ref_size*0.5F, 0, ref_size*2.5F, ref_size);
    do_places(base, "-S2-P2", ref_size*3.75F, 0, ref_size*2.5F, ref_size);
    do_places(base, "-S2-P3", ref_size*7, 0, ref_size*2.5F, ref_size);
    
    do_places(base, "-S1-P1", ref_size*0,     0, ref_size, ref_size);
    do_places(base, "-S1-P2", ref_size*1.125F, 0, ref_size, ref_size);
    do_places(base, "-S1-P3", ref_size*2.25F,  0, ref_size, ref_size);
    do_places(base, "-S1-P4", ref_size*3.375F, 0, ref_size, ref_size);
    do_places(base, "-S1-P5", ref_size*4.5F,   0, ref_size, ref_size);
    do_places(base, "-S1-P6", ref_size*5.625F, 0, ref_size, ref_size);
    do_places(base, "-S1-P7", ref_size*6.75F,  0, ref_size, ref_size);
    do_places(base, "-S1-P8", ref_size*7.875F, 0, ref_size, ref_size);
    do_places(base, "-S1-P9", ref_size*9,     0, ref_size, ref_size);
    
    do_places(base, "-SS1-P1", ref_size*0.125F, ref_size*0.125F, ref_size*0.75F, ref_size*0.75F);
    do_places(base, "-SS1-P2", ref_size*1.25F,  ref_size*0.125F, ref_size*0.75F, ref_size*0.75F);
    do_places(base, "-SS1-P3", ref_size*2.375F, ref_size*0.125F, ref_size*0.75F, ref_size*0.75F);
    do_places(base, "-SS1-P4", ref_size*3.5F,   ref_size*0.125F, ref_size*0.75F, ref_size*0.75F);
    do_places(base, "-SS1-P5", ref_size*4.625F, ref_size*0.125F, ref_size*0.75F, ref_size*0.75F);
    do_places(base, "-SS1-P6", ref_size*5.75F,  ref_size*0.125F, ref_size*0.75F, ref_size*0.75F);
    do_places(base, "-SS1-P7", ref_size*6.875F, ref_size*0.125F, ref_size*0.75F, ref_size*0.75F);
    do_places(base, "-SS1-P8", ref_size*7.0F,   ref_size*0.125F, ref_size*0.75F, ref_size*0.75F);
    do_places(base, "-SS1-P9", ref_size*9.125F, ref_size*0.125F, ref_size*0.75F, ref_size*0.75F);
    
    for (int i = 1 ; i <= 9 ; i++) {
      do_places(base, "-N1-P"+i, ref_size*(((i-1)*1.125F)+0.125F), ref_size*0.0F, ref_size*0.5F, ref_size*0.5F);
      do_places(base, "-N2-P"+i, ref_size*(((i-1)*1.125F)+0.125F), ref_size*0.5F, ref_size*0.5F, ref_size*0.5F);
      do_places(base, "-N3-P"+i, ref_size*(((i-1)*1.125F)+0.675F), ref_size*0.0F, ref_size*0.5F, ref_size*0.5F);
      do_places(base, "-N4-P"+i, ref_size*(((i-1)*1.125F)+0.675F), ref_size*0.5F, ref_size*0.5F, ref_size*0.5F);
    }
  }
}















//class nSimplePanel extends nShelfPanel {
//  nSimplePanel setPosition(float x, float y) {
//    grabber.setPosition(x, y); return this;}
//    
//  ArrayList<nRunnable> eventCloseRun = new ArrayList<nRunnable>();
//  nSimplePanel addEventClose(nRunnable r)       { eventCloseRun.add(r); return this; }
//  nSimplePanel removeEventClose(nRunnable r)       { eventCloseRun.remove(r); return this; }
//  
//  nWidget grabber, closer;
//  nSimplePanel(nGUI _g, float _ref_size, float _space_factor, String ti) { 
//    super(_g, _ref_size, _space_factor); 
//    
//    grabber = addModel("Head_Button_Small_Outline-SS4").setText(ti)
//      .setGrabbable()
//      .setSX(ref_size*10.25F)
//      .show()
//      .addEventGrab(new nRunnable() { public void run() { toLayerTop(); } } )
//      ;
//    
//    closer = addModel("Head_Button_Small_Outline-SS1").setText("X")
//      .setTrigger()
//      .addEventTrigger(new nRunnable() { public void run() { 
//        clear(); } } )
//      .setParent(grabber)
//      .alignRight()
//      ;
//    panel.setParent(grabber).stackDown();
//    addShelf()
//      //.addDrawer(10, 0)
//      ;
//  } 
//  public nSimplePanel clear() { 
//    nRunnable.runEvents(eventCloseRun); 
//    super.clear(); return this; }
//  public nSimplePanel updateHeight() { 
//    super.updateHeight(); return this; }
//  public nSimplePanel updateWidth() { 
//    super.updateWidth(); grabber.setSX((float) Math.max(ref_size * 1.5F, panel.getLocalSX())); 
//    return this; }
//}




   
