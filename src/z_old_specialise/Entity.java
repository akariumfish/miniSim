package z_old_specialise;

public abstract class Entity { 
	  Community com;
	  int age = 0, id;
	  public boolean active = false;
	  Entity(Community c) { 
	    com = c; 
	    id = com.list.size();
	  }
	  Entity activate() {
	    if (!active) { 
	      active = true; 
	      age = 0; 
	      init();
	    }
	    return this;
	  }
	  Entity destroy() {
	    if (active) { 
	      active = false; 
	      clear();
	    }
	    return this;
	  }
	  abstract Entity tick();     //exec by community 
	  abstract Entity frame();    //exec by community 
	  abstract Entity draw();    //exec by community 
	  abstract Entity init();     //exec by activate and community.reset
	  abstract Entity clear();    //exec by destroy
	}

