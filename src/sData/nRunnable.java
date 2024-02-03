package sData;

import java.util.ArrayList;


//execution ordonné en layer et timer


//class EventPile {
//void addEvent(Runnable r, int l) { events.add(new Event(r, l)); }
////execution order
//void addEventFirst(Runnable r)       { events.add(0, new Event(r, 0)); }
//void addEventMiddleFirst(Runnable r) { events.add(0, new Event(r, 1)); }
//void addEventMiddleLast(Runnable r)  { events.add(new Event(r, 1)); }
//void addEventLast(Runnable r)        { events.add(new Event(r, 2)); }
//
//class Event { Runnable r; int layer; Event(Runnable _r, int l) { r = _r; layer = l; } }
//ArrayList<Event> events = new ArrayList<Event>();
//
//EventPile() { }
//void run() {
//  int layer = 0, run_count = 0;
//  while (run_count < events.size()) {
//    for (Event r : events) if (r.layer == layer) { r.r.run(); run_count++; } 
//    layer++; } }
//void run(float v) {
//  int layer = 0, run_count = 0;
//  while (run_count < events.size()) {
//    for (Event r : events) if (r.layer == layer) { r.r.run(v); run_count++; } 
//    layer++; } }
//
//}

public abstract class nRunnable {
	public boolean to_clear = false;
	public Object builder = null; 
	public nRunnable() {} 
	public nRunnable(Object p) { builder = p; } 
	public void run() {}
	public void run(float v) {}
  
	public static void runEvents(ArrayList<nRunnable> e) { for (int i = e.size() - 1 ; i >= 0 ; i--) e.get(i).run(); }
	public static void runEvents(ArrayList<nRunnable> e, float v) { for (int i = e.size() - 1 ; i >= 0 ; i--) e.get(i).run(v); }
	
	public static void clearEvents(ArrayList<nRunnable> e) { 
		ArrayList<nRunnable> e2 = new ArrayList<nRunnable>();
		for (nRunnable r : e) if (r.to_clear) e2.add(r);
		for (nRunnable r : e2) e.remove(r);
	  
	}
}
