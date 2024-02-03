package UI;

import sData.nRunnable;

public class nInfo {
	  //  A AMELIORER
	  //nInfo on cam react to object pos in cam space not on object pos on screen
	  public void showText(String t) { 
	    float s = (float) (t.length()*(ref.getLocalSX() / 1.2));
	    float p = (float) (-t.length()*(ref.getLocalSX() / 1.2) / 2);
	    if (ref.getLocalX() + p + s > gui.view.pos.x + gui.view.size.x) 
	      p -= ref.getLocalX() + p + s - (gui.view.pos.x + gui.view.size.x);
	    if (ref.getLocalX() + p < gui.view.pos.x) p += gui.view.pos.x - (ref.getLocalX() + p);
	    if (invert) { ref.stackDown(); label.stackDown().setPY(0); }
	    else        { ref.stackUp(); label.stackUp().setPY(0); }
	    label.setPX(p).setSX(s);
	    label.setText(t); ref.show(); count = 3; toLayerTop();  }
	  nInfo setLayer(int l) { label.setLayer(l); ref.setLayer(l); return this; }
	  nInfo toLayerTop() { label.toLayerTop(); ref.toLayerTop(); return this; }
	  public nInfo(nGUI _g, float f) {
	    gui = _g;
	    ref = new nWidget(gui, 0, 0, f/2, f/2).setPassif()
	      .setDrawable(new Drawable(_g.drawing_pile) { public void drawing() {
//	        fill(ref.look.standbyColor);
//	        noStroke();
//	        if (invert) triangle(ref.getX(), ref.getY(), 
//	                 ref.getX() - ref.getSX()/2, ref.getY() + ref.getSY(), 
//	                 ref.getX() + ref.getSX()/2, ref.getY() + ref.getSY() );
//	        else triangle(ref.getX(), ref.getY() + ref.getSY(), 
//	                 ref.getX() - ref.getSX()/2, ref.getY(), 
//	                 ref.getX() + ref.getSX()/2, ref.getY() );
	      } } )
	      .addEventFrame(new nRunnable() { public void run() {
	        if (count > 0) {
	          count--; if (count == 0) ref.hide();
	          ref.setPosition(gui.mouseVector.x, gui.mouseVector.y);
	          if (gui.mouseVector.y < ref.getLocalSY()*8 && !invert) invert = true;
	          else if (gui.mouseVector.y > ref.getLocalSY()*12 && invert) invert = false; 
	        }
	      } } );
	    ref.stackDown();
	    label = new nWidget(gui, "", (int)(f*0.8), 0, -f, 0, f*1).setPassif()
	      .setParent(ref)
	      .stackDown()
	      ;
	    ref.hide();
	  }
	  nWidget ref,label;
	  nGUI gui;
	  int count = 0; boolean invert = true;
	}
