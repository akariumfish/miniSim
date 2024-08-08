package z_old_specialise;

import RApplet.Rapp;
import processing.core.PConstants;
import processing.core.PVector;

public class Animation {
	Simulation sim;
	Rapp app;
	
	PVector pos;
	float rad;
	int life_cnt;
	int rank;
	
	Animation(Simulation s) {
		sim = s;
		app = sim.gui.app;
		sim.anims.add(this);
		pos = new PVector();
	}
	
	void play(float x, float y, int r) {
		life_cnt = 150;
		pos.set(x, y);
		rad = 20;
		rank = r + 1;
	}
	
	void tick() {
		life_cnt--;
		if (life_cnt > 0) rad++;
//		else if (rank < 5) {
//			Animation a = new Animation(sim);
//			a.play(pos.x, pos.y, rank);
//		}
	}
	
	void draw() {
		if (life_cnt > 0) {
		  app.pushMatrix();
		  app.translate(pos.x, pos.y);
		  app.noFill();
		  app.stroke(200, 200);
		  app.strokeWeight(0.5F);
		  app.ellipseMode(PConstants.CENTER);
		  app.ellipse(0, 0, rad, rad);
		  app.popMatrix();
		}
	}
	
}
