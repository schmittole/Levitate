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
    float explosionGestureMinDistance = 1.5;
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

    float getPosX()                       { return rightHandIndex.x; }
    float getPosY()                       { return rightHandIndex.y; }
    float getPosZ()                       { return rightHandIndex.z; }
    float getScaleFactor()                { return scaleFactor; }
    float getNormalizedPosZ()             { return rightHandIndex.z; }
    float getNormalizedPosZ(int a, int b) { return rightHandIndex.z; }

    PVector getPosition(int hand) {
        if      (hand == LEFT)  { return leftHand; }
        else if (hand == RIGHT) { return rightHandIndex; }
        return null;
    }

    boolean noFingerStretched() {
        return !(leftRingStretched | leftPinkyStretched | leftMidStretched | leftIndexStretched);
    }



    void setState(int s) { 
            lastState = state;
            state = s; 
    }

    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    void draw() {

    }


    void setup() { }






    void enableTagging() { taggingEnabled = true; }
    void disableTagging() { taggingEnabled = false; }

    boolean isTagging() { return taggingEnabled; }

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
