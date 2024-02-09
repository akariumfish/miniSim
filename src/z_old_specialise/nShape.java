package z_old_specialise;

import RApplet.Rapp;
import processing.core.PVector;

public abstract class nShape {
	  public PVector pos = new PVector(0, 0);
	  public PVector dir = new PVector(10, 0); //heading : rot , mag : scale
	  public boolean do_fill = true, do_stroke = true;
	  public int col_fill, col_line;
	  public float line_w = 0.01F;
	  public nShape(Rapp app) {
		  col_fill = app.color(20, 130, 40); col_line = app.color(10, 190, 40);
	  }
	  public void draw(Rapp app, boolean is_line) {
		  app.pushMatrix();
		  app.translate(pos.x, pos.y);
		  app.rotate(dir.heading());
		  app.scale(dir.mag());
		if (!is_line) {
//		    app.noStroke();
//		    app.noFill(); 
		    if (do_fill) app.fill(col_fill); else app.noFill(); 
		    if (do_stroke) app.stroke(col_line); else app.noStroke(); app.strokeWeight(line_w);
		    draw_fill_call(app);
//		    draw_stroke_call(app);
		} else {
			app.noFill(); 
		    if (do_stroke) app.stroke(col_line); else app.noStroke(); app.strokeWeight(line_w);
		    draw_line_call(app);
		}
	    
	    app.popMatrix();
	  }
	  public abstract void draw_fill_call(Rapp a);
	  public abstract void draw_stroke_call(Rapp a);
	  public abstract void draw_line_call(Rapp a);
	}
