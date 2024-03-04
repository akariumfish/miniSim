package Macro;

import java.util.ArrayList;

import RApplet.Rapp;
import RApplet.RConst;
import processing.core.PVector;
import sData.sValue;
import sData.sValueBloc;

public class Macro_Packet {
	
	public static Macro_Packet newPacketBang() { return new Macro_Packet("bang"); }

	public static Macro_Packet newPacketFloat(float f) { return new Macro_Packet("float").addMsg(String.valueOf(f)); }
	public static Macro_Packet newPacketFloat(String f) { return new Macro_Packet("float").addMsg(f); }

	public static Macro_Packet newPacketInt(int f) { return new Macro_Packet("int").addMsg(String.valueOf(f)); }

	public static Macro_Packet newPacketVec(PVector p) { return new Macro_Packet("vec").addMsg(String.valueOf(p.x)).addMsg(String.valueOf(p.y)); }

	public static Macro_Packet newPacketCol(int p) { return new Macro_Packet("col")
	  .addMsg(String.valueOf(app.red(p))).addMsg(String.valueOf(app.green(p))).addMsg(String.valueOf(app.blue(p))); }

	public static Macro_Packet newPacketStr(String p) { return new Macro_Packet("str").addMsg(RConst.copy(p)); }

	public static Macro_Packet newPacketObject(Object m) { return new Macro_Packet("obj").setObject(m); }
	public static Macro_Packet newPacketRun(Object m) { return new Macro_Packet("obj").setObject(m); }

	public static Macro_Packet newPacketBool(boolean b) { 
	  String r; 
	  if (b) r = "T"; else r = "F"; 
	  return new Macro_Packet("bool").addMsg(r); }
	
  static public Rapp app;
  
  public Macro_Packet copy() {
	  Macro_Packet p = new Macro_Packet(def);
	  p.pobj = pobj;
	  for (String s : messages) p.addMsg(s);
	  return p;
  }
  Object pobj = null;
  String def = new String();
  ArrayList<String> messages = new ArrayList<String>();
  public Macro_Packet(String d) {
    def = d;
  }
  Macro_Packet addMsg(String m) { messages.add(m); return this; }

  String popMsg() { // !!!!! if the packet is send to two in, for the sec messages will have been emptied
	  				// packet is shared !!!!!
	  if (messages.size() > 0) {
		  String s = messages.get(0); 
		  messages.remove(s);
		  return s; 
	  }
	  return null; 
  }
  
  boolean hasMsg() {
	  return messages.size() > 0;
  }
  
  boolean isBang()  { return def.equals("bang"); }
  boolean isFloat() { return def.equals("float"); }
  boolean isInt()   { return def.equals("int"); }
  boolean isBool()  { return def.equals("bool"); }
  boolean isVec()   { return def.equals("vec"); }
  boolean isCol()   { return def.equals("col"); }
  boolean isStr()   { return def.equals("str"); }
  
  boolean equalsVec(PVector v)   { return isVec() && v.x == asVec().x && v.y == asVec().y; }
  boolean equalsCol(int v)   { return isCol() && v == asCol(); }
  
  PVector asVec()   { 
    if (isVec()) return new PVector(Float.parseFloat(messages.get(0)), Float.parseFloat(messages.get(1))); else return null; }
  int asCol()   { 
    if (isCol()) return app.color(Float.parseFloat(messages.get(0)), 
    						  Float.parseFloat(messages.get(1)), 
    						  Float.parseFloat(messages.get(2))); else return 0; }
  float   asFloat()   { if (isFloat()) return Float.parseFloat(messages.get(0)); else return 0; }
  int     asInt()   { if (isInt()) return Integer.parseInt(messages.get(0)); else return 0; }
  String  asStr()   { if (isStr()) return messages.get(0); else return ""; }
  boolean asBool()   {
    if (isBool() && messages.get(0).equals("T")) return true; else return false; }
  
  String getType() { return def; }
  String getText() {
    if (isBang()) return "bang";
    else if (isFloat()) return RConst.trimFlt(asFloat());
    else if (isInt()) return String.valueOf(asInt());
    else if (isBool() && messages.get(0).equals("T")) return "true";
    else if (isBool() && !messages.get(0).equals("T")) return "false";
    else if (isVec()) return RConst.trimFlt(asVec().x)+","+RConst.trimFlt(asVec().y);
    else if (isCol()) return RConst.trimFlt(app.red(asCol()))+","+
                             RConst.trimFlt(app.green(asCol()))+","+
                             RConst.trimFlt(app.blue(asCol()));
    else if (isStr()) return asStr();
    else if (isObj()) return "object:"+asObj();
    return "";
  }

  Macro_Packet setObject(Object b) { pobj = b; return this; }
  boolean isObj() { return def.equals("obj"); }
  Object  asObj()   { if (isObj()) return pobj; else return null; }
  
}
