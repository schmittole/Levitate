import de.voidplus.leapmotion.*;


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

    void draw() {

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
    void checkCarry(Hand hand) {
        if(hand.getIndexFinger() != null && hand.getPinkyFinger() != null && hand.getIndexFinger().getPosition().x > hand.getPinkyFinger().getPosition().x) {
            carry = true;
        } else {
            carry = false;
        }
    }

    void resetCarryBoolean() {
        carry = false;
    }


    void resetAllStretchedBooleans() {
        leftRingStretched = false;
        leftPinkyStretched = false;
        leftMidStretched = false;
        leftIndexStretched = false;
    }

    void draw(Hand hand) {
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

    float getMappedZ(float zIn) {
        float curZ = zIn;
        curZ = curZ < leapMinZ ? leapMinZ : curZ > leapMaxZ ? leapMaxZ : curZ;
        return map(curZ, leapMinZ, leapMaxZ, upperZ, lowerZ);
    }

    float getMappedX(float xIn) {
        float curX = xIn;
        curX = curX < leapMinX ? leapMinX : curX > leapMaxX ? leapMaxX : curX;
        return map(curX, leapMinX, leapMaxX, 0, maxX);
    }

    float getMappedY(float yIn) {
        float curY = yIn;
        curY = curY < leapMinY ? leapMinY : curY > leapMaxY ? leapMaxY : curY;
        return map(curY, leapMinY, leapMaxY, 0, maxY);
    }
}
