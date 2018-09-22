import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Comparator; 
import java.util.Collections; 
import java.util.ArrayList; 
import java.util.Iterator; 
import java.util.HashSet; 
import java.util.HashMap; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch180731_BoxFitting_collision extends PApplet {








ArrayList<Expander> exs = new ArrayList<Expander>();
int groupmax = 100;

public void setup(){
  

  colorMode(HSB, 360, 100, 100);

  // // create expanding box randomly
  // for(int i = 0; i < 100; i++){
  //   createExpander(exs);
  // }
}

public void draw(){
  background(0);
  for(Expander tmpex : exs) {
    tmpex.expand(exs);    
  }

  Iterator<Expander> it = exs.iterator();
  while(it.hasNext()){
    Expander i = it.next();
    if(!i.expanding && i.life <= 0) it.remove();
  }

  // color by contact
  // Collections.sort(exs, new ExpanderComparator());
  colorByTangency(exs);

  for(Expander tmpex : exs){
    tmpex.display();
    tmpex.displayText();
  } 

  createExpander(exs); // add new expander

  // information
  fill(360);
  textAlign(RIGHT, BOTTOM);
  text("framerate: " + String.format("%.2f", frameRate) + "\n"
    + "fill rate: " + String.format("%.3f", calcFilllingRate(exs)) + "\n"
    + "num expander: " + exs.size()
    , width, height);
}

public void keyPressed(){
  switch(key){
    case ' ':
      createExpander(exs);
    break;
    case 'c' :
      removeLowerRanker(exs, 10);      
    break;  
    case 'r':
      background(0);
      for(Expander tmpex : exs) tmpex.resetPos();
    break;  
    default :
    break;  

  }
}

class Expander{
  float x;
  float y;
  float w;
  float h;
  float ar; // aspect ratio = h/w 
  boolean expanding;
  int life;
  int lifetime;
  int fc_hue_expanding;
  int fc_hue_stopping;
  int sc_hue_stopping;

  // for colorByTangency
  int group;

  Expander(float _x, float _y, float _w, float _h, float _ar){
    x = _x;
    y = _y;
    // using even number for WIDTH and HEIGHT of this class.
    w = _w;
    h = _h;
    ar = _ar;
    expanding = true; 
    life = 0;
    lifetime = 0;
    fc_hue_expanding = 180;
    fc_hue_stopping = 120;
    sc_hue_stopping = 120;
    group = getUnusedGroup(exs);
  }

  public void expand(ArrayList<Expander> _exs){
    if(ar < 1){
      float dw = 1;
      if(!judgeRect(x, y, w, h, _exs)){
        if(expanding){
          lifetime = calcLifetime(w, h);
          life = lifetime;
        }
        expanding = false;
        life--;
      } else{
        w = w + dw*2;
        h = w*ar;
        expanding = true;
      }
    }else{
      float dh = 1;
      if(!judgeRect(x, y, w, h, _exs)){
        if(expanding){
          lifetime = calcLifetime(w, h);
          life = lifetime;
        }
        expanding = false;
        life--;
      } else{
        h = h + dh*2;
        w = h/ar;
        expanding = true;
      }
    }
  }

  public void display(){
    if(expanding) fill(fc_hue_expanding);
    else fill(fc_hue_stopping, 100, map(life, 0, lifetime, 0, 100));
    stroke(sc_hue_stopping, 100, 100);
    rectMode(CENTER);
    rect(x, y, w, h);
  }

  public void displayText(){
    fill(360);
    textAlign(CENTER, CENTER);
    text(group + "\n" + life, x, y);
  }

  public void resetPos(){
    w = 2;
    h = 2;
  }

  public float getSize(){
    return w*h;
  }

};

public boolean judgeRect(float _x, float _y, float _w, float _h, ArrayList<Expander> _exs){
  if(_x - _w/2 < 0 || width <= _x + _w/2) return false;
  if(_y - _h/2 < 0 || height <= _y + _h/2) return false;

  for(Expander tmpex : _exs){
    if(_x == tmpex.x && _y == tmpex.y) continue;
    if(abs(_x - tmpex.x) <= (_w/2 + tmpex.w/2) 
      && abs(_y - tmpex.y) <= (_h/2 + tmpex.h/2)) return false;
  }  
  return true;
}

public void removeLowerRanker(ArrayList<Expander> _exs, int _num){
  Collections.sort(_exs, new ExpanderComparator());
  for (int i = 0; i < _num; ++i) {
    _exs.remove(_exs.size() - 1);    
  }
}

public class ExpanderComparator implements Comparator<Expander> { 
  @Override public int compare(Expander p1, Expander p2) { 
    return p1.getSize() > p2.getSize() ? -1 : 1;
  }
} 

public int calcLifetime(float _w, float _h){
  return floor(_w * _h / 10);
}

public void createExpander(ArrayList<Expander> _exs){
  float tmpx = random(0, width);
  float tmpy = random(0, height);
  float tmpw = random(1, 5);
  float tmph = random(1, 5);
  // float tmpw = 1;
  // float tmph = 1;

  if(judgeRect(tmpx, tmpy, tmpw, tmph, _exs)) _exs.add(new Expander(tmpx, tmpy, tmpw, tmph, tmph/tmpw));      

}

public float calcFilllingRate(ArrayList<Expander> _exs){
  float sum = 0;
  for(Expander tmpex : _exs){
    sum += tmpex.getSize();
  }
  return sum / (width*height);
}

public void colorByTangency(ArrayList<Expander> _exs){
  // preparation
  for (int i = 0; i < _exs.size(); ++i) {
    _exs.get(i).group = i;    
  }

  // judge tangency. if contact, use smaller number group
  for(int j = 0; j < _exs.size(); j++){
    for(int i = j + 1; i < _exs.size(); i++){
      Expander ex1 = _exs.get(j);
      Expander ex2 = _exs.get(i);
      if(ex1.x == ex2.x && ex1.y == ex2.y) continue; // skip myself
      if(abs(ex1.x - ex2.x) <= (ex1.w/2 + ex2.w/2) && abs(ex1.y - ex2.y) <= (ex1.h/2 + ex2.h/2)){
        ex2.group = ex1.group;
      } 
    }      
  }

  // 
  HashSet<Integer> hash_set = new HashSet<Integer>();
  for(Expander ex : _exs) hash_set.add(ex.group);  
  ArrayList<Integer> list = new ArrayList<Integer>(hash_set);

  // create hash map for re-coloring
  HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
  for (int i = 0; i < list.size(); ++i) {
    // hm.put(list.get(i), 360 * i / list.size());
    hm.put(list.get(i), (int)map(list.get(i), 0, groupmax, 60, 180));
  }

  for(Expander ex : _exs){
    ex.fc_hue_stopping = hm.get(ex.group);
    ex.sc_hue_stopping = hm.get(ex.group);
  }  

}

public int getUnusedGroup(ArrayList<Expander> _exs){
  HashSet<Integer> hash_set = new HashSet<Integer>();
  for(Expander ex : _exs){
    hash_set.add(ex.group);
  }
  // println(_exs.size() + ", " + hash_set.size());
  if(hash_set.size() == 0) return 0;
  for (int i = 0; i < groupmax; ++i) {
    if(!hash_set.contains(i)) return i;
  }
  return -1;
}
  public void settings() {  size(1600, 1200, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch180731_BoxFitting_collision" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
