import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import processing.sound.*; 
import org.openkinect.freenect.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ZEN_levs extends PApplet {


// import utils




InteractionController interactionController;

Sound mySound;

FlowerList levs;

PApplet main;

public int fram = 0;
public boolean frameIsTracked = false;

public Curser curser;

int levsInRow = 2;

int maxDepth = 400;

int moveCounter = 0;

boolean withWandering = false;

public static final int IN_ORDER = 1;
public static final int WANDERING = 2;

int state = 1;

public void settings() {
  fullScreen(P3D);
//size(1920, 1080, P3D);
  //smooth(8);
  // set ColorMode to HSB


}

public void setup() {
    colorMode(HSB, 360, 100, 100, 100);

    main = this;
    mySound = new Sound(this);
    interactionController = new MouseController(this, mySound);
    interactionController.enableTracking();

    levs = new FlowerList();
    addlevs();

    curser = new Curser(new PVector());
}

public void addlevs() {
  levs.addlevs();
}

public void draw() {
    background(0);

    levs.run();

    interactionController.draw();
    drawCurser();
}

public void drawCurser() {
  curser.move(interactionController.getPosX(), interactionController.getPosY(), interactionController.getPosZ());
  curser.render();
}
public class Curser {

  PVector pos;
  int size = 50;

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
    fill(50, 100, 100, 50);
    pushMatrix();
    noStroke();
    translate(pos.x, pos.y, pos.z);
    ellipse(0, 0, size, size);
    popMatrix();
  }

}
class Flower {


    /*---------------------------------------------------------------
    Fields
    ----------------------------------------------------------------*/

    PVector pos;

    float angle1;
    float angle2;

    float angleForLevitate = random(0, TWO_PI);

    float angle1Inc = 0.007f;
    float angle2Inc = 0.06f;

    float angleForLevitateInc = random(0.02f, 0.06f);

    float scalar = 100;

    boolean collected = false;

    float collectDis = width*0.05f;

    int sizeA = 5;
    int sizeB = 30;
    int sizeC = 15;


    int amount;

    float speed = 0.1f;
    float vel = 0.02f;

    //Ani animationA;
    int faderOpacity = 100;

    int appearCounter = 0;
    int appearCounterStep = 10;

    float movSpeedX = 4.0f;
    float movSpeedY = 4.0f;
    float movSpeedZ = 4.0f;

    PVector homeComingVector;

    PVector basePos;

    boolean home = false;


    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    Flower(PVector p) {
        pos = new PVector();
        pos.set(p);
        basePos = new PVector();
        basePos.set(pos);
        amount = (int)random(1,9);
        setRandoMovSpeeds();
        //animationA = new Ani(this, 2, "faderOpacity", 100);
        //animationA.start();
    }


    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    public void setPos(PVector p) { pos.set(p); }
    public PVector getPos() { return pos; }
    public boolean isCollected() { return collected; }
    public void setAmount(int a) {amount = a; }

    public void render() {

        angleForLevitate += angleForLevitateInc;
        angle1 += angle1Inc;
        if (angleForLevitate >= TWO_PI) { angleForLevitate = 0; }
        if (angle1 >= TWO_PI) { angle1 = 0; }

        //pos.x += sin(angle1);
        pos.y += sin(angleForLevitate);
        //pos.z += sin(angle1);

        //if (boidList != null && !collected) { detect(boidList.getCenterOfSwarm()); }
    }

    public void setRandoMovSpeeds() {
      movSpeedX = random(0,4);
      movSpeedY = random(0,4);
      movSpeedZ = random(0,4);
    }

    public void move() {

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
      if(pos.y > width) {
        //pos.y = 0;
        movSpeedY = -movSpeedY;
      }
      if(pos.y < 0) {
        //pos.y = width;
        movSpeedY = -movSpeedY;
      }

      pos.z += movSpeedZ;
      if(pos.z > maxDepth) {
        //pos.z = -maxDepth;
        movSpeedZ = -movSpeedZ;
      }
      if(pos.z < -maxDepth) {
        //pos.z = maxDepth;
        movSpeedZ = -movSpeedZ;
      }
    }

