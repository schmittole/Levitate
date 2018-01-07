import processing.sound.*;

class Lev {


    /*---------------------------------------------------------------
    Fields
    ----------------------------------------------------------------*/

    //position
    PVector pos;

    //angles for rotation
    float angle1;
    float angle2;

    //angles for levitation
    float angleForLevitate = random(0, TWO_PI);

    //increments. (speeds)
    float angle1Inc = 0.007;
    float angle2Inc = 0.06;

    //increments. (speeds)
    float angleForLevitateInc = random(0.02, 0.06);

    //speed scalar
    float scalar = 100;

    // is it tagged?
    boolean tagged = false;

    //different sizes for different levs
    int sizeA = 5;
    int sizeB = 30;
    int sizeC = 15;


    //amount if shapes in one lev
    int amount;

    //speed
    float speed = 0.1;
    float vel = 0.02;

    //opacitiy for fading
    int faderOpacity = 100;

    //speeds for all three directions
    float movSpeedX = 4.0f;
    float movSpeedY = 4.0f;
    float movSpeedZ = 4.0f;

    float speedScalar = 0.5f;

    //starting position - current position
    PVector homeComingVector;

    //starting position
    PVector basePos;

    // is it home?
    boolean home = false;

    //color
    int cX;
    int cY;
    int cZ;
    int cA;

    //for lerping
    int lerpRangeX = 80;

    //colors
    color baseColor;
    color baseColorLerpTo;
    color curColor = color(0,0,0,0);

    float colorLerpInc = 0.001;
    float colorLerp = 0;

    int disToHome = 2;

    int timeToGetHome = 80;

    //my sound
    SoundFile sound;

    // when to chose a next one
    float getNextIntervall = 0.8f;
    float detectNextIntervall = 0.9f;

    float startFadingAway = 0.8f;

    Timer soundTimer;

    Plane myPlane;

    Lev next;

    PVector connectionColor = new PVector(50, 100, 100);

    //current size
    int curSize;

    //speeds
    float baseMovSpeedX;
    float baseMovSpeedY;
    float baseMovSpeedZ;

    //lives
    int lifeTime = 1;


    //for randomizing color
    int hValueRange = 10;

    int sValueRange = 10;

    int vValueRange = 10;

    int toScalar = 5;

    // my Main (sketch)
    PApplet main;

    //my detected list
    ArrayList<Lev> detected = new ArrayList<Lev>();

    int spawnLimit = 1;

    //for visualizing
    int bezierNoise = 25;

    int noiseScalar1 = 0;
    int noiseScalar2 = 0;

    //for hand gesture levitate
    float zTagOffset;

    boolean taggedByLev = false;

    float startPlayingIntervall = 0.9;

    //for big levs: have I already spaned two levs?
    boolean iSpawnedTwolevs = false;

    //for visualizing
    float cBezierNoise1;
    float cBezierNoise2;
    float cBezierNoise3;

    float ccBezierNoise1;
    float ccBezierNoise2;
    float ccBezierNoise3;

    int bezierInc = 10;

    boolean cOneUp = false;
    boolean cTwoUp = false;
    boolean cThreeUp = false;


    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    Lev(PVector p) {
        pos = new PVector();
        pos.set(p);
        basePos = new PVector();
        basePos.set(pos);
        amount = (int)random(2,9);
        setRandomMovSpeed();
    }

    Lev(PVector p, String s, PApplet main) {
        pos = new PVector();
        pos.set(p);
        basePos = new PVector();
        basePos.set(pos);
        amount = (int)random(2,9);
        setRandomMovSpeed();
        sound = new SoundFile(main,s);
        myPlane = new Plane();
        lifeTime = int(random(1, 5));
        this.main = main;
        spawnLimit = int(random(2, 4));
    }

    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    void setSound(SoundFile s) {
      sound = s;
    }



    void setPos(PVector p) { pos.set(p); }
    PVector getPos() { return pos; }
    boolean isTagged() { return tagged; }
    void setAmount(int a) {amount = a; }

    void setMyPlane(Plane p) {
      myPlane = new Plane();
      myPlane = p;
    } 

    void addToMyPlane(Lev f) {
      myPlane.add(f);
    }


