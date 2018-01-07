// import utils
import java.util.*;
import processing.sound.*;
import org.openkinect.freenect.*;

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

float tagDis = width*0.2;

boolean couldTag = false;

int backgroundOpacity = 10;

PVector baseColor1Vec = new PVector(0, 0, 0);

color baseColor1 = color(baseColor1Vec.x, baseColor1Vec.y, baseColor1Vec.z, backgroundOpacity);

int lerpMax = 30;

PVector baseColor2Vec = new PVector(lerpMax, lerpMax, lerpMax);

color baseColor2 = color(baseColor2Vec.x, baseColor2Vec.y, baseColor2Vec.z, backgroundOpacity);

PVector curColorVec = new PVector(baseColor1Vec.x, baseColor1Vec.y, baseColor1Vec.z);

color curColor = color(baseColor1Vec.x, baseColor1Vec.y, baseColor1Vec.z, backgroundOpacity);

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

void settings() {
  fullScreen(P3D);
}

void setup() {
    maxX = width;
    maxY = height;
    colorMode(HSB, 360, 100, 100, 100);
    backgroundColorTimer.start();
    //camera(width/2.0, height/2.0, (height/2.0) / tan(PI*30.0 / 180.0)*1000, width/2.0, height/2.0, 0, 0, 1, 0);
    camera(width/2.0, height/2.0, (height/2.0) / tan(PI*30.0 / 180.0)*2, width/2.0, height/2.0, 0, 0, 1, 0);    

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
    swipeSound.amp(0.2);

    screenTapTimer = new Timer(1);
    screenTapTimer.start();

}



void setBaseColor1(PVector c) {
  baseColor1 = color(c.x, c.y, c.z, backgroundOpacity);
  baseColor1Vec = new PVector(c.x, c.y, c.z);
}

void setBaseColor2(PVector c) {
  baseColor2 = color(c.x, c.y, c.z, backgroundOpacity);
  baseColor2Vec = new PVector(c.x, c.y, c.z);
}

void shiftCurColor() {
  curColor = lerpColor(baseColor1, baseColor2, shiftFactor);
  shiftFactor += shiftFactorInc;

  if(shiftFactor > 1-abs(shiftFactorInc) || shiftFactor < abs(shiftFactorInc)) {
    shiftFactorInc = -shiftFactorInc;
    if(shiftFactorInc > 0) {
      setBaseColor2(new PVector(int(random(lerpMax)), int(random(lerpMax)), int(random(lerpMax))));
      shiftComplete = true;
    }
  }
}

void checkSpawnEvents() {
  if(removeThisSpawnEvent != null) {
    spawnEvents.remove(removeThisSpawnEvent);
    removeThisSpawnEvent = null;
  }
}

void addMeToToDelete(Spawnevent s) {
  removeThisSpawnEvent = s;
}

void checkSpawnEventsTwo() {
  if(removeThisSpawnEventTwo != null) {
    spawnEventsTwo.remove(removeThisSpawnEventTwo);
    removeThisSpawnEventTwo = null;
  }
}

void addMeToToDeleteTwo(SpawneventTwo s) {
  removeThisSpawnEventTwo = s;
}

void draw() {

    noCursor();
    colorMode(RGB, 100);
    background(curColor);
    if(backgroundColorTimer.finished()) {
      if(!shiftComplete) {
        shiftCurColor();
      } else {
        backgroundColorTimer = new Timer(int(random(3,7)));
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
      levs.addSingleLev(new LevA(interactionController.getPosition(RIGHT), soundStringsA[int(random(soundStringsA.length))], this));
    }

    if(currentlevsInPlaneB() <= maxlevsPerClass && makeLev && makeLevB()) {
      playLevPlantSound();
      levs.addSingleLev(new LevB(interactionController.getPosition(RIGHT), soundStringsB[int(random(soundStringsB.length))], this));
    }

    if(currentlevsInPlaneC() <= maxlevsPerClass && makeLev && makeLevC()) {
      playLevPlantSound();
      levs.addSingleLev(new LevC(interactionController.getPosition(RIGHT), soundStringsA[int(random(soundStringsA.length))], this));
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

void playLevPlantSound() {
  playRandomPlant();
}

void playRandomPlant() {
  SoundFile aux = new SoundFile(this, soundStringsPlant[int(random(soundStringsPlant.length))]);
  aux.amp(0.3);
  aux.play();
}

void playRandomB() {
  SoundFile aux = new SoundFile(this, soundStringsB[int(random(soundStringsB.length))]);
  aux.amp(0.3);
  aux.play();
}

void playRandomC() {
  SoundFile aux = new SoundFile(this, soundStringsC[int(random(soundStringsC.length))]);
  aux.amp(0.3);
  aux.play();
}

ArrayList<Lev> findlevsNextToHand() {
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

void setCurserColor() {
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

void playSwipeSound() {
  swipeSound.play();
}

void clearA() {
  levs.getPlanes().get(0).oneIsTagged = false;
  levs.getPlanes().get(0).getlevs().clear();
}

void clearB() {
  levs.getPlanes().get(1).oneIsTagged = false;
  levs.getPlanes().get(1).getlevs().clear();
}

void clearC() {
  levs.getPlanes().get(2).oneIsTagged = false;
  levs.getPlanes().get(2).getlevs().clear();
}

void clearAll() {
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

int currentlevsInPlaneA() {
  return levs.getPlanes().get(0).getlevs().size();
}

int currentlevsInPlaneB() {
  return levs.getPlanes().get(1).getlevs().size();
}

int currentlevsInPlaneC() {
  return levs.getPlanes().get(2).getlevs().size();
}

boolean makeLevA() {
  if(interactionController.leftIndexStretched && !interactionController.leftMidStretched && !interactionController.leftRingStretched && !interactionController.leftPinkyStretched) {
    return true;
  }
  return false;
}

boolean makeLevB() {
  if(interactionController.leftIndexStretched && interactionController.leftMidStretched && !interactionController.leftRingStretched && !interactionController.leftPinkyStretched) {
    return true;
  }
  return false;
}

boolean makeLevC() {
  if(interactionController.leftIndexStretched && interactionController.leftMidStretched && interactionController.leftRingStretched && !interactionController.leftPinkyStretched) {
    return true;
  }
  return false;
}

void resetAllCommands() {
    makeLev = false;
    clearAll = false;
    clearA = false;
    clearB = false;
    clearC = false;
    couldTag = false;
}

void drawCurser() {
  if(drawC && !noHands) {
    curser.move(interactionController.rightHandIndex.x, interactionController.rightHandIndex.y, interactionController.rightHandIndex.z);
    curser.render();
}
}

String getRandomSound() {
  return soundStringsA[int(random(4))];
}

void playSwoosh() {
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

void leapOnSwipeGesture(SwipeGesture g, int state){
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

void leapOnScreenTapGesture(ScreenTapGesture g){
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
    screenTapTimer = new Timer(0.5);
    screenTapTimer.start();
  }
}