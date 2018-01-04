public class Curser {

  PVector pos;
  int size = 30;
  color myColor = color(100, 100, 100, 100);

    float angle1;
    float angle2;
    float angle3;

    float angle1Inc = 0.007;
    float angle2Inc = 0.006;
    float angle3Inc = 0.009;

  public Curser(PVector p) {
    pos = new PVector();
    pos.set(p);
  }

  public void move(float x, float y, float z) {
    pos.x = x;
    pos.y = y;
    pos.z = z;
  }

  public void render() {

    angle1 += angle1Inc;
    angle2 += angle2Inc;
    angle3 += angle3Inc;

    if(angle1 >= TWO_PI) {angle1 = 0;}
    if(angle2 >= TWO_PI) {angle2 = 0;}
    if(angle3 >= TWO_PI) {angle3 = 0;}

    stroke(myColor);
    noFill();
    
    pushMatrix();
    
    translate(pos.x, pos.y, pos.z);

    //renders circle
    rotateX(angle1);
    rotateY(angle2);
    rotateZ(angle3);
    ellipse(0, 0, size, size);

    //renders triangle
    rotateX(angle2);
    rotateY(angle3);
    rotateZ(angle1);
    polygon(0, 0, size, 3);

    //renders rectangle
    rotateX(angle3);
    rotateY(angle1);
    rotateZ(angle2);
    polygon(0, 0, size, 4);

    popMatrix();
  }

  //draws polygon with center x,y. radius radius. points npoints
  //https://processing.org/examples/regularpolygon.html
  void polygon(float x, float y, float radius, int npoints) {
    float angle = TWO_PI / npoints;
    beginShape();
    for (float a = 0; a < TWO_PI; a += angle) {
      float sx = x + cos(a) * radius;
      float sy = y + sin(a) * radius;
      vertex(sx, sy);
    }
    endShape(CLOSE);
}

  //sets color depending on String s (=which left fingers are stretched?)
  public void setMyColor(String s) {
    switch(s) {
      
      case("circle"):
        myColor = color(210,90,90,90);
      break;
      
      case("triangle"):
      myColor = color(150, 90, 90, 90);
      break;

      case("rectangle"):
      myColor = color(360, 90, 90, 90);
      break;

      case("NONE"):
      myColor = color(100, 100, 100, 0);
      break;
      
      case("normal"):
      myColor = color(60, 90, 90, 90);
      break;

      default:
      myColor = color(60, 90, 90, 90);
    }
  }
}