    void addMeToToErase() {
      myPlane.setToErase(this);
    }

    boolean isDead() {
      return lifeTime <= 0;
    }

    //decrement fader opacity if necessary
    void fadeAway() {
      if(soundTimer != null && soundTimer.progress() > startFadingAway) {
        faderOpacity--;
        if(faderOpacity < 0) {
          faderOpacity = 0;
        }
      }
    }

    boolean fadedAway() {
      return faderOpacity <= 0;
    }

    //reset next if necessary
    void checkMyNext() {
      for(Plane p : levs.getPlanes()) {
        for(Lev f : p.getlevs()) {
          if(f != this && f.next == next) {
            next = null;
          }
        }
      }
    }

    PVector randomPoint() {
      return new PVector(int(random(width)), int(random(height)), int(random(lowerZ, upperZ)));
    }

    boolean checkIfSpawnEventTwo() {
      if(soundTimer != null) {
        return true;
      } else {
        return false;
      }
    }

    void tryToSpawnTwolevs() {
      if(!iSpawnedTwolevs && checkIfSpawnEventTwo()) {
          spawnEventsTwo.add(new SpawneventTwo(this));
          iSpawnedTwolevs = true;
        }
    }

    void render() {

        if(isDead()) {
          fadeAway();
        }

        //this should be erased if dead
        if(soundTimer != null && soundTimer.progress() >= 0.98 && fadedAway()) {
          addMeToToErase();
        }

        //handle angles
        angleForLevitate += angleForLevitateInc;
        angle1 += angle1Inc;
        if (angleForLevitate >= TWO_PI) { angleForLevitate = 0; }
        if (angle1 >= TWO_PI) { angle1 = 0; }
        pos.y += sin(angleForLevitate)*0.5;

        //this is still playing sound
        if(soundTimer != null && soundTimer.progress() <= 1) {
          visualizePlaying();
          visualizeConnection();
        }

        //find a next object
        if(myPlane.getlevs().size() > 1 && next == null && soundTimer != null && soundTimer.progress() > getNextIntervall) {
          findNext();
        }

        //get detexted
        if(next != null && soundTimer != null && soundTimer.progress() > startPlayingIntervall) {
          next.detectMeByLev();
        }

        //check if should spawn a big Lev (spawnEvent)
        if(checkIfSpawnEvent() && next != null) {
          spawnEvents.add(new Spawnevent(this, next, main));
        }

        //reset if finished with playing sound
        if(soundTimer != null && soundTimer.progress() >= 1.1) {
          tagged = false;
          soundTimer = null;
          next = null;
        }
    }

    void detectMeByLev() {
      if(soundTimer == null) {
        tagged = true;
       startPlaying();
      }
    }

    void startPlaying() {
      soundTimer = new Timer(sound.duration());
      soundTimer.start();
      play();
    }

    void play() {
      sound.play();
      //each time playing, decrement lifetime
      lifeTime--;
    }

    //try to detect this object with cPos (Hand position)
    boolean detect(PVector cPos) {
      if(!myPlane.oneIsTagged && !tagged && interactionController.isTagging() && dist(pos.x, pos.y, pos.z + zTagOffset, cPos.x, cPos.y, cPos.z) < tagDis) {
        tagged = true;
        myPlane.oneIsTagged = true;
        playRandomPlant();
        startPlaying();
        return true;
      } else {
        return false;
      }
    }

    //find a next object to activate
    void findNext() {
      if(!(this instanceof LevD || this instanceof LevE || this instanceof LevF) && next == null) {
        next = findNearest();
        if(next == this) {
          next = null;
        }
        if(next != null) {
          detected.add(next);
        }
      }
    }

    //find a random object in own class
    Lev findRandomNext() {
      Lev re = null;
      if(levs.totalSize > 1) {
        while(next == null) {
          Plane p = levs.getPlanes().get(int(random(3)));
          if(p != null && p.getlevs().size() > 0) {
            re = p.getlevs().get(int(random(p.getlevs().size())));
            if(re != null && re != this && !re.tagged) {
              re.tagged = true;
              return re;
            }
          }
        }
      }
      return null;
    }

