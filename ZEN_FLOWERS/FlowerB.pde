class FlowerB extends Flower {

  FlowerB(PVector p, String s, PApplet m) {
      super(p, s, m);
      cX = 210;
      cY = 90;
      cZ = 90;
      cA = 90;
      cX = random(1) < 0.5 ? cX - int(random(hValueRange)) : cX + int(random(hValueRange));
      cY = random(1) < 0.5 ? cY - int(random(sValueRange)) : cY + int(random(sValueRange));
      cZ = random(1) < 0.5 ? cZ - int(random(vValueRange)) : cZ + int(random(vValueRange));
      baseColor = color(cX, cY, cZ, cA);
      curColor = color(cX, cY, cZ, cA);
      baseColorLerpTo = color((cX + lerpRangeX)%360, cY, cZ, cA);
      curSize = sizeB/2;

  }

  void render() {
      super.render();

        noFill();

        pushMatrix();

            translate(pos.x, pos.y, pos.z);
            int inc = 360/amount;

            for (int i = 0; i < 360; i += inc) {
                float line = (1+i/10000) < 2 ? 2 : (1+i/20);
                strokeWeight(line);
                stroke(curColor, faderOpacity);
                rotateY((angle1)%360);
                float mySize = curSize+i/10;

                ellipse(0,0,mySize,mySize);
            }
        popMatrix();
          }

}