    public void moveToBasePos() {
      if(homeComingVector == null) {
        homeComingVector = new PVector((basePos.x-pos.x)/180, (basePos.y-pos.y)/180, (basePos.z-pos.z)/180);
      }
      boolean one = true;
      if(abs(pos.x-basePos.x) > 20) {
        pos.x += homeComingVector.x;
        one = false;
      }
      boolean two = true;
      if(abs(pos.y-basePos.y) > 20) {
        pos.y += homeComingVector.y;
        two = false;
      }
      boolean three = true;
      if(abs(pos.z-basePos.z) > 20) {
        pos.z += homeComingVector.z;
        three = false;
      }
      home = one && two && three;
    }

    public boolean getHome() {
      return home;
    }

    public void resetHomeComingVector() {
      homeComingVector = null;
    }

    public void detect(PVector swarmCenter) {
        if (dist(pos.x, pos.y, pos.z, swarmCenter.x, swarmCenter.y, swarmCenter.z) < collectDis) {
            hitDetected();
        } else {
            collected = false;
        }
    }


    public void hitDetected() {
        collected = true;
        // Set highlight color
        /**
        for (int i=0; i<boidList.getBoidList().size(); i++) {
            Boid b = (Boid)boidList.getBoidList().get(i);
            b.setHighlightHSB("167/36/90");
        }
        */
    }

}
class FlowerA extends Flower {

  FlowerA(PVector p) {
      super(p);
      //animationA = new Ani(this, 2, "faderOpacity", 100);
      //animationA.start();
  }

  public void render() {
    super.render();

    noFill();

  pushMatrix();

      translate(pos.x, pos.y, pos.z);
      rotateZ(180);
      int inc = 360/amount;

      for (int i = 0; i < 360; i += inc) {
          float line = (1+i/10000) < 2 ? 2 : (1+i/20);
          strokeWeight(line);
          stroke(167, 36, 90, faderOpacity);
          //rotateX(angle1);
          rotateY((angle1)%360);
          float mySize = sizeA+i/10;
          rect(-mySize/2, -mySize/2, mySize, mySize);
      }

  popMatrix();
  }

}
public class FlowerList {

  ArrayList<Flower> levs;

  int offset = 50;

  public FlowerList() {
    levs = new ArrayList<Flower>();
    addlevs();
  }

  public void addlevs() {
    int zInc = (width-2*offset)/levsInRow;
    int xInc = (width-2*offset)/levsInRow;
    int yInc = (width-2*offset)/levsInRow;

    int max = width-offset;
    for(int z = offset; z < max; z += zInc) {
      for(int x = offset; x < max; x += xInc) {
        for(int y = offset; y < max; y += yInc) {
            levs.add(new FlowerA(new PVector(x, y, z)));
        }
      }
    }
  }

  public void run() {
    for(Flower f : levs) {
      switch (state) {
        case (IN_ORDER) :

          break;
        case (WANDERING) :
          f.move();
          break;

        case (GO_HOME) :


        default :
          f.render();
      }
    }
  }

  public void drawlevs() {
    for(Flower f : levs)  {
      f.render();
    }
  }

  public void wander() {
    moveCounter++;
    if(moveCounter < 500) {
      movelevs();
    } else {
      homelevs();
    }
    if(alllevsHome() || moveCounter > 1000) {
      moveCounter = 0;
      resetAllHomeComingVectors();
    }
  }

  public void movelevs() {
    for(Flower f : levs) {
      f.move();
    }
  }

  public void resetAllHomeComingVectors() {
    for(Flower f : levs) {
      f.resetHomeComingVector();
    }
  }

  public boolean alllevsHome() {
    for(Flower f : levs) {
      if(!f.getHome()) {
        return false;
      }
    }
    return true;
  }

  public void homelevs() {
    for(Flower f : levs) {
      f.moveToBasePos();
    }
  }

}
class InteractionController {


    /*---------------------------------------------------------------
    Fields
    ----------------------------------------------------------------*/

    float scaleFactor = 1;
    int radius = width;
    float deg = 0;
    boolean trackingEnabled = false;
    boolean speedEnabled = false;
    boolean divideEnabled = false;
    boolean explosionEnabled = false;
    int frame = 0;

    Sound mySound;

    PVector leftHand = new PVector();
    PVector rightHandIndex = new PVector();

    // Gestures
    PVector[] handHistoryRight = new PVector[20];
    PVector[] handHistoryLeft = new PVector[20];
    PVector rightVec, leftVec, frontVec, backVec;
    PVector thresholds = new PVector(height/10, width/10, 50);
    float explosionGestureMinDistance = 1.5f;
    int gestureThreshold = 5;
    int lastFramesConsidered = 5;

    Timer gestureRecTimer = new Timer();