    //check if should spawn a big Lev (spawnEvent). Depending on memory of activations.
    boolean checkIfSpawnEvent() {
      int max = 0;
      for(Lev f : detected) {
        if(f == next) {
          max++;
        }
        if(max == spawnLimit) {
          detected = new ArrayList<Lev>();
          f.detected = new ArrayList<Lev>();
          return true;
        }
      }
      return false;
    }

    //find nearest object in own class
    Lev findNearest() {
      float max = Float.MAX_VALUE;
      Lev nearest = this;
      for(Lev f : myPlane.getlevs()) {
        if(f != this) {
        float newMax = dist(pos.x, pos.y, pos.z, f.pos.x, f.pos.y, f.pos.z);
          if(newMax < max) {
            max = newMax;
            nearest = f;
          }
        }
      }
      return nearest;
    }


    //***********************************************
    //MOVING
    //***********************************************

    void setRandomMovSpeed() {
      movSpeedX = random(0,1)*speedScalar;
      movSpeedY = random(0,1)*speedScalar;
      movSpeedZ = random(0,1)*speedScalar;

      baseMovSpeedX = movSpeedX;
      baseMovSpeedY = movSpeedY;
      baseMovSpeedZ = movSpeedZ;
    }


    void move() {

      pos.x += movSpeedX;
      if(pos.x > width) {
        //pos.x = 0;
        movSpeedX = -movSpeedX;
      }
      if(pos.x < 0) {
        //pos.x = width;
        movSpeedX = -movSpeedX;
      }

      pos.y += movSpeedY;
      if(pos.y > height) {
        //pos.y = 0;
        movSpeedY = -movSpeedY;
      }
      if(pos.y < 0) {
        //pos.y = width;
        movSpeedY = -movSpeedY;
      }

      pos.z += movSpeedZ;
      if(pos.z > upperZ) {
        //pos.z = -maxDepth;
        movSpeedZ = -movSpeedZ;
      }
      if(pos.z < lowerZ) {
        //pos.z = maxDepth;
        movSpeedZ = -movSpeedZ;
      }
      //addPhysics();
      //playPhysics();

    }

    void makeHomeComingVector() {
      homeComingVector = new PVector((basePos.x-pos.x)/timeToGetHome, (basePos.y-pos.y)/timeToGetHome, (basePos.z-pos.z)/timeToGetHome);
    }

    void moveTo(PVector to) {

      if((pos.x-to.x) > 0) {
        pos.x += -abs(movSpeedX*toScalar);
      } else {
        pos.x += abs(movSpeedX*toScalar);
      }

      if((pos.y-to.y) > yOffsetToHand) {
        pos.y += -abs(movSpeedY*toScalar);
      } else {
        pos.y += abs(movSpeedY*toScalar);
      }

      if((pos.z-to.z) > 0) {
        pos.z += -abs(movSpeedZ*toScalar);
      } else {
        pos.z += abs(movSpeedZ*toScalar);
      }

      pos.x += sin(angleForLevitate)*0.5;

    }



    void goHome() {
      if(homeComingVector == null) {
        makeHomeComingVector();
      }
      checkHomeComingVector();
      boolean one = true;
      if(abs(pos.x-basePos.x) > disToHome) {
        pos.x += homeComingVector.x;
        one = false;
      }
      boolean two = true;
      if(abs(pos.y-basePos.y) > disToHome) {
        pos.y += homeComingVector.y;
        two = false;
      }
      boolean three = true;
      if(abs(pos.z-basePos.z) > disToHome) {
        pos.z += homeComingVector.z;
        three = false;
      }
      home = one && two && three;
      if(home) {
        homeComingVector = new PVector(0,0,0);
        pos.set(basePos);
      }
    }

    void checkHomeComingVector() {
      if(pos.y < basePos.y && homeComingVector.y < 0) {
        homeComingVector.y *= -1;
      } else if(pos.y > basePos.y && homeComingVector.y > 0) {
        homeComingVector.y *= -1;
      }
    }

    boolean getHome() {
      return home;
    }

    void resetHomeComingVector() {
      homeComingVector = null;
    }


    //***********************************************
    //VISUALIZE
    //***********************************************

