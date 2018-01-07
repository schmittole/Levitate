import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import processing.sound.*; 
import org.openkinect.freenect.*; 
import de.voidplus.leapmotion.*; 
import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Levitate extends PApplet {

// import utils




InteractionController interactionController;

LevList levs;

PApplet main;

public Curser curser;

int lowerZ = 0;
int upperZ = 1000;
int maxX = 1280;
int maxY = 800;

int moveCounter = 0;

boolean withWandering = false;

public static final int IN_ORDER = 1;
public static final int WANDERING = 2;
public static final int GO_HOME = 3;

int state = 2;

int lastState = 1;

boolean drawC = true;

int lowerColor = 60;
int upperColor = 360;


String[] soundStringsA;

boolean noHands = false;

boolean makeLev = false;

boolean clearAll = false;
boolean clearA = false;
boolean clearB = false;
boolean clearC = false;

int maxlevsPerClass = 4;

int disToHand = 100;

float tagDis = width*0.2f;

boolean couldTag = false;

int backgroundOpacity = 10;

PVector baseColor1Vec = new PVector(0, 0, 0);

int baseColor1 = color(baseColor1Vec.x, baseColor1Vec.y, baseColor1Vec.z, backgroundOpacity);

int lerpMax = 30;

PVector baseColor2Vec = new PVector(lerpMax, lerpMax, lerpMax);

int baseColor2 = color(baseColor2Vec.x, baseColor2Vec.y, baseColor2Vec.z, backgroundOpacity);

PVector curColorVec = new PVector(baseColor1Vec.x, baseColor1Vec.y, baseColor1Vec.z);

int curColor = color(baseColor1Vec.x, baseColor1Vec.y, baseColor1Vec.z, backgroundOpacity);

float shiftFactor = 0.0f;

float shiftFactorInc = 0.001f;

Timer backgroundColorTimer = new Timer(5);

boolean shiftComplete = false;

int yOffsetToHand = -200;

ArrayList<Spawnevent> spawnEvents = new ArrayList<Spawnevent>();

ArrayList<SpawneventTwo> spawnEventsTwo = new ArrayList<SpawneventTwo>();

Spawnevent removeThisSpawnEvent = null;

SpawneventTwo removeThisSpawnEventTwo = null;

SoundFile swipeSound;

SoundFile swoosh;

Timer swooshTimer;

String[] soundStringsB;

String[] soundStringsC;

String[] soundStringsD;

String[] soundStringsEnlight;

String[] soundStringsPlant;

Timer screenTapTimer;

public void settings() {
  fullScreen(P3D);
}

public void setup() {
    maxX = width;
    maxY = height;
    colorMode(HSB, 360, 100, 100, 100);
    backgroundColorTimer.start();
    //camera(width/2.0, height/2.0, (height/2.0) / tan(PI*30.0 / 180.0)*1000, width/2.0, height/2.0, 0, 0, 1, 0);
    camera(width/2.0f, height/2.0f, (height/2.0f) / tan(PI*30.0f / 180.0f)*2, width/2.0f, height/2.0f, 0, 0, 1, 0);    

    main = this;
    interactionController = new LeapMotionController(this);

    levs = new LevList(main);

    curser = new Curser(new PVector());

    swoosh = new SoundFile(this, "swoosh.mp3");

    soundStringsA = new String[7];
    soundStringsA[0] = "A_01.mp3";
    soundStringsA[1] = "A_02.mp3";
    soundStringsA[2] = "A_03.mp3";
    soundStringsA[3] = "A_04.mp3";
    soundStringsA[4] = "A_05.mp3";
    soundStringsA[5] = "A_06.mp3";
    soundStringsA[6] = "A_07.mp3";

    soundStringsB = new String[7];
    soundStringsB[0] = "A_01.mp3";
    soundStringsB[1] = "A_02.mp3";
    soundStringsB[2] = "A_03.mp3";
    soundStringsB[3] = "A_04.mp3";
    soundStringsB[4] = "A_05.mp3";
    soundStringsB[5] = "A_06.mp3";
    soundStringsB[6] = "A_07.mp3";

    soundStringsC = new String[7];
    soundStringsC[0] = "C_01.wav";
    soundStringsC[1] = "C_02.wav";
    soundStringsC[2] = "C_03.wav";
    soundStringsC[3] = "C_04.wav";
    soundStringsC[4] = "C_05.wav";
    soundStringsC[5] = "C_06.wav";
    soundStringsC[6] = "C_07.wav";

    soundStringsD = new String[7];
    soundStringsD[0] = "D_01.wav";
    soundStringsD[1] = "D_02.wav";
    soundStringsD[2] = "D_03.wav";
    soundStringsD[3] = "D_04.wav";
    soundStringsD[4] = "D_05.wav";
    soundStringsD[5] = "D_06.wav";
    soundStringsD[6] = "D_07.wav";

    soundStringsPlant = new String[7];
    soundStringsPlant[0] = "C_01.wav";
    soundStringsPlant[1] = "C_02.wav";
    soundStringsPlant[2] = "C_03.wav";
    soundStringsPlant[3] = "C_04.wav";
    soundStringsPlant[4] = "C_05.wav";
    soundStringsPlant[5] = "C_06.wav";
    soundStringsPlant[6] = "C_07.wav";

    soundStringsEnlight = new String[3];
    soundStringsEnlight[0] = "enlight_01.wav";
    soundStringsEnlight[1] = "enlight_02.wav";
    soundStringsEnlight[2] = "enlight_03.wav";

    swipeSound = new SoundFile(this, "swipe02_f.wav");
    swipeSound.amp(0.2f);

    screenTapTimer = new Timer(1);
    screenTapTimer.start();

}



public void setBaseColor1(PVector c) {
  baseColor1 = color(c.x, c.y, c.z, backgroundOpacity);
  baseColor1Vec = new PVector(c.x, c.y, c.z);
}

public void setBaseColor2(PVector c) {
  baseColor2 = color(c.x, c.y, c.z, backgroundOpacity);
  baseColor2Vec = new PVector(c.x, c.y, c.z);
}

public void shiftCurColor() {
  curColor = lerpColor(baseColor1, baseColor2, shiftFactor);
  shiftFactor += shiftFactorInc;

  if(shiftFactor > 1-abs(shiftFactorInc) || shiftFactor < abs(shiftFactorInc)) {
    shiftFactorInc = -shiftFactorInc;
    if(shiftFactorInc > 0) {
      setBaseColor2(new PVector(PApplet.parseInt(random(lerpMax)), PApplet.parseInt(random(lerpMax)), PApplet.parseInt(random(lerpMax))));
      shiftComplete = true;
    }
  }
}

public void checkSpawnEvents() {
  if(removeThisSpawnEvent != null) {
    spawnEvents.remove(removeThisSpawnEvent);
    removeThisSpawnEvent = null;
  }
}

public void addMeToToDelete(Spawnevent s) {
  removeThisSpawnEvent = s;
}

public void checkSpawnEventsTwo() {
  if(removeThisSpawnEventTwo != null) {
    spawnEventsTwo.remove(removeThisSpawnEventTwo);
    removeThisSpawnEventTwo = null;
  }
}

public void addMeToToDeleteTwo(SpawneventTwo s) {
  removeThisSpawnEventTwo = s;
}

public void draw() {

    noCursor();
    colorMode(RGB, 100);
    background(curColor);
    if(backgroundColorTimer.finished()) {
      if(!shiftComplete) {
        shiftCurColor();
      } else {
        backgroundColorTimer = new Timer(PApplet.parseInt(random(3,7)));
        shiftComplete = false;
      }
    } 
    colorMode(HSB, 360, 100, 100, 100);


    levs.run();
    checkSpawnEvents();
    for(Spawnevent sE : spawnEvents) {
      sE.run();
    }
    checkSpawnEventsTwo();
    for(SpawneventTwo sE : spawnEventsTwo) {
      sE.run();
    }


    interactionController.draw();

    setCurserColor(); 
    drawCurser();

    if(currentlevsInPlaneA() <= maxlevsPerClass && makeLev && makeLevA()) {
      playLevPlantSound();
      levs.addSingleLev(new LevA(interactionController.getPosition(RIGHT), soundStringsA[PApplet.parseInt(random(soundStringsA.length))], this));
    }

    if(currentlevsInPlaneB() <= maxlevsPerClass && makeLev && makeLevB()) {
      playLevPlantSound();
      levs.addSingleLev(new LevB(interactionController.getPosition(RIGHT), soundStringsB[PApplet.parseInt(random(soundStringsB.length))], this));
    }

    if(currentlevsInPlaneC() <= maxlevsPerClass && makeLev && makeLevC()) {
      playLevPlantSound();
      levs.addSingleLev(new LevC(interactionController.getPosition(RIGHT), soundStringsA[PApplet.parseInt(random(soundStringsA.length))], this));
    }

    if(interactionController.carry) {
      ArrayList<Lev> toHand = new ArrayList<Lev>();
      toHand = findlevsNextToHand();
      for(Lev f : toHand) {
        f.moveTo(interactionController.rightHandPos);
      }
    }
    

    if(clearAll) {
      clearAll();
      playSwipeSound();
    }
    if(clearC) {
      clearC();
      playSwipeSound();
    }
    if(clearB) {
      clearB();
      playSwipeSound();
    }
    if(clearA) {
      clearA();
      playSwipeSound();
    }

    resetAllCommands();
}

public void playLevPlantSound() {
  playRandomPlant();
}

public void playRandomPlant() {
  SoundFile aux = new SoundFile(this, soundStringsPlant[PApplet.parseInt(random(soundStringsPlant.length))]);
  aux.amp(0.3f);
  aux.play();
}

public void playRandomB() {
  SoundFile aux = new SoundFile(this, soundStringsB[PApplet.parseInt(random(soundStringsB.length))]);
  aux.amp(0.3f);
  aux.play();
}

public void playRandomC() {
  SoundFile aux = new SoundFile(this, soundStringsC[PApplet.parseInt(random(soundStringsC.length))]);
  aux.amp(0.3f);
  aux.play();
}

public ArrayList<Lev> findlevsNextToHand() {
  ArrayList<Lev> list = new ArrayList<Lev>();
  for(Plane p : levs.getPlanes()) {
    for(Lev f : p.getlevs()) {
      if(dist(f.pos.x, f.pos.y, f.pos.z, interactionController.rightHandPos.x, interactionController.rightHandPos.y , interactionController.rightHandPos.z) < disToHand) {
        list.add(f);
      }
   }
  }
  return list;
}

public void setCurserColor() {
  if(makeLevA()) {
    curser.setMyColor("triangle");
  } else if(makeLevB()) {
    curser.setMyColor("circle");
  } else if(makeLevC()) {
    curser.setMyColor("rectangle");
  } else if(interactionController.carry) {
    curser.setMyColor("NONE");
  } else {
    curser.setMyColor("normal");
  }
}

public void playSwipeSound() {
  swipeSound.play();
}

public void clearA() {
  levs.getPlanes().get(0).oneIsTagged = false;
  levs.getPlanes().get(0).getlevs().clear();
}

public void clearB() {
  levs.getPlanes().get(1).oneIsTagged = false;
  levs.getPlanes().get(1).getlevs().clear();
}

public void clearC() {
  levs.getPlanes().get(2).oneIsTagged = false;
  levs.getPlanes().get(2).getlevs().clear();
}

public void clearAll() {
    levs.getPlanes().get(0).oneIsTagged = false;
    levs.getPlanes().get(1).oneIsTagged = false;
    levs.getPlanes().get(2).oneIsTagged = false;
    levs.getPlanes().get(3).oneIsTagged = false;
    levs.getPlanes().get(4).oneIsTagged = false;
    levs.getPlanes().get(5).oneIsTagged = false;

    ArrayList<Lev> listA = levs.getPlanes().get(0).getlevs();
    ArrayList<Lev> listB = levs.getPlanes().get(1).getlevs();
    ArrayList<Lev> listC = levs.getPlanes().get(2).getlevs();
    ArrayList<Lev> listD = levs.getPlanes().get(3).getlevs();
    ArrayList<Lev> listE = levs.getPlanes().get(4).getlevs();
    ArrayList<Lev> listF = levs.getPlanes().get(5).getlevs();

    listA.removeAll(listA);
    listB.removeAll(listB);
    listC.removeAll(listC);
    listD.removeAll(listD);
    listE.removeAll(listE);
    listF.removeAll(listF);

    spawnEvents.clear();
    spawnEventsTwo.clear();
}

public int currentlevsInPlaneA() {
  return levs.getPlanes().get(0).getlevs().size();
}

public int currentlevsInPlaneB() {
  return levs.getPlanes().get(1).getlevs().size();
}

public int currentlevsInPlaneC() {
  return levs.getPlanes().get(2).getlevs().size();
}

public boolean makeLevA() {
  if(interactionController.leftIndexStretched && !interactionController.leftMidStretched && !interactionController.leftRingStretched && !interactionController.leftPinkyStretched) {
    return true;
  }
  return false;
}

public boolean makeLevB() {
  if(interactionController.leftIndexStretched && interactionController.leftMidStretched && !interactionController.leftRingStretched && !interactionController.leftPinkyStretched) {
    return true;
  }
  return false;
}

public boolean makeLevC() {
  if(interactionController.leftIndexStretched && interactionController.leftMidStretched && interactionController.leftRingStretched && !interactionController.leftPinkyStretched) {
    return true;
  }
  return false;
}

public void resetAllCommands() {
    makeLev = false;
    clearAll = false;
    clearA = false;
    clearB = false;
    clearC = false;
    couldTag = false;
}

public void drawCurser() {
  if(drawC && !noHands) {
    curser.move(interactionController.rightHandIndex.x, interactionController.rightHandIndex.y, interactionController.rightHandIndex.z);
    curser.render();
}
}

public String getRandomSound() {
  return soundStringsA[PApplet.parseInt(random(4))];
}

public void playSwoosh() {
  if(swooshTimer == null) {
    swooshTimer = new Timer(swoosh.duration());
    swooshTimer.start();
    swoosh.play();
  }
  if(swooshTimer.finished()) {
    swooshTimer = null;
    }
}




// ======================================================// ======================================================
// FUNCTIONS FOR LEAP MOTION DEVICE
// ======================================================// ======================================================

// ======================================================
// 1. Swipe Gesture

public void leapOnSwipeGesture(SwipeGesture g, int state){
  int     id               = g.getId();
  Finger  finger           = g.getFinger();
  PVector position         = g.getPosition();
  PVector positionStart    = g.getStartPosition();
  PVector direction        = g.getDirection();
  float   speed            = g.getSpeed();
  long    duration         = g.getDuration();
  float   durationSeconds  = g.getDurationInSeconds();

  switch(state){
    case 1: // Start
      break;
    case 2: // Update
      break;
    case 3: // Stop

    //which delete command should be performed?
    if(interactionController.allLeftFingersStretched()) {
      clearAll = true;
    } else if(interactionController.leftIndexStretched() && interactionController.leftMidStretched() && interactionController.leftRingStretched()) {
      clearC = true;
    } else if(interactionController.leftIndexStretched() && interactionController.leftMidStretched()) {
      clearB = true;
    } else if(interactionController.leftIndexStretched()) {
      clearA = true;
    }
      break;
  }
}


// ======================================================
// 3. Screen Tap Gesture

public void leapOnScreenTapGesture(ScreenTapGesture g){
  int     id               = g.getId();
  Finger  finger           = g.getFinger();
  PVector position         = g.getPosition();
  PVector direction        = g.getDirection();
  long    duration         = g.getDuration();
  float   durationSeconds  = g.getDurationInSeconds();

  println("ScreenTapGesture: " + id);

  //user can activate lev?
  if(interactionController.noFingerStretched()) {
    interactionController.enableTagging();
    //user can plant new lev?
  } else if(screenTapTimer.finished()) {
    interactionController.disableTagging();
    makeLev = true;
    screenTapTimer = new Timer(0.5f);
    screenTapTimer.start();
  }
}
public class Curser {

  PVector pos;
  int size = 30;
  int myColor = color(100, 100, 100, 100);

    float angle1;
    float angle2;
    float angle3;

    float angle1Inc = 0.007f;
    float angle2Inc = 0.006f;
    float angle3Inc = 0.009f;

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
  public void polygon(float x, float y, float radius, int npoints) {
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
class InteractionController {


    /*---------------------------------------------------------------
    Fields
    ----------------------------------------------------------------*/

    float scaleFactor = 1;
    int radius = width;
    float deg = 0;

    boolean taggingEnabled = false;
    int frame = 0;


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

    boolean leftIndexStretched = false;
    boolean leftMidStretched = false;
    boolean leftRingStretched = false;
    boolean leftPinkyStretched = false;

    boolean carry = false;

    PVector rightHandIndexPosition = new PVector();

    PVector rightHandPos = new PVector();


    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    InteractionController() {


        // init for gestures
        for (int i = 0; i < handHistoryRight.length; i++) { handHistoryRight[i] = new PVector(); }
        for (int i = 0; i < handHistoryLeft.length; i++)  { handHistoryLeft[i] = new PVector(); }
        rightVec = new PVector((width/4)*3, height/4, 0);
        leftVec  = new PVector(width/4, height/4, 0);
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

    public boolean noFingerStretched() {
        return !(leftRingStretched | leftPinkyStretched | leftMidStretched | leftIndexStretched);
    }



    public void setState(int s) { 
            lastState = state;
            state = s; 
    }

    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    public void draw() {

    }


    public void setup() { }






    public void enableTagging() { taggingEnabled = true; }
    public void disableTagging() { taggingEnabled = false; }

    public boolean isTagging() { return taggingEnabled; }

    public boolean allLeftFingersStretched() {
        return leftIndexStretched && leftMidStretched && leftRingStretched && leftPinkyStretched;
    }

    public boolean leftIndexStretched() {
        return leftIndexStretched;
    }

    public boolean leftMidStretched() {
        return leftMidStretched;
    }

    public boolean leftRingStretched() {
        return leftRingStretched;
    }

    public boolean leftPinkyStretched() {
        return leftPinkyStretched;
    }


}



class LeapMotionController extends InteractionController {

    LeapMotion leap;

    int leapMinX = -50;
    int leapMaxX = 650;

    int leapMinY = 150;
    int leapMaxY = 450;

    int leapMinZ = 20;
    int leapMaxZ = 80;



    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    LeapMotionController(PApplet pa) {
        super();
        leap = new LeapMotion(pa);
        leap.allowGestures();
    }


    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    public void draw() {

        resetAllStretchedBooleans();

            for(Hand hand : leap.getHands()) {

                //right Hand
                if(hand.isRight()) {
                    rightHandPos.x = map(hand.getPosition().x, leapMinX, leapMaxX, 0, maxX);
                    rightHandPos.y = map(hand.getPosition().y, leapMinY, leapMaxY, 0, maxY);
                    rightHandPos.z = map(hand.getPosition().z, leapMinZ, leapMaxZ, upperZ, lowerZ);
                    draw(hand);
                    checkCarry(hand);
                    for(Finger finger : hand.getFingers()) {
                        //index Finger
                        if(finger.getType() == 1) {

                            float curX = finger.getPosition().x;
                            curX = curX < leapMinX ? leapMinX : curX > leapMaxX ? leapMaxX : curX;
                            float curY = finger.getPosition().y;
                            curY = curY < leapMinY ? leapMinY : curY > leapMaxY ? leapMaxY : curY;
                            float curZ = finger.getPosition().z;
                            curZ = curZ < leapMinZ ? leapMinZ : curZ > leapMaxZ ? leapMaxZ : curZ;

                            //map position to space
                            rightHandIndex.x = map(curX, leapMinX, leapMaxX, 0, maxX);
                            rightHandIndex.y = map(curY, leapMinY, leapMaxY, 0, maxY);
                            rightHandIndex.z = map(curZ, leapMinZ, leapMaxZ, upperZ, lowerZ);
                            break;    
                        }
                    }
                    //left Hand
                } else {
                    //get current id for each finger
                    int curIndexId = hand.getFinger(1).getId();
                    int curMidId = hand.getFinger(2).getId();
                    int curRingId = hand.getFinger(3).getId();
                    int curPinkyId = hand.getFinger(4).getId();

                    //draw each outstretched finger
                    for(Finger f : hand.getOutstretchedFingers()) {
                        stroke(150, 100, 100, 50);
                        if(f.getId() == curIndexId) {
                            leftIndexStretched = true;
                            draw(f);
                        }
                        if(f.getId() == curMidId) {
                            leftMidStretched = true;
                            draw(f);
                        }  
                        if(f.getId() == curRingId) {
                            leftRingStretched = true;
                            draw(f);
                        }   
                        if(f.getId() == curPinkyId) {
                            leftPinkyStretched = true;
                            draw(f);
                        }  
                    }
                }
            }

            //check if there is no hands
            if(leap.getHands().size() == 0) {
                noHands = true;
            } else {
                noHands = false;
            }


            if (keyPressed) {
                //set states
                if (key > '0' && key < '4') {
                    setState(key-48);
                }
                //draw cursor
                if (key == 'c' || key == 'C') {
                    drawC = true;
                }
                if (key == 'x' || key == 'x') {
                    drawC = false;
                }
            }
        super.draw();

    }

    //check if hand should carry object
    public void checkCarry(Hand hand) {
        if(hand.getIndexFinger() != null && hand.getPinkyFinger() != null && hand.getIndexFinger().getPosition().x > hand.getPinkyFinger().getPosition().x) {
            carry = true;
        } else {
            carry = false;
        }
    }

    public void resetCarryBoolean() {
        carry = false;
    }


    public void resetAllStretchedBooleans() {
        leftRingStretched = false;
        leftPinkyStretched = false;
        leftMidStretched = false;
        leftIndexStretched = false;
    }

    public void draw(Hand hand) {
        int radius = 5;
        PVector position = hand.getPosition();

        stroke(0, 100, 100, 50);
        fill(0, 100, 100, 50);
        pushMatrix();
            rightHandIndexPosition.x = getMappedX(position.x);
            rightHandIndexPosition.y = getMappedY(position.y);
            rightHandIndexPosition.z = getMappedZ(position.z);
        translate(getMappedX(position.x), getMappedY(position.y), getMappedZ(position.z));
        popMatrix();

        // Are there any fingers?
        if (hand.hasFingers()) {
            if (hand.countFingers() == 5) {
                PVector lastJointOfThumb = hand.getThumb().getProximalBone().getPrevJoint();
                PVector lastJointOfIndex = hand.getIndexFinger().getMetacarpalBone().getPrevJoint();
                PVector lastJointOfMiddle = hand.getMiddleFinger().getMetacarpalBone().getPrevJoint();
                PVector lastJointOfRing = hand.getRingFinger().getMetacarpalBone().getPrevJoint();
                PVector lastJointOfPinky = hand.getPinkyFinger().getMetacarpalBone().getPrevJoint();

                beginShape();

                    vertex(getMappedX(lastJointOfThumb.x), getMappedY(lastJointOfThumb.y), getMappedZ(lastJointOfThumb.z));
                    vertex(getMappedX(lastJointOfIndex.x), getMappedY(lastJointOfIndex.y), getMappedZ(lastJointOfIndex.z));
                    vertex(getMappedX(lastJointOfMiddle.x), getMappedY(lastJointOfMiddle.y), getMappedZ(lastJointOfMiddle.z));
                    vertex(getMappedX(lastJointOfRing.x), getMappedY(lastJointOfRing.y), getMappedZ(lastJointOfRing.z));
                    vertex(getMappedX(lastJointOfPinky.x), getMappedY(lastJointOfPinky.y), getMappedZ(lastJointOfPinky.z));

                endShape(PConstants.OPEN);
            }
            for (Finger finger : hand.getFingers()) {
                if(hand.isLeft() && hand.getOutstretchedFingers().size() > 0 && hand.getOutstretchedFingers().contains(finger)) {
                    stroke(0, 100, 100, 50);
                    draw(finger);
                } else if (hand.isRight()) {
                    stroke(0, 100, 100, 50);
                    draw(finger);
                }
            }

        }
    }

    public void draw(Finger finger) {
        drawBones(finger);
    }

    public void drawBones(Finger finger) {
        draw(finger.getDistalBone());
        draw(finger.getIntermediateBone());
        draw(finger.getMetacarpalBone());
        //it's not the thumb? (id == 0)
        if (finger.getType() != 0) {
            draw(finger.getProximalBone());
        }
    }

    public void draw(Bone bone) {
        PVector next = bone.getNextJoint();
        PVector prev = bone.getPrevJoint();

        beginShape(PConstants.LINES);
            vertex(getMappedX(next.x), getMappedY(next.y), getMappedZ(next.z));
            vertex(getMappedX(prev.x), getMappedY(prev.y), getMappedZ(prev.z));
        endShape(PConstants.OPEN);
    }

    public float getMappedZ(float zIn) {
        float curZ = zIn;
        curZ = curZ < leapMinZ ? leapMinZ : curZ > leapMaxZ ? leapMaxZ : curZ;
        return map(curZ, leapMinZ, leapMaxZ, upperZ, lowerZ);
    }

    public float getMappedX(float xIn) {
        float curX = xIn;
        curX = curX < leapMinX ? leapMinX : curX > leapMaxX ? leapMaxX : curX;
        return map(curX, leapMinX, leapMaxX, 0, maxX);
    }

    public float getMappedY(float yIn) {
        float curY = yIn;
        curY = curY < leapMinY ? leapMinY : curY > leapMaxY ? leapMaxY : curY;
        return map(curY, leapMinY, leapMaxY, 0, maxY);
    }
}
class MouseController extends InteractionController {

    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    MouseController(PApplet pa) {
        super();
    }


    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    public void draw() {



            rightHandIndex.x = mouseX;
            rightHandIndex.y = mouseY;

            disableTagging();

            if (keyPressed) {

                if (key == 'e'||key == 'E') {
                    rightHandIndex.z -= 10;
                    if(rightHandIndex.z < lowerZ) { rightHandIndex.z = lowerZ; }
                }

                if (key == 'd'||key == 'D') {
                    rightHandIndex.z += 10;
                    if (rightHandIndex.z > upperZ) { rightHandIndex.z = upperZ; }
                }

                if (key == 't'||key == 'T') {
                    enableTagging();
                }

                if (key > '0' && key < '4') {
                    setState(key-48);
                }

                if (key == 'c' || key == 'C') {
                    drawC = true;
                }

                if (key == 'x' || key == 'x') {
                    drawC = false;
                }
            }

        super.draw();

    }

}


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
    float angle1Inc = 0.007f;
    float angle2Inc = 0.06f;

    //increments. (speeds)
    float angleForLevitateInc = random(0.02f, 0.06f);

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
    float speed = 0.1f;
    float vel = 0.02f;

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
    int baseColor;
    int baseColorLerpTo;
    int curColor = color(0,0,0,0);

    float colorLerpInc = 0.001f;
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

    float startPlayingIntervall = 0.9f;

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
        lifeTime = PApplet.parseInt(random(1, 5));
        this.main = main;
        spawnLimit = PApplet.parseInt(random(2, 4));
    }

    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    public void setSound(SoundFile s) {
      sound = s;
    }



    public void setPos(PVector p) { pos.set(p); }
    public PVector getPos() { return pos; }
    public boolean isTagged() { return tagged; }
    public void setAmount(int a) {amount = a; }

    public void setMyPlane(Plane p) {
      myPlane = new Plane();
      myPlane = p;
    } 

    public void addToMyPlane(Lev f) {
      myPlane.add(f);
    }


    public void addMeToToErase() {
      myPlane.setToErase(this);
    }

    public boolean isDead() {
      return lifeTime <= 0;
    }

    //decrement fader opacity if necessary
    public void fadeAway() {
      if(soundTimer != null && soundTimer.progress() > startFadingAway) {
        faderOpacity--;
        if(faderOpacity < 0) {
          faderOpacity = 0;
        }
      }
    }

    public boolean fadedAway() {
      return faderOpacity <= 0;
    }

    //reset next if necessary
    public void checkMyNext() {
      for(Plane p : levs.getPlanes()) {
        for(Lev f : p.getlevs()) {
          if(f != this && f.next == next) {
            next = null;
          }
        }
      }
    }

    public PVector randomPoint() {
      return new PVector(PApplet.parseInt(random(width)), PApplet.parseInt(random(height)), PApplet.parseInt(random(lowerZ, upperZ)));
    }

    public boolean checkIfSpawnEventTwo() {
      if(soundTimer != null) {
        return true;
      } else {
        return false;
      }
    }

    public void tryToSpawnTwolevs() {
      if(!iSpawnedTwolevs && checkIfSpawnEventTwo()) {
          spawnEventsTwo.add(new SpawneventTwo(this));
          iSpawnedTwolevs = true;
        }
    }

    public void render() {

        if(isDead()) {
          fadeAway();
        }

        //this should be erased if dead
        if(soundTimer != null && soundTimer.progress() >= 0.98f && fadedAway()) {
          addMeToToErase();
        }

        //handle angles
        angleForLevitate += angleForLevitateInc;
        angle1 += angle1Inc;
        if (angleForLevitate >= TWO_PI) { angleForLevitate = 0; }
        if (angle1 >= TWO_PI) { angle1 = 0; }
        pos.y += sin(angleForLevitate)*0.5f;

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
        if(soundTimer != null && soundTimer.progress() >= 1.1f) {
          tagged = false;
          soundTimer = null;
          next = null;
        }
    }

    public void detectMeByLev() {
      if(soundTimer == null) {
        tagged = true;
       startPlaying();
      }
    }

    public void startPlaying() {
      soundTimer = new Timer(sound.duration());
      soundTimer.start();
      play();
    }

    public void play() {
      sound.play();
      //each time playing, decrement lifetime
      lifeTime--;
    }

    //try to detect this object with cPos (Hand position)
    public boolean detect(PVector cPos) {
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
    public void findNext() {
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
    public Lev findRandomNext() {
      Lev re = null;
      if(levs.totalSize > 1) {
        while(next == null) {
          Plane p = levs.getPlanes().get(PApplet.parseInt(random(3)));
          if(p != null && p.getlevs().size() > 0) {
            re = p.getlevs().get(PApplet.parseInt(random(p.getlevs().size())));
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
    public boolean checkIfSpawnEvent() {
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
    public Lev findNearest() {
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

    public void setRandomMovSpeed() {
      movSpeedX = random(0,1)*speedScalar;
      movSpeedY = random(0,1)*speedScalar;
      movSpeedZ = random(0,1)*speedScalar;

      baseMovSpeedX = movSpeedX;
      baseMovSpeedY = movSpeedY;
      baseMovSpeedZ = movSpeedZ;
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

    public void makeHomeComingVector() {
      homeComingVector = new PVector((basePos.x-pos.x)/timeToGetHome, (basePos.y-pos.y)/timeToGetHome, (basePos.z-pos.z)/timeToGetHome);
    }

    public void moveTo(PVector to) {

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

      pos.x += sin(angleForLevitate)*0.5f;

    }



    public void goHome() {
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

    public void checkHomeComingVector() {
      if(pos.y < basePos.y && homeComingVector.y < 0) {
        homeComingVector.y *= -1;
      } else if(pos.y > basePos.y && homeComingVector.y > 0) {
        homeComingVector.y *= -1;
      }
    }

    public boolean getHome() {
      return home;
    }

    public void resetHomeComingVector() {
      homeComingVector = null;
    }


    //***********************************************
    //VISUALIZE
    //***********************************************

    public void visualizePlaying() {
      if(soundTimer != null && !soundTimer.finished()) {
        if(soundTimer.progress() <= 0.5f) {
          colorLerpInc = 0.001f;
        } else {
          colorLerpInc = - 0.001f;
        }
        colorLerp += colorLerpInc;
        curColor = lerpColor(baseColor, baseColorLerpTo, colorLerp);
      }
    }

    public void visualizeConnection() {
      if(next != null) {
        drawConnectionLine(next.pos);
        if(noiseScalar1 == 0) {
          noiseScalar1 = random(1) > 0.5f ? -1 : 1;
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

    public void drawConnectionLine(PVector to) {
      stroke(curColor);
      float cX = to.x;
      float cY = to.y;
      float cZ = to.z;
      float scalar = map(soundTimer.progress() - getNextIntervall, 0, 1-getNextIntervall, 0 , 1);
      
      
      if(isDead()) {
        scalar = scalar * 1.1f;
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


      float cX1 = (cX - pos.x) * 0.3f + (noiseScalar1 * scalar * ccBezierNoise1) + pos.x;
      float cY1 = (cY - pos.y) * 0.3f + (noiseScalar1 * scalar * ccBezierNoise2) + pos.y;
      float cZ1 = (cZ - pos.z) * 0.3f + (noiseScalar1 * scalar * ccBezierNoise3) + pos.z;

      float cX2 = (cX - pos.x) * 0.7f + (noiseScalar2 * scalar * ccBezierNoise2) + pos.x;
      float cY2 = (cY - pos.y) * 0.7f + (noiseScalar2 * scalar * ccBezierNoise3) + pos.y;
      float cZ2 = (cZ - pos.z) * 0.7f + (noiseScalar2 * scalar * ccBezierNoise1) + pos.z;

      cX = cX + noiseScalar1 * ((1-scalar) >= 0 ? (1-scalar) : 0) * ccBezierNoise1/2;
      cY = cY + noiseScalar1 * ((1-scalar) >= 0 ? (1-scalar) : 0) * ccBezierNoise3/2;
      cZ = cZ + noiseScalar2 * ((1-scalar) >= 0 ? (1-scalar) : 0) * ccBezierNoise2/2;

      bezier(cX, cY, cZ, cX2, cY2, cZ2, cX1, cY1, cZ1, pos.x, pos.y, pos.z);
    }

}
class LevA extends Lev {

  LevA(PVector p, String s, PApplet m) {
      super(p, s, m);
      cX = 150;
      cY = 90;
      cZ = 90;
      cA = 90;
      cX = random(1) < 0.5f ? cX - PApplet.parseInt(random(hValueRange)) : cX + PApplet.parseInt(random(hValueRange));
      cY = random(1) < 0.5f ? cY - PApplet.parseInt(random(sValueRange)) : cY + PApplet.parseInt(random(sValueRange));
      cZ = random(1) < 0.5f ? cZ - PApplet.parseInt(random(vValueRange)) : cZ + PApplet.parseInt(random(vValueRange));
      baseColor = color(cX, cY, cZ, cA);
      curColor = color(cX, cY, cZ, cA);
      baseColorLerpTo = color((cX + lerpRangeX)%360, cY, cZ, cA);
      curSize = sizeA;
  }



  public void render() {
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
                beginShape(TRIANGLES);
                vertex(-mySize/2, mySize*0.1f);
                vertex(0, -mySize*1.5f);
                vertex(mySize/2, mySize*0.1f);
                endShape();
            }

        popMatrix();

    }

}
class LevB extends Lev {

  LevB(PVector p, String s, PApplet m) {
      super(p, s, m);
      cX = 210;
      cY = 90;
      cZ = 90;
      cA = 90;
      cX = random(1) < 0.5f ? cX - PApplet.parseInt(random(hValueRange)) : cX + PApplet.parseInt(random(hValueRange));
      cY = random(1) < 0.5f ? cY - PApplet.parseInt(random(sValueRange)) : cY + PApplet.parseInt(random(sValueRange));
      cZ = random(1) < 0.5f ? cZ - PApplet.parseInt(random(vValueRange)) : cZ + PApplet.parseInt(random(vValueRange));
      baseColor = color(cX, cY, cZ, cA);
      curColor = color(cX, cY, cZ, cA);
      baseColorLerpTo = color((cX + lerpRangeX)%360, cY, cZ, cA);
      curSize = sizeB/2;

  }

  public void render() {
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


class LevC extends Lev {

  LevC(PVector p, String s, PApplet m) {
      super(p, s, m);
      cX = 360;
      cY = 90;
      cZ = 90;
      cA = 90;
      cX = random(1) < 0.5f ? cX - PApplet.parseInt(random(hValueRange)) : cX + PApplet.parseInt(random(hValueRange));
      cY = random(1) < 0.5f ? cY - PApplet.parseInt(random(sValueRange)) : cY + PApplet.parseInt(random(sValueRange));
      cZ = random(1) < 0.5f ? cZ - PApplet.parseInt(random(vValueRange)) : cZ + PApplet.parseInt(random(vValueRange));
      baseColor = color(cX, cY, cZ, cA);
      curColor = color(cX, cY, cZ, cA);
      baseColorLerpTo = color((cX + lerpRangeX)%360, cY, cZ, cA);
      curSize = sizeA;
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
                stroke(curColor, faderOpacity);
                rotateY((angle1)%360);
                float mySize = curSize+i/10;
                rect(-mySize/2, -mySize/2, mySize, mySize);
            }

        popMatrix();

    }

}


class LevD extends Lev {

  LevD(PVector p, String s, PApplet m) {
      super(p, s, m);
      cX = 290;
      cY = 90;
      cZ = 90;
      cA = 90;
      cX = random(1) < 0.5f ? cX - PApplet.parseInt(random(hValueRange)) : cX + PApplet.parseInt(random(hValueRange));
      cY = random(1) < 0.5f ? cY - PApplet.parseInt(random(sValueRange)) : cY + PApplet.parseInt(random(sValueRange));
      cZ = random(1) < 0.5f ? cZ - PApplet.parseInt(random(vValueRange)) : cZ + PApplet.parseInt(random(vValueRange));
      baseColor = color(cX, cY, cZ, cA);
      curColor = color(cX, cY, cZ, cA);
      baseColorLerpTo = color((cX + lerpRangeX)%360, cY, cZ, cA);
      curSize = sizeA;
      lifeTime = 1;
      startPlaying();
  }





  public void render() {
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

        pos.y += sin(angleForLevitate)*0.5f;

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
                polygon(0,0,(i+1)*0.6f, 9);
            }

        popMatrix();

    }
  //draws polygon with center x,y. radius radius. points npoints
  //https://processing.org/examples/regularpolygon.html
  public void polygon(float x, float y, float radius, int npoints) {
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


class LevE extends Lev {

  LevE(PVector p, String s, PApplet m) {
      super(p, s, m);
      cX = 100;
      cY = 90;
      cZ = 90;
      cA = 90;
      cX = random(1) < 0.5f ? cX - PApplet.parseInt(random(hValueRange)) : cX + PApplet.parseInt(random(hValueRange));
      cY = random(1) < 0.5f ? cY - PApplet.parseInt(random(sValueRange)) : cY + PApplet.parseInt(random(sValueRange));
      cZ = random(1) < 0.5f ? cZ - PApplet.parseInt(random(vValueRange)) : cZ + PApplet.parseInt(random(vValueRange));
      baseColor = color(cX, cY, cZ, cA);
      curColor = color(cX, cY, cZ, cA);
      baseColorLerpTo = color((cX + lerpRangeX)%360, cY, cZ, cA);
      curSize = sizeA - 5;
      lifeTime = 1;
      startPlaying();
  }

  public void render() {
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

        pos.y += sin(angleForLevitate)*0.5f;

        visualizePlaying();

        noFill();

        pushMatrix();

            translate(pos.x, pos.y, pos.z);
            int inc = 360/amount;

            for (int i = 0; i < 360; i += inc) {
                float line = (1+i/10000) < 2 ? 2 : (1+i/20);
                strokeWeight(line);
                stroke(curColor, faderOpacity);
                //rotateX(angle1);
                rotateY((angle1)%360);
                polygon(0,0,(i+1)*0.6f, 7);
            }

        popMatrix();

    }
  //draws polygon with center x,y. radius radius. points npoints
  //https://processing.org/examples/regularpolygon.html
  public void polygon(float x, float y, float radius, int npoints) {
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


class LevF extends Lev {

  LevF(PVector p, String s, PApplet m) {
      super(p, s, m);
      cX = 50;
      cY = 90;
      cZ = 90;
      cA = 90;
      cX = random(1) < 0.5f ? cX - PApplet.parseInt(random(hValueRange)) : cX + PApplet.parseInt(random(hValueRange));
      cY = random(1) < 0.5f ? cY - PApplet.parseInt(random(sValueRange)) : cY + PApplet.parseInt(random(sValueRange));
      cZ = random(1) < 0.5f ? cZ - PApplet.parseInt(random(vValueRange)) : cZ + PApplet.parseInt(random(vValueRange));
      baseColor = color(cX, cY, cZ, cA);
      curColor = color(cX, cY, cZ, cA);
      baseColorLerpTo = color((cX + lerpRangeX)%360, cY, cZ, cA);
      curSize = sizeA - 5;
      lifeTime = 1;
      startPlaying();
  }

  public void render() {
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

        pos.y += sin(angleForLevitate)*0.5f;

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
                polygon(0,0,(i+1)*0.6f, 5);
            }

        popMatrix();

    }

  //draws polygon with center x,y. radius radius. points npoints
  //https://processing.org/examples/regularpolygon.html
  public void polygon(float x, float y, float radius, int npoints) {
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


public class LevList {

  ArrayList<Plane> planes;

  PApplet myMain;

  Lev firstAdded;

  PVector myCenter;

  int activePlanes = 0;

  PVector conCol = new PVector(0, 0, 100);

  int totalSize;

  public LevList(PApplet main) {
    myCenter = new PVector();
    planes = new ArrayList<Plane>();
    for(int i = 0; i < 6; i++) {
      planes.add(new Plane());
    }
    myMain = main;
  }

  public boolean totalSizeBiggerOne() {
    int size = 0;
    for(Plane p : planes) {
      for(Lev f : p.getlevs()) {
        size++;
        if(size > 1) {
          return true;
        }
      }
    }
    return false;
  }

  public int getTotalSize() {
    int size = 0;
    for(Plane p : planes) {
      for(Lev f : p.getlevs()) {
        size++;
      }
    }
    return size;
  }

  public ArrayList<Plane> getPlanes() {
    return planes;
  }

  public void set(int i, Plane p) {
    planes.set(i, p);
  }

  public void addSingleLev(Lev f) {
    //LevA
    if(f instanceof LevA) {
      planes.get(0).addLev(f);
      for(Lev cur : planes.get(0).getlevs()) {
        cur.setMyPlane(planes.get(0));
      }
    }
    //LevB
    if(f instanceof LevB) {
      planes.get(1).addLev(f);
      for(Lev cur : planes.get(1).getlevs()) {
        cur.setMyPlane(planes.get(1));
      }
    }
    //LevC
    if(f instanceof LevC) {
      planes.get(2).addLev(f);
      for(Lev cur : planes.get(2).getlevs()) {
        cur.setMyPlane(planes.get(2));
      }
    }

    //LevD
    if(f instanceof LevD) {
      planes.get(3).addLev(f);
      for(Lev cur : planes.get(3).getlevs()) {
        cur.setMyPlane(planes.get(3));
      }
    }

    //LevE
    if(f instanceof LevE) {
      planes.get(4).addLev(f);
      for(Lev cur : planes.get(4).getlevs()) {
        cur.setMyPlane(planes.get(4));
      }
    }

    //LevF
    if(f instanceof LevF) {
      planes.get(5).addLev(f);
      for(Lev cur : planes.get(5).getlevs()) {
        cur.setMyPlane(planes.get(5));
      }
    }
  }

  public void addPlane(Plane p) {
    planes.add(p);
  }

  public void setTotalSize() {
    totalSize = 0;
    for(int i = 0; i < 3; i++) {
      Plane p = planes.get(i);
      for(Lev f : p.getlevs()) {
        totalSize++;
      }
    }
  }

  public void run() {
    setTotalSize();
    myCenter = new PVector();

  for(Plane p : planes) {
    p.removeToErase();
  }

  activePlanes = 0;
  for(Plane p : planes) {
    p.render();
      if(p.isAlive()) {
      myCenter.x += p.myCenter.x;
      myCenter.y += p.myCenter.y;
      myCenter.z += p.myCenter.z;
      activePlanes++;
      }
    }
  }

  //draws a line from center to center
  public void drawFromListCenterToEachPlane() {

    myCenter.x = myCenter.x / activePlanes;
    myCenter.y = myCenter.y / activePlanes;
    myCenter.z = myCenter.z / activePlanes;

    for(Plane f : planes) {
        stroke(conCol.x, conCol.y, conCol.z, 90);
        line(f.myCenter.x, f.myCenter.y, f.myCenter.z, myCenter.x, myCenter.y, myCenter.z);
    }
  }

  public void resetAllHomeComingVectors() {
    for(Plane plane : planes) {
      for(Lev f : plane.getlevs()) {
        f.resetHomeComingVector();
      }
    }
  }

  //checks if all levs are home
  public boolean alllevsHome() {
    for(Plane plane : planes) {
      for(Lev f : plane.getlevs()) {
        if(!f.getHome()) {
          return false;
        }
      }
    }
      return true;
  }
}
class Plane {
	
	ArrayList<Lev> mylevs;

  Lev toErase;

  PVector myCenter;

  PVector conCol = new PVector(0, 0, 100);

  boolean oneIsTagged = false;

	public Plane() {
		mylevs = new ArrayList<Lev>();
    myCenter = new PVector();
	}

  public void setToErase(Lev f) {
    toErase = f;
  }

	public void addLev(Lev f) {
		mylevs.add(f);
	}	

	public ArrayList<Lev> getlevs() {
		return mylevs;
	}

  public void add(Lev f) {
    mylevs.add(f);
  }

  public boolean isAlive() {
    return mylevs.size() > 0;
  }

  public void removeToErase() {
    if(toErase != null) {
      mylevs.remove(toErase);
      toErase = null;
    }
  }

	public void render() {
    myCenter = new PVector();

    if(mylevs.size() < 2) {
      oneIsTagged = false;
    }
      //camera(width/2.0, height/2.0, (height/2.0) / tan(PI*30.0 / 180.0)*2, width/2.0, height/2.0, 0, 0, 1, 0);    
      for(Lev f : mylevs) {
        switch (state) {
          //levitating in order (starting positions)
          case (IN_ORDER) :
          if(f.homeComingVector != null) {
            f.resetHomeComingVector();
          }
            f.detect(curser.pos);
            f.render();
            break;

          //levitating freely. wandering.  
          case (WANDERING) :
          if(f.homeComingVector != null) {
            f.resetHomeComingVector();
          }
            f.move();
            f.render();
            boolean curCouldTag = f.detect(curser.pos);
            if(curCouldTag) {
              couldTag = true;
            }

            break;

          //lev back to starting position
          case (GO_HOME) :
            f.goHome();
            f.render();
            if(lastState != state) {
              playSwoosh();
            }
          default :
        }

        myCenter.x += f.getPos().x;
        myCenter.y += f.getPos().y;
        myCenter.z += f.getPos().z;

      }
	}
}
class Spawnevent {

	ArrayList<Lev> list;

	Timer myTimer = new Timer(3);

	PVector spawnPoint = new PVector(PApplet.parseInt(random(maxX)), PApplet.parseInt(random(maxY)), PApplet.parseInt(random(lowerZ, upperZ)));

	PApplet main;

	boolean dead = false;

	boolean spawned = false;

	public Spawnevent(Lev a, Lev b, PApplet m) {
		main = m;
		list = new ArrayList<Lev>();
		list.add(a);
		list.add(b);
		myTimer.start();
	}

	public Spawnevent(Lev a, Lev b, PApplet m, PVector pos) {
		main = m;
		list = new ArrayList<Lev>();
		list.add(a);
		list.add(b);
		myTimer.start();
		spawnPoint.set(pos);
	}

	public void run() {
		//check if spawnEvent is dead (=big lev has been spawned)
		if(dead) {
			addMeToToDelete(this);
		}
		drawLines();
	}

	public void drawLines() {
		  float scalar = myTimer.progress();
		  if(list != null && list.size() == 2) {
		  for(Lev f : list) {
			  stroke(f.curColor, f.faderOpacity+0.2f);
		      float cX = spawnPoint.x;
		      float cY = spawnPoint.y;
		      float cZ = spawnPoint.z;
		      cX = scalar * (spawnPoint.x - f.pos.x) + f.pos.x;
		      cY = scalar * (spawnPoint.y - f.pos.y) + f.pos.y;
		      cZ = scalar * (spawnPoint.z - f.pos.z) + f.pos.z;
		      line(cX, cY, cZ, f.pos.x, f.pos.y, f.pos.z);
	  	  }
	  	  //check if big lev should be spawned
	  	  if(scalar >= 1 && !spawned) {
	  	  	spawnLev();
	  	  	spawned = true;
	  	  }
	  	}
	}

	public void spawnLev() {
		//what kind of big lev should be spawned?
		int r = PApplet.parseInt(random(3));
		switch (r) {
			case 0: 
			levs.addSingleLev(new LevD(spawnPoint, soundStringsEnlight[PApplet.parseInt(random(soundStringsEnlight.length-1))], main));
			break;

			case 1: 
			levs.addSingleLev(new LevE(spawnPoint, soundStringsEnlight[PApplet.parseInt(random(soundStringsEnlight.length-1))], main));
			break;

			case 2: 
			levs.addSingleLev(new LevF(spawnPoint, soundStringsEnlight[PApplet.parseInt(random(soundStringsEnlight.length-1))], main));
			break;
			
			default:
			levs.addSingleLev(new LevF(spawnPoint, soundStringsEnlight[PApplet.parseInt(random(soundStringsEnlight.length-1))], main));
		}

		dead = true;
	}

}
class SpawneventTwo {
	
	PVector one = new PVector();

	PVector two = new PVector();

	Lev f;

	Timer myTimer = new Timer(3);

	boolean dead = false;

	boolean spawned = false;

	ArrayList<PVector> list = new ArrayList<PVector>();

	public SpawneventTwo(Lev mf) {
		f = mf;

		list.add(randomPoint());

		//ckeck if add some more vectors to spanw from (up to 3)
		int r = PApplet.parseInt(random(5));
		if(r > 1) {
			list.add(randomPoint());
		}
		if(r > 3) {
			list.add(randomPoint());
		}
		myTimer = f.soundTimer;
	}

	public PVector randomPoint() {
      return new PVector(PApplet.parseInt(random(width)), PApplet.parseInt(random(height)), PApplet.parseInt(random(lowerZ, upperZ)));
    }

	public void run() {
		//check if spawnEvent is dead (= new levs have been spawned)
		if(dead) {
			addMeToToDeleteTwo(this);
		}
		drawLines();
	}

	public void drawLines() {
		  float scalar = myTimer.progress();
		  for(PVector p : list) {
			  stroke(f.curColor, f.faderOpacity+0.2f);
		      float cX = p.x;
		      float cY = p.y;
		      float cZ = p.z;
		      cX = scalar * (p.x - f.pos.x) + f.pos.x;
		      cY = scalar * (p.y - f.pos.y) + f.pos.y;
		      cZ = scalar * (p.z - f.pos.z) + f.pos.z;
		      line(cX, cY, cZ, f.pos.x, f.pos.y, f.pos.z);
	  	  }
	  	  if(scalar >= 1 && !spawned) {
	  	  	spawnlevs();
	  	  	spawned = true;
	  	  }
	}

	public void spawnlevs() {
		//add a lev for each vector in list
		for(int i = 0; i < list.size(); i++) {
			playRandomPlant();
			//add a random lev
			int r = PApplet.parseInt(random(3));
			switch (r) {
				case 0: 
				levs.addSingleLev(new LevA(list.get(i), soundStringsA[PApplet.parseInt(random(soundStringsA.length-1))], main));
				break;

				case 1: 
				levs.addSingleLev(new LevB(list.get(i), soundStringsB[PApplet.parseInt(random(soundStringsB.length-1))], main));
				break;

				case 2: 
				levs.addSingleLev(new LevC(list.get(i), soundStringsA[PApplet.parseInt(random(soundStringsA.length-1))], main));
				break;
				
				default:
				levs.addSingleLev(new LevC(list.get(i), soundStringsA[PApplet.parseInt(random(soundStringsA.length-1))], main));
			}
		}
		dead = true;
	}
}
class Timer {


    float endtime, duration;
    boolean started = false;



    Timer() { }
    Timer(float d) { set(d); }


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
    String[] appletArgs = new String[] { "Levitate" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