    final static int NONE = 200;
    final static int SPEED = 201;
    final static int EXPLOSION = 202;
    final static int DIVIDE = 203;
    int lastGesture;


    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    InteractionController(Sound s) {

        mySound = s;

        // init for gestures
        for (int i = 0; i < handHistoryRight.length; i++) { handHistoryRight[i] = new PVector(); }
        for (int i = 0; i < handHistoryLeft.length; i++)  { handHistoryLeft[i] = new PVector(); }
        rightVec = new PVector((width/4)*3, height/4, 0);
        leftVec  = new PVector(width/4, height/4, 0);
        //frontVec = new PVector(width/2, height/2, upperZBorder - thresholds.z);
        //backVec  = new PVector(width/2, height/2, lowerZBorder + thresholds.z);

    }



    /*---------------------------------------------------------------
    Getter
    ----------------------------------------------------------------*/

    public float getPosX()                       { return rightHandIndex.x; }
    public float getPosY()                       { return rightHandIndex.y; }
    public float getPosZ()                       { return rightHandIndex.z; }
    public float getScaleFactor()                { return scaleFactor; }
    public float getNormalizedPosZ()             { return rightHandIndex.z; }
    public float getNormalizedPosZ(int a, int b) { return rightHandIndex.z; }

    public PVector getPosition(int hand) {
        if      (hand == LEFT)  { return leftHand; }
        else if (hand == RIGHT) { return rightHandIndex; }
        return null;
    }



    /*---------------------------------------------------------------
    Setter
    ----------------------------------------------------------------*/

    public void setPosX(float value) { rightHandIndex.x = (int)value; }
    public void setPosY(float value) { rightHandIndex.y = (int)value; }
    public void setPosZ(float value) { rightHandIndex.z = (int)value; }
    public void setPosXY(float a, float b) { setPosX(a); setPosY(b); }
    public void setPosXYZ(float a, float b, float c) { setPosXY(a, b); setPosZ(c); }




    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    public void draw() {

        if (trackingEnabled) { highlightBorders(); }
        //if (frameIsTracked) { updateHandHistory(); }

    }


    public void setup() { }





    public void enableTracking()  { trackingEnabled = true; }
    public void disableTracking() { trackingEnabled = false; }

    public void enableSpeedGesture()  { speedEnabled = true; }
    public void disableSpeedGesture() { speedEnabled = false; }

    public void enableDivideGesture()  { divideEnabled = true; }
    public void disableDivideGesture() { divideEnabled = false; }

    public void enableExplosionGesture()  { explosionEnabled = true; }
    public void disableExplosionGesture() { explosionEnabled = false; }

    public void enableAllGestures() {
        enableSpeedGesture();
        enableDivideGesture();
        enableExplosionGesture();
    }

    public void disableAllGestures() {
        disableSpeedGesture();
        disableDivideGesture();
        disableExplosionGesture();
    }


    public boolean isTracking() { return true; }



    public void highlightBorders() {

        float highlightPos = 0.2f;

        if (rightHandIndex.x < width*highlightPos) {
            float distanceFromBorderPercentage = rightHandIndex.x/width;
            int opacity = calcOpacity(distanceFromBorderPercentage, highlightPos);
            //gradientRect(0, 0, width*highlightPos, height, color(200, 60, 100, opacity), color(200, 60, 100, 0), false);
            mySound.playWallHit();
        }

        if (rightHandIndex.x > width*(1-highlightPos)) {
            float distanceFromBorderPercentage = 1-rightHandIndex.x/width;
            int opacity = calcOpacity(distanceFromBorderPercentage, highlightPos);
            //gradientRect(int(width*(1-highlightPos)), 0, width*highlightPos, height, color(200, 60, 100, 0), color(200, 60, 100, opacity), false);
            mySound.playWallHit();
        }

        if (rightHandIndex.y < height*highlightPos) {
            float distanceFromBorderPercentage = rightHandIndex.y/height;
            int opacity = calcOpacity(distanceFromBorderPercentage, highlightPos);
            //gradientRect(0, 0, width, height*highlightPos, color(200, 60, 100, opacity), color(200, 60, 100, 0), true);
            mySound.playWallHit();
        }

        if (rightHandIndex.y > height*(1-highlightPos)) {
            float distanceFromBorderPercentage = 1-rightHandIndex.y/height;
            int opacity = calcOpacity(distanceFromBorderPercentage, highlightPos);
            //gradientRect(0, int(height*(1-highlightPos)), width, height*highlightPos, color(200, 60, 100, 0), color(200, 60, 100, opacity), true);
            mySound.playWallHit();
        }

    }



