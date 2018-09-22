ArrayList<Expander> exs = new ArrayList<Expander>();

void setup(){
  size(800, 600);

  fill(0);
  stroke(255);
  rect(0, 0, width - 1, height - 1);

  // // create expanding box randomly
  // for(int i = 0; i < 100; i++){
  //   exs.add(new Expander(
  //     (int)random(0, width), 
  //     (int)random(0, height), 
  //     2 * (int)random(1, 5), 
  //     2 * (int)random(1, 5)
  //     ));
  // }

  // create expanding box with blurred regular interval
  int laticewidth = 100;
  for (int j = 0; j < height; j+=laticewidth) {
    for(int i = 0; i < width; i+=laticewidth){
      exs.add(new Expander(
        (int)random(i, i+laticewidth), 
        (int)random(j, j+laticewidth), 
        2 * (int)random(1, 5), 
        2 * (int)random(1, 5)
        ));
    }
  }

}

void draw(){
  for(Expander tmpex : exs) tmpex.expand();

  fill(20, 20, 20);
  stroke(255);
  for(Expander tmpex : exs) tmpex.display();
  // for(Expander tmpex : exs) tmpex.displayText();
}

void keyPressed(){
  switch(key){
    case ' ':
      loadPixels();

      int tmpx = (int)random(0, width);
      int tmpy = (int)random(0, height);
      color tmpc = pixels[width*tmpy + tmpx];

      if(tmpc != color(255, 255, 255)
        && tmpc != color(20, 20, 20)){
        exs.add(new Expander(tmpx, tmpy, 10, 10));      
      }
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
  int x;
  int y;
  int w;
  int h;

  Expander(int _x, int _y, int _w, int _h){
    x = _x;
    y = _y;
    // using even number for WIDTH and HEIGHT of this class.
    w = _w;
    h = _h;
  }

  void expand(){
    int tmpx, tmpy;
    color tmpc;
    int dd = 1;    

    loadPixels();

    // left side
    tmpx = x - w/2 - dd;
    for(tmpy = y - h/2 - dd; tmpy <= y + h/2 + dd; tmpy++){
      if(tmpx < 0 || width <= tmpx) continue;
      if(tmpy < 0 || height <= tmpy) continue;
      tmpc = pixels[width*tmpy + tmpx];
      if(tmpc == color(255, 255, 255)) return;
    }

    // right side
    tmpx = x + w/2 + dd;
    for(tmpy = y - h/2 - dd; tmpy <= y + h/2 + dd; tmpy++){
      if(tmpx < 0 || width <= tmpx) continue;
      if(tmpy < 0 || height <= tmpy) continue;
      tmpc = pixels[width*tmpy + tmpx];
      if(tmpc == color(255, 255, 255)) return;
    }

    // upper side
    tmpy = y + h/2 + dd;
    for(tmpx = x - w/2 - dd; tmpx <= x + w/2 + dd; tmpx++){
      if(tmpx < 0 || width <= tmpx) continue;
      if(tmpy < 0 || height <= tmpy) continue;
      tmpc = pixels[width*tmpy + tmpx];
      if(tmpc == color(255, 255, 255)) return;
    }

    // lower side
    tmpy = y - h/2 - dd;
    for(tmpx = x - w/2 - dd; tmpx <= x + w/2 + dd; tmpx++){
      if(tmpx < 0 || width <= tmpx) continue;
      if(tmpy < 0 || height <= tmpy) continue;
      tmpc = pixels[width*tmpy + tmpx];
      if(tmpc == color(255, 255, 255)) return;
    }

    w += dd*2;
    h += dd*2;
  }

  void display(){
    rectMode(CENTER);
    rect(x, y, w, h);
  }

  void displayText(){
    fill(255);
    textAlign(CENTER, CENTER);
    text(w + "x" + h, x, y);
  }

  void resetPos(){
    w = 2;
    h = 2;
  }

};

void judgeRect(){
  
}