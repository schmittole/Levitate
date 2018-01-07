class LevF extends Lev {

  LevF(PVector p, String s, PApplet m) {
      super(p, s, m);
      cX = 50;
      cY = 90;
      cZ = 90;
      cA = 90;
      cX = random(1) < 0.5 ? cX - int(random(hValueRange)) : cX + int(random(hValueRange));
      cY = random(1) < 0.5 ? cY - int(random(sValueRange)) : cY + int(random(sValueRange));
      cZ = random(1) < 0.5 ? cZ - int(random(vValueRange)) : cZ + int(random(vValueRange));
      baseColor = color(cX, cY, cZ, cA);
      curColor = color(cX, cY, cZ, cA);
      baseColorLerpTo = color((cX + lerpRangeX)%360, cY, cZ, cA);
      curSize = sizeA - 5;
      lifeTime = 1;
      startPlaying();
  }

  void render() {
      super.tryToSpawnTwolevs();

        if(isDead()) {
          fadeAway();
        }

        if(fadedAway()) {
          addMeToToErase();
        }

        angleForLevitate += angleForLevitateInc;
        angle1 += angle1Inc;
        if (angleForLevitate >= TWO_PI) { angleForLevitate = 0; }
        if (angle1 >= TWO_PI) { angle1 = 0; }

        pos.y += sin(angleForLevitate)*0.5;

        visualizePlaying();

        noFill();

        pushMatrix();

            translate(pos.x, pos.y, pos.z);
            int inc = 360/amount;

            for (int i = 0; i < 360; i += inc) {
                float line = (1+i/10000) < 2 ? 2 : (1+i/20);
                strokeWeight(line);
                stroke(curColor, faderOpacity);
                rotateY((angle1)%360);
                polygon(0,0,(i+1)*0.6, 5);
            }

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

}