    public int calcOpacity(float dist, float hP) {
        return (int)(((hP-dist)*100)*0.5f);
    }





    /*---------------------------------------------------------------
    Gestures
    ----------------------------------------------------------------*/


    public void updateHandHistory() {

        //updadate handHistoryRight
        for (int i = handHistoryRight.length-1; i > 0; i--) {
            handHistoryRight[i] = handHistoryRight[i-1];
            handHistoryRight[0] = interactionController.getPosition(RIGHT);
        }

        //update handHistoryLeft
        for (int i = handHistoryLeft.length-1; i > 0; i--) {
            handHistoryLeft[i] = handHistoryLeft[i-1];
            handHistoryLeft[0] = interactionController.getPosition(LEFT);
        }

    }


    public void swoosh() {
        //play swoosh if appropriate
        if (dist(handHistoryRight[0].x, handHistoryRight[0].y, handHistoryRight[0].z, handHistoryRight[handHistoryRight.length-1].x, handHistoryRight[handHistoryRight.length-1].y,  handHistoryRight[handHistoryRight.length-1].z) > mySound.swooshTreshold) {
            //mySound.playSwoosh();
        }
    }


    public int getSpeedInt() { return SPEED; }
    public int getDivideInt() { return DIVIDE; }
    public int getExplosionInt() { return EXPLOSION; }

    public boolean gesturePerformedLast(int gest) {
        return (lastGesture == gest);
    }


    public boolean speedGestureTriggered() {
        if (gestureRecTimer.finished() && speedEnabled && detectSpeedGesture()) {
            gestureRecTimer.setAndStart(10);
            lastGesture = SPEED;
            return true;
        } else { return false; }
    }

    public boolean divideGestureTriggered() {
        if (gestureRecTimer.finished() && divideEnabled && detectDivideGesture()) {
            gestureRecTimer.setAndStart(10);
            lastGesture = DIVIDE;
            return true;
        } else { return false; }
    }

    public boolean explosionGestureTriggered() {
        if (gestureRecTimer.finished() && explosionEnabled && detectExplosionGesture()) {
            gestureRecTimer.setAndStart(10);
            lastGesture = EXPLOSION;
            return true;
        } else { return false; }
    }

    public boolean gestureReseted() {
        if (gestureRecTimer.finished() && (lastGesture != NONE)) {
            lastGesture = NONE;
            return true;
        } else { return false; }
    }


    public boolean detectSpeedGesture() {

        boolean left = false;
        boolean right = false;
        PVector aux = new PVector();

        // look at first frames considered in handHistoryRight
        for (int i = 0; i < lastFramesConsidered; i++) {
            //is there any vector close to starting x left ?
            if (abs(handHistoryRight[i].x - leftVec.x) < thresholds.y) {
                left = true;
                //set current starting vector of gesture
                aux.set(handHistoryRight[i]);
                break;
            }
            //is there any vector close to starting x right ?
            if (abs(handHistoryRight[i].x - rightVec.x) < thresholds.y) {
                right = true;
                //set current starting vector of gesture
                aux.set(handHistoryRight[i]);
                break;
            }
        }

        // look at last frames considered in handHistoryRight
        for (int i = handHistoryRight.length-1; i > handHistoryRight.length - 1 - lastFramesConsidered; i--) {
            if (left && abs(handHistoryRight[i].x - rightVec.x) < thresholds.y && (abs(handHistoryRight[i].y - aux.y) < thresholds.x)) {
                right = true;
                break;
            }
            if (right && (abs(handHistoryRight[i].x - leftVec.x) < thresholds.y) && (abs(handHistoryRight[i].y - aux.y) < thresholds.x)) {
                left = true;
                break;
            }
        }

        return left && right;

    }


