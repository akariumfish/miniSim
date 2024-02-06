package UI;

import java.util.HashMap;
import java.util.Map;

import RApplet.Rapp;

//conteneur de presets
public class nTheme {
	float ref_size = 30;
	public Rapp app;
	public nTheme(Rapp a, float s) { app = a; ref_size = s; new nConstructor(this, s); }
	HashMap<String, nWidget> models = new HashMap<String, nWidget>();
	public nTheme addModel(String r, nWidget w) { models.put(r,w); return this; }
	public nWidget getModel(String r) {  return models.get(r); }
	public nLook getLook(String r) { return models.get(r).look; }
	public nWidget newWidget(String r) { //only for theme model making !!
	  for (Map.Entry<String,nWidget> me : models.entrySet()) if (me.getKey().equals(r)) { 
	    nWidget m = (nWidget)me.getValue(); 
	    return new nWidget(app).copy(m); }
	  return null; }
	public nWidget newWidget(nGUI g, String r) {
	  for (Map.Entry<String,nWidget> me : models.entrySet()) if (me.getKey().equals(r)) { 
	    nWidget m = (nWidget)me.getValue(); 
	    return new nWidget(g).copy(m); }
	  return null; }
	public nLinkedWidget newLinkedWidget(nGUI g, String r) {
	  for (Map.Entry<String,nWidget> me : models.entrySet()) if (me.getKey().equals(r)) { 
	    nWidget m = (nWidget)me.getValue(); 
	    nLinkedWidget lw = new nLinkedWidget(g); lw.copy(m); return lw; }
	  return null; }
	public nWatcherWidget newWatcherWidget(nGUI g, String r) {
	  for (Map.Entry<String,nWidget> me : models.entrySet()) if (me.getKey().equals(r)) { 
	    nWidget m = (nWidget)me.getValue(); 
	    nWatcherWidget lw = new nWatcherWidget(g); lw.copy(m); return lw; }
	  return null; }
	public nCtrlWidget newCtrlWidget(nGUI g, String r) {
	  for (Map.Entry<String,nWidget> me : models.entrySet()) if (me.getKey().equals(r)) { 
	    nWidget m = (nWidget)me.getValue(); 
	    nCtrlWidget lw = new nCtrlWidget(g); lw.copy(m); return lw; }
	  return null; }
}