    void visualizePlaying() {
      if(soundTimer != null && !soundTimer.finished()) {
        if(soundTimer.progress() <= 0.5) {
          colorLerpInc = 0.001;
        } else {
          colorLerpInc = - 0.001;
        }
        colorLerp += colorLerpInc;
        curColor = lerpColor(baseColor, baseColorLerpTo, colorLerp);
      }
    }

    void visualizeConnection() {
      if(next != null) {
        drawConnectionLine(next.pos);
        if(noiseScalar1 == 0) {
          noiseScalar1 = random(1) > 0.5 ? -1 : 1;
          if(noiseScalar1 == 1) {
            noiseScalar2 = -1;
          } else {
            noiseScalar2 = 1;
          }
          cBezierNoise1 = random(bezierNoise, bezierNoise*2);
          cBezierNoise2 = random(bezierNoise, bezierNoise*2);
          cBezierNoise3 = random(bezierNoise, bezierNoise*2);
        }
      } else {
        noiseScalar1 = 0;
        noiseScalar2 = 0;
      }

    }

    void drawConnectionLine(PVector to) {
      stroke(curColor);
      float cX = to.x;
      float cY = to.y;
      float cZ = to.z;
      float scalar = map(soundTimer.progress() - getNextIntervall, 0, 1-getNextIntervall, 0 , 1);
      
      
      if(isDead()) {
        scalar = scalar * 1.1;
      }
      
      
      cX = scalar * (to.x - pos.x) + pos.x;
      cY = scalar * (to.y - pos.y) + pos.y;
      cZ = scalar * (to.z - pos.z) + pos.z;

      //line(cX, cY, cZ, pos.x, pos.y, pos.z);

      if(cOneUp) {
        ccBezierNoise1 += bezierInc;
        if(ccBezierNoise1 >= cBezierNoise1) {
          cOneUp = false;
        }
      } else {
        ccBezierNoise1 -= bezierInc;
        if(ccBezierNoise1 <= -cBezierNoise1) {
          cOneUp = true;
        }
      } 

      if(cTwoUp) {
        ccBezierNoise2 += bezierInc;
        if(ccBezierNoise2 >= cBezierNoise2) {
          cTwoUp = false;
        }
      } else {
        ccBezierNoise2 -= bezierInc;
        if(ccBezierNoise2 <= -cBezierNoise2) {
          cTwoUp = true;
        }
      } 

      if(cThreeUp) {
        ccBezierNoise3 += bezierInc;
        if(ccBezierNoise3 >= cBezierNoise3) {
          cThreeUp = false;
        }
      } else {
        ccBezierNoise3 -= bezierInc;
        if(ccBezierNoise3 <= -cBezierNoise3) {
          cThreeUp = true;
        }
      } 


      float cX1 = (cX - pos.x) * 0.3 + (noiseScalar1 * scalar * ccBezierNoise1) + pos.x;
      float cY1 = (cY - pos.y) * 0.3 + (noiseScalar1 * scalar * ccBezierNoise2) + pos.y;
      float cZ1 = (cZ - pos.z) * 0.3 + (noiseScalar1 * scalar * ccBezierNoise3) + pos.z;

      float cX2 = (cX - pos.x) * 0.7 + (noiseScalar2 * scalar * ccBezierNoise2) + pos.x;
      float cY2 = (cY - pos.y) * 0.7 + (noiseScalar2 * scalar * ccBezierNoise3) + pos.y;
      float cZ2 = (cZ - pos.z) * 0.7 + (noiseScalar2 * scalar * ccBezierNoise1) + pos.z;

      cX = cX + noiseScalar1 * ((1-scalar) >= 0 ? (1-scalar) : 0) * ccBezierNoise1/2;
      cY = cY + noiseScalar1 * ((1-scalar) >= 0 ? (1-scalar) : 0) * ccBezierNoise3/2;
      cZ = cZ + noiseScalar2 * ((1-scalar) >= 0 ? (1-scalar) : 0) * ccBezierNoise2/2;

      bezier(cX, cY, cZ, cX2, cY2, cZ2, cX1, cY1, cZ1, pos.x, pos.y, pos.z);
    }

}