    public boolean detectDivideGesture() {

        boolean rightInMid = false;
        boolean leftInMid = false;
        boolean rightIsRight = false;
        boolean leftIsLeft = false;
        PVector aux = new PVector();

        // look at first frames considered in both handHistories
        for (int i = handHistoryRight.length-1; i > handHistoryRight.length - 1 - lastFramesConsidered; i--) {
            if (abs(handHistoryRight[i].x - handHistoryLeft[i].x) < width/20) {
                if (abs(handHistoryRight[i].x - width/2) < thresholds.y) {
                    rightInMid = true;
                    leftInMid = true;
                    aux.set(handHistoryRight[i]);
                    break;
                }
            }
        }

        // look at last frames considered in both handHistories
        for (int i = 0; i < lastFramesConsidered; i++) {
            if (rightInMid && (abs(handHistoryRight[i].x - rightVec.x) < thresholds.y) && abs(handHistoryRight[i].y - aux.y) < thresholds.x) {
                rightIsRight = true;
            }
            if (leftInMid && (abs(handHistoryLeft[i].x - leftVec.x) < thresholds.y) && abs(handHistoryLeft[i].y - aux.y) < thresholds.x) {
                leftIsLeft = true;
            }
        }

        return rightInMid && leftInMid && rightIsRight && leftIsLeft;

    }


/**
    boolean detectExplosionGesture() {

        // back
        boolean back = false;
        for (int i = 0; i < lastFramesConsidered; i++) {
            if((abs(handHistoryRight[i].z - backVec.z) < thresholds.z)) {
                back = true;
                break;
            }
        }

        // front
        boolean front = false;
        for(int i = handHistoryRight.length-1; i > handHistoryRight.length -1 - lastFramesConsidered; i--) {
            if((abs(handHistoryRight[i].z - frontVec.z) < thresholds.z)) {
                front = true;
                break;
            }
        }

        return back && front;

    }

*/

    public boolean detectExplosionGesture() {

        for(int i = 0; i < lastFramesConsidered; i++) {
            for(int j = handHistoryRight.length-1; j > handHistoryRight.length -1 - lastFramesConsidered; j--) {
                //if dist between last frames and first frames is bigger than z-depth/2
/**
                if((abs(handHistoryRight[i].z - handHistoryRight[j].z) > ((abs(upperZBorder) + abs(lowerZBorder))/explosionGestureMinDistance))) {
                    return true;
                }
                */
            }
        }
        return false;
    }



}
class MouseController extends InteractionController {

    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    MouseController(PApplet pa, Sound s) {
        super(s);
    }


    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    public void draw() {

        frame++;

        if (frame == 3) {
            frameIsTracked = true;
            frame = 0;
        } else {
            frameIsTracked = false;
        }

        if (trackingEnabled) {

            rightHandIndex.x = mouseX;
            rightHandIndex.y = mouseY;

            if (keyPressed) {

                if (key == 'e'||key == 'E') {
                    rightHandIndex.z -= 10;
                    //if(rightHandIndex.z < lowerZBorder) { rightHandIndex.z = lowerZBorder; }
                }

                if (key == 'd'||key == 'D') {
                    rightHandIndex.z += 10;
                    //if (rightHandIndex.z > upperZBorder) { rightHandIndex.z = upperZBorder; }
                }
            }

            leftHand.x = width-mouseX;
            leftHand.y = height-mouseY;
            leftHand.z = rightHandIndex.z;

        }

        super.draw();

    }

}
class Sound {

    /*---------------------------------------------------------------
    Audio files
    ----------------------------------------------------------------*/

    SoundFile eat, swoosh, spawn, despawn, divide, revelation, speedUp, wallHit, explosion, lightbulb;
    SoundFile[] aArray, bArray;

    int aCounter = 600;
    int aCounterLimit = 600;
    float swooshTreshold = width/2;
    int lastTrackA;


    /*---------------------------------------------------------------
    Sound Triggers
    ----------------------------------------------------------------*/




    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    public Sound(PApplet sketch) {
        soundSetup(sketch);
        //wallHitTimer = new Timer(1);
        //lightbulbTimer = new Timer(3);
    }

    public void soundSetup(PApplet sketch) {


    }



    public void playWallHit() {

    }

}
class Timer {


    /*---------------------------------------------------------------
    Fields
    ----------------------------------------------------------------*/

    int endtime, duration;
    boolean started = false;



    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    Timer() { }
    Timer(float d) { set(d); }


    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    public void start() {
        started = true;
        endtime = millis() + duration;
    }

    public boolean started() {
        return started;
    }

    public boolean startedAndFinished() {
        return (started() && finished());
    }

    public void set(float d) {
        duration = PApplet.parseInt(d*1000);
    }

    public void setAndStart(float d) {
        set(d);
        start();
    }

    public boolean finished() {
        boolean r = false;
        if (endtime <= millis()) { r = true; }
        return r;
    }

    public float progress() {
        return map(millis(), endtime-duration, endtime, 0, 1);
    }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ZEN_levs" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